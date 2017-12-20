package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Negotiation")
@JsonDeserialize(builder = ApiNegotiationDTO.Builder.class)
public class ApiNegotiationDTO {

	private final ApiRescheduleRequestDTO rescheduleRequest;
	private final ApiBudgetIncreaseRequestDTO budgetIncreaseRequest;
	private final ApiExpenseReimbursementRequestDTO expenseReimbursementRequest;
	private final ApiBonusRequestDTO bonusRequest;

	private ApiNegotiationDTO(Builder builder) {
		rescheduleRequest = builder.rescheduleRequest;
		budgetIncreaseRequest = builder.budgetIncreaseRequest;
		expenseReimbursementRequest = builder.expenseReimbursementRequest;
		bonusRequest = builder.bonusRequest;
	}

	@ApiModelProperty(name = "reschedule_request")
	@JsonProperty("reschedule_request")
	public ApiRescheduleRequestDTO getRescheduleRequest() {
		return rescheduleRequest;
	}

	@ApiModelProperty(name = "budget_increase_request")
	@JsonProperty("budget_increase_request")
	public ApiBudgetIncreaseRequestDTO getBudgetIncreaseRequest() {
		return budgetIncreaseRequest;
	}

	@ApiModelProperty(name = "expense_reimbursement_request")
	@JsonProperty("expense_reimbursement_request")
	public ApiExpenseReimbursementRequestDTO getExpenseReimbursementRequest() {
		return expenseReimbursementRequest;
	}

	@ApiModelProperty(name = "bonus_request")
	@JsonProperty("bonus_request")
	public ApiBonusRequestDTO getBonusRequest() {
		return bonusRequest;
	}

	public static final class Builder {
		private ApiRescheduleRequestDTO rescheduleRequest;
		private ApiBudgetIncreaseRequestDTO budgetIncreaseRequest;
		private ApiExpenseReimbursementRequestDTO expenseReimbursementRequest;
		private ApiBonusRequestDTO bonusRequest;

		public Builder() {
		}

		public Builder(ApiNegotiationDTO copy) {
			this.rescheduleRequest = copy.rescheduleRequest;
			this.budgetIncreaseRequest = copy.budgetIncreaseRequest;
			this.expenseReimbursementRequest = copy.expenseReimbursementRequest;
			this.bonusRequest = copy.bonusRequest;
		}

		@JsonProperty("reschedule_request")
		public Builder withRescheduleRequest(ApiRescheduleRequestDTO rescheduleRequest) {
			this.rescheduleRequest = rescheduleRequest;
			return this;
		}

		@JsonProperty("budget_increase_request")
		public Builder withBudgetIncreaseRequest(ApiBudgetIncreaseRequestDTO budgetIncreaseRequest) {
			this.budgetIncreaseRequest = budgetIncreaseRequest;
			return this;
		}

		@JsonProperty("expense_reimbursement_request")
		public Builder withExpenseReimbursementRequest(ApiExpenseReimbursementRequestDTO expenseReimbursementRequest) {
			this.expenseReimbursementRequest = expenseReimbursementRequest;
			return this;
		}

		@JsonProperty("bonus_request")
		public Builder withBonusRequest(ApiBonusRequestDTO bonusRequest) {
			this.bonusRequest = bonusRequest;
			return this;
		}

		public ApiNegotiationDTO build() {
			return new ApiNegotiationDTO(this);
		}
	}
}

