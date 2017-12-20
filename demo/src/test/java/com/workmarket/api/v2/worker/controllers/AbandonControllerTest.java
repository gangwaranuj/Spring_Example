package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.model.AbandonAssignmentDTO;
import com.workmarket.api.v2.worker.model.validator.AbandonAssignmentDTOValidator;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class AbandonControllerTest extends BaseApiControllerTest {

	private static final String ABANDON_URL = "/worker/v2/assignments/{workNumber}/abandon";
	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@Mock
	private XAssignment xAssignment;
	@Mock
	private AbandonAssignmentDTOValidator abandonAssignmentDTOValidator;
	@InjectMocks
	private AbandonController controller = spy(new AbandonController());

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		mockMvc = getMockMvc(controller);

		final List<Object> processingResult = new ArrayList<>();

		when(xAssignment.abandonAssignment(org.mockito.Matchers.isA(ExtendedUserDetails.class), anyString(), (org.mockito.Matchers.isA(AbandonAssignmentDTO.class)))).thenReturn(
				processingResult);
		when(xAssignment.abandonAssignment(org.mockito.Matchers.isA(ExtendedUserDetails.class), eq(INVALID_WORK_NUMBER), (org.mockito.Matchers.isA(AbandonAssignmentDTO.class)))).thenThrow(
				new RuntimeException("sss"));

	}

	private ResultActions abandon(final String workNumber, final Object abandonAssignmentDto) throws Exception {

		final String messageJson = jackson.writeValueAsString(abandonAssignmentDto);

		return mockMvc.perform(post(ABANDON_URL, workNumber).contentType(MediaType.APPLICATION_JSON)
				.content(messageJson));

	}

	private AbandonAssignmentDTO getValidAbandonDto() {

		final AbandonAssignmentDTO abandonAssignmentDTO = new AbandonAssignmentDTO.Builder().withMessage("Hello.").build();

		return abandonAssignmentDTO;

	}

	private AbandonAssignmentDTO getInvalidAbandonDto() {

		final AbandonAssignmentDTO abandonAssignmentDTO = new AbandonAssignmentDTO.Builder().withMessage("").build();

		return abandonAssignmentDTO;

	}


	@Test
	public void abandon_withValidWorkNumber_shouldReturn200Response() throws Exception {

		final AbandonAssignmentDTO abandonAssignmentDto = getValidAbandonDto();

		doCallRealMethod().when(abandonAssignmentDTOValidator).validate(anyString(), any(BindingResult.class));

		abandon(VALID_WORK_NUMBER, abandonAssignmentDto).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void abandon_withValidWorkNumber_shouldReturn400Response() throws Exception {

		final AbandonAssignmentDTO abandonAssignmentDto = getInvalidAbandonDto();

		doCallRealMethod().when(abandonAssignmentDTOValidator).validate(anyString(), any(BindingResult.class));

		abandon(VALID_WORK_NUMBER, abandonAssignmentDto).andExpect(status().is4xxClientError()).andExpect(jsonPath("$.meta.code", is(400)));
	}

	@Test
	public void abandon_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		final AbandonAssignmentDTO abandonAssignmentDto = getValidAbandonDto();

		abandon(INVALID_WORK_NUMBER, abandonAssignmentDto).andExpect(status().isInternalServerError()).andExpect(jsonPath("$.meta.code", is(500)));
	}
}