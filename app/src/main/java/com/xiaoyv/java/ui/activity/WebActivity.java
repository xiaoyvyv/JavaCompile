package com.xiaoyv.java.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.lib.utils.ClipboardUtils;
import com.lib.utils.MyUtils;
import com.lib.x5web.WebViewProgressBar;
import com.lib.x5web.X5JavaScriptFunction;
import com.lib.x5web.X5WebView;
import com.tencent.smtt.sdk.ValueCallback;
import com.xiaoyv.java.R;
import com.xiaoyv.java.ui.activity.base.BaseActivity;
import com.xiaoyv.java.url.Url;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WebActivity extends BaseActivity {
    private static final int FILE_CHOOSER_RESULT_CODE = 5520;
    private X5WebView mWebView;
    private Toolbar toolbar;
    private WebViewProgressBar progress;

    private String from_url;

    private String referer = "";

    private FrameLayout web_container;
    private boolean isNoTitle;
    private String base_html;
    private String base_url;
    private String base_title;
    private ValueCallback<Uri[]> valueCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow().setFlags(
                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception ignored) {
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        base_title = getIntent().getStringExtra("base_title");
        base_url = getIntent().getStringExtra("base_url");
        base_html = getIntent().getStringExtra("base_html");
        from_url = getIntent().getStringExtra("from_url");
        referer = getIntent().getStringExtra("referer");
        isNoTitle = getIntent().getBooleanExtra("isNoTitle", false);

        super.onCreate(savedInstanceState);
        if (isNoTitle) {
            setTheme(R.style.AppTheme_NoStateBar);
            ScreenUtils.setFullScreen(this);
        }

        setContentView(R.layout.activity_web);
        init();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v ->
                WebActivity.this.finish());


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.e("setting_header", "----------横屏------------");
            toolbar.setVisibility(View.GONE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LogUtils.e("setting_header", "----------竖屏------------");
            if (isNoTitle) {
                toolbar.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mWebView.getSettings().setJavaScriptEnabled(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.getSettings().setJavaScriptEnabled(true);
    }


    @Override
    public void findViews() {
        mWebView = new X5WebView(this);
        toolbar = findViewById(R.id.toolbar);
        progress = findViewById(R.id.web_progress);
        web_container = findViewById(R.id.web_container);

    }

    @SuppressLint({"SetJavaScriptEnabled", "RestrictedApi"})
    @Override
    public void setEvents() {
        if (isNoTitle) {
            toolbar.setVisibility(View.GONE);
        }


        web_container.addView(mWebView, 0, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        progress.setProgressColor(Color.GREEN);
        mWebView.setTitleAndProgressBar(toolbar, progress);

        mWebView.addJavascriptInterface(new X5JavaScriptFunction(WebActivity.this), "android");

        mWebView.setOnFileChooser((webView, valueCallback, fileChooserParams) -> {
            WebActivity.this.valueCallback = valueCallback;
            String[] acceptTypes = fileChooserParams.getAcceptTypes();
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);

            if (ObjectUtils.isNotEmpty(acceptTypes)) {
                i.setType(fileChooserParams.getAcceptTypes()[0]);
            } else {
                i.setType("*/*");
            }
            startActivityForResult(Intent.createChooser(i, "长大助手-文件选择"), FILE_CHOOSER_RESULT_CODE);
        });

        if (base_html == null) {
            if (StringUtils.isEmpty(referer)) {
                // 普通加载
                mWebView.loadUrl(from_url);
            } else {
                // 设置Referer加载
                LogUtils.i("referer:" + referer);
                Map<String, String> extraHeaders = new HashMap<>();
                extraHeaders.put("Referer", referer);
                mWebView.loadUrl(from_url, extraHeaders);
            }
        } else {
            // 加载HTML源码
            if (base_title != null)
                toolbar.setTitle(base_title);
            if (base_html != null)
                mWebView.loadDataWithBaseURL(base_url, base_html, "text/html", "utf-8", null);
            LogUtils.e("加载基础代码", base_html);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.refresh:
                if (mWebView.getUrl().contains("about:blank"))
                    ToastUtils.showShort(R.string.cant_refresh);
                else
                    mWebView.reload();
                return true;
            case R.id.share:
                MyUtils.shareText(WebActivity.this,
                        mWebView.getTitle() + "\n"
                                + mWebView.getUrl() + "\n\n数据来自："
                                + Url.App_Download);
                break;
            case R.id.copy:
                if (mWebView.getUrl().contains("asset") || mWebView.getUrl().contains("about:blank")) {
                    ToastUtils.showShort(R.string.cant_copy);
                } else {
                    ToastUtils.showShort(R.string.copy_right);
                    ClipboardUtils.copyText(mWebView.getUrl());
                }
                return true;
            case R.id.open:
                if (mWebView.getUrl().contains("asset") || mWebView.getUrl().contains("about:blank")) {
                    ToastUtils.showShort(R.string.cant_open);
                } else {
                    MyUtils.openBrowser(WebActivity.this, mWebView.getUrl());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            Exit();
        }
    }


    private boolean isExit = false;

    @SuppressLint("HandlerLeak")
    private void Exit() {
        if (!isExit) {
            isExit = true;
            ToastUtils.showShort(R.string.double_close_web);
            Utils.runOnUiThreadDelayed(() -> isExit = false, 2000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ObjectUtils.isEmpty(data)) {
            if (valueCallback != null)
                valueCallback.onReceiveValue(null);
            return;
        }
        Uri uri = data.getData();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FILE_CHOOSER_RESULT_CODE) {
                valueCallback.onReceiveValue(new Uri[]{uri});
            } else {
                if (valueCallback != null)
                    valueCallback.onReceiveValue(null);
            }
        } else {
            if (valueCallback != null)
                valueCallback.onReceiveValue(null);
        }
    }
}
