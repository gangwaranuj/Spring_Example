package com.workmarket.domains.model.invoice;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.PaymentPeriod;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Set;

@Entity(name = "serviceInvoice")
@DiscriminatorValue(AbstractServiceInvoice.SERVICE_INVOICE_TYPE)
@AuditChanges
public abstract class AbstractServiceInvoice extends AbstractInvoice {

	private static final long serialVersionUID = 1L;
	public static final String SERVICE_INVOICE_TYPE = "service";

	private Set<InvoiceLineItem> invoiceLineItems = Sets.newHashSet();
	private PaymentPeriod paymentPeriod;
	private SubscriptionInvoiceType subscriptionInvoiceType;

	public AbstractServiceInvoice() {
		super();
		setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLMENT_NOT_APPLICABLE));
	}

	public AbstractServiceInvoice(Company company) {
		super();
		setCompany(company);
		setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLMENT_NOT_APPLICABLE));
	}

	@Fetch(FetchMode.JOIN)
	@OneToMany
	@JoinColumn(name = "invoice_id")
	@Where(clause = "deleted = 0")
	public Set<InvoiceLineItem> getInvoiceLineItems() {
		return invoiceLineItems;
	}

	public void setInvoiceLineItems(Set<InvoiceLineItem> invoiceLineItems) {
		this.invoiceLineItems = invoiceLineItems;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_period_id", referencedColumnName = "id")
	public PaymentPeriod getPaymentPeriod() {
		return paymentPeriod;
	}

	public AbstractServiceInvoice setPaymentPeriod(PaymentPeriod paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
		return this;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name="subscription_invoice_type_code", referencedColumnName="code")
	public SubscriptionInvoiceType getSubscriptionInvoiceType() {
		return subscriptionInvoiceType;
	}

	public void setSubscriptionInvoiceType(SubscriptionInvoiceType subscriptionInvoiceType) {
		this.subscriptionInvoiceType = subscriptionInvoiceType;
	}

	@Override
	@Transient
	public String getType() {
		return SERVICE_INVOICE_TYPE;
	}
}
