package com.workmarket.service.business;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ValidationServiceImpl implements ValidationService {
	
	private static final Log logger = LogFactory.getLog(ValidationServiceImpl.class);
	
	private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

	@Override
	public <T> Set<ConstraintViolation<T>> validateEntity(T entity) {
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> errors =  validator.validate(entity);
		
		if (logger.isDebugEnabled()) {
			for (ConstraintViolation<T> e : errors) {
				logger.debug(String.format(
					"[validation-error] field => %s, type => %s, message => %s",
					e.getPropertyPath(),
					e.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
					e.getMessage()
				));
			}
		}
		
		return errors;
	}
}
