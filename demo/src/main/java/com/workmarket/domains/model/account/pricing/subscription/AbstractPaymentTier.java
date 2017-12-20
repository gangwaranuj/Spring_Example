package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.utility.NumberUtilities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Author: rocio
 */
@MappedSuperclass
public abstract class AbstractPaymentTier extends AuditedEntity {

	private static final long serialVersionUID = 1L;
	public static final BigDecimal MAXIMUM = BigDecimal.valueOf(100000000L);

	private BigDecimal minimum = BigDecimal.ZERO;
	private BigDecimal maximum = SubscriptionPaymentTier.MAXIMUM;
	private BigDecimal paymentAmount = BigDecimal.ZERO;
	private BigDecimal vendorOfRecordAmount = BigDecimal.ZERO;

	@Column(name = "maximum", nullable = false)
	public BigDecimal getMaximum() {
		return maximum;
	}

	public void setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
	}

	@Column(name = "minimum", nullable = false)
	public BigDecimal getMinimum() {
		return minimum;
	}

	public void setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
	}

	@Column(name = "payment_amount", nullable = false)
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	@Column(name = "vendor_of_record_amount", nullable = false)
	public BigDecimal getVendorOfRecordAmount() {
		return vendorOfRecordAmount;
	}

	public void setVendorOfRecordAmount(BigDecimal vendorOfRecordAmount) {
		this.vendorOfRecordAmount = vendorOfRecordAmount;
	}

	@Transient
	public BigDecimal calculatePercentReached(BigDecimal amount) {
		// % = (throughput - minimum) / (maximum - minimum)
		if (NumberUtilities.isPositive(amount) && NumberUtilities.isPositive(maximum)) {
			(amount.subtract(minimum)).divide(maximum.subtract(minimum), MathContext.DECIMAL32).multiply(BigDecimal.valueOf(100));
		}
		return BigDecimal.ZERO;
	}

}
