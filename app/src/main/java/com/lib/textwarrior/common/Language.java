/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.lib.textwarrior.common;

import com.xiaoyv.java.compile.utils.JarUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Base class for programming language syntax.
 * By default, C-like symbols and operators are included, but not keywords.
 */
public abstract class Language {
    public final static char EOF = '\uFFFF';
    public final static char NULL_CHAR = '\u0000';
    public final static char NEWLINE = '\n';
    public final static char BACKSPACE = '\b';
    public final static char TAB = '\t';
    public final static String GLYPH_NEWLINE = "\u21b5";
    public final static String GLYPH_SPACE = "\u00b7";
    public final static String GLYPH_TAB = "\u00bb";


    // 关键词
    private List<String> keywordMap = new ArrayList<>();

    // 第一个参数 class.getSimpleName() ,第二个 class.getName()
    private LinkedHashMap<String, String> identifierMap = new LinkedHashMap<>();

    // 用户自己定义的标识符
    private List<String> namesMap = new ArrayList<>();

    // 第一个参数 用户自定义标识符 ,第二个 标识符的类
    private List<String> defendMapKey = new ArrayList<>();
    private List<String> defendMapValue = new ArrayList<>();


    public List<String> getKeywords() {
        return keywordMap;
    }

    public void setKeywords(String[] keywords) {
        Collections.addAll(keywordMap, keywords);
    }

    public void clearIdentifier() {
        identifierMap.clear();
    }

    public LinkedHashMap<String, String> getIdentifier() {
        return identifierMap;
    }

    public void addIdentifier(String simpleName, String name) {
        if (identifierMap.containsKey(simpleName)) {
            return;
        }
        if (identifierMap.containsValue(name)) {
            return;
        }
        identifierMap.put(simpleName, name);
    }

    /**
     * 添加 jar 依赖的标识符数据
     *
     * @param filePath jar 路径
     */
    public void addRtIdentifier(String filePath) {
        List<Class> classes = JarUtils.getClasses(filePath);
        for (Class cls : classes) {
            String simpleName = cls.getSimpleName();
            String name = cls.getName();
            addIdentifier(simpleName, name);
        }
    }


    public List<String> getDefendMapKey() {
        return defendMapKey;
    }

    public void clearDefendMapKey() {
        defendMapKey.clear();
    }

    public void addDefendMapKey(String identifier) {
        defendMapKey.add(identifier);
    }







/*
    public void updateUserWord() {
        String[] uw = new String[userCache.size()];
        userWords = userCache.toArray(uw);
    }

    public String[] getUserWord() {
        return userWords;
    }


    public void clearUserWord() {
        userCache.clear();
    }

    public void addUserWord(String name) {
        if (!userCache.contains(name.trim()) && !namesMap.contains(name.trim()))
            userCache.add(name);
    }




    public String[] getNames() {
        return names;
    }


    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = new String[keywords.length];
        for (int i = 0; i < keywords.length; i++) {
            this.keywords[i] = keywords[i] + "[keywords]";
        }
    }

    public void setNames(String[] names) {
        this.names = names;
        ArrayList<String> buf = new ArrayList<>();
        namesMap.clear();
        for (String name : names) {
            if (!buf.contains(name)) {
                buf.add(name);
            }
            namesMap.add(name);
        }
        this.names = new String[buf.size()];
        buf.toArray(this.names);
    }

    public void addNames(String[] names) {
        String[] old = getNames();
        String[] news = new String[old.length + names.length];
        System.arraycopy(old, 0, news, 0, old.length);
        System.arraycopy(names, 0, news, old.length, names.length);
        setNames(news);*/

    /**
     * 空白符
     */
    public boolean isWhitespace(char c) {
        return (c == ' ' || c == '\n' || c == '\t' ||
                c == '\r' || c == '\f' || c == EOF);
    }

    /**
     * 点运算符
     */
    public boolean isSentenceTerminator(char c) {
        return (c == '.');
    }


    /**
     * 不表示类c语言的派生类应返回false；否则返回true
     */
    public boolean isProLang() {
        return true;
    }

}
