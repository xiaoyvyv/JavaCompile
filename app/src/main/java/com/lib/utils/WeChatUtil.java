package com.lib.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xiaoyv.java.url.Url;


/**
 * Created by didikee on 2017/7/28.
 * 使用微信捐赠
 */

public class WeChatUtil {
    private static final String WeChatCodePic = Url.My_Img_Home + "/yangtzeu/we_chat.png";
    // 微信包名
    private static final String TENCENT_PACKAGE_NAME = "com.tencent.mm";
    // 微信二维码扫描页面地址
    private static final String TENCENT_ACTIVITY_BIZSHORTCUT = "com.tencent.mm.action.BIZSHORTCUT";
    // Extra data
    private static final String TENCENT_EXTRA_ACTIVITY_BIZSHORTCUT = "LauncherUI.From.Scaner.Shortcut";

    /**
     * 启动微信二维码扫描页
     * ps： 需要你引导用户从文件中扫描二维码
     *
     * @param activity activity
     */
    private static void gotoWeChatQrScan(@NonNull Activity activity) {
        Intent intent = new Intent(TENCENT_ACTIVITY_BIZSHORTCUT);
        intent.setPackage(TENCENT_PACKAGE_NAME);
        intent.putExtra(TENCENT_EXTRA_ACTIVITY_BIZSHORTCUT, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.showShort("你好像没有安装微信");

        }
    }


    /**
     * 微信捐赠
     *
     * @param activity activity
     */
    public static void donateWeiXin(final Activity activity) {
        MyUtils.saveFile(activity, WeChatCodePic, PathUtils.getExternalStoragePath() + "/A_Tool/Donate/", "微信付款码.png");
        MyUtils.getAlert(activity, "请等待二维码加载完成后，在微信扫一扫中选择相册里的二维码即可！",
                (dialog, which) ->
                        gotoWeChatQrScan(activity)).show();
    }
}