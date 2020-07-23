package com.xiaoyv.java.base;

public interface BaseView<P> {

    void setPresenter(P presenter);

    void showError(String failMsg);
}
