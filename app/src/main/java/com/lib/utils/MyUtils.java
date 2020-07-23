package com.lib.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.xiaoyv.java.R;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtils {
    /**
     * 在浏览器中打开链接
     *
     * @param targetUrl 要打开的网址
     */
    public static void openBrowser(Context context, String targetUrl) {
        if (!TextUtils.isEmpty(targetUrl) && targetUrl.startsWith("file://")) {
            ToastUtils.showShort(R.string.cant_open);
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse(targetUrl);
        intent.setData(url);
        context.startActivity(intent);
    }

    /**
     * 文件大小换算
     *
     * @param target_size 字节大小
     * @return 文件大小
     */
    public static String formatSize(Context context, String target_size) {
        return formatSize(context, Long.valueOf(target_size));
    }

    public static String formatSize(Context context, long target_size) {
        return Formatter.formatFileSize(context, target_size);
    }

    /**
     * 获取取随机数
     *
     * @param num 最大数限制
     * @return 随机数`
     */
    public static String getRand(int num) {
        Random ran = new Random(System.currentTimeMillis());
        int refresh_int = ran.nextInt(num);
        return String.valueOf(refresh_int);
    }

    /**
     * 分享文件
     *
     * @param what 分享内容
     */
    public static void shareText(Context context, String what) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, "分享了：\n" + what);
        share.setType("text/plain");
        context.startActivity(Intent.createChooser(share, "分享到"));
    }

    // 保存网络文件
    public static void saveFile(final Context context, String url, @NonNull String saveDir, String saveFileName) {
       // TODO 保存网络文件
    }

    public static void saveImageToGallery(Context context, String path) {
        LogUtils.e("图片更新路径" + path);
        File file = new File(path);
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), "网页图片");
        } catch (Exception e) {
            ToastUtils.showShort("更新失败");
            e.printStackTrace();
        }
        //最后通知图库更新
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//扫描单个文件
        intent.setData(Uri.fromFile(file));//给图片的绝对路径
        context.sendBroadcast(intent);
    }

    /**
     * 正则获取Host
     *
     * @param url 链接
     * @return Host
     */
    public static String getHost(String url) {
        if (url == null || url.trim().equals("")) {
            return "";
        }
        String host = "";
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }


    public static android.app.AlertDialog getAlert(Context context, String message, DialogInterface.OnClickListener listener) {
        android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(context);
        build.setTitle(R.string.tip);
        build.setMessage(message);
        build.setPositiveButton(R.string.done, listener);
        build.setNegativeButton(R.string.clear, null);
        build.create();
        android.app.AlertDialog dialog = build.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static android.app.AlertDialog getAlert(Context context, String message, DialogInterface.OnClickListener pos_listener, DialogInterface.OnClickListener neg_listener) {
        android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(context);
        build.setTitle(R.string.tip);
        build.setMessage(message);
        build.setPositiveButton(R.string.done, pos_listener);
        build.setNegativeButton(R.string.clear, neg_listener);
        build.create();
        AlertDialog dialog = build.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 根据扩展名获取文件Mime类型
     *
     * @param file 文件
     * @return 文件Mime类型
     */
    public static String getMimeType(File file) {
        String extension = FileUtils.getFileExtension(file);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }


    /**
     * 传入路径，打开文件
     *
     * @param path 文件路径
     */

    public static void openFileByPath(Context context, String path) {
        try {
            File file = new File(path);
            String mimeType = getMimeType(file);
            LogUtils.e("尝试打开文件", path, mimeType);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Uri uri;
            //判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, AppUtils.getAppPackageName() + ".fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, mimeType);

            context.startActivity(intent);
        } catch (Exception e) {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("此App不支持打开此文件，请在手机文件管理器中打开路径\n\n\"A_Tool/Download/\"")
                    .setPositiveButton("知道了", null)
                    .create();
            dialog.show();
        }
    }

    //App更新检查
    public static void checkAppVersion(final Context context) {
        // TODO APP检查更新
    }

    public static void setToolbarBackToHome(final AppCompatActivity activity, Toolbar toolbar) {
        activity.setSupportActionBar(toolbar);
        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar == null) return;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v ->
                activity.onBackPressed());

    }
}
