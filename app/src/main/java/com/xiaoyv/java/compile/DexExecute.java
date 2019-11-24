package com.xiaoyv.java.compile;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.xiaoyv.java.compile.listener.DexExecuteListener;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * 执行dex的main方法
 */
public class DexExecute {
    private static DexExecute dexExecute;

    public static DexExecute getInstance() {
        if (dexExecute == null) {
            synchronized (DexExecute.class) {
                if (dexExecute == null) {
                    dexExecute = new DexExecute();
                }
            }
        }
        return dexExecute;
    }

    public void exec(@NonNull String dexPath, @NonNull String className, String[] args, @NonNull DexExecuteListener dexExecuteListener) {
        exec(new File(dexPath), className, args, dexExecuteListener);
    }


    /**
     * @param dexPath Dex文件
     */
    public void exec(@NonNull File dexPath, @NonNull String className, String[] args, @NonNull DexExecuteListener dexExecuteListener) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                String optimizedDirectory = Utils.getApp().getCacheDir().getAbsolutePath() + "/dex";
                FileUtils.createOrExistsDir(optimizedDirectory);
                DexClassLoader dexClassLoader = new DexClassLoader(dexPath.getAbsolutePath(), optimizedDirectory,
                        null, ClassLoader.getSystemClassLoader());
                // 加载 Class
                Class<?> clazz = dexClassLoader.loadClass(className);
                // 获取main方法
                Method method = clazz.getDeclaredMethod("main", String[].class);
                // 调用静态方法可以直接传 null
                method.invoke(null, new Object[]{args});
                return null;
            }

            @Override
            public void onFail(Throwable t) {
                dexExecuteListener.onError(t);
            }

            @Override
            public void onSuccess(Object result) {
                dexExecuteListener.onExeCute();
            }
        });
    }
}
