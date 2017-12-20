package com.workmarket.web.validators;

import com.workmarket.service.business.dto.FeedbackDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.web.forms.work.WorkAssetForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class FeedbackValidator implements Validator {

	public static final int TITLE_MAX_LENGTH = 255;

	@Override
	public boolean supports(Class<?> clazz) {
		return FeedbackDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		FeedbackDTO feedback = (FeedbackDTO) target;
		if (isBlank(feedback.getTitle())) {
			errors.rejectValue(null, "feedback.title.blank");
		}
		if (isBlank(feedback.getDescription())) {
			errors.rejectValue(null, "feedback.description.blank");
		}

		if (isBlank(feedback.getType())) {
			errors.rejectValue(null, "feedback.type.blank");
		}

		if (feedback.getConcern() == null) {
			errors.rejectValue(null, "feedback.concern.blank");
		}

		if (feedback.getConcern() != null && isBlank(feedback.getConcern().getCode())) {
			errors.rejectValue(null, "feedback.concern.blank");
		}

		if (StringUtils.length(feedback.getTitle()) > TITLE_MAX_LENGTH) {
			errors.rejectValue(null, "feedback.title.length");
		}

		if (StringUtils.length(feedback.getDescription()) > Constants.TEXT_MAX_LENGTH) {
			errors.rejectValue(null, "feedback.description.length");
		}

		if (feedback.getAttachments() != null) {
			for (WorkAssetForm form : feedback.getAttachments()) {
				if (StringUtils.length(form.getDescription()) > Constants.ASSET_DESCRIPTION_TEXT_LENGTH) {
					errors.rejectValue(null, "feedback.asset.length");
				}
			}
		}
	}
}
