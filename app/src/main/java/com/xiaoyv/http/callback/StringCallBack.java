package com.xiaoyv.http.callback;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.xiaoyv.http.cacahe.CacheUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class StringCallBack implements Callback {
    private Handler UI = new Handler(Looper.getMainLooper());

    /**
     * 有网加载成功 onFinish 返回网络响应
     * 有网加载失败或没网情况下，本地有缓存 onFinish 返回缓存响应
     * 有网加载失败或没网情况下，本地无缓存 onFinish 返回错误信息
     *
     * @param call            请求
     * @param response        响应
     * @param isResponseExist 响应是否存在
     * @param isCacheResponse 响应存在的话，是否是缓存响应
     */
    public abstract void onFinish(Call call, String response, boolean isResponseExist, boolean isCacheResponse);

    @Override
    public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
        e.printStackTrace();
        UI.post(() -> {
            String stringCache = CacheUtils.getStringCache(call);

            if (stringCache == null) {
                onFinish(call, e.getMessage(), false, false);
                /*LogUtils.v("请求链接：" + call.request().url(),
                        "执行操作：OkHttp缓存查询操作",
                        "执行原因：" + e.getMessage(),
                        "执行结果：没有缓存-回调错误信息");*/
            } else {
                onFinish(call, stringCache, true, true);
                /*LogUtils.v("请求链接：" + call.request().url(),
                        "执行操作：OkHttp缓存查询操作",
                        "执行原因：" + e.getMessage(),
                        "执行结果：存在缓存-回调缓存响应");*/
            }
        });
    }

    @Override
    public void onResponse(@NonNull final Call call, @NonNull Response response) {
        try {
            if (!response.isSuccessful()) {
                onFailure(call, new IOException("code:" + response.code() + ",message:" + response.message()));
            } else {
                ResponseBody body = response.body();
                if (body == null) {
                    onFailure(call, new IOException("The response'body is null"));
                } else {
                    //请求结果
                    final String string = body.string();
                    //保存
                    UI.post(() -> {
                        /*LogUtils.v(call.request().url(), "OkHttp网络请求操作结果：存在响应-回调网络响应");*/
                        onFinish(call, string, true, false);
                    });
                    if (string.contains("请不要过快点击")) return;
                    if (string.contains("密码错误")) return;
                    if (string.contains("账户不存在")) return;
                    if (string.contains("验证码不正确")) return;
                    if (string.contains("重复登录")) return;
                    if (string.contains("用户名")) return;

                    new Thread(() -> CacheUtils.put(call, string)).start();
                }
            }
        } catch (IOException e) {
            onFailure(call, e);
        }
    }
}














