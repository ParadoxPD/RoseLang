package org.error;

public interface ErrorList {
	String EOF = "EOF";
	String TYPE_ERROR = "TYPE_ERROR";
	String OUT_OF_TOKENS = "OUT_OF_TOKENS";
	String INVALID_SYNTAX = "INVALID_SYNTAX";

	public interface LexerError {

	}

	public interface ParserError {

	}

	public interface CompilerError {

	}

	public interface VMError {

	}
}
