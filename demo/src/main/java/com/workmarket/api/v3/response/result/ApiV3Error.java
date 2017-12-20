package com.workmarket.api.v3.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by joshlevine on 12/26/16.
 */
public interface ApiV3Error {
	@JsonProperty("code")
	String getCode();

	@JsonProperty("message")
	String getMessage();

	@JsonProperty("resource")
	String getResource();

	@JsonProperty("field")
	String getField();
}
