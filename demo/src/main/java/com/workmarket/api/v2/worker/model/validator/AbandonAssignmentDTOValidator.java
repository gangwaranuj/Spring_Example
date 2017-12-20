package com.workmarket.api.v2.worker.model.validator;

import com.workmarket.api.v2.worker.model.AbandonAssignmentDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 * Created by michaelrothbaum on 7/31/17.
 */

@Component
public class AbandonAssignmentDTOValidator {

	public static final int MAX_SIZE_MESSAGE = 65535;
	public static final String YOU_MUST_ADD_A_MESSAGE_IN_ORDER_TO_ABANDON_THE_ASSIGNMENT = "You must add a message in order to abandon the assignment.";
	public static final String THE_MESSAGE_IS_TOO_LONG = "The message is too long.";

	public boolean supports(Class<?> clazz) {
		return AbandonAssignmentDTO.class.equals(clazz);
	}

	public void validate(String message, BindingResult errors) {

		validateMessage(message, errors);

	}

	private void validateMessage(final String message, final BindingResult errors) {

		if (StringUtils.isEmpty(message)) {
			errors.reject("NotEmpty.abandon_work.mobilemessage", YOU_MUST_ADD_A_MESSAGE_IN_ORDER_TO_ABANDON_THE_ASSIGNMENT);
		}

		if (message.length() > MAX_SIZE_MESSAGE) {
			errors.reject("assignment.abandon_work.mobiletoolong", THE_MESSAGE_IS_TOO_LONG);
		}
	}
}

