package com.workmarket.web.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

public class FieldMatchValidator implements
		ConstraintValidator<FieldMatch, Object> {
	private String firstFieldName;
	private String secondFieldName;

	public void initialize(final FieldMatch constraintAnnotation) {
		firstFieldName = constraintAnnotation.first();
		secondFieldName = constraintAnnotation.second();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
			final Object secondObj = BeanUtils.getProperty(value,secondFieldName);
			
			String errorMessage = "Fields do not match"; //change to be property driven
			
			
			boolean fieldsMatch = true;
			
			fieldsMatch = firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
			
			if(false == fieldsMatch) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(errorMessage).addNode(firstFieldName).addConstraintViolation();
				
				
				return false;
			}
			
			return true;
		} catch (final Exception ignore) {
			// ignore
		}
		return true;
	}
}

