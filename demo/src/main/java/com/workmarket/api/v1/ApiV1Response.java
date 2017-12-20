package com.workmarket.api.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.workmarket.api.ApiSwaggerModelConverter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonPropertyOrder(value = { "meta", "response" })
public class ApiV1Response<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(name = "meta", value = "Details about this response")
	@JsonProperty
	private ApiV1ResponseMeta meta;

	@ApiModelProperty(name = "response", value = "Response object - may be a list or a single object", reference = ApiSwaggerModelConverter.REFERENCE_GENERIC)
	@JsonProperty
	private Object response;

	public ApiV1Response(T response) {
		this.response = response;
		this.meta = new ApiV1ResponseMeta();
	}

	public ApiV1Response() {
		meta = new ApiV1ResponseMeta();
		response = Collections.emptyList();
	}

	public ApiV1Response(List errors) {
		meta = new ApiV1ResponseMeta(errors);
		response = Collections.emptyList();
	}

	public ApiV1Response(T response, ApiV1ResponseMeta meta) {
		this.meta = meta;
		this.response = response;
	}

	public ApiV1Response(ApiV1ResponseMeta meta) {
		this.meta = meta;
		response = Collections.emptyList();
	}

	public ApiV1Response(T response, List errors, int statusCode) {
		this.meta = new ApiV1ResponseMeta(errors, statusCode);
		this.response = response;
	}

	public ApiV1Response(T response, List errors) {
		this.meta = new ApiV1ResponseMeta(errors);
		this.response = response;
	}

	public ApiV1Response(T response, int statusCode) {
		this.meta = new ApiV1ResponseMeta(Collections.emptyList(), statusCode);
		this.response = response;
	}

	public ApiV1ResponseMeta getMeta() {
		return meta;
	}

	public ApiV1Response<T> setMeta(ApiV1ResponseMeta meta) {
		this.meta = meta;
		return this;
	}

	public T getResponse() {
		return (T)response;
	}

	public ApiV1Response<T> setResponse(T response) {
		this.response = response;
		return this;
	}

	public ApiV1Response<T> setSuccessful(final boolean successful) {
		this.response = new ApiV1ResponseStatus(successful);
		return this;
	}

	public static <T> ApiV1Response<T> wrap(Class<T> type, ApiV1Response<ApiV1ResponseStatus> response) {
		return new ApiV1Response<T>(response.getMeta());
	}

	public static ApiV1Response<ApiV1ResponseStatus> of(final boolean successful) {
		return new ApiV1Response<>(new ApiV1ResponseStatus(successful));
	}

	public static ApiV1Response<ApiV1ResponseStatus> of(final boolean successful, List<String> errors) {
		return new ApiV1Response<>(
			new ApiV1ResponseStatus(successful),
			errors
		);
	}

	public static ApiV1Response<ApiV1ResponseStatus> of(final boolean successful, int statusCode) {
		return new ApiV1Response<>(
			new ApiV1ResponseStatus(successful),
			statusCode
		);
	}

	public static ApiV1Response<ApiV1ResponseStatus> of(final boolean successful, List<String> errors, int statusCode) {
		return new ApiV1Response<>(
			new ApiV1ResponseStatus(successful),
			errors,
			statusCode
		);
	}
}
