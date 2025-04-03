
package org.error;

public class CompilerError extends Error {

	// private ParserError(){
	// super();
	// }

	public CompilerError(String errorType, String message) {
		super(errorType, message);
	}

	@Override
	public void printError() {
		System.out.println("[Compiler Error] " + super.getErrorMessage());
	}
}
