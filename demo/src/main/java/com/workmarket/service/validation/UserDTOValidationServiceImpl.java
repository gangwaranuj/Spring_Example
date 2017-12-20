package com.workmarket.service.validation;

import com.workmarket.api.v2.employer.settings.models.validator.UserDTOValidator;
import com.workmarket.thrift.core.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service("userValidationService")
public class UserDTOValidationServiceImpl extends AbstractValidationService {

	@Autowired private UserDTOValidator userDTOValidator;

	@Override
	public <T> void validate(final T entity, final BindingResult binding, final List<ConstraintViolation> errors) {
		userDTOValidator.validate(entity, binding);
		if (binding.hasErrors()) {
			getConstraintViolations(binding, errors);
			logErrors(errors);
		}
	}
}
