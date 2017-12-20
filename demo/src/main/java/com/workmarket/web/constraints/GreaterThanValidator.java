package com.workmarket.web.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.math.BigInteger;

public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Number> {
	private long testValue;

	@Override
	public void initialize(final GreaterThan constraintAnnotation) {
		testValue = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Number value, ConstraintValidatorContext constraintValidatorContext) {
		// null values are valid
		// use with NotNull as desired
		if (value == null) {
			return true;
		}

		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).compareTo(BigDecimal.valueOf(testValue)) > 0;
		}
		else if (value instanceof BigInteger) {
			return ((BigInteger) value).compareTo(BigInteger.valueOf(testValue)) > 0;
		}
		else {
			long longValue = value.longValue();
			return longValue > testValue;
		}
	}
}
