package com.xiaoyv.java.ui.activity.main.project;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.xiaoyv.java.R;
import com.xiaoyv.java.base.BaseFragmentActivity;
import com.xiaoyv.java.mode.JavaProject;

import java.io.File;
import java.util.Collections;
import java.util.List;


/**
 * 项目页面
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class ProjectPresenter implements ProjectContract.Presenter {
    @NonNull
    private final ProjectContract.View view;
    private final Context context;


    public ProjectPresenter(@NonNull ProjectContract.View view, Context context) {
        this.view = view;
        this.context = context;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadProjectList();
    }

    @Override
    public void loadProjectList() {
        List<File> projectList = FileUtils.listFilesInDirWithFilter(JavaProject.rootDirPtah, FileUtils::isDir);
        view.showProjectList(projectList);
    }


    @Override
    public void loadProjectData(JavaProject javaProject, TreeNode rootNode) {
        if (ObjectUtils.isEmpty(javaProject)) {
            view.showProjectData(rootNode);
            return;
        }
        // 历遍项目文件
        listFileNode(rootNode, new File(javaProject.getProjectDirPath()));
        // 显示项目树
        view.showProjectData(rootNode);
    }

    @Override
    public void listFileNode(TreeNode rootNode, File file) {
        ProjectTreeHolder treeHolder = new ProjectTreeHolder(context, view);

        // 文件
        if (FileUtils.isFile(file)) {
            TreeNode fileNode = new TreeNode(file);
            fileNode.setViewHolder(treeHolder);
            rootNode.addChild(fileNode);
            return;
        }

        // 文件夹节点，默认不展开
        TreeNode dirNode = new TreeNode(file);
        dirNode.setViewHolder(treeHolder);
        rootNode.addChild(dirNode);

        // 排序
        List<File> fileList = FileUtils.listFilesInDir(file);
        Collections.sort(fileList, (o1, o2) -> {
            if (o1.isDirectory() && o2.isFile())
                return -1;
            if (o1.isFile() && o2.isDirectory())
                return 1;
            return o1.getName().compareTo(o2.getName());
        });

        // 继续历遍
        for (File file1 : fileList) {
            listFileNode(dirNode, file1);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


}
