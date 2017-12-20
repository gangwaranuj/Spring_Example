package com.workmarket.web.validators;

import com.workmarket.service.business.dto.PaymentDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("paymentValidator")
public class PaymentValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return (PaymentDTO.class == clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PaymentDTO payment = (PaymentDTO)target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardType", "NotEmpty", new Object[] {"Credit card type"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardNumber", "NotEmpty", new Object[] {"Credit card number"});

		if (!StringUtils.hasText(payment.getCardExpirationMonth())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardExpirationMonth", "NotEmpty", new Object[] {"Credit card expiration month"});
		}

		if (!StringUtils.hasText(payment.getCardExpirationYear())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardExpirationYear", "NotEmpty", new Object[] {"Credit card expiration year"});
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardSecurityCode", "NotEmpty", new Object[] {"Security code"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty", new Object[] {"First name"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty", new Object[] {"Last Name"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address1", "NotEmpty", new Object[] {"Address 1"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "NotEmpty", new Object[] {"City"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "NotEmpty", new Object[] {"State"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postalCode", "NotEmpty", new Object[] {"Zip code"});
	}
}
