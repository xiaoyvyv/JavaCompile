package com.xiaoyv.java.mode;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.googlejavaformat.java.JavaFormatterOptions;

public class Setting {
    public static final String KEY = "JavaSetting";
    private static final String sourceVersion = "1.6";
    private static final String targetVersion = "1.6";

    public static String getRtPath() {
        String rtFilePath = Utils.getApp().getFilesDir() + "/lib/rt.jar";
        String path = SPUtils.getInstance(KEY).getString("rt_path");
        return StringUtils.isEmpty(path) ? rtFilePath : path;

    }

    public static JavaFormatterOptions getJavaFormatterOptions() {
        String formatter = SPUtils.getInstance(KEY).getString("formatter", "2");
        JavaFormatterOptions.Style style;
        if (StringUtils.equals(formatter, "2")) {
            style = JavaFormatterOptions.Style.GOOGLE;
        } else {
            style = JavaFormatterOptions.Style.AOSP;
        }
        return JavaFormatterOptions.builder()
                .style(style)
                .build();
    }

    public static void restore() {
        String rtFilePath = Utils.getApp().getFilesDir() + "/lib/rt.jar";
        SPUtils.getInstance(KEY).put("rt_path", rtFilePath);
        SPUtils.getInstance(KEY).put("formatter", "2");
        SPUtils.getInstance(KEY).put("compile_source", sourceVersion);
        SPUtils.getInstance(KEY).put("compile_target", targetVersion);
        SPUtils.getInstance(KEY).put("editor_row", true);
        SPUtils.getInstance(KEY).put("editor_auto_compete", true);
        SPUtils.getInstance(KEY).put("editor_dark_mode", false);
        SPUtils.getInstance(KEY).put("editor_auto_save", true);
        SPUtils.getInstance(KEY).put("run_args", "args");
        SPUtils.getInstance(KEY).put("compile_encoding", "UTF-8");
        SPUtils.getInstance(KEY).put("maven", Maven.MAVEN_JCENTER);
        ToastUtils.showShort("操作成功");
    }

    public static String getClassSourceVersion() {
        String version = SPUtils.getInstance(KEY).getString("compile_source");
        return StringUtils.isEmpty(version) ? sourceVersion : version;
    }

    public static String getClassTargetVersion() {
        String version = SPUtils.getInstance(KEY).getString("compile_target");
        return StringUtils.isEmpty(version) ? targetVersion : version;
    }

    public static String getMainArgs() {
        return SPUtils.getInstance(KEY).getString("run_args", "args");
    }

    public static String getCompileEncoding() {
        return SPUtils.getInstance(KEY).getString("compile_encoding", "UTF-8");
    }

    public static boolean isAutoRunWhenCompiled() {
        return SPUtils.getInstance(KEY).getBoolean("compile_auto_run", true);
    }

    public static boolean isShowRow() {
        return SPUtils.getInstance(KEY).getBoolean("editor_row", true);
    }


    public static boolean isAutoCompete() {
        return SPUtils.getInstance(KEY).getBoolean("editor_auto_compete", true);
    }

    public static boolean isDarkMode() {
        return SPUtils.getInstance(KEY).getBoolean("editor_dark_mode", false);
    }

    public static boolean isAutoSaveWhenExit() {
        return SPUtils.getInstance(KEY).getBoolean("editor_auto_save", true);
    }


    public static String getMaven() {
        return SPUtils.getInstance(KEY).getString("maven", Maven.MAVEN_JCENTER);
    }

}
