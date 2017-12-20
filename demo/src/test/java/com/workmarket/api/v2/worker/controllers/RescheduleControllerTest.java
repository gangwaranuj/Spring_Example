package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.model.RescheduleDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class RescheduleControllerTest extends BaseApiControllerTest {

	private static final String RESCHEDULE_URL = "/worker/v2/assignments/reschedule/{workNumber}";
	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@InjectMocks private RescheduleController controller = new RescheduleController();
	@Mock private XAssignment xAssignment;

	private ObjectMapper jackson = new ObjectMapper();

	@Before
	public void setup() throws Exception {

		super.setup(controller);
		final List<Object> processingResult = new ArrayList<>();

		when(xAssignment.reschedule(anyString(),
																org.mockito.Matchers.isA(RescheduleDTO.class))).thenReturn(processingResult);

		when(xAssignment.reschedule(eq(INVALID_WORK_NUMBER),
																org.mockito.Matchers.isA(RescheduleDTO.class))).thenThrow(new RuntimeException("sss"));
	}

	private ResultActions reschedule(final String workNumber, final RescheduleDTO rescheduleDTO) throws Exception {

		final String rescheduleJSON = jackson.writeValueAsString(rescheduleDTO);

		return mockMvc.perform(post(RESCHEDULE_URL, workNumber).contentType(MediaType.APPLICATION_JSON)
																	 .content(rescheduleJSON));
	}

	private RescheduleDTO getStartRescheduleDTO() {

		final RescheduleDTO rescheduleDTO = new RescheduleDTO.Builder()
			.withStart(0L)
			//.withEnd(0L)
			//.withStartWindowBegin(0L)
			//.withStartWindowEnd(0L)
			.withNote("This is my note")
			.build();

		return rescheduleDTO;
	}

	private RescheduleDTO getStartWindowRescheduleDTO() {

		final RescheduleDTO rescheduleDTO = new RescheduleDTO.Builder()
			//.withStart(0L)
			//.withEnd(0L)
			.withStartWindowBegin(0L)
			.withStartWindowEnd(0L)
			.withNote("This is my note")
			.build();

		return rescheduleDTO;
	}

	@Test
	public void reschedule_withValidWorkNumber_shouldReturn200Response() throws Exception {

		final RescheduleDTO rescheduleDTO = new RescheduleDTO.Builder().build();

		reschedule(VALID_WORK_NUMBER, rescheduleDTO).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void reschedule_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		final RescheduleDTO rescheduleDTO = new RescheduleDTO.Builder().build();
		final Long onBehalfOf = null;

		reschedule(INVALID_WORK_NUMBER, rescheduleDTO).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}
}
