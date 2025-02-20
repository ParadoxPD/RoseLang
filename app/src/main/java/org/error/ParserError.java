package org.error;

public class ParserError extends Error {

	// private ParserError(){
	// super();
	// }

	public ParserError(String errorType, String message) {
		super(errorType, message);
	}

	@Override
	public void printError() {
		System.out.println("Parser Error : " + super.getErrorMessage());
	}
}
