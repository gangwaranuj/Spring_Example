package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.v2.worker.model.CheckInDTO;
import com.workmarket.api.v2.worker.model.CheckOutDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.thrift.work.TimeTrackingResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class CheckInOutControllerTest extends BaseApiControllerTest {

	private static final String NEW_CHECKIN_URL = "/worker/v2/assignments/{workNumber}/checkin";
	private static final String NEW_CHECKOUT_URL = "/worker/v2/assignments/{workNumber}/checkout";
	private static final String UPDATE_CHECKIN_URL = "/worker/v2/assignments/{workNumber}/checkin/{checkInOutPairId}";
	private static final String UPDATE_CHECKOUT_URL = "/worker/v2/assignments/{workNumber}/checkout/{checkInOutPairId}";

	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";

	@Mock private XAssignment xAssignment;
	@InjectMocks private CheckInOutController controller = new CheckInOutController();

	private ObjectMapper jackson = new ObjectMapper();

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		mockMvc = getMockMvc(controller);

		final TimeTrackingResponse timeTrackingResponse = new TimeTrackingResponse();

		when(xAssignment.checkIn(anyLong(), anyString(), anyLong(), org.mockito.Matchers.isA(CheckInDTO.class))).thenReturn(
						timeTrackingResponse);

		when(xAssignment.checkIn(anyLong(), eq(INVALID_WORK_NUMBER), anyLong(), org.mockito.Matchers.isA(CheckInDTO.class)))
						.thenThrow(new ApiException("Invalid work number"));

		when(xAssignment.checkOut(anyLong(),
															anyString(),
															anyLong(),
															org.mockito.Matchers.isA(CheckOutDTO.class))).thenReturn(timeTrackingResponse);

		when(xAssignment.checkOut(anyLong(),
															eq(INVALID_WORK_NUMBER),
															anyLong(),
															org.mockito.Matchers.isA(CheckOutDTO.class))).thenThrow(new ApiException(
						"Invalid work number"));
	}

	private CheckInDTO getValidCheckInDTO() {

		final CheckInDTO checkInDTO = new CheckInDTO.Builder()
			.withLatitude(0.0)
			.withLongitude(0.0)
			.build();

		return checkInDTO;
	}

	private CheckInDTO getInvalidCheckInDTO() {

		final CheckInDTO checkInDTO = new CheckInDTO.Builder().build();

		//checkInDTO.setLatitude(0.0);
		//checkInDTO.setLongitude(0.0);

		return checkInDTO;
	}

	private CheckOutDTO getValidCheckOutDTO() {

		final CheckOutDTO checkOutDTO = new CheckOutDTO.Builder()
			.withNoteText("hello")
			.withLatitude(0.0)
			.withLongitude(0.0)
			.build();

		return checkOutDTO;
	}

	private CheckOutDTO getInvalidCheckOutDTO() {

		final CheckOutDTO checkOutDTO = new CheckOutDTO.Builder().build();

		//checkOutDTO.setNoteText("");
		//checkOutDTO.setLatitude(0.0);
		//checkOutDTO.setLongitude(0.0);

		return checkOutDTO;
	}

	private ResultActions checkIn(String workNumber, CheckInDTO checkInDTO) throws Exception {

		final String checkInJSON = jackson.writeValueAsString(checkInDTO);

		return mockMvc.perform(post(NEW_CHECKIN_URL, workNumber).contentType(MediaType.APPLICATION_JSON)
																	 .content(checkInJSON));
	}

	private ResultActions checkOut(final String workNumber, final CheckOutDTO checkOutDTO) throws Exception {

		final String checkOutJSON = jackson.writeValueAsString(checkOutDTO);

		return mockMvc.perform(post(NEW_CHECKOUT_URL, workNumber).contentType(MediaType.APPLICATION_JSON)
																	 .content(checkOutJSON));
	}

	@Test
	public void checkIn_withValidWorkNumberAndNullDTO_shouldReturn200Response() throws Exception {

		final CheckInDTO checkInDTO = null;

		checkIn(VALID_WORK_NUMBER, checkInDTO).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void checkIn_withValidWorkNumberAndValidDTO_shouldReturn200Response() throws Exception {

		final CheckInDTO checkInDTO = getValidCheckInDTO();

		checkIn(VALID_WORK_NUMBER, checkInDTO).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void checkIn_withValidWorkAndInvalidDTOReturns400() throws Exception {

		final CheckInDTO checkInDTO = getInvalidCheckInDTO();

		checkIn(INVALID_WORK_NUMBER, checkInDTO).andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.meta.code", is(400)));
	}

	@Test
	public void checkIn_withInvalidWorkNumber_shouldReturn400Response() throws Exception {

		final CheckInDTO checkInDTO = getValidCheckInDTO();

		checkIn(INVALID_WORK_NUMBER, checkInDTO).andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.meta.code", is(400)));
	}

	@Test
	public void checkOut_withValidWorkNumberAndNullDTO_shoulReturn200Response() throws Exception {

		final CheckOutDTO checkOutDTO = null;

		checkOut(VALID_WORK_NUMBER, checkOutDTO).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void checkOut_withValidWorkNumberAndValidDTO_shouldReturn200Response() throws Exception {

		final CheckOutDTO checkOutDTO = getValidCheckOutDTO();

		checkOut(VALID_WORK_NUMBER, checkOutDTO).andExpect(status().isOk()).andExpect(jsonPath("$.meta.code", is(200)));
	}

	@Test
	public void checkOut_withValidWorkAndInvalidDTO_shouldReturn400Response() throws Exception {

		final CheckOutDTO checkOutDTO = getInvalidCheckOutDTO();

		checkOut(INVALID_WORK_NUMBER, checkOutDTO).andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.meta.code", is(400)));
	}

	@Test
	public void checkOut_withInvalidWorkNumber_shouldReturn400Response() throws Exception {

		final CheckOutDTO checkOutDTO = getValidCheckOutDTO();

		checkOut(INVALID_WORK_NUMBER, checkOutDTO).andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.meta.code", is(400)));
	}
}
