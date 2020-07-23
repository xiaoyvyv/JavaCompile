package com.xiaoyv.java.ui.activity.main.function;

import androidx.annotation.NonNull;


/**
 * 功能
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class FunctionPresenter implements FunctionContract.Presenter {
    @NonNull
    private final FunctionContract.View view;

    public FunctionPresenter(@NonNull FunctionContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

}
