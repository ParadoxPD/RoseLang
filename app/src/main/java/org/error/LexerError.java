
package org.error;

public class LexerError extends Error {

	// private ParserError(){
	// super();
	// }

	public LexerError(String errorType, String message) {
		super(errorType, message);
	}

	@Override
	public void printError() {
		System.out.println("[Lexer Error] " + super.getErrorMessage());
	}
}
