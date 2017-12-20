package com.workmarket.api.v1;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@ApiModel(value = "v1Status")
public class ApiV1ResponseStatus implements Serializable {

    protected Boolean successful;

    public ApiV1ResponseStatus(boolean successful) {
        this.successful = successful;
    }

    public ApiV1ResponseStatus() {
        this.successful = false;
    }

    @JsonProperty
    @ApiModelProperty(name = "successful")
    public boolean isSuccessful() {
        return this.successful;
    }
}
