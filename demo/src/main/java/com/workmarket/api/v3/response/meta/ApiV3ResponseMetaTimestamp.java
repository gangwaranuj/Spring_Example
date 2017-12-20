package com.workmarket.api.v3.response.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3ResponseMetaTimestamp {
	@ApiModelProperty(name = "timestamp", value = "Request start time in seconds from epoch")
	@JsonProperty
	Long getTimestamp();
	void setTimestamp(Long timestamp);
}
