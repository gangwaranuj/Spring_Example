package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "subscriptionInvoice")
@DiscriminatorValue(SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE)
@AuditChanges
public class SubscriptionInvoice extends AbstractServiceInvoice {

	private static final long serialVersionUID = 1L;
	public static final String SUBSCRIPTION_INVOICE_TYPE = "subscription";

	private String clientRefId;

	public SubscriptionInvoice() {
		super();
		this.setSubscriptionInvoiceType(new SubscriptionInvoiceType(SubscriptionInvoiceType.REGULAR));
	}

	public SubscriptionInvoice(Company company) {
		super(company);
		this.setSubscriptionInvoiceType(new SubscriptionInvoiceType(SubscriptionInvoiceType.REGULAR));
	}

	@Override
	@Transient
	public String getType() {
		return SUBSCRIPTION_INVOICE_TYPE;
	}

	@Transient
	public String getClientRefId() {
		return clientRefId;
	}

	public void setClientRefId(String clientRefId) {
		this.clientRefId = clientRefId;
	}
}
