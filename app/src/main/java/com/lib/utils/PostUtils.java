package com.lib.utils;

import com.blankj.utilcode.util.LogUtils;
import com.xiaoyv.java.url.Url;

import okhttp3.FormBody;
import okhttp3.Request;


/**
 * 发送邮件工具类
 */
public class PostUtils {

    public static void sendMessage(String qq, String text) {
        sendMessage(qq, "", text);
    }

    public static void sendMessage(String qq, String title, String text) {
        String url = Url.App_Send_Email + "?address=" + qq;
        FormBody formBody = new FormBody.Builder()
                .add("to_address", qq)
                .add("title", title)
                .add("html", text)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        //TODO 发送邮件
    }

    public static void sendBug(String crashCause, String crashMessage) {
        String text = "<body>" +
                "<h3>Java编译器崩溃日志</h3 align=\"center\">" +
                "<h4>原因</h4>" +
                "<h5><font color=\"red\">" + crashCause + "</font></h5>" +
                "<h4>信息</h4>" +
                "<h5>" + crashMessage + "</h5>" +
                "</body>";

        FormBody formBody = new FormBody.Builder()
                .add("html", text)
                .add("to_address", "1223414335@qq.com")
                .build();

        Request request = new Request.Builder()
                .url(Url.App_Send_Email)
                .post(formBody)
                .build();

        //TODO 发送邮件
    }
}
