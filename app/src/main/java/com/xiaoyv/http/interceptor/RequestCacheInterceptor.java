package com.xiaoyv.http.interceptor;


import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.StringUtils;
import com.lib.utils.MyUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//请求头插值器
public final class RequestCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //在原来的request的基础上修改
        Request request = chain.request();
        String url = request.url().toString();
        Request.Builder builder = request.newBuilder();


        builder.removeHeader("User-Agent");
        String userAgent = System.getProperty("http.agent");
        if (StringUtils.isEmpty(userAgent)) {
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/" + MyUtils.getRand(1000);
        }

        //修改UA
        builder.addHeader("User-Agent", userAgent);
        //builder.addHeader("Connection", "close");
        builder.addHeader("Accept", "*/*");
        builder.addHeader("Accept-Language", "zh-CN,en-US;q=0.8,en;q=0.6");
        builder.addHeader("Referer", url);
        builder.addHeader("Client", AppUtils.getAppPackageName() + ":" + DeviceUtils.getUniqueDeviceId());
        Request buildRequest = builder.build();
        return chain.proceed(buildRequest);
    }
}
