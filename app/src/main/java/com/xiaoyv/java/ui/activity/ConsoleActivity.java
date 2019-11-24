package com.xiaoyv.java.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.lib.utils.MyUtils;
import com.xiaoyv.java.JavaApplication;
import com.xiaoyv.java.R;
import com.xiaoyv.java.compile.DexExecute;
import com.xiaoyv.java.compile.listener.DexExecuteListener;
import com.xiaoyv.java.compile.utils.ClassUtil;
import com.xiaoyv.java.compile.view.console.ConsoleEditText;
import com.xiaoyv.java.mode.Project;
import com.xiaoyv.java.mode.Setting;
import com.xiaoyv.java.ui.activity.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class ConsoleActivity extends BaseActivity {
    public static final String DEX_FILE = "RUN_DEX_FILE";
    private ConsoleEditText consoleView;
    private Toolbar toolbar;
    // dex 文件路径
    private String runDexFilePath;
    // main 方法初始参数
    private String[] args = new String[]{};

    /**
     * 启动控制台并且运行Dex文件
     */
    public static void start(Context context, String runDexFilePath) {
        LogUtils.e(runDexFilePath);
        Intent intent = new Intent(context, ConsoleActivity.class);
        intent.putExtra(ConsoleActivity.DEX_FILE, runDexFilePath);
        MyUtils.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        // 要运行的Dex文件路径
        runDexFilePath = getIntent().getStringExtra(DEX_FILE);
        if (StringUtils.isEmpty(runDexFilePath)) {
            finish();
            return;
        }
        // 初始化
        init();
        MyUtils.setToolbarBackToHome(this, toolbar);
    }

    @Override
    public void findViews() {
        consoleView = findViewById(R.id.consoleView);
        toolbar = findViewById(R.id.toolbar);

    }

    @Override
    public void setEvents() {
        runDex(runDexFilePath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("清空").setOnMenuItemClickListener(item -> {
            consoleView.setText(null);
            toolbar.setTitle("未运行程序");
            toolbar.setSubtitle("状态：空闲");
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        setArgs(Setting.getMainArgs().split(" "));

    }

    @Override
    protected void onStop() {
        super.onStop();
        removeInOutStream();
    }


    public void runDex(String dexPath) {
        // 读取dex文件所有的类
        List<String> dexClasses = new ArrayList<>();
        try {
            DexFile dexFile = new DexFile(dexPath);
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                dexClasses.add(entries.nextElement());
            }
            dexFile.close();
        } catch (Exception e) {
            System.err.println("运行错误：Dex文件解析失败");
            return;
        }

        // 查询src目录有 main 方法的类 且dex也存在的类
        List<String> mainClassList = new ArrayList<>();
        List<File> fileList = FileUtils.listFilesInDir(Project.getCurrentSrcDir(), true);
        for (File file : fileList) {
            if (ClassUtil.hasMainFunction(file)) {
                String fileAbsolutePath = file.getAbsolutePath();
                String mainClass = fileAbsolutePath.replace(Project.getCurrentSrcDirPath() + "/", "");
                mainClass = mainClass.replace("/", ".");
                mainClass = mainClass.replace(".java", "");
                // 检测dex是否有该类
                if (dexClasses.contains(mainClass)) {
                    mainClassList.add(mainClass);
                }
            }
        }

        if (mainClassList.size() == 0) {
            System.err.println("运行错误：未发现类信息");
            return;
        }

        if (mainClassList.size() == 1) {
            runDex(dexPath, mainClassList.get(0));
            return;
        }

        String[] names = new String[mainClassList.size()];
        String[] cls = new String[mainClassList.size()];
        for (int i = 0; i < mainClassList.size(); i++) {
            String classPath = mainClassList.get(i);
            cls[i] = classPath;
            if (classPath.contains(".")) {
                classPath = classPath.substring(classPath.lastIndexOf(".") + 1);
            }
            names[i] = classPath;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请选择一个类运行").setItems(names, (dialog12, which) ->
                        runDex(dexPath, cls[which]))
                .setPositiveButton("取消", (dialog1, which) ->
                        System.err.println("未选择入口类，取消执行"))
                .create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }


    private void runDex(String dexPath, String className) {
        toolbar.setTitle(className + ".main(args)");
        toolbar.setSubtitle("状态：运行中");

        // 开启控制台拦截
        initInOutStream();
        // 截获输入
        System.setIn(consoleView.getInputStream());
        DexExecute.getInstance().exec(dexPath, className, args, new DexExecuteListener() {
            @Override
            public void onExeCute() {
                // 方法执行完成
                System.out.println("\n[Process finished with exit code 0]");
                // 重置输入
                System.setIn(System.in);
                // 关闭控制台IO
                removeInOutStream();
                // 状态：运行结束
                toolbar.setSubtitle("状态：运行结束");
            }

            @Override
            public void onError(Throwable error) {
                toolbar.setSubtitle("状态：错误");
                // 打印错误
                error.printStackTrace(consoleView.getErrorStream());
                // 关闭控制台IO
                removeInOutStream();
            }
        });
    }

    private void initInOutStream() {
        ThreadUtils.getCachedPool().execute(() -> {
            JavaApplication application = (JavaApplication) Utils.getApp();
            application.addStdErr(consoleView.getErrorStream());
            application.addStdOut(consoleView.getOutputStream());
        });
    }

    private void removeInOutStream() {
        ThreadUtils.getCachedPool().execute(() -> {
            JavaApplication application = (JavaApplication) Utils.getApp();
            application.removeErrStream(consoleView.getErrorStream());
            application.removeOutStream(consoleView.getOutputStream());
        });
    }


    private void setArgs(String[] args) {
        this.args = args;
    }

}
