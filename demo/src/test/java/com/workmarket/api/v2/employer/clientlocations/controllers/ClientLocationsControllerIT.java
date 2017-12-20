package com.workmarket.api.v2.employer.clientlocations.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.service.business.CRMService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ClientLocationsControllerIT extends ApiV2BaseIT {

	private static final String ENDPOINT = "/employer/v2/client_locations";

	@Autowired private CRMService crmService;
	private ClientCompany clientCompany;
	private ClientLocation clientLocation;

	@Before
	public void setUp() throws Exception {
		login();
		clientCompany = newClientCompany(user.getId());
		ClientContact clientContact = newClientContactForCompany(user.getCompany().getId());
		clientLocation = newClientLocationForClientCompany(user.getCompany().getId(), clientCompany.getId());
		clientContact.setClientLocation(clientLocation);
		crmService.addLocationToClientContact(clientContact.getId(), clientLocation.getId());
	}

	@Test
	public void getClientLocationByIdByName_Success() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("clientId", String.valueOf(clientCompany.getId()))
				.param("locationName", clientLocation.getName())
				.param("fields", "id", "name", "locationNumber")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		assertThat(result, hasEntry("id", String.valueOf(clientLocation.getId())));
		assertThat(result, hasEntry("name", String.valueOf(clientLocation.getName())));
		assertThat(result, hasEntry("locationNumber", String.valueOf(clientLocation.getLocationNumber())));
	}

	@Test
	public void getClientLocationByNameWithTwoFields_Success() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("locationName", clientLocation.getName())
				.param("fields", "id", "locationNumber")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		assertThat(result, hasEntry("id", String.valueOf(clientLocation.getId())));
		assertThat(result, hasEntry("locationNumber", String.valueOf(clientLocation.getLocationNumber())));
	}

	@Test
	public void getClientLocationByNoMatchingName_Success() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("locationName", RandomUtilities.generateAlphaNumericString(10))
				.param("fields", "id", "name", "locationNumber")
		).andExpect(status().isOk()).andReturn();

		List<Map<String, String>> result = getResults(mvcResult, mapType);
		assertThat(result, is(empty()));
	}
}
