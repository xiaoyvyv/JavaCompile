package com.xiaoyv.java.ui.activity.main.function;


import com.xiaoyv.java.base.BasePresenter;
import com.xiaoyv.java.base.BaseView;

/**
 * 功能
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public interface FunctionContract {
    interface View extends BaseView<FunctionContract.Presenter> {

    }

    interface Presenter extends BasePresenter {

        /**
         * 返回上一级
         */
        boolean onBackPressed();
    }
}
