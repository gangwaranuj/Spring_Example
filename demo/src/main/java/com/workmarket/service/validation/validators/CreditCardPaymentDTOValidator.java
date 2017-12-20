package com.workmarket.service.validation.validators;

import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static com.workmarket.utility.NumberUtilities.isPositive;

@Component
public class CreditCardPaymentDTOValidator implements Validator {

	@Override
	public boolean supports(final Class<?> clazz) {
		return (CreditCardPaymentDTO.class == clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		CreditCardPaymentDTO creditCardPaymentDTO = (CreditCardPaymentDTO) target;

		if (StringUtils.isEmpty(creditCardPaymentDTO.getAmount()) ? true : !isPositive(new BigDecimal(creditCardPaymentDTO.getAmount()))) {
			errors.rejectValue("amount", "funds.addcc.invalid_amount");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardType", "NotEmpty", new Object[] {"Credit card type"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardNumber", "NotEmpty", new Object[] {"Credit card number"});

		if (!StringUtils.hasText(creditCardPaymentDTO.getCardExpirationMonth())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardExpirationMonth", "NotEmpty", new Object[] {"Credit card expiration month"});
		}

		if (!StringUtils.hasText(creditCardPaymentDTO.getCardExpirationYear())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardExpirationYear", "NotEmpty", new Object[] {"Credit card expiration year"});
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardSecurityCode", "NotEmpty", new Object[] {"Security code"});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nameOnCard", "NotEmpty", new Object[] {"Name on card"});
	}
}
