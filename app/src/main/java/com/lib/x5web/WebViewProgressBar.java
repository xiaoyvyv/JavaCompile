package com.lib.x5web;

/**
 * Created by Administrator on 2018/4/9.
 *
 * @author 王怀玉
 * @explain WebViewProgressBar
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 平滑进度条
 * Created by cc_want on 2017/5/28.
 */

public class WebViewProgressBar extends View {
    private Drawable drawable;
    private int progress;
    public int true_progress;
    private double progressWidth;
    private FlingRunnable mFlingRunnable;

    public WebViewProgressBar(Context context) {
        super(context);
        init();
    }

    public WebViewProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebViewProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawable = new ColorDrawable(0xff4fd922);
    }

    /**
     * set progress bar color
     *
     * @param color
     */
    public void setProgressColor(int color) {
        drawable = new ColorDrawable(color);
        invalidate();
    }

    /**
     * set progress
     *
     * @param progress this progress max value 100
     */
    public void setProgress(int progress) {
        this.true_progress = progress;
        if (progress < 0 || progress > 100) {
            return;
        }
        this.progress = progress;
        if (mFlingRunnable == null) {
            mFlingRunnable = new FlingRunnable();
        } else {
            removeCallbacks(mFlingRunnable);
        }
        mFlingRunnable.startFling();
    }

    /**
     * getInstance progress value
     *
     * @return progress value
     */
    public int getProgress() {
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawable == null) {
            return;
        }
        drawable.setBounds(0, 0, (int) Math.floor(progressWidth), getHeight());
        drawable.draw(canvas);
    }


    class FlingRunnable implements Runnable {

        double speed = 1;
        double targetWidth = 0;

        public void startFling() {
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
                progressWidth = 0;
            }
            targetWidth = (getWidth() * progress / 100);
            if (progressWidth > targetWidth) {
                progressWidth = targetWidth;
            }
            postOnAnimation(this);
        }

        public void endFling() {
            progressWidth = getWidth();
            removeCallbacks(this);
            setVisibility(GONE);
        }

        @Override
        public void run() {
            //计算速度
            if (progressWidth < targetWidth) {
                speed++;
            } else {
                speed = 1;
            }
            //计算进度条宽度
            progressWidth += speed / 2;
            //计算当前进度
            progress = (int) Math.floor(progressWidth / getWidth() * 100);
            if (progressWidth < getWidth()) {
                invalidate();
                postOnAnimationDelayed(this, 10);
            } else {
                endFling();
            }
        }
    }

}