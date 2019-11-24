package com.lib.x5web;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xiaoyv.http.callback.OnFileChooser;
import com.xiaoyv.java.R;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2018/4/9.
 *
 * @author 王怀玉
 * @explain X5WebView
 */

public class X5WebView extends WebView {
    private Context context;
    private Toolbar toolbar;
    private WebViewProgressBar progressBar;
    private X5LoadFinishListener x5LoadFinishListener;
    private OnFileChooser onFileChooser;

    private AlertDialog dialog;
    private OnProgressChangeListener onProgressChangeListener;
    private String referer;

    public X5WebView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.context = context;
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
        this.context = context;
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i, Map<String, Object> map, boolean b) {
        super(context, attributeSet, i, map, b);
        this.context = context;
        init();
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void init() {
        this.getView().setClickable(true);
        initWebView();
        initWebViewSettings();

    }


    public void setTitleAndProgressBar(Toolbar toolbar, WebViewProgressBar progressBar) {
        this.toolbar = toolbar;
        this.progressBar = progressBar;
    }

    public void setX5LoadFinishListener(X5LoadFinishListener x5LoadFinishListener) {
        this.x5LoadFinishListener = x5LoadFinishListener;
    }

    private void initWebView() {
        this.setWebViewClient(mWebViewClient);
        this.setWebChromeClient(mWebChromeClient);

        setDownloadListener(new X5DownloadListener(context));
    }

    //客户端设置
    public WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String Url) {
            //return false 历史记录不会保存重定向的网页
            LogUtils.i("加载网页" + Url);
            if (!Url.startsWith("http")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                boolean isInstall = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
                if (isInstall) {
                    ToastUtils.showShort(R.string.open_app);
                    context.startActivity(intent);
                    return false;
                } else {
                    ToastUtils.showShort(R.string.no_open_app);
                    return true;
                }
            }
            //微信H5支付核心代码
            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Referer", StringUtils.isEmpty(referer) ? getUrl() : referer);
            view.loadUrl(Url, extraHeaders);

            return super.shouldOverrideUrlLoading(view, Url);
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            if (x5LoadFinishListener != null)
                x5LoadFinishListener.onLoadFinish(webView, progressBar, url);
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            return super.shouldInterceptRequest(webView, webResourceRequest);
        }
    };

    //辅助类
    WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView webView, String title) {
            if (StringUtils.isEmpty(title)) {
                title = context.getString(R.string.web_details);
            }
            setToolBarTitle(title);
        }

        @Override
        public void onReceivedIcon(WebView webView, Bitmap bitmap) {
            super.onReceivedIcon(webView, bitmap);
        }

        @Override
        public void onProgressChanged(WebView webView, int progress) {
            setProgress(progress);
            if (onProgressChangeListener != null)
                onProgressChangeListener.onProgressChanged(webView, progress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            if (onFileChooser != null)
                onFileChooser.onShowFileChooser(webView, valueCallback, fileChooserParams);
            else valueCallback.onReceiveValue(null);
            return true;
        }
    };

    public OnProgressChangeListener getOnProgressChangeListener() {
        return onProgressChangeListener;
    }

    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    public OnFileChooser getOnFileChooser() {
        return onFileChooser;
    }

    public void setOnFileChooser(OnFileChooser onFileChooser) {
        this.onFileChooser = onFileChooser;
    }

    private void setProgress(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }

    private void setToolBarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);

        webSetting.setSupportMultipleWindows(false);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);


        // 设置自适应屏幕，两者合用
        webSetting.setUseWideViewPort(true); //将图片调整到适合WebView的大小
        webSetting.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        // 缩放操作
        webSetting.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSetting.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSetting.setDisplayZoomControls(false); //隐藏原生的缩放控件


        // 其他细节操作
        webSetting.setAllowFileAccess(true); //设置可以访问文件
        webSetting.setAllowFileAccessFromFileURLs(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSetting.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSetting.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSetting.setGeolocationEnabled(true);
        webSetting.setBlockNetworkImage(false);
        webSetting.setSavePassword(true);
        webSetting.setSaveFormData(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCacheEnabled(true);
        //设置  Application Caches 缓存目录
        FileUtils.createOrExistsDir(PathUtils.getExternalStoragePath() + "/A_Tool/WebCache/");
        webSetting.setAppCachePath(PathUtils.getExternalStoragePath() + "A_Tool/WebCache/");
        webSetting.setDatabaseEnabled(true);
        webSetting.setDatabasePath(PathUtils.getExternalStoragePath() + "A_Tool/WebCache/");
        webSetting.setDomStorageEnabled(true);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        if (NetworkUtils.isConnected()) {
            webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            ToastUtils.showShort(R.string.no_internet);
            webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }

        //设置混合协议
        webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

    }
}
