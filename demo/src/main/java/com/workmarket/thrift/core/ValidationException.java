package com.workmarket.thrift.core;

import java.util.Collections;
import java.util.List;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	private List<ConstraintViolation> errors = Collections.emptyList();

	public ValidationException() {
	}

	public ValidationException(List<ConstraintViolation> errors) {
		this.errors = errors;
	}

	public ValidationException(String why, List<ConstraintViolation> errors) {
		super(why);
		this.errors = errors;
	}

	public List<ConstraintViolation> getErrors() {
		return errors;
	}

	public String getWhy() {
		return super.getMessage();
	}
}