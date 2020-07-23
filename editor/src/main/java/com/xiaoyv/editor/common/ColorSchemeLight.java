/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */

package com.xiaoyv.editor.common;


/**
 * Off-black on off-white background color scheme
 */
public class ColorSchemeLight extends ColorScheme {

    public ColorSchemeLight() {
        // 前景色
        setColor(ColorType.FOREGROUND, OFF_BLACK);
        // 背景色
        setColor(ColorType.BACKGROUND, OFF_WHITE);
        // 选中前景色
        setColor(ColorType.SELECTION_FOREGROUND, OFF_WHITE);
        // 光标前景色
        setColor(ColorType.CARET_FOREGROUND, OFF_WHITE);
    }

    private static final int OFF_WHITE = 0xFFFFFFFF;
    private static final int OFF_BLACK = 0xFF4F5B66;

    @Override
    public boolean isDark() {
        return false;
    }
}
