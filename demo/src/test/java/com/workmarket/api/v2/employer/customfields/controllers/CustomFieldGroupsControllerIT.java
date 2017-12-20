package com.workmarket.api.v2.employer.customfields.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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
public class CustomFieldGroupsControllerIT extends ApiV2BaseIT {
	private static final String ENDPOINT = "/employer/v2/custom_field_groups";

	private WorkCustomFieldGroup customFieldGroup;

	@Before
	public void setUp() throws Exception {
		login();

		customFieldGroup = createCustomFieldGroup(user.getId());
	}

	@Test
	public void getCustomFieldGroupsWithFields() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "id", "name", "required")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasEntry("id", String.valueOf(customFieldGroup.getId())));
		assertThat(result, hasEntry("name", String.valueOf(customFieldGroup.getName())));
		assertThat(result, hasEntry("required", String.valueOf(customFieldGroup.isRequired())));
	}

	@Test
	public void getCustomFieldGroupsWithOneField() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "id")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, hasEntry("id", String.valueOf(customFieldGroup.getId())));
		assertThat(result, not(hasKey("name")));
		assertThat(result, not(hasKey("required")));
	}

	@Test
	public void getCustomFieldGroupsWithTwoFields() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "name", "required")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, not(hasKey("id")));
		assertThat(result, hasEntry("name", String.valueOf(customFieldGroup.getName())));
		assertThat(result, hasEntry("required", String.valueOf(customFieldGroup.isRequired())));
	}

	@Test
	public void getCustomFieldGroupsWithWonkyField() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "wonkyField")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);

		assertThat(result, not(hasKey("wonkyField")));
	}

	@Test
	public void getCustomFieldsByGroup() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(String.format(ENDPOINT + "/%s/custom_fields", customFieldGroup.getId()))
				.param("fields", "id", "name", "defaultValue", "required", "type")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		WorkCustomField customField = customFieldGroup.getWorkCustomFields().get(0);
		assertThat(result, hasEntry("id", String.valueOf(customField.getId())));
		assertThat(result, hasEntry("name", String.valueOf(customField.getName())));
		assertThat(result, customField.getDefaultValue() == null ?
			hasEntry("defaultValue", String.valueOf("")) :
			hasEntry("defaultValue", String.valueOf(customField.getDefaultValue())));
		assertThat(result, hasEntry("required", String.valueOf(String.valueOf(customField.getRequiredFlag()))));
		assertThat(result, hasEntry("type", String.valueOf(String.valueOf(customField.getWorkCustomFieldType().getCode()))));
	}
}
