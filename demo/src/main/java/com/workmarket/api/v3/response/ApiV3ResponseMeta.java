package com.workmarket.api.v3.response;

import com.workmarket.api.ApiBaseResponseMeta;
import com.workmarket.api.v3.response.meta.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "meta", description = "Metadata about this request")
public interface ApiV3ResponseMeta extends Serializable,
																					 ApiBaseResponseMeta,
																					 ApiV3ResponseMetaStatusCode,
																					 ApiV3ResponseMetaResponseTime,
																					 ApiV3ResponseMetaClientRequestId,
																					 ApiV3ResponseMetaTimestamp,
																					 ApiV3ResponseMetaRequestId {
}
