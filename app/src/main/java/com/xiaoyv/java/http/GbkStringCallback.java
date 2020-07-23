package com.xiaoyv.java.http;

import com.lzy.okgo.callback.AbsCallback;

import java.nio.charset.Charset;

import okhttp3.ResponseBody;

/**
 * @author 王怀玉
 * @since 2020/5/17
 */
public abstract class GbkStringCallback extends AbsCallback<String> {

    @Override
    public String convertResponse(okhttp3.Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) return null;
        byte[] bytes = body.bytes();
        response.close();
        return new String(bytes, Charset.forName("GB2312"));
    }
}
