package com.xiaoyv.java.base;

import android.view.View;

import androidx.core.widget.ContentLoadingProgressBar;

import com.blankj.utilcode.util.FragmentUtils;
import com.xiaoyv.java.R;

/**
 * 新长大助手 抽取基类 App
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public abstract class BaseFragmentActivity<T extends BaseFragment> extends BaseActivity {
    public T fragment;
    protected View rootView;
    protected View rootLoading;
    protected ContentLoadingProgressBar rootLoadingBar;

    /**
     * @return 获取布局文件ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_root;
    }

    /**
     * 初始化视图
     */
    @Override
    protected void initView() {
        // 绑定 默认布局文件
        rootView = findView(R.id.root_view);
        rootLoading = findView(R.id.root_loading);
        rootLoadingBar = findView(R.id.root_loading_bar);
    }

    /**
     * 载入数据
     */
    @Override
    protected void initData() {
        fragment = createFragment();
        fragment.setUserVisibleHint(true);
        FragmentUtils.add(getSupportFragmentManager(), fragment, rootView.getId(), false);
        createPresenter(fragment);
    }

    /**
     * 创建 P 层
     */
    protected abstract void createPresenter(T fragment);

    protected abstract T createFragment();

    /**
     * 显示正在加载内容的视图
     */
    public void showContentLoadingView() {
        rootLoadingBar.onAttachedToWindow();
        rootLoadingBar.show();
        rootLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏正在加载内容的视图
     */
    public void hideContentLoadingView() {
        rootLoadingBar.onDetachedFromWindow();
        rootLoadingBar.hide();
        rootLoading.setVisibility(View.GONE);
    }
}
