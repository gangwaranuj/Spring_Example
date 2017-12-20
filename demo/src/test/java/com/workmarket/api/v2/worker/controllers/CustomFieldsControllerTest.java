package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.worker.ex.WorkInvalidException;
import com.workmarket.api.v2.worker.model.SaveCustomFieldsDTO;
import com.workmarket.api.v2.worker.model.validator.SaveCustomFieldsDTOValidator;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.errorType;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class CustomFieldsControllerTest extends BaseApiControllerTest {

	private static final String GET_CUSTOM_FIELDS_URL = "/worker/v2/assignments/{workNumber}/customfields";
	private static final String SAVE_CUSTOM_FIELDS_URL = "/worker/v2/assignments/{workNumber}/customfields";
	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@InjectMocks private CustomFieldsController controller = new CustomFieldsController();
	@Mock private XAssignment xAssignment;

	private ObjectMapper jackson = new ObjectMapper();

	@Before
	public void setup() throws Exception {
		super.setup(controller);

		List<Object> processingResult = new ArrayList<>();

		when(xAssignment.getCustomFields(org.mockito.Matchers.isA(ExtendedUserDetails.class), anyString())).thenReturn(
						processingResult);

		when(xAssignment.getCustomFields(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																		 eq(INVALID_WORK_NUMBER))).thenThrow(new RuntimeException("sss"));

		when(xAssignment.saveCustomFields(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																			anyString(),
																			org.mockito.Matchers.isA(SaveCustomFieldsDTO.class),
																			anyBoolean(),
																			org.mockito.Matchers.isA(BindingResult.class))).thenReturn(processingResult);
		when(xAssignment.saveCustomFields(org.mockito.Matchers.isA(ExtendedUserDetails.class),
																			eq(INVALID_WORK_NUMBER),
																			org.mockito.Matchers.isA(SaveCustomFieldsDTO.class),
																			anyBoolean(),
																			org.mockito.Matchers.isA(BindingResult.class))).thenThrow(new WorkInvalidException());
	}

	@Override
	protected Validator getValidator() {
		return new SaveCustomFieldsDTOValidator();
	}

	private ResultActions getCustomFields(final String workNumber) throws Exception {

		return mockMvc.perform(get(GET_CUSTOM_FIELDS_URL, workNumber));
	}

	private ResultActions saveCustomFields(final String workNumber,
																				 final Object saveCustomFieldsDTO,
																				 final Boolean onComplete) throws Exception {

		final String saveCustomFieldsJSON = jackson.writeValueAsString(saveCustomFieldsDTO);

		return mockMvc.perform(post(SAVE_CUSTOM_FIELDS_URL, workNumber).contentType(MediaType.APPLICATION_JSON)
																	 .content(saveCustomFieldsJSON));
	}

	private SaveCustomFieldsDTO getValidSaveCustomFieldsDTO() {



		final CustomFieldDTO.Builder dto1 = new CustomFieldDTO.Builder().setId(1l).setName("Field 1");
		final CustomFieldDTO.Builder dto2 = new CustomFieldDTO.Builder().setId(2l).setName("Field 2");

		final Set<CustomFieldDTO.Builder> customFieldDTOs = ImmutableSet.of(dto1, dto2);

		final CustomFieldGroupDTO customFieldGroupDTO = new CustomFieldGroupDTO.Builder().setFields(customFieldDTOs).build();

		final SaveCustomFieldsDTO saveCustomFieldsDTO = new SaveCustomFieldsDTO.Builder()
			.withCustomFields(ImmutableList.of(customFieldGroupDTO))
			.build();

		return saveCustomFieldsDTO;
	}

	private SaveCustomFieldsDTO getInvalidSaveCustomFieldsDTO() {

		final SaveCustomFieldsDTO saveCustomFieldsDTO = new SaveCustomFieldsDTO.Builder().build();

		return saveCustomFieldsDTO;
	}

	@Test
	public void getCustomFields_withValidWorkNumber_shouldReturn200Response() throws Exception {

		getCustomFields(VALID_WORK_NUMBER).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void getCustomFields_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		getCustomFields(INVALID_WORK_NUMBER).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}

	@Test
	public void saveCustomFields_withValidWorkNumberAndValidCustomFieldGroupSet_shouldReturn200Response() throws
																																																				Exception {
		final SaveCustomFieldsDTO saveCustomFieldsDTO = getValidSaveCustomFieldsDTO();
		saveCustomFields(VALID_WORK_NUMBER, saveCustomFieldsDTO, false).andExpect(status().isOk())
						.andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void saveCustomFields_withValidWorkNumberAndInvalidCustomFieldGroupSet_shouldReturn400Response() throws
																																																					Exception {
		final SaveCustomFieldsDTO saveCustomFieldsDTO = getInvalidSaveCustomFieldsDTO();
		MvcResult mvcResult = saveCustomFields(VALID_WORK_NUMBER,
																					 saveCustomFieldsDTO,
																					 false).andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.meta.code", is(400)))
						.andReturn();

		ApiV2Response<ApiBaseError> response = expectApiV2Response(mvcResult, errorType);
		expectApiErrorCode(response.getResults(), "api.v2.validation.error.assignment.customfield.empty");
	}

	@Test
	public void saveCustomFields_withInvalidWorkNumber_shouldReturn400Response() throws Exception {
		final SaveCustomFieldsDTO saveCustomFieldsDTO = getValidSaveCustomFieldsDTO();
		saveCustomFields(INVALID_WORK_NUMBER, saveCustomFieldsDTO, false).andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.meta.code", is(400)));
	}
}
