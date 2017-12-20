package com.workmarket.web.validators;

import com.workmarket.domains.work.service.WorkBatchSendRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class WorkBatchSendRequestValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return WorkBatchSendRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		WorkBatchSendRequest form = (WorkBatchSendRequest) target;
		if (form == null) {
			errors.reject("workNumbers");
		}
		if (form.isMissingUserNumbers() && form.isMissingGroupIds() && form.isMissingVendorCompanyNumbers()) {
			errors.reject("workNumbers");
		}
	}
}
