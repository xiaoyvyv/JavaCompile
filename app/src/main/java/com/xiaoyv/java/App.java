package com.xiaoyv.java;

import android.os.Environment;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.lib.mimo.MimoUtils;
import com.xiaoyv.java.mode.service.X5InitService;

import java.io.File;
import java.util.List;

public class App extends JavaApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化X5内核
        X5InitService.start(this);
        MimoUtils.initMimo(this);
        List<File> fileList = FileUtils.listFilesInDir(PathUtils.getExternalStoragePath());


        File externalFilesDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        LogUtils.e(externalFilesDir.getAbsolutePath());
    }
}
