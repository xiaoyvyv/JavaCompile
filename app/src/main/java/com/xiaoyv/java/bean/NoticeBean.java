package com.xiaoyv.java.bean;

public class NoticeBean {
    /**
     * message : 部分依赖可能会编译错误，可尝试切换依赖的版本
     * url :
     */

    private String message;
    private String url;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
