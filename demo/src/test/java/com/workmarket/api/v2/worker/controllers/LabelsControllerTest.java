package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.model.AddLabelDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class LabelsControllerTest extends BaseApiControllerTest {

	private static final String GET_LABELS_URL = "/worker/v2/assignments/{workNumber}/labels";
	private static final String ADD_LABEL_URL = "/worker/v2/assignments/{workNumber}/labels/{labelId}";
	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";
	private static final Long VALID_LABEL_ID = 0L;

	@Mock private XAssignment xAssignment;
	@InjectMocks private LabelsController controller = new LabelsController();

	private ObjectMapper jackson = new ObjectMapper();

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		mockMvc = getMockMvc(controller);

		List<Object> processingResult = new ArrayList<>();

		RuntimeException invalidWorkNumberException = new RuntimeException("MockException: Invalid work number");
		when(xAssignment.getLabels(org.mockito.Matchers.isA(ExtendedUserDetails.class), anyString())).thenReturn(
						processingResult);
		when(xAssignment.getLabels(org.mockito.Matchers.isA(ExtendedUserDetails.class), eq(INVALID_WORK_NUMBER))).thenThrow(
						invalidWorkNumberException);
		when(xAssignment.addLabel(org.mockito.Matchers.isA(ExtendedUserDetails.class),
															eq(INVALID_WORK_NUMBER),
															anyLong(),
															org.mockito.Matchers.isA(AddLabelDTO.class),
															org.mockito.Matchers.isA(BindingResult.class))).thenThrow(invalidWorkNumberException);
	}

	private ResultActions getLabels(final String workNumber) throws Exception {

		return mockMvc.perform(get(GET_LABELS_URL, workNumber));
	}

	private ResultActions addLabel(final String workNumber, final Long labelId, final AddLabelDTO addLabelDTO) throws
																																																						 Exception {

		final String addLabelJSON = jackson.writeValueAsString(addLabelDTO);

		return mockMvc.perform(post(ADD_LABEL_URL, workNumber, labelId).contentType(MediaType.APPLICATION_JSON)
																	 .content(addLabelJSON));
	}

	private AddLabelDTO getValidAddLabelDTO() {

		final AddLabelDTO addLabelDTO = new AddLabelDTO.Builder().build();

		return addLabelDTO;
	}

	private AddLabelDTO getInvalidAddLabelDTO() {

		final AddLabelDTO addLabelDTO = new AddLabelDTO.Builder().build();

		return addLabelDTO;
	}

	@Test
	public void getLabels_withValidWorkNumber_shouldReturn200Response() throws Exception {

		getLabels(VALID_WORK_NUMBER).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void getLabels_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		getLabels(INVALID_WORK_NUMBER).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}

	@Test
	public void addLabel_withValidWorkNumberAndValidDTO_shouldReturn200Response() throws Exception {

		final AddLabelDTO addLabelDTO = getValidAddLabelDTO();

		addLabel(VALID_WORK_NUMBER, VALID_LABEL_ID, addLabelDTO).andExpect(status().isOk())
						.andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void addLabel_withValidWorkNumberAndInvalidDTO_shouldReturn500Response() throws Exception {

		final AddLabelDTO addLabelDTO = getInvalidAddLabelDTO();

		addLabel(VALID_WORK_NUMBER, VALID_LABEL_ID, addLabelDTO).andExpect(status().isOk())
						.andExpect(jsonPath("$.meta.code", is(200)));
	}
}
