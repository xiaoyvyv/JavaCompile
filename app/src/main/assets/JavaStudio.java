package com.xiaoyv.java;

import java.lang.reflect.Field;

/**
 * 注意：本软件完全免费且开源
 *
 * <p>2018-2020 本次为2020年7月20日更新的IDE v3版本
 *
 * <p>优化了代码自动提示功能，修复中文输出乱码
 *
 * <p>修复 Android Q 的适配问题
 *
 * @author 王怀玉
 */
public class JavaStudio {
    public String 软件名称 = "Java JavaStudio";
    public String 软件类型 = "免费软件";
    public String 软件别名 = "Java IDE";
    public String 程序开发 = "王怀玉";
    public String 联系邮箱 = "1223414335@qq.com";
    public String 官方鹅群 = "微技术：617082514";
    public String 官方网站 = "https://www.coolapk.com/apk/248636";
    public String 版权信息 = "版权所有(C)2018-2020, 王怀玉保留部分权利";
    public String 软件简介 = "一个可视化的、易用的、快捷的、一体化的 Android Java IDE";
    public String 更多信息 = "请联系作者QQ：1223414335，欢迎支持";

    public static void main(String[] args) {
        JavaStudio javaStudio = new JavaStudio();
        Field[] fields = javaStudio.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                System.out.println(field.getName() + "：" + field.get(javaStudio) + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
