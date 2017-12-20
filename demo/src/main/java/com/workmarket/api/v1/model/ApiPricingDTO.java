package com.workmarket.api.v1.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Pricing")
@JsonDeserialize(builder = ApiPricingDTO.Builder.class)
public class ApiPricingDTO {

	private final String type;
	private final Double spendLimit;
	private final String budgetIncreases;
	private final Double expenseReimbursements;
	private final Double bonuses;
	private final Double additionalExpenses;
	private final Double flatPrice;
	private final Double perHourPrice;
	private final Double maxNumberOfHours;
	private final Double perUnitPrice;
	private final Double maxNumberOfUnits;
	private final Double initialPerHourPrice;
	private final Double initialNumberOfHours;
	private final Double additionalPerHourPrice;
	private final Double maxBlendedNumberOfHours;

	protected ApiPricingDTO(Builder builder) {
		type = builder.type;
		spendLimit = builder.spendLimit;
		budgetIncreases = builder.budgetIncreases;
		expenseReimbursements = builder.expenseReimbursements;
		bonuses = builder.bonuses;
		additionalExpenses = builder.additionalExpenses;
		flatPrice = builder.flatPrice;
		perHourPrice = builder.perHourPrice;
		maxNumberOfHours = builder.maxNumberOfHours;
		perUnitPrice = builder.perUnitPrice;
		maxNumberOfUnits = builder.maxNumberOfUnits;
		initialPerHourPrice = builder.initialPerHourPrice;
		initialNumberOfHours = builder.initialNumberOfHours;
		additionalPerHourPrice = builder.additionalPerHourPrice;
		maxBlendedNumberOfHours = builder.maxBlendedNumberOfHours;
	}

	@ApiModelProperty(name = "type")
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@ApiModelProperty(name = "spend_limit")
	@JsonProperty("spend_limit")
	public Double getSpendLimit() {
		return spendLimit;
	}

	@ApiModelProperty(name = "budget_increases")
	@JsonProperty("budget_increases")
	public String getBudgetIncreases() {
		return budgetIncreases;
	}

	@ApiModelProperty(name = "expense_reimbursements")
	@JsonProperty("expense_reimbursements")
	public Double getExpenseReimbursements() {
		return expenseReimbursements;
	}

	@ApiModelProperty(name = "bonuses")
	@JsonProperty("bonuses")
	public Double getBonuses() {
		return bonuses;
	}

	@ApiModelProperty(name = "additional_expenses")
	@JsonProperty("additional_expenses")
	public Double getAdditionalExpenses() {
		return additionalExpenses;
	}

	@ApiModelProperty(name = "flat_price")
	@JsonProperty("flat_price")
	public Double getFlatPrice() {
		return flatPrice;
	}

	@ApiModelProperty(name = "per_hour_price")
	@JsonProperty("per_hour_price")
	public Double getPerHourPrice() {
		return perHourPrice;
	}

	@ApiModelProperty(name = "max_number_of_hours")
	@JsonProperty("max_number_of_hours")
	public Double getMaxNumberOfHours() {
		return maxNumberOfHours;
	}

	@ApiModelProperty(name = "per_unit_price")
	@JsonProperty("per_unit_price")
	public Double getPerUnitPrice() {
		return perUnitPrice;
	}

	@ApiModelProperty(name = "max_number_of_units")
	@JsonProperty("max_number_of_units")
	public Double getMaxNumberOfUnits() {
		return maxNumberOfUnits;
	}

	@ApiModelProperty(name = "initial_per_hour_price")
	@JsonProperty("initial_per_hour_price")
	public Double getInitialPerHourPrice() {
		return initialPerHourPrice;
	}

	@ApiModelProperty(name = "initial_number_of_hours")
	@JsonProperty("initial_number_of_hours")
	public Double getInitialNumberOfHours() {
		return initialNumberOfHours;
	}

	@ApiModelProperty(name = "additional_per_hour_price")
	@JsonProperty("additional_per_hour_price")
	public Double getAdditionalPerHourPrice() {
		return additionalPerHourPrice;
	}

	@ApiModelProperty(name = "max_blended_number_of_hours")
	@JsonProperty("max_blended_number_of_hours")
	public Double getMaxBlendedNumberOfHours() {
		return maxBlendedNumberOfHours;
	}

	public static class Builder {
		private String type;
		private Double spendLimit;
		private String budgetIncreases;
		private Double expenseReimbursements;
		private Double bonuses;
		private Double additionalExpenses;
		private Double flatPrice;
		private Double perHourPrice;
		private Double maxNumberOfHours;
		private Double perUnitPrice;
		private Double maxNumberOfUnits;
		private Double initialPerHourPrice;
		private Double initialNumberOfHours;
		private Double additionalPerHourPrice;
		private Double maxBlendedNumberOfHours;

		public Builder() {
		}

		public Builder(ApiPricingDTO copy) {
			this.type = copy.type;
			this.spendLimit = copy.spendLimit;
			this.budgetIncreases = copy.budgetIncreases;
			this.expenseReimbursements = copy.expenseReimbursements;
			this.bonuses = copy.bonuses;
			this.additionalExpenses = copy.additionalExpenses;
			this.flatPrice = copy.flatPrice;
			this.perHourPrice = copy.perHourPrice;
			this.maxNumberOfHours = copy.maxNumberOfHours;
			this.perUnitPrice = copy.perUnitPrice;
			this.maxNumberOfUnits = copy.maxNumberOfUnits;
			this.initialPerHourPrice = copy.initialPerHourPrice;
			this.initialNumberOfHours = copy.initialNumberOfHours;
			this.additionalPerHourPrice = copy.additionalPerHourPrice;
			this.maxBlendedNumberOfHours = copy.maxBlendedNumberOfHours;
		}

		@JsonProperty("type")
		public Builder withType(String type) {
			this.type = type;
			return this;
		}

		@JsonProperty("spend_limit")
		public Builder withSpendLimit(Double spendLimit) {
			this.spendLimit = spendLimit;
			return this;
		}

		@JsonProperty("budget_increases")
		public Builder withBudgetIncreases(String budgetIncreases) {
			this.budgetIncreases = budgetIncreases;
			return this;
		}

		@JsonProperty("expense_reimbursements")
		public Builder withExpenseReimbursements(Double expenseReimbursements) {
			this.expenseReimbursements = expenseReimbursements;
			return this;
		}

		@JsonProperty("bonuses")
		public Builder withBonuses(Double bonuses) {
			this.bonuses = bonuses;
			return this;
		}

		@JsonProperty("additional_expenses")
		public Builder withAdditionalExpenses(Double additionalExpenses) {
			this.additionalExpenses = additionalExpenses;
			return this;
		}

		@JsonProperty("flat_price")
		public Builder withFlatPrice(Double flatPrice) {
			this.flatPrice = flatPrice;
			return this;
		}

		@JsonProperty("per_hour_price")
		public Builder withPerHourPrice(Double perHourPrice) {
			this.perHourPrice = perHourPrice;
			return this;
		}

		@JsonProperty("max_number_of_hours")
		public Builder withMaxNumberOfHours(Double maxNumberOfHours) {
			this.maxNumberOfHours = maxNumberOfHours;
			return this;
		}

		@JsonProperty("per_unit_price")
		public Builder withPerUnitPrice(Double perUnitPrice) {
			this.perUnitPrice = perUnitPrice;
			return this;
		}

		@JsonProperty("max_number_of_units")
		public Builder withMaxNumberOfUnits(Double maxNumberOfUnits) {
			this.maxNumberOfUnits = maxNumberOfUnits;
			return this;
		}

		@JsonProperty("initial_per_hour_price")
		public Builder withInitialPerHourPrice(Double initialPerHourPrice) {
			this.initialPerHourPrice = initialPerHourPrice;
			return this;
		}

		@JsonProperty("initial_number_of_hours")
		public Builder withInitialNumberOfHours(Double initialNumberOfHours) {
			this.initialNumberOfHours = initialNumberOfHours;
			return this;
		}

		@JsonProperty("additional_per_hour_price")
		public Builder withAdditionalPerHourPrice(Double additionalPerHourPrice) {
			this.additionalPerHourPrice = additionalPerHourPrice;
			return this;
		}

		@JsonProperty("max_blended_number_of_hours")
		public Builder withMaxBlendedNumberOfHours(Double maxBlendedNumberOfHours) {
			this.maxBlendedNumberOfHours = maxBlendedNumberOfHours;
			return this;
		}

		public ApiPricingDTO build() {
			return new ApiPricingDTO(this);
		}
	}
}
