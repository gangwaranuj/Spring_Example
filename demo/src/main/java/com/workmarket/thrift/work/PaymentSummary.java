package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class PaymentSummary implements Serializable {
	private static final long serialVersionUID = 1L;

	private double maxSpendLimit;
	private double actualSpendLimit;
	private double buyerFee;
	private double buyerFeePercentage;
	private int buyerFeeBand;
	private double totalCost;
	private double hoursWorked;
	private double unitsProcessed;
	private boolean salesTaxCollectedFlag;
	private double salesTaxCollected;
	private double salesTaxRate;
	private long paidOn;
	private long paymentDueOn;
	private boolean legacyBuyerFee;
	private double additionalExpenses;
	private double additionalExpensesWithFee;
	private double bonus;
	private double bonusWithFee;
	private double perHourPriceWithFee;
	private double perUnitPriceWithFee;
	private double initialPerHourPriceWithFee;
	private double additionalPerHourPriceWithFee;

	public PaymentSummary() {
	}

	public PaymentSummary(
			double maxSpendLimit,
			double actualSpendLimit,
			double buyerFee,
			double buyerFeePercentage,
			int buyerFeeBand,
			double totalCost,
			double hoursWorked,
			double unitsProcessed,
			boolean salesTaxCollectedFlag,
			double salesTaxCollected,
			double salesTaxRate,
			long paidOn,
			long paymentDueOn,
			boolean legacyBuyerFee,
			double additionalExpenses,
			double additionalExpensesWithFee,
			double perHourPriceWithFee,
			double perUnitPriceWithFee,
			double initialPerHourPriceWithFee,
			double additionalPerHourPriceWithFee) {
		this();
		this.maxSpendLimit = maxSpendLimit;
		this.actualSpendLimit = actualSpendLimit;
		this.buyerFee = buyerFee;
		this.buyerFeePercentage = buyerFeePercentage;
		this.buyerFeeBand = buyerFeeBand;
		this.totalCost = totalCost;
		this.hoursWorked = hoursWorked;
		this.unitsProcessed = unitsProcessed;
		this.salesTaxCollectedFlag = salesTaxCollectedFlag;
		this.salesTaxCollected = salesTaxCollected;
		this.salesTaxRate = salesTaxRate;
		this.paidOn = paidOn;
		this.paymentDueOn = paymentDueOn;
		this.legacyBuyerFee = legacyBuyerFee;
		this.additionalExpenses = additionalExpenses;
		this.additionalExpensesWithFee = additionalExpensesWithFee;
		this.perHourPriceWithFee = perHourPriceWithFee;
		this.perUnitPriceWithFee = perUnitPriceWithFee;
		this.initialPerHourPriceWithFee = initialPerHourPriceWithFee;
		this.additionalPerHourPriceWithFee = additionalPerHourPriceWithFee;

	}

	public double getMaxSpendLimit() {
		return this.maxSpendLimit;
	}

	public PaymentSummary setMaxSpendLimit(double maxSpendLimit) {
		this.maxSpendLimit = maxSpendLimit;
		return this;
	}

	public boolean isSetMaxSpendLimit() {
		return (maxSpendLimit > 0D);
	}

	public double getActualSpendLimit() {
		return this.actualSpendLimit;
	}

	public PaymentSummary setActualSpendLimit(double actualSpendLimit) {
		this.actualSpendLimit = actualSpendLimit;
		return this;
	}

	public boolean isSetActualSpendLimit() {
		return (actualSpendLimit > 0D);
	}

	public double getBuyerFee() {
		return this.buyerFee;
	}

	public PaymentSummary setBuyerFee(double buyerFee) {
		this.buyerFee = buyerFee;
		return this;
	}

	public boolean isSetBuyerFee() {
		return (buyerFee > 0D);
	}

	public double getBuyerFeePercentage() {
		return this.buyerFeePercentage;
	}

	public PaymentSummary setBuyerFeePercentage(double buyerFeePercentage) {
		this.buyerFeePercentage = buyerFeePercentage;
		return this;
	}

	public boolean isSetBuyerFeePercentage() {
		return (buyerFeePercentage > 0D);
	}

	public int getBuyerFeeBand() {
		return this.buyerFeeBand;
	}

	public PaymentSummary setBuyerFeeBand(int buyerFeeBand) {
		this.buyerFeeBand = buyerFeeBand;
		return this;
	}

	public boolean isSetBuyerFeeBand() {
		return (buyerFeeBand > 0);
	}

	public double getTotalCost() {
		return this.totalCost;
	}

	public PaymentSummary setTotalCost(double totalCost) {
		this.totalCost = totalCost;
		return this;
	}

	public boolean isSetTotalCost() {
		return (totalCost > 0D);
	}

	public double getHoursWorked() {
		return this.hoursWorked;
	}

	public PaymentSummary setHoursWorked(double hoursWorked) {
		this.hoursWorked = hoursWorked;
		return this;
	}

	public boolean isSetHoursWorked() {
		return (hoursWorked > 0D);
	}

	public double getUnitsProcessed() {
		return this.unitsProcessed;
	}

	public PaymentSummary setUnitsProcessed(double unitsProcessed) {
		this.unitsProcessed = unitsProcessed;
		return this;
	}

	public boolean isSetUnitsProcessed() {
		return (unitsProcessed > 0D);
	}

	public boolean isSalesTaxCollectedFlag() {
		return this.salesTaxCollectedFlag;
	}

	public PaymentSummary setSalesTaxCollectedFlag(boolean salesTaxCollectedFlag) {
		this.salesTaxCollectedFlag = salesTaxCollectedFlag;
		return this;
	}

	public double getSalesTaxCollected() {
		return this.salesTaxCollected;
	}

	public PaymentSummary setSalesTaxCollected(double salesTaxCollected) {
		this.salesTaxCollected = salesTaxCollected;
		return this;
	}

	public boolean isSetSalesTaxCollected() {
		return (salesTaxCollected > 0D);
	}

	public double getSalesTaxRate() {
		return this.salesTaxRate;
	}

	public PaymentSummary setSalesTaxRate(double salesTaxRate) {
		this.salesTaxRate = salesTaxRate;
		return this;
	}

	public boolean isSetSalesTaxRate() {
		return (salesTaxRate > 0D);
	}

	public long getPaidOn() {
		return this.paidOn;
	}

	public PaymentSummary setPaidOn(long paidOn) {
		this.paidOn = paidOn;
		return this;
	}

	public long getPaymentDueOn() {
		return this.paymentDueOn;
	}

	public PaymentSummary setPaymentDueOn(long paymentDueOn) {
		this.paymentDueOn = paymentDueOn;
		return this;
	}

	public boolean isSetPaymentDueOn() {
		return (paymentDueOn > 0L);
	}

	public boolean isLegacyBuyerFee() {
		return this.legacyBuyerFee;
	}

	public PaymentSummary setLegacyBuyerFee(boolean legacyBuyerFee) {
		this.legacyBuyerFee = legacyBuyerFee;
		return this;
	}

	public double getAdditionalExpenses() {
		return this.additionalExpenses;
	}

	public PaymentSummary setAdditionalExpenses(double additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
		return this;
	}

	public double getAdditionalExpensesWithFee() {
		return additionalExpensesWithFee;
	}

	public PaymentSummary setAdditionalExpensesWithFee(double additionalExpensesWithFee) {
		this.additionalExpensesWithFee = additionalExpensesWithFee;
		return this;
	}

	public double getBonus() {
		return bonus;
	}

	public PaymentSummary setBonus(double bonus) {
		this.bonus = bonus;
		return this;
	}

	public double getBonusWithFee() {
		return bonusWithFee;
	}

	public PaymentSummary setBonusWithFee(double bonusWithFee) {
		this.bonusWithFee = bonusWithFee;
		return this;
	}

	public double getPerHourPriceWithFee() {
		return perHourPriceWithFee;
	}

	public PaymentSummary setPerHourPriceWithFee(double perHourPriceWithFee) {
		this.perHourPriceWithFee = perHourPriceWithFee;
		return this;
	}

	public double getPerUnitPriceWithFee() {
		return perUnitPriceWithFee;
	}

	public PaymentSummary setPerUnitPriceWithFee(double perUnitPriceWithFee) {
		this.perUnitPriceWithFee = perUnitPriceWithFee;
		return this;
	}

	public double getInitialPerHourPriceWithFee() {
		return initialPerHourPriceWithFee;
	}

	public PaymentSummary setInitialPerHourPriceWithFee(double initialPerHourPriceWithFee) {
		this.initialPerHourPriceWithFee = initialPerHourPriceWithFee;
		return this;
	}

	public double getAdditionalPerHourPriceWithFee() {
		return additionalPerHourPriceWithFee;
	}

	public PaymentSummary setAdditionalPerHourPriceWithFee(double additionalPerHourPriceWithFee) {
		this.additionalPerHourPriceWithFee = additionalPerHourPriceWithFee;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof PaymentSummary)
			return this.equals((PaymentSummary) that);
		return false;
	}

	private boolean equals(PaymentSummary that) {
		if (that == null)
			return false;

		boolean this_present_maxSpendLimit = true;
		boolean that_present_maxSpendLimit = true;
		if (this_present_maxSpendLimit || that_present_maxSpendLimit) {
			if (!(this_present_maxSpendLimit && that_present_maxSpendLimit))
				return false;
			if (this.maxSpendLimit != that.maxSpendLimit)
				return false;
		}

		boolean this_present_actualSpendLimit = true;
		boolean that_present_actualSpendLimit = true;
		if (this_present_actualSpendLimit || that_present_actualSpendLimit) {
			if (!(this_present_actualSpendLimit && that_present_actualSpendLimit))
				return false;
			if (this.actualSpendLimit != that.actualSpendLimit)
				return false;
		}

		boolean this_present_buyerFee = true;
		boolean that_present_buyerFee = true;
		if (this_present_buyerFee || that_present_buyerFee) {
			if (!(this_present_buyerFee && that_present_buyerFee))
				return false;
			if (this.buyerFee != that.buyerFee)
				return false;
		}

		boolean this_present_buyerFeePercentage = true;
		boolean that_present_buyerFeePercentage = true;
		if (this_present_buyerFeePercentage || that_present_buyerFeePercentage) {
			if (!(this_present_buyerFeePercentage && that_present_buyerFeePercentage))
				return false;
			if (this.buyerFeePercentage != that.buyerFeePercentage)
				return false;
		}

		boolean this_present_buyerFeeBand = true;
		boolean that_present_buyerFeeBand = true;
		if (this_present_buyerFeeBand || that_present_buyerFeeBand) {
			if (!(this_present_buyerFeeBand && that_present_buyerFeeBand))
				return false;
			if (this.buyerFeeBand != that.buyerFeeBand)
				return false;
		}

		boolean this_present_totalCost = true;
		boolean that_present_totalCost = true;
		if (this_present_totalCost || that_present_totalCost) {
			if (!(this_present_totalCost && that_present_totalCost))
				return false;
			if (this.totalCost != that.totalCost)
				return false;
		}

		boolean this_present_hoursWorked = true;
		boolean that_present_hoursWorked = true;
		if (this_present_hoursWorked || that_present_hoursWorked) {
			if (!(this_present_hoursWorked && that_present_hoursWorked))
				return false;
			if (this.hoursWorked != that.hoursWorked)
				return false;
		}

		boolean this_present_unitsProcessed = true;
		boolean that_present_unitsProcessed = true;
		if (this_present_unitsProcessed || that_present_unitsProcessed) {
			if (!(this_present_unitsProcessed && that_present_unitsProcessed))
				return false;
			if (this.unitsProcessed != that.unitsProcessed)
				return false;
		}

		boolean this_present_salesTaxCollectedFlag = true;
		boolean that_present_salesTaxCollectedFlag = true;
		if (this_present_salesTaxCollectedFlag || that_present_salesTaxCollectedFlag) {
			if (!(this_present_salesTaxCollectedFlag && that_present_salesTaxCollectedFlag))
				return false;
			if (this.salesTaxCollectedFlag != that.salesTaxCollectedFlag)
				return false;
		}

		boolean this_present_salesTaxCollected = true;
		boolean that_present_salesTaxCollected = true;
		if (this_present_salesTaxCollected || that_present_salesTaxCollected) {
			if (!(this_present_salesTaxCollected && that_present_salesTaxCollected))
				return false;
			if (this.salesTaxCollected != that.salesTaxCollected)
				return false;
		}

		boolean this_present_salesTaxRate = true;
		boolean that_present_salesTaxRate = true;
		if (this_present_salesTaxRate || that_present_salesTaxRate) {
			if (!(this_present_salesTaxRate && that_present_salesTaxRate))
				return false;
			if (this.salesTaxRate != that.salesTaxRate)
				return false;
		}

		boolean this_present_paidOn = true;
		boolean that_present_paidOn = true;
		if (this_present_paidOn || that_present_paidOn) {
			if (!(this_present_paidOn && that_present_paidOn))
				return false;
			if (this.paidOn != that.paidOn)
				return false;
		}

		boolean this_present_paymentDueOn = true;
		boolean that_present_paymentDueOn = true;
		if (this_present_paymentDueOn || that_present_paymentDueOn) {
			if (!(this_present_paymentDueOn && that_present_paymentDueOn))
				return false;
			if (this.paymentDueOn != that.paymentDueOn)
				return false;
		}

		boolean this_present_legacyBuyerFee = true;
		boolean that_present_legacyBuyerFee = true;
		if (this_present_legacyBuyerFee || that_present_legacyBuyerFee) {
			if (!(this_present_legacyBuyerFee && that_present_legacyBuyerFee))
				return false;
			if (this.legacyBuyerFee != that.legacyBuyerFee)
				return false;
		}

		boolean this_present_additionalExpenses = true;
		boolean that_present_additionalExpenses = true;
		if (this_present_additionalExpenses || that_present_additionalExpenses) {
			if (!(this_present_additionalExpenses && that_present_additionalExpenses))
				return false;
			if (this.additionalExpenses != that.additionalExpenses)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_maxSpendLimit = true;
		builder.append(present_maxSpendLimit);
		if (present_maxSpendLimit)
			builder.append(maxSpendLimit);

		boolean present_actualSpendLimit = true;
		builder.append(present_actualSpendLimit);
		if (present_actualSpendLimit)
			builder.append(actualSpendLimit);

		boolean present_buyerFee = true;
		builder.append(present_buyerFee);
		if (present_buyerFee)
			builder.append(buyerFee);

		boolean present_buyerFeePercentage = true;
		builder.append(present_buyerFeePercentage);
		if (present_buyerFeePercentage)
			builder.append(buyerFeePercentage);

		boolean present_buyerFeeBand = true;
		builder.append(present_buyerFeeBand);
		if (present_buyerFeeBand)
			builder.append(buyerFeeBand);

		boolean present_totalCost = true;
		builder.append(present_totalCost);
		if (present_totalCost)
			builder.append(totalCost);

		boolean present_hoursWorked = true;
		builder.append(present_hoursWorked);
		if (present_hoursWorked)
			builder.append(hoursWorked);

		boolean present_unitsProcessed = true;
		builder.append(present_unitsProcessed);
		if (present_unitsProcessed)
			builder.append(unitsProcessed);

		boolean present_salesTaxCollectedFlag = true;
		builder.append(present_salesTaxCollectedFlag);
		if (present_salesTaxCollectedFlag)
			builder.append(salesTaxCollectedFlag);

		boolean present_salesTaxCollected = true;
		builder.append(present_salesTaxCollected);
		if (present_salesTaxCollected)
			builder.append(salesTaxCollected);

		boolean present_salesTaxRate = true;
		builder.append(present_salesTaxRate);
		if (present_salesTaxRate)
			builder.append(salesTaxRate);

		boolean present_paidOn = true;
		builder.append(present_paidOn);
		if (present_paidOn)
			builder.append(paidOn);

		boolean present_paymentDueOn = true;
		builder.append(present_paymentDueOn);
		if (present_paymentDueOn)
			builder.append(paymentDueOn);

		boolean present_legacyBuyerFee = true;
		builder.append(present_legacyBuyerFee);
		if (present_legacyBuyerFee)
			builder.append(legacyBuyerFee);

		boolean present_additionalExpenses = true;
		builder.append(present_additionalExpenses);
		if (present_additionalExpenses)
			builder.append(additionalExpenses);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PaymentSummary(");
		boolean first = true;

		sb.append("maxSpendLimit:");
		sb.append(this.maxSpendLimit);
		first = false;
		if (!first) sb.append(", ");
		sb.append("actualSpendLimit:");
		sb.append(this.actualSpendLimit);
		first = false;
		if (!first) sb.append(", ");
		sb.append("buyerFee:");
		sb.append(this.buyerFee);
		first = false;
		if (!first) sb.append(", ");
		sb.append("buyerFeePercentage:");
		sb.append(this.buyerFeePercentage);
		first = false;
		if (!first) sb.append(", ");
		sb.append("buyerFeeBand:");
		sb.append(this.buyerFeeBand);
		first = false;
		if (!first) sb.append(", ");
		sb.append("totalCost:");
		sb.append(this.totalCost);
		first = false;
		if (!first) sb.append(", ");
		sb.append("hoursWorked:");
		sb.append(this.hoursWorked);
		first = false;
		if (!first) sb.append(", ");
		sb.append("unitsProcessed:");
		sb.append(this.unitsProcessed);
		first = false;
		if (!first) sb.append(", ");
		sb.append("salesTaxCollectedFlag:");
		sb.append(this.salesTaxCollectedFlag);
		first = false;
		if (!first) sb.append(", ");
		sb.append("salesTaxCollected:");
		sb.append(this.salesTaxCollected);
		first = false;
		if (!first) sb.append(", ");
		sb.append("salesTaxRate:");
		sb.append(this.salesTaxRate);
		first = false;
		if (!first) sb.append(", ");
		sb.append("paidOn:");
		sb.append(this.paidOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("paymentDueOn:");
		sb.append(this.paymentDueOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("legacyBuyerFee:");
		sb.append(this.legacyBuyerFee);
		first = false;
		if (!first) sb.append(", ");
		sb.append("additionalExpenses:");
		sb.append(this.additionalExpenses);
		first = false;
		sb.append(")");
		return sb.toString();
	}

}