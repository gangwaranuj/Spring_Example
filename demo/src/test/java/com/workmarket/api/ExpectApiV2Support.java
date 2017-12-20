package com.workmarket.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.v2.ApiV2Response;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Base class of "expects" for API V2.
 *
 * ExpectApiV2Support provides API expectations for API V2 response classes.
 * You can request upgrade or downgrade to Base API functionality by modifying the expectations.
 *
 * Created by joshlevine on 12/25/16.
 */
public class ExpectApiV2Support extends ExpectApiSupport {
	/**
	 * Try to covert the given MvcResult into an ApiV2Response with no specific type.
	 *
	 * If the mvcResult cannot be deserialized, an exception will be thrown eg. JsonMappingException.
	 * To fix deserialization issues, check your type and check that the T class follows the Builder and @Json* annotations pattern.
	 *
	 * @param jsonResponse
	 * @return
	 * @throws IOException
	 */
	public ApiV2Response expectApiV2Response(String jsonResponse) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonResponse, ApiV2Response.class);
		}
		catch(IOException e) {
			assertEquals("Expected ApiV2Response", jsonResponse, false);
		}
		return null;
	}

	/**
	 * Try to covert the given MvcResult into an ApiV2Response with generics matching the given type.
	 *
	 * If the mvcResult cannot be deserialized, an exception will be thrown eg. JsonMappingException.
	 * To fix deserialization issues, check your type and check that the T class follows the Builder and @Json* annotations pattern.
	 *
	 * @param jsonResponse
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> ApiV2Response<T> expectApiV2Response(String jsonResponse, TypeReference<ApiV2Response<T>> type) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonResponse, type);
		}
		catch(IOException e) {
			e.printStackTrace();
			assertEquals("Expected ApiV2Response", jsonResponse, false);
		}
		return null;
	}

	/**
	 * Try to covert the given MvcResult into an ApiV2Response matching generics matching the given type.
	 *
	 * If the mvcResult cannot be deserialized, an exception will be thrown eg. JsonMappingException.
	 * To fix deserialization issues, check your type and check that the T class follows the Builder and @Json* annotations pattern.
	 *
	 * @param mvcResult Result from a mockMvc request
	 * @param type Specific TypeReference we should target during Deserialization
	 * @param <T> A DTO class we expect in the ApiV2Response
	 * @return ApiV2Response generified according to the given type
	 * @throws IOException
	 */
	protected <T> ApiV2Response<T> expectApiV2Response(MvcResult mvcResult, TypeReference<ApiV2Response<T>> type) throws IOException {
		MockHttpServletResponse response = mvcResult.getResponse();
		response.setCharacterEncoding("utf8");
		ApiV2Response apiResponse = jackson.readValue(response.getContentAsString(), type);
		// TODO API - lock down the rest of API V2 response
		return apiResponse;
	}
}
