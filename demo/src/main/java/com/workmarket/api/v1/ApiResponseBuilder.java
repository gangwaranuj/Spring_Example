package com.workmarket.api.v1;

/**
 * A set of convenience functions to create common ApiResponses
 */
public interface ApiResponseBuilder {
	//TODO: add a response(Map<String, Object> items) helper

	/**
	 * The typical status_code=200, successful=True response
	 * @return
	 */
	ApiV1Response<ApiV1ResponseStatus> standardResponse();

	ApiV1Response<ApiV1ResponseStatus> createErrorResponse(Integer statusCode, String errorProperty, Object... arguments);

	ApiV1Response<ApiV1ResponseStatus> createErrorResponse(String errorProperty, Object... arguments);

	ApiV1Response<ApiV1ResponseStatus> createErrorResponse(ApiV1Exception apiV1Exception);
}
