/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.lib.textwarrior.common;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Does lexical analysis of a text for C-like languages.
 * The programming language syntax used is set as a static class variable.
 */
public class Lexer {
    public static final int LEXER_NORMAL = 0;
    public static final int LEXER_KEYWORD = 1;
    public static final int LEXER_COMMENT = 2;
    public static final int LEXER_STRING = 3;
    public static final int LEXER_NUMBER = 4;
    public static final int LEXER_IDENTIFIER = 5;
    public static final int LEXER_OPERATOR = 6;
    public static final int LEXER_SYMBOL = 7;
    public static final int LEXER_OTHER = 8;

    private static Language globalLanguage = LanguageNonPro.getInstance();

    private DocumentProvider documentProvider;
    private LexThread lexThread = null;
    private LexCallback lexCallback;


    synchronized public static void setLanguage(Language lang) {
        globalLanguage = lang;
    }

    synchronized public static Language getLanguage() {
        return globalLanguage;
    }


    public Lexer(LexCallback callback) {
        lexCallback = callback;
    }

    public void tokenize(DocumentProvider documentProvider) {
        if (!Lexer.getLanguage().isProLang()) {
            return;
        }

        setDocument(new DocumentProvider(documentProvider));
        if (lexThread == null) {
            lexThread = new LexThread(this);
            lexThread.start();
        } else {
            lexThread.restart();
        }
    }

    private void tokenizeDone(List<Pair> pairList) {
        if (lexCallback != null) {
            lexCallback.lexDone(pairList);
        }
        lexThread = null;
    }

    public void cancelTokenize() {
        if (lexThread != null) {
            lexThread.abort();
            lexThread = null;
        }
    }

    public synchronized void setDocument(DocumentProvider document) {
        documentProvider = document;
    }

    public synchronized DocumentProvider getDocument() {
        return documentProvider;
    }


    private class LexThread extends Thread {
        private boolean rescan = false;
        private final Lexer lexManager;
        // 可由另一线程设置以立即停止扫描
        private final Flag abortFlag;
        // Pair.first 是tokenize的起始位置，Pair.second 是tokenize的类型
        private List<Pair> pairList;

        public LexThread(Lexer lexer) {
            lexManager = lexer;
            abortFlag = new Flag();
        }

        @Override
        public void run() {
            do {
                rescan = false;
                abortFlag.clear();
                tokenize();
            }
            while (rescan);

            if (!abortFlag.isSet()) {
                // 词法分析完成
                lexManager.tokenizeDone(pairList);
            }
        }

        public void restart() {
            rescan = true;
            abortFlag.set();
        }

        public void abort() {
            abortFlag.set();
        }

        public void tokenize() {
            DocumentProvider document = getDocument();
            Language language = Lexer.getLanguage();
            List<Pair> tokenPairs = new ArrayList<>();

            // 如果不是程序语言则退出
            if (!language.isProLang()) {
                tokenPairs.add(new Pair(0, LEXER_NORMAL));
                pairList = tokenPairs;
                return;
            }
            // 获取当前的代码字符串
            StringReader stringReader = new StringReader(document.toString());
            // 创建一个词法分析器
            JavaLexer javaLexer = new JavaLexer(stringReader);
            // 初始化词法类型
            JavaType javaType = null;
            // 索引
            int idx = 0;
            // 存储标识符
            String identifier = null;
            // 存储标识符类型
            String identifierType = null;
            // 清除用户自定义标识符
            language.clearDefendMapKey();

            while (javaType != JavaType.EOF) {
                try {
                    javaType = javaLexer.yylex();
                    switch (javaType) {
                        case KEYWORD:                // 关键字
                        case NULL_LITERAL:           // null 常量
                        case BOOLEAN_LITERAL:        // boolean 常量
                            tokenPairs.add(new Pair(idx, LEXER_KEYWORD));
                            break;
                        // 注释
                        case COMMENT:
                            tokenPairs.add(new Pair(idx, LEXER_COMMENT));
                            break;
                        case STRING:                   // 字符串
                        case CHARACTER_LITERAL:        // 字符
                            tokenPairs.add(new Pair(idx, LEXER_STRING));
                            break;
                        // 数字
                        case INTEGER_LITERAL:
                        case FLOATING_POINT_LITERAL:
                            tokenPairs.add(new Pair(idx, LEXER_NUMBER));
                            break;
                        case EOF:                    // EOF
                            tokenPairs.add(new Pair(idx, LEXER_IDENTIFIER));
                            break;
                        // 标识符
                        case IDENTIFIER:
                            identifier = javaLexer.yytext();
                            tokenPairs.add(new Pair(idx, LEXER_IDENTIFIER));
                            break;
                        // 标点
                        case LPAREN:       // (
                        case LBRACK:       // [
                        case RBRACK:       // ]
                        case LBRACE:       // {
                        case RBRACE:       // }
                        case LT:           // <
                        case GT:           // >
                        case COMMA:        // ,
                        case COMP:         // ~
                        case COLON:        // :
                        case DOT:          // .
                        case SEMICOLON:    // ;
                        case EQ:           // =
                        case RPAREN:       // )
                        case WHITESPACE:             // 空格
                            // 标识符
                            tokenPairs.add(new Pair(idx, LEXER_SYMBOL));
                            if (identifier != null) {
                                language.addDefendMapKey(identifier);
                                identifier = null;
                            }
                            break;
                        // 运算符
                        case PLUS:         // +
                        case MINUS:        // -
                        case PLUSPLUS:     // ++
                        case PLUSEQ:       // +=
                        case MINUSMINUS:   // --
                        case MINUSEQ:      // -=
                        case DIV:          // /
                        case DIVEQ:        // /=
                        case MULT:         // *
                        case MULTEQ:       // *=
                        case MOD:          // %
                        case MODEQ:        // %=
                        case OR:           // |
                        case OROR:         // ||
                        case OREQ:         // |=
                        case XOR:          // ^
                        case XOREQ:        // ^=
                        case LTEQ:         // <=
                        case GTEQ:         // >=
                        case AND:          // &
                        case ANDAND:       // &&
                        case ANDEQ:        // &=
                        case NOT:          // !
                        case NOTEQ:        // !=
                        case QUESTION:     // ?
                        case LSHIFT:       // <<
                        case LSHIFTEQ:     // <<=
                        case RSHIFT:       // >>
                        case URSHIFT:      // >>>
                        case RSHIFTEQ:     // >>=
                        case URSHIFTEQ:    // >>>=
                            tokenPairs.add(new Pair(idx, LEXER_OPERATOR));
                            break;
                        // 其余符号默认
                        default:
                            tokenPairs.add(new Pair(idx, LEXER_OTHER));
                            break;
                    }
                    idx += javaLexer.yytext().length();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 错误了，索引也要往后挪
                    idx++;
                }
            }

            if (tokenPairs.isEmpty()) {
                // 禁止返回空值，填充默认值
                tokenPairs.add(new Pair(0, LEXER_NORMAL));
            }
            pairList = tokenPairs;
        }
    }


    public interface LexCallback {
        void lexDone(List<Pair> results);
    }
}
