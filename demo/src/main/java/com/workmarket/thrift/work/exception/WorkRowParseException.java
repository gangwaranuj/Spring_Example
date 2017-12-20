package com.workmarket.thrift.work.exception;

import java.util.Collection;

import com.workmarket.service.exception.WorkMarketException;

public class WorkRowParseException extends WorkMarketException {

	private static final long serialVersionUID = 1309957152731128204L;

	private final Collection<WorkRowParseError> errors;
	
	public WorkRowParseException(String message, Exception e, Collection<WorkRowParseError> errors) {
		super(message, e);
		this.errors = errors;
	}
	
	public WorkRowParseException(String message, Collection<WorkRowParseError> errors) {
		super(message);
		this.errors = errors;
	}
	
	public WorkRowParseException(Collection<WorkRowParseError> errors) {
		super("Row parse error");
		this.errors = errors;
	}

	public Collection<WorkRowParseError> getErrors() {
		return errors;
	}

}
