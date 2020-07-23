package com.lib.x5web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.widget.TextView;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.utils.MyUtils;
import com.xiaoyv.java.R;

import java.net.URLDecoder;
import java.util.Objects;

/**
 * Created by Administrator on 2018/4/10.
 *
 * @author 王怀玉
 * @explain X5DownloadListener
 */

public class X5DownloadListener implements DownloadListener {
    private final ProgressDialog progressDialog;
    private Context context;
    private String FilePath;

    public X5DownloadListener(Context context) {
        this.context = context;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.tip);
        progressDialog.setMessage(context.getString(R.string.download_ing));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private String fileName;

    @SuppressLint("SetTextI18n")
    @Override
    public void onDownloadStart(final String s, String s1, final String s2, String s3, long l) {
        LogUtils.e(s, s, s2, s3, l);
        // s 下载地址,s1 UA ,s2 文件名字 l文件大小
        String FileName = s2.substring(s2.lastIndexOf("=") + 1);
        try {
            fileName = URLDecoder.decode(FileName, "utf-8");
            fileName = fileName.replace("\"", "");
        } catch (Exception ignored) {
        }

        if (StringUtils.isEmpty(fileName)) {
            fileName = s.substring(s.lastIndexOf("/") + 1);
        }
        if (fileName.contains("?")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("?"));
        }
        if (fileName.endsWith(".cn")) {
            fileName = fileName.replace(".cn", ".jpg");
        }

        if (fileName.endsWith(";")) {
            fileName = fileName.replace(";", "");
        }

        if (fileName.contains("utf-8")) {
            fileName = fileName.replace("utf-8", "");
        }


        @SuppressLint("InflateParams")
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.activity_web_down, null);
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.style_dialog)
                .setView(view).create();
        dialog.show();

        TextView text = view.findViewById(R.id.text);
        TextView download = view.findViewById(R.id.download);
        text.setText(context.getString(R.string.file_name) + "：" + fileName
                + "\n\n" + context.getString(R.string.size) + "：" + MyUtils.formatSize(context, String.valueOf(l)));

        download.setOnClickListener(v -> {
            dialog.dismiss();
            String RandNum = EncryptUtils.encryptMD5ToString(MyUtils.getRand(10));
            RandNum = RandNum.substring(0, 5);
            RandNum = "文件_" + RandNum + "_";
            fileName = RandNum + fileName;

            //过SystemService 以获取 DownloadManager
            String savePath = SPUtils.getInstance("app_info").getString("down_path", "A_Tool/Download/");
            FileUtils.createOrExistsDir(PathUtils.getExternalStoragePath() + "/" + savePath);

            FilePath = Environment.getExternalStorageDirectory() + "/" + savePath + fileName;

            progressDialog.show();

            //第二个是相对参数
            //TODO 下载
            /*
            DownloadUtils.getInstance().download(s, savePath, fileName, new DownloadUtils.OnDownloadListener() {
                @Override
                public void onDownloadSuccess() {
                    progressDialog.dismiss();

                    AlertDialog dialog1 = new AlertDialog.Builder(context, R.style.style_dialog)
                            .setTitle(R.string.tip)
                            .setMessage(context.getString(R.string.download_dir) + "：\n\n" + FilePath)
                            .setNegativeButton(R.string.open_file, (dialog11, which) ->
                                    MyUtils.openFileByPath(context, FilePath))
                            .create();
                    dialog1.show();
                    dialog1.setCanceledOnTouchOutside(false);
                }

                @Override
                public void onDownloading(int progress) {
                    progressDialog.setProgress(progress);
                    progressDialog.show();
                }

                @Override
                public void onDownloadFailed(String error) {
                    progressDialog.dismiss();
                    ToastUtils.showShort(R.string.download_error);
                }
            });*/
        });


        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        Objects.requireNonNull(window).setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setWindowAnimations(R.style.BottomToBottom);  //添加动画
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }
}