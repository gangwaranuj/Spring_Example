package com.workmarket.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.ApiV1ResponseMeta;
import com.workmarket.common.util.proto.JacksonProtoModule;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Base class of "expects" for API.
 *
 * ExpectsApiSupport provides API expectations for the baseline and V1 API response classes.
 * You can request upgrade or downgrade to Base API functionality by modifying the expectations.
 *
 * Created by joshlevine on 12/23/16.
 */
public abstract class ExpectApiSupport {
	protected ObjectMapper jackson = new ObjectMapper()
			.registerModule(new GuavaModule())
			.registerModule(new JacksonProtoModule());

	/**
	 * Access the value of the given key as an int from the given ApiBaseResponseMeta.
	 *
	 * @param key
	 * @param responseMeta
	 * @return
	 */
	public int getMetaInt(String key, ApiBaseResponseMeta responseMeta) {
		return Integer.valueOf(String.valueOf(responseMeta.get(key)));
	}

	/**
	 * Access the value of the given key as a String from the given ApiBaseResponseMeta.
	 *
	 * @param key
	 * @param responseMeta
	 * @return
	 */
	public String getMetaString(String key, ApiBaseResponseMeta responseMeta) {
		return String.valueOf(responseMeta.get(key));
	}

	/**
	 * Expect either API V1 or API V2 style status code from the given ApiBaseResponseMeta
	 *
	 * @param expectedStatusCode
	 * @param responseMeta
	 */
	public void expectStatusCode(int expectedStatusCode, ApiBaseResponseMeta responseMeta) {
		boolean apiV1StatusCode = false;
		boolean apiV2StatusCode = false;
		if(responseMeta.get(ApiJSONPayloadMap.META_V1_STATUS_CODE) != null) {
			apiV1StatusCode = true;
			assertEquals("Expected API V1 status code", expectedStatusCode, getMetaInt(ApiJSONPayloadMap.META_V1_STATUS_CODE, responseMeta));
		}
		if(responseMeta.get(ApiJSONPayloadMap.META_V2_STATUS_CODE) != null) {
			apiV1StatusCode = true;
			assertEquals("Expected API V2 status code", expectedStatusCode, getMetaInt(ApiJSONPayloadMap.META_V2_STATUS_CODE, responseMeta));
		}
		assertEquals("Expected either API V1 or V2 Status Code Support", true, apiV1StatusCode || apiV2StatusCode);
	}

	/**
	 * Expect the given status code from the given ApiV1ResponseMeta
	 *
	 * @param expectedStatusCode
	 * @param responseMeta
	 */
	public void expectStatusCode(int expectedStatusCode, ApiV1ResponseMeta responseMeta) {
		assertEquals("Expected API V1 status code", expectedStatusCode, responseMeta.getStatusCode());
	}

	/**
	 * Checks that the given ApiBaseResponseMeta instance implements all expected interfaces.
	 *
	 * @param responseMeta
	 */
	public void expectApiResponseMetaSupport(ApiBaseResponseMeta responseMeta) {
		assertEquals("Expected API V0 Support: get", true, responseMeta instanceof ApiBaseResponseMeta);
	}

	public void expectApiErrorCode(ApiBaseError error, String field, String code) {
		expectApiErrorCode(ImmutableList.of(error), field, code);
	}

	/**
	 * Expects that the given list of errors contains the given code
	 *
	 * @param errors A list of ApiBaseErrors, presumably from a MockMvc Api request's response
	 * @param code The error code we expect to find in the list
	 */
	public void expectApiErrorCode(List<ApiBaseError> errors, String code) {
		expectApiErrorCode(errors, null, code);
	}

	/**
	 * Expects that the given list of errors contains the given code for the given field
	 *
	 * @param errors A list of ApiBaseErrors, presumably from a MockMvc Api request's response
	 * @param field The field in question
	 * @param code The error code we expect to find in the list
	 */
	public void expectApiErrorCode(List<ApiBaseError> errors, String field, String code) {
		boolean errorFound = false;
		for(ApiBaseError error: errors) {
				if(code.equals(error.getCode()) && (field == null || field.equals(error.getField()))) {
					errorFound = true;
					break;
				}
		}
		assertEquals("Expected field[" + field + "] to have error code[" + code + "]: " + errors, true, errorFound);
	}

	/**
	 * Expects that the given error matches the given field and message
	 *
	 * @param error An ApiBaseError, presumably from a MockMvc Api request's response
	 * @param field
	 * @param message
	 */
	public void expectApiErrorMessage(ApiBaseError error, String field, String message) {
		expectApiErrorMessage(ImmutableList.of(error), field, message);
	}

	/**
	 * Expects that the given list of errors has an error matching the given field and message
	 *
	 * @param errors A list of ApiBaseErrors, presumably from a MockMvc Api request's response
	 * @param field
	 * @param message
	 */
	public void expectApiErrorMessage(List<ApiBaseError> errors, String field, String message) {
		boolean errorFound = false;
		for(ApiBaseError error: errors) {
				if(message.equals(error.getMessage()) && field.equals(error.getField())) {
					errorFound = true;
					break;
				}
		}
		assertEquals("Expected field[" + field + "] to have error message[" + message + "]: " + errors, true, errorFound);
	}

	/**
	 * Function that validates that the given response object implements all expected interfaces
	 *
	 * @param apiResponse
	 */
	public void expectApiResponseResult(Object apiResponse) {
		// noop - add baseline Api validation here
	}

	/**
	 * Try to covert the given MvcResult into an ApiV1Response matching generics matching the given type.
	 *
	 * If the mvcResult cannot be deserialized, an exception will be thrown eg. JsonMappingException.
	 * To fix deserialization issues, check your type and check that the T class follows the Builder and @Json* annotations pattern.
	 *
	 * @param mvcResult Result from a mockMvc request
	 * @param type Specific TypeReference we should target during Deserialization
	 * @param <T> A DTO class we expect in the ApiV1Response
	 * @return ApiV1Response generified according to the given type
	 * @throws IOException
	 */
	protected <T>ApiV1Response<T> expectApiV1Response(MvcResult mvcResult, TypeReference<ApiV1Response<T>> type) throws IOException {
		MockHttpServletResponse response = mvcResult.getResponse();
		response.setCharacterEncoding("utf8");
		return jackson.readValue(response.getContentAsString(), type);
	}
}
