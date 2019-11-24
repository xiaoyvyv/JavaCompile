package com.xiaoyv.http.interceptor;


import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

//响应头插值器
public final class ResponseCacheInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        return chain.proceed(chain.request()).newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .build();
    }
}

