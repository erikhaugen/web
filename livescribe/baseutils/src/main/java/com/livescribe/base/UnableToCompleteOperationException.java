package com.livescribe.base;

@SuppressWarnings("serial")
public class UnableToCompleteOperationException extends Exception {
	
	public UnableToCompleteOperationException(){}
	
	public UnableToCompleteOperationException(String operation, Throwable throwable) {
		super("Unable to " + operation + "-" + throwable.getMessage(), throwable);
	}
}
