/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.xiaoyv.editor.android;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.xiaoyv.editor.common.ColorScheme;
import com.xiaoyv.editor.common.DocumentProvider;

//TODO minimise unnecessary invalidate calls

public class TouchNavigationMethod extends GestureDetector.SimpleOnGestureListener {
    private GestureDetector gestureDetector;
    private float lastDist;
    private float lastSize;
    private int fling;
    boolean isCaretTouched = false;
    FreeScrollingTextField scrollingTextField;

    TouchNavigationMethod(FreeScrollingTextField textField) {
        scrollingTextField = textField;
        gestureDetector = new GestureDetector(textField.getContext(), this);
        gestureDetector.setIsLongpressEnabled(true);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        int x = screenToViewX((int) e.getX());
        int y = screenToViewY((int) e.getY());
        isCaretTouched = isNearChar(x, y, scrollingTextField.getCaretPosition());

        if (scrollingTextField.isFlingScrolling()) {
            scrollingTextField.stopFlingScrolling();
        } else if (scrollingTextField.isSelectText()) {
            if (isNearChar(x, y, scrollingTextField.getSelectionStart())) {
                scrollingTextField.focusSelectionStart();
                scrollingTextField.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                isCaretTouched = true;
            } else if (isNearChar(x, y, scrollingTextField.getSelectionEnd())) {
                scrollingTextField.focusSelectionEnd();
                scrollingTextField.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                isCaretTouched = true;
            }
        }

        if (isCaretTouched) {
            scrollingTextField.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        int x = screenToViewX((int) e.getX());
        int y = screenToViewY((int) e.getY());
        int charOffset = scrollingTextField.coordToCharIndex(x, y);

        if (scrollingTextField.isSelectText()) {
            int strictCharOffset = scrollingTextField.coordToCharIndexStrict(x, y);
            if (scrollingTextField.inSelectionRange(strictCharOffset) ||
                    isNearChar(x, y, scrollingTextField.getSelectionStart()) ||
                    isNearChar(x, y, scrollingTextField.getSelectionEnd())) {
                // do nothing
            } else {
                scrollingTextField.selectText(false);
                if (strictCharOffset >= 0) {
                    scrollingTextField.moveCaret(charOffset);
                }
            }
        } else {
            if (charOffset >= 0) {
                scrollingTextField.moveCaret(charOffset);
            }
        }
        scrollingTextField.showIME();
        return true;
    }

    public boolean onUp(MotionEvent e) {
        scrollingTextField.stopAutoScrollCaret();
        isCaretTouched = false;
        lastDist = 0;
        fling = 0;
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2
            , float distanceX, float distanceY) {
        if (isCaretTouched) {
            dragCaret(e2);
        } else if (e2.getPointerCount() == 1) {
            if (fling == 0) {
                if (Math.abs(distanceX) > Math.abs(distanceY))
                    fling = 1;
                else
                    fling = -1;
            }
            if (fling == 1)
                distanceY = 0;
            else if (fling == -1)
                distanceX = 0;

            scrollView(distanceX, distanceY);

        }

        if ((e2.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            onUp(e2);
        }
        return true;
    }

    // 边缘临界值
    private static final int SCROLL_EDGE_SLOP = 10;

    private void dragCaret(MotionEvent e) {
        if (!scrollingTextField.isSelectText() && isDragSelect()) {
            scrollingTextField.selectText(true);
        }

        int x = (int) e.getX() - scrollingTextField.getPaddingLeft();
        int y = (int) e.getY() - scrollingTextField.getPaddingTop();
        boolean scrolled = false;

        // 如果触摸到文本字段内容区域的边缘，请向相应方向滚动。
        if (x < SCROLL_EDGE_SLOP) {
            scrolled = scrollingTextField.autoScrollCaret(FreeScrollingTextField.SCROLL_LEFT);
        } else if (x >= (scrollingTextField.getContentWidth() - SCROLL_EDGE_SLOP)) {
            scrolled = scrollingTextField.autoScrollCaret(FreeScrollingTextField.SCROLL_RIGHT);
        } else if (y < SCROLL_EDGE_SLOP) {
            scrolled = scrollingTextField.autoScrollCaret(FreeScrollingTextField.SCROLL_UP);
        } else if (y >= (scrollingTextField.getContentHeight() - SCROLL_EDGE_SLOP)) {
            scrolled = scrollingTextField.autoScrollCaret(FreeScrollingTextField.SCROLL_DOWN);
        }

        if (!scrolled) {
            scrollingTextField.stopAutoScrollCaret();
            int newCaretIndex = scrollingTextField.coordToCharIndex(
                    screenToViewX((int) e.getX()),
                    screenToViewY((int) e.getY())
            );
            if (newCaretIndex >= 0) {
                scrollingTextField.moveCaret(newCaretIndex);
            }
        }
    }

    private void scrollView(float distanceX, float distanceY) {
        int newX = (int) distanceX + scrollingTextField.getScrollX();
        int newY = (int) distanceY + scrollingTextField.getScrollY();

        // 如果 scrollX 和 scrollX 比推荐的最大滚动值更大，则将它们作为新的最大值使用。
        // 还要考虑插入符号的大小，
        // 可能超出文本边界
        int maxWidth = Math.max(scrollingTextField.getMaxScrollX(),
                scrollingTextField.getScrollX());
        if (newX > maxWidth) {
            newX = maxWidth;
        } else if (newX < 0) {
            newX = 0;
        }

        int maxHeight = Math.max(scrollingTextField.getMaxScrollY(),
                scrollingTextField.getScrollY());
        if (newY > maxHeight) {
            newY = maxHeight;
        } else if (newY < 0) {
            newY = 0;
        }
        scrollingTextField.smoothScrollTo(newX, newY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (!isCaretTouched) {
            if (fling == 1)
                velocityY = 0;
            else if (fling == -1)
                velocityX = 0;

            scrollingTextField.flingScroll((int) -velocityX, (int) -velocityY);
        }
        onUp(e2);
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private boolean onTouchZoom(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (e.getPointerCount() == 2) {
                if (lastDist == 0) {
                    float x = e.getX(0) - e.getX(1);
                    float y = e.getY(0) - e.getY(1);
                    lastDist = (float) Math.sqrt(x * x + y * y);
                    lastSize = scrollingTextField.getTextSize();
                }

                float dist = spacing(e);
                if (lastDist != 0) {
                    scrollingTextField.setTextSize((int) (lastSize * (dist / lastDist)));
                }
                return true;
            }
        }
        lastDist = 0;
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        onTouchZoom(event);
        boolean handled = gestureDetector.onTouchEvent(event);
        if (!handled
                && (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            handled = onUp(event);
        }
        return handled;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        onDoubleTap(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        isCaretTouched = true;
        int x = screenToViewX((int) e.getX());
        int y = screenToViewY((int) e.getY());
        int charOffset = scrollingTextField.coordToCharIndex(x, y);

        if (charOffset >= 0) {
            scrollingTextField.moveCaret(charOffset);
            DocumentProvider doc = scrollingTextField.createDocumentProvider();
            int start;
            int end;
            for (start = charOffset; start >= 0; start--) {
                char c = doc.charAt(start);
                if (!Character.isJavaIdentifierPart(c))
                    break;
            }
            if (start != charOffset)
                start++;
            for (end = charOffset; end >= 0; end++) {
                char c = doc.charAt(end);
                if (!Character.isJavaIdentifierPart(c))
                    break;
            }
            scrollingTextField.selectText(true);
            scrollingTextField.setSelectionRange(start, end - start);
        }
        return true;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    void onPause() {
        //do nothing
    }

    void onResume() {
        //do nothing
    }

    /**
     * Called by FreeScrollingTextField when it has finished drawing text.
     * Classes extending TouchNavigationMethod can use this to draw, for
     * example, a custom caret.
     * <p>
     * The canvas includes padding in it.
     *
     * @param canvas
     */
    public void onTextDrawComplete(Canvas canvas) {
        // Do nothing. Basic caret drawing is handled by FreeScrollingTextField.
    }

    public void onColorSchemeChanged(ColorScheme colorScheme) {
        // Do nothing. Derived classes can use this to change their graphic assets accordingly.
    }

    public void onChiralityChanged(boolean isRightHanded) {
        // Do nothing. Derived classes can use this to change their input
        // handling and graphic assets accordingly.
    }

    private final static Rect _caretBloat = new Rect(0, 0, 0, 0);

    /**
     * For any printed character, this method returns the amount of space
     * required in excess of the bounding box of the character to draw the
     * caret.
     * Subclasses should override this method if they are drawing their
     * own carets.
     */
    public Rect getCaretBloat() {
        return _caretBloat;
    }


    //*********************************************************************
    //**************************** Utilities ******************************
    //*********************************************************************

    final protected int getPointerId(MotionEvent e) {
        return (e.getAction() & MotionEvent.ACTION_POINTER_ID_MASK)
                >> MotionEvent.ACTION_POINTER_ID_SHIFT;
    }

    /**
     * Converts a x-coordinate from screen coordinates to local coordinates,
     * excluding padding
     */
    final protected int screenToViewX(int x) {
        return x - scrollingTextField.getPaddingLeft() + scrollingTextField.getScrollX();
    }

    /**
     * Converts a y-coordinate from screen coordinates to local coordinates,
     * excluding padding
     */
    final protected int screenToViewY(int y) {
        return y - scrollingTextField.getPaddingTop() + scrollingTextField.getScrollY();
    }

    final public boolean isRightHanded() {
        return true;
    }

    final private boolean isDragSelect() {
        return false;
    }


    /**
     * The radius, in density-independent pixels, around a point of interest
     * where any touch event within that radius is considered to have touched
     * the point of interest itself
     */
    protected static int TOUCH_SLOP = 12;

    /**
     * Determine if a point(x,y) on screen is near a character of interest,
     * specified by its index charOffset. The radius of proximity is defined
     * by TOUCH_SLOP.
     *
     * @param x          X-coordinate excluding padding
     * @param y          Y-coordinate excluding padding
     * @param charOffset the character of interest
     * @return Whether (x,y) lies close to the character with index charOffset
     */
    public boolean isNearChar(int x, int y, int charOffset) {
        Rect bounds = scrollingTextField.getBoundingBox(charOffset);

        return (y >= (bounds.top - TOUCH_SLOP)
                && y < (bounds.bottom + TOUCH_SLOP)
                && x >= (bounds.left - TOUCH_SLOP)
                && x < (bounds.right + TOUCH_SLOP)
        );
    }
}
