package com.workmarket.web.validators;

import com.workmarket.BaseUnitTest;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.external.ShippingProviderDetectResponse;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class PartValidatorTest extends BaseUnitTest {

	@Mock MessageBundleHelper messageHelper;
	@InjectMocks PartValidator validator;

	PartDTO part;
	Errors errors;
	ShippingProviderDetectResponse shippingProviderDetectResponse;

	@Before
	public void setup() {
		part = mock(PartDTO.class);
		errors = mock(Errors.class);
		shippingProviderDetectResponse = mock(ShippingProviderDetectResponse.class);
		when(part.getName()).thenReturn(ANY_STRING);
		when(part.getTrackingNumber()).thenReturn(ANY_STRING);
		when(part.getPartValue()).thenReturn(BigDecimal.ZERO);
		when(shippingProviderDetectResponse.isSuccessful()).thenReturn(true);
	}

	@Test
	public void validate_withNullName_thenRejectValue() throws Exception {
		when(part.getName()).thenReturn(null);

		validator.validate(part, errors);

		verify(errors).rejectValue(eq("name"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_withEmptyName_thenRejectValue() throws Exception {
		when(part.getName()).thenReturn(EMPTY_TOKEN);

		validator.validate(part, errors);

		verify(errors).rejectValue(eq("name"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_withBlankName_thenRejectValue() throws Exception {
		when(part.getName()).thenReturn(WHITE_SPACE_TOKEN);

		validator.validate(part, errors);

		verify(errors).rejectValue(eq("name"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_withNullNumber_thenRejectValue() throws Exception {
		when(part.getTrackingNumber()).thenReturn(null);

		validator.validate(part, errors);

		verify(errors).rejectValue(eq("trackingNumber"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_withEmptyNumber_thenRejectValue() throws Exception {
		when(part.getTrackingNumber()).thenReturn(EMPTY_TOKEN);

		validator.validate(part, errors);

		verify(errors).rejectValue(eq("trackingNumber"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_withBlankNumber_thenRejectValue() throws Exception {
		when(part.getTrackingNumber()).thenReturn(WHITE_SPACE_TOKEN);

		validator.validate(part, errors);

		verify(errors).rejectValue(eq("trackingNumber"), eq("NotNull"), anyString());
	}

	@Test
	public void validate_withValidPartValue_thenNoErrors() throws Exception {
		validator.validate(part, errors);

		verify(errors, never()).rejectValue(eq("partValue"), eq("OutOfRange"), anyString());
	}

	@Test
	public void validate_withNullPartValue_thenNoErrors() throws Exception {
		when(part.getPartValue()).thenReturn(null);

		validator.validate(part, errors);

		verify(errors, never()).rejectValue(eq("partValue"), eq("OutOfRange"), anyString());
	}


	@Test
	public void validate_withTooLargePartValue_thenRejectPartValue() throws Exception {
		when(part.getPartValue()).thenReturn(PartDTO.PART_VALUE_MAX.add(new BigDecimal(0.01)));

		validator.validate(part, errors);

		verify(errors).rejectValue(eq("partValue"), eq("OutOfRange"), any(Object[].class), anyString());
	}

	@Test
	public void validate_withTooSmallPartValue_thenRejectPartValue() throws Exception {
		when(part.getPartValue()).thenReturn(PartDTO.PART_VALUE_MIN.subtract(new BigDecimal(0.01)));

		validator.validate(part, errors);

		verify(errors).rejectValue(eq("partValue"), eq("OutOfRange"), any(Object[].class), anyString());
	}
}
