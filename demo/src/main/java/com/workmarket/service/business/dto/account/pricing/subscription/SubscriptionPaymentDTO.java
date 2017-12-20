package com.workmarket.service.business.dto.account.pricing.subscription;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Author: rocio
 */
public class SubscriptionPaymentDTO {

	private BigDecimal softwareFeeAmount = BigDecimal.ZERO;
	private BigDecimal addOnsAmount = BigDecimal.ZERO;
	private BigDecimal vorFeeAmount = BigDecimal.ZERO;
	private BigDecimal discount = BigDecimal.ZERO;
	private BigDecimal setupFee = BigDecimal.ZERO;
	private boolean vendorOfRecord = false;
	private Long subscriptionFeeConfigurationId;
	private Long subscriptionPaymentTierSWId;
	private Long subscriptionPaymentTierVORId;

	private Calendar dueDate;

	public BigDecimal getAddOnsAmount() {
		return addOnsAmount;
	}

	public SubscriptionPaymentDTO setAddOnsAmount(BigDecimal addOnsAmount) {
		this.addOnsAmount = addOnsAmount;
		return this;
	}

	public BigDecimal getDiscount() {
		if (discount != null) {
			return discount.negate();
		}
		return discount;
	}

	public SubscriptionPaymentDTO setDiscount(BigDecimal discount) {
		this.discount = discount;
		return this;
	}

	public BigDecimal getSoftwareFeeAmount() {
		return softwareFeeAmount;
	}

	public SubscriptionPaymentDTO setSoftwareFeeAmount(BigDecimal softwareFeeAmount) {
		this.softwareFeeAmount = softwareFeeAmount;
		return this;
	}

	public BigDecimal getVorFeeAmount() {
		return vorFeeAmount;
	}

	public SubscriptionPaymentDTO setVorFeeAmount(BigDecimal vorFeeAmount) {
		this.vorFeeAmount = vorFeeAmount;
		return this;
	}

	public Calendar getDueDate() {
		return dueDate;
	}

	public SubscriptionPaymentDTO setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public BigDecimal getSetupFee() {
		return setupFee;
	}

	public SubscriptionPaymentDTO setSetupFee(BigDecimal setupFee) {
		this.setupFee = setupFee;
		return this;
	}

	public boolean isVendorOfRecord() {
		return vendorOfRecord;
	}

	public SubscriptionPaymentDTO setVendorOfRecord(boolean vendorOfRecord) {
		this.vendorOfRecord = vendorOfRecord;
		return this;
	}

	public boolean hasSoftwareFee() {
		if (softwareFeeAmount == null) {
			return false;
		}
		return softwareFeeAmount.compareTo(BigDecimal.ZERO) > 0;
	}

	public boolean hasVorFee() {
		if (vorFeeAmount == null) {
			return false;
		}
		return vorFeeAmount.compareTo(BigDecimal.ZERO) > 0;
	}

	public boolean hasAddOnsFee() {
		if (addOnsAmount == null) {
			return false;
		}
		return addOnsAmount.compareTo(BigDecimal.ZERO) > 0;
	}

	public boolean hasDiscount() {
		if (discount == null) {
			return false;
		}
		return discount.compareTo(BigDecimal.ZERO) != 0;
	}

	public boolean hasSetupFee() {
		if (setupFee == null) {
			return false;
		}
		return setupFee.compareTo(BigDecimal.ZERO) > 0;
	}

	public Long getSubscriptionFeeConfigurationId() {
		return subscriptionFeeConfigurationId;
	}

	public void setSubscriptionFeeConfigurationId(Long subscriptionFeeConfigurationId) {
		this.subscriptionFeeConfigurationId = subscriptionFeeConfigurationId;
	}

	public Long getSubscriptionPaymentTierSWId() {
		return subscriptionPaymentTierSWId;
	}

	public void setSubscriptionPaymentTierSWId(Long subscriptionPaymentTierSWId) {
		this.subscriptionPaymentTierSWId = subscriptionPaymentTierSWId;
	}

	public Long getSubscriptionPaymentTierVORId() {
		return subscriptionPaymentTierVORId;
	}

	public void setSubscriptionPaymentTierVORId(Long subscriptionPaymentTierVORId) {
		this.subscriptionPaymentTierVORId = subscriptionPaymentTierVORId;
	}

	public boolean hasValue() {
		return hasSoftwareFee() || hasVorFee() || hasAddOnsFee() || hasSetupFee();
	}

	public BigDecimal getTotal() {
		BigDecimal total = BigDecimal.ZERO;
		if (!hasValue()) {
			return total;
		}
		if (hasAddOnsFee()) {
			total = total.add(addOnsAmount);
		}
		if (hasSetupFee()) {
			total = total.add(setupFee);
		}
		if (hasSoftwareFee()) {
			total = total.add(softwareFeeAmount);
		}
		if (hasVorFee()) {
			total = total.add(vorFeeAmount);
		}
		if (hasDiscount()) {
			total = total.subtract(discount);
		}
		return total;
	}

	@Override
	public String toString() {
		return "SubscriptionPaymentDTO{" +
				"softwareFeeAmount=" + softwareFeeAmount +
				", addOnsAmount=" + addOnsAmount +
				", vorFeeAmount=" + vorFeeAmount +
				", discount=" + discount +
				", setupFee=" + setupFee +
				'}';
	}
}
