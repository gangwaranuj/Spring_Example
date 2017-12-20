package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "paymentFulfillmentStatusType")
@Table(name = "payment_fulfillment_status_type")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(length = 20)) })
public class PaymentFulfillmentStatusType extends LookupEntity {

	private static final long serialVersionUID = 9191188533406291665L;

	public static final String PENDING_FULFILLMENT = "pendingFulfillment";
	public static final String FULFILLED = "fulfilled";
	public static final String FULFILLMENT_NOT_APPLICABLE = "notApplicable";

	public PaymentFulfillmentStatusType() {
		super();
	}

	public PaymentFulfillmentStatusType(String code) {
		super(code);
	}
}
