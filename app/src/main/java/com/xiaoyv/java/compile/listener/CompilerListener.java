package com.xiaoyv.java.compile.listener;


public abstract class CompilerListener {
    public abstract void onSuccess(String path);

    public abstract void onError(Throwable error);

    public void onProgress(String task, int progress) {

    }
}