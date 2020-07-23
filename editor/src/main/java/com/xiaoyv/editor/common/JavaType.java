package com.xiaoyv.editor.common;

public enum JavaType {
    EOF,
    IDENTIFIER,// 标识符
    KEYWORD, // 关键字
    COMMENT, // 注释
    CHARACTER_LITERAL,// 字符
    STRING, // 字符串
    INTEGER_LITERAL, // 整数
    FLOATING_POINT_LITERAL, // 浮点
    COMMA, // 逗号
    SEMICOLON,// 分号
    RBRACK, // 右中括号
    LBRACK,// 左中括号
    LPAREN,// 左括号
    RPAREN,// 右括号
    RBRACE, // 右大括号
    LBRACE, // 左大括号
    DOT, // 点
    WHITESPACE, // 空格
    DIV,
    MULT,
    MINUS,
    EQ,
    GT,
    NOT,
    LT,
    COMP,
    QUESTION, // ?
    COLON,
    OR,// |
    OROR, // ||
    PLUS,
    PLUSPLUS,// ++
    AND, // &
    ANDAND, // &&
    XOR, //异或
    MOD,
    DIVEQ,
    MULTEQ,
    URSHIFTEQ,
    NULL_LITERAL,// null常量
    BOOLEAN_LITERAL, // boolean常量
    LSHIFTEQ,
    URSHIFT,
    RSHIFTEQ,
    MODEQ,
    XOREQ,
    MINUSMINUS,
    MINUSEQ,
    EQEQ,
    GTEQ,
    RSHIFT,
    LTEQ,
    LSHIFT,
    NOTEQ,
    ANDEQ,
    OREQ,
    PLUSEQ
}
