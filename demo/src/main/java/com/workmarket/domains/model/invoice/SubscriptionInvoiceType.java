package com.workmarket.domains.model.invoice;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.LookupEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Entity(name="subscriptionInvoiceType")
@Table(name="subscription_invoice_type")
@AttributeOverrides({@AttributeOverride(name="code", column=@Column(length=15))})
public class SubscriptionInvoiceType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	@Deprecated
	public static final String AD_HOC = "adhoc";
	public static final String CANCELLATION = "cancellation";
	public static final String FUTURE = "future";
	public static final String INCREMENTAL = "incremental"; // tier bust
	public static final String INCREMENTAL_FUTURE = "incrementFuture"; // tier bust
	public static final String OVERAGE = "overage"; // tier bust
	public static final String REGULAR = "regular";
	public static final String CREDIT_MEMO = "creditmemo";
	public static final String RAMP = "ramp"; // for invoicing outside of a subscription plan
	public static final String PILOT = "pilot"; // for invoicing outside of a subscription plan

	public static final List<String> ADHOC_SUBSCRIPTION_INVOICE_TYPE_CODES = Lists.newArrayList(OVERAGE, REGULAR, INCREMENTAL, RAMP, PILOT);
	public static final List<String> NO_PLAN_ADHOC_SUBSCRIPTION_INVOICE_TYPE_CODES = Lists.newArrayList(RAMP, PILOT);

	public SubscriptionInvoiceType() {
		super();
	}

	public SubscriptionInvoiceType(String code) {
		super(code);
	}

	public static SubscriptionInvoiceType newSubscriptionInvoiceType(String code) {
		return new SubscriptionInvoiceType(code);
	}

	@Transient
	public boolean isCancellationInvoice() {
		return CANCELLATION.equals(getCode());
	}
}
