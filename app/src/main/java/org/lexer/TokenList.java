package org.lexer;

import java.util.AbstractMap;
import java.util.Map;

/** TokenList */
public interface TokenList {

    // Special
    String ILLEGAL = "ILLEGAL";
    String EOF = "EOF";

    // Identifiers
    String IDENTIFIER = "IDENTIFIER";

    // DataTypes
    String INT = "INT";
    String FLOAT = "FLOAT";
    String STRING = "STRING";

    // Mathemetical Operators
    String ASSIGN = "=";
    String PLUS = "+";
    String MINUS = "-";
    String BANG = "!";
    String ASTERISK = "*";
    String SLASH = "/";
    String CHARAT = "^";

    // Logical Operators
    String LT = "<";
    String GT = ">";
    String LTE = "<=";
    String GTE = ">=";
    String EQ = "==";
    String NOT_EQ = "!=";

    // Delemeters
    String COMMA = ",";
    String SEMICOLON = ";";
    String COLON = ":";
    String DOT = ".";

    // Separators
    String PAREN_OPEN = "(";
    String PAREN_CLOSE = ")";

    String BRACE_OPEN = "{";
    String BRACE_CLOSE = "}";

    String SQUARE_BRACKET_OPEN = "[";
    String SQUARE_BRACKET_CLOSE = "]";

    // KEYWORDS
    String LET = "LET";
    String CONST = "CONST";
    String RETURN = "RETURN";
    String FUNCTION = "FUNCTION";
    String IF = "IF";
    String ELIF = "ELIF";
    String ELSE = "ELSE";
    String WHILE = "WHILE";
    String TRUE = "TRUE";
    String FALSE = "FALSE";

    Map<String, String> KEYWORDS =
            Map.ofEntries(
                    new AbstractMap.SimpleEntry<String, String>("function", "FUNCTION"),
                    new AbstractMap.SimpleEntry<String, String>("let", "LET"),
                    new AbstractMap.SimpleEntry<String, String>("const", "CONST"),
                    new AbstractMap.SimpleEntry<String, String>("true", "TRUE"),
                    new AbstractMap.SimpleEntry<String, String>("false", "FALSE"),
                    new AbstractMap.SimpleEntry<String, String>("if", "IF"),
                    new AbstractMap.SimpleEntry<String, String>("elif", "ELIF"),
                    new AbstractMap.SimpleEntry<String, String>("else", "ELSE"),
                    new AbstractMap.SimpleEntry<String, String>("while", "WHILE"),
                    new AbstractMap.SimpleEntry<String, String>("return", "RETURN"));

    String NEWLINE = "n";
    String HASH = "#";
    String SPACE = " ";
    String CARRIEAGERETURN = "r";
    String TAB = "t";
}
