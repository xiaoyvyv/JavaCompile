package com.xiaoyv.java.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ScreenUtils;
import com.xiaoyv.java.R;

/**
 * 新长大助手 抽取基类 App
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
@SuppressLint("InflateParams,ClickableViewAccessibility")
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 主题模式
     */
    public BaseTheme baseTheme = BaseTheme.LIGHT;
    /**
     * 标题栏
     */
    public Toolbar toolbar;
    /**
     * 布局变化监听器
     */
    private View.OnLayoutChangeListener layoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
        hideNavBar();
    };
    /**
     * 加载中对话框
     */
    private ProgressDialog loadingDialog;

    /**
     * DecorView
     */
    private View decorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenUtils.setPortrait(this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        super.onCreate(savedInstanceState);
        // 最先安装主题
        initTheme();
        setContentView(getLayoutId());
        initView();
        initAnimate();
        initToolbar();
        initLoadingDialog();
        initData();
        initListener();

        // 隐藏虚拟按键
        decorView = getWindow().getDecorView();
        decorView.addOnLayoutChangeListener(layoutChangeListener);
        hideNavBar();
    }

    /**
     * 加载中对话框
     */
    private void initLoadingDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.dialog_loading));
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * @return 获取布局文件ID
     */
    protected abstract int getLayoutId();


    /**
     * 初始化主题
     */
    private void initTheme() {
        // 检测是否黑暗模式
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (mode == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.App_AppTheme_Dark);
            return;
        }
        // 设置正常主题
        switch (baseTheme) {
            case LIGHT:
                setTheme(R.style.App_AppTheme_Light);
                break;
            case DARK:
                setTheme(R.style.App_AppTheme_Dark);
                break;
        }
    }

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 载入数据
     */
    protected abstract void initData();

    /**
     * 设置监听器
     */
    public void initListener() {

    }

    /**
     * 界面动画
     */
    public void initAnimate() {

    }

    /**
     * 查找控件
     */
    public <VIEW extends View> VIEW findView(@IdRes int viewId) {
        return findViewById(viewId);
    }

    /**
     * 绑定 Toolbar
     */
    private void initToolbar() {
        // 查询绑定 Toolbar
        toolbar = findView(getToolbarId());
        if (toolbar != null && isToolbarCanBack()) {
            toolbar.setNavigationIcon(R.drawable.toolbar_back);
            // 返回键
            toolbar.setNavigationOnClickListener(view ->
                    onBackPressed());
        }
    }

    private boolean isToolbarCanBack() {
        return true;
    }

    public int getToolbarId() {
        return R.id.toolbar;
    }


    /**
     * 显示加载中
     */
    public void showLoading() {
        loadingDialog.show();
    }

    public void showLoading(String msg) {
        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    /**
     * 隐藏加载中
     */
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    /**
     * 隐藏导航栏
     */
    public void hideNavBar() {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
        );
    }

    @Override
    protected void onDestroy() {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        decorView.removeOnLayoutChangeListener(layoutChangeListener);
        super.onDestroy();
    }
}
