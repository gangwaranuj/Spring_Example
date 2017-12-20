package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application")
@JsonDeserialize(builder = ApiApplicationDTO.Builder.class)
public class ApiApplicationDTO {

	private final Long id;
	private final String note;
	private final ApiApplicantProfileDTO resource;
	private final ApiRescheduleRequestDTO scheduling;
	private final ApiPricingDTO pricing;
	private final Long expiresOn;
	private final Boolean expired;

	private ApiApplicationDTO(Builder builder) {
		id = builder.id;
		note = builder.note;
		resource = builder.resource;
		scheduling = builder.scheduling;
		pricing = builder.pricing;
		expiresOn = builder.expiresOn;
		expired = builder.expired;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	@ApiModelProperty(name = "resource")
	@JsonProperty("resource")
	public ApiApplicantProfileDTO getResource() {
		return resource;
	}

	@ApiModelProperty(name = "scheduling")
	@JsonProperty("scheduling")
	public ApiRescheduleRequestDTO getScheduling() {
		return scheduling;
	}

	@ApiModelProperty(name = "pricing")
	@JsonProperty("pricing")
	public ApiPricingDTO getPricing() {
		return pricing;
	}

	@ApiModelProperty(name = "expires_on")
	@JsonProperty("expires_on")
	public Long getExpiresOn() {
		return expiresOn;
	}

	@ApiModelProperty(name = "expired")
	@JsonProperty("expired")
	public Boolean getExpired() {
		return expired;
	}

	public static final class Builder {
		private Long id;
		private String note;
		private ApiApplicantProfileDTO resource;
		private ApiRescheduleRequestDTO scheduling;
		private ApiPricingDTO pricing;
		private Long expiresOn;
		private Boolean expired;

		public Builder() {
		}

		public Builder(ApiApplicationDTO copy) {
			this.id = copy.id;
			this.note = copy.note;
			this.resource = copy.resource;
			this.scheduling = copy.scheduling;
			this.pricing = copy.pricing;
			this.expiresOn = copy.expiresOn;
			this.expired = copy.expired;
		}

		@JsonProperty("id")
		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("note")
		public Builder withNote(String note) {
			this.note = note;
			return this;
		}

		@JsonProperty("resource")
		public Builder withResource(ApiApplicantProfileDTO resource) {
			this.resource = resource;
			return this;
		}

		@JsonProperty("scheduling")
		public Builder withScheduling(ApiRescheduleRequestDTO scheduling) {
			this.scheduling = scheduling;
			return this;
		}

		@JsonProperty("pricing")
		public Builder withPricing(ApiPricingDTO pricing) {
			this.pricing = pricing;
			return this;
		}

		@JsonProperty("expires_on")
		public Builder withExpiresOn(Long expiresOn) {
			this.expiresOn = expiresOn;
			return this;
		}

		@JsonProperty("expired")
		public Builder withExpired(Boolean expired) {
			this.expired = expired;
			return this;
		}

		public ApiApplicationDTO build() {
			return new ApiApplicationDTO(this);
		}
	}
}
