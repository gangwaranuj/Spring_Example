package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ConfirmControllerTest extends BaseApiControllerTest {

	private static final String CONFIRM_URL = "/worker/v2/assignments/confirm/{workNumber}";
	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@InjectMocks private ConfirmController controller = new ConfirmController();
	@Mock private XAssignment xAssignment;

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		final List<Object> processingResult = new ArrayList<>();

		when(xAssignment.confirm(org.mockito.Matchers.isA(ExtendedUserDetails.class), anyString())).thenReturn(
						processingResult);

		when(xAssignment.confirm(org.mockito.Matchers.isA(ExtendedUserDetails.class), eq(INVALID_WORK_NUMBER))).thenThrow(
						new RuntimeException("sss"));
	}

	private ResultActions confirm(String workNumber) throws Exception {

		return mockMvc.perform(post(CONFIRM_URL, workNumber));
	}

	@Test
	public void confirm_withValidWorkNumber_shouldReturn200Response() throws Exception {

		confirm(VALID_WORK_NUMBER).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void confirm_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		confirm(INVALID_WORK_NUMBER).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}
}
