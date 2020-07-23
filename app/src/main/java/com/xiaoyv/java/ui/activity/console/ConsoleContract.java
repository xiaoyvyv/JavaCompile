package com.xiaoyv.java.ui.activity.console;

import com.xiaoyv.java.base.BasePresenter;
import com.xiaoyv.java.base.BaseView;

/**
 * 控制台
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public interface ConsoleContract {
    interface View extends BaseView<Presenter> {
        /**
         * 输出错误日志
         * @param err 错误日志
         */
        void showStderr(CharSequence err);

        /**
         * 输出正常日志
         * @param out 正常日志
         */
        void showStdout(CharSequence out);
    }

    interface Presenter extends BasePresenter {

        /**
         * 运行 DexFile
         *
         * @param args Main(String[] args)参数
         */
        void runDexFile(String[] args);

        /**
         * 返回上一级
         */
        boolean onBackPressed();
    }
}
