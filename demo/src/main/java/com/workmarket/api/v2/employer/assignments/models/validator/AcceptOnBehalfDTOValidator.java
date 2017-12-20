package com.workmarket.api.v2.employer.assignments.models.validator;

import com.workmarket.api.v2.employer.assignments.models.AcceptOnBehalfDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AcceptOnBehalfDTOValidator implements Validator {
	@Override
	public boolean supports(Class<?> aClass) {
		return AcceptOnBehalfDTO.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		AcceptOnBehalfDTO dto = (AcceptOnBehalfDTO) o;
		if (StringUtils.isEmpty(dto.getNote())) {
			errors.rejectValue("note", "note", "note field can not be empty");
		}

		if (StringUtils.isEmpty(dto.getUserNumber())) {
			errors.rejectValue("userNumber", "userNumber", "userNumber field can not be empty");
		}
	}
}
