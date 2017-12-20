package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="deliveryStatusType")
@Table(name="delivery_status_type")
public class DeliveryStatusType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final String NONE = "none";
	public static final String SCHEDULED = "scheduled";
	public static final String SENT = "sent";
	public static final String SENT_WITH_ERRORS = "sent_error";
	public static final String FAILED = "failed";
	public static final String CANCELLED = "cancelled";
	
	public DeliveryStatusType() {}
	public DeliveryStatusType(String code) {
		super(code);
	}

	@Transient
	public boolean isNone() {
		return NONE.equals(this.getCode());
	}

	@Transient
	public boolean isScheduled() {
		return SCHEDULED.equals(this.getCode());
	}

	@Transient
	public boolean isSent() {
		return SENT.equals(this.getCode());
	}

	@Transient
	public boolean isSentWithErrors() {
		return SENT_WITH_ERRORS.equals(this.getCode());
	}

	@Transient
	public boolean isFailed() {
		return FAILED.equals(this.getCode());
	}

	@Transient
	public boolean isCanceled() {
		return CANCELLED.equals(this.getCode());
	}
}