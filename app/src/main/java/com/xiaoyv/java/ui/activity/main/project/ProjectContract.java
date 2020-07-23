package com.xiaoyv.java.ui.activity.main.project;


import androidx.appcompat.widget.AppCompatImageView;

import com.unnamed.b.atv.model.TreeNode;
import com.xiaoyv.java.base.BasePresenter;
import com.xiaoyv.java.base.BaseView;
import com.xiaoyv.java.mode.JavaProject;

import java.io.File;
import java.util.List;

/**
 * 项目页面
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public interface ProjectContract {
    interface View extends BaseView<ProjectContract.Presenter> {

        /**
         * 显示新建项目对话框
         */
        void showCreateJavaProject();

        /**
         * 显示项目列表
         *
         * @param projectList 项目列表
         */
        void showProjectList(List<File> projectList);

        /**
         * 显示项目树
         *
         * @param rootNode 项目树
         */
        void showProjectData(TreeNode rootNode);

        /**
         * 打开文件
         *
         * @param file 选中的文件
         */
        void openFile(File file);

        /**
         * 运行 Dex
         *
         * @param file Dex
         */
        void runDex(File file);

        /**
         * 文件夹条目的更多操作
         *
         * @param moreView moreView
         * @param dir      文件夹条目
         */
        void showOperateMenu(AppCompatImageView moreView, File dir);

        /**
         * 长按删除文件夹条目
         *
         * @param fileOrDir 文件夹条目
         */
        void showDeleteFile(File fileOrDir);

        /**
         * 新建包
         *
         * @param dir 长按的文件夹条目
         */
        void showCreatePackageDialog(File dir);

        void showCreateClassDialog(File dir);
    }

    interface Presenter extends BasePresenter {

        /**
         * 载入项目列表
         */
        void loadProjectList();

        /**
         * 载入项目文件数据
         *
         * @param javaProject 项目
         */
        void loadProjectData(JavaProject javaProject, TreeNode rootNode);

        /**
         * 历遍项目文件
         *
         * @param rootNode 根节点
         * @param file     文件
         */
        void listFileNode(TreeNode rootNode, File file);

        /**
         * 返回上一级
         */
        boolean onBackPressed();

    }
}
