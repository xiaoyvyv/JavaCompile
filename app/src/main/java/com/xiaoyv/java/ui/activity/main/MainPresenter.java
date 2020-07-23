package com.xiaoyv.java.ui.activity.main;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.xiaomi.market.sdk.Constants;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;


/**
 * 主页
 *
 * @author 王怀玉
 * @since 2020/2/8
 */
public class MainPresenter implements MainContract.Presenter {
    @NonNull
    private final MainContract.View view;

    MainPresenter(@NonNull MainContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        checkUpdate();
    }

    @Override
    public void checkUpdate() {
        // 检查更新
        XiaomiUpdateAgent.setUpdateMethod(Constants.UpdateMethod.DOWNLOAD_MANAGER);
        XiaomiUpdateAgent.setUpdateAutoPopup(false);
        XiaomiUpdateAgent.setUpdateListener((i, updateResponse) -> {
            LogUtils.json(updateResponse);
        });
        XiaomiUpdateAgent.update(Utils.getApp());
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
