package com.livescribe.base.dao;

public class DbOperationStatus<T, E extends Exception> {

	public enum Status {
		SUCCESS,
		FAIL,
		DB_UNAVAILBLE,
		CONSTRTAINT_VIOLATION,
		STALE_OBJECT_STATE;
	};
	
	private Status status;
	private T obj;
	private E exception;
	
	public DbOperationStatus(Status status, T obj) {
		this.status = status;
		this.obj = obj;
	}
	
	public DbOperationStatus(Status status, T obj, E exception) {
		this.status = status;
		this.obj = obj;
		this.exception = exception;
	}
	
	public boolean isSuccessful() {
		return status == Status.SUCCESS;
	}
	
	public boolean isConstraintViolation() {
		return status == Status.CONSTRTAINT_VIOLATION;
	}
	
	public boolean isStaleObjectState() {
		return status == Status.STALE_OBJECT_STATE;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public T getObject() {
		return obj;
	}
	
	public E getException() {
		return exception;
	}
}
