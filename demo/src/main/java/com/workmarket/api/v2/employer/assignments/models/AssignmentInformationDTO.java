package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("AssignmentInformation")
@JsonDeserialize(builder = AssignmentInformationDTO.Builder.class)
public class AssignmentInformationDTO {
	private final BigDecimal cost;
	
	private AssignmentInformationDTO(Builder builder) {
		this.cost = builder.cost;
	}

	@ApiModelProperty(name = "cost")
	@JsonProperty("cost")
	public BigDecimal getCost() {
		return cost;
	}

	public static class Builder implements AbstractBuilder<AssignmentInformationDTO> {
		private BigDecimal cost = BigDecimal.ZERO;

		@JsonProperty("cost") public Builder setCost(BigDecimal cost) {
			this.cost = cost;
			return this;
		}

		@Override
		public AssignmentInformationDTO build() {
			return new AssignmentInformationDTO(this);
		}
	}
}
