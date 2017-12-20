package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ExpenseReimbursementRequest")
@JsonDeserialize(builder = ApiExpenseReimbursementRequestDTO.Builder.class)
public class ApiExpenseReimbursementRequestDTO {

	private final Double expenseReimbursement;
	private final String note;
	private final Long requestedOn;

	private ApiExpenseReimbursementRequestDTO(Builder builder) {
		expenseReimbursement = builder.expenseReimbursement;
		note = builder.note;
		requestedOn = builder.requestedOn;
	}

	@ApiModelProperty(name = "expense_reimbursement")
	@JsonProperty("expense_reimbursement")
	public Double getExpenseReimbursement() {
		return expenseReimbursement;
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

	public static final class Builder {
		private Double expenseReimbursement;
		private String note;
		private Long requestedOn;

		public Builder() {
		}

		public Builder(ApiExpenseReimbursementRequestDTO copy) {
			this.expenseReimbursement = copy.expenseReimbursement;
			this.note = copy.note;
			this.requestedOn = copy.requestedOn;
		}

		@JsonProperty("expense_reimbursement")
		public Builder withExpenseReimbursement(Double expenseReimbursement) {
			this.expenseReimbursement = expenseReimbursement;
			return this;
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

		public ApiExpenseReimbursementRequestDTO build() {
			return new ApiExpenseReimbursementRequestDTO(this);
		}
	}
}
