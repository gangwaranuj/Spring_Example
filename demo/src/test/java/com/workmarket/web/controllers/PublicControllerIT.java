package com.workmarket.web.controllers;

import com.workmarket.api.v2.ApiV2BaseIT;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class PublicControllerIT extends ApiV2BaseIT {

	private static final String LOGIN_STATUS_ENDPOINT = "/login-status";

	@Test
	public void loginStatus_unauthenticated() throws Exception {
		mockMvc.perform(
				doGet(LOGIN_STATUS_ENDPOINT))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isAuthenticated", is(false)));
	}
}
