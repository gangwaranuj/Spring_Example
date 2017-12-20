package com.workmarket.service.validation;

import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.web.validators.AddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service("addressValidationService")
public class AddressValidationServiceImpl extends AbstractValidationService {

	@Autowired private AddressValidator addressValidator;

	@Override
	public <T> void validate(final T entity, final BindingResult binding, final List<ConstraintViolation> errors) {
		addressValidator.validate(entity, binding);
		if (binding.hasErrors()) {
			getConstraintViolations(binding, errors);
			logErrors(errors);
		}
	}
}
