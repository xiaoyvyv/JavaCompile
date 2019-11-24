/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.lib.textwarrior.common;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Iterator class to access characters of the underlying text buffer.
 * <p>
 * The usage procedure is as follows:
 * 1. Call seekChar(offset) to mark the position to start iterating
 * 2. Call hasNext() to see if there are any more char
 * 3. Call next() to get the next char
 * <p>
 * If there is more than 1 DocumentProvider pointing to the same Document,
 * changes made by one DocumentProvider will not cause other DocumentProviders
 * to be notified. Implement a publish/subscribe interface if required.
 */
public class DocumentProvider implements CharSequence {

    /**
     * Current position in the text. Range [ 0, theText.getTextLength() )
     */
    private int currIndex;
    private final Document theText;

    public DocumentProvider(Document.TextFieldMetrics metrics) {
        currIndex = 0;
        theText = new Document(metrics);
    }

    public DocumentProvider(Document doc) {
        currIndex = 0;
        theText = doc;
    }

    public DocumentProvider(DocumentProvider rhs) {
        currIndex = 0;
        theText = rhs.theText;
    }

    @Override
    public int length() {
        // TODO: Implement this method
        return theText.length();
    }

    /**
     * 从charOffset开始，获取最大为maxChars长度的子字符串
     */
    @NonNull
    public CharSequence subSequence(int charOffset, int maxChars) {
        return theText.subSequence(charOffset, maxChars);
    }

    public char charAt(int charOffset) {
        if (theText.isValid(charOffset)) {
            return theText.charAt(charOffset);
        } else {
            return Language.NULL_CHAR;
        }
    }

    public String getRow(int rowNumber) {
        return theText.getRow(rowNumber);
    }

    /**
     * Get the row number that charOffset is on
     */
    public int findRowNumber(int charOffset) {
        return theText.findRowNumber(charOffset);
    }

    /**
     * Get the line number that charOffset is on. The difference between a line
     * and a row is that a line can be word-wrapped into many rows.
     */
    public int findLineNumber(int charOffset) {
        return theText.findLineNumber(charOffset);
    }

    /**
     * Get the offset of the first character on rowNumber
     */
    public int getRowOffset(int rowNumber) {
        return theText.getRowOffset(rowNumber);
    }


    /**
     * Get the offset of the first character on lineNumber. The difference
     * between a line and a row is that a line can be word-wrapped into many rows.
     */
    public int getLineOffset(int lineNumber) {
        return theText.getLineOffset(lineNumber);
    }

    /**
     * Sets the iterator to point at startingChar.
     * <p>
     * If startingChar is invalid, hasNext() will return false, and currIndex
     * will be set to -1.
     *
     * @return startingChar, or -1 if startingChar does not exist
     */
    public int seekChar(int startingChar) {
        if (theText.isValid(startingChar)) {
            currIndex = startingChar;
        } else {
            currIndex = -1;
        }
        return currIndex;
    }

    public boolean hasNext() {
        return (currIndex >= 0 &&
                currIndex < theText.getTextLength());
    }

    /**
     * Returns the next character and moves the iterator forward.
     * <p>
     * Does not do bounds-checking. It is the responsibility of the caller
     * to check hasNext() first.
     *
     * @return Next character
     */
    public char next() {
        char nextChar = theText.charAt(currIndex);
        ++currIndex;
        return nextChar;
    }

    /**
     * Inserts c into the document, shifting existing characters from
     * insertionPoint (inclusive) to the right
     * <p>
     * If insertionPoint is invalid, nothing happens.
     */
    public void insertBefore(char c, int insertionPoint, long timestamp) {
        if (!theText.isValid(insertionPoint)) {
            return;
        }

        char[] a = new char[1];
        a[0] = c;
        theText.insert(a, insertionPoint, timestamp, true);
    }

    /**
     * Inserts characters of cArray into the document, shifting existing
     * characters from insertionPoint (inclusive) to the right
     * <p>
     * If insertionPoint is invalid, nothing happens.
     */
    public void insertBefore(char[] cArray, int insertionPoint, long timestamp) {
        if (!theText.isValid(insertionPoint) || cArray.length == 0) {
            return;
        }

        theText.insert(cArray, insertionPoint, timestamp, true);
    }

    public void insert(int i, CharSequence s) {
        theText.insert(new char[]{s.charAt(0)}, i, System.nanoTime(), true);
    }

    /**
     * Deletes the character at deletionPoint index.
     * If deletionPoint is invalid, nothing happens.
     */
    public void deleteAt(int deletionPoint, long timestamp) {
        if (!theText.isValid(deletionPoint)) {
            return;
        }
        theText.delete(deletionPoint, 1, timestamp, true);
    }


    /**
     * Deletes up to maxChars number of characters starting from deletionPoint
     * If deletionPoint is invalid, or maxChars is not positive, nothing happens.
     */
    public void deleteAt(int deletionPoint, int maxChars, long time) {
        if (!theText.isValid(deletionPoint) || maxChars <= 0) {
            return;
        }
        int totalChars = Math.min(maxChars, theText.getTextLength() - deletionPoint);
        theText.delete(deletionPoint, totalChars, time, true);
    }

    /**
     * Returns true if the underlying text buffer is in batch edit mode
     */
    public boolean isBatchEdit() {
        return theText.isBatchEdit();
    }

    /**
     * Signals the beginning of a series of insert/delete operations that can be
     * undone/redone as a single unit
     */
    public void beginBatchEdit() {
        theText.beginBatchEdit();
    }

    /**
     * Signals the end of a series of insert/delete operations that can be
     * undone/redone as a single unit
     */
    public void endBatchEdit() {
        theText.endBatchEdit();
    }

    /**
     * Returns the number of rows in the document
     */
    public int getRowCount() {
        return theText.getRowCount();
    }

    /**
     * Returns the number of characters in the row specified by rowNumber
     */
    public int getRowSize(int rowNumber) {
        return theText.getRowSize(rowNumber);
    }

    /**
     * Returns the number of characters in the document, including the terminal
     * End-Of-File character
     */
    public int docLength() {
        return theText.getTextLength();
    }

    //TODO make thread-safe

    /**
     * Removes spans from the document.
     * Beware: Not thread-safe! Another thread may be modifying the same spans
     * returned from getSpans()
     */
    public void clearSpans() {
        theText.clearSpans();
    }

    /**
     * Beware: Not thread-safe!
     */
    public List<Pair> getSpans() {
        return theText.getSpans();
    }

    /**
     * Sets the spans to use in the document.
     * Spans are continuous sequences of characters that have the same format
     * like color, font, etc.
     *
     * @param spans A collection of Pairs, where Pair.first is the start
     *              position of the token, and Pair.second is the type of the token.
     */
    public void setSpans(List<Pair> spans) {
        theText.setSpans(spans);
    }

    public void setMetrics(Document.TextFieldMetrics metrics) {
        theText.setMetrics(metrics);
    }

    /**
     * 启用/禁用文档的自动换行。
     * 如果启用，则会立即分析文档中的换行断点，这可能需要任意长的时间。
     */
    public void setWordWrap(boolean enable) {
        theText.setWordWrap(enable);
    }

    public boolean isWordWrap() {
        return theText.isWordWrap();
    }

    /**
     * Analyze the document for word wrap break points. Does nothing if word
     * wrap is disabled for the document.
     */
    public void analyzeWordWrap() {
        theText.analyzeWordWrap();
    }

    public boolean canUndo() {
        return theText.canUndo();
    }

    public boolean canRedo() {
        return theText.canRedo();
    }

    public int undo() {
        return theText.undo();
    }

    public int redo() {
        return theText.redo();
    }

    @Override
    public String toString() {
        // TODO: Implement this method
        return theText.toString();
    }


}
