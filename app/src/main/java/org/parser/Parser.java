package org.parser;

import org.lexer.Token;
import org.lexer.TokenList;
import org.error.ErrorList;
import org.error.ParserError;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

abstract class PrefixParser {

	abstract Expression parse();
}

abstract class InfixParser {

	// private Expression left;

	InfixParser() {
		// this.left = null;
	}

	// InfixParser(Expression left) {
	// this.left = left;
	// }

	// abstract Expression parse();

	abstract Expression parse(Expression left);

	// public Expression getLeft() {
	// return this.left;
	// }
}

public class Parser {

	private Vector<Token> tokens;
	private Vector<ParserError> errors;
	private Token curr = null;
	private Token peek = null;
	private boolean peekAvailable;
	private int currPos;
	private Map<String, PrefixParser> prefixParsers;
	private Map<String, InfixParser> infixParsers;
	private Program program;
	private boolean debug = false;

	public Parser(Vector<Token> tokens, boolean debug) {
		this.tokens = tokens;
		this.currPos = 0;
		this.errors = new Vector<ParserError>();
		this.peekAvailable = true;
		this.program = null;
		this.debug = debug;

		this.nextToken();
		this.nextToken();

		this.prefixParsers = new HashMap<String, PrefixParser>();
		this.infixParsers = new HashMap<String, InfixParser>();
		this.registerAllParsers();

	}

	void registerAllParsers() {
		PrefixParser integerParser = new PrefixParser() {
			@Override
			public Expression parse() {
				IntegerLiteral lit = new IntegerLiteral(curr);

				// TODO: ADD ERROR CHECKING FOR INTEGER VALUE
				int val = Integer.parseInt(curr.getTokenValue());
				lit.setValue(val);
				return lit;

			}
		};
		PrefixParser idenParser = new PrefixParser() {
			@Override
			public Expression parse() {
				return new Identifier(curr, curr.getTokenValue());
			}
		};

		PrefixParser prefixExpressionParser = new PrefixParser() {
			@Override
			Expression parse() {
				PrefixExpression exp = new PrefixExpression(curr, curr.getTokenValue());
				nextToken();
				exp.setRight(parseExpression(PrecedenceList.PREFIX));
				return exp;
			}
		};

		PrefixParser booleanParser = new PrefixParser() {
			@Override
			Expression parse() {
				return new BooleanLiteral(curr, currTokenIs(TokenList.TRUE));
			}
		};

		PrefixParser groupedExpressionParser = new PrefixParser() {
			@Override
			Expression parse() {
				nextToken();
				Expression exp = parseExpression(PrecedenceList.LOWEST);

				if (!expectPeek(TokenList.PAREN_CLOSE)) {
					return null;

				}
				return exp;
			}
		};

		PrefixParser ifExpressionParser = new PrefixParser() {
			@Override
			Expression parse() {
				IfExpression stm = new IfExpression(curr);
				if (!expectPeek(TokenList.PAREN_OPEN)) {
					return null;
				}

				nextToken();
				stm.setCondition(parseExpression(PrecedenceList.LOWEST));

				if (!expectPeek(TokenList.PAREN_CLOSE)) {
					return null;
				}
				if (!expectPeek(TokenList.BRACE_OPEN)) {
					return null;
				}
				stm.setConsequence(parseBlockStatement());

				if (peekTokenIs(TokenList.ELSE)) {
					nextToken();
					if (!expectPeek(TokenList.BRACE_OPEN)) {
						return null;
					}
					stm.setAlternative(parseBlockStatement());
				}

				return stm;
			}
		};
		PrefixParser functionParser = new PrefixParser() {
			@Override
			Expression parse() {
				FunctionLiteral fnt = new FunctionLiteral(curr);

				if (!expectPeek(TokenList.IDENTIFIER)) {
					return null;
				}
				fnt.setName(new Identifier(curr, curr.getTokenValue()));

				if (!expectPeek(TokenList.PAREN_OPEN)) {
					return null;
				}
				fnt.addParameters(parseFunctionParameters());

				if (!expectPeek(TokenList.BRACE_OPEN)) {
					return null;
				}
				fnt.addBody(parseBlockStatement());
				if (debug)
					fnt.print("Function : ");
				return fnt;

			}
		};

		this.registerPrefixParser((TokenList.IDENTIFIER), idenParser);
		this.registerPrefixParser((TokenList.INT), integerParser);
		this.registerPrefixParser((TokenList.BANG), prefixExpressionParser);
		this.registerPrefixParser((TokenList.MINUS), prefixExpressionParser);
		this.registerPrefixParser((TokenList.TRUE), booleanParser);
		this.registerPrefixParser((TokenList.FALSE), booleanParser);
		this.registerPrefixParser((TokenList.PAREN_OPEN), groupedExpressionParser);
		this.registerPrefixParser((TokenList.IF), ifExpressionParser);
		this.registerPrefixParser((TokenList.FUNCTION), functionParser);

		InfixParser infixParser = new InfixParser() {
			@Override
			Expression parse(Expression left) {
				InfixExpression exp = new InfixExpression(curr, curr.getTokenValue(), left);

				int precedence = currPrecedence();
				nextToken();
				exp.setRight(parseExpression(precedence));

				return exp;
			}

		};

		InfixParser callExpressionParser = new InfixParser() {
			@Override
			Expression parse(Expression function) {
				CallExpression exp = new CallExpression(curr, function);
				exp.addArguments(parseCallArguments());
				if (debug)
					exp.print("Call Exp: ");
				return exp;
			}
		};

		this.registerInfixParser((TokenList.PLUS), infixParser);
		this.registerInfixParser((TokenList.MINUS), infixParser);
		this.registerInfixParser((TokenList.SLASH), infixParser);
		this.registerInfixParser((TokenList.ASTERISK), infixParser);
		this.registerInfixParser((TokenList.EQ), infixParser);
		this.registerInfixParser((TokenList.NOT_EQ), infixParser);
		this.registerInfixParser((TokenList.LT), infixParser);
		this.registerInfixParser((TokenList.GT), infixParser);
		this.registerInfixParser((TokenList.PAREN_OPEN), callExpressionParser);

	}

	void registerPrefixParser(String type, PrefixParser parser) {
		this.prefixParsers.put(type, parser);
	}

	void registerInfixParser(String type, InfixParser parser) {
		this.infixParsers.put(type, parser);
	}

	int peekPrecedence() {
		if (this.peekAvailable) {
			if (PrecedenceList.Precedences.containsKey(this.peek.getType())) {

				return PrecedenceList.Precedences.get(this.peek.getType());
			} else {
				return PrecedenceList.LOWEST;
			}
		} else {
			return PrecedenceList.LOWEST;
		}
	}

	int currPrecedence() {
		if (PrecedenceList.Precedences.containsKey(this.curr.getType())) {

			return PrecedenceList.Precedences.get(this.curr.getType());
		} else {
			return PrecedenceList.LOWEST;
		}
	}

	void nextToken() {
		if (!this.tokens.get(this.currPos).getType().equals(TokenList.EOF)) {
			this.curr = this.peek;
			this.peek = this.tokens.get(this.currPos++);
		} else {
			this.curr = this.peek;
			this.peek = null;
			this.peekAvailable = false;
			if (debug)
				System.out.println("Out of Tokens. If its not EOF then you might have fked up.");
			// System.exit(1);
		}
	}

	boolean currTokenIs(String type) {
		return this.curr.getType() == type;
	}

	boolean peekTokenIs(String type) {
		if (!this.peekAvailable) {
			return false;
		}
		return this.peek.getType() == type;
	}

	boolean expectPeek(String type) {
		if (this.peekTokenIs(type)) {
			this.nextToken();
			return true;
		} else {
			this.errors.addElement(new ParserError(ErrorList.INVALID_SYNTAX,
					"Expected " + this.peek.getType() + " Got : " + type));
			return false;
		}
	}

	Statement parseStatement() {
		switch (this.curr.getType()) {
			case TokenList.LET:
				return this.parseLetStatement();
			case TokenList.RETURN:
				return this.parseReturnStatement();
			case TokenList.WHILE:
				return this.parseWhileStatement();
			default:
				return this.parseExpressionStatement();
		}
	}

	Statement parseWhileStatement() {
		WhileStatement stm = new WhileStatement(this.curr);
		if (!expectPeek(TokenList.PAREN_OPEN)) {
			return null;
		}

		nextToken();
		stm.setCondition(parseExpression(PrecedenceList.LOWEST));

		if (!expectPeek(TokenList.PAREN_CLOSE)) {
			return null;
		}
		if (!expectPeek(TokenList.BRACE_OPEN)) {
			return null;
		}
		stm.setBody(parseBlockStatement());
		if (debug)
			stm.print("While Loop : ");
		return stm;

	}

	ExpressionStatement parseExpressionStatement() {
		ExpressionStatement smt = new ExpressionStatement(this.curr,
				this.parseExpression(PrecedenceList.LOWEST));

		if (this.peekTokenIs(TokenList.SEMICOLON)) {
			this.nextToken();
		}
		if (debug)
			smt.print("Exp Stm: ");

		return smt;

	}

	Expression parseExpression(int precedence) {

		if (!this.prefixParsers.containsKey(this.curr.getType())) {
			this.errors.addElement(
					new ParserError("", "No Prefix Parser found for : "
							+ this.curr.getType()));
			return null;
		}

		PrefixParser prefix = this.prefixParsers.get(this.curr.getType());
		Expression leftExp = prefix.parse();
		if (leftExp != null)
			if (debug)
				leftExp.print("Left Exp: ");

		while (!this.peekTokenIs(TokenList.SEMICOLON) && precedence < this.peekPrecedence()) {

			if (this.infixParsers.containsKey(this.peek.getType())) {

				InfixParser infix = this.infixParsers.get(this.peek.getType());
				this.nextToken();
				leftExp = infix.parse(leftExp);
			} else {
				return leftExp;
			}
		}
		return leftExp;
	}

	LetStatement parseLetStatement() {
		LetStatement stm = new LetStatement(this.curr);

		if (!this.expectPeek(TokenList.IDENTIFIER)) {
			return null;
		}

		stm.setName(new Identifier(this.curr, this.curr.getTokenValue()));

		if (!this.expectPeek(TokenList.ASSIGN)) {
			return null;
		}

		this.nextToken();

		stm.setValue(this.parseExpression(PrecedenceList.LOWEST));

		if (this.peekTokenIs(TokenList.SEMICOLON)) {
			this.nextToken();
		}

		if (debug)
			stm.print("Let Statement : ");
		return stm;
	}

	ReturnStatement parseReturnStatement() {
		ReturnStatement stm = new ReturnStatement(this.curr);

		this.nextToken();

		stm.setReturnValue(this.parseExpression(PrecedenceList.LOWEST));

		if (this.peekTokenIs(TokenList.SEMICOLON)) {
			this.nextToken();
		}

		if (debug)
			stm.print("Return Statement : ");
		return stm;
	}

	BlockStatement parseBlockStatement() {
		BlockStatement block = new BlockStatement(this.curr);
		this.nextToken();

		while (!(this.currTokenIs(TokenList.BRACE_CLOSE) || this.currTokenIs(TokenList.EOF))) {
			Statement stm = this.parseStatement();
			if (stm != null) {
				block.addStatement(stm);
			}
			this.nextToken();
		}
		return block;
	}

	Vector<Identifier> parseFunctionParameters() {
		Vector<Identifier> parameters = new Vector<Identifier>();
		if (this.peekTokenIs(TokenList.PAREN_CLOSE)) {
			this.nextToken();
			return parameters;
		}
		this.nextToken();

		Identifier ident = new Identifier(this.curr, this.curr.getTokenValue());
		parameters.addElement(ident);

		while (this.peekTokenIs(TokenList.COMMA)) {
			this.nextToken();
			this.nextToken();
			ident = new Identifier(this.curr, this.curr.getTokenValue());
			parameters.addElement(ident);
		}

		if (!this.expectPeek(TokenList.PAREN_CLOSE)) {
			return null;
		}
		return parameters;

	}

	Vector<Expression> parseCallArguments() {
		Vector<Expression> args = new Vector<Expression>();

		if (this.peekTokenIs(TokenList.PAREN_CLOSE)) {
			this.nextToken();
			return args;
		}
		this.nextToken();
		args.addElement(this.parseExpression(PrecedenceList.LOWEST));

		while (this.peekTokenIs(TokenList.COMMA)) {
			this.nextToken();
			this.nextToken();
			args.addElement(this.parseExpression(PrecedenceList.LOWEST));
		}
		if (!this.expectPeek(TokenList.PAREN_CLOSE)) {
			return null;
		}
		return args;
	}

	public Program parseProgram() {

		this.program = new Program();
		// this.tokens.getLast().printToken();

		while (this.peek != null) {
			// this.curr.printToken();
			Statement stm = this.parseStatement();
			if (stm != null) {
				program.statements.addElement(stm);
			}
			this.nextToken();
		}

		return this.program;

	}

	public void printProgram() {
		if (debug)
			this.program.print("Parsed Program : ");
		else
			System.out.println("DEBUG set to FALSE");

	}

	public Vector<ParserError> getErrors() {
		return this.errors;
	}

	public static void main(String[] args) {
		System.out.println("Parsing :");

	}
}
