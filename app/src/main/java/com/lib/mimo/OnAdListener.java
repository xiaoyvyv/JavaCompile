package com.lib.mimo;


import com.blankj.utilcode.util.LogUtils;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;

public abstract class OnAdListener implements MimoAdListener {
    public final static int AdPresent = 0;
    public final static int AdClick = 1;
    public final static int AdDismissed = 2;
    public final static int AdFailed = 3;
    public final static int AdLoaded = 4;
    public final static int StimulateSuccess = 5;
    public final static int VideoSuccess = 6;
    public IAdWorker iAdWorker = null;

    public void onResultListener(IAdWorker iAdWorker, int state, Object info) {

    }

    @Override
    public void onAdPresent() {
        LogUtils.i("广告被显示");
        onResultListener(iAdWorker, AdPresent, "广告被显示");
    }

    @Override
    public void onAdClick() {
        LogUtils.i("广告被点击");
        onResultListener(iAdWorker, AdClick, "广告被点击");
    }

    @Override
    public void onAdDismissed() {
        LogUtils.i("广告被关闭");
        onResultListener(iAdWorker, AdDismissed, "广告被关闭");
    }

    @Override
    public void onAdFailed(String s) {
        LogUtils.i("广告加载失败：" + s);
        onResultListener(iAdWorker, AdFailed, s);
    }

    @Override
    public void onAdLoaded(int i) {
        LogUtils.i("广告加载：" + i);
        onResultListener(iAdWorker, AdLoaded, i);
    }

    @Override
    public void onStimulateSuccess() {
        LogUtils.i("广告完成");
        onResultListener(iAdWorker, StimulateSuccess, "广告完成");
    }


}
