package org.lexer;

import java.util.Vector;
import org.debugger.Debugger;

public class Lexer {

  String input;
  private int position;
  private int readPos;
  private byte curr;
  private Vector<Token> tokens;
  private Debugger debugger;

  public Lexer(String input, Debugger debugger) {
    this.input = input;
    this.position = 0;
    this.readPos = 0;
    this.readChar();
    this.tokens = new Vector<Token>();
    this.debugger = debugger;
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
      case ':':
        tok = new Token(TokenList.COLON, this.curr);
        break;

      case '.':
        tok = new Token(TokenList.DOT, this.curr);
        break;

      case '(':
        tok = new Token(TokenList.PAREN_OPEN, this.curr);
        break;

      case ')':
        tok = new Token(TokenList.PAREN_CLOSE, this.curr);
        break;
      case '[':
        tok = new Token(TokenList.SQUARE_BRACKET_OPEN, this.curr);
        break;

      case ']':
        tok = new Token(TokenList.SQUARE_BRACKET_CLOSE, this.curr);
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

      case '^':
        tok = new Token(TokenList.CHARAT, this.curr);
        break;

      case '!':
        if (this.peekChar() == '=') {
          byte ch = this.curr;
          this.readChar();
          tok = new Token(TokenList.NOT_EQ, String.valueOf((char) ch) + (char) this.curr + "");
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
        if (this.peekChar() == '=') {
          byte ch = this.curr;
          this.readChar();
          tok = new Token(TokenList.LTE, String.valueOf((char) ch) + (char) this.curr);
        } else {
          tok = new Token(TokenList.LT, this.curr);
        }

        break;

      case '>':
        if (this.peekChar() == '=') {
          byte ch = this.curr;
          this.readChar();
          tok = new Token(TokenList.GTE, String.valueOf((char) ch) + (char) this.curr);
        } else {
          tok = new Token(TokenList.GT, this.curr);
        }

        break;

      case '{':
        tok = new Token(TokenList.BRACE_OPEN, this.curr);
        break;

      case '}':
        tok = new Token(TokenList.BRACE_CLOSE, this.curr);
        break;
      case '"':
        tok = new Token(TokenList.STRING, this.readString());
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

          if ((char) this.curr == '.') {
            System.out.println("Float");
            this.readChar();
            while (Token.isDigit((char) this.curr)) {
              this.readChar();
            }
            number = this.input.substring(currPos, this.position);
            tok = new Token(TokenList.FLOAT, number);

          } else {

            tok = new Token(TokenList.INT, number);
          }
          return tok;
        } else {
          tok = new Token(TokenList.ILLEGAL, String.valueOf(this.curr));
        }
    }
    this.readChar();
    return tok;
  }

  String readString() {
    int pos = this.readPos;
    while (true) {
      this.readChar();
      if (this.curr == '"' || this.curr == 0) {
        break;
      }
    }
    return this.input.substring(pos, this.position);
  }

  public void tokenize() {
    Token tok = this.nextToken();
    while (!tok.getType().equals(TokenList.EOF)) {
      this.tokens.addElement(tok);
      tok = this.nextToken();
      if (tok.getType().equals(TokenList.ILLEGAL)) {
        System.out.println("Illegal Token : " + tok.printToken());
        break;
      }
    }
    this.tokens.addElement(new Token(TokenList.EOF, TokenList.EOF));
  }

  public Vector<Token> getTokens() {
    return this.tokens;
  }

  public void printTokens() {
    this.debugger.log("TOKENS: ", this.tokens);
  }
}
