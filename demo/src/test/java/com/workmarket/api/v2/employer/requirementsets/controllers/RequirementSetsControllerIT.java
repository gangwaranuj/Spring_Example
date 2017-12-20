package com.workmarket.api.v2.employer.requirementsets.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.service.business.requirementsets.RequirementSetsService;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RequirementSetsControllerIT extends ApiV2BaseIT {
	private static final String ENDPOINT = "/employer/v2/requirement_sets";

	@Autowired private RequirementSetsService requirementSetService;

	private RequirementSet requirementSet;

	@Before
	public void setUp() throws Exception {
		login();

		requirementSet = new RequirementSet();
		requirementSet.setName("Requirement Set");
		requirementSet.setCompany(user.getCompany());
		requirementSet.setActive(true);
		requirementSetService.save(requirementSet);
	}

	@Test
	public void getRequirementSetsWithFields() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "id", "name", "active")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasEntry("id", String.valueOf(requirementSet.getId())));
		assertThat(result, hasEntry("name", String.valueOf(requirementSet.getName())));
		assertThat(result, hasEntry("active", String.valueOf(requirementSet.isActive())));
	}

	@Test
	public void getRequirementSetsWithOneField() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "id")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasEntry("id", String.valueOf(requirementSet.getId())));
		assertThat(result, not(hasKey("name")));
		assertThat(result, not(hasKey("active")));
	}

	@Test
	public void getRequirementSetsWithTwoFields() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "name", "active")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, not(hasKey("id")));
		assertThat(result, hasEntry("name", String.valueOf(requirementSet.getName())));
		assertThat(result, hasEntry("active", String.valueOf(requirementSet.isActive())));
	}

	@Test
	public void getRequirementSetsWithWonkyField() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "wonkyField")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, not(hasKey("wonkyField")));
	}
}
