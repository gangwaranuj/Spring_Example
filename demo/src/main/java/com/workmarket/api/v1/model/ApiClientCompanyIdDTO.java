package com.workmarket.api.v1.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@ApiModel(value = "ClientCompanyId")
@JsonDeserialize(builder = ApiClientCompanyIdDTO.Builder.class)
public class ApiClientCompanyIdDTO {

    @JsonProperty("id")
    @ApiModelProperty(name = "id")
    private final Long id;

    private ApiClientCompanyIdDTO(final Builder builder) {
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

        public ApiClientCompanyIdDTO build() {
            return new ApiClientCompanyIdDTO(this);
        }
    }
}
