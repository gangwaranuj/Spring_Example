package com.workmarket.web.validators;

import com.workmarket.domains.model.Company;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompanyValidatorTest {
	CompanyValidator validator;
	Company company;
	Errors errors;

	@Before
	public void setup() {
		validator = new CompanyValidator();
		company = mock(Company.class);
		errors = mock(Errors.class);

		when(company.getName()).thenReturn("Acme Inc.");
		when(company.getWebsite()).thenReturn("www.acmeinc.com");
		when(company.getOverview()).thenReturn("Acme Inc. is the bomb!");
	}

	@Test
	public void itShouldNotHaveErrorsOnValidCompany() {
		validator.validate(company, errors);
		verify(errors, never()).rejectValue(any(String.class), any(String.class));
	}

	@Test
	public void itShouldHaveErrorOnNullName() {
		when(company.getName()).thenReturn(null);
		verifyErrorNotEmpty();
	}

	@Test
	public void itShouldHaveErrorOnEmptyName() {
		when(company.getName()).thenReturn("");
		verifyErrorNotEmpty();
	}

	@Test
	public void itShouldHaveErrorOnMaxWebsiteLengthExceeded() {
		when(company.getWebsite()).thenReturn(RandomStringUtils.random(256));
		verifyErrorNotEmpty();
	}

	@Test
	public void itShouldHaveErrorOnMaxOverviewLengthExceeded() {
		when(company.getOverview()).thenReturn(RandomStringUtils.random(1001));
		verifyErrorNotEmpty();
	}

	private void verifyErrorNotEmpty() {
		validator.validate(company, errors);
		verify(errors).rejectValue(any(String.class), any(String.class));
	}
}