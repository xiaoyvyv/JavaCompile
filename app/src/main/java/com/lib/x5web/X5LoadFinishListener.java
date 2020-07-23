package com.lib.x5web;


import android.webkit.WebView;

/**
 * Created by Administrator on 2018/4/9.
 *
 * @author 王怀玉
 * @explain X5LoadFinishListener
 */

public interface X5LoadFinishListener {
    void onLoadFinish(WebView webView, WebViewProgressBar progressBar, String s);
}