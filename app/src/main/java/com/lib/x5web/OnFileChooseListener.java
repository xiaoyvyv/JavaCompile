package com.lib.x5web;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


/**
 * 网页文件选择
 *
 * @author 王怀玉
 * @since 2020/5/18
 */
public interface OnFileChooseListener {
    void onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams);
}
