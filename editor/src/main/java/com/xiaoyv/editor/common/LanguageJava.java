package com.xiaoyv.editor.common;

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
