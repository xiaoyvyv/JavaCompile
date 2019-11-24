package com.lib.textwarrior.code;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by why on 2017/11/4.
 */
public class SymbolView extends HorizontalScrollView {
    // 上下文
    private final Context context;
    // TAB
    public static final String TAB = "→";
    // 每个符号宽度
    private static final int TILE_WIDTH = 80;
    // 符号
    private static final String symbol = "→{}();,%.=\"[]#+-*/<>\\|'&!~?$@:_";
    private int textBackgroundColor = 0xFFDDDDDD;
    // 符号点击事件
    private OnSymbolViewClick onSymbolViewClick;
    private LinearLayout linearLayout;
    private int bottomPadding = 20;
    private int topPadding = 20;

    public SymbolView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SymbolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SymbolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void init() {
        // 取消滚动条
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);

        linearLayout = new LinearLayout(context);
        addSymbol();
        this.addView(linearLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSymbol() {
        linearLayout.removeAllViews();
        final float[] tempPoint = new float[2];
        for (int i = 0; i < SymbolView.symbol.length(); i++) {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            if (i == 0) {
                textView.setText(TAB);
            } else {
                textView.setText(String.valueOf(SymbolView.symbol.charAt(i)));
            }

            textView.setClickable(true);
            textView.setPadding(20, topPadding, 20, bottomPadding);
            textView.setTextSize(24);
            textView.setBackgroundColor(textBackgroundColor);
            textView.setMinWidth(TILE_WIDTH);

            // 设置符号点击回调和点击加深背景
            textView.setOnTouchListener((v, event) -> {
                TextView text = (TextView) v;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tempPoint[0] = event.getX();
                        tempPoint[1] = event.getY();
                        text.setBackgroundColor(Color.GRAY);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // 恢复默认颜色
                        text.setBackgroundColor(textBackgroundColor);
                        if (Math.abs(event.getX() - tempPoint[0]) < TILE_WIDTH) {
                            if (onSymbolViewClick != null)
                                onSymbolViewClick.onClick(text, text.getText().toString());
                        }
                        break;
                }
                return true;
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.addView(textView, layoutParams);
        }
    }

    public void setVisible(boolean visible) {
        if (!visible) {
            this.setVisibility(View.GONE);
            return;
        }
        this.setVisibility(View.VISIBLE);
    }

    public void setOnSymbolViewClick(OnSymbolViewClick onSymbolViewClick) {
        this.onSymbolViewClick = onSymbolViewClick;
    }

    public interface OnSymbolViewClick {
        void onClick(View view, String text);
    }

    public int getTextBackgroundColor() {
        return textBackgroundColor;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    public void setTopBottomPadding(int topPadding, int bottomPadding) {
        this.topPadding = topPadding;
        this.bottomPadding = bottomPadding;
        addSymbol();
    }

    public void setTextBackgroundColor(int textBackgroundColor) {
        this.textBackgroundColor = textBackgroundColor;
        addSymbol();
    }
}
