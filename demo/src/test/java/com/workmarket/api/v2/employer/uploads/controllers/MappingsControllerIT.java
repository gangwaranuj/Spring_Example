package com.workmarket.api.v2.employer.uploads.controllers;

import com.google.api.client.util.Sets;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingsDTO;
import com.workmarket.api.v2.employer.uploads.services.UploadMappingService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.controllers.ControllerIT;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Set;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mapType;
import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.mappingsType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class MappingsControllerIT extends ControllerIT {
	@Autowired UploadMappingService uploadMappingService;

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void getMappings() throws Exception {
		MappingsDTO mappingsDTO = uploadMappingService.create(
			new MappingsDTO.Builder()
				.setName("Blargedy Blurgh Blaster")
				.build()
		);

		MvcResult mvcResult = mockMvc.perform(doGet("/employer/v2/mappings").param("fields", "id", "name"))
						.andExpect(status().isOk())
						.andReturn();

		Map<String, String> mappings = getFirstResult(mvcResult, mapType);

		assertThat(mappings.get("id"), is(String.valueOf(mappingsDTO.getId())));
		assertThat(mappings.get("name"), is(mappingsDTO.getName()));
	}

	@Test
	public void createMappings() throws Exception {
		MappingsDTO mappingsDTO = new MappingsDTO.Builder()
			.setName("Blargedy Blurgh Bingo")
			.build();

		String mappingsJson = jackson.writeValueAsString(mappingsDTO);

		MvcResult mvcResult = mockMvc.perform(doPost("/employer/v2/mappings").content(mappingsJson))
						.andExpect(status().isOk())
						.andReturn();

		MappingsDTO blarg = getFirstResult(mvcResult, mappingsType);

		assertThat(blarg, hasProperty("id", not(nullValue())));
		assertThat(blarg, hasProperty("name", is(mappingsDTO.getName())));
	}

	@Test
	public void getMapping() throws Exception {
		MappingsDTO mappingsDTO = uploadMappingService.create(
			new MappingsDTO.Builder()
				.setName("Blargedy Blurgh Blaster")
				.build()
		);

		MvcResult mvcResult = mockMvc.perform(doGet("/employer/v2/mappings/" + mappingsDTO.getId()))
						.andExpect(status().isOk())
						.andReturn();

		MappingsDTO mappings = getFirstResult(mvcResult, mappingsType);

		assertThat(mappings, samePropertyValuesAs(mappingsDTO));
	}

	@Test
	public void updateMappings() throws Exception {
		MappingsDTO mappingsDTO = uploadMappingService.create(
			new MappingsDTO.Builder()
				.setName("Blargedy Blurgh Blaster")
				.build()
		);

		MappingsDTO modifiedMappingsDTO = new MappingsDTO.Builder(mappingsDTO)
			.setName("BOOM")
			.build();

		String mappingsJson = jackson.writeValueAsString(modifiedMappingsDTO);

		MvcResult mvcResult = mockMvc.perform(doPut("/employer/v2/mappings/" + mappingsDTO.getId()).content(mappingsJson))
						.andExpect(status().isOk())
						.andReturn();

		MappingsDTO mappings = getFirstResult(mvcResult, mappingsType);

		assertThat(mappings, not(samePropertyValuesAs(mappingsDTO)));
		assertThat(mappings, samePropertyValuesAs(modifiedMappingsDTO));
	}

	@Test
	public void destroyMapping() throws Exception {
		MappingsDTO mappingsDTO = uploadMappingService.create(
			new MappingsDTO.Builder()
				.setName("Blargedy Blurgh Blaster")
				.build()
		);

		// DELETE
		MvcResult mvcResult = mockMvc.perform(doDelete("/employer/v2/mappings/" + mappingsDTO.getId()))
						.andExpect(status().isOk())
						.andReturn();

		// GET
		mvcResult = mockMvc.perform(doGet("/employer/v2/mappings/" + mappingsDTO.getId()))
						.andExpect(status().isForbidden())
						.andReturn();
	}

	@Test
	public void createMappingGroup() throws Exception {
		MappingsDTO mappingsDTO = createMappingGroupWithMappings();

		String mappingsJson = jackson.writeValueAsString(mappingsDTO);

		MvcResult mvcResult = mockMvc.perform(doPost("/employer/v2/mappings").content(mappingsJson))
						.andExpect(status().isOk())
						.andReturn();

		MappingsDTO blarg = getFirstResult(mvcResult, mappingsType);

		assertThat(blarg, hasProperty("id", not(nullValue())));
		assertThat(blarg, hasProperty("name", is(mappingsDTO.getName())));

	}

	@Test
	public void destroyMappingGroup_withMappings() throws Exception {

		MappingsDTO mappingsDTO = createMappingGroupWithMappings();

		String mappingsJson = jackson.writeValueAsString(mappingsDTO);

		// CREATE
		MvcResult mvcResult = mockMvc.perform(doPost("/employer/v2/mappings").content(mappingsJson))
						.andExpect(status().isOk())
						.andReturn();

		MappingsDTO created = getFirstResult(mvcResult, mappingsType);

		// DELETE
		mvcResult = mockMvc.perform(doDelete("/employer/v2/mappings/" + created.getId()))
						.andExpect(status().isOk())
						.andReturn();

		// GET
		mvcResult = mockMvc.perform(doGet("/employer/v2/mappings/" + created.getId()))
						.andExpect(status().isForbidden())
						.andReturn();
	}

	private MappingsDTO createMappingGroupWithMappings() {
		String titleHeader = "MyTitle";
		String descriptionHeader = "MyDescription";
		String instructionsHeader = "MyInstructions";
		String skillsHeader = "MySkills";
		String industryHeader = "MyIndustry";

		Set<MappingDTO.Builder> mappingSet = Sets.newHashSet();
		mappingSet.add(new MappingDTO.Builder().setProperty("title").setHeader(titleHeader));
		mappingSet.add(new MappingDTO.Builder().setProperty("description").setHeader(descriptionHeader));
		mappingSet.add(new MappingDTO.Builder().setProperty("instructions").setHeader(instructionsHeader));
		mappingSet.add(new MappingDTO.Builder().setProperty("desired_skills").setHeader(skillsHeader));
		mappingSet.add(new MappingDTO.Builder().setProperty("industry_id").setHeader(industryHeader));

		return new MappingsDTO.Builder()
			.setName("Test Mapping Group-" + System.currentTimeMillis())
			.setMappings(mappingSet)
			.build();
	}
}
