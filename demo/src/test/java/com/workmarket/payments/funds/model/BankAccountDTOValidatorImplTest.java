package com.workmarket.payments.funds.model;

import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.service.BankAccountDTOValidatorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankAccountDTOValidatorImplTest {
	@Mock
	BankAccountDTO bankAccountDTO;
	BankAccountDTOValidatorImpl validator;

	@Before
	public void setup() {
		when(bankAccountDTO.getCountry()).thenReturn("USA");
		when(bankAccountDTO.getGovIdType()).thenReturn("SSN");
		validator = new BankAccountDTOValidatorImpl();
	}

	@Test
	public void itShouldBeTrueForGovIdTypeSSNIfCountryIsUSA() {
		assertTrue(validator.validate(bankAccountDTO).isEmpty());
	}

	@Test
	public void itShouldBeTrueForGovIdTypeUSStateIdIfCountryIsUSA() {
		when(bankAccountDTO.getGovIdType()).thenReturn("USSTATEID");
		assertTrue(validator.validate(bankAccountDTO).isEmpty());
	}

	@Test
	public void itShouldBeFalseForGovIdTypeNonUSIfCountryIsUSA() {
		when(bankAccountDTO.getGovIdType()).thenReturn("NONUSTAXID");
		assertFalse(validator.validate(bankAccountDTO).isEmpty());
	}

	@Test
	public void itShouldBeFalseForGovIdTypeSSNIfCountryIsNotUSA() {
		when(bankAccountDTO.getCountry()).thenReturn("CAN");
		assertFalse(validator.validate(bankAccountDTO).isEmpty());
	}

	@Test
	public void itShouldBeFalseForGovIdTypeUsStateIdIfCountryIsNotUSA() {
		when(bankAccountDTO.getCountry()).thenReturn("CAN");
		when(bankAccountDTO.getGovIdType()).thenReturn("USSTATEID");
		assertFalse(validator.validate(bankAccountDTO).isEmpty());
	}

	@Test
	public void itShouldBeTrueForGovIdTypeNonUSIfCountryIsNotUSA() {
		when(bankAccountDTO.getCountry()).thenReturn("CAN");
		when(bankAccountDTO.getGovIdType()).thenReturn("NONUSTAXID");
		assertTrue(validator.validate(bankAccountDTO).isEmpty());
	}

}