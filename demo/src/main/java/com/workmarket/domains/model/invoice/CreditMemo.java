package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity(name = "creditMemo")
@DiscriminatorValue(CreditMemo.CREDIT_MEMO_TYPE)
@AuditChanges
public class CreditMemo extends AbstractServiceInvoice {

	private static final long serialVersionUID = 1017390094066321852L;
	public static final String CREDIT_MEMO_TYPE = "creditMemo";

	private CreditMemoAudit creditMemoAudit;

	public CreditMemo() {
		super();
		this.setSubscriptionInvoiceType(new SubscriptionInvoiceType(SubscriptionInvoiceType.CREDIT_MEMO));
	}

	public CreditMemo(final AbstractServiceInvoice invoice) {
		super();
		this.setCompany(invoice.getCompany());
		this.setBalance(invoice.getBalance());
		setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLMENT_NOT_APPLICABLE));
		this.setDescription("Credit Memo for Invoice: " + invoice.getInvoiceNumber());
		this.setSubscriptionInvoiceType(new SubscriptionInvoiceType(SubscriptionInvoiceType.CREDIT_MEMO));
	}

	@OneToOne(cascade= CascadeType.ALL, mappedBy="creditMemo")
	public CreditMemoAudit getCreditMemoAudit() {
		return creditMemoAudit;
	}

	public CreditMemo setCreditMemoAudit(CreditMemoAudit creditMemoAudit) {
		this.creditMemoAudit = creditMemoAudit;
		return this;
	}

	@Override
	@Transient
	public String getType() {
		return CREDIT_MEMO_TYPE;
	}
}
