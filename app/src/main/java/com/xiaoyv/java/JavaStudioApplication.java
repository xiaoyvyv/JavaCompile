package com.xiaoyv.java;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpHeaders;
import com.xiaoyv.java.http.OkHttp;
import com.xiaoyv.javaengine.JavaEngineApplication;

import okhttp3.OkHttpClient;

@SuppressLint("Registered")
public class JavaStudioApplication extends JavaEngineApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        CrashUtils.init(PathUtils.getExternalAppFilesPath() + "/Crash");

        OkGo.getInstance()
                .init(this)                                                 // 必须调用初始化
                .setOkHttpClient(OkHttp.getOkHttpClient(this))     // 建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)          // 全局统一缓存模式
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)               // 全局统一缓存时间，永不过期。
                .setRetryCount(2);                                          // 全局统一超时重连2次
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
