package com.workmarket.web.validators;

import com.workmarket.domains.model.ManageMyWorkMarket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MmwValidatorTest {

	@InjectMocks MmwValidator mmwValidator;
	private ManageMyWorkMarket manageMyWorkMarket;
	private Errors bindingResults;

	@Before
	public void setUp() throws Exception {
		manageMyWorkMarket = mock(ManageMyWorkMarket.class);
		bindingResults = mock(BindingResult.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void validate_withNullArguments_throwsException() {
		mmwValidator.validate(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void validate_withNullErrors_throwsException() {
		mmwValidator.validate(manageMyWorkMarket, null);
	}

	@Test
	public void validate_withAutoCloseEnabledFlagFalse_doNothing() {
		mmwValidator.validate(manageMyWorkMarket, bindingResults);
		verify(manageMyWorkMarket, never()).getAutocloseDelayInHours();
	}

	@Test
	public void validate_withAutoCloseEnabledFlagTrueAndAutoCloseDelayInHours_returnsError() {
		when(manageMyWorkMarket.getAutocloseDelayInHours()).thenReturn(null);
		when(manageMyWorkMarket.getAutocloseEnabledFlag()).thenReturn(true);

		mmwValidator.validate(manageMyWorkMarket, bindingResults);
		verify(manageMyWorkMarket, times(1)).getAutocloseDelayInHours();
		verify(bindingResults, times(1)).rejectValue(anyString(), anyString());
	}

	@Test
	public void validate_withAutoCloseEnabledFlagTrueAndAutoCloseDelayInHoursGreaterThan0_passValidation() {
		when(manageMyWorkMarket.getAutocloseDelayInHours()).thenReturn(1);
		when(manageMyWorkMarket.getAutocloseEnabledFlag()).thenReturn(true);

		mmwValidator.validate(manageMyWorkMarket, bindingResults);
		verify(manageMyWorkMarket, times(2)).getAutocloseDelayInHours();
		verify(bindingResults, never()).rejectValue(anyString(), anyString());
	}
}