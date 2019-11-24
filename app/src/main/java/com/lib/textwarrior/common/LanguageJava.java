/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.lib.textwarrior.common;

public class LanguageJava extends Language {
    private static Language language = null;

    private final static String[] keywords = {
            "void", "boolean", "byte", "char", "short", "int", "long", "float", "double", "strictfp",
            "import", "package", "new", "class", "interface", "extends", "implements", "enum",
            "public", "private", "protected", "static", "abstract", "final", "native", "volatile",
            "assert", "try", "throw", "throws", "catch", "finally", "instanceof", "super", "this",
            "if", "else", "for", "do", "while", "switch", "case", "default",
            "continue", "break", "return", "synchronized", "transient",
            "true", "false", "null",
    };

    public static Language getInstance() {
        if (language == null) {
            language = new LanguageJava();
        }
        return language;
    }

    private LanguageJava() {
        super.setKeywords(keywords);
    }

}
