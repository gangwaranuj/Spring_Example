package com.workmarket.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.workmarket.api.v3.response.result.ApiV3Error;

/**
 * Created by ianha on 4/1/15.
 */
public class ApiBaseError implements ApiV3Error {
	/**
	 * Error code
	 */
	private final String code;

	/**
	 * Human readable error message string
	 */
	private final String message;

	/**
	 * Name of the resource that prompted the error. The name of the resource
	 * is not the Java class name but the resource name as defined in the API
	 * documentation.
	 */
	private final String resource;

	/**
	 * The field of the resource that prompted the error.
	 */
	private final String field;

	public ApiBaseError() {
		this("", "", "", "");
	}

	/**
	 * @deprecated prefer new Error(code, message).
	 */
	@Deprecated
	public ApiBaseError(String message) {

		this("", message);
	}

	public ApiBaseError(String code, String message) {

		this(code, message, "", "");
	}

	public ApiBaseError(String code, String message, String field) {
		this(code, message, field, "");
	}

	public ApiBaseError(String code, String message, String field, String resource) {
		this.code = code;
		this.message = message;
		this.field = field;
		this.resource = resource;
	}

	@JsonProperty("code")
	public String getCode() {
		return code;
	}

	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@JsonProperty("resource")
	public String getResource() {
		return resource;
	}

	@JsonProperty("field")
	public String getField() {
		return field;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
						.add("code", code)
						.add("message", message)
						.add("resource", resource)
						.add("field", field)
						.toString();
	}
}
