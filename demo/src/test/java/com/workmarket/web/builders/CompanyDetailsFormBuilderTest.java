package com.workmarket.web.builders;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.service.business.CompanyService;
import com.workmarket.web.forms.account.CompanyDetailsForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompanyDetailsFormBuilderTest {

	@Mock CompanyService companyService;
	@InjectMocks CompanyDetailsFormBuilder companyDetailsFormBuilder;
	
	Company company;
	CompanyAssetAssociation avatars;
	Asset avatar;
	Address companyAddress;
	State state;
	Country country;
	DressCode dressCode;
	LocationType locationType;

	Long
		companyId = 1L,
		locationTypeID = LocationType.COMMERCIAL_CODE,
		dressCodeID = DressCode.BUSINESS_CASUAL;
	Integer
		employees = 3,
		yearFounded = 1999,
		employedProfs = 14;
	String
		name = "CO",
		overview = "overview",
		website = "website",
		uri = "www",
		address1 = "1",
		address2 = "2",
		city = "c",
		stateCode = "IL",
		postalCode = "z",
		countryCode = "USA";

	@Before
	public void setup() {
		company = mock(Company.class);
		avatars = mock(CompanyAssetAssociation.class);
		avatar = mock(Asset.class);
		companyAddress = mock(Address.class);
		state = mock(State.class);
		country = mock(Country.class);
		dressCode = mock(DressCode.class);
		locationType = mock(LocationType.class);

		when(company.getId()).thenReturn(companyId);
		when(company.getName()).thenReturn(name);
		when(company.getOverview()).thenReturn(overview);
		when(company.getWebsite()).thenReturn(website);
		when(company.getEmployees()).thenReturn(employees);
		when(company.getYearFounded()).thenReturn(yearFounded);
		when(company.getEmployedProfessionals()).thenReturn(employedProfs);
		
		when(companyService.findCompanyAvatars(companyId)).thenReturn(avatars);
		when(avatars.getSmall()).thenReturn(avatar);
		when(avatar.getCdnUri()).thenReturn(uri);

		when(companyAddress.getAddress1()).thenReturn(address1);
		when(companyAddress.getAddress2()).thenReturn(address2);
		when(companyAddress.getCity()).thenReturn(city);
		when(companyAddress.getState()).thenReturn(state);
		when(companyAddress.getPostalCode()).thenReturn(postalCode);
		when(companyAddress.getCountry()).thenReturn(country);
		when(companyAddress.getDressCode()).thenReturn(dressCode);
		when(companyAddress.getLocationType()).thenReturn(locationType);
		
		when(state.getShortName()).thenReturn(stateCode);
		when(country.getId()).thenReturn(countryCode);
		when(dressCode.getId()).thenReturn(dressCodeID);
		when(locationType.getId()).thenReturn(locationTypeID);
	}

	@Test
	public void build_nullCompany_returnForm() {
		assertNotNull(companyDetailsFormBuilder.build(null, companyAddress));
	}

	@Test
	public void build_nullCompanyAddress_returnForm() {
		assertNotNull(companyDetailsFormBuilder.build(company, null));
	}

	@Test
	public void build_nonNullCompanyAndCompanyAddress_returnFormWithAllProperties() {
		CompanyDetailsForm form = companyDetailsFormBuilder.build(company, companyAddress);

		assertEquals(form.getName(), company.getName());
		assertEquals(form.getOverview(), company.getOverview());
		assertEquals(form.getWebsite(), company.getWebsite());
		assertEquals(form.getEmployees(), company.getEmployees());
		assertEquals(form.getEmployedprofessionals(), company.getEmployedProfessionals());
		assertEquals(form.getYearfounded(), company.getYearFounded());
		assertEquals(form.getAvatar(), avatars.getSmall().getCdnUri());
		assertEquals(form.getAddress1(), companyAddress.getAddress1());
		assertEquals(form.getAddress2(), companyAddress.getAddress2());
		assertEquals(form.getCity(), companyAddress.getCity());
		assertEquals(form.getState(), companyAddress.getState().getShortName());
		assertEquals(form.getPostalCode(), companyAddress.getPostalCode());
		assertEquals(form.getCountry(), companyAddress.getCountry().getId());
		assertEquals(form.getDress_code(), companyAddress.getDressCode().getId());
		assertEquals(form.getLocation_type(), companyAddress.getLocationType().getId());
	}

	@Test
	public void build_noCompanyAvatars_dontSetAvatarOnForm() {
		when(companyService.findCompanyAvatars(companyId)).thenReturn(null);

		CompanyDetailsForm form = companyDetailsFormBuilder.build(company, companyAddress);

		assertNull(form.getAvatar());
	}

	@Test
	public void build_noSmallCompanyAvatar_dontSetAvatarOnForm() {
		when(avatars.getSmall()).thenReturn(null);

		CompanyDetailsForm form = companyDetailsFormBuilder.build(company, companyAddress);

		assertNull(form.getAvatar());
	}
}
