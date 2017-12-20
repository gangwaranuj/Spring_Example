package com.workmarket.api.v2.employer.uploads.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkUploadLabelsControllerIT extends ApiV2BaseIT {
	private static final String ENDPOINT = "/employer/v2/labels";

	private WorkSubStatusTypeDTO workSubStatusType;

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void getWorkUploadLabels() throws Exception {

		createWorkSubStatusType( user.getCompany().getId());

		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "code", "description")
		).andExpect(status().isOk()).andReturn();

		List<Map<String, String>> results = getResults(mvcResult, mapType);
		assertEquals(2, results.size());
	}

	@Test
	public void getWorkUploadLabel() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "id", "code", "description", "dashboardDisplayType", "subStatusDescriptor")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		assertThat(result, hasKey("code"));
		assertThat(result, hasKey("description"));
		assertThat(result, hasKey("dashboardDisplayType"));
		assertThat(result, hasKey("subStatusDescriptor"));
	}
}
