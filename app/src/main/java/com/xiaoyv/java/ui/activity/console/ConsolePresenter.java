package com.xiaoyv.java.ui.activity.console;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.xiaomi.market.sdk.Constants;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.xiaoyv.javaengine.JavaEngine;
import com.xiaoyv.javaengine.compile.listener.ExecuteListener;
import com.xiaoyv.javaengine.console.JavaConsole;


/**
 * 控制台
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class ConsolePresenter implements ConsoleContract.Presenter {
    @NonNull
    private final ConsoleContract.View view;
    private final String className;
    private final String dexPath;
    /**
     * Java控制台对象
     */
    private JavaConsole javaConsole;

    ConsolePresenter(@NonNull ConsoleContract.View view, String dexPath, String className) {
        this.view = view;
        this.dexPath = dexPath;
        this.className = className;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        javaConsole = new JavaConsole(new JavaConsole.AppendStdListener() {
            @Override
            public void printStderr(CharSequence err) {
                view.showStderr(err);
            }

            @Override
            public void printStdout(CharSequence out) {
                view.showStdout(out);
            }
        });
        runDexFile(new String[]{""});
    }

    @Override
    public void runDexFile(String[] args) {
        javaConsole.start();
        ExecuteListener executeListener = new ExecuteListener() {
            @Override
            public void onExecuteFinish() {
                LogUtils.i("运行结束");
                javaConsole.stop();
            }

            @Override
            public void onExecuteError(Throwable error) {
                LogUtils.e("运行错误：" + error);
                javaConsole.stop();
            }
        };

        if (!StringUtils.isEmpty(className)) {
            JavaEngine.getDexExecutor().exec(dexPath, className, args, executeListener);
        } else {
            JavaEngine.getDexExecutor().exec(dexPath, args, executeListener);
        }
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }
}
