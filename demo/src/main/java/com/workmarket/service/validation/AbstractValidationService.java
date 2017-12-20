package com.workmarket.service.validation;

import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

public abstract class AbstractValidationService implements ValidationService{

	private static final Log logger = LogFactory.getLog(AbstractValidationService.class);
	@Autowired MessageBundleHelper messageHelper;

	@Override
	public <T> void validate(final T entity, final List<ConstraintViolation> errors) {
		BindingResult bindings = new DataBinder(entity).getBindingResult();
		validate(entity, bindings, errors);
	}

	public abstract <T> void validate(final T entity, final BindingResult binding, final List<ConstraintViolation> errors);

	protected void getConstraintViolations(final BindingResult binding, final List<ConstraintViolation> errors) {
		if (binding.hasErrors()) {
			for (ObjectError e : binding.getAllErrors()) {
				boolean hasArgument = ArrayUtils.isEmpty(e.getArguments());
				errors.add(new ConstraintViolation().setWhy(
					hasArgument ?
						messageHelper.getMessage(e.getCode(), ((FieldError) e).getField()) :
						messageHelper.getMessage(e.getCode(), e.getArguments()))
					.setError(
						hasArgument ? e.getCode() : messageHelper.getMessage(e.getCode(), e.getArguments())
					)
					.setProperty((String.format("%s", ((FieldError) e).getField()))));
			}
		}
	}

	protected void logErrors(final List<ConstraintViolation> errors) {
		for (ConstraintViolation error : errors)
			logger.error(error);
	}
}
