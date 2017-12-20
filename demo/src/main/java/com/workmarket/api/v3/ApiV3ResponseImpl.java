package com.workmarket.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v3.response.ApiV3Response;
import com.workmarket.api.v3.response.ApiV3ResponseResult;
import com.workmarket.api.v3.response.ApiV3ResponseResultPagination;
import com.workmarket.api.v3.response.result.ApiV3Error;
import io.swagger.annotations.ApiModel;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class ApiV3ResponseImpl<T> implements ApiV3Response<T> {

	private final ApiJSONPayloadMap meta;
	private final ApiV3ResponseResult<T> result;

	/**
	 * Returns the metadata.
	 *
	 * @return a map of the metdata.
	 */
	public ApiJSONPayloadMap getMeta() {
		return meta;
	}

	/**
	 * Returns the result list.
	 *
	 * @return a list of result objects.
	 */
	public ApiV3ResponseResult<T> getResult() {
		return result;
	}

	/**
	 * Creates a response with an empty list.  Default response code is 200 OK
	 *
	 * @return
	 */
	public static ApiV3ResponseImpl OK() {

		return OK(new ArrayList());
	}

	/**
	 * Create a response with the given list of results.
	 *
	 * @param results List of DTOs - for a single item response, use e.g. Collections.singletonList()
	 *
	 * @param <E>
	 * @return
	 */
	public static <E> ApiV3ResponseImpl<E> OK(List<E> results) {
		return new ApiV3ResponseImpl<>(new ApiJSONPayloadMap(), null, null, null, results);
	}

	/**
	 * Construct a new empty response.
	 */
	public ApiV3ResponseImpl() {
		this(new ApiJSONPayloadMap(), ImmutableList.<T>of());
	}

	/**
	 * Construct a new response with the provided metadata and results
	 */
	public ApiV3ResponseImpl(final ApiJSONPayloadMap meta, final List<T> results) {
		this(meta, null, null, null, results);
	}

	/**
	 * Construct a new response with the provided metadata, results, and pagination
	 */
	public ApiV3ResponseImpl(@JsonProperty("meta") final ApiJSONPayloadMap meta,
													 @JsonProperty("results") final List<ApiV3Error> warnings,
													 @JsonProperty("results") final List<ApiV3Error> errors,
													 @JsonProperty("pagination") final ApiV3ResponseResultPagination pagination,
													 @JsonProperty("results") final List<T> payload) {
		this.meta = meta;
		this.result = new ApiV3ResponseResultImpl(warnings, errors, pagination, payload);
		if(meta.getStatusCode() == null) {
			meta.setStatusCode(HttpStatus.OK.value());
		}
	}

	/**
	 * The key of the status message in the metadata.
	 */
	public static final String META_MESSAGE = "message";

	/**
	 * Create an empty response.
	 */



	/**
	 * Create a response with metadata only, for things like errors.
	 *
	 * @param <T>  The type of the objects contained in the response.
	 * @param meta The map of metadata about the response.
	 * @return The new response.
	 */
	public static <T> ApiV3ResponseImpl<T> valueWithMeta(final ApiJSONPayloadMap meta) {
		return new ApiV3ResponseImpl<>(meta, ImmutableList.<T>of());
	}

	/**
	 * Create a response with just the message part of the metadata and a status code.
	 *
	 * @param message The message to put in in the response.
	 * @param statusCode A Spring HttpStatus object representing the status code to put in the response
	 * @param <T>     The type of the objects contained in the response.
	 * @return The new response.
	 */
	public static <T> ApiV3ResponseImpl<T> valueWithMessage(final String message, HttpStatus statusCode) {
		ApiJSONPayloadMap payloadMap = new ApiJSONPayloadMap();
		payloadMap.put(META_MESSAGE, message);
		payloadMap.setStatusCode(statusCode.value());
		return new ApiV3ResponseImpl<>(payloadMap, ImmutableList.<T>of());
	}

	/**
	 * Create a response with a message, a list of results, and status code
	 *
	 * @param message The message to put in in the response.
	 * @param results The list of result objects for the response.
	 * @param <T>     The type of the objects contained in the response.
	 * @return The new response.
	 */
	public static <T> ApiV3ResponseImpl<T> valueWithMessageAndResults(final String message, final List<T> results, HttpStatus statusCode) {
		ApiJSONPayloadMap payloadMap = new ApiJSONPayloadMap();
		payloadMap.put(META_MESSAGE, message);
		payloadMap.setStatusCode(statusCode.value());
		return new ApiV3ResponseImpl<>(payloadMap, results);
	}

	/**
	 * Create a response with no metadata, just a list of results.
	 *
	 * @param results The list of result objects for the response.
	 * @param <T>     The type of the objects contained in the response.
	 * @return The new response.
	 */
	public static <T> ApiV3ResponseImpl<T> valueWithResults(final List<T> results) {
		return new ApiV3ResponseImpl<>(new ApiJSONPayloadMap(), results);
	}

	/**
	 * Create a response with a single result and no metadata.
	 * The response results will be a single item list.
	 *
	 * @param item A single result object to respond with.
	 * @param <T>  The type of the objects contained in the response.
	 * @return The new response.
	 */
	public static <T> ApiV3ResponseImpl<T> valueWithResult(final T item) {
		return new ApiV3ResponseImpl<>(new ApiJSONPayloadMap(), ImmutableList.of(item));
	}
}
