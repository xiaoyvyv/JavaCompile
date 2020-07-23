package com.xiaoyv.java.ui.activity.main;

import com.xiaoyv.java.base.BasePresenter;
import com.xiaoyv.java.base.BaseView;

/**
 * 主页
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public interface MainContract {
    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        /**
         * 检查软件更新
         */
        void checkUpdate();

        /**
         * 返回上一级
         */
        boolean onBackPressed();
    }
}
