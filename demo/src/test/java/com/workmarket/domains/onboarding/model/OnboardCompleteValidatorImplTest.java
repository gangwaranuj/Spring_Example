package com.workmarket.domains.onboarding.model;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyEmployeeCountRangeEnum;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.dto.IndustryDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OnboardCompleteValidatorImplTest {

	@Mock Profile profile;
	@Mock User user;
	@Mock Address address;
	@Mock Company company;
	@Mock IndustryService industryService;
	@Mock AddressService addressService;
	@InjectMocks OnboardCompleteValidatorImpl validator;

	@Before
	public void setUp() throws Exception {
		when(user.getEmail()).thenReturn("test@test.com");
		when(user.getFirstName()).thenReturn("Frank");
		when(user.getLastName()).thenReturn("Lloyd Wright");

		when(profile.getMobilePhone()).thenReturn("9174606351");
		when(profile.getSmsPhone()).thenReturn("9174606351");
		when(profile.getWorkPhone()).thenReturn("9174606351");
		when(profile.getOverview()).thenReturn("This profile overview is great");
		when(profile.getAddressId()).thenReturn(1L);
		IndustryDTO general = new IndustryDTO();
		general.setId(1060L);
		when(industryService.getIndustryDTOsForProfile(anyLong())).thenReturn(Sets.newHashSet(general));

		when(address.getLatitude()).thenReturn(new BigDecimal("70.0"));
		when(address.getLongitude()).thenReturn(new BigDecimal("40.0"));
		when(address.getAddress1()).thenReturn("303 East 12th Street");
		when(address.getAddress2()).thenReturn("");
		when(address.getState()).thenReturn(new State());
		when(address.getCity()).thenReturn("New York");
		when(address.getPostalCode()).thenReturn("10003");
		when(address.getCountry()).thenReturn(new Country("USA"));

		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.TRUE);
		when(company.getName()).thenReturn("Acme Inc.");
		when(company.getWebsite()).thenReturn("www.google.com");
		when(company.getCompanyEmployeeCountRangeEnum()).thenReturn(CompanyEmployeeCountRangeEnum.FIVETOTEN);
		when(company.getYearFounded()).thenReturn(2014);
		when(company.getOverview()).thenReturn("Acme Inc is a the worlds leader in acme products");

		when(profile.getUser()).thenReturn(user);
		when(addressService.findById(anyLong())).thenReturn(address);
	}

	@Test
	public void isCompleteIfProfileHasAllRequiredFields_web() throws Exception {
		assertTrue(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfEmailNull_web() throws Exception {
		when(user.getEmail()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfEmailEmpty_web() throws Exception {
		when(user.getEmail()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfFirstNameNull_web() throws Exception {
		when(user.getFirstName()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfFirstNameEmpty_web() throws Exception {
		when(user.getFirstName()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfLastNameNull_web() throws Exception {
		when(user.getLastName()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfLastNameEmpty_web() throws Exception {
		when(user.getLastName()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfAllPhoneNumbersNull_web() throws Exception {
		when(profile.getMobilePhone()).thenReturn(null);
		when(profile.getSmsPhone()).thenReturn(null);
		when(profile.getWorkPhone()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfAllEmptyPhoneNumbers_web() throws Exception {
		when(profile.getMobilePhone()).thenReturn("");
		when(profile.getSmsPhone()).thenReturn("");
		when(profile.getWorkPhone()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isCompleteIfYouOnlyHaveMobilePhone_web() throws Exception {
		when(profile.getSmsPhone()).thenReturn(null);
		when(profile.getWorkPhone()).thenReturn(null);
		assertTrue(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isCompleteIfYouOnlyHaveSmsPhone_web() throws Exception {
		when(profile.getMobilePhone()).thenReturn(null);
		when(profile.getWorkPhone()).thenReturn(null);
		assertTrue(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isCompleteIfYouOnlyHaveWorkPhone_web() throws Exception {
		when(profile.getMobilePhone()).thenReturn(null);
		when(profile.getSmsPhone()).thenReturn(null);
		assertTrue(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isCompleteIfYouHaveAddressAndNoGeocode_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		assertTrue(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isCompleteIfYouHaveGeocodeAndNoAddressNull_web() throws Exception {
		when(address.getAddress1()).thenReturn(null);
		when(address.getAddress2()).thenReturn(null);
		when(address.getState()).thenReturn(null);
		when(address.getCity()).thenReturn(null);
		when(address.getPostalCode()).thenReturn(null);
		when(address.getCountry()).thenReturn(null);
		assertTrue(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isCompleteIfYouHaveGeocodeAndNoAddressEmpty_web() throws Exception {
		when(address.getAddress1()).thenReturn("");
		when(address.getAddress2()).thenReturn("");
		when(address.getState()).thenReturn(null);
		when(address.getCity()).thenReturn("");
		when(address.getPostalCode()).thenReturn("");
		when(address.getCountry()).thenReturn(null);
		assertTrue(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndAddress1Null_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getAddress1()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndNoEmpty_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getAddress1()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndCityNull_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getCity()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndCityEmpty_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getCity()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndNoState_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getState()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndPostalCodeNull_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getPostalCode()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndPostalCodeEmpty_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getPostalCode()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndNoCountry_web() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getCountry()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfCompanyAndCompanyNameNull_web() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getName()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfCompanyAndCompanyNameEmpty_web() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getName()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfCompanyAndWebsiteNull_web() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getWebsite()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfCompanyAndWebsiteEmpty_web() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getWebsite()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfCompanyAndCompanyYearFoundedNull_web() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getYearFounded()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfCompanyAndCompanyOverviewNull_web() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getOverview()).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfCompanyAndCompanyOverviewEmpty_web() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getOverview()).thenReturn("");
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfIndustriesNull_web() throws Exception {
		when(industryService.getIndustryDTOsForProfile(anyLong())).thenReturn(null);
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfIndustriesEmpty_web() throws Exception {
		when(industryService.getIndustryDTOsForProfile(anyLong())).thenReturn(new HashSet<IndustryDTO>());
		assertFalse(validator.validateWeb(profile, company, true));
	}

	@Test
	public void isIncompleteIfIndustryIsNONE_web() throws Exception {
		IndustryDTO none = new IndustryDTO();
		none.setId(1L);
		when(industryService.getIndustryDTOsForProfile(anyLong())).thenReturn(Sets.newHashSet(none));
		assertFalse(validator.validateWeb(profile, company, true));
	}
	
	@Test
	public void isCompleteIfProfileHasAllRequiredFields_mobile() throws Exception {
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfEmailNull_mobile() throws Exception {
		when(user.getEmail()).thenReturn(null);
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfEmailEmpty_mobile() throws Exception {
		when(user.getEmail()).thenReturn("");
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfFirstNameNull_mobile() throws Exception {
		when(user.getFirstName()).thenReturn(null);
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfFirstNameEmpty_mobile() throws Exception {
		when(user.getFirstName()).thenReturn("");
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfLastNameNull_mobile() throws Exception {
		when(user.getLastName()).thenReturn(null);
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfLastNameEmpty_mobile() throws Exception {
		when(user.getLastName()).thenReturn("");
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfAllPhoneNumbersNull_mobile() throws Exception {
		when(profile.getMobilePhone()).thenReturn(null);
		when(profile.getSmsPhone()).thenReturn(null);
		when(profile.getWorkPhone()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfAllEmptyPhoneNumbers_mobile() throws Exception {
		when(profile.getMobilePhone()).thenReturn("");
		when(profile.getSmsPhone()).thenReturn("");
		when(profile.getWorkPhone()).thenReturn("");
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfYouOnlyHaveMobilePhone_mobile() throws Exception {
		when(profile.getSmsPhone()).thenReturn(null);
		when(profile.getWorkPhone()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfYouOnlyHaveSmsPhone_mobile() throws Exception {
		when(profile.getMobilePhone()).thenReturn(null);
		when(profile.getWorkPhone()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfYouOnlyHaveWorkPhone_mobile() throws Exception {
		when(profile.getMobilePhone()).thenReturn(null);
		when(profile.getSmsPhone()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfYouHaveAddressAndNoGeocode_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfYouHaveGeocodeAndNoAddressNull_mobile() throws Exception {
		when(address.getAddress1()).thenReturn(null);
		when(address.getAddress2()).thenReturn(null);
		when(address.getState()).thenReturn(null);
		when(address.getCity()).thenReturn(null);
		when(address.getPostalCode()).thenReturn(null);
		when(address.getCountry()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfYouHaveGeocodeAndNoAddressEmpty_mobile() throws Exception {
		when(address.getAddress1()).thenReturn("");
		when(address.getAddress2()).thenReturn("");
		when(address.getState()).thenReturn(null);
		when(address.getCity()).thenReturn("");
		when(address.getPostalCode()).thenReturn("");
		when(address.getCountry()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndAddress1Null_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getAddress1()).thenReturn(null);
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndNoEmpty_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getAddress1()).thenReturn("");
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndCityNull_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getCity()).thenReturn(null);
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndCityEmpty_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getCity()).thenReturn("");
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndNoState_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getState()).thenReturn(null);
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndPostalCodeNull_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getPostalCode()).thenReturn(null);
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndPostalCodeEmpty_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getPostalCode()).thenReturn("");
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isIncompleteIfNoGeocodeAndNoCountry_mobile() throws Exception {
		when(address.getLatitude()).thenReturn(null);
		when(address.getLongitude()).thenReturn(null);
		when(address.getCountry()).thenReturn(null);
		assertFalse(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfCompanyAndCompanyNameNull_mobile() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getName()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfCompanyAndCompanyNameEmpty_mobile() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getName()).thenReturn("");
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfCompanyAndWebsiteNull_mobile() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getWebsite()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfCompanyAndWebsiteEmpty_mobile() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getWebsite()).thenReturn("");
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfCompanyAndCompanyYearFoundedNull_mobile() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getYearFounded()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfCompanyAndCompanyOverviewNull_mobile() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getOverview()).thenReturn(null);
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isCompleteIfCompanyAndCompanyOverviewEmpty_mobile() throws Exception {
		when(company.getOperatingAsIndividualFlag()).thenReturn(Boolean.FALSE);
		when(company.getOverview()).thenReturn("");
		assertTrue(validator.validateMobile(profile, company));
	}

	@Test
	public void isNotIncompleteIfIndustriesNull_mobile() throws Exception {
		when(industryService.getIndustryDTOsForProfile(anyLong())).thenReturn(null);
		assertTrue("Expected empty industry to be allowed", validator.validateMobile(profile, company));
	}

	@Test
	public void isNotIncompleteIfIndustriesEmpty_mobile() throws Exception {
		when(industryService.getIndustryDTOsForProfile(anyLong())).thenReturn(new HashSet<IndustryDTO>());
		assertTrue("Expected empty industry to be allowed", validator.validateMobile(profile, company));
	}

	@Test
	public void isNotIncompleteIfIndustryIsNONE_mobile() throws Exception {
		IndustryDTO none = new IndustryDTO();
		none.setId(1L);
		when(industryService.getIndustryDTOsForProfile(anyLong())).thenReturn(Sets.newHashSet(none));
		assertTrue("Expected empty industry to be allowed", validator.validateMobile(profile, company));
	}
}
