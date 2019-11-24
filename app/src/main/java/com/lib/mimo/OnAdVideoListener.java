package com.lib.mimo;

import com.miui.zeus.mimo.sdk.listener.MimoRewardVideoListener;

public abstract class OnAdVideoListener implements MimoRewardVideoListener {
    public final static int onVideoStart = 0;
    public final static int onVideoPause = 1;
    public final static int onVideoComplete = 2;
    public final static int onAdPresent = 3;
    public final static int onAdClick = 4;
    public final static int onAdDismissed = 5;
    public final static int onAdFailed = 6;
    public final static int onAdLoaded = 7;
    public final static int onStimulateSuccess = 8;

    public abstract void onResultListener(int state, Object info);

    @Override
    public void onVideoStart() {
        onResultListener(onVideoStart, "onVideoStart");
    }

    @Override
    public void onVideoPause() {
        onResultListener(onVideoPause, "onVideoPause");
    }

    @Override
    public void onVideoComplete() {
        onResultListener(onVideoComplete, "onVideoComplete");
    }

    @Override
    public void onAdPresent() {
        onResultListener(onAdPresent, "onAdPresent");
    }

    @Override
    public void onAdClick() {
        onResultListener(onAdClick, "onAdClick");
    }

    @Override
    public void onAdDismissed() {
        onResultListener(onAdDismissed, "onAdClick");
    }

    @Override
    public void onAdFailed(String s) {
        onResultListener(onAdFailed, s);
    }

    @Override
    public void onAdLoaded(int i) {
        onResultListener(onAdLoaded, i);
    }

    @Override
    public void onStimulateSuccess() {
        onResultListener(onStimulateSuccess, "onStimulateSuccess");
    }
}
