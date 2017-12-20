package com.workmarket.web.validators;

import com.workmarket.domains.model.LanguageProficiencyType;
import com.workmarket.service.business.dto.ProfileLanguageDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Validator;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class ProfileLanguageValidatorTest extends BaseValidatorTest {
	private final ProfileLanguageValidator validator = new ProfileLanguageValidator();

	@Test
	public void supports_ProfileLanguageDTO_true() {
		assertTrue(validator.supports(ProfileLanguageDTO.class));
	}

	@Test
	public void supports_OtherClass_false() {
		assertFalse(validator.supports(List.class));
	}

	@Test
	public void validate_ValidDTO_success() throws Exception {
		assertFalse(validate(validDTO()).hasErrors());
	}

	@Test
	public void validate_NoId_fail() throws Exception {
		ProfileLanguageDTO dto = validDTO();
		dto.setLanguageId(null);
		assertTrue(hasErrorCode(validate(dto), "languages.id.required"));
	}

	@Test
	public void validate_NoTypeCode_fail() throws Exception {
		ProfileLanguageDTO dto = validDTO();
		dto.setLanguageProficiencyTypeCode("");
		assertTrue(hasErrorCode(validate(dto), "languages.fluency.required"));
	}

	@Test
	public void validate_NullTypeCode_fail() throws Exception {
		ProfileLanguageDTO dto = validDTO();
		dto.setLanguageProficiencyTypeCode(null);
		assertTrue(hasErrorCode(validate(dto), "languages.fluency.required"));
	}

	// TODO: we do not check validity of proficiency

	private ProfileLanguageDTO validDTO() {
		ProfileLanguageDTO dto = new ProfileLanguageDTO();
		dto.setLanguageId(1L);
		dto.setLanguageProficiencyTypeCode(LanguageProficiencyType.FLUENT);
		return dto;
	}

	protected Validator getValidator() {
		return validator;
	}
}