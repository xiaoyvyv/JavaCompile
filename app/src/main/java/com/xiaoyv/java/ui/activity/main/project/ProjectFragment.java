package com.xiaoyv.java.ui.activity.main.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.lib.utils.MyUtils;
import com.lib.utils.ShareUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.xiaoyv.java.R;
import com.xiaoyv.java.base.BaseFragment;
import com.xiaoyv.java.mode.JavaProject;
import com.xiaoyv.java.mode.JavaTemplate;

import java.io.File;
import java.util.List;


/**
 * 项目页面
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
@SuppressLint("InflateParams")
public class ProjectFragment extends BaseFragment<ProjectContract.Presenter> implements ProjectContract.View, MenuItem.OnMenuItemClickListener {
    private static final int CHOOSE_JAR = 777;
    private static final int CHOOSE_ZIP = 888;
    private LinearLayout projectContainer;
    private AppCompatTextView projectNotice;
    private SwipeRefreshLayout projectRefresh;
    private RecyclerView projectRecycler;
    private ProjectAdapter projectAdapter;
    private JavaProject javaProject;
    private String saveState;
    private AndroidTreeView projectTreeView;

    public static ProjectFragment newInstance() {
        return new ProjectFragment();
    }

    @Override
    public void setPresenter(ProjectContract.Presenter presenter) {
        super.setFragmentPresenter(presenter);
    }

    @Override
    public void showError(String failMsg) {
        SnackbarUtils.with(rootView)
                .setMessage(failMsg)
                .setMessageColor(Color.WHITE)
                .showError();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_project;
    }

    @Override
    public void initView() {
        projectContainer = findView(R.id.project_container);
        projectNotice = findView(R.id.project_notice);
        projectRefresh = findView(R.id.project_refresh);
        projectRecycler = findView(R.id.project_recycler);
    }

    @Override
    public void initData() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        projectAdapter = new ProjectAdapter(R.layout.fragment_project_item);
        projectAdapter.bindToRecyclerView(projectRecycler);
        projectRecycler.setAdapter(projectAdapter);

        projectRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

        projectTreeView = new AndroidTreeView(activity, TreeNode.root());
    }

    @Override
    public void initListener() {
        toolbar.getMenu().add(0, 1, 0, R.string.project_menu_new)
                .setOnMenuItemClickListener(this)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        toolbar.getMenu().add(0, 2, 0, R.string.project_menu_import)
                .setOnMenuItemClickListener(this)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        toolbar.getMenu().add(0, 3, 0, R.string.project_menu_clean)
                .setOnMenuItemClickListener(this)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        projectAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            String projectName = projectAdapter.getData().get(position).getName();
            javaProject = JavaProject.openProject(projectName);
            // 设置当前打开项目
            toolbar.setTitle(getString(R.string.project_now) + javaProject.getProjectName());
            toolbar.setSubtitle(getString(R.string.project_code_file_size) + javaProject.getSrcFileSize());

            // 打开项目
            if (view.getId() == R.id.project_item_layout) {
                projectContainer.setVisibility(View.VISIBLE);
                projectRecycler.setVisibility(View.GONE);

                saveState = projectTreeView.getSaveState();
                presenter.loadProjectData(javaProject, TreeNode.root());
            }

            // 删除
            if (view.getId() == R.id.project_item_delete) {
                SnackbarUtils.with(rootView)
                        .setMessage(String.format(getString(R.string.project_is_delete), projectName))
                        .setMessageColor(Color.WHITE)
                        .setAction(getString(R.string.dialog_done), v -> {
                            javaProject.delete();

                            // 清除标题数据
                            javaProject = null;
                            toolbar.setTitle(R.string.app_name);
                            toolbar.setSubtitle(null);

                            presenter.loadProjectList();
                        })
                        .showWarning();
            }

            // 导出
            if (view.getId() == R.id.project_item_export) {
                activity.showLoading();
                File exportFile = javaProject.export();
                activity.hideLoading();
                ShareUtils.shareFile(activity, exportFile);
            }
        });

        // 刷新操作
        projectRefresh.setOnRefreshListener(() -> {
            presenter.loadProjectList();
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                // 关闭项目
                projectContainer.setVisibility(View.GONE);
                projectRecycler.setVisibility(View.VISIBLE);

                // 清除标题数据
                javaProject = null;
                toolbar.setTitle(R.string.app_name);
                toolbar.setSubtitle(null);
                // 重新加载项目条目
                presenter.loadProjectList();

                //新建项目
                showCreateJavaProject();
                return true;
            case 2:
                //TODO 仅支持导入该软件导出的zip格式源码包
                MyUtils.getAlert(activity, "仅支持导入该软件导出的zip格式源码包", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/zip");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, ProjectFragment.CHOOSE_ZIP);
                }).show();
                return true;
            case 3:
                if (ObjectUtils.isEmpty(javaProject)) {
                    showError(getString(R.string.project_no_open));
                    return true;
                }
                // 清理项目
                javaProject.clean();
                ToastUtils.showShort(javaProject.getProjectName() + getString(R.string.project_clean_success));
                // 刷新树结构
                saveState = projectTreeView.getSaveState();
                presenter.loadProjectData(javaProject, TreeNode.root());
                return true;
        }
        return false;
    }

    @Override
    public void showProjectList(List<File> projectList) {
        projectRefresh.setRefreshing(false);
        projectAdapter.setNewData(projectList);
        if (ObjectUtils.isEmpty(projectList)) {
            projectAdapter.setEmptyView(activity, getString(R.string.project_no_data));
        }
    }

    @Override
    public void showProjectData(TreeNode rootNode) {
        projectRefresh.setRefreshing(false);
        projectTreeView = new AndroidTreeView(activity, rootNode);
        projectTreeView.setDefaultViewHolder(ProjectTreeHolder.class);
        projectTreeView.restoreState(saveState);
        projectContainer.removeAllViews();
        projectContainer.addView(projectTreeView.getView());
    }

    @Override
    public void openFile(File file) {
        ToastUtils.showShort(file.toString());
    }

    @Override
    public void runDex(File file) {
        ToastUtils.showShort(file.toString());
    }

    @Override
    public void showDeleteFile(File fileOrDir) {
        SnackbarUtils.with(rootView)
                .setMessage(getString(R.string.project_is_delete_item))
                .setMessageColor(Color.WHITE)
                .setAction(getString(R.string.dialog_done), v -> {
                    if (FileUtils.isDir(fileOrDir)) {
                        FileUtils.deleteAllInDir(fileOrDir);
                    }
                    FileUtils.delete(fileOrDir);
                    saveState = projectTreeView.getSaveState();
                    presenter.loadProjectData(javaProject, TreeNode.root());
                })
                .showWarning();
    }

    @Override
    public void showOperateMenu(AppCompatImageView moreView, File dir) {
        String filePath = dir.getAbsolutePath();
        PopupMenu popupMenu = new PopupMenu(activity, moreView);
        // lib文件夹菜单
        if (StringUtils.equals(filePath, javaProject.getLibDirPath())) {
            popupMenu.getMenu().add("导入 Maven 依赖").setOnMenuItemClickListener(item -> {
                //showNewProjectDialog();
                return true;
            });
            popupMenu.getMenu().add("本地 Jar 依赖").setOnMenuItemClickListener(item -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, ProjectFragment.CHOOSE_JAR);
                return true;
            });
        }
        // src 文件夹及其子文件夹菜单
        else if (filePath.contains(javaProject.getSrcDirPath())) {
            popupMenu.getMenu().add("新建 Java Package").setOnMenuItemClickListener(item -> {
                showCreatePackageDialog(dir);
                return true;
            });
            popupMenu.getMenu().add("新建 Java Class").setOnMenuItemClickListener(item -> {
                showCreateClassDialog(dir);
                return true;
            });
        }
        // 工程文件夹
        else if (StringUtils.equals(filePath, javaProject.getProjectDirPath())) {
            popupMenu.getMenu().add("关闭工程").setOnMenuItemClickListener(item -> {
                // 关闭项目
                projectContainer.setVisibility(View.GONE);
                projectRecycler.setVisibility(View.VISIBLE);

                // 清除标题数据
                javaProject = null;
                toolbar.setTitle(R.string.app_name);
                toolbar.setSubtitle(null);
                // 重新加载项目条目
                presenter.loadProjectList();
                return true;
            });
        }
        popupMenu.show();
    }

    @Override
    public void showCreateJavaProject() {
        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_project_dialog_create, null);
        TextInputEditText projectNameView = view.findViewById(R.id.project_dialog_name);

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setNegativeButton(R.string.dialog_clear, null)
                .setPositiveButton(R.string.dialog_done, (dialog, which) -> {
                    String javaProjectName = String.valueOf(projectNameView.getText());
                    if (StringUtils.isEmpty(javaProjectName)) {
                        showError(getString(R.string.project_cant_null));
                        return;
                    }
                    KeyboardUtils.hideSoftInput(projectNameView);
                    // 创建项目
                    javaProject = JavaProject.newProject(javaProjectName);
                    // 载入项目列表
                    presenter.loadProjectList();
                })
                .create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void showCreatePackageDialog(File dir) {
        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_project_dialog_package, null);
        TextInputEditText packageNameView = view.findViewById(R.id.project_package_name);
        TextView parentNameView = view.findViewById(R.id.project_parent_name);

        // 获取包名
        String path = dir.getAbsolutePath();
        String parentName = path.substring(path.indexOf(JavaProject.srcDirName) + JavaProject.srcDirName.length());
        parentName = parentName.replace("/", ".");
        if (parentName.startsWith(".")) parentName = parentName.substring(1);
        parentNameView.setText(parentName);

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setNegativeButton(R.string.dialog_clear, null)
                .setPositiveButton(R.string.dialog_done, (dialog, which) -> {
                    String packageName = String.valueOf(packageNameView.getText());
                    if (StringUtils.isEmpty(packageName)) {
                        showError(getString(R.string.project_input_package));
                        return;
                    }
                    KeyboardUtils.hideSoftInput(packageNameView);

                    File newJavaDir = new File(dir.getAbsolutePath() + "/" + packageName);
                    FileUtils.createOrExistsDir(newJavaDir);

                    // 刷新文件状态
                    saveState = projectTreeView.getSaveState();
                    presenter.loadProjectData(javaProject, TreeNode.root());
                })
                .create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void showCreateClassDialog(File dir) {
        String path = dir.getAbsolutePath();
        String packageName = path.substring(path.indexOf(JavaProject.srcDirName) + JavaProject.srcDirName.length());
        packageName = packageName.replace("/", ".");
        if (packageName.startsWith(".")) packageName = packageName.substring(1);

        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_project_dialog_class, null);
        TextView packageNameView = view.findViewById(R.id.project_package_name);

        TextInputEditText classNameView = view.findViewById(R.id.project_class_name);
        RadioGroup radioGroup = view.findViewById(R.id.project_radio_group);
        CheckBox methodCheckBox = view.findViewById(R.id.project_method_check);
        LinearLayout methodLayout = view.findViewById(R.id.project_method);

        packageNameView.setText(packageName);
        if (StringUtils.isEmpty(packageName)) {
            ViewGroup parent = (ViewGroup) packageNameView.getParent();
            parent.setVisibility(View.GONE);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.project_radio_class:
                    methodCheckBox.setChecked(true);
                    methodLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.project_radio_interface:
                    methodCheckBox.setChecked(false);
                    methodLayout.setVisibility(View.GONE);
                    break;
            }
        });

        String finalPackageName = packageName;
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setNegativeButton(R.string.dialog_clear, null)
                .setPositiveButton(R.string.dialog_done, (dialog, which) -> {
                    String className = String.valueOf(classNameView.getText());
                    if (StringUtils.isEmpty(className)) {
                        ToastUtils.showShort("请输入类名");
                        return;
                    }
                    KeyboardUtils.hideSoftInput(classNameView);

                    File newJavaFile = new File(dir.getAbsolutePath() + "/" + className + ".java");
                    if (methodCheckBox.isChecked()) {
                        String classTemplate = JavaTemplate.getClassTemplate(finalPackageName, className, methodCheckBox.isChecked());
                        boolean b = FileIOUtils.writeFileFromString(newJavaFile, classTemplate);
                        ToastUtils.showShort(newJavaFile.getName() + " 创建结果：" + b);
                    } else {
                        String interfaceTemplate = JavaTemplate.getInterfaceTemplate(finalPackageName, className);
                        boolean b = FileIOUtils.writeFileFromString(newJavaFile, interfaceTemplate);
                        ToastUtils.showShort(newJavaFile.getName() + " 创建结果：" + b);
                    }

                    // 刷新文件状态
                    saveState = projectTreeView.getSaveState();
                    presenter.loadProjectData(javaProject, TreeNode.root());
                })
                .create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

}