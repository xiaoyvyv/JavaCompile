/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */

package com.lib.textwarrior.common;


public class ColorSchemeDark extends ColorScheme {

    private static final int OFF_BLACK = 0xFF040404;
    private static final int OFF_WHITE = 0xFFFFFFFF;
    private static final int BEIGE = 0xFFDDDDDD;
    private static final int DARK_GREY = 0xFF606060;
    private static final int FLUORESCENT_YELLOW = 0x30FFFFFF;
    private static final int JUNGLE_GREEN = 0xFF608B4E;
    private static final int MARINE = 0xFF569CD6;
    private static final int OCEAN_BLUE = 0xFF256395;
    private static final int PEACH = 0xFFD69D85;

    public ColorSchemeDark() {
        setColor(Colorable.FOREGROUND, OFF_WHITE);
        setColor(Colorable.BACKGROUND, OFF_BLACK);
        setColor(Colorable.SELECTION_FOREGROUND, OFF_WHITE);
        setColor(Colorable.SELECTION_BACKGROUND, OCEAN_BLUE);
        setColor(Colorable.LINE_HIGHLIGHT, FLUORESCENT_YELLOW);
        setColor(Colorable.NON_PRINTING_GLYPH, DARK_GREY);
        setColor(Colorable.COMMENT, JUNGLE_GREEN);
        setColor(Colorable.KEYWORD, MARINE);
        setColor(Colorable.LITERAL, PEACH);
        setColor(Colorable.IDENTIFIER, BEIGE);
        setColor(Colorable.SYMBOL, BEIGE);
    }

    @Override
    public boolean isDark() {
        return true;
    }
}
