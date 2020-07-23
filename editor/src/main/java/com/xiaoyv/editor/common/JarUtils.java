package com.xiaoyv.editor.common;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;

public class JarUtils {
    public static List<Class<?>> getClasses(String jarPath) {
        return getClasses(new File(jarPath));
    }

    public static List<Class<?>> getClasses(File file) {
        if (file == null || !file.exists()) {
            return new ArrayList<>();
        }
        List<Class<?>> classes = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class") || jarEntry.getName().contains("$")) {
                    continue;
                }
                String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                className = className.replace('/', '.');
                try {
                    Class<?> c = ClassLoader.getSystemClassLoader().loadClass(className);
                    classes.add(c);
                } catch (Exception ex) {
                    Log.e("编译器未适配该类：", className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
}
