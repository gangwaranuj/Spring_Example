package com.workmarket.web.validators;


import com.workmarket.service.business.dto.ProfileLanguageDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("profileLanguageValidator")
public class ProfileLanguageValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return ProfileLanguageDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ProfileLanguageDTO languageDTO = (ProfileLanguageDTO)target;

		if (languageDTO.getLanguageId() == null) {
			errors.rejectValue("languageId", "languages.id.required");
		}

		if (!StringUtils.hasLength(languageDTO.getLanguageProficiencyTypeCode())) {
			errors.rejectValue("languageProficiencyTypeCode", "languages.fluency.required");
		}
	}
}
