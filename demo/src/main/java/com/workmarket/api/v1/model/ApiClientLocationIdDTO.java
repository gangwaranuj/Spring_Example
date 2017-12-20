package com.workmarket.api.v1.model;

import com.workmarket.api.v1.ApiV1ResponseStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(Include.NON_NULL)
@ApiModel(value = "ClientLocationId")
@JsonDeserialize(builder = ApiClientLocationIdDTO.Builder.class)
public class ApiClientLocationIdDTO extends ApiV1ResponseStatus {

    private final Long id;

    private final Long contactId;

    private ApiClientLocationIdDTO(final Builder builder) {
        this.id = builder.id;
        this.contactId = builder.contactId;
        this.successful = builder.successful;
    }

    @JsonProperty("id")
    @ApiModelProperty(name = "id")
    public Long getId() {
        return this.id;
    }

    @JsonProperty("contact_id")
    @ApiModelProperty(name = "contact_id")
    public Long getContactId() {
        return this.contactId;
    }

    public static final class Builder {
        private Long id;
        private Long contactId;
        private boolean successful;

        public Builder() {
        }

        @JsonProperty("id")
        public Builder withId(Long id) {
            this.id = id;

            return this;
        }

        @JsonProperty("contact_id")
        public Builder withContactId(Long contactId) {
            this.contactId = contactId;

            return this;
        }

        @JsonProperty("successful")
        public Builder withStatus(boolean flag) {
            this.successful = flag;

            return this;
        }

        public ApiClientLocationIdDTO build() {
            return new ApiClientLocationIdDTO(this);
        }
    }
}
