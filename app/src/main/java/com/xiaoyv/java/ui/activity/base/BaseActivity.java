package com.xiaoyv.java.ui.activity.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ScreenUtils;

public abstract class BaseActivity extends AppCompatActivity implements BaseActivityMode {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenUtils.setPortrait(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init() {
        findViews();
        setEvents();
    }
}
