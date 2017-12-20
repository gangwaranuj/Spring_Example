package com.workmarket.api.v3.response.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3ResponseMetaClientRequestId {
	@ApiModelProperty(name = "clientRequestId", value = "Unique id provided by client")
	@JsonProperty
	String getClientRequestId();

	void setClientRequestId(String clientRequestId);
}
