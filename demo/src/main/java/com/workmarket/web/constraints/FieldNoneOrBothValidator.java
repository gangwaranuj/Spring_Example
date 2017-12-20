package com.workmarket.web.constraints;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by nick on 9/26/12 6:46 PM
 * A validator to verify that either 1) both fields are null or 2) both fields are not-null
 */
public class FieldNoneOrBothValidator implements ConstraintValidator<FieldNoneOrBoth, Object> {

	private static final Log logger = LogFactory.getLog(FieldNoneOrBothValidator.class);

	private String firstFieldName;
	private String secondFieldName;

	public void initialize(final FieldNoneOrBoth constraintAnnotation) {
		firstFieldName = constraintAnnotation.first();
		secondFieldName = constraintAnnotation.second();
	}

	@Override public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
			final Object secondObj = BeanUtils.getProperty(value, secondFieldName);

			String errorMessage = "Fields must both be set or both must be null";

			boolean fieldsNoneOrBoth = (firstObj == null && secondObj == null) || (firstObj != null && secondObj != null);

			if (!fieldsNoneOrBoth) {
				context.disableDefaultConstraintViolation();
				ConstraintValidatorContext.ConstraintViolationBuilder violation = context.buildConstraintViolationWithTemplate(errorMessage);
				if (violation != null)
					violation.addNode(firstFieldName).addConstraintViolation();
				return false;
			}
			return true;

		} catch (final Exception e) {
			logger.error("Error accessing bean property: ", e);
		}
		return true;
	}
}
