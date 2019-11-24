package com.xiaoyv.java.compile.listener;

import com.blankj.utilcode.util.LogUtils;

import org.eclipse.jdt.core.compiler.CompilationProgress;

public abstract class SimpleCompilationProgress extends CompilationProgress {
    private int allSize = 0;
    private int index = 0;

    @Override
    public void begin(int i) {
        allSize = i;
        index = 0;
        LogUtils.e("待编译文件数目：" + i);
    }

    @Override
    public void done() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void setTaskName(String s) {
        int progress = (int) ((index / (allSize * 1.0f) * 100));
        onProgress(s, progress);
        LogUtils.e(s, index, progress);
        index++;
    }

    @Override
    public void worked(int workIncrement, int remainingWork) {

    }

    protected abstract void onProgress(String task, int progress);
}
