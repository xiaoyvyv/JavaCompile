package com.xiaoyv.java.ui.activity.main.editor;


import com.google.googlejavaformat.java.FormatterException;
import com.xiaoyv.java.base.BasePresenter;
import com.xiaoyv.java.base.BaseView;

import java.io.File;

/**
 * 编辑器
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public interface EditorContract {
    interface View extends BaseView<Presenter> {

        /**
         * 插入导包文本
         *
         * @param importText 导包文本
         */
        void sendImportText(String importText);

        /**
         * 格式化失败
         *
         * @param exception 失败
         */
        void formatCodeFail(FormatterException exception);

        /**
         * 格式化成功
         *
         * @param sourceCode 格式化后的代码
         */
        void formatCodeSuccess(String sourceCode);

        /**
         * 形式编译日志框
         */
        void showLog();

        /**
         * 设置日志可关闭
         */
        void hideLog();

        /**
         * 显示编译进度
         *
         * @param task     任务名称
         * @param progress 编译进度
         */
        void showProgress(String task, int progress);

        /**
         * 编译错误日志
         *
         * @param err 错误日志
         */
        void showErrorInfo(String err);

        /**
         * 编译正常日志
         *
         * @param out 正常日志
         */
        void showNormalInfo(String out);

        /**
         * 设置日志关闭监听
         *
         * @param dexPath dex文件
         */
        void showLogDismissListener(String dexPath);
    }

    interface Presenter extends BasePresenter {
        /**
         * 编译运行单个Java文件
         *
         * @param javaFile 单个Java文件
         */
        void runFile(File javaFile);

        void formatJavaCode(String codeStr, boolean b);

        void findPackage(String selectedText, boolean isHide);

        /**
         * 返回上一级
         */
        boolean onBackPressed();
    }
}
