package com.xiaoyv.java.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.lib.utils.MyUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.xiaoyv.http.OkHttp;
import com.xiaoyv.http.OnResultStringListener;
import com.xiaoyv.java.R;
import com.xiaoyv.java.bean.NoticeBean;
import com.xiaoyv.java.mode.Project;
import com.xiaoyv.java.ui.activity.MainActivity;
import com.xiaoyv.java.ui.activity.base.BaseFragment;
import com.xiaoyv.java.ui.adapter.TreeViewAdapter;
import com.xiaoyv.java.url.Url;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectFragment extends BaseFragment {
    public static final int CHOOSE_ZIP = 321;
    public MainActivity activity;
    private LinearLayout container;
    private HashMap<String, String> saveStates = new HashMap<>();
    private HashMap<String, AndroidTreeView> treeViewHashMap = new HashMap<>();
    private Toolbar toolbar;
    private TextView notice;

    @Override
    public int setContentView() {
        return R.layout.fragment_project;
    }

    @Override
    public void findViews() {
        container = findViewById(R.id.container);
        toolbar = findViewById(R.id.toolbar);
        notice = findViewById(R.id.notice);
    }

    @Override
    public void setEvents() {
        activity = (MainActivity) getActivity();

        Menu menu = toolbar.getMenu();
        menu.add("刷新")
                .setOnMenuItemClickListener(item -> {
                    readProjectList();
                    ToastUtils.showShort("刷新成功");
                    return false;
                }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("新建")
                .setOnMenuItemClickListener(item -> {
                    showNewProjectDialog();
                    return false;
                }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("导入")
                .setOnMenuItemClickListener(item -> {
                    MyUtils.getAlert(activity, "仅支持导入该软件导出的zip格式源码包", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("file/*");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        ProjectFragment.this.startActivityForResult(intent, ProjectFragment.CHOOSE_ZIP);
                    }).show();
                    return false;
                }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("收拢所有")
                .setOnMenuItemClickListener(item -> {
                    // 保存AndroidTreeView展开状态
                    for (Map.Entry<String, AndroidTreeView> entry : treeViewHashMap.entrySet()) {
                        AndroidTreeView value = entry.getValue();
                        value.collapseAll();
                    }
                    readProjectList();
                    return false;
                });
        menu.add("清洁项目")
                .setOnMenuItemClickListener(item -> {
                    if (StringUtils.isEmpty(Project.currentProjectName)) {
                        ToastUtils.showShort("未选择项目");
                        return false;
                    }
                    FileUtils.deleteAllInDir(Project.getCurrentBuildDirPath());
                    FileUtils.deleteAllInDir(Project.getCurrentBinDirPath());
                    ToastUtils.showShort(Project.currentProjectName + "：清理成功");
                    readProjectList();
                    return false;
                });

        // 加载通知
        OkHttp.do_Get(Url.App_Notice, new OnResultStringListener() {
            @Override
            public void onResponse(String response) {
                NoticeBean noticeBean = GsonUtils.fromJson(response, NoticeBean.class);
                notice.setText(noticeBean.getMessage());
                notice.requestFocus();
                notice.setOnClickListener(v -> MyUtils.openUrl(activity, noticeBean.getUrl()));
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    /**
     * 新建 Java Package 对话框
     */
    @SuppressLint("InflateParams")
    private void showNewProjectDialog() {
        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_project_dialog_project, null);
        TextInputEditText projectNameView = view.findViewById(R.id.projectName);

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("新建一个 Java Project")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String projectName = String.valueOf(projectNameView.getText());
                    if (StringUtils.isEmpty(projectName)) {
                        ToastUtils.showShort("请输入项目名");
                        return;
                    }
                    Project.createProject(projectName);
                    // 刷新文件状态
                    readProjectList();
                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    public void onResume() {
        super.onResume();
        readProjectList();
    }


    /**
     * 读取所有工程，并且历遍文件
     */
    public void readProjectList() {
        // 检测编辑器的文件是否还在
        if (activity.editorFragment.keys.size() != 0) {
            activity.editorFragment.checkFiles();
        }

        // 先保存TreeView之前展开状态
        saveState();

        String rootPtah = Project.rootPtah;
        FileUtils.createOrExistsDir(rootPtah);
        // 历遍存在的项目
        List<File> projects = FileUtils.listFilesInDirWithFilter(rootPtah, FileUtils::isDir);
        if (projects == null || ObjectUtils.isEmpty(projects)) {
            showNewProjectDialog();
            return;
        }

        sortByName(projects);
        container.removeAllViews();
        for (File file : projects) {
            TreeNode rootNode = TreeNode.root();
            listFileNode(rootNode, file);
            AndroidTreeView treeView = new AndroidTreeView(activity, rootNode);
            treeViewHashMap.put(file.getName(), treeView);
            container.addView(treeView.getView());
            // 获取AndroidTreeView展开状态
            String state = saveStates.get(file.getName());
            if (!StringUtils.isEmpty(state)) {
                treeView.restoreState(state);
            }
        }
    }

    /**
     * 历遍文件节点
     */
    private void listFileNode(TreeNode rootNode, File file) {
        // 文件
        if (FileUtils.isFile(file)) {
            TreeNode fileNode = new TreeNode(file).setViewHolder(new TreeViewAdapter(activity, ProjectFragment.this));
            rootNode.addChild(fileNode);
            return;
        }

        // 文件夹节点，默认不展开
        TreeNode direNode = new TreeNode(file).setViewHolder(new TreeViewAdapter(activity, ProjectFragment.this));
        rootNode.addChild(direNode);


        // 排序
        List<File> fileList = FileUtils.listFilesInDir(file);
        sortByName(fileList);

        // 继续历遍
        for (File file1 : fileList) {
            listFileNode(direNode, file1);
        }
    }


    /**
     * 保存状态
     */
    private void saveState() {
        // 保存AndroidTreeView展开状态
        for (Map.Entry<String, AndroidTreeView> entry : treeViewHashMap.entrySet()) {
            String key = entry.getKey();
            AndroidTreeView value = entry.getValue();
            saveStates.put(key, value.getSaveState());
        }
    }

    /**
     * 文件按名字排序
     */
    private void sortByName(List<File> fileList) {
        Collections.sort(fileList, (o1, o2) -> {
            if (o1.isDirectory() && o2.isFile())
                return -1;
            if (o1.isFile() && o2.isDirectory())
                return 1;
            return o1.getName().compareTo(o2.getName());
        });
    }

    /**
     * 分析并设置当前工程
     */
    public void setCurrentProjectByFile(File projectFile) {
        String projectName = Project.getCurrentProjectNameByFile(projectFile);
        // 设置当前打开项目
        Project.setCurrentProject(new File(Project.rootPtah + "/" + projectName));
        toolbar.setTitle("当前工程：" + projectName);
    }

    /**
     * 在编辑器打开文件
     */
    public void setOpenFile(File file) {
        activity.editorFragment.setUserVisibleHint(true);
        activity.editorFragment.openFile(file);
        activity.bottomNavigationView.setSelectedItemId(R.id.main_editor);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == TreeViewAdapter.CHOOSE_JAR && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                File file = UriUtils.uri2File(uri);
                if (FileUtils.getFileName(file).endsWith(".jar")) {
                    boolean b = FileUtils.copyFile(file.getAbsolutePath(), Project.getCurrentLibDirPath() + "/" + file.getName());
                    ToastUtils.showShort(file.getName() + " 导入：" + b);
                    return;
                }
                ToastUtils.showShort("仅支持本地jar文件");
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == ProjectFragment.CHOOSE_ZIP && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                File file = UriUtils.uri2File(uri);
                if (FileUtils.getFileName(file).endsWith(".zip")) {
                    boolean fileExists = FileUtils.isFileExists(file);
                    if (!fileExists) {
                        return;
                    }
                    ThreadUtils.getCachedPool().execute(() -> {
                        try {
                            ZipUtils.unzipFile(file.getAbsolutePath(), Project.rootPtah);
                            ToastUtils.showShort("项目：" + file.getName() + " 导入成功");
                            readProjectList();
                        } catch (IOException e) {
                            e.printStackTrace();
                            ToastUtils.showShort("项目：" + file.getName() + " 导入失败");
                        }
                    });
                    return;
                }
                ToastUtils.showShort("仅支持zip文件");
            }
        }
    }
}
