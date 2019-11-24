package com.xiaoyv.java.ui.activity.base;

import android.view.View;

import androidx.annotation.IdRes;

public interface BaseFragmentMode {
    int setContentView();

    void findViews();

    void setEvents();

    void lazyLoad();

    <T extends View> T findViewById(@IdRes int ids);

    View getRootView();
}
