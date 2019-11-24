package com.xiaoyv.http;

import com.blankj.utilcode.util.Utils;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.xiaoyv.http.callback.StringCallBack;
import com.xiaoyv.http.cookie.HttpPersistentCookieJar;
import com.xiaoyv.http.interceptor.RequestCacheInterceptor;
import com.xiaoyv.http.interceptor.ResponseCacheInterceptor;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class OkHttp {
    private static volatile OkHttpClient okHttpClient;
    private static volatile HttpPersistentCookieJar cookieJar;

    public OkHttp() {

    }

    public static OkHttpClient getInstance() {

        if (cookieJar == null) {
            synchronized (HttpPersistentCookieJar.class) {
                if (cookieJar == null) {
                    CookieCache cache = new SetCookieCache();
                    CookiePersistor persist = new SharedPrefsCookiePersistor(Utils.getApp().getApplicationContext());
                    cookieJar = new HttpPersistentCookieJar(cache, persist);
                }
            }
        }

        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .proxy(Proxy.NO_PROXY)
                            .addInterceptor(new RequestCacheInterceptor())
                            .addNetworkInterceptor(new ResponseCacheInterceptor())
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(5 * 60, TimeUnit.SECONDS)
                            .cookieJar(cookieJar)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    public static void do_Get(final String url, final OnResultStringListener onResultStringListener) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        do_Post(request, onResultStringListener);
    }

    public static void do_Post(final Request request, final OnResultStringListener onResultStringListener) {
        Call call = OkHttp.getInstance().newCall(request);
        StringCallBack stringCallBack = new StringCallBack() {
            @Override
            public void onFinish(Call call, String response, boolean isResponseExist, boolean isCacheResponse) {
                if (isResponseExist) {
                    if (onResultStringListener != null)
                        onResultStringListener.onResponse(response);
                } else {
                    if (onResultStringListener != null)
                        onResultStringListener.onFailure(response);
                }
            }
        };
        call.enqueue(stringCallBack);
    }

    public static Request getRequest(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    public static Request getRequest(String url, FormBody formBody) {
        return new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
    }

    //Cookie管理
    public static PersistentCookieJar cookieJar() {
        return cookieJar;
    }
}
