package org.error;

public class EvaluatorError extends Error {
	public EvaluatorError(String errorType, String message) {
		super(errorType, message);
	}
}
