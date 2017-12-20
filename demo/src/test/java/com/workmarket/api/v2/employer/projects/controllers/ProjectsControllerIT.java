package com.workmarket.api.v2.employer.projects.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ProjectsControllerIT extends ApiV2BaseIT {

	private static final String ENDPOINT = "/employer/v2/projects";
	
	private Project project;

	@Before
	public void setUp() throws Exception {
		login();
		ClientCompany clientCompany = newClientCompany(user.getId());
		project = newProject(user.getId(), clientCompany.getId(), "Project Title");
	}

	@Test
	public void getAllCompanyProjects_Success() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
				.param("fields", "id", "name")
		).andExpect(status().isOk()).andReturn();

		Map<String, String> result = getFirstResult(mvcResult, mapType);
		assertThat(result, hasEntry("id", String.valueOf(project.getId())));
		assertThat(result, hasEntry("name", String.valueOf(project.getName())));
	}
}
