package com.lib.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xiaoyv.java.R;
import com.xiaoyv.java.url.Url;

import java.net.URISyntaxException;

/**
 * Created by Administrator on 2018/2/4.
 */

public class AlipayUtil {
    private static final String AlipayCodePic = Url.My_Img_Home + "/yangtzeu/alipay.jpg";

    // 支付宝包名
    private static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";

    // 旧版支付宝二维码通用 Intent Scheme Url 格式
    private static final String INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&" +
            "clientVersion=10.1.55&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s" +
            "%3Dweb-other&_t=1472443966571#Intent;" +
            "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";

    public static void startAlipayClient(Activity activity, String urlCode) {
        startIntentUrl(activity, INTENT_URL_FORMAT.replace("{urlCode}", urlCode));
    }


    static void startAlipayClient(Context activity) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
        activity.startActivity(intent);
    }

    public static void donateAlipay(Activity activity) {
        AlipayUtil.startAlipayClient(activity, activity.getString(R.string.apply_me_key));
    }

    static void donateAlipayByPic(final Activity activity) {
        MyUtils.saveFile(activity, AlipayCodePic, PathUtils.getExternalStoragePath() + "/A_Tool/Donate/", "支付宝付款码.png");
        MyUtils.getAlert(activity, "请等待二维码加载完成后，在支付宝扫一扫中选择相册里的二维码即可！", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAlipayScan(activity);
            }
        }).show();
    }

    /**
     * 打开 Intent Scheme Url
     *
     * @param activity      Parent Activity
     * @param intentFullUrl Intent 跳转地址
     */
    private static void startIntentUrl(Activity activity, String intentFullUrl) {
        try {
            Intent intent = Intent.parseUri(
                    intentFullUrl,
                    Intent.URI_INTENT_SCHEME
            );
            activity.startActivity(intent);
        } catch (URISyntaxException | ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     *
     * @param context Context
     * @return 支付宝客户端是否已安装
     */
    public static boolean hasInstalledAlipayClient(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 打开支付宝扫一扫界面
     *
     * @param context Context
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void openAlipayScan(Context context) {
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (context instanceof TileService) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ((TileService) context).startActivityAndCollapse(intent);
                }
            } else {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            ToastUtils.showShort("你好像没有安装支付宝");
        }
    }
}