package com.workmarket.api.v3.response.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public interface ApiV3ResponseMetaResponseTime {
	@ApiModelProperty(name = "responseTime", value = "Server response time in seconds")
	@JsonProperty
	Double getResponseTime();

	void setResponseTime(Double responseTime);
}
