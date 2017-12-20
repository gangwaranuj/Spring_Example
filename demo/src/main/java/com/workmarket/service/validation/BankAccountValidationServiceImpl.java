package com.workmarket.service.validation;

import com.workmarket.domains.payments.validator.BankAccountValidator;
import com.workmarket.thrift.core.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service("achBankAccountValidationService")
public class BankAccountValidationServiceImpl extends AbstractValidationService {

	@Autowired private BankAccountValidator bankAccountValidator;

	@Override
	public <T> void validate(final T entity, final BindingResult binding, final List<ConstraintViolation> errors) {
		bankAccountValidator.validate(entity, binding);
		if (binding.hasErrors()) {
			getConstraintViolations(binding, errors);
			logErrors(errors);
		}
	}
}
