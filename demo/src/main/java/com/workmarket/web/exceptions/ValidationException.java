package com.workmarket.web.exceptions;

import com.google.common.collect.Lists;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Created by ianha on 6/4/14
 */
public class ValidationException extends Exception {
	private List<ObjectError> errors;

	public ValidationException(String field, String message) {
		this(Lists.newArrayList((ObjectError)(new FieldError(field, field, message))));
	}

	public ValidationException(List<ObjectError> errors) {
		this.errors = errors;
	}

	public List<ObjectError> getErrors() {
		return errors;
	}
}
