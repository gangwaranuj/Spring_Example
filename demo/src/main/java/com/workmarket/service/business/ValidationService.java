package com.workmarket.service.business;

import java.util.Set;

import javax.validation.ConstraintViolation;

public interface ValidationService {

	<T> Set<ConstraintViolation<T>> validateEntity(T entity);
}
