package org.parser;

import org.lexer.TokenList;

import java.util.Map;
import java.util.AbstractMap;

interface PrecedenceList {
	int LOWEST = 0;
	int EQUALS = 1;// ==
	int LESSGREATER = 2;// > or <
	int SUM = 3;// +
	int PRODUCT = 4;// *
	int PREFIX = 5;// -X or !X
	int CALL = 6; // func(x)

	Map<String, Integer> Precedences = Map.ofEntries(
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.EQ), EQUALS),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.NOT_EQ), EQUALS),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.LT), LESSGREATER),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.GT), LESSGREATER),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.PLUS), SUM),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.MINUS), SUM),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.SLASH), PRODUCT),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.PAREN_OPEN), CALL),
			new AbstractMap.SimpleEntry<String, Integer>((TokenList.ASTERISK), PRODUCT));

}
