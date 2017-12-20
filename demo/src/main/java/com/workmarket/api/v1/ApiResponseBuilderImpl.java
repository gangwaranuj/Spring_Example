package com.workmarket.api.v1;

import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ApiResponseBuilderImpl implements ApiResponseBuilder {
	@Autowired private MessageBundleHelper messageHelper;

	@Override
	public ApiV1Response<ApiV1ResponseStatus> standardResponse() {
		return ApiV1Response.of(true);
	}

	@Override
	public ApiV1Response<ApiV1ResponseStatus> createErrorResponse(Integer statusCode, String errorProperty, Object... arguments) {
		final List<String> messages = Arrays.asList(messageHelper.getMessage(errorProperty, arguments));
		final ApiV1Response apiResponse = ApiV1Response.of(false, messages, statusCode);

		return apiResponse;
	}

	@Override
	public ApiV1Response<ApiV1ResponseStatus> createErrorResponse(String errorProperty, Object... arguments) {
		return createErrorResponse(HttpStatus.SC_BAD_REQUEST, errorProperty, arguments);
	}

	@Override
	public ApiV1Response<ApiV1ResponseStatus> createErrorResponse(ApiV1Exception apiV1Exception) {
		return createErrorResponse(
						apiV1Exception.getStatusCode(), apiV1Exception.getMessage(), apiV1Exception.getMessageArguments());
	}
}
