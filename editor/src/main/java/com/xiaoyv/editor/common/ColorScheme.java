package com.xiaoyv.editor.common;

import java.util.HashMap;

public abstract class ColorScheme {
    public enum ColorType {
        FOREGROUND, BACKGROUND, SELECTION_FOREGROUND, SELECTION_BACKGROUND,
        CARET_FOREGROUND, CARET_BACKGROUND, CARET_DISABLED, LINE_HIGHLIGHT,
        NON_PRINTING_GLYPH, COMMENT, KEYWORD, LITERAL, NUMBER, OPERATOR,
        IDENTIFIER, SYMBOL, OTHER
    }

    private HashMap<ColorType, Integer> colors = generateDefaultColors();

    // In ARGB format: 0xAARRGGBB
    public static final int COLOR_FOREGROUND = 0xFF4F5B66;
    public static final int COLOR_BACKGROUND = 0xFFEFF1F5;
    public static final int COLOR_SELECTION_FOREGROUND = 0xFFFFFFFF;
    public static final int COLOR_SELECTION_BACKGROUND = 0xFF708090;
    public static final int COLOR_CARET_FOREGROUND = 0xFFFFFFFF;
    public static final int COLOR_CARET_BACKGROUND = 0xFF00BFFF;
    public static final int COLOR_CARET_DISABLE = 0xFF808080;
    public static final int COLOR_LINE_HIGHLIGHT = 0x206E6E6E;
    public static final int COLOR_NON_PRINTING = 0xFFAAAAAA;
    public static final int COLOR_LITERAL = 0xFF81C784;
    public static final int COLOR_COMMENT = 0xFFA7ADBA;
    public static final int COLOR_KEYWORD = 0xFF8E67D2;
    public static final int COLOR_NUMBER = 0xFFf99157;
    public static final int COLOR_OPERATOR = 0xFFFFA500;
    public static final int COLOR_SYMBOL = 0xFF4F5B66;
    public static final int COLOR_IDENTIFIER = 0xFF4F5B66;
    public static final int COLOR_OTHER = 0xFFFF0000;

    private HashMap<ColorType, Integer> generateDefaultColors() {
        // 高对比度，黑白配色方案
        HashMap<ColorType, Integer> colors = new HashMap<>(ColorType.values().length);
        // 前景色
        colors.put(ColorType.FOREGROUND, COLOR_FOREGROUND);
        // 背景色
        colors.put(ColorType.BACKGROUND, COLOR_BACKGROUND);
        // 选中前景色
        colors.put(ColorType.SELECTION_FOREGROUND, COLOR_SELECTION_FOREGROUND);
        // 选中背景色
        colors.put(ColorType.SELECTION_BACKGROUND, COLOR_SELECTION_BACKGROUND);
        // 光标前景色
        colors.put(ColorType.CARET_FOREGROUND, COLOR_CARET_FOREGROUND);
        // 光标背景色
        colors.put(ColorType.CARET_BACKGROUND, COLOR_CARET_BACKGROUND);
        // 光标禁用色
        colors.put(ColorType.CARET_DISABLED, COLOR_CARET_DISABLE);
        // 高亮行颜色
        colors.put(ColorType.LINE_HIGHLIGHT, COLOR_LINE_HIGHLIGHT);
        // 非打印字形颜色（若显示空白字符、行号和回车字符等等）
        colors.put(ColorType.NON_PRINTING_GLYPH, COLOR_NON_PRINTING);
        // 注释颜色
        colors.put(ColorType.COMMENT, COLOR_COMMENT);
        // 关键字颜色
        colors.put(ColorType.KEYWORD, COLOR_KEYWORD);
        // 字符串颜色
        colors.put(ColorType.LITERAL, COLOR_LITERAL);
        // 数字颜色
        colors.put(ColorType.NUMBER, COLOR_NUMBER);
        // 标点符号颜色
        colors.put(ColorType.SYMBOL, COLOR_SYMBOL);
        // 运算符符颜色
        colors.put(ColorType.OPERATOR, COLOR_OPERATOR);
        // 标识符颜色
        colors.put(ColorType.IDENTIFIER, COLOR_IDENTIFIER);
        // 其他颜色
        colors.put(ColorType.OTHER, COLOR_OTHER);
        return colors;
    }

    /**
     * Whether this color scheme uses a dark background, like black or dark grey.
     */
    public abstract boolean isDark();


    // 颜色方案与标记类型的语义紧密耦合
    public int getTokenColor(int tokenType) {
        ColorType element;
        switch (tokenType) {
            case Lexer.LEXER_NORMAL:
                element = ColorType.FOREGROUND;
                break;
            case Lexer.LEXER_KEYWORD:
                element = ColorType.KEYWORD;
                break;
            case Lexer.LEXER_NUMBER:
                element = ColorType.NUMBER;
                break;
            case Lexer.LEXER_COMMENT:
                element = ColorType.COMMENT;
                break;
            case Lexer.LEXER_STRING:
                element = ColorType.LITERAL;
                break;
            case Lexer.LEXER_IDENTIFIER:
                element = ColorType.IDENTIFIER;
                break;
            case Lexer.LEXER_OPERATOR:
                element = ColorType.OPERATOR;
                break;
            case Lexer.LEXER_SYMBOL:
                element = ColorType.SYMBOL;
                break;
            case Lexer.LEXER_OTHER:
                element = ColorType.OTHER;
                break;
            default:
                TextWarriorException.fail("Invalid token type");
                element = ColorType.FOREGROUND;
                break;
        }
        return getColor(element);
    }

    public void setColor(ColorType colorable, int color) {
        colors.put(colorable, color);
    }

    public int getColor(ColorType colorable) {
        Integer color = colors.get(colorable);
        if (color == null) {
            TextWarriorException.fail("Color not specified for " + colorable);
            return 0;
        }
        return color;
    }


}
