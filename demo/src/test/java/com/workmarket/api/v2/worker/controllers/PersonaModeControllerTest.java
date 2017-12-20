package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Optional;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.model.ApiPersonaModeDTO;
import com.workmarket.api.v2.worker.model.PersonaMode;
import com.workmarket.common.jwt.Either;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.user.PersonaPreference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class PersonaModeControllerTest extends BaseApiControllerTest {
	private static final TypeReference<ApiV2Response<ApiPersonaModeDTO>> PERSONA_MODE_RESPONSE_TYPE = new TypeReference<ApiV2Response<ApiPersonaModeDTO>>() {};
	private static final TypeReference<ApiV2Response<PersonaMode>> ALLOWED_PERSONA_MODES_RESPONSE_TYPE = new TypeReference<ApiV2Response<PersonaMode>>() {};
	private static final TypeReference<ApiV2Response<ApiBaseError>> ERROR_RESPONSE_TYPE = new TypeReference<ApiV2Response<ApiBaseError>>() {};

	public static final String ENDPOINT_V2_GET_PERSONA_MODE = "/worker/v2/persona/get";
	public static final String ENDPOINT_V2_SET_PERSONA_MODE = "/worker/v2/persona/set";
	public static final String ENDPOINT_V2_LIST_PERSONA_MODES = "/worker/v2/persona/modes";

	protected final ObjectMapper jackson = new ObjectMapper().registerModule(new GuavaModule());

	@InjectMocks
	private final PersonaModeController controller = new PersonaModeController();

	@Before
	public void setup() throws Exception {
		super.setup(controller);
	}

	@Test
	public void getPersonaMode_noData() throws Exception {
		when(userService.getPersonaPreference(eq(user.getId()))).thenReturn(Optional.<PersonaPreference>absent());
		final ApiV2Response<ApiPersonaModeDTO> response = getApiPersonaModeDTOApiV2Response();
		assertNull("Expect persona mode to be null", response.getResults().get(0).getPersonaMode());
	}

	@Test
	public void getPersonaMode_buyer() throws Exception {
		final PersonaPreference personaPreference = new PersonaPreference();
		personaPreference.setBuyer(true);
		when(userService.getPersonaPreference(eq(user.getId()))).thenReturn(Optional.of(personaPreference));
		final ApiV2Response<ApiPersonaModeDTO> response = getApiPersonaModeDTOApiV2Response();
		assertEquals("Expect persona mode to be BUYER", PersonaMode.BUYER, response.getResults().get(0).getPersonaMode());
	}

	@Test
	public void getPersonaMode_seller() throws Exception {
		final PersonaPreference personaPreference = new PersonaPreference();
		personaPreference.setSeller(true);
		when(userService.getPersonaPreference(eq(user.getId()))).thenReturn(Optional.of(personaPreference));
		final ApiV2Response<ApiPersonaModeDTO> response = getApiPersonaModeDTOApiV2Response();
		assertEquals("Expect persona mode to be SELLER", PersonaMode.SELLER, response.getResults().get(0).getPersonaMode());
	}

	@Test
	public void getPersonaMode_dispatcher() throws Exception {
		final PersonaPreference personaPreference = new PersonaPreference();
		personaPreference.setDispatcher(true);
		when(userService.getPersonaPreference(eq(user.getId()))).thenReturn(Optional.of(personaPreference));
		final ApiV2Response<ApiPersonaModeDTO> response = getApiPersonaModeDTOApiV2Response();
		assertEquals("Expect persona mode to be DISPATCHER",
			PersonaMode.DISPATCHER,
			response.getResults().get(0).getPersonaMode());
	}

	@Test
	public void setPersonaMode_buyer() throws Exception {

		final Either<ApiV2Response<ApiBaseError>, ApiV2Response<ApiPersonaModeDTO>> responseEither = setApiPersonaModeDTOApiV2Response(
			new ApiPersonaModeDTO.Builder().personaMode(PersonaMode.BUYER).build(),
			status().isOk(),
			true,
			false,
			false,
			false);
		assertTrue("Expected ApiV2Response<ApiPersonaModeDTO> but got ApiV2Response<ApiBaseError>", responseEither.isRight());
		assertEquals("Expect persona mode to be BUYER",
			PersonaMode.BUYER,
			responseEither.get().getResults().get(0).getPersonaMode());
	}

	@Test
	public void setPersonaMode_buyer_notAllowed_NoManageWork() throws Exception {
		final Either<ApiV2Response<ApiBaseError>, ApiV2Response<ApiPersonaModeDTO>> responseEither = setApiPersonaModeDTOApiV2Response(
			new ApiPersonaModeDTO.Builder().personaMode(PersonaMode.BUYER).build(),
			status().isBadRequest(),
			false,
			false,
			false,
			false);
		assertTrue("Expected ApiV2Response<ApiBaseError> but got ApiV2Response<ApiPersonaMode>", responseEither.isLeft());
		expectApiErrorCode(responseEither.getLeft().getResults(), PersonaModeController.USER_PERSONA_MODE_NOT_ALLOWED);
	}

	@Test
	public void setPersonaMode_buyer_notAllowed_EmployeeWorker() throws Exception {
		final Either<ApiV2Response<ApiBaseError>, ApiV2Response<ApiPersonaModeDTO>> responseEither = setApiPersonaModeDTOApiV2Response(
			new ApiPersonaModeDTO.Builder().personaMode(PersonaMode.BUYER).build(),
			status().isBadRequest(),
			true,
			true,
			false,
			false);
		assertTrue("Expected ApiV2Response<ApiBaseError> but got ApiV2Response<ApiPersonaMode>", responseEither.isLeft());
		expectApiErrorCode(responseEither.getLeft().getResults(), PersonaModeController.USER_PERSONA_MODE_NOT_ALLOWED);
	}

	@Test
	public void setPersonaMode_seller() throws Exception {
		final Either<ApiV2Response<ApiBaseError>, ApiV2Response<ApiPersonaModeDTO>> responseEither =
			setApiPersonaModeDTOApiV2Response(new ApiPersonaModeDTO.Builder().personaMode(PersonaMode.SELLER).build(),
				status().isOk(),
				false,
				false,
				true,
				false);
		assertTrue("Expected ApiV2Response<ApiPersonaModeDTO> but got ApiV2Response<ApiBaseError>", responseEither.isRight());
		assertEquals("Expect persona mode to be SELLER", PersonaMode.SELLER, responseEither.get().getResults().get(0).getPersonaMode());
	}

	@Test()
	public void setPersonaMode_seller_noFindWork() throws Exception {
		final Either<ApiV2Response<ApiBaseError>, ApiV2Response<ApiPersonaModeDTO>> responseEither =
			setApiPersonaModeDTOApiV2Response(new ApiPersonaModeDTO.Builder().personaMode(PersonaMode.SELLER).build(),
				status().isBadRequest(),
				false,
				false,
				false,
				false);
		assertTrue("Expected ApiV2Response<ApiBaseError> but got ApiV2Response<ApiPersonaMode>", responseEither.isLeft());
		expectApiErrorCode(responseEither.getLeft().getResults(), PersonaModeController.USER_PERSONA_MODE_NOT_ALLOWED);
	}

	@Test
	public void setPersonaMode_dispatcher() throws Exception {
		final Either<ApiV2Response<ApiBaseError>, ApiV2Response<ApiPersonaModeDTO>> responseEither =
			setApiPersonaModeDTOApiV2Response(new ApiPersonaModeDTO.Builder().personaMode(PersonaMode.DISPATCHER).build(),
				status().isOk(),
				false,
				false,
				false,
				true);
		assertTrue("Expected ApiV2Response<ApiPersonaModeDTO> but got ApiV2Response<ApiBaseError>", responseEither.isRight());
		assertEquals("Expect persona mode to be DISPATCHER",
			PersonaMode.DISPATCHER,
			responseEither.get().getResults().get(0).getPersonaMode());
	}

	@Test
	public void setPersonaMode_dispatcher_noRoles() throws Exception {
		final String[] roles = {PersonaModeController.ACL_DISPATCHER};
		final Either<ApiV2Response<ApiBaseError>, ApiV2Response<ApiPersonaModeDTO>> responseEither =
			setApiPersonaModeDTOApiV2Response(new ApiPersonaModeDTO.Builder().personaMode(PersonaMode.DISPATCHER).build(),
				status().isBadRequest(),
				false,
				false,
				false,
				false);
		assertTrue("Expected ApiV2Response<ApiBaseError> but got ApiV2Response<ApiPersonaMode>", responseEither.isLeft());
		expectApiErrorCode(responseEither.getLeft().getResults(), PersonaModeController.USER_PERSONA_MODE_NOT_ALLOWED);
	}

	@Test
	public void testListPersonaModesForBuyerWithManageWorkAndNotEmployeeWorker() throws Exception {
		final ApiV2Response<PersonaMode> response = listAllowedPersonaModesApiV2Response(true, false, false, false);
		final List<PersonaMode> expected = Arrays.asList(PersonaMode.BUYER);
		final List<PersonaMode> actual = response.getResults();

		assertEquals(expected, actual);
	}

	@Test
	public void testListPersonaModesForSellerWithFindWork() throws Exception {
		final ApiV2Response<PersonaMode> response = listAllowedPersonaModesApiV2Response(false, false, true, false);
		final List<PersonaMode> expected = Arrays.asList(PersonaMode.SELLER);
		final List<PersonaMode> actual = response.getResults();

		assertEquals(expected, actual);
	}

	@Test
	public void testListPersonaModesForDispatcher() throws Exception {
		final ApiV2Response<PersonaMode> response = listAllowedPersonaModesApiV2Response(false, false, false, true);
		final List<PersonaMode> expected = Arrays.asList(PersonaMode.DISPATCHER);
		final List<PersonaMode> actual = response.getResults();

		assertEquals(expected, actual);
	}

	@Test
	public void testListPersonaModes() throws Exception {
		final ApiV2Response<PersonaMode> response = listAllowedPersonaModesApiV2Response(true, false, true, true);
		final List<PersonaMode> expected = Arrays.asList(PersonaMode.BUYER, PersonaMode.DISPATCHER, PersonaMode.SELLER);
		final List<PersonaMode> actual = new ArrayList<>(response.getResults());

		Collections.sort(actual, new Comparator<PersonaMode>() {
			@Override
			public int compare(final PersonaMode o1, final PersonaMode o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		assertEquals(expected, actual);
	}

	@Test
	public void setPersonaMode_noData() throws Exception {
		ApiPersonaModeDTO apiPersonaModeDTO = new ApiPersonaModeDTO.Builder().build();

		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(ENDPOINT_V2_SET_PERSONA_MODE)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jackson.writeValueAsString(apiPersonaModeDTO))).andExpect(status().isBadRequest()).andReturn();

		final ApiV2Response<ApiBaseError> response = expectApiV2Response(result, ERROR_RESPONSE_TYPE);
		final ApiJSONPayloadMap responseMeta = response.getMeta();
		expectApiV3ResponseMetaSupport(responseMeta);
		expectApiErrorCode(response.getResults(), PersonaModeController.USER_PERSONA_MODE_REQUIRED);
		expectStatusCode(HttpStatus.BAD_REQUEST.value(), responseMeta);
		verify(userService, times(0)).saveOrUpdatePersonaPreference(any(PersonaPreference.class));
		verify(authenticationService, times(0)).refreshSessionForUser(user.getId());
	}

	private final Either<ApiV2Response<ApiBaseError>, ApiV2Response<ApiPersonaModeDTO>> setApiPersonaModeDTOApiV2Response(
		final ApiPersonaModeDTO apiPersonaModeDTO,
		final ResultMatcher statusCodeMatcher,
		final boolean manageWork,
		final boolean employeeWorker,
		final boolean findWork,
		final boolean dispatcherRole) throws Exception {

		mockUserDetails(manageWork, employeeWorker, findWork, dispatcherRole);

		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(ENDPOINT_V2_SET_PERSONA_MODE)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jackson.writeValueAsString(apiPersonaModeDTO))).andExpect(statusCodeMatcher).andReturn();

		try {
			final ApiV2Response<ApiPersonaModeDTO> response = expectApiV2Response(result, PERSONA_MODE_RESPONSE_TYPE);
			final ApiJSONPayloadMap responseMeta = response.getMeta();
			expectApiV3ResponseMetaSupport(responseMeta);
			expectStatusCode(HttpStatus.OK.value(), responseMeta);
			verify(userService, times(1)).saveOrUpdatePersonaPreference(any(PersonaPreference.class));
			verify(authenticationService, times(1)).refreshSessionForUser(user.getId());
			return Either.right(response);
		} catch (Exception e) {
			final ApiV2Response<ApiBaseError> response = expectApiV2Response(result, ERROR_RESPONSE_TYPE);
			return Either.left(response);
		}
	}

	private ApiV2Response<ApiPersonaModeDTO> getApiPersonaModeDTOApiV2Response() throws Exception {
		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get(ENDPOINT_V2_GET_PERSONA_MODE)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
		final ApiV2Response<ApiPersonaModeDTO> response = expectApiV2Response(result, PERSONA_MODE_RESPONSE_TYPE);
		final ApiJSONPayloadMap responseMeta = response.getMeta();
		expectApiV3ResponseMetaSupport(responseMeta);
		expectStatusCode(HttpStatus.OK.value(), responseMeta);
		return response;
	}

	private ApiV2Response<PersonaMode> listAllowedPersonaModesApiV2Response(
		boolean manageWork,
		boolean employeeWorker,
		boolean findWork,
		boolean dispatcherRole) throws Exception {

		mockUserDetails(manageWork, employeeWorker, findWork, dispatcherRole);

		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get(ENDPOINT_V2_LIST_PERSONA_MODES)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		final ApiV2Response<PersonaMode> response = expectApiV2Response(result, ALLOWED_PERSONA_MODES_RESPONSE_TYPE);
		final ApiJSONPayloadMap responseMeta = response.getMeta();
		expectApiV3ResponseMetaSupport(responseMeta);
		expectStatusCode(HttpStatus.OK.value(), responseMeta);

		return response;
	}

	private void mockUserDetails(
		final boolean manageWork,
		final boolean employeeWorker,
		final boolean findWork,
		final boolean dispatcherRole) throws Exception {

		final ExtendedUserDetails mockExtendedUserDetails = mock(ExtendedUserDetails.class);

		when(extendedUserDetailsService.loadUser(eq(user)))
			.thenReturn(mockExtendedUserDetails);

		when(mockExtendedUserDetails.getId())
			.thenReturn(user.getId());

		when(mockExtendedUserDetails.isManageWork())
			.thenReturn(manageWork);

		when(mockExtendedUserDetails.isEmployeeWorker())
			.thenReturn(employeeWorker);

		when(mockExtendedUserDetails.isFindWork())
			.thenReturn(findWork);

		when(mockExtendedUserDetails.hasAnyRoles(eq(PersonaModeController.ACL_DISPATCHER)))
			.thenReturn(dispatcherRole);
	}
}
