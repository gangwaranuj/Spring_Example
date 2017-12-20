package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.model.AddDeliverableDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.asset.Asset;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class DeliverableControllerTest extends BaseApiControllerTest {

	//private static final String ADD_DELIVERABLE_URL = "/worker/v2/assignments/{workNumber}/deliverables/{deliverableRequirementId}";
	private static final String ADD_DELIVERABLE_URL = "/worker/v2/assignments/{workNumber}/deliverables";
	private static final String REPLACE_DELIVERABLE_URL = "/worker/v2/assignments/{workNumber}/deliverables/{deliverableRequirementId}/{deliverableId}";
	private static final String DELETE_DELIVERABLE_URL = "/worker/v2/assignments/{workNumber}/deliverables/{deliverableId}";

	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";
	private static final Long VALID_DELIVERABLE_REQUIREMENT_ID = 0L;
	private static final Long VALID_DELIVERABLE_ID = 0L;

	@InjectMocks private DeliverablesController controller = new DeliverablesController();
	@Mock private XAssignment xAssignment;

	private ObjectMapper jackson = new ObjectMapper();

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		java.util.List<Object> processingResult = new java.util.ArrayList<>();

		final Asset asset = new Asset();
		asset.setUUID(UUID.randomUUID().toString());
		asset.setDescription("description");
		asset.setName("file.jpg");
		asset.setMimeType("image/jpeg");
		asset.setFileByteSize(100);

		processingResult.add(asset);

		when(xAssignment.addDeliverable(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																		anyString(),
																		anyLong(),
																		anyLong(),
																		org.mockito.Matchers.isA(AddDeliverableDTO.class))).thenReturn(processingResult);

		when(xAssignment.addDeliverable(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																		eq(INVALID_WORK_NUMBER),
																		anyLong(),
																		anyLong(),
																		org.mockito.Matchers.isA(AddDeliverableDTO.class))).thenThrow(new RuntimeException(
						"sss"));

		when(xAssignment.deleteDeliverable(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																			 anyString(),
																			 anyString())).thenReturn(processingResult);

		when(xAssignment.deleteDeliverable(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																			 eq(INVALID_WORK_NUMBER),
																			 anyString())).thenThrow(new RuntimeException("sss"));
	}

	private AddDeliverableDTO getValidAddDeliverableDTO() {

		final AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
			.withName("")
			.withDescription("")
			.withData("")
			.build();

		return addDeliverableDTO;
	}

	private AddDeliverableDTO getInvalidAddDeliverableDTO() {

		final AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder().build();

		return addDeliverableDTO;
	}

	private ResultActions addDeliverable(final String workNumber, final AddDeliverableDTO addDeliverableDTO) throws
																																																					 Exception {

		final String addDeliverableJSON = jackson.writeValueAsString(addDeliverableDTO);

		return mockMvc.perform(post(ADD_DELIVERABLE_URL, workNumber).contentType(MediaType.APPLICATION_JSON)
																	 .content(addDeliverableJSON));
	}

	private ResultActions deleteDeliverable(final String workNumber, final Long deliverableId) throws Exception {

		return mockMvc.perform(delete(DELETE_DELIVERABLE_URL, workNumber, deliverableId));
	}

	@Test
	public void addDeliverable_withValidWorkNumberAndValidDTO_shouldReturn200Response() throws Exception {

		final AddDeliverableDTO addDeliverableDTO = getValidAddDeliverableDTO();

		addDeliverable(VALID_WORK_NUMBER, addDeliverableDTO).andExpect(status().isOk())
						.andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Ignore
	@Test
	public void addDeliverable_withMissingName_shouldReturn500Response() throws Exception {
	}

	@Ignore
	@Test
	public void addDeliverable_withMissingDescription_shouldReturn500Response() throws Exception {
	}

	@Ignore
	@Test
	public void addDeliverable_withMissingData_shouldReturn500Response() throws Exception {
	}

	@Ignore
	@Test
	public void addDeliverable_withInvalidName_shouldReturn500Response() throws Exception {
	}

	@Ignore
	@Test
	public void addDeliverable_withInvalidRequirementId_shouldReturn500Response() throws Exception {
	}

	@Test
	public void addDeliverable_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		final AddDeliverableDTO addDeliverableDTO = getValidAddDeliverableDTO();

		addDeliverable(INVALID_WORK_NUMBER, addDeliverableDTO).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}

	public void deleteDeliverable_withValidWorkNumber_shouldReturn200Response() throws Exception {

		deleteDeliverable(VALID_WORK_NUMBER, VALID_DELIVERABLE_ID).andExpect(status().isOk())
						.andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void deleteDeliverable_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		deleteDeliverable(INVALID_WORK_NUMBER, VALID_DELIVERABLE_ID).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}
}
