package com.workmarket.api.v3.response.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3ResponseMetaStatusCode {
	@ApiModelProperty(name = "statusCode", value = "HTTP status code of this response")
	@JsonProperty
	Integer getStatusCode();

	void setStatusCode(Integer statusCode);
}
