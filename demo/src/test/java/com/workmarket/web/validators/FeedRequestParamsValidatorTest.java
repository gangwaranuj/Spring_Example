package com.workmarket.web.validators;

import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.workmarket.web.validators.FeedRequestParamsValidator.POSTAL_CODE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedRequestParamsValidatorTest extends BaseValidatorTest {

	@Mock MessageBundleHelper messageBundleHelper;
	@Mock InvariantDataService invariantDataService;
	@InjectMocks FeedRequestParamsValidator validator;

	FeedRequestParams feedRequestParams = mock(FeedRequestParams.class);
	PostalCode postalCode = mock(PostalCode.class);

	@Before
	public void setUp() throws Exception {
		when(feedRequestParams.getPostalCode()).thenReturn(ANY_STRING);
		when(feedRequestParams.getState()).thenReturn(ANY_STRING);
		when(feedRequestParams.getPostalCodeToggle()).thenReturn(true);
		when(invariantDataService.findOrSavePostalCode(ANY_STRING)).thenReturn(postalCode);
	}

	@Test
	public void validate_postalCode_validPostalCode_noErrors() throws Exception {
		Errors errors = validate(feedRequestParams);

		assertFalse(hasFieldInError(errors, POSTAL_CODE));
	}

	@Test
	public void validate_postalCode_tooLong_returnError() throws Exception {
		when(feedRequestParams.getPostalCode()).thenReturn(RandomUtilities.generateAlphaNumericString(PostalCode.POSTAL_CODE_MAX + 1));

		Errors errors = validate(feedRequestParams);

		assertTrue(hasFieldInError(errors, POSTAL_CODE));
		assertTrue(hasErrorCode(errors, "Max"));
	}

	@Test
	public void validate_postalCode_tooShort_returnError() throws Exception {
		when(feedRequestParams.getPostalCode()).thenReturn(RandomUtilities.generateAlphaNumericString(PostalCode.POSTAL_CODE_MIN - 1));

		Errors errors = validate(feedRequestParams);

		assertTrue(hasFieldInError(errors, POSTAL_CODE));
		assertTrue(hasErrorCode(errors, "Min"));
	}

	@Test
	public void validate_postalCode_invalid_returnError() throws Exception {
		when(invariantDataService.findOrSavePostalCode(ANY_STRING)).thenReturn(null);

		Errors errors = validate(feedRequestParams);

		assertTrue(hasFieldInError(errors, POSTAL_CODE));
		assertTrue(hasErrorCode(errors, "Invalid"));
	}

	protected Validator getValidator() {
		return validator;
	}
}