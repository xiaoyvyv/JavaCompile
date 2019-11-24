package com.lib.utils;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.xiaoyv.http.OkHttp;
import com.xiaoyv.http.OnResultStringListener;
import com.xiaoyv.java.R;
import com.xiaoyv.java.bean.UpDateBean;
import com.xiaoyv.java.ui.activity.WebActivity;
import com.xiaoyv.java.url.Url;

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

    /**
     * 跳转网页
     *
     * @param url 网页链接
     */
    public static void openUrl(Context context, String url) {
        //浏览器中打开
        if (url.endsWith("openBrowser")) {
            openBrowser(context, url);
            return;
        }

        //无标题打开
        if (url.endsWith("noTitle")) {
            openUrl(context, url, true);
            return;
        }


        //支付宝二维码捐赠
        if (url.contains(context.getResources().getString(R.string.apply_me_key)) || url.contains("donateAlipayByPic")) {
            if (context instanceof Activity)
                AlipayUtil.donateAlipayByPic(((Activity) context));
            return;
        }

        //支付宝捐赠
        if (url.contains(context.getResources().getString(R.string.apply_me_key)) || url.contains("donateAlipay")) {
            if (context instanceof Activity)
                AlipayUtil.donateAlipay(((Activity) context));
            return;
        }


        //微信二维码捐赠
        if (url.contains(context.getResources().getString(R.string.apply_me_key)) || url.contains("donateWeiXin")) {
            if (context instanceof Activity)
                WeChatUtil.donateWeiXin(((Activity) context));
            return;
        }


        if (URLUtil.isNetworkUrl(url)) {
            context.startActivity(new Intent(context, WebActivity.class)
                    .putExtra("from_url", url));
            return;
        }

        startActivity(url);
    }

    /**
     * 跳转网页
     *
     * @param url 网页链接
     */
    public static void openUrl(Context context, String url, boolean isNoTitle) {
        if (URLUtil.isNetworkUrl(url)) {
            context.startActivity(new Intent(context, WebActivity.class)
                    .putExtra("from_url", url)
                    .putExtra("isNoTitle", isNoTitle));
        } else {
            startActivity(url);
        }
    }

    public static void openUrlWithReferer(Context context, String url, String referer, boolean isNoTitle) {
        if (URLUtil.isNetworkUrl(url)) {
            context.startActivity(new Intent(context, WebActivity.class)
                    .putExtra("from_url", url)
                    .putExtra("referer", referer)
                    .putExtra("isNoTitle", isNoTitle));
        } else {
            startActivity(url);
        }
    }


    public static void startActivity(Class<? extends Activity> activity) {
        Context context = null;
        List<Activity> activityList = ActivityUtils.getActivityList();

        if (!ObjectUtils.isEmpty(activityList)) {
            context = activityList.get(activityList.size() - 1);
        }
        if (context == null) {
            context = Utils.getApp().getApplicationContext();
        }
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void startActivity(String cls) {
        try {
            Intent intent = new Intent();
            //前名一个参数是应用程序的包名,后一个是这个应用程序的主Activity名
            intent.setComponent(new ComponentName(AppUtils.getAppPackageName(), cls));
            startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort(R.string.open_error);
        }
    }

    public static void startActivity(Intent intent) {
        Context context = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<Activity> activityList = ActivityUtils.getActivityList();

        if (!ObjectUtils.isEmpty(activityList)) {
            context = activityList.get(activityList.size() - 1);
        }
        if (context == null) {
            context = Utils.getApp().getApplicationContext();
        }
        if (context == null) {
            ActivityUtils.startActivity(intent);
            return;
        }
        context.startActivity(intent);
    }

    //保存网络文件
    public static void saveFile(final Context context, String url, @NonNull String saveDir, String saveFileName) {
        if (saveFileName.length() > 25) {
            saveFileName = saveFileName.substring(saveFileName.length() - 20);
        }

        FileUtils.createOrExistsDir(saveDir);
        ToastUtils.showShort(R.string.saving);

        final String finalSaveFileName = saveFileName;
        DownloadUtils.getInstance().download(url, saveDir, saveFileName, new DownloadUtils.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                ToastUtils.showShort(R.string.save_success);
                if (finalSaveFileName.endsWith(".png") || finalSaveFileName.endsWith(".jpg") || finalSaveFileName.endsWith(".jpeg") || finalSaveFileName.endsWith(".gif")) {
                    MyUtils.saveImageToGallery(context, saveDir + "/" + finalSaveFileName);
                }
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed(String error) {
                LogUtils.e(error);
                ToastUtils.showShort(R.string.save_error);
            }
        });
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
        OkHttp.do_Get(Url.App_Update, new OnResultStringListener() {
            @Override
            public void onResponse(String response) {
                UpDateBean upDateBean = GsonUtils.fromJson(response, UpDateBean.class);

                SPUtils.getInstance("app_info").put("update", "已经是最新版本！");

                //当前版本
                int oldCode = AppUtils.getAppVersionCode();
                int newAppCode = upDateBean.getCode();
                String newAppVersion = upDateBean.getVersion();
                if (newAppCode <= oldCode) {
                    ToastUtils.showShort("已经是最新版本");
                    return;
                }
                SPUtils.getInstance("app_info").put("update", "发现新版本！");
                String title = upDateBean.getTitle();
                String message = upDateBean.getMessage();
                String rightText = upDateBean.getRightText();
                String centerText = upDateBean.getCenterText();
                String leftText = upDateBean.getLeftText();
                String isForce = upDateBean.getForce();
                final int minCode = upDateBean.getMinCode();
                final String minVersion = upDateBean.getMinVersion();
                final String rightUrl = upDateBean.getApkurl();
                final String centerUrl = upDateBean.getBackurl();
                final String leftUrl = upDateBean.getLeftUrl();


                //判断最低升级版本
                if (oldCode < minCode) {
                    isForce = "true";
                    message = message + "\n\n注意：版本" + minVersion + "以下需要更新后才能使用";
                }

                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(context).inflate(R.layout.view_upadte_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.update_dialog)
                        .setView(view).create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                if (isForce.equals("true")) {
                    alertDialog.setCancelable(false);
                }

                TextView titleTv = view.findViewById(R.id.title);
                TextView versionTv = view.findViewById(R.id.version);
                TextView messageTv = view.findViewById(R.id.message);
                TextView leftBtn = view.findViewById(R.id.leftBtn);
                TextView centerBtn = view.findViewById(R.id.centerBtn);
                TextView rightBtn = view.findViewById(R.id.rightBtn);

                versionTv.setText(newAppVersion);
                titleTv.setText(title);
                messageTv.setText(message);
                leftBtn.setText(leftText);
                centerBtn.setText(centerText);
                rightBtn.setText(rightText);

                final String finalIsForce = isForce;
                leftBtn.setOnClickListener(v -> {
                    MyUtils.openBrowser(context, leftUrl);
                    if (!finalIsForce.equals("true")) {
                        alertDialog.dismiss();
                    }
                });
                centerBtn.setOnClickListener(v -> {
                    MyUtils.openBrowser(context, centerUrl);
                    if (!finalIsForce.equals("true")) {
                        alertDialog.dismiss();
                    }

                });
                rightBtn.setOnClickListener(v -> {
                    MyUtils.openUrl(context, rightUrl);

                    // 更新操作清除数据
                    CleanUtils.cleanInternalCache();
                    CleanUtils.cleanInternalDbs();
                    CleanUtils.cleanExternalCache();

                    if (!finalIsForce.equals("true")) {
                        alertDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                LogUtils.e(error);
            }
        });
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
