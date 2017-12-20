package com.workmarket.domains.model.account;

import com.workmarket.domains.model.account.pricing.PaymentPeriod;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name = "serviceTransaction")
@Table(name = "service_transaction")
@AuditChanges
public class ServiceTransaction extends RegisterTransaction {

	private static final long serialVersionUID = 1L;
	private PaymentPeriod paymentPeriod;
	private boolean invoiced = false;
	private boolean subscriptionVendorOfRecord = false;
	private Calendar invoicedOn;
	private boolean subscriptionIncrementalTransaction = false;

	public ServiceTransaction() {
	}

	public ServiceTransaction(PaymentPeriod paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}

	@Column(name = "invoiced", nullable = false)
	public boolean isInvoiced() {
		return invoiced;
	}

	public void setInvoiced(boolean invoiced) {
		this.invoiced = invoiced;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "subscription_payment_period_id", referencedColumnName = "id", updatable = false)
	public PaymentPeriod getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(PaymentPeriod paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}

	@Column(name = "subscription_vendor_of_record")
	public boolean isSubscriptionVendorOfRecord() {
		return subscriptionVendorOfRecord;
	}

	public void setSubscriptionVendorOfRecord(boolean subscriptionVendorOfRecord) {
		this.subscriptionVendorOfRecord = subscriptionVendorOfRecord;
	}

	@Column(name = "invoiced_on")
	public Calendar getInvoicedOn() {
		return invoicedOn;
	}

	public void setInvoicedOn(Calendar invoicedOn) {
		this.invoicedOn = invoicedOn;
	}

	@Column(name = "subscription_incremental_transaction", nullable = false)
	public boolean isSubscriptionIncrementalTransaction() {
		return subscriptionIncrementalTransaction;
	}

	public void setSubscriptionIncrementalTransaction(boolean subscriptionIncrementalTransaction) {
		this.subscriptionIncrementalTransaction = subscriptionIncrementalTransaction;
	}
}
