package com.xiaoyv.java.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lib.utils.MyUtils;
import com.xiaoyv.java.R;
import com.xiaoyv.java.ui.activity.base.BaseActivity;
import com.xiaoyv.java.ui.fragment.DocumentFragment;
import com.xiaoyv.java.ui.fragment.EditorFragment;
import com.xiaoyv.java.ui.fragment.ProjectFragment;


public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FrameLayout container;
    public BottomNavigationView bottomNavigationView;
    public EditorFragment editorFragment;
    public ProjectFragment projectFragment;
    public DocumentFragment documentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.init();
    }

    @Override
    public void findViews() {
        container = findViewById(R.id.container);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    @Override
    public void setEvents() {
        MyUtils.checkAppVersion(this);

        FragmentManager manager = getSupportFragmentManager();

        projectFragment = new ProjectFragment();
        projectFragment.setUserVisibleHint(true);
        editorFragment = new EditorFragment();
        documentFragment = new DocumentFragment();


        FragmentUtils.add(manager, projectFragment, container.getId(), false);
        FragmentUtils.add(manager, editorFragment, container.getId(), true);
        FragmentUtils.add(manager, documentFragment, container.getId(), true);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_project:
                projectFragment.setUserVisibleHint(true);
                projectFragment.readProjectList();
                FragmentUtils.showHide(projectFragment, editorFragment, documentFragment);
                return true;
            case R.id.main_editor:
                editorFragment.setUserVisibleHint(true);
                FragmentUtils.showHide(editorFragment, projectFragment, documentFragment);
                return true;
            case R.id.main_document:
                documentFragment.setUserVisibleHint(true);
                FragmentUtils.showHide(documentFragment, editorFragment, projectFragment);
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        switch (bottomNavigationView.getSelectedItemId()) {
            case R.id.main_project:
                onClick();
                return;
            case R.id.main_editor:
                bottomNavigationView.setSelectedItemId(R.id.main_project);
                return;
            case R.id.main_document:
                bottomNavigationView.setSelectedItemId(R.id.main_editor);
                return;
        }
        super.onBackPressed();
    }


    private boolean isExit = false;

    private void onClick() {
        // 自动保存
        editorFragment.saveWhenExit();

        if (!isExit) {
            isExit = true;
            ToastUtils.showShort(getString(R.string.double_exit));
            Utils.runOnUiThreadDelayed(() -> isExit = false, 2000);
        } else {
            // moveTaskToBack(true);
            ActivityUtils.finishAllActivities(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            AppUtils.exitApp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
