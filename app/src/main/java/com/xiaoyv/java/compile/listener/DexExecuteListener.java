package com.xiaoyv.java.compile.listener;


public interface DexExecuteListener {
    void onExeCute();

    void onError(Throwable error);
}
