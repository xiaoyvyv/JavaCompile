package com.xiaoyv.java.ui.activity.splash;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.xiaoyv.java.base.BaseFragmentActivity;


/**
 * 主页
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class SplashActivity extends BaseFragmentActivity<SplashFragment> {

    public static void start() {
        ActivityUtils.startActivity(SplashActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenUtils.setFullScreen(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void createPresenter(SplashFragment fragment) {
        new SplashPresenter(fragment);
    }

    @Override
    protected SplashFragment createFragment() {
        return SplashFragment.newInstance();
    }

    @Override
    public void onBackPressed() {
        if (!fragment.onBackPressed()) {
           super.onBackPressed();
        }
    }
}
