package org.lexer;

public class Token {
	private TokenType type;
	private String tokenValue;

	Token(String type, String tokenValue) {
		this.type = new TokenType(type);
		this.tokenValue = tokenValue;
	}

	Token(TokenType type, String tokenValue) {
		this.type = type;
		this.tokenValue = tokenValue;
	}

	Token(String type, byte ch) {
		this.type = new TokenType(type);
		this.tokenValue = (char) ch + "";
	}

	public TokenType getType() {
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

	static TokenType lookUpIdentifier(String tok) {

		if (TokenList.KEYWORDS.containsKey(tok)) {
			return new TokenType(TokenList.KEYWORDS.get(tok));
		}
		return new TokenType(TokenList.IDENTIFIER);

	}

	public void printToken() {
		System.out.println("{Type: " + this.type.getType() + " Value: " + this.tokenValue + "}");
	}
}
