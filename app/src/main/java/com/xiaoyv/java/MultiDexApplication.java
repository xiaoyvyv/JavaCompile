package com.xiaoyv.java;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

@SuppressLint("Registered")
public class MultiDexApplication extends Application {

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
