package com.workmarket.domains.model.account;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "creditMemoTransaction")
@Table(name = "credit_memo_transaction")
@AuditChanges
public class CreditMemoTransaction extends RegisterTransaction {

	private static final long serialVersionUID = 1L;
	private boolean originalInvoicePaid = false;
	private boolean subscriptionVendorOfRecord = false;
	private int creditMemoType;

	@Column(name = "original_invoice_paid", nullable = false)
	public boolean isOriginalInvoicePaid() {
		return originalInvoicePaid;
	}

	public CreditMemoTransaction setOriginalInvoicePaid(boolean originalInvoicePaid) {
		this.originalInvoicePaid = originalInvoicePaid;
		return this;
	}

	@Column(name = "subscription_vendor_of_record", nullable = false)
	public boolean isSubscriptionVendorOfRecord() {
		return subscriptionVendorOfRecord;
	}

	public CreditMemoTransaction setSubscriptionVendorOfRecord(boolean subscriptionVendorOfRecord) {
		this.subscriptionVendorOfRecord = subscriptionVendorOfRecord;
		return this;
	}

	@Column(name = "credit_memo_type", nullable = false)
	public int getCreditMemoType() {
		return creditMemoType;
	}

	public CreditMemoTransaction setCreditMemoType(int creditMemoType) {
		this.creditMemoType = creditMemoType;
		return this;
	}
}
