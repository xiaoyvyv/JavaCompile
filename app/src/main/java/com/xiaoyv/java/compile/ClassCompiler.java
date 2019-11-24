package com.xiaoyv.java.compile;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.ZipUtils;
import com.xiaoyv.java.compile.listener.CompilerListener;
import com.xiaoyv.java.compile.listener.SimpleCompilationProgress;
import com.xiaoyv.java.mode.Project;
import com.xiaoyv.java.mode.Setting;

import org.eclipse.jdt.internal.compiler.batch.Main;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * java文件编译为class
 */
public class ClassCompiler {
    private static final String LogFile = "class_compile.log";
    private static ClassCompiler classCompiler;

    public static ClassCompiler getInstance() {
        if (classCompiler == null) {
            synchronized (ClassCompiler.class) {
                if (classCompiler == null) {
                    classCompiler = new ClassCompiler();
                }
            }
        }
        return classCompiler;
    }

    public void compile(@NonNull String sourceFileOrDir, @NonNull String distFile, @NonNull CompilerListener compilerListener) {
        compile(new File(sourceFileOrDir), new File(distFile), compilerListener);
    }


    /**
     * @param sourceFileOrDir  待编译文件或文件夹
     * @param distDir          Class存放文件夹
     * @param compilerListener 编译进度监听
     */
    public void compile(@NonNull File sourceFileOrDir, @NonNull File distDir, @NonNull CompilerListener compilerListener) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                // 待编译文件
                if (!sourceFileOrDir.exists()) {
                    throw new Exception("编译代码源不存在");
                }
                // 创建编译文件保存目录
                FileUtils.createOrExistsDir(distDir);


                // 依赖路径
                String classPath = Project.getUserLibClassPath() + File.pathSeparator + Setting.getRtPath();
                LogUtils.e(classPath);
                // 编译命令
                String[] compileCmd = new String[]{
                        sourceFileOrDir.getAbsolutePath(),
                        "-d", distDir.getAbsolutePath(),
                        "-encoding", Setting.getCompileEncoding(),
                        "-classpath", classPath,
                        "-source", Setting.getClassSourceVersion(),
                        "-target", Setting.getClassTargetVersion(),
                        //"-verbose", //启用详细输出
                        "-nowarn",
                        "-time"
                };

                // 编译日志文件
                String logFilePath = Utils.getApp().getCacheDir() + File.separator + LogFile;
                FileUtils.createFileByDeleteOldFile(logFilePath);
                // 日志输出
                PrintWriter printWriter = new PrintWriter(logFilePath);
                // 编译
                boolean compile = Main.compile(compileCmd, printWriter, printWriter, new SimpleCompilationProgress() {
                    @Override
                    protected void onProgress(String task, int progress) {
                        // 编译文件监听，回调正在编译文件名
                        Utils.runOnUiThread(() -> compilerListener.onProgress(task, progress));
                    }
                });
                // 编译结果
                if (compile) {
                    // 编译完成，若编译的为文件，返回class类路径，若编译的文件夹，返回jar路径
                    if (FileUtils.isFile(sourceFileOrDir)) {
                        String source = sourceFileOrDir.getAbsolutePath();
                        String classFilePath = source.replace(Project.getCurrentSrcDirPath(), Project.getCurrentBinDirPath());
                        classFilePath = classFilePath.replace(".java", ".class");
                        // 返回文件路径（Class文件路径）
                        return classFilePath;
                    }
                    // 当编译的是项目时
                    else {
                        // 将编译后的项目打包为jar
                        // 获取 bin 文件夹内的文件和包
                        List<File> fileList = FileUtils.listFilesInDir(Project.getCurrentBinDirPath());
                        File jarPath = new File(Project.getCurrentBuildDirPath() + "/" + Project.currentProjectName + ".jar");
                        // 打包jar
                        try {
                            FileUtils.createFileByDeleteOldFile(jarPath);
                            boolean b = ZipUtils.zipFiles(fileList, jarPath);
                            if (b) {
                                return jarPath.getAbsolutePath();
                            } else {
                                throw new Exception("Jar打包错误：未知错误");
                            }
                        } catch (Exception e) {
                            throw new Exception("Jar打包错误", e);
                        }
                    }
                } else {
                    // 读取错误日志
                    String log = FileIOUtils.readFile2String(logFilePath);
                    throw new Exception("字节码编译错误：\n" + log);
                }
            }

            @Override
            public void onFail(Throwable t) {
                compilerListener.onError(t);
            }

            @Override
            public void onSuccess(Object result) {
                compilerListener.onSuccess(String.valueOf(result));
            }
        });
    }

    public static boolean checkRtFile() {
        String rtPath = Setting.getRtPath();
        boolean fileExists = FileUtils.isFileExists(rtPath);
        if (!fileExists) {
            return ResourceUtils.copyFileFromAssets("rt.jar", rtPath);
        }
        return true;
    }
}
