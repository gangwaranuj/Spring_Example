package com.workmarket.web.validators;

import com.google.common.collect.Maps;
import com.workmarket.web.forms.RatesLocationsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class RatesLocationsFormValidatorTest extends BaseValidatorTest {

	private final RatesLocationsFormValidator validator = new RatesLocationsFormValidator();

	@Test
	public void supports_RatesLocationsForm_true() {
		assertTrue(validator.supports(RatesLocationsForm.class));
	}

	@Test
	public void supports_OtherClass_false() {
		assertFalse(validator.supports(List.class));
	}

	@Test
	public void validate_emptyCurrentLocationTypes_fail() {
		assertTrue(hasErrorCode(validate(new RatesLocationsForm()), "profile.rates_locations.required"));
	}

	@Test
	public void validate_currentLocationTypesSet_success() {
		RatesLocationsForm form = new RatesLocationsForm();
		Map<Long, Boolean> locations = Maps.newHashMap();
		locations.put(ANY_LONG, Boolean.TRUE);
		form.setCurrentLocationTypes(locations);

		assertFalse(validate(form).hasErrors());
	}

	protected Validator getValidator() {
		return validator;
	}
}