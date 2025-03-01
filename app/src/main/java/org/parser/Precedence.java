package org.parser;

import org.lexer.TokenList;

import java.util.Map;
import java.util.AbstractMap;

interface PrecedenceList {
	int LOWEST = 0;
	int ASSIGNMENT = 1;
	int EQUALS = 8;// ==
	int LESSGREATER = 9;// > or <
	int SUM = 10;// +
	int PRODUCT = 11;// *
	int EXPONENT = 12;
	int PREFIX = 14;// -X or !X
	int CALL = 16; // func(x)

	Map<String, Integer> Precedences = Map.ofEntries(
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.EQ), EQUALS),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.NOT_EQ), EQUALS),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.LTE), LESSGREATER),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.GTE), LESSGREATER),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.LT), LESSGREATER),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.GT), LESSGREATER),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.PLUS), SUM),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.MINUS), SUM),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.SLASH), PRODUCT),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.CHARAT), EXPONENT),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.PAREN_OPEN), CALL),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.ASSIGN), ASSIGNMENT),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.SQUARE_BRACKET_OPEN), CALL),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.ASTERISK), PRODUCT));

}
