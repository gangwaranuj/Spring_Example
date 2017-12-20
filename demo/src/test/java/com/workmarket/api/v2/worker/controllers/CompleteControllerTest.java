package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.worker.model.CheckInDTO;
import com.workmarket.api.v2.worker.model.CompleteDTO;
import com.workmarket.api.v2.worker.model.validator.CompleteDTOValidator;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.web.forms.assignments.WorkCompleteForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class CompleteControllerTest extends BaseApiControllerTest {

	private static final String OLD_COMPLETE_URL = "/worker/v2/assignments/complete/{workNumber}";
	private static final String NEW_COMPLETE_URL = "/worker/v2/assignments/{workNumber}/complete";
	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@InjectMocks private CompleteController controller = new CompleteController();
	@Mock private XAssignment xAssignment;

	private ObjectMapper jackson = new ObjectMapper();

	@Override
	protected Validator getValidator() {
		return new CompleteDTOValidator();
	}

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		final List<Object> processingResult = new ArrayList<>();

		when(xAssignment.complete(org.mockito.Matchers.isA(ExtendedUserDetails.class),
															anyString(),
															org.mockito.Matchers.isA(CompleteDTO.class),
															anyLong(),
															org.mockito.Matchers.isA(BindingResult.class))).thenReturn(processingResult);

		when(xAssignment.complete(org.mockito.Matchers.isA(ExtendedUserDetails.class),
															eq(INVALID_WORK_NUMBER),
															org.mockito.Matchers.isA(CompleteDTO.class),
															anyLong(),
															org.mockito.Matchers.isA(BindingResult.class))).thenThrow(new RuntimeException("sss"));
	}

	private ResultActions complete(final String workNumber, final Object completeDTO, final Long onBehalfOf) throws
																																																								Exception {

		final String completeJSON = jackson.writeValueAsString(completeDTO);

		return mockMvc.perform(post(NEW_COMPLETE_URL, workNumber).contentType(MediaType.APPLICATION_JSON)
																	 .content(completeJSON));
	}

	private CompleteDTO getValidCompleteDTO() {

		final CompleteDTO completeDTO = new CompleteDTO.Builder()
			.withResolution("The work is complete")
			.withOverrideMinutesWorked(0)
			.withUnits(0)
			.withAdditionalExpenses(0.0)
			.withOverridePrice(0.0)
			.withTaxPercent(0.0)
			.build();

		return completeDTO;
	}

	private Object getInvalidCompleteDTO() {
		return new CheckInDTO.Builder().build();
	}

	private WorkCompleteForm getWorkCompleteForm() {

		final WorkCompleteForm form = new WorkCompleteForm();

		form.setResolution("resolution");
		form.setHours(0);
		form.setMinutes(0);
		form.setUnits(0.0);
		form.setAdditional_expenses(0.0);
		form.setOverride_price(0.0);
		form.setCollect_tax(false);
		form.setTax_percent(0.0);
		form.setShare(false);

		return form;
	}

	@Test
	public void complete_withValidWorkNumberAndValidDTO_shouldReturn200Response() throws Exception {

		final CompleteDTO completeDTO = getValidCompleteDTO();
		final Long onBehalfOf = null;

		complete(VALID_WORK_NUMBER, completeDTO, onBehalfOf).andExpect(status().isOk())
						.andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void complete_withValidWorkNumberAndInvalidDTO_shouldReturn400Response() throws Exception {

		final Object completeDTO = getInvalidCompleteDTO();
		final Long onBehalfOf = null;

		complete(VALID_WORK_NUMBER, completeDTO, onBehalfOf).andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.meta.code", is(400)));
	}

	@Test
	public void complete_withInvalidWorkNumber_shouldReturn500Response() throws Exception {

		final CompleteDTO completeDTO = getValidCompleteDTO();
		final Long onBehalfOf = null;

		complete(INVALID_WORK_NUMBER, completeDTO, onBehalfOf).andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.meta.code", is(500)));
	}
}
