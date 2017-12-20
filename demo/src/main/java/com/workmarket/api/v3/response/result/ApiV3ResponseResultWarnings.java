package com.workmarket.api.v3.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.ApiBaseError;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3ResponseResultWarnings {
	@ApiModelProperty(value = "warnings", notes = "List of warnings that occurred while processing this request")
	@JsonProperty
	List<ApiV3Error> getWarnings();


	void setWarnings(List<ApiV3Error> errors);
}
