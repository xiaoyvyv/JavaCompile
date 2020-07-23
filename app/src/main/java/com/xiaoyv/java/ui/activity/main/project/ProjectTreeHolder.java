package com.xiaoyv.java.ui.activity.main.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.sun.tools.javac.util.Log;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.xiaoyv.java.R;
import com.xiaoyv.java.mode.JavaProject;

import java.io.File;

/**
 * 文件树数据适配器
 */
@SuppressLint({"InflateParams", "SetTextI18n"})
public class ProjectTreeHolder extends TreeNode.BaseNodeViewHolder<File> {
    public static final int CHOOSE_JAR = 123;
    private final ProjectContract.View view;

    public ProjectTreeHolder(Context context, ProjectContract.View view) {
        super(context);
        this.view = view;
    }

    @Override
    public View createNodeView(TreeNode treeNode, File file) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.fragment_project_leaf, null);
        itemView.setPadding((treeNode.getLevel() - 1) * 20, 0, 0, 0);

        // 查找控件
        AppCompatImageView iconView = itemView.findViewById(R.id.project_leaf_icon);
        AppCompatImageView arrowView = itemView.findViewById(R.id.project_leaf_arrow);
        AppCompatImageView moreView = itemView.findViewById(R.id.project_leaf_more);
        AppCompatTextView nameView = itemView.findViewById(R.id.project_leaf_name);
        View hrView = itemView.findViewById(R.id.project_leaf_hr);

        String fileName = file.getName();
        String filePath = file.getAbsolutePath();


        // 设置名称、箭头是否隐藏
        nameView.setText(fileName);
        arrowView.setVisibility(treeNode.isLeaf() ? View.INVISIBLE : View.VISIBLE);
        arrowView.setImageResource(treeNode.isExpanded() ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_right);

        // 设置文件图标
        moreView.setVisibility(View.GONE);
        switch (JavaProject.getFileType(file)) {
            case DIR_PROJECT:
                // 默认展开工程文件夹
                treeNode.setExpanded(true);
                arrowView.setImageResource(R.drawable.ic_arrow_down);
                // 设置工程名称
                nameView.setText(context.getString(R.string.project_now) + fileName);
                iconView.setImageResource(R.drawable.ic_folder_project);
                moreView.setVisibility(View.VISIBLE);
                hrView.setVisibility(View.VISIBLE);
                break;
            case DIR_FOLDER:
                iconView.setImageResource(R.drawable.ic_project);
                moreView.setVisibility(View.VISIBLE);
                hrView.setVisibility(View.GONE);

                // bin 和 build 及其子目录不提供操作
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.contains("/" + JavaProject.binDirName) || absolutePath.contains("/" + JavaProject.buildDirName)) {
                    moreView.setVisibility(View.GONE);
                }
                break;
            case DIR_PACKAGE:
                iconView.setImageResource(R.drawable.ic_folder_package);
                moreView.setVisibility(View.VISIBLE);
                hrView.setVisibility(View.GONE);
                break;
            case DIR_PACKAGE_EMPTY:
                iconView.setImageResource(R.drawable.ic_folder_package_empty);
                moreView.setVisibility(View.VISIBLE);
                hrView.setVisibility(View.GONE);
                break;
            case FILE_JAVA:
                iconView.setImageResource(R.drawable.ic_file_java);
                hrView.setVisibility(View.GONE);
                break;
            case FILE_INI:
                iconView.setImageResource(R.drawable.ic_file_ini);
                hrView.setVisibility(View.GONE);
                break;
            case FILE:
                iconView.setImageResource(R.drawable.ic_file);
                hrView.setVisibility(View.GONE);
                break;
            case NONE:
                iconView.setImageResource(R.drawable.ic_none);
                hrView.setVisibility(View.GONE);
                break;
        }

        // 条目的点击操作
        treeNode.setClickListener((node, o) -> {
            arrowView.setImageResource(treeNode.isExpanded() ? R.drawable.ic_arrow_right : R.drawable.ic_arrow_down);

            // 如果点击了文件
            if (treeNode.isLeaf() && FileUtils.isFile(file)) {
                if (fileName.endsWith(".java") || fileName.endsWith(".txt") || fileName.endsWith(".xml"))
                    view.openFile(file);
                else if (fileName.endsWith(".dex"))
                    view.runDex(file);
                else
                    view.showError(context.getString(R.string.project_cant_open));
            }
        });

        // 长按删除
        treeNode.setLongClickListener((node, o) -> {
            if (FileUtils.isDir(file)) {
                // bin 和 scr 和 build 和 lib 不执行删除操作
                if (StringUtils.equals(fileName, JavaProject.binDirName)
                        || StringUtils.equals(fileName, JavaProject.srcDirName)
                        || StringUtils.equals(fileName, JavaProject.buildDirName)
                        || StringUtils.equals(fileName, JavaProject.libDirName)) {
                    return true;
                }
            }
            view.showDeleteFile(file);
            return true;
        });

        // 文件夹的更多操作
        moreView.setOnClickListener(v ->
                view.showOperateMenu(moreView, file));

        return itemView;
    }


    /**
     * 输入依赖对话框
     */

    @SuppressLint("InflateParams")
    private void showNewProjectDialog() {
        /*
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_project_dialog_dependency, null);
        TextInputEditText dependencyNameView = view.findViewById(R.id.dependencyName);
        dependencyNameView.setHint("<dependency>\n" +
                "\t<groupId>org.jsoup</groupId>\n" +
                "\t<artifactId>jsoup</artifactId>\n" +
                "\t<version>1.12.1</version>\n" +
                "</dependency>\n" +
                "...");

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("请输入 Maven 依赖")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String dependency = String.valueOf(dependencyNameView.getText());
                    if (StringUtils.isEmpty(dependency)) {
                        ToastUtils.showShort("请输入 Maven 依赖");
                        return;
                    }
                    List<Dependency> dependencies = JavaMaven.getInstance().getDependence(dependency);
                    if (ObjectUtils.isEmpty(dependencies)) {
                        ToastUtils.showShort("请输入的依赖格式错误");
                        return;
                    }

                    String s = FileIOUtils.readFile2String(JavaProject.getCurrentDependencyFilePath());
                    if (!s.contains(dependency)) {
                        FileIOUtils.writeFileFromString(JavaProject.getCurrentDependencyFilePath(), dependency, true);
                    }


                    KeyboardUtils.hideSoftInput(dependencyNameView);

                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMax(100);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setTitle("Maven 依赖下载数目：" + dependencies.size());
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);

                    final int[] allSize = {dependencies.size()};
                    */
        // TODO Maven下载
        /*
                    Maven.getInstance().downloadDependence(dependencies, Project.getCurrentLibDirPath(), new DownloadUtils.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess() {
                            allSize[0]--;
                            if (allSize[0] == 0) {
                                progressDialog.dismiss();
                                projectFragment.readProjectList();
                            }
                        }

                        @Override
                        public void onDownloading(int progress) {
                            progressDialog.setProgress(progress);
                        }

                        @Override
                        public void onDownloadFailed(String error) {
                            ToastUtils.showShort(error);
                            allSize[0]--;
                            if (allSize[0] == 0) {
                                progressDialog.dismiss();
                                projectFragment.readProjectList();
                            }
                        }
                    });*/
        /*
                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        */
    }

}
