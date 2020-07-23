package com.xiaoyv.java.ui.activity.splash;

import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.xiaoyv.java.R;
import com.xiaoyv.java.ui.activity.main.MainActivity;


/**
 * 主页
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class SplashPresenter implements SplashContract.Presenter {
    @NonNull
    private final SplashContract.View view;
    public CountDownTimer countDownTimer;
    private long splashDelayTime = 4200;

    SplashPresenter(@NonNull SplashContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        startCountDown();
    }

    @Override
    public void startCountDown() {
        countDownTimer = new CountDownTimer(splashDelayTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long l = millisUntilFinished / 1000;
                if (l < 2.5) {
                    view.showSkipText(l + Utils.getApp().getString(R.string.splash_skip_time));
                    view.showSkipListener();
                } else {
                    view.showSkipText(l + "s");
                }
            }

            @Override
            public void onFinish() {
                ActivityUtils.finishAllActivities(true);
                ActivityUtils.startActivity(MainActivity.class);
            }
        };
        countDownTimer.start();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public CountDownTimer getTimer() {
        return countDownTimer;
    }
}
