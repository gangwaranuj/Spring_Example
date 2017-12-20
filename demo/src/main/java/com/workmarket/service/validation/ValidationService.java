package com.workmarket.service.validation;

import com.workmarket.thrift.core.ConstraintViolation;

import java.util.List;

public interface ValidationService {
	<T> void validate(T entity, List<ConstraintViolation> errors);
}
