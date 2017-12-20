package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

public class ProblemsDTO {
	private final boolean insufficientFunds;

	private ProblemsDTO(Builder builder) {
		this.insufficientFunds = builder.insufficientFunds;
	}

	@ApiModelProperty(name = "insufficientFunds")
	@JsonProperty("insufficientFunds")
	public boolean isInsufficientFunds() {
		return insufficientFunds;
	}

	public static class Builder implements AbstractBuilder<ProblemsDTO> {
		private boolean insufficientFunds;

		public Builder(ProblemsDTO problemsDTO) {
			this.insufficientFunds = problemsDTO.insufficientFunds;
		}

		public Builder() {}

		@JsonProperty("insufficientFunds")
		public Builder setInsufficientFunds(boolean insufficientFunds) {
			this.insufficientFunds = insufficientFunds;
			return this;
		}

		@Override
		public ProblemsDTO build() {
			return new ProblemsDTO(this);
		}
	}
}
