package com.xiaoyv.java.ui.activity.splash;


import android.os.CountDownTimer;

import com.xiaoyv.java.base.BasePresenter;
import com.xiaoyv.java.base.BaseView;

/**
 * 启动页
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public interface SplashContract {
    interface View extends BaseView<Presenter> {

        /**
         * 跳过文字
         *
         * @param skipText 跳过
         */
        void showSkipText(String skipText);

        /**
         * 跳过监听
         */
        void showSkipListener();
    }

    interface Presenter extends BasePresenter {

        /**
         * 倒计时
         */
        void startCountDown();

        /**
         * 返回上一级
         */
        boolean onBackPressed();

        /**
         * 倒计时
         *
         * @return 倒计时
         */
        CountDownTimer getTimer();
    }
}
