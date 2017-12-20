package com.workmarket.api.v3.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3Response<T> extends Serializable {

	@ApiModelProperty(name = "meta", value = "Metadata about this request")
	@JsonProperty
	ApiV3ResponseMeta getMeta();
	@ApiModelProperty(name = "result", value = "Results envelope")
	@JsonProperty
	ApiV3ResponseResult<T> getResult();
}
