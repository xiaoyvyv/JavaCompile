package com.lib.mimo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.miui.zeus.mimo.sdk.MimoSdk;
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.ad.IRewardVideoAdWorker;
import com.miui.zeus.mimo.sdk.api.IMimoSdkListener;
import com.xiaomi.ad.common.pojo.AdType;
import com.xiaoyv.java.R;

@SuppressLint("InflateParams")
public class MimoUtils {
    private static final String APP_KEY = "fake_app_key";
    private static final String APP_TOKEN = "fake_app_token";
    public static boolean DEBUG = true;
    public static boolean IS_READY_ERROR = false;

    /**
     * 初始化小米广告SDK
     *
     * @param context 上下文
     */
    public static void initMimo(Context context) {
        MimoUtils.DEBUG = AppUtils.isAppDebug();

        String APP_ID;
        MimoSdk.setEnableUpdate(true);
        if (DEBUG) {
            APP_ID = "2882303761517411490";
            MimoSdk.setDebug(true);
            MimoSdk.setStaging(true);
        } else {
            APP_ID = "2882303761518244874";
        }
        MimoSdk.init(context, APP_ID, APP_KEY, APP_TOKEN, new IMimoSdkListener() {
            @Override
            public void onSdkInitSuccess() {
                IS_READY_ERROR = false;
                LogUtils.i("小米广告SDK启动成功，是否为调试模式：" + DEBUG);
            }

            @Override
            public void onSdkInitFailed() {
                IS_READY_ERROR = true;
                LogUtils.i("小米广告SDK启动失败，是否为调试模式：" + DEBUG);
            }
        });
    }

    public static class FeedView {
        static final String TEST_FeedView_SMALL = "0c220d9bf7029e71461f247485696d07";
        static final String TEST_FeedView_BIG = "2cae1a1f63f60185630f78a1d63923b0";
        static final String TEST_FeedView_GROUP = "b38f454156852941f3883c736c79e7e1";


        /**
         * 信息流大图
         * 信息流大图 发布：00ab925ee304e7b3a37739f8605932dc
         */
        public static void loadBigPicture(Context context, int size, OnAdListener listener) {
            try {
                listener.iAdWorker = AdWorkerFactory.getAdWorker(context, null, listener, AdType.AD_STANDARD_NEWSFEED);
                if (DEBUG) {
                    listener.iAdWorker.load(TEST_FeedView_BIG, size);
                } else {
                    listener.iAdWorker.load("00ab925ee304e7b3a37739f8605932dc", size);
                }
            } catch (Exception e) {
                listener.onAdFailed(e.getMessage());
            }
        }

        /**
         * 信息流小图
         * 信息流小图 发布：98ceef8ac487e47adf72ef0a5aceb43e
         */
        public static void loadSmallPicture(Context context, int size, OnAdListener listener) {
            try {
                listener.iAdWorker = AdWorkerFactory.getAdWorker(context, null, listener, AdType.AD_STANDARD_NEWSFEED);
                if (DEBUG) {
                    listener.iAdWorker.load(TEST_FeedView_SMALL, size);
                } else {
                    listener.iAdWorker.load("98ceef8ac487e47adf72ef0a5aceb43e", size);
                }
            } catch (Exception e) {
                listener.onAdFailed(e.getMessage());
            }
        }


        /**
         * 回到App界面插屏（信息流大图）
         * 发布用：f75d6e0a1b7fd73195f7c8579a924d86
         */
        public static void loadBackAppFeedView(Context context, int size, OnAdListener listener) {
            try {
                listener.iAdWorker = AdWorkerFactory.getAdWorker(context, null, listener, AdType.AD_STANDARD_NEWSFEED);
                if (DEBUG) {
                    listener.iAdWorker.load(TEST_FeedView_BIG);
                } else {
                    listener.iAdWorker.load("e632178735f05865588140b23f151d59", size);
                }
            } catch (Exception e) {
                listener.onAdFailed(e.getMessage());
            }
        }

    }

    public static class Action {

        public static boolean show(IAdWorker worker) {
            try {
                worker.show();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        public static boolean show(IRewardVideoAdWorker worker) {
            try {
                worker.show();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        public static View updateAdView(IAdWorker worker, int i) {
            View inflate = LayoutInflater.from(Utils.getApp().getApplicationContext()).inflate(R.layout.view_ad_container, null);
            FrameLayout container = inflate.findViewById(R.id.container);
            try {
                View view = worker.updateAdView(container, i);
                if (view != null)
                    container.addView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return inflate;
        }

        public static void recyclerView(IAdWorker worker) {
            try {
                if (worker != null) {
                    worker.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static boolean isReady(IAdWorker worker) {
            if (worker == null) return false;
            try {
                return worker.isReady();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }


}

