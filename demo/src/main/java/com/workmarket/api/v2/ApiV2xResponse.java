package com.workmarket.api.v2;

import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.ApiSwaggerModelConverter;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


public class ApiV2xResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private ApiJSONPayloadMap meta;
	private Object response;
	private Object availableCommands;
	private String jsonPCallback;
	private List<String> errors;


	public ApiV2xResponse() {
		response = Collections.emptyList();
		meta = new ApiJSONPayloadMap();
	}

	@ApiModelProperty(name = "meta", value = "Details about this response", dataType = "com.workmarket.api.v3.response.ApiV3ResponseMeta")
	public ApiJSONPayloadMap getMeta() {
		return meta;
	}

	public ApiV2xResponse setMeta(ApiJSONPayloadMap meta) {
		this.meta = meta;
		return this;
	}

	@ApiModelProperty(name = "response", value = "Result object, or list of objects")
	public Object getResponse() {
		return response;
	}

	public ApiV2xResponse setResponse(Object response) {
		this.response = response;
		return this;
	}

	@ApiModelProperty(name = "errors", value = "List of errors")
	public List getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
}
