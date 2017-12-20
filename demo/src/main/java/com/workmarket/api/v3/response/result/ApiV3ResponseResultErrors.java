package com.workmarket.api.v3.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.ApiJSONPayloadMap;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3ResponseResultErrors {
	@ApiModelProperty(value = "errors", notes = "List of errors that occurred while processing this request")
	@JsonProperty
	List<ApiV3Error> getErrors();


	void setErrors(List<ApiV3Error> errors);
}
