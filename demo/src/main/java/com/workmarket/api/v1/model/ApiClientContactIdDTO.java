package com.workmarket.api.v1.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@ApiModel(value = "ClientContactId")
@JsonDeserialize(builder = ApiClientContactIdDTO.Builder.class)
public class ApiClientContactIdDTO {

    @JsonProperty("id")
    @ApiModelProperty(name = "id")
    private final Long id;

    private ApiClientContactIdDTO(final Builder builder) {
        this.id = builder.id;
    }

    public Long getId() {
        return id;
    }

    public static final class Builder {
        private Long id;

        public Builder() {
        }

        @JsonProperty("id")
        public Builder withId(Long id) {
            this.id = id;

            return this;
        }

        public ApiClientContactIdDTO build() {
            return new ApiClientContactIdDTO(this);
        }
    }
}
