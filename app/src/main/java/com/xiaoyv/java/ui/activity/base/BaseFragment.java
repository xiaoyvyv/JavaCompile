package com.xiaoyv.java.ui.activity.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment implements BaseFragmentMode {
    private View rootView;
    private boolean isViewBind = false;
    private boolean isLoaded = false;
    private boolean visibleHint = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(setContentView(), container, false);
        findViews();
        isViewBind = true;
        lazyLoad();
        return rootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visibleHint = isVisibleToUser;
        lazyLoad();
    }

    @Override
    public void lazyLoad() {
        if (visibleHint) {
            if (isViewBind && !isLoaded) {
                setEvents();
                isLoaded = true;
            }
        }
    }

    @Override
    public <T extends View> T findViewById(@IdRes int ids) {
        if (rootView == null) {
            throw new NullPointerException("Fragment引入的布局视图为空");
        } else {
            return rootView.findViewById(ids);
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
