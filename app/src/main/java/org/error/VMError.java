
package org.error;

public class VMError extends Error {

	// private ParserError(){
	// super();
	// }

	public VMError(String errorType, String message) {
		super(errorType, message);
	}

	@Override
	public void printError() {
		System.out.println("[VM Error] " + super.getErrorMessage());
	}
}
