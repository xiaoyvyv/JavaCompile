package com.xiaoyv.java.ui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.lib.utils.DownloadUtils;
import com.lib.utils.MyUtils;
import com.lib.utils.ShareUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.xiaoyv.java.R;
import com.xiaoyv.java.bean.Dependency;
import com.xiaoyv.java.mode.Maven;
import com.xiaoyv.java.mode.Project;
import com.xiaoyv.java.ui.activity.ConsoleActivity;
import com.xiaoyv.java.ui.fragment.ProjectFragment;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TreeViewAdapter extends TreeNode.BaseNodeViewHolder<File> {
    public static final int CHOOSE_JAR = 123;
    private final ProjectFragment projectFragment;

    public TreeViewAdapter(Context context, ProjectFragment projectFragment) {
        super(context);
        this.projectFragment = projectFragment;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View createNodeView(TreeNode treeNode, File file) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_project_item, null);
        ImageView iconView = view.findViewById(R.id.icon);
        ImageView arrowView = view.findViewById(R.id.arrow);
        ImageView moreView = view.findViewById(R.id.more);
        TextView nameView = view.findViewById(R.id.name);
        String fileName = file.getName();
        String fileAbsolutePath = file.getAbsolutePath();
        nameView.setText(fileName);


        view.setPadding(treeNode.getLevel() * 20, 0, 0, 0);

        if (treeNode.isLeaf()) {
            arrowView.setVisibility(View.INVISIBLE);
        } else {
            arrowView.setVisibility(View.VISIBLE);
        }

        treeNode.setClickListener((treeNode1, o) -> {
            projectFragment.setCurrentProjectByFile(file);
            // 如果点击了文件
            if (treeNode.isLeaf() && FileUtils.isFile(file)) {
                if (fileName.endsWith(".java")
                        || fileName.endsWith(".txt")
                        || fileName.endsWith(".xml"))
                    projectFragment.setOpenFile(file);
                else if (fileName.endsWith(".dex")) {
                    ConsoleActivity.start(context, fileAbsolutePath);
                } else
                    ToastUtils.showShort("不支持打开此文件");
                return;
            }

            if (treeNode.isExpanded()) {
                arrowView.setImageResource(R.drawable.ic_arrow_right);
            } else {
                arrowView.setImageResource(R.drawable.ic_arrow_down);
            }
        });

        treeNode.setLongClickListener((treeNode12, o) -> {
            // 文件夹
            if (FileUtils.isDir(file)) {
                // 未打开项目
                if (Project.currentProjectPath == null) return true;
                // 长按项目根目录
                if (StringUtils.equals(fileAbsolutePath, Project.currentProjectPath)) return true;

                // bin 和 scr 和 build 和 lib 不执行删除操作
                if (StringUtils.equals(fileName, Project.binDirName)
                        || StringUtils.equals(fileName, Project.srcDirName)
                        || StringUtils.equals(fileName, Project.buildDirName)
                        || StringUtils.equals(fileName, Project.libDirName)) {
                    return true;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Package：" + fileName)
                        .setMessage("是否删除该文件夹及其子内容：" + fileName + "?")
                        .setPositiveButton("确定", (dialog, which) -> {
                            FileUtils.deleteAllInDir(file);
                            FileUtils.deleteDir(file);
                            projectFragment.readProjectList();
                        })
                        .setNegativeButton("取消", null)
                        .create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
            }
            // 文件
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("File:" + fileName)
                        .setMessage("是否删除该文件：" + fileName + "?")
                        .setPositiveButton("确定", (dialog, which) -> {
                            FileUtils.delete(file);
                            projectFragment.readProjectList();
                        })
                        .setNegativeButton("取消", null)
                        .create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
            }
            return true;
        });

        // 文件夹的更多操作
        moreView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, moreView);
            // lib文件夹菜单
            if (StringUtils.equals(fileAbsolutePath, Project.getCurrentLibDirPath())) {
                popupMenu.getMenu().add("导入 Maven 依赖").setOnMenuItemClickListener(item -> {
                    showNewProjectDialog();
                    return false;
                });
                popupMenu.getMenu().add("本地 Jar 依赖").setOnMenuItemClickListener(item -> {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    projectFragment.startActivityForResult(intent, TreeViewAdapter.CHOOSE_JAR);
                    return false;
                });
            }
            // src文件夹及其子文件夹菜单
            else if (fileAbsolutePath.contains(Project.getCurrentSrcDirPath())) {
                popupMenu.getMenu().add("新建 Java Package").setOnMenuItemClickListener(item -> {
                    showNewPackageDialog(file);
                    return false;
                });
                popupMenu.getMenu().add("新建 Java Class").setOnMenuItemClickListener(item -> {
                    showNewClassDialog(file);
                    return false;
                });
            }
            // 工程目录菜单
            else if (StringUtils.equals(fileAbsolutePath, Project.currentProjectPath)) {
                popupMenu.getMenu().add("导出工程源码").setOnMenuItemClickListener(item -> {
                    try {
                        File shareFile = new File(Project.zipPath + "/" + Project.currentProjectName + ".zip");
                        FileUtils.createFileByDeleteOldFile(shareFile);
                        LogUtils.e(Project.currentProjectPath, shareFile.getAbsolutePath());
                        ZipUtils.zipFile(Project.currentProjectPath, shareFile.getAbsolutePath());
                        MyUtils.getAlert(context, "源码导出位置：\n内部存储：" + shareFile.getAbsolutePath(), null).show();
                    } catch (IOException e) {
                        ToastUtils.showShort("源码导出失败");
                    }
                    return false;
                });
                popupMenu.getMenu().add("分享工程源码").setOnMenuItemClickListener(item -> {
                    ThreadUtils.getCachedPool().execute(() -> {
                        try {
                            File shareFile = new File(Project.zipPath + "/" + Project.currentProjectName + ".zip");
                            FileUtils.createFileByDeleteOldFile(shareFile);
                            LogUtils.e(Project.currentProjectPath, shareFile.getAbsolutePath());
                            ZipUtils.zipFile(Project.currentProjectPath, shareFile.getAbsolutePath());
                            ShareUtils.shareFile(context, shareFile);
                        } catch (IOException e) {
                            ToastUtils.showShort("源码导出失败");
                        }
                    });
                    return false;
                });
                popupMenu.getMenu().add("删除工程").setOnMenuItemClickListener(item -> {
                    FileUtils.deleteAllInDir(file);
                    FileUtils.deleteDir(file);
                    projectFragment.readProjectList();
                    return false;
                });
            }

            popupMenu.show();
        });

        // 项目分割线
        View hrView = view.findViewById(R.id.hr);
        hrView.setVisibility(View.GONE);

        // 设置文件图标
        moreView.setVisibility(View.GONE);
        switch (Project.getFileType(file)) {
            case DIR_PROJECT:
                iconView.setImageResource(R.drawable.ic_folder_project);
                // 默认收拢工程文件夹
                treeNode.setExpanded(false);
                hrView.setVisibility(View.VISIBLE);
                nameView.setText("工程：" + fileName);
                moreView.setVisibility(View.VISIBLE);
                break;
            case DIR_FOLDER:
                iconView.setImageResource(R.drawable.ic_project);
                moreView.setVisibility(View.VISIBLE);
                break;
            case DIR_PACKAGE:
                iconView.setImageResource(R.drawable.ic_folder_package);
                moreView.setVisibility(View.VISIBLE);
                break;
            case DIR_PACKAGE_EMPTY:
                iconView.setImageResource(R.drawable.ic_folder_package_empty);
                moreView.setVisibility(View.VISIBLE);
                break;
            case FILE_JAVA:
                iconView.setImageResource(R.drawable.ic_file_java);
                break;
            case FILE_INI:
                iconView.setImageResource(R.drawable.ic_file_ini);
                break;
            case FILE:
                iconView.setImageResource(R.drawable.ic_file);
                break;
            case NONE:
                iconView.setImageResource(R.drawable.ic_none);
                break;
        }

        // bin 和 build 及其子目录不提供操作
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.contains("/" + Project.binDirName) || absolutePath.contains("/" + Project.buildDirName)) {
            moreView.setVisibility(View.GONE);
        }


        // 设置箭头状态
        if (treeNode.isExpanded())
            arrowView.setImageResource(R.drawable.ic_arrow_down);
        else
            arrowView.setImageResource(R.drawable.ic_arrow_right);

        return view;
    }

    /**
     * 输入依赖对话框
     */
    @SuppressLint("InflateParams")
    private void showNewProjectDialog() {
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
                    List<Dependency> dependencies = Maven.getInstance().getDependence(dependency);
                    if (ObjectUtils.isEmpty(dependencies)) {
                        ToastUtils.showShort("请输入的依赖格式错误");
                        return;
                    }

                    String s = FileIOUtils.readFile2String(Project.getCurrentDependencyFilePath());
                    if (!s.contains(dependency)) {
                        FileIOUtils.writeFileFromString(Project.getCurrentDependencyFilePath(), dependency, true);
                    }


                    KeyboardUtils.hideSoftInput(dependencyNameView);

                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMax(100);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setTitle("Maven 依赖下载数目：" + dependencies.size());
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);

                    final int[] allSize = {dependencies.size()};
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
                    });
                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 新建 Java Package 对话框
     */
    @SuppressLint("InflateParams")
    private void showNewPackageDialog(File dir) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_project_dialog_package, null);
        TextInputEditText packageNameView = view.findViewById(R.id.packageName);
        TextView parentNameView = view.findViewById(R.id.parentName);

        String path = dir.getAbsolutePath();
        String parentName = path.substring(path.indexOf(Project.srcDirName) + Project.srcDirName.length());
        parentName = parentName.replace("/", ".");
        if (parentName.startsWith(".")) parentName = parentName.substring(1);
        parentNameView.setText(parentName);

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("新建一个 Java Package")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String packageName = String.valueOf(packageNameView.getText());
                    if (StringUtils.isEmpty(packageName)) {
                        ToastUtils.showShort("请输入包名");
                        return;
                    }
                    File newJavaDir = new File(dir.getAbsolutePath() + "/" + packageName);
                    boolean b = FileUtils.createOrExistsDir(newJavaDir);
                    ToastUtils.showShort(newJavaDir.getName() + " 创建结果：" + b);

                    // 刷新文件状态
                    projectFragment.readProjectList();

                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 新建 Java Class 对话框
     */
    private boolean isCreateClass = true;

    @SuppressLint("InflateParams")
    private void showNewClassDialog(File dir) {
        isCreateClass = true;

        String path = dir.getAbsolutePath();
        String packageName = path.substring(path.indexOf(Project.srcDirName) + Project.srcDirName.length());
        packageName = packageName.replace("/", ".");
        if (packageName.startsWith(".")) packageName = packageName.substring(1);

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_project_dialog_class, null);
        TextView packageNameView = view.findViewById(R.id.packageName);

        TextInputEditText classNameView = view.findViewById(R.id.className);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        CheckBox methodCheckBox = view.findViewById(R.id.methodCheckBox);
        LinearLayout methodLayout = view.findViewById(R.id.methodLayout);

        packageNameView.setText(packageName);
        if (StringUtils.isEmpty(packageName)) {
            ViewGroup parent = (ViewGroup) packageNameView.getParent();
            parent.setVisibility(View.GONE);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_class:
                    isCreateClass = true;
                    methodCheckBox.setChecked(true);
                    methodLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.radio_interface:
                    isCreateClass = false;
                    methodCheckBox.setChecked(false);
                    methodLayout.setVisibility(View.GONE);
                    break;
            }
        });

        String finalPackageName = packageName;
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("新建一个 Java Class")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String className = String.valueOf(classNameView.getText());
                    if (StringUtils.isEmpty(className)) {
                        ToastUtils.showShort("请输入类名");
                        return;
                    }
                    File newJavaFile = new File(dir.getAbsolutePath() + "/" + className + ".java");
                    if (isCreateClass) {
                        String classTemplate = Project.Template.getClassTemplate(finalPackageName, className, methodCheckBox.isChecked());
                        boolean b = FileIOUtils.writeFileFromString(newJavaFile, classTemplate);
                        ToastUtils.showShort(newJavaFile.getName() + " 创建结果：" + b);
                    } else {
                        String interfaceTemplate = Project.Template.getInterfaceTemplate(finalPackageName, className);
                        boolean b = FileIOUtils.writeFileFromString(newJavaFile, interfaceTemplate);
                        ToastUtils.showShort(newJavaFile.getName() + " 创建结果：" + b);
                    }

               /*     // 添加新的子节点
                    TreeNode newJavaFileNode = new TreeNode(newJavaFile).setViewHolder(new TreeViewAdapter(context, projectFragment));
                    treeNode.addChild(newJavaFileNode);*/
                    // 刷新文件状态
                    projectFragment.readProjectList();

                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }
}
