package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "BudgetIncreaseRequest")
@JsonDeserialize(builder = ApiBudgetIncreaseRequestDTO.Builder.class)
public class ApiBudgetIncreaseRequestDTO extends ApiPricingDTO {

	private final String note;
	private final Long requestedOn;

	private ApiBudgetIncreaseRequestDTO(Builder builder) {
		super(builder);
		note = builder.note;
		requestedOn = builder.requestedOn;
	}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	@ApiModelProperty(name = "requested_on")
	@JsonProperty("requested_on")
	public Long getRequestedOn() {
		return requestedOn;
	}

	public static final class Builder extends ApiPricingDTO.Builder {
		private String note;
		private Long requestedOn;

		public Builder() {

		}

		public Builder(ApiPricingDTO.Builder apiPricingDTO) {
			super(apiPricingDTO.build());
		}

		public Builder(ApiBudgetIncreaseRequestDTO copy) {
			super(copy);
			this.note = copy.note;
			this.requestedOn = copy.requestedOn;
		}

		@JsonProperty("note")
		public Builder withNote(String note) {
			this.note = note;
			return this;
		}

		@JsonProperty("requested_on")
		public Builder withRequestedOn(Long requestedOn) {
			this.requestedOn = requestedOn;
			return this;
		}

		public ApiBudgetIncreaseRequestDTO build() {
			return new ApiBudgetIncreaseRequestDTO(this);
		}
	}
}
