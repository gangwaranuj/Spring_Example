package com.workmarket.web.validators;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.workmarket.BaseUnitTest;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

public abstract class BaseValidatorTest extends BaseUnitTest {

	protected boolean hasErrorCode(Errors errors, final String errorCode) {
		Assert.notNull(errors);
		return Iterables.tryFind(errors.getAllErrors(), new Predicate<ObjectError>() {
			@Override
			public boolean apply(ObjectError objectError) {
				return Arrays.asList(objectError.getCodes()).contains(errorCode);
			}
		}).isPresent();
	}

	protected boolean hasErrorMessage(Errors errors, final String errorMessage) {
		Assert.notNull(errors);
		return Iterables.tryFind(errors.getAllErrors(), new Predicate<ObjectError>() {
			@Override
			public boolean apply(ObjectError objectError) {
				return Arrays.asList(objectError.getDefaultMessage()).contains(errorMessage);
			}
		}).isPresent();
	}

	protected boolean hasFieldInError(Errors errors, final String field) {
		Assert.notNull(errors);
		return Iterables.tryFind(errors.getFieldErrors(), new Predicate<FieldError> () {
			@Override
			public boolean apply(FieldError objectError) {
				return Arrays.asList(objectError.getField()).contains(field);
			}
		}).isPresent();
	}

	protected Errors validate(Object o) {
		BindingResult binding = new DataBinder(o).getBindingResult();
		getValidator().validate(o, binding);
		return binding;
	}

	protected String generateMaxExceededString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= length; i++) {
			sb.append('b');
		}
		return sb.toString();
	}

	protected abstract Validator getValidator();
}
