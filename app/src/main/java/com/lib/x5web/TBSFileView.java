package com.lib.x5web;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

/**
 * @author fangzhenpeng
 * @date 2017/10/23
 * @description 使用腾讯TBS打开office文件
 */

public class TBSFileView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private static final String TAG = "OfficeView";

    private TbsReaderView mTbsReaderView;

    public TBSFileView(Context context) {
        this(context, null);
        init();
    }

    public TBSFileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public TBSFileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    private void init() {
        mTbsReaderView = getTbsReaderView(getContext());
        removeAllViews();
        addView(mTbsReaderView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    public void displayFile(File mFile) {
        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //加载文件
            Bundle localBundle = new Bundle();
            localBundle.putString("filePath", mFile.toString());
            localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");
            if (mTbsReaderView == null) {
                mTbsReaderView = getTbsReaderView(getContext());
            }
            boolean bool = mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
            if (bool) {
                mTbsReaderView.openFile(localBundle);
            }
        } else {
            Log.e(TAG, "文件路径无效！: ");
        }
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Log.d(TAG, "文件路径为空");
            return str;
        }
        Log.d(TAG, "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Log.d(TAG, "i <= -1");
            return str;
        }
        str = paramString.substring(i + 1);
        Log.d(TAG, "文件类型" + str);
        return str;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }

    public void onStop() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }
}
