package com.workmarket.api.v3.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v3.response.result.ApiV3ResponseResultErrors;
import com.workmarket.api.v3.response.result.ApiV3ResponseResultWarnings;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3ResponseResult<T> extends Serializable,
																								ApiV3ResponseResultWarnings,
																								ApiV3ResponseResultErrors {

	@ApiModelProperty(name = "pagination", value = "Information about paginations")
	@JsonProperty
	ApiV3ResponseResultPagination getPagination();

	@ApiModelProperty(name = "payload", value = "List of results")
	@JsonProperty
	List<T> getPayload();
}
