package com.lib.textwarrior.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.lib.textwarrior.common.ColorScheme;
import com.lib.textwarrior.common.Pair;

public class YoyoNavigationMethod extends TouchNavigationMethod {
    private final Yoyo yoyoCaret;
    private final Yoyo yoyoStart;
    private final Yoyo yoyoEnd;

    private boolean isStartHandleTouched = false;
    private boolean isEndHandleTouched = false;
    private boolean isCaretHandleTouched = false;
    private boolean isShowYoyoCaret = false;

    private int _yoyoSize;

    public YoyoNavigationMethod(FreeScrollingTextField textField) {
        super(textField);
        DisplayMetrics dm = textField.getContext().getResources().getDisplayMetrics();
        _yoyoSize = (int) TypedValue.applyDimension(2, (float) (FreeScrollingTextField.BASE_TEXT_SIZE_PIXELS * 1.5), dm);
        yoyoCaret = new Yoyo();
        yoyoStart = new Yoyo();
        yoyoEnd = new Yoyo();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        super.onDown(e);
        if (!isCaretTouched) {
            int x = (int) e.getX() + scrollingTextField.getScrollX();
            int y = (int) e.getY() + scrollingTextField.getScrollY();
            isCaretHandleTouched = yoyoCaret.isInHandle(x, y);
            isStartHandleTouched = yoyoStart.isInHandle(x, y);
            isEndHandleTouched = yoyoEnd.isInHandle(x, y);

            if (isCaretHandleTouched) {
                isShowYoyoCaret = true;
                yoyoCaret.setInitialTouch(x, y);
                yoyoCaret.invalidateHandle();
            } else if (isStartHandleTouched) {
                yoyoStart.setInitialTouch(x, y);
                scrollingTextField.focusSelectionStart();
                yoyoStart.invalidateHandle();
            } else if (isEndHandleTouched) {
                yoyoEnd.setInitialTouch(x, y);
                scrollingTextField.focusSelectionEnd();
                yoyoEnd.invalidateHandle();
            }
        }

        return true;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        isCaretHandleTouched = false;
        isStartHandleTouched = false;
        isEndHandleTouched = false;
        yoyoCaret.clearInitialTouch();
        yoyoStart.clearInitialTouch();
        yoyoEnd.clearInitialTouch();
        super.onUp(e);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {

        if (isCaretHandleTouched) {
            if ((e2.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                onUp(e2);
            } else {
                isShowYoyoCaret = true;
                moveHandle(yoyoCaret, e2);
            }

            return true;
        } else if (isStartHandleTouched) {
            if ((e2.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                onUp(e2);
            } else {
                moveHandle(yoyoStart, e2);
            }

            return true;
        } else if (isEndHandleTouched) {
            if ((e2.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                onUp(e2);
            } else {
                moveHandle(yoyoEnd, e2);
            }

            return true;
        } else {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private void moveHandle(Yoyo _yoyo, MotionEvent e) {

        Pair foundIndex = _yoyo.findNearestChar((int) e.getX(), (int) e.getY());
        int newCaretIndex = foundIndex.getFirst();

        if (newCaretIndex >= 0) {
            scrollingTextField.moveCaret(newCaretIndex);
            //snap the handle to the caret
            Rect newCaretBounds = scrollingTextField.getBoundingBox(newCaretIndex);
            int newX = newCaretBounds.left + scrollingTextField.getPaddingLeft();
            int newY = newCaretBounds.bottom + scrollingTextField.getPaddingTop();

            _yoyo.attachYoyo(newX, newY);
        }

    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        int x = (int) e.getX() + scrollingTextField.getScrollX();
        int y = (int) e.getY() + scrollingTextField.getScrollY();

        //ignore taps on handle
        if (yoyoCaret.isInHandle(x, y) || yoyoStart.isInHandle(x, y) || yoyoEnd.isInHandle(x, y)) {
            return true;
        } else {
            isShowYoyoCaret = true;
            return super.onSingleTapUp(e);
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        int x = (int) e.getX() + scrollingTextField.getScrollX();
        int y = (int) e.getY() + scrollingTextField.getScrollY();

        //ignore taps on handle
        if (yoyoCaret.isInHandle(x, y)) {
            scrollingTextField.selectText(true);
            return true;
        } else if (yoyoStart.isInHandle(x, y)) {
            return true;
        } else {
            return super.onDoubleTap(e);
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        onDoubleTap(e);
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        if (isCaretHandleTouched || isStartHandleTouched || isEndHandleTouched) {
            onUp(e2);
            return true;
        } else {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public void onTextDrawComplete(Canvas canvas) {
        if (!scrollingTextField.isSelectText2()) {
            yoyoCaret.show();
            yoyoStart.hide();
            yoyoEnd.hide();

            if (!isCaretHandleTouched) {
                Rect caret = scrollingTextField.getBoundingBox(scrollingTextField.getCaretPosition());
                int x = caret.left + scrollingTextField.getPaddingLeft();
                int y = caret.bottom + scrollingTextField.getPaddingTop();
                yoyoCaret.setRestingCoord(x, y);
            }
            if (isShowYoyoCaret)
                yoyoCaret.draw(canvas);
            isShowYoyoCaret = false;
        } else {
            yoyoCaret.hide();
            yoyoStart.show();
            yoyoEnd.show();

            if (!(isStartHandleTouched && isEndHandleTouched)) {
                Rect caret = scrollingTextField.getBoundingBox(scrollingTextField.getSelectionStart());
                int x = caret.left + scrollingTextField.getPaddingLeft();
                int y = caret.bottom + scrollingTextField.getPaddingTop();
                yoyoStart.setRestingCoord(x, y);

                Rect caret2 = scrollingTextField.getBoundingBox(scrollingTextField.getSelectionEnd());
                int x2 = caret2.left + scrollingTextField.getPaddingLeft();
                int y2 = caret2.bottom + scrollingTextField.getPaddingTop();
                yoyoEnd.setRestingCoord(x2, y2);
            }

            yoyoStart.draw(canvas);
            yoyoEnd.draw(canvas);
        }
    }

    @Override
    public Rect getCaretBloat() {
        return yoyoCaret.HANDLE_BLOAT;
    }

    @Override
    public void onColorSchemeChanged(ColorScheme colorScheme) {
        // TODO: Implement this method
        yoyoCaret.setHandleColor(colorScheme.getColor(ColorScheme.Colorable.CARET_BACKGROUND));
    }

    private class Yoyo {
        private final int YOYO_STRING_RESTING_HEIGHT = _yoyoSize / 3;
        private final Rect HANDLE_RECT = new Rect(0, 0, _yoyoSize, _yoyoSize);
        public final Rect HANDLE_BLOAT;

        //coordinates where the top of the yoyo string is attached
        private int _anchorX = 0;
        private int _anchorY = 0;

        //coordinates of the top-left corner of the yoyo handle
        private int _handleX = 0;
        private int _handleY = 0;

        //the offset where the handle is first touched,
        //(0,0) being the top-left of the handle
        private int _xOffset = 0;
        private int _yOffset = 0;

        private final Paint _brush;

        private boolean _isShow;

        public Yoyo() {
            int radius = getRadius();
            HANDLE_BLOAT = new Rect(
                    radius,
                    0,
                    0,
                    HANDLE_RECT.bottom + YOYO_STRING_RESTING_HEIGHT);

            _brush = new Paint();
            _brush.setColor(scrollingTextField.getColorScheme().getColor(ColorScheme.Colorable.CARET_BACKGROUND));
            //,_brush.setStrokeWidth(2);
            _brush.setAntiAlias(true);
        }

        public void setHandleColor(int color) {
            // TODO: Implement this method
            _brush.setColor(color);
        }

        /**
         * Draws the yoyo handle and string. The Yoyo handle can extend into
         * the padding region.
         *
         * @param canvas
         */
        public void draw(Canvas canvas) {
            int radius = getRadius();

            canvas.drawLine(_anchorX, _anchorY,
                    _handleX + radius, _handleY + radius, _brush);
            canvas.drawArc(new RectF(_anchorX - radius, _anchorY - radius / 2 - YOYO_STRING_RESTING_HEIGHT,
                    _handleX + radius * 2, _handleY + radius / 2), 60, 60, true, _brush);
            canvas.drawOval(new RectF(_handleX, _handleY, _handleX + HANDLE_RECT.right, _handleY + HANDLE_RECT.bottom), _brush);
        }

        final public int getRadius() {
            return HANDLE_RECT.right / 2;
        }

        /**
         * Clear the yoyo at the current position and attaches it to (x, y),
         * with the handle hanging directly below.
         */
        public void attachYoyo(int x, int y) {
            invalidateYoyo(); //clear old position
            setRestingCoord(x, y);
            invalidateYoyo(); //update new position
        }


        /**
         * Sets the yoyo string to be attached at (x, y), with the handle
         * hanging directly below, but does not trigger any redrawing
         */
        public void setRestingCoord(int x, int y) {
            _anchorX = x;
            _anchorY = y;
            _handleX = x - getRadius();
            _handleY = y + YOYO_STRING_RESTING_HEIGHT;
        }

        private void invalidateYoyo() {
            int handleCenter = _handleX + getRadius();
            int x0, x1, y0, y1;
            if (handleCenter >= _anchorX) {
                x0 = _anchorX;
                x1 = handleCenter + 1;
            } else {
                x0 = handleCenter;
                x1 = _anchorX + 1;
            }

            if (_handleY >= _anchorY) {
                y0 = _anchorY;
                y1 = _handleY;
            } else {
                y0 = _handleY;
                y1 = _anchorY;
            }

            //invalidate the string area
            scrollingTextField.invalidate(x0, y0, x1, y1);
            invalidateHandle();
        }

        public void invalidateHandle() {
            Rect handleExtent = new Rect(_handleX, _handleY,
                    _handleX + HANDLE_RECT.right, _handleY + HANDLE_RECT.bottom);
            scrollingTextField.invalidate(handleExtent);
        }

        /**
         * This method projects a yoyo string directly above the handle and
         * determines which character it should be attached to, or -1 if no
         * suitable character can be found.
         * <p>
         * (handleX, handleY) is the handle origin in screen coordinates,
         * where (0, 0) is the top left corner of the textField, regardless of
         * its internal scroll values.
         *
         * @return Pair.first contains the nearest character while Pair.second
         * is the exact character found by a strict search
         */
        public Pair findNearestChar(int handleX, int handleY) {
            int attachedLeft = screenToViewX(handleX) - _xOffset + getRadius();
            int attachedBottom = screenToViewY(handleY) - _yOffset - YOYO_STRING_RESTING_HEIGHT - 2;

            return new Pair(scrollingTextField.coordToCharIndex(attachedLeft, attachedBottom),
                    scrollingTextField.coordToCharIndexStrict(attachedLeft, attachedBottom));
        }

        /**
         * Records the coordinates of the initial down event on the
         * handle so that subsequent movement events will result in the
         * handle being offset correctly.
         * <p>
         * Does not check if isInside(x, y). Calling methods have
         * to ensure that (x, y) is within the handle area.
         */
        public void setInitialTouch(int x, int y) {
            _xOffset = x - _handleX;
            _yOffset = y - _handleY;
        }

        public void clearInitialTouch() {
            _xOffset = 0;
            _yOffset = 0;
        }

        public void show() {
            _isShow = true;
        }

        public void hide() {
            _isShow = false;
        }

        public boolean isInHandle(int x, int y) {
            return _isShow && (x >= _handleX
                    && x < (_handleX + HANDLE_RECT.right)
                    && y >= _handleY
                    && y < (_handleY + HANDLE_RECT.bottom)
            );
        }
    }//end inner class
}
