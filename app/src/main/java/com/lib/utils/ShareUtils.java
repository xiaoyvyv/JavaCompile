package com.lib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.AppUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/4/10.
 *
 * @author 王怀玉
 * @explain ShareUtils
 */

public class ShareUtils {

    /**
     * 分享功能|分享单张图片
     *
     * @param context  上下文
     * @param msgTitle 消息标题
     * @param msgText  消息内容
     * @param imgPath  图片路径，不分享图片则传null
     */
    public static void shareMsg(Context context, String msgTitle, String msgText, String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享到"));
    }

    /**
     * 分享多张照片
     *
     * @param context
     * @param list    ArrayList＜ImageUri＞
     */
    public static void sendMultiple(Context context,
                                    ArrayList<? extends Parcelable> list) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_TITLE, "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享图片"));
    }


    /**
     * <ul>
     * <li>分享任意类型的<b style="color:red">单个</b>文件|不包含目录</li>
     * <li>[<b>经验证！可以发送任意类型的文件！！！</b>]</li>
     * <li># @author http://blog.csdn.net/yuxiaohui78/article/details/8232402</li>
     * <ul>
     *
     * @param context context
     * @param file    Uri.from(file);
     */
    public static void shareFile(Context context, File file) {
        Uri uri;
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, AppUtils.getAppPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("subject", file.getName());  //
        intent.putExtra("body", file.getName());     // 正文
        intent.putExtra(Intent.EXTRA_STREAM, uri);          // 添加附件，附件为file对象
        intent.setType(MyUtils.getMimeType(file));
        context.startActivity(intent);                      // 调用系统的mail客户端进行发送
    }

    /**
     * <ul>
     * <li>分享任意类型的<b style="color:red">多个</b>文件|不包含目录</li>
     * <li>[<b>经验证！可以发送任意类型的文件！！！</b>]</li>
     * <li># @author http://blog.csdn.net/yuxiaohui78/article/details/8232402</li>
     * <ul>
     *
     * @param context
     * @param uris    list.add(Uri.from(file));
     */
    public static void shareMultipleFiles(Context context, ArrayList<Uri> uris) {

        boolean multiple = uris.size() > 1;
        Intent intent = new Intent(
                multiple ? Intent.ACTION_SEND_MULTIPLE
                        : Intent.ACTION_SEND);

        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
            Uri value = uris.get(0);
            String ext = MimeTypeMap.getFileExtensionFromUrl(value.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if (mimeType == null) {
                mimeType = "*/*";
            }
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, value);
        }
        context.startActivity(Intent.createChooser(intent, "分享到"));
    }
}
