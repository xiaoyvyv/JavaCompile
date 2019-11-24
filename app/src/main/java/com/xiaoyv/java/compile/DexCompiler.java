package com.xiaoyv.java.compile;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.duy.dex.Dex;
import com.duy.dx.command.dexer.Main;
import com.duy.dx.merge.CollisionPolicy;
import com.duy.dx.merge.DexMerger;
import com.xiaoyv.java.compile.listener.CompilerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * class文件转dex
 */
public class DexCompiler {
    private static DexCompiler dexCompiler;

    public static DexCompiler getInstance() {
        if (dexCompiler == null) {
            synchronized (DexCompiler.class) {
                if (dexCompiler == null) {
                    dexCompiler = new DexCompiler();
                }
            }
        }
        return dexCompiler;
    }

    public void compile(@NonNull String sourceFile, @NonNull String distFile, @NonNull CompilerListener compilerListener) {
        compile(new File(sourceFile), new File(distFile), compilerListener);
    }


    /**
     * @param sourceFile       待编译文件
     * @param distFile         编译目标文件（xxx.dex或者xxx.jar）
     * @param compilerListener 编译结果监听
     */
    public void compile(@NonNull File sourceFile, @NonNull File distFile, @NonNull CompilerListener compilerListener) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                if (ObjectUtils.isEmpty(sourceFile)) {
                    throw new Exception("要转换的class文件为空");
                }

                FileUtils.createFileByDeleteOldFile(distFile);

                // 编译命令
                String[] compileCmd = new String[]{
                        "--verbose",
                        "--no-strict",
                        "--no-files",
                        "--output=" + distFile.getAbsolutePath(),
                        sourceFile.getAbsolutePath()
                };

                // 开始转换dex
                Main.main(compileCmd);
                return distFile.getAbsolutePath();
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

    /**
     * 将多个jar或class文件分别编译至 dexFilesDir 文件夹下
     */
    public void compile(@NonNull List<File> jarList, @NonNull File dexFilesDir, @NonNull CompilerListener compilerListener) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                if (ObjectUtils.isEmpty(jarList)) {
                    throw new Exception("要转换的Jar列表为空");
                }
                // 编译前检查储存文件夹是否存在
                FileUtils.createOrExistsDir(dexFilesDir);

                // 编译后的Dex文件路径集合
                List<String> newDexFiles = new ArrayList<>();
                for (int i = 0; i < jarList.size(); i++) {
                    File file = jarList.get(i);
                    // 先检测jar文件是否变化，未变化则不转换为dex
                    String fileMd5 = FileUtils.getFileMD5ToString(file);
                    String newDexFile = dexFilesDir.getAbsolutePath() + "/" + fileMd5.toLowerCase() + ".dex";
                    if (FileUtils.isFileExists(newDexFile) && newDexFile.length() > 0) {
                        System.out.println("Dex已经编译 长度：" + newDexFile.length());
                        newDexFiles.add(newDexFile);
                        continue;
                    }
                    // 将新生成的dex文件加到集合
                    newDexFiles.add(newDexFile);

                    FileUtils.createFileByDeleteOldFile(newDexFile);
                    // 编译命令
                    String[] compileCmd = new String[]{
                            "--verbose",
                            "--no-strict",
                            "--no-files",
                            "--output=" + newDexFile,
                            file.getAbsolutePath()
                    };
                    // 开始转换dex
                    Main.main(compileCmd);
                }

                // 删除掉旧的Dex文件
                FileUtils.deleteFilesInDirWithFilter(dexFilesDir.getAbsolutePath(), pathname -> {
                    boolean isDelete = true;
                    for (String path : newDexFiles) {
                        if (StringUtils.equals(path, pathname.getAbsolutePath())) {
                            isDelete = false;
                        }
                    }
                    return isDelete;
                });

                return dexFilesDir.getAbsolutePath();
            }

            @Override
            public void onFail(Throwable t) {
                if (t instanceof Error) {
                    Throwable cause = t.getCause();
                    if (cause != null) {
                        String errorFile = cause.getMessage();
                        // 删除已转换的半成品文件
                        String fileMd5 = FileUtils.getFileMD5ToString(errorFile);
                        String newDexFile = dexFilesDir.getAbsolutePath() + "/" + fileMd5.toLowerCase() + ".dex";
                        FileUtils.delete(newDexFile);
                    }
                }
                compilerListener.onError(t);
            }

            @Override
            public void onSuccess(Object result) {
                compilerListener.onSuccess(String.valueOf(result));
            }
        });
    }

    /**
     * 合并Dex文件
     */
    public void mergerDex(@NonNull List<File> dexFiles, @NonNull File distDexFile, @NonNull CompilerListener compilerListener) {
        FileUtils.createFileByDeleteOldFile(distDexFile);
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                if (dexFiles.size() != 0) {
                    Dex[] toBeMerge = new Dex[dexFiles.size()];
                    for (int i = 0; i < dexFiles.size(); i++) {
                        toBeMerge[i] = new Dex(dexFiles.get(i));
                    }
                    DexMerger dexMerger = new DexMerger(toBeMerge, CollisionPolicy.FAIL);
                    Dex merged = dexMerger.merge();
                    if (merged != null) {
                        merged.writeTo(distDexFile);
                    }
                    return distDexFile.getAbsolutePath();
                } else {
                    throw new Exception("Dex合并错误:未找到可以合并的dex文件");
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
}
