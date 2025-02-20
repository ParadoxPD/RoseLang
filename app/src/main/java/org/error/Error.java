package org.error;

public class Error {

	private String errorType;
	private String message;

	public Error(String errorType, String message) {
		this.errorType = errorType;
		this.message = message;
	}

	public String getErrorType() {
		return this.errorType;
	}

	public String getErrorMessage() {
		return this.message;
	}

	public void printError() {
		System.out.println(this.message);
	}
}
