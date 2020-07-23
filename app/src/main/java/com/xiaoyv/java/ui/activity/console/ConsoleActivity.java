package com.xiaoyv.java.ui.activity.console;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.xiaoyv.java.R;
import com.xiaoyv.java.base.BaseFragmentActivity;


/**
 * 控制台
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class ConsoleActivity extends BaseFragmentActivity<ConsoleFragment> {
    public static final String CONSOLE_ACTIVITY_DEX_PATH = "CONSOLE_ACTIVITY_DEX_PATH";
    public static final String CONSOLE_ACTIVITY_CLASS_NAME = "CONSOLE_ACTIVITY_CLASS_NAME";

    public static void start(String dexPath, String className) {
        Intent intent = new Intent(Utils.getApp().getApplicationContext(), ConsoleActivity.class);
        intent.putExtra(CONSOLE_ACTIVITY_DEX_PATH, dexPath);
        intent.putExtra(CONSOLE_ACTIVITY_CLASS_NAME, className);
        ActivityUtils.startActivity(intent);
    }

    @Override
    protected void createPresenter(ConsoleFragment fragment) {
        String dexPath = getIntent().getStringExtra(CONSOLE_ACTIVITY_DEX_PATH);
        String className = getIntent().getStringExtra(CONSOLE_ACTIVITY_CLASS_NAME);
        new ConsolePresenter(fragment, dexPath, className);
    }


    @Override
    protected ConsoleFragment createFragment() {
        return ConsoleFragment.newInstance();
    }

    @Override
    public void onBackPressed() {
        if (!fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

}
