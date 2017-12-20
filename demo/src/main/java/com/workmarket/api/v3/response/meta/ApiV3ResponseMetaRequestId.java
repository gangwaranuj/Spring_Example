package com.workmarket.api.v3.response.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3ResponseMetaRequestId {
	@ApiModelProperty(name = "requestId", value = "UUID of this request")
	@JsonProperty
	String getRequestId();

	void setRequestId(String requestId);
}
