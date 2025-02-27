package org.lexer;

import java.util.Vector;

public class Lexer {

	String input;
	private int position;
	private int readPos;
	private byte curr;
	private Vector<Token> tokens;
	private boolean debug;

	public Lexer(String input, boolean debug) {
		this.input = input;
		this.position = 0;
		this.readPos = 0;
		this.readChar();
		this.tokens = new Vector<Token>();
		this.debug = debug;
	}

	void readChar() {
		if (this.readPos >= this.input.length()) {
			this.curr = 0;
		} else {
			this.curr = (byte) this.input.charAt(this.readPos);
		}
		this.position = this.readPos;
		this.readPos++;
	}

	char peekChar() {
		if (this.readPos >= this.input.length()) {
			return 0;
		} else {
			return this.input.charAt(this.readPos);
		}

	}

	public Token nextToken() {
		Token tok = null;

		while (this.curr == ' ' || this.curr == '\t' || this.curr == '\n' || this.curr == '\r') {
			this.readChar();
		}

		switch ((char) this.curr) {
			case '=':
				if (this.peekChar() == '=') {
					byte ch = this.curr;
					this.readChar();
					tok = new Token(TokenList.EQ, String.valueOf((char) ch) + (char) this.curr);
				} else {
					tok = new Token(TokenList.ASSIGN, this.curr);
				}
				break;
			case ';':
				tok = new Token(TokenList.SEMICOLON, this.curr);
				break;

			case '(':
				tok = new Token(TokenList.PAREN_OPEN, this.curr);
				break;

			case ')':
				tok = new Token(TokenList.PAREN_CLOSE, this.curr);
				break;

			case ',':
				tok = new Token(TokenList.COMMA, this.curr);
				break;

			case '+':
				tok = new Token(TokenList.PLUS, this.curr);
				break;

			case '-':
				tok = new Token(TokenList.MINUS, this.curr);
				break;

			case '!':
				if (this.peekChar() == '=') {
					byte ch = this.curr;
					this.readChar();
					tok = new Token(TokenList.NOT_EQ,
							String.valueOf((char) ch) + (char) this.curr + "");
				} else {

					tok = new Token(TokenList.BANG, this.curr);
				}
				break;

			case '/':
				tok = new Token(TokenList.SLASH, this.curr);
				break;

			case '*':
				tok = new Token(TokenList.ASTERISK, this.curr);
				break;

			case '<':
				tok = new Token(TokenList.LT, this.curr);
				break;

			case '>':
				tok = new Token(TokenList.GT, this.curr);
				break;

			case '{':
				tok = new Token(TokenList.BRACE_OPEN, this.curr);
				break;

			case '}':
				tok = new Token(TokenList.BRACE_CLOSE, this.curr);
				break;

			case 0:
				tok = new Token(TokenList.EOF, TokenList.EOF);
				break;
			default:
				if (Token.isLetter((char) this.curr)) {
					int currPos = this.position;
					while (Token.isLetter((char) this.curr)) {
						this.readChar();
					}
					String identifier = this.input.substring(currPos, this.position);
					tok = new Token(Token.lookUpIdentifier(identifier), identifier);
					return tok;

				} else if (Token.isDigit((char) this.curr)) {
					int currPos = this.position;
					while (Token.isDigit((char) this.curr)) {
						this.readChar();
					}
					String number = this.input.substring(currPos, this.position);
					tok = new Token(TokenList.INT, number);
					return tok;
				} else {
					tok = new Token(TokenList.ILLEGAL, String.valueOf(this.curr));
				}

		}
		this.readChar();
		return tok;
	}

	public void tokenize() {
		Token tok = null;
		for (tok = this.nextToken(); !tok.getType().equals(TokenList.EOF); tok = this.nextToken()) {
			this.tokens.addElement(tok);
		}
		this.tokens.addElement(tok);
	}

	public Vector<Token> getTokens() {
		return this.tokens;
	}

	public void printTokens() {
		if (debug) {
			for (Token tok : this.tokens) {
				tok.printToken();
			}

		} else {
			// System.out.println("DEBUG set to FALSE");
		}
	}

	public static void main(String[] args) {
		System.out.println("Hello ");
		String testInput = "let five = 5;\nlet ten = 10;\nfunction add(x, y) {\n!-/*5;\n5 < 10 > 5;\n5 < 10 > 5;\nif (5 < 10) {\nreturn true;\n} else {\nreturn false;\n}\n};\nlet result = add(five, ten);\n10 == 10;\n10 != 9;\n";
		Lexer lx = new Lexer(testInput, true);
		lx.tokenize();
		Vector<Token> tokens = lx.getTokens();
		System.out.println("Created the lexer : " + lx);
		for (Token tok : tokens) {
			System.out.println(
					"Token Type : " + tok.getType() + "\nToken Value : "
							+ tok.getTokenValue() + "\n");
		}

	}

}
