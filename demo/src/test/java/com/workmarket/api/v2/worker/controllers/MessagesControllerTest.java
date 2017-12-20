package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.marshaller.AssignmentMarshaller;
import com.workmarket.api.v2.worker.model.AddMessageDTO;
import com.workmarket.api.v2.worker.model.CheckInDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class MessagesControllerTest extends BaseApiControllerTest {

	private static final String MESSAGES_URL = "/worker/v2/assignments/{workNumber}/messages";

	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@InjectMocks private MessagesController controller = new MessagesController();
	@Mock private XAssignment xAssignment;
	@Mock private AssignmentMarshaller assignmentMarshaller;

	private ObjectMapper jackson = new ObjectMapper();

	@Before
	public void setup() throws Exception {

		super.setup(controller);
		final Note note = new Note();
		note.setId(0L);
		note.setContent("hello");
		note.setCreatorId(1L);

		final NotePagination pagination = new NotePagination();

		List<Note> results = new ArrayList<>();
		results.add(note);

		pagination.setStartRow(0);
		pagination.setResultsLimit(50);
		pagination.setRowCount(1);
		pagination.setResults(results);

		when(xAssignment.getMessages(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																 anyString(),
																 anyInt(),
																 anyInt())).thenReturn(pagination);

		when(xAssignment.getMessages(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																 eq(INVALID_WORK_NUMBER),
																 anyInt(),
																 anyInt())).thenThrow(new RuntimeException("sss"));

		when(xAssignment.addMessage(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																anyString(),
																org.mockito.Matchers.isA(AddMessageDTO.class))).thenReturn(note);

		when(xAssignment.addMessage(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																eq(INVALID_WORK_NUMBER),
																org.mockito.Matchers.isA(AddMessageDTO.class))).thenThrow(new RuntimeException("sss"));

		when(assignmentMarshaller.getDomainModelNotes(any(List.class), any(Map.class))).thenReturn(Collections.singletonList(new ApiJSONPayloadMap()));
	}

	private ResultActions getMessages(final String workNumber) throws Exception {

		return mockMvc.perform(get(MESSAGES_URL, workNumber));
	}

	private ResultActions addMessage(final String workNumber, final Object addMessageDTO) throws Exception {

		final String addMessageJSON = jackson.writeValueAsString(addMessageDTO);

		return mockMvc.perform(post(MESSAGES_URL, workNumber).contentType(MediaType.APPLICATION_JSON)
																	 .content(addMessageJSON));
	}

	private AddMessageDTO getValidAddMessageDTO() {

		final AddMessageDTO addMessageDTO = new AddMessageDTO.Builder()
			.withMessage("hello")
			.build();

		return addMessageDTO;
	}

	private CheckInDTO getInvalidAddMessageDTO() {

		final CheckInDTO addMessageDTO = new CheckInDTO.Builder().build();

		//addMessageDTO.setMessage("hello");

		return addMessageDTO;
	}

	@Test
	public void getMessages_withValidWorkNumber_shouldReturn200Response() throws Exception {

		getMessages(VALID_WORK_NUMBER).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void getMessages_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		getMessages(INVALID_WORK_NUMBER).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}

	@Test
	public void addMessage_withValidWorkNumberAndValidDTO_shouldReturn200Response() throws Exception {

		final AddMessageDTO addMessageDTO = getValidAddMessageDTO();

		addMessage(VALID_WORK_NUMBER, addMessageDTO).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void addMessages_withValidWorkNumberAndInvalidDTO_shouldReturn500Response() throws Exception {

		final CheckInDTO addMessageDTO = getInvalidAddMessageDTO();

		addMessage(VALID_WORK_NUMBER, addMessageDTO).andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.meta.code", is(400)));
	}

	@Test
	public void addMessage_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		final AddMessageDTO addMessageDTO = getValidAddMessageDTO();

		addMessage(INVALID_WORK_NUMBER, addMessageDTO).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}

}
