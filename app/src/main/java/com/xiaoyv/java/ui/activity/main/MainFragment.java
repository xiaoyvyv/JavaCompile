package com.xiaoyv.java.ui.activity.main;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.FragmentUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationView;
import com.xiaoyv.java.R;
import com.xiaoyv.java.base.BaseFragment;
import com.xiaoyv.java.ui.activity.main.editor.EditorFragment;
import com.xiaoyv.java.ui.activity.main.editor.EditorPresenter;
import com.xiaoyv.java.ui.activity.main.function.FunctionFragment;
import com.xiaoyv.java.ui.activity.main.function.FunctionPresenter;
import com.xiaoyv.java.ui.activity.main.project.ProjectFragment;
import com.xiaoyv.java.ui.activity.main.project.ProjectPresenter;


/**
 * 主页
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class MainFragment extends BaseFragment<MainContract.Presenter> implements MainContract.View, NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private FrameLayout mainContainer;
    private BottomNavigationView mainBottomNavView;
    public EditorFragment editorFragment;
    public FunctionFragment functionFragment;
    public ProjectFragment projectFragment;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        super.setFragmentPresenter(presenter);
    }

    @Override
    public void showError(String failMsg) {

    }

    @Override
    public boolean isShowLoadingViewOnStart() {
        return false;
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void initView() {
        mainContainer = findView(R.id.project_container);
        mainBottomNavView = findView(R.id.main_bottom_view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {
        //activity. getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        projectFragment = ProjectFragment.newInstance();
        editorFragment = EditorFragment.newInstance();
        functionFragment = FunctionFragment.newInstance();

        projectFragment.setUserVisibleHint(true);

        new ProjectPresenter(projectFragment, activity);
        new EditorPresenter(editorFragment);
        new FunctionPresenter(functionFragment);


        // 填充视图
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentUtils.add(manager, projectFragment, mainContainer.getId(), false);
        FragmentUtils.add(manager, editorFragment, mainContainer.getId(), true);
        FragmentUtils.add(manager, functionFragment, mainContainer.getId(), true);

        mainBottomNavView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
    }

    @Override
    public void initListener() {
        // 底部导航菜单点击事件
        mainBottomNavView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 底部导航菜单
        if (item.getItemId() == R.id.main_project) {
            projectFragment.setUserVisibleHint(true);
            FragmentUtils.showHide(projectFragment, editorFragment, functionFragment);
            return true;
        } else if (item.getItemId() == R.id.main_editor) {
            editorFragment.setUserVisibleHint(true);
            FragmentUtils.showHide(editorFragment, projectFragment, functionFragment);
            return true;
        } else if (item.getItemId() == R.id.main_document) {
            functionFragment.setUserVisibleHint(true);
            FragmentUtils.showHide(functionFragment, projectFragment, editorFragment);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        int selectedId = mainBottomNavView.getSelectedItemId();
        if (selectedId != R.id.main_project) {
            mainBottomNavView.setSelectedItemId(R.id.main_project);
            return true;
        }
        return presenter.onBackPressed();
    }

}