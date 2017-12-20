package com.workmarket.web.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.apache.commons.lang3.StringUtils.containsAny;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by nick on 7/3/13 2:32 PM
 */
@Component
public class FilenameValidator implements Validator {

	public static final int MAX_S3_FILENAME_LENGTH = 1024;
	public static final CharSequence DISALLOWED_FILENAME_CHARS = "?/\\*,%:\"<>";

	@Override
	public boolean supports(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		String filename = (String) o;
		if (isBlank(filename)) {
			errors.rejectValue(null, "certifications.add.attachment.blank");
		} else {
			if (containsAny(filename, DISALLOWED_FILENAME_CHARS)) {
				errors.rejectValue(null, "filename.invalid_chars");
			}
			if (filename.length() > MAX_S3_FILENAME_LENGTH) {
				errors.rejectValue(null, "filename.length", new Object[]{MAX_S3_FILENAME_LENGTH}, "");
			}
		}

	}
}
