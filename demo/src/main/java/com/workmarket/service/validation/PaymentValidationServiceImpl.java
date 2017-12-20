package com.workmarket.service.validation;

import com.workmarket.service.validation.validators.CreditCardPaymentDTOValidator;
import com.workmarket.thrift.core.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service("creditCardPaymentValidationService")
public class PaymentValidationServiceImpl extends AbstractValidationService {

	@Autowired CreditCardPaymentDTOValidator validator;

	@Override
	public <T> void validate(final T entity, final BindingResult binding, final List<ConstraintViolation> errors) {
		validator.validate(entity, binding);
		if (binding.hasErrors()) {
			getConstraintViolations(binding, errors);
			logErrors(errors);
		}
	}
}
