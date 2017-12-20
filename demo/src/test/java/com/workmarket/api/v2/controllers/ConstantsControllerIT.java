package com.workmarket.api.v2.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Map;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ConstantsControllerIT extends ApiV2BaseIT {

	private static final String ENDPOINT = "/v2/constants/%s";
	private static final String POSTFIX_COUNTRY_CODES = "country_codes";
	private static final String POSTFIX_COUNTRIES = "countries";

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void getCountryCodes_Success() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(String.format(ENDPOINT, POSTFIX_COUNTRY_CODES))
				.param("fields", "id", "name")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		assertThat(result, hasKey("id"));
		assertThat(result, hasKey("name"));
		assertThat(result, hasValue(not("")));
	}

	@Test
	public void getAllCountries_Success() throws Exception{
		MvcResult mvcResult = mockMvc.perform(
			doGet(String.format(ENDPOINT, POSTFIX_COUNTRIES))
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		assertTrue(result.size() > 0);
		assertThat(result, hasKey(not("")));
		assertThat(result, hasValue(not("")));
	}
}
