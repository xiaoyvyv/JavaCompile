package com.xiaoyv.java.ui.activity.splash;

import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.xiaoyv.java.R;
import com.xiaoyv.java.base.BaseFragment;


/**
 * 启动页
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class SplashFragment extends BaseFragment<SplashContract.Presenter> implements SplashContract.View {
    private FrameLayout splashContainer;
    private AppCompatTextView splashSkipView;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        super.setFragmentPresenter(presenter);
    }

    @Override
    public void showError(String failMsg) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_splash;
    }

    @Override
    public void initView() {
        splashContainer = findView(R.id.splash_container);
        splashSkipView = findView(R.id.splash_skip_view);

    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void showSkipText(String skipText) {
        splashSkipView.setText(skipText);
    }

    @Override
    public void showSkipListener() {
        splashSkipView.setOnClickListener(v -> {
            presenter.getTimer().cancel();
            presenter.getTimer().onFinish();
        });
    }
}