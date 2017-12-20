package com.workmarket.web.validators;

import com.workmarket.dto.AddressDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;

@Component
public class ApiAddressWorkValidator extends AddressWorkValidator {

	public void validate(AddressDTO addressDTO, BindingResult bindingResult) {
		this.postalCodeAttribute = "postal_code";
		super.validate(addressDTO,bindingResult);
		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "client_id", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "location_name", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "location_type", "NotEmpty");
	}
}
