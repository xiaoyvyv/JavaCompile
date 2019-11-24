/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */

package com.lib.textwarrior.common;

import java.util.HashMap;

public abstract class ColorScheme {
    public enum Colorable {
        FOREGROUND, BACKGROUND, SELECTION_FOREGROUND, SELECTION_BACKGROUND,
        CARET_FOREGROUND, CARET_BACKGROUND, CARET_DISABLED, LINE_HIGHLIGHT,
        NON_PRINTING_GLYPH, COMMENT, KEYWORD, LITERAL, NUMBER, OPERATOR,
        IDENTIFIER, SYMBOL, OTHER
    }

    private HashMap<Colorable, Integer> _colors = generateDefaultColors();

    // In ARGB format: 0xAARRGGBB
    private static final int COLOR_FOREGROUND = 0xFF4F5B66;
    private static final int COLOR_BACKGROUND = 0xFFEFF1F5;
    private static final int COLOR_SELECTION_FOREGROUND = 0xFFFFFFFF;
    private static final int COLOR_SELECTION_BACKGROUND = 0xFF708090;
    private static final int COLOR_CARET_FOREGROUND = 0xFFFFFFFF;
    private static final int COLOR_CARET_BACKGROUND = 0xFF00BFFF;
    private static final int COLOR_CARET_DISABLE = 0xFF808080;
    private static final int COLOR_LINE_HIGHLIGHT = 0x206E6E6E;
    private static final int COLOR_NON_PRINTING = 0xFFAAAAAA;
    private static final int COLOR_LITERAL = 0xFF81C784;
    private static final int COLOR_COMMENT = 0xFFA7ADBA;
    private static final int COLOR_KEYWORD = 0xFF8E67D2;
    private static final int COLOR_NUMBER = 0xFFf99157;
    private static final int COLOR_OPERATOR = 0xFFFFA500;
    private static final int COLOR_SYMBOL = 0xFF4F5B66;
    private static final int COLOR_IDENTIFIER = 0xFF4F5B66;
    private static final int COLOR_OTHER = 0xFFFF0000;

    private HashMap<Colorable, Integer> generateDefaultColors() {
        // 高对比度，黑白配色方案
        HashMap<Colorable, Integer> colors = new HashMap<>(Colorable.values().length);
        // 前景色
        colors.put(Colorable.FOREGROUND, COLOR_FOREGROUND);
        // 背景色
        colors.put(Colorable.BACKGROUND, COLOR_BACKGROUND);
        // 选中前景色
        colors.put(Colorable.SELECTION_FOREGROUND, COLOR_SELECTION_FOREGROUND);
        // 选中背景色
        colors.put(Colorable.SELECTION_BACKGROUND, COLOR_SELECTION_BACKGROUND);
        // 光标前景色
        colors.put(Colorable.CARET_FOREGROUND, COLOR_CARET_FOREGROUND);
        // 光标背景色
        colors.put(Colorable.CARET_BACKGROUND, COLOR_CARET_BACKGROUND);
        // 光标禁用色
        colors.put(Colorable.CARET_DISABLED, COLOR_CARET_DISABLE);
        // 高亮行颜色
        colors.put(Colorable.LINE_HIGHLIGHT, COLOR_LINE_HIGHLIGHT);
        // 非打印字形颜色（若显示空白字符、行号和回车字符等等）
        colors.put(Colorable.NON_PRINTING_GLYPH, COLOR_NON_PRINTING);
        // 注释颜色
        colors.put(Colorable.COMMENT, COLOR_COMMENT);
        // 关键字颜色
        colors.put(Colorable.KEYWORD, COLOR_KEYWORD);
        // 字符串颜色
        colors.put(Colorable.LITERAL, COLOR_LITERAL);
        // 数字颜色
        colors.put(Colorable.NUMBER, COLOR_NUMBER);
        // 标点符号颜色
        colors.put(Colorable.SYMBOL, COLOR_SYMBOL);
        // 运算符符颜色
        colors.put(Colorable.OPERATOR, COLOR_OPERATOR);
        // 标识符颜色
        colors.put(Colorable.IDENTIFIER, COLOR_IDENTIFIER);
        // 其他颜色
        colors.put(Colorable.OTHER, COLOR_OTHER);
        return colors;
    }

    /**
     * Whether this color scheme uses a dark background, like black or dark grey.
     */
    public abstract boolean isDark();


    // 颜色方案与标记类型的语义紧密耦合
    public int getTokenColor(int tokenType) {
        Colorable element;
        switch (tokenType) {
            case Lexer.LEXER_NORMAL:
                element = Colorable.FOREGROUND;
                break;
            case Lexer.LEXER_KEYWORD:
                element = Colorable.KEYWORD;
                break;
            case Lexer.LEXER_NUMBER:
                element = Colorable.NUMBER;
                break;
            case Lexer.LEXER_COMMENT:
                element = Colorable.COMMENT;
                break;
            case Lexer.LEXER_STRING:
                element = Colorable.LITERAL;
                break;
            case Lexer.LEXER_IDENTIFIER:
                element = Colorable.IDENTIFIER;
                break;
            case Lexer.LEXER_OPERATOR:
                element = Colorable.OPERATOR;
                break;
            case Lexer.LEXER_SYMBOL:
                element = Colorable.SYMBOL;
                break;
            case Lexer.LEXER_OTHER:
                element = Colorable.OTHER;
                break;
            default:
                TextWarriorException.fail("Invalid token type");
                element = Colorable.FOREGROUND;
                break;
        }
        return getColor(element);
    }

    public void setColor(Colorable colorable, int color) {
        _colors.put(colorable, color);
    }

    public int getColor(Colorable colorable) {
        Integer color = _colors.get(colorable);
        if (color == null) {
            TextWarriorException.fail("Color not specified for " + colorable);
            return 0;
        }
        return color;
    }


}
