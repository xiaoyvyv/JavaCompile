package com.xiaoyv.java.mode.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.smtt.sdk.QbSdk;

public class X5InitService extends IntentService {
    private static final String ACTION_X5INIT = "X5InitService.ACTION_X5INIT";

    public X5InitService() {
        super("X5InitService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, X5InitService.class);
        intent.setAction(ACTION_X5INIT);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                if (ACTION_X5INIT.equals(action)) {
                    handleActionFoo();
                }
            }
        }
    }

    private void handleActionFoo() {
        QbSdk.setDownloadWithoutWifi(true);
        // x5内核初始化接口
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.i("X5内核启动结果：" + arg0);
            }

            @Override
            public void onCoreInitFinished() {

            }
        });
    }

}
