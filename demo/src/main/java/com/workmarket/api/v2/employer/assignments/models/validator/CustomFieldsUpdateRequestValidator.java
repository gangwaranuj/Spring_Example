package com.workmarket.api.v2.employer.assignments.models.validator;

import com.workmarket.api.v2.employer.assignments.models.CustomFieldsUpdateRequest;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CustomFieldsUpdateRequestValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return CustomFieldsUpdateRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		CustomFieldsUpdateRequest request = (CustomFieldsUpdateRequest) o;

		if(CollectionUtilities.isEmpty(request.getGroups()) && request.getGroupId() == 0 && CollectionUtilities.isEmpty(request.getFields())){
			errors.rejectValue("group", "group", "Either single group or groups should be present");
		}

	}
}
