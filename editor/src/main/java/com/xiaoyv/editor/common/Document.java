/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.xiaoyv.editor.common;

import java.util.ArrayList;

/**
 * TextBuffer的装饰器，增加了自动换行功能。
 * <p>
 * Positions for word wrap row breaks are stored here.
 * Word-wrap is enabled by default.
 */
public class Document extends TextBuffer {

    private boolean isWordWrap = false;

    /**
     * 包含与字符打印，显示大小等有关的信息
     */
    private TextFieldMetrics fieldMetrics;

    /**
     * 包含文档中每一行的字符偏移量的表。
     * 值仅在自动换行模式下有效
     */
    private ArrayList<Integer> rowTable;

    public Document(TextFieldMetrics metrics) {
        super();
        fieldMetrics = metrics;
        resetRowTable();
    }

    public void setText(CharSequence text) {
        if (text == null) text = "";
        int lineCount = 1;
        int len = text.length();
        char[] ca = new char[memoryNeeded(len)];
        for (int i = 0; i < len; i++) {
            ca[i] = text.charAt(i);
            if (text.charAt(i) == '\n')
                lineCount++;
        }
        setBuffer(ca, len, lineCount);
    }

    private void resetRowTable() {
        ArrayList<Integer> rowTable = new ArrayList<Integer>();
        rowTable.add(0); //every document contains at least 1 row
        this.rowTable = rowTable;
    }

    public void setMetrics(TextFieldMetrics metrics) {
        fieldMetrics = metrics;
    }

    /**
     * Enable/disable word wrap. If enabled, the document is immediately
     * analyzed for word wrap breakpoints, which might take an arbitrarily long time.
     */
    public void setWordWrap(boolean enable) {
        if (enable && !isWordWrap) {
            isWordWrap = true;
            analyzeWordWrap();
        } else if (!enable && isWordWrap) {
            isWordWrap = false;
            analyzeWordWrap();
        }
    }

    public boolean isWordWrap() {
        return isWordWrap;
    }


    @Override
    public synchronized void delete(int charOffset, int totalChars, long timestamp, boolean undoable) {
        super.delete(charOffset, totalChars, timestamp, undoable);

        int startRow = findRowNumber(charOffset);
        int analyzeEnd = findNextLineFrom(charOffset);
        updateWordWrapAfterEdit(startRow, analyzeEnd, -totalChars);
    }

    @Override
    public synchronized void insert(char[] c, int charOffset, long timestamp, boolean undoable) {
        super.insert(c, charOffset, timestamp, undoable);

        int startRow = findRowNumber(charOffset);
        int analyzeEnd = findNextLineFrom(charOffset + c.length);
        updateWordWrapAfterEdit(startRow, analyzeEnd, c.length);
    }

    @Override
    /**
     * Moves _gapStartIndex by displacement units. Note that displacement can be
     * negative and will move _gapStartIndex to the left.
     *
     * Only UndoStack should use this method to carry out a simple undo/redo
     * of insertions/deletions. No error checking is done.
     */
    synchronized void shiftGapStart(int displacement) {
        super.shiftGapStart(displacement);

        if (displacement != 0) {
            int startOffset = (displacement > 0)
                    ? _gapStartIndex - displacement
                    : _gapStartIndex;
            int startRow = findRowNumber(startOffset);
            int analyzeEnd = findNextLineFrom(_gapStartIndex);
            updateWordWrapAfterEdit(startRow, analyzeEnd, displacement);
        }
    }

    //No error checking is done on parameters.
    private int findNextLineFrom(int charOffset) {
        int lineEnd = logicalToRealIndex(charOffset);

        while (lineEnd < _contents.length) {
            // skip the gap
            if (lineEnd == _gapStartIndex) {
                lineEnd = _gapEndIndex;
            }

            if (_contents[lineEnd] == Language.NEWLINE ||
                    _contents[lineEnd] == Language.EOF) {
                break;
            }

            ++lineEnd;
        }

        return realToLogicalIndex(lineEnd) + 1;
    }

    private void updateWordWrapAfterEdit(int startRow, int analyzeEnd, int delta) {
        if (startRow > 0) {
            // if the first word becomes shorter or an inserted space breaks it
            // up, it may fit the previous line, so analyse that line too
            --startRow;
        }
        int analyzeStart = rowTable.get(startRow);

        //changes only affect the rows after startRow
        removeRowMetadata(startRow + 1, analyzeEnd - delta);
        adjustOffsetOfRowsFrom(startRow + 1, delta);
        analyzeWordWrap(startRow + 1, analyzeStart, analyzeEnd);
    }

    /**
     * Removes row offset info from fromRow to the row that endOffset is on,
     * inclusive.
     * <p>
     * No error checking is done on parameters.
     */
    private void removeRowMetadata(int fromRow, int endOffset) {
        while (fromRow < rowTable.size() &&
                rowTable.get(fromRow) <= endOffset) {
            rowTable.remove(fromRow);
        }
    }

    private void adjustOffsetOfRowsFrom(int fromRow, int offset) {
        for (int i = fromRow; i < rowTable.size(); ++i) {
            rowTable.set(i, rowTable.get(i) + offset);
        }
    }

    public void analyzeWordWrap() {

        resetRowTable();

        if (isWordWrap && !hasMinimumWidthForWordWrap()) {
            if (fieldMetrics.getRowWidth() > 0) {
                TextWarriorException.fail("Text field has non-zero width but still too small for word wrap");
            }
            // fieldMetrics.getRowWidth() might legitmately be zero when the text field has not been layout yet
            return;
        }

        analyzeWordWrap(1, 0, getTextLength());
    }

    private boolean hasMinimumWidthForWordWrap() {
        final int maxWidth = fieldMetrics.getRowWidth();
        //assume the widest char is 2ems wide
        return (maxWidth >= 2 * fieldMetrics.getAdvance('M'));
    }

    //No error checking is done on parameters.
    //A word consists of a sequence of 0 or more non-whitespace characters followed by
    //exactly one whitespace character. Note that EOF is considered whitespace.
    private void analyzeWordWrap(int rowIndex, int startOffset, int endOffset) {
        if (!isWordWrap) {
            int offset = logicalToRealIndex(startOffset);
            int end = logicalToRealIndex(endOffset);
            ArrayList<Integer> rowTable = new ArrayList<Integer>();

            while (offset < end) {
                // skip the gap
                if (offset == _gapStartIndex) {
                    offset = _gapEndIndex;
                }
                char c = _contents[offset];
                if (c == Language.NEWLINE) {
                    //start a new row
                    rowTable.add(realToLogicalIndex(offset) + 1);
                }
                ++offset;

            }
            this.rowTable.addAll(rowIndex, rowTable);
            return;
        }
        if (!hasMinimumWidthForWordWrap()) {
            TextWarriorException.fail("Not enough space to do word wrap");
            return;
        }

        ArrayList<Integer> rowTable = new ArrayList<Integer>();
        int offset = logicalToRealIndex(startOffset);
        int end = logicalToRealIndex(endOffset);
        int potentialBreakPoint = startOffset;
        int wordExtent = 0;
        final int maxWidth = fieldMetrics.getRowWidth();
        int remainingWidth = maxWidth;

        while (offset < end) {
            // skip the gap
            if (offset == _gapStartIndex) {
                offset = _gapEndIndex;
            }

            char c = _contents[offset];
            wordExtent += fieldMetrics.getAdvance(c);

            boolean isWhitespace = (c == ' ' || c == Language.TAB
                    || c == Language.NEWLINE || c == Language.EOF);

            if (isWhitespace) {
                //full word obtained
                if (wordExtent <= remainingWidth) {
                    remainingWidth -= wordExtent;
                } else if (wordExtent > maxWidth) {
                    //handle a word too long to fit on one row
                    int current = logicalToRealIndex(potentialBreakPoint);
                    remainingWidth = maxWidth;

                    //start the word on a new row, if it isn't already
                    if (potentialBreakPoint != startOffset && (rowTable.isEmpty() ||
                            potentialBreakPoint != rowTable.get(rowTable.size() - 1))) {
                        rowTable.add(potentialBreakPoint);
                    }

                    while (current <= offset) {
                        // skip the gap
                        if (current == _gapStartIndex) {
                            current = _gapEndIndex;
                        }

                        int advance = fieldMetrics.getAdvance(_contents[current]);
                        if (advance > remainingWidth) {
                            rowTable.add(realToLogicalIndex(current));
                            remainingWidth = maxWidth - advance;
                        } else {
                            remainingWidth -= advance;
                        }

                        ++current;
                    }
                } else {
                    //invariant: potentialBreakPoint != startOffset
                    //put the word on a new row
                    rowTable.add(potentialBreakPoint);
                    remainingWidth = maxWidth - wordExtent;
                }

                wordExtent = 0;
                potentialBreakPoint = realToLogicalIndex(offset) + 1;
            }

            if (c == Language.NEWLINE) {
                //start a new row
                rowTable.add(potentialBreakPoint);
                remainingWidth = maxWidth;
            }

            ++offset;
        }

        //merge with existing row table
        this.rowTable.addAll(rowIndex, rowTable);
    }

    public String getRow(int rowNumber) {

        int rowSize = getRowSize(rowNumber);
        if (rowSize == 0) {
            return new String();
        }

        int startIndex = rowTable.get(rowNumber);
        return subSequence(startIndex, rowSize).toString();
    }

    public int getRowSize(int rowNumber) {

        if (isInvalidRow(rowNumber)) {
            return 0;
        }

        if (rowNumber != (rowTable.size() - 1)) {
            return rowTable.get(rowNumber + 1) - rowTable.get(rowNumber);
        } else {
            //last row
            return getTextLength() - rowTable.get(rowNumber);
        }
    }

    public int getRowCount() {

        return rowTable.size();
    }

    public int getRowOffset(int rowNumber) {


        if (isInvalidRow(rowNumber)) {
            return -1;
        }

        return rowTable.get(rowNumber);
    }

    /**
     * Get the row number that charOffset is on
     *
     * @return The row number that charOffset is on, or -1 if charOffset is invalid
     */
    public int findRowNumber(int charOffset) {

        if (!isValid(charOffset)) {
            return -1;
        }

        // rowTable的二进制搜索
        int right = rowTable.size() - 1;
        int left = 0;
        while (right >= left) {
            int mid = (left + right) / 2;
            int nextLineOffset = ((mid + 1) < rowTable.size()) ? rowTable.get(mid + 1) : getTextLength();
            if (charOffset >= rowTable.get(mid) && charOffset < nextLineOffset) {
                return mid;
            }

            if (charOffset >= nextLineOffset) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // should not be here
        return -1;
    }


    protected boolean isInvalidRow(int rowNumber) {
        return rowNumber < 0 || rowNumber >= rowTable.size();
    }


    public interface TextFieldMetrics {
        /**
         * Returns printed width of c.
         *
         * @param c Character to measure
         * @return Advance of character, in pixels
         */
        int getAdvance(char c);

        /**
         * Returns the maximum width available for a row of text to be layout. This
         * should not be larger than the width of the text field.
         *
         * @return Maximum width of a row, in pixels
         */
        int getRowWidth();
    }
}
