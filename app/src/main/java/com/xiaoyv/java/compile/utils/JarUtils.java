package com.xiaoyv.java.compile.utils;

import android.content.Context;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;

public class JarUtils {
    public static List<Class> getClasses(String jarPath) {
        return getClasses(new File(jarPath));
    }

    public static List<Class> getClasses(File file) {
        if (!FileUtils.isFileExists(file)) {
            return new ArrayList<>();
        }
        DexClassLoader dexClassLoader = new DexClassLoader(file.getAbsolutePath(), Utils.getApp().getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(),
                null, ClassLoader.getSystemClassLoader());
        List<Class> classes = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> e = jarFile.entries();

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class") || je.getName().contains("$")) {
                    continue;
                }
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                try {
                    Class c = dexClassLoader.loadClass(className);
                    classes.add(c);
                } catch (Exception ex) {
                    LogUtils.e(ex.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
}
