package com.workmarket.api.v2.worker.profile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.model.ApiProfileDTO;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.utility.RandomUtilities;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApiProfileControllerIT extends ApiV2BaseIT {
    private static final TypeReference<ApiV2Response<ApiProfileDTO>> API_PROFILE_TYPE = new TypeReference<ApiV2Response<ApiProfileDTO>>(){};

    @Before
    public void setUp() throws Exception {
        login();
    }

    @Test
    public void testCreateAndReadProfile() throws Exception {
        final String lastName = "LastName" + RandomUtilities.nextLong();
        final String firstName = "FirstName" + RandomUtilities.nextLong();
        final String password = "a1s2d3f4!@#$"+ RandomUtilities.nextLong();
        final String email = RandomUtilities.nextLong() + "+primary@email.com";
        final String secondaryEmail = RandomUtilities.nextLong() + "+secondary@email.com";
        final CreateNewWorkerRequest payload = new CreateNewWorkerRequest.CreateNewWorkerDTOBuilder()
                .setSecondaryEmail(secondaryEmail)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setLocale("en_US")
                .setEmail(email)
                .build();

        final MvcResult mvcCreateResult = sendPostRequest("/v2/worker/create-account", payload)
                .andExpect(status().isOk())
                .andReturn();

        final ApiV2Response<ApiProfileDTO> createResponse = expectApiV2Response(mvcCreateResult, API_PROFILE_TYPE);
        final List<ApiProfileDTO> createResults = createResponse.getResults();
        final ApiJSONPayloadMap createMeta = createResponse.getMeta();

        expectApiV3ResponseMetaSupport(createMeta);
        expectStatusCode(HttpStatus.OK.value(), createMeta);

        assertEquals(1, createResults.size());
        assertEquals(email, createResults.get(0).getEmail());
        assertNotNull(createResults.get(0).getUserNumber());

        final MvcResult mvcReadResult = mockMvc.perform(doGet("/v2/worker/profile/" + createResults.get(0).getUserNumber()))
                .andExpect(status().isOk())
                .andReturn();

        final ApiV2Response<ApiProfileDTO> readResponse = expectApiV2Response(mvcReadResult, API_PROFILE_TYPE);
        final List<ApiProfileDTO> readResults = readResponse.getResults();
        final ApiJSONPayloadMap readMeta = readResponse.getMeta();

        expectStatusCode(HttpStatus.OK.value(), readMeta);

        assertEquals(1, readResults.size());
        assertEquals(lastName, readResults.get(0).getLastName());
        assertEquals(firstName, readResults.get(0).getFirstName());
        assertEquals(secondaryEmail, readResults.get(0).getSecondaryEmail());
        assertEquals(createResults.get(0).getUserNumber(), readResults.get(0).getUserNumber());
    }

    @Test
    public void testEditMyProfile() throws Exception {
        final String userNumber = user.getUserNumber();
        final String lastName = "LastName" + RandomUtilities.nextLong();
        final String firstName = "FirstName" + RandomUtilities.nextLong();
        final String secondaryEmail = RandomUtilities.nextLong() + "+secondary@email.com";
        final ApiProfileDTO payload = new ApiProfileDTO.Builder()
            .withSecondaryEmail(secondaryEmail)
            .withFirstName(firstName)
            .withLastName(lastName)
            .build();

        final MvcResult mvcResult = sendPostRequest("/v2/worker/profile/" + userNumber, payload)
            .andExpect(status().isOk())
            .andReturn();

        final ApiV2Response<ApiProfileDTO> response = apiProfileDTOResponse(mvcResult);
        final List<ApiProfileDTO> results = response.getResults();
        final ApiJSONPayloadMap meta = response.getMeta();

        expectStatusCode(HttpStatus.OK.value(), meta);

        assertEquals(1, results.size());
        assertEquals(lastName, results.get(0).getLastName());
        assertEquals(firstName, results.get(0).getFirstName());
        assertEquals(userNumber, results.get(0).getUserNumber());
        assertEquals(secondaryEmail, results.get(0).getSecondaryEmail());
    }

    private ApiV2Response<ApiProfileDTO> apiProfileDTOResponse(final MvcResult mvcResult) throws IOException {
        final ObjectMapper mapper = createObjectMapper();
        final MockHttpServletResponse response = mvcResult.getResponse();

        return mapper.readValue(response.getContentAsString(), API_PROFILE_TYPE);
    }

    private ResultActions sendPostRequest(final String endpoint, final Object payload) throws Exception {
        final ObjectMapper mapper = createObjectMapper();
        final String payloadString = mapper.writeValueAsString(payload);
        final ResultActions resultActions = mockMvc.perform(doPost(endpoint)
            .content(payloadString));

        return resultActions;
    }

    private ObjectMapper createObjectMapper() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleFilterProvider filterProvider = new SimpleFilterProvider();

        filterProvider.addFilter(
            ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
            new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.EMPTY_SET)
        );

        mapper.setFilterProvider(filterProvider);

        return mapper;
    }
}
