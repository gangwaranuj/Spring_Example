package com.workmarket.api.v2.employer.settings.controllers;

import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.settings.controllers.support.CompanyProfileMaker;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.dto.AddressDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.companyProfileType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static com.workmarket.api.v2.employer.settings.controllers.support.CompanyProfileMaker.overview;
import static com.workmarket.api.v2.employer.settings.controllers.support.CompanyProfileMaker.website;
import static com.workmarket.api.v2.employer.settings.controllers.support.CompanyProfileMaker.workInviteSentToUserId;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CompanyProfileControllerIT extends ApiV2BaseIT {

	private static final String ENDPOINT = "/employer/v2/settings/profile";

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void saveCompanyProfileWithEmptyOverview() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO, withNull(overview)));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "overview", "Overview is a required field.");
	}

	@Test
	public void saveCompanyProfileWithOverviewExceededMaxLength() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO, with(overview, RandomStringUtils.random(1001))));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "overview", "Overview field must be less than 1000 characters.");
	}

	@Test
	public void saveCompanyProfileWithWebsiteExceededMaxLength() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO, with(website, RandomStringUtils.random(256))));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "website", "Website field must be less than 255 characters.");
	}

	@Test
	public void saveCompanyProfileWithEmptyAddress() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithEmptyAddressLine1));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "address1", "Address is a required field.");
	}

	@Test
	public void saveCompanyProfileWithEmptyCity() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithEmptyCity));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "city", "This location does not have a valid city.");
	}

	@Test
	public void saveCompanyProfileWithEmptyCountry() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithEmptyCountry));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		ApiBaseError result = getFirstResult(mvcResult, errorType);
		expectApiErrorMessage(result, "country", "This location does not have a valid country.");
	}

	@Test
	public void saveCompanyProfileWithEmptyLatLong() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithEmptyLatLong));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		List<ApiBaseError> results = getResults(mvcResult, errorType);
		for (ApiBaseError result : results) {
			assertThat(result, hasProperty("field", anyOf(is("state"), is("postalCode"))));
			assertThat(result, hasProperty("message", anyOf(is("State is a required field."), is("This location does not have a valid postal code."))));
		}
	}

	@Test
	public void saveCompanyProfileWithLatLonZeros() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithLatLonZeros));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson))
						.andExpect(status().isBadRequest())
						.andReturn();

		List<ApiBaseError> results = getResults(mvcResult, errorType);
		for (ApiBaseError result : results) {
			assertThat(result, hasProperty("field", anyOf(is("state"), is("postalCode"))));
			assertThat(result, hasProperty("message", anyOf(is("State is a required field."), is("This location does not have a valid postal code."))));
		}
	}

	@Test
	public void saveCompanyProfileWithEmptyStateSuccess() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithEmptyState));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		mockMvc.perform(
			doPost(ENDPOINT).content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();
	}

	@Test
	public void saveCompanyProfileSuccess() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT).content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();

		CompanyProfileDTO result = getFirstResult(mvcResult, companyProfileType);
		assertThat(result, hasProperty("overview", is(companyProfileDTO.getOverview())));
		assertThat(result, hasProperty("website", is(companyProfileDTO.getWebsite())));
		assertThat(result, hasProperty("location", hasProperty("addressLine1", is(companyProfileDTO.getLocation().getAddressLine1()))));
		assertThat(result, hasProperty("location", hasProperty("city", is(companyProfileDTO.getLocation().getCity()))));
		assertThat(result, hasProperty("location", hasProperty("state", is(companyProfileDTO.getLocation().getState()))));
		assertThat(result, hasProperty("location", hasProperty("country", is(companyProfileDTO.getLocation().getCountry()))));
		assertThat(result, hasProperty("location", hasProperty("zip", is(companyProfileDTO.getLocation().getZip()))));
		assertThat(result, hasProperty("location", hasProperty("latitude", is(companyProfileDTO.getLocation().getLatitude()))));
		assertThat(result, hasProperty("location", hasProperty("longitude", is(companyProfileDTO.getLocation().getLongitude()))));
		assertThat(result, hasProperty("yearFounded", is(companyProfileDTO.getYearFounded())));
		assertThat(result, hasProperty("workInviteSentToUserId", is(companyProfileDTO.getWorkInviteSentToUserId())));
		assertThat(result, hasProperty("inVendorSearch", is(companyProfileDTO.getInVendorSearch())));
		assertTrue(authenticationService.userHasAclRole(getDispatcherUser(companyProfileDTO).getId(), AclRole.ACL_DISPATCHER));
		assertTrue(authenticationService.userHasAclRole(getDispatcherUser(companyProfileDTO).getId(), AclRole.ACL_WORKER));
	}

	@Test
	public void saveCompanyProfileWithEmptyLocationSuccess() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithEmptyLocation));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
																									.content(companyProfileJson)).andExpect(status().isOk()).andReturn();

		CompanyProfileDTO result = getFirstResult(mvcResult, companyProfileType);
		assertThat(result, hasProperty("overview", is(companyProfileDTO.getOverview())));
		assertThat(result, hasProperty("website", is(companyProfileDTO.getWebsite())));
		assertThat(result, hasProperty("location", hasProperty("addressLine1", is(companyProfileDTO.getLocation().getAddressLine1()))));
		assertThat(result, hasProperty("location", hasProperty("city", is(companyProfileDTO.getLocation().getCity()))));
		assertThat(result, hasProperty("location", hasProperty("state", is(companyProfileDTO.getLocation().getState()))));
		assertThat(result, hasProperty("location", hasProperty("country", is(companyProfileDTO.getLocation().getCountry()))));
		assertThat(result, hasProperty("location", hasProperty("zip", is(companyProfileDTO.getLocation().getZip()))));
		assertThat(result, hasProperty("location", hasProperty("latitude", is(companyProfileDTO.getLocation().getLatitude()))));
		assertThat(result, hasProperty("location", hasProperty("longitude", is(companyProfileDTO.getLocation().getLongitude()))));
		assertThat(result, hasProperty("yearFounded", is(companyProfileDTO.getYearFounded())));
		assertThat(result, hasProperty("workInviteSentToUserId", is(companyProfileDTO.getWorkInviteSentToUserId())));
		assertThat(result, hasProperty("inVendorSearch", is(companyProfileDTO.getInVendorSearch())));
		assertTrue(authenticationService.userHasAclRole(getDispatcherUser(companyProfileDTO).getId(), AclRole.ACL_DISPATCHER));
		assertTrue(authenticationService.userHasAclRole(getDispatcherUser(companyProfileDTO).getId(), AclRole.ACL_WORKER));
	}

	@Test
	public void saveCompanyProfileWithVerifications() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithVerifications));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
				.content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();

		CompanyProfileDTO result = getFirstResult(mvcResult, companyProfileType);
		assertThat(result, hasProperty("drugTest", is(companyProfileDTO.getDrugTest())));
		assertThat(result, hasProperty("backgroundCheck", is(companyProfileDTO.getBackgroundCheck())));
	}

	@Test
	public void saveCompanyProfileWithLocationsServiced() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithLocationsServiced));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
				.content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();

		CompanyProfileDTO result = getFirstResult(mvcResult, companyProfileType);
		assertThat(result.getLocationsServiced(), hasSize(companyProfileDTO.getLocationsServiced().size()));

		Company company = companyService.findById(authenticationService.getCurrentUserCompanyId());
		mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + company.getCompanyNumber())
				.content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();

		result = getFirstResult(mvcResult, companyProfileType);
		assertThat(result.getLocationsServiced(), hasSize(1));
	}

	@Test
	public void saveCompanyProfileWithSkills() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTOWithSkills));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
				.content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();

		CompanyProfileDTO result = getFirstResult(mvcResult, companyProfileType);
		assertThat(result.getSkills(), hasSize(companyProfileDTO.getSkills().size()));

		Company company = companyService.findById(authenticationService.getCurrentUserCompanyId());
		mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + company.getCompanyNumber())
				.content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();

		result = getFirstResult(mvcResult, companyProfileType);
		assertThat(result.getSkills(), hasSize(1));
	}

	@Test
	public void saveCompanyProfile_userMissingAddress_setToCompanyAddress() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
				.content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();

		getFirstResult(mvcResult, companyProfileType);

		Profile userProfile = profileService.findProfile(user.getId());
		Address address = addressService.findById(userProfile.getAddressId());
		assertEquals(address.getAddress1(), companyProfileDTO.getLocation().getAddressLine1());
		assertEquals(address.getAddress2(), companyProfileDTO.getLocation().getAddressLine2());
		assertEquals(address.getCity(), companyProfileDTO.getLocation().getCity());
		assertEquals(address.getState().getShortName(), companyProfileDTO.getLocation().getState());
		assertEquals(address.getPostalCode(), companyProfileDTO.getLocation().getZip());
		assertEquals(address.getCountry().getISO3(), companyProfileDTO.getLocation().getCountry());
	}

	@Test
	public void saveCompanyProfile_userHasAddress_notChanged() throws Exception {

		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1("7 High St");
		addressDTO.setAddress2("Suite 407");
		addressDTO.setCity("Huntington");
		addressDTO.setState("NY");
		addressDTO.setPostalCode("11743");
		addressDTO.setCountry("USA");

		Map<String, String> addressProperties = CollectionUtilities.newStringMap(
			"address1", addressDTO.getAddress1(),
			"address2", addressDTO.getAddress2(),
			"city", addressDTO.getCity(),
			"state", addressDTO.getState(),
			"postalCode", addressDTO.getPostalCode(),
			"country", addressDTO.getCountry(),
			"latitude", String.valueOf(addressDTO.getLatitude()),
			"longitude", String.valueOf(addressDTO.getLongitude()),
			"addressType", "profile");
		profileService.updateProfileAddressProperties(user.getId(), addressProperties);

		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
				.content(companyProfileJson)
		).andExpect(status().isOk()).andReturn();

		getFirstResult(mvcResult, companyProfileType);

		Profile userProfile = profileService.findProfile(user.getId());
		Address address = addressService.findById(userProfile.getAddressId());
		assertEquals(address.getAddress1(), addressDTO.getAddress1());
		assertEquals(address.getAddress2(), addressDTO.getAddress2());
		assertEquals(address.getCity(), addressDTO.getCity());
		assertEquals(address.getState().getShortName(), addressDTO.getState());
		assertEquals(address.getPostalCode(), addressDTO.getPostalCode());
		assertEquals(address.getCountry().getISO3(), addressDTO.getCountry());
	}

	public void getCompanyProfileWithWorkInviteSentTo() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO, with(workInviteSentToUserId, this.user.getUserNumber())));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(companyProfileJson))
			.andExpect(status().isOk())
			.andReturn();
		CompanyProfileDTO result = getFirstResult(mvcResult, companyProfileType);

		mvcResult = mockMvc.perform(doGet(ENDPOINT)
			.content(companyProfileJson)).andExpect(status().isOk()).andReturn();
		result = getFirstResult(mvcResult, companyProfileType);
		assertEquals(result.getWorkInviteSentToUserId(), companyProfileDTO.getWorkInviteSentToUserId());
	}

	@Test
	public void getCompanyProfileWithoutWorkInviteSentTo() throws Exception {
		CompanyProfileDTO companyProfileDTO = make(a(CompanyProfileMaker.CompanyProfileDTO, withNull(workInviteSentToUserId)));
		String companyProfileJson = jackson.writeValueAsString(companyProfileDTO);

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
			.content(companyProfileJson))
			.andExpect(status().isOk())
			.andReturn();
		CompanyProfileDTO result = getFirstResult(mvcResult, companyProfileType);

		mvcResult = mockMvc.perform(doGet(ENDPOINT)
			.content(companyProfileJson)).andExpect(status().isOk()).andReturn();
		result = getFirstResult(mvcResult, companyProfileType);
		assertNull(result.getWorkInviteSentToUserId());
	}

	private User getDispatcherUser(CompanyProfileDTO companyProfileDTO) {
		return userService.findUserByUserNumber(companyProfileDTO.getWorkInviteSentToUserId());
	}
}
