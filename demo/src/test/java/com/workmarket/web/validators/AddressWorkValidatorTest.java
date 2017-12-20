package com.workmarket.web.validators;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.infra.business.InvariantDataServiceImpl;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressWorkValidatorTest extends BaseValidatorTest {

	@Mock InvariantDataServiceImpl invariantDataService;
	@InjectMocks AddressWorkValidator validator;
	@Mock MessageBundleHelper messageBundleHelper;

	protected AddressDTO VALID_US_ADDRESS = getDefaultUSAddress();
	protected AddressDTO INVALID_US_ADDRESS_COUNTRY = getUSAddressInvalidCountry();
	protected AddressDTO INVALID_US_ADDRESS_CITY = getUSAddressInvalidCity();
	protected AddressDTO VALID_CAN_ADDRESS = getDefaultCanadianAddress();
	protected AddressDTO INVALID_PR_ADDRESS = getInvalidPuertoRicanAddress();
	protected AddressDTO VALID_PR_ADDRESS = getValidPuertoRicanAddress();

	@Before
	public void setup() {
		when(messageBundleHelper.getMessage(any(String.class))).thenReturn("Some validation message goes here");
		when(invariantDataService.findOrCreatePostalCode(VALID_US_ADDRESS)).thenReturn(createPostalCode(VALID_US_ADDRESS));
		when(invariantDataService.findOrCreatePostalCode(INVALID_US_ADDRESS_COUNTRY)).thenReturn(createPostalCode(INVALID_US_ADDRESS_COUNTRY));
		when(invariantDataService.findOrCreatePostalCode(INVALID_US_ADDRESS_CITY)).thenReturn(createPostalCode(INVALID_US_ADDRESS_CITY));
		when(invariantDataService.findOrCreatePostalCode(INVALID_US_ADDRESS_COUNTRY)).thenReturn(createPostalCode(INVALID_US_ADDRESS_COUNTRY));
		when(invariantDataService.findOrCreatePostalCode(VALID_CAN_ADDRESS)).thenReturn(createPostalCode(VALID_CAN_ADDRESS));
		when(invariantDataService.findOrCreatePostalCode(INVALID_PR_ADDRESS)).thenReturn(createPostalCode(VALID_PR_ADDRESS));
	}

	@Test
	public void validUSAddress() {
		assertFalse(validate(VALID_US_ADDRESS).hasErrors());
	}

	@Test
	public void validCanadianAddress() {
		assertFalse(validate(VALID_CAN_ADDRESS).hasErrors());
	}

	@Test
	public void validPuertoRicanAddress() {
		Errors result = validate(INVALID_PR_ADDRESS);
		assertFalse(result.hasErrors());
		assertEquals(INVALID_PR_ADDRESS.getCountry(), Country.USA);
		assertEquals(INVALID_PR_ADDRESS.getState(), Country.PR);
	}

	@Test
	public void invalidUSAddressCountry() {
		assertTrue(validate(INVALID_US_ADDRESS_COUNTRY).hasErrors());
	}

	@Test
	public void invalidUSAddressCity() {
		assertTrue(validate(INVALID_US_ADDRESS_CITY).hasErrors());
	}

	private AddressDTO getDefaultUSAddress() {
		AddressDTO a = new AddressDTO();
		a.setAddress1("20 West 20th Street");
		a.setAddress2("Suite 402");
		a.setCity("New York");
		a.setState("NY");
		a.setPostalCode("10010");
		a.setCountry("USA");
		return a;
	}

	private AddressDTO getUSAddressInvalidCountry() {
		AddressDTO a = getDefaultUSAddress();
		a.setCountry(null);
		return a;
	}
	private AddressDTO getUSAddressInvalidCity() {
		AddressDTO a = getDefaultUSAddress();
		a.setCity(null);
		return a;
	}

	private AddressDTO getDefaultCanadianAddress() {
		AddressDTO a = new AddressDTO();
		a.setAddress1("Bank of Canada");
		a.setAddress2("234 Wellington Street");
		a.setCity("Ottawa");
		a.setState("ON");
		a.setPostalCode("K1A 0G9");
		a.setCountry("CAN");
		return a;
	}

	private AddressDTO getInvalidPuertoRicanAddress() {
		AddressDTO a = new AddressDTO();
		a.setAddress1("123 Main St");
		a.setCity("San Juan");
		a.setState("San Juan");
		a.setPostalCode("00901");
		a.setCountry("PR");
		return a;
	}

	private AddressDTO getValidPuertoRicanAddress() {
		AddressDTO a = getInvalidPuertoRicanAddress();
		a.setState("PR");
		a.setCountry("USA");
		return a;
	}

	private PostalCode createPostalCode(AddressDTO a) {
		PostalCode p = new PostalCode();
		State state = new State();
		state.setShortName(a.getState());
		p.setStateProvince(state);
		p.setCountry(Country.valueOf(a.getCountry()));
		return p;
	}

	protected Validator getValidator() {
	    return validator;
	}

}
