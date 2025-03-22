package org.lexer;

public class Token {
	private String type;
	private String tokenValue;

	public Token(String type, String tokenValue) {
		this.type = (type);
		this.tokenValue = tokenValue;
	}

	public Token(String type, byte ch) {
		this.type = (type);
		this.tokenValue = (char) ch + "";
	}

	public String getType() {
		return this.type;
	}

	public String getTokenValue() {
		return this.tokenValue;
	}

	static boolean isLetter(char ch) {
		return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_';
	}

	static boolean isDigit(char ch) {
		return '0' <= ch && ch <= '9';
	}

	static String lookUpIdentifier(String tok) {

		if (TokenList.KEYWORDS.containsKey(tok)) {
			return (TokenList.KEYWORDS.get(tok));
		}
		return (TokenList.IDENTIFIER);

	}

	public void printToken() {
		System.out.println("{Type: " + this.getType() + " Value: " + this.tokenValue + "}");
	}
}
