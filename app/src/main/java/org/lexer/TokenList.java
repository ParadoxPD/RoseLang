package org.lexer;

import java.util.Map;
import java.util.AbstractMap;

/**
 * TokenList
 */
public interface TokenList {
	String ILLEGAL = "ILLEGAL";
	String EOF = "EOF";

	String IDENTIFIER = "IDENTIFIER";

	String INT = "INT";

	String ASSIGN = "=";
	String PLUS = "+";
	String MINUS = "-";
	String BANG = "!";
	String ASTERISK = "*";
	String SLASH = "/";

	String LT = "<";
	String GT = ">";

	String EQ = "==";
	String NOT_EQ = "!=";

	String COMMA = ",";
	String SEMICOLON = ";";

	String PAREN_OPEN = "(";
	String PAREN_CLOSE = ")";

	String BRACE_OPEN = "{";
	String BRACE_CLOSE = "}";

	String LET = "LET";
	String CONST = "CONST";
	String RETURN = "RETURN";
	String FUNCTION = "FUNCTION";
	String IF = "IF";
	String ELSE = "ELSE";
	String TRUE = "TRUE";
	String FALSE = "FALSE";

	Map<String, String> KEYWORDS = Map.ofEntries(
			new AbstractMap.SimpleEntry<String, String>("function", "FUNCTION"),
			new AbstractMap.SimpleEntry<String, String>("let", "LET"),
			new AbstractMap.SimpleEntry<String, String>("const", "CONST"),
			new AbstractMap.SimpleEntry<String, String>("true", "TRUE"),
			new AbstractMap.SimpleEntry<String, String>("false", "FALSE"),
			new AbstractMap.SimpleEntry<String, String>("if", "IF"),
			new AbstractMap.SimpleEntry<String, String>("else", "ELSE"),
			new AbstractMap.SimpleEntry<String, String>("return", "RETURN"));

}
