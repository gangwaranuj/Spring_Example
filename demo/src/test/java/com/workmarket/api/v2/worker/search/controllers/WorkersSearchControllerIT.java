package com.workmarket.api.v2.worker.search.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class WorkersSearchControllerIT extends ApiV2BaseIT {

	private static final String SEARCH_ENDPOINT = "/v2/workers/search";

	@Before
	public void setup() throws Exception {
		login();
		setCompanyFeatureToggle("workersSearchCompany", user.getCompany().getId());
	}

	@Test
	public void search_statusOk() throws Exception {
		mockMvc.perform(doGet(SEARCH_ENDPOINT)).andExpect(status().isOk());
	}

	@Test
	public void search_page() throws Exception {
		mockMvc.perform(doGet(SEARCH_ENDPOINT)).andExpect(jsonPath("$.meta.page", is(1)));
	}

	@Test
	public void search_pageSize() throws Exception {
		mockMvc.perform(doGet(SEARCH_ENDPOINT)).andExpect(jsonPath("$.meta.pageSize", is(25)));
	}
}
