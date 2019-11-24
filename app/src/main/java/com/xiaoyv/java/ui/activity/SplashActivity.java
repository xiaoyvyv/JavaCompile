package com.xiaoyv.java.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lib.mimo.MimoUtils;
import com.lib.mimo.OnAdListener;
import com.lib.utils.MyUtils;
import com.miui.zeus.mimo.sdk.MimoSdk;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.xiaoyv.java.R;
import com.xiaoyv.java.ui.activity.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;//需要请求的权限
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;//需要请求的权限
    private static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;//需要请求的权限
    private String[] permission = new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, READ_PHONE_STATE};
    private static final String AppDebugSignatureMD5 = "2E:68:6C:0B:CC:9F:41:1E:39:7D:1E:B5:B0:F3:05:3B";
    private static final String AppReleaseSignatureMD5 = "AD:3B:5A:F3:92:92:8D:27:BE:C9:1A:60:AB:42:3B:7F";
    private LinearLayout ad_container;
    private TextView times;
    private TextView ad_title;
    private TextView skipView;
    private ImageView splashImage;
    public IAdWorker worker;
    private long ad_time = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    @Override
    public void findViews() {
        ad_container = findViewById(R.id.ad_container);
        times = findViewById(R.id.times);
        ad_title = findViewById(R.id.ad_title);
        skipView = findViewById(R.id.skipView);
        splashImage = findViewById(R.id.splashImage);
    }

    @Override
    public void setEvents() {
        boolean granted = PermissionUtils.isGranted(permission);
        if (granted) {
            enterApp();
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("用户授权")
                .setMessage("运行软件需要以下权限：\n\n*储存读写权限\n*日志访问权限")
                .setPositiveButton("授权", (dialog, which) -> {
                    PermissionUtils.permission(permission).callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            enterApp();
                        }

                        @Override
                        public void onDenied() {
                            ToastUtils.showLong("请到设置中开启储存和日志权限");
                            ActivityUtils.finishActivity(SplashActivity.this);
                        }
                    }).request();
                })
                .setNegativeButton("退出", (dialog, which) -> {
                    ToastUtils.showLong("请授权储存和日志权限");
                    ActivityUtils.finishActivity(SplashActivity.this);
                }).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void enterApp() {
        checkCopyRight();
        loadMIAD();
    }

    public void loadMIAD() {
        while (true) {
            //如果广告SDK启动失败，则跳出
            if (MimoUtils.IS_READY_ERROR) {
                startCountDown();
                break;
            }
            if (MimoSdk.isSdkReady()) {
                MimoUtils.FeedView.loadBigPicture(this, 1, new OnAdListener() {
                    @Override
                    public void onResultListener(IAdWorker iAdWorker, int state, Object info) {
                        LogUtils.e(state, info);
                        switch (state) {
                            case OnAdListener.AdLoaded:
                                splashImage.setVisibility(View.GONE);
                                ad_container.setVisibility(View.VISIBLE);
                                View ad = MimoUtils.Action.updateAdView(iAdWorker, 0);
                                ad_container.addView(ad);
                                startCountDown();

                                SplashActivity.this.worker = iAdWorker;
                                break;
                            case OnAdListener.AdDismissed:
                            case OnAdListener.AdFailed:
                                ad_title.setVisibility(View.GONE);
                                splashImage.setVisibility(View.VISIBLE);
                                ad_container.setVisibility(View.GONE);

                                MyUtils.startActivity(MainActivity.class);
                                SplashActivity.this.finish();
                                break;
                        }
                    }

                });
                break;
            }
        }
    }

    private CountDownTimer countDownTimer;

    private void startCountDown() {
        countDownTimer = new CountDownTimer(ad_time, 1000) {

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                long l = millisUntilFinished / 1000;
                if (l < 3) {
                    skipView.setText(l + "s 跳过");
                    skipView.setOnClickListener(v -> {
                        countDownTimer.cancel();
                        countDownTimer.onFinish();
                    });
                } else {
                    skipView.setText(l + "s");
                }
            }

            @Override
            public void onFinish() {
                MyUtils.startActivity(MainActivity.class);
                SplashActivity.this.finish();
            }
        };
        countDownTimer.start();
    }


    public void checkCopyRight() {
        String signatureMD5 = AppUtils.getAppSignatureMD5();
        if (StringUtils.equals(signatureMD5, AppDebugSignatureMD5)) {
            LogUtils.v("当前版本为内测版");
        } else if (StringUtils.equals(signatureMD5, AppReleaseSignatureMD5)) {
            LogUtils.v("当前版本为发布版");
        } else {
            ToastUtils.showLong("你当前使用的版本问非官方版，即将退出！");
            new Handler(Looper.getMainLooper()).postDelayed(AppUtils::exitApp, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        MimoUtils.Action.recyclerView(worker);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (ad_container.getVisibility() == View.VISIBLE) return;
        super.onBackPressed();
    }
}
