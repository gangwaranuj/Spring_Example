package com.workmarket.api.v2;

import com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.ApiSwaggerModelConverter;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ApiV2Response<T> {

	@ApiModelProperty(name = "meta", value = "Details about this response", dataType = "com.workmarket.api.v3.response.ApiV3ResponseMeta")
	@JsonProperty("meta")
	private final ApiJSONPayloadMap meta;

	@ApiModelProperty(name = "results", value = "List of results", reference = ApiSwaggerModelConverter.REFERENCE_GENERIC_LIST)
	@JsonProperty("results")
	private final List<T> results;

	@ApiModelProperty(name = "pagination", value = "Pagination details (optional)")
	@JsonInclude(JsonInclude.Include.NON_NULL)
  private Object pagination;

	/**
	 * Returns the metadata.
	 *
	 * @return a map of the metadata.
	 */
	public ApiJSONPayloadMap getMeta() {
		return meta;
	}

	/**
	 * Returns the result list.
	 *
	 * @return a list of result objects.
	 */
	public List<T> getResults() {
		return results;
	}

	public Object getPagination() {
		return pagination;
	}

	/**
	 * Creates a response with an empty list.  Default response code is 200 OK
	 *
	 * @return
	 */
	public static ApiV2Response OK() {

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
	public static <E> ApiV2Response<E> OK(List<E> results) {
		return new ApiV2Response<>(new ApiJSONPayloadMap(), results, null);
	}

	/**
	 * Construct a new empty response.
	 */
	public ApiV2Response() {
		this(new ApiJSONPayloadMap(), ImmutableList.<T>of());
	}

	/**
	 * Construct a new response with the provided metadata and results
	 */
	public ApiV2Response(final ApiJSONPayloadMap meta, final List<T> results) {
		this(meta, results, null);
	}

	/**
	 * Construct a new response with the provided metadata, results, and pagination
	 */
	public ApiV2Response(@JsonProperty("meta") final ApiJSONPayloadMap meta,
											 @JsonProperty("results") final List<T> results,
											 @JsonProperty("pagination") final Object pagination) {
		this.meta = meta;
		this.results = ImmutableList.copyOf(results);
		this.pagination = pagination;
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
	public static <T> ApiV2Response<T> valueWithMeta(final ApiJSONPayloadMap meta) {
		return new ApiV2Response<>(meta, ImmutableList.<T>of());
	}

	/**
	 * Create a response with just the message part of the metadata and a status code.
	 *
	 * @param message The message to put in in the response.
	 * @param statusCode A Spring HttpStatus object representing the status code to put in the response
	 * @param <T>     The type of the objects contained in the response.
	 * @return The new response.
	 */
	public static <T> ApiV2Response<T> valueWithMessage(final String message, HttpStatus statusCode) {
		ApiJSONPayloadMap payloadMap = new ApiJSONPayloadMap();
		payloadMap.put(META_MESSAGE, message);
		payloadMap.setStatusCode(statusCode.value());
		return new ApiV2Response<>(payloadMap, ImmutableList.<T>of());
	}

	/**
	 * Create a response with a message, a list of results, and status code
	 *
	 * @param message The message to put in in the response.
	 * @param results The list of result objects for the response.
	 * @param <T>     The type of the objects contained in the response.
	 * @return The new response.
	 */
	public static <T> ApiV2Response<T> valueWithMessageAndResults(final String message, final List<T> results, HttpStatus statusCode) {
		ApiJSONPayloadMap payloadMap = new ApiJSONPayloadMap();
		payloadMap.put(META_MESSAGE, message);
		payloadMap.setStatusCode(statusCode.value());
		return new ApiV2Response<>(payloadMap, results);
	}

	/**
	 * Create a response with no metadata, just a list of results.
	 *
	 * @param results The list of result objects for the response.
	 * @param <T>     The type of the objects contained in the response.
	 * @return The new response.
	 */
	public static <T> ApiV2Response<T> valueWithResults(final List<T> results) {
		return new ApiV2Response<>(new ApiJSONPayloadMap(), results);
	}

	/**
	 * Create a response with a single result and no metadata.
	 * The response results will be a single item list.
	 *
	 * @param item A single result object to respond with.
	 * @param <T>  The type of the objects contained in the response.
	 * @return The new response.
	 */
	public static <T> ApiV2Response<T> valueWithResult(final T item) {
		return new ApiV2Response<>(new ApiJSONPayloadMap(), ImmutableList.of(item));
	}
}
