package com.xiaoyv.java.bean;

public class UpDateBean {
    /**
     * version : 2.9
     * title : 发现新版本可用
     * message : *修复18级同学查看课表出现未登录情况
     * 修改课表颜色
     * <p>
     * 若升级出现闪退，请选择浏览器中打开
     * rightText :
     * centerText : 浏览器下载
     * leftText :
     * minVersion : 2.7
     * force : false
     * apkurl : https://www.coolapk.com/apk/com.yangtzeu
     * backurl : https://www.coolapk.com/apk/com.yangtzeu
     * leftUrl : https://www.coolapk.com/apk/com.yangtzeu
     */

    private int code;
    private int minCode;
    private String version;
    private String title;
    private String message;
    private String rightText;
    private String centerText;
    private String leftText;
    private String minVersion;
    private String force;
    private String apkurl;
    private String backurl;
    private String leftUrl;

    public int getMinCode() {
        return minCode;
    }

    public void setMinCode(int minCode) {
        this.minCode = minCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public String getCenterText() {
        return centerText;
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }

    public String getBackurl() {
        return backurl;
    }

    public void setBackurl(String backurl) {
        this.backurl = backurl;
    }

    public String getLeftUrl() {
        return leftUrl;
    }

    public void setLeftUrl(String leftUrl) {
        this.leftUrl = leftUrl;
    }
}
