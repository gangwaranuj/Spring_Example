package com.workmarket.domains.model.invoice;

import com.google.common.collect.Ordering;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity(name = "invoice")
@DiscriminatorValue(Invoice.INVOICE_TYPE)
@AuditChanges
public class Invoice extends AbstractInvoice {

	private static final long serialVersionUID = 1651688809192633480L;

	private String oldInvoiceNumber;
	private Long activeWorkResourceId;
	private boolean bundled = false;
	private BigDecimal workPrice;
	private BigDecimal workBuyerFee;
	private Calendar fastFundedOn;
	private Long cancelPaymentWorkId;

	public static final String INVOICE_TYPE = "invoice";

	public static final Ordering<Invoice> ORDERING_BY_DUE_DATE = new Ordering<Invoice>() {
		@Override public int compare(@Nullable Invoice one, @Nullable Invoice two) {

			if (one == null ^ two == null) return (one == null) ? -1 : 1;
			if (one == null && two == null) return 0;
			Calendar oneDueDate = one.getDueDate();
			Calendar twoDueDate = two.getDueDate();
			if (oneDueDate == null ^ twoDueDate == null) return (oneDueDate == null) ? -1 : 1;
			if (oneDueDate == null && twoDueDate == null) return 0;

			return oneDueDate.compareTo(twoDueDate);
		}
	};

	public Invoice() {
		super();
	}

	@Column(name = "old_invoice_number", length=50, updatable = false)
	public String getOldInvoiceNumber() {
		return oldInvoiceNumber;
	}

	public void setOldInvoiceNumber(String oldInvoiceNumber) {
		this.oldInvoiceNumber = oldInvoiceNumber;
	}

	@Column(name = "active_work_resource_id")
	public Long getActiveWorkResourceId() {
		return activeWorkResourceId;
	}

	public void setActiveWorkResourceId(Long activeWorkResourceId) {
		this.activeWorkResourceId = activeWorkResourceId;
	}

	@Column(name = "bundled", nullable = false)
	public boolean isBundled() {
		return bundled;
	}

	public void setBundled(boolean bundled) {
		this.bundled = bundled;
	}

	@Column(name = "work_price")
	public BigDecimal getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(BigDecimal workPrice) {
		this.workPrice = workPrice;
	}

	@Column(name = "work_buyer_fee")
	public BigDecimal getWorkBuyerFee() {
		return workBuyerFee;
	}

	public void setWorkBuyerFee(BigDecimal workBuyerFee) {
		this.workBuyerFee = workBuyerFee;
	}

	@Column(name = "cancel_payment_work_id")
	public Long getCancelPaymentWorkId() {
		return cancelPaymentWorkId;
	}

	public void setCancelPaymentWorkId(Long cancelPaymentWorkId) {
		this.cancelPaymentWorkId = cancelPaymentWorkId;
	}

	@Column(name = "fast_funded_on")
	public Calendar getFastFundedOn() {
		return fastFundedOn;
	}

	public void setFastFundedOn(Calendar fastFundedOn) {
		this.fastFundedOn = fastFundedOn;
	}

	@Override
	@Transient
	public String getType() {
		return INVOICE_TYPE;
	}

	@Transient
	@Override
	public boolean isEditable() {
		return !isPaid();
	}
}
