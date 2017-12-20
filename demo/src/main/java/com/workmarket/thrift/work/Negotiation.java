package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Negotiation implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String encryptedId;
	private com.workmarket.thrift.core.Note note;
	private com.workmarket.thrift.core.User requestedBy;
	private long requestedOn;
	private com.workmarket.thrift.core.User approvedBy;
	private long approvedOn;
	private com.workmarket.thrift.core.Status approvalStatus;
	private long expiresOn;
	private boolean isExpired;
	private boolean isPriceNegotiation;
	private PricingStrategy pricing;
	private PaymentSummary payment;
	private boolean isScheduleNegotiation;
	private Schedule schedule;
	private com.workmarket.thrift.core.Status type;
	private boolean initiatedByResource;
	private double distanceToAssignment;

	public Negotiation() {
	}

	public Negotiation(
			long id,
			String encryptedId,
			com.workmarket.thrift.core.Note note,
			com.workmarket.thrift.core.User requestedBy,
			long requestedOn,
			com.workmarket.thrift.core.User approvedBy,
			long approvedOn,
			com.workmarket.thrift.core.Status approvalStatus,
			long expiresOn,
			boolean isExpired,
			boolean isPriceNegotiation,
			PricingStrategy pricing,
			PaymentSummary payment,
			boolean isScheduleNegotiation,
			Schedule schedule,
			com.workmarket.thrift.core.Status type,
			boolean initiatedByResource,
			double distanceToAssignment) {
		this();
		this.id = id;
		this.encryptedId = encryptedId;
		this.note = note;
		this.requestedBy = requestedBy;
		this.requestedOn = requestedOn;
		this.approvedBy = approvedBy;
		this.approvedOn = approvedOn;
		this.approvalStatus = approvalStatus;
		this.expiresOn = expiresOn;
		this.isExpired = isExpired;
		this.isPriceNegotiation = isPriceNegotiation;
		this.pricing = pricing;
		this.payment = payment;
		this.isScheduleNegotiation = isScheduleNegotiation;
		this.schedule = schedule;
		this.type = type;
		this.initiatedByResource = initiatedByResource;
		this.distanceToAssignment = distanceToAssignment;
	}

	public long getId() {
		return this.id;
	}

	public Negotiation setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getEncryptedId() {
		return this.encryptedId;
	}

	public Negotiation setEncryptedId(String encryptedId) {
		this.encryptedId = encryptedId;
		return this;
	}

	public boolean isSetEncryptedId() {
		return this.encryptedId != null;
	}

	public com.workmarket.thrift.core.Note getNote() {
		return this.note;
	}

	public Negotiation setNote(com.workmarket.thrift.core.Note note) {
		this.note = note;
		return this;
	}

	public boolean isSetNote() {
		return this.note != null;
	}

	public com.workmarket.thrift.core.User getRequestedBy() {
		return this.requestedBy;
	}

	public Negotiation setRequestedBy(com.workmarket.thrift.core.User requestedBy) {
		this.requestedBy = requestedBy;
		return this;
	}

	public boolean isSetRequestedBy() {
		return this.requestedBy != null;
	}

	public long getRequestedOn() {
		return this.requestedOn;
	}

	public Negotiation setRequestedOn(long requestedOn) {
		this.requestedOn = requestedOn;
		return this;
	}

	public boolean isSetRequestedOn() {
		return (requestedOn > 0L);
	}

	public com.workmarket.thrift.core.User getApprovedBy() {
		return this.approvedBy;
	}

	public Negotiation setApprovedBy(com.workmarket.thrift.core.User approvedBy) {
		this.approvedBy = approvedBy;
		return this;
	}

	public boolean isSetApprovedBy() {
		return this.approvedBy != null;
	}

	public long getApprovedOn() {
		return this.approvedOn;
	}

	public Negotiation setApprovedOn(long approvedOn) {
		this.approvedOn = approvedOn;
		return this;
	}

	public boolean isSetApprovedOn() {
		return (approvedOn > 0L);
	}

	public com.workmarket.thrift.core.Status getApprovalStatus() {
		return this.approvalStatus;
	}

	public Negotiation setApprovalStatus(com.workmarket.thrift.core.Status approvalStatus) {
		this.approvalStatus = approvalStatus;
		return this;
	}

	public boolean isSetApprovalStatus() {
		return this.approvalStatus != null;
	}

	public long getExpiresOn() {
		return this.expiresOn;
	}

	public Negotiation setExpiresOn(long expiresOn) {
		this.expiresOn = expiresOn;
		return this;
	}

	public boolean isSetExpiresOn() {
		return (expiresOn > 0L);
	}

	public boolean isIsExpired() {
		return this.isExpired;
	}

	public Negotiation setIsExpired(boolean isExpired) {
		this.isExpired = isExpired;
		return this;
	}

	public boolean isIsPriceNegotiation() {
		return this.isPriceNegotiation;
	}

	public Negotiation setIsPriceNegotiation(boolean isPriceNegotiation) {
		this.isPriceNegotiation = isPriceNegotiation;
		return this;
	}

	public PricingStrategy getPricing() {
		return this.pricing;
	}

	public Negotiation setPricing(PricingStrategy pricing) {
		this.pricing = pricing;
		return this;
	}

	public boolean isSetPricing() {
		return this.pricing != null;
	}

	public PaymentSummary getPayment() {
		return this.payment;
	}

	public Negotiation setPayment(PaymentSummary payment) {
		this.payment = payment;
		return this;
	}

	public boolean isSetPayment() {
		return this.payment != null;
	}

	public boolean isIsScheduleNegotiation() {
		return this.isScheduleNegotiation;
	}

	public Negotiation setIsScheduleNegotiation(boolean isScheduleNegotiation) {
		this.isScheduleNegotiation = isScheduleNegotiation;
		return this;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public Negotiation setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	public boolean isSetSchedule() {
		return this.schedule != null;
	}

	public com.workmarket.thrift.core.Status getType() {
		return this.type;
	}

	public Negotiation setType(com.workmarket.thrift.core.Status type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public boolean isInitiatedByResource() {
		return this.initiatedByResource;
	}

	public Negotiation setInitiatedByResource(boolean initiatedByResource) {
		this.initiatedByResource = initiatedByResource;
		return this;
	}

	public double getDistanceToAssignment() {
		return this.distanceToAssignment;
	}

	public Negotiation setDistanceToAssignment(double distanceToAssignment) {
		this.distanceToAssignment = distanceToAssignment;
		return this;
	}

	public boolean isSetDistanceToAssignment() {
		return (distanceToAssignment > 0D);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Negotiation)
			return this.equals((Negotiation) that);
		return false;
	}

	private boolean equals(Negotiation that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_encryptedId = true && this.isSetEncryptedId();
		boolean that_present_encryptedId = true && that.isSetEncryptedId();
		if (this_present_encryptedId || that_present_encryptedId) {
			if (!(this_present_encryptedId && that_present_encryptedId))
				return false;
			if (!this.encryptedId.equals(that.encryptedId))
				return false;
		}

		boolean this_present_note = true && this.isSetNote();
		boolean that_present_note = true && that.isSetNote();
		if (this_present_note || that_present_note) {
			if (!(this_present_note && that_present_note))
				return false;
			if (!this.note.equals(that.note))
				return false;
		}

		boolean this_present_requestedBy = true && this.isSetRequestedBy();
		boolean that_present_requestedBy = true && that.isSetRequestedBy();
		if (this_present_requestedBy || that_present_requestedBy) {
			if (!(this_present_requestedBy && that_present_requestedBy))
				return false;
			if (!this.requestedBy.equals(that.requestedBy))
				return false;
		}

		boolean this_present_requestedOn = true;
		boolean that_present_requestedOn = true;
		if (this_present_requestedOn || that_present_requestedOn) {
			if (!(this_present_requestedOn && that_present_requestedOn))
				return false;
			if (this.requestedOn != that.requestedOn)
				return false;
		}

		boolean this_present_approvedBy = true && this.isSetApprovedBy();
		boolean that_present_approvedBy = true && that.isSetApprovedBy();
		if (this_present_approvedBy || that_present_approvedBy) {
			if (!(this_present_approvedBy && that_present_approvedBy))
				return false;
			if (!this.approvedBy.equals(that.approvedBy))
				return false;
		}

		boolean this_present_approvedOn = true;
		boolean that_present_approvedOn = true;
		if (this_present_approvedOn || that_present_approvedOn) {
			if (!(this_present_approvedOn && that_present_approvedOn))
				return false;
			if (this.approvedOn != that.approvedOn)
				return false;
		}

		boolean this_present_approvalStatus = true && this.isSetApprovalStatus();
		boolean that_present_approvalStatus = true && that.isSetApprovalStatus();
		if (this_present_approvalStatus || that_present_approvalStatus) {
			if (!(this_present_approvalStatus && that_present_approvalStatus))
				return false;
			if (!this.approvalStatus.equals(that.approvalStatus))
				return false;
		}

		boolean this_present_expiresOn = true;
		boolean that_present_expiresOn = true;
		if (this_present_expiresOn || that_present_expiresOn) {
			if (!(this_present_expiresOn && that_present_expiresOn))
				return false;
			if (this.expiresOn != that.expiresOn)
				return false;
		}

		boolean this_present_isExpired = true;
		boolean that_present_isExpired = true;
		if (this_present_isExpired || that_present_isExpired) {
			if (!(this_present_isExpired && that_present_isExpired))
				return false;
			if (this.isExpired != that.isExpired)
				return false;
		}

		boolean this_present_isPriceNegotiation = true;
		boolean that_present_isPriceNegotiation = true;
		if (this_present_isPriceNegotiation || that_present_isPriceNegotiation) {
			if (!(this_present_isPriceNegotiation && that_present_isPriceNegotiation))
				return false;
			if (this.isPriceNegotiation != that.isPriceNegotiation)
				return false;
		}

		boolean this_present_pricing = true && this.isSetPricing();
		boolean that_present_pricing = true && that.isSetPricing();
		if (this_present_pricing || that_present_pricing) {
			if (!(this_present_pricing && that_present_pricing))
				return false;
			if (!this.pricing.equals(that.pricing))
				return false;
		}

		boolean this_present_payment = true && this.isSetPayment();
		boolean that_present_payment = true && that.isSetPayment();
		if (this_present_payment || that_present_payment) {
			if (!(this_present_payment && that_present_payment))
				return false;
			if (!this.payment.equals(that.payment))
				return false;
		}

		boolean this_present_isScheduleNegotiation = true;
		boolean that_present_isScheduleNegotiation = true;
		if (this_present_isScheduleNegotiation || that_present_isScheduleNegotiation) {
			if (!(this_present_isScheduleNegotiation && that_present_isScheduleNegotiation))
				return false;
			if (this.isScheduleNegotiation != that.isScheduleNegotiation)
				return false;
		}

		boolean this_present_schedule = true && this.isSetSchedule();
		boolean that_present_schedule = true && that.isSetSchedule();
		if (this_present_schedule || that_present_schedule) {
			if (!(this_present_schedule && that_present_schedule))
				return false;
			if (!this.schedule.equals(that.schedule))
				return false;
		}

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		boolean this_present_initiatedByResource = true;
		boolean that_present_initiatedByResource = true;
		if (this_present_initiatedByResource || that_present_initiatedByResource) {
			if (!(this_present_initiatedByResource && that_present_initiatedByResource))
				return false;
			if (this.initiatedByResource != that.initiatedByResource)
				return false;
		}

		boolean this_present_distanceToAssignment = true;
		boolean that_present_distanceToAssignment = true;
		if (this_present_distanceToAssignment || that_present_distanceToAssignment) {
			if (!(this_present_distanceToAssignment && that_present_distanceToAssignment))
				return false;
			if (this.distanceToAssignment != that.distanceToAssignment)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_encryptedId = true && (isSetEncryptedId());
		builder.append(present_encryptedId);
		if (present_encryptedId)
			builder.append(encryptedId);

		boolean present_note = true && (isSetNote());
		builder.append(present_note);
		if (present_note)
			builder.append(note);

		boolean present_requestedBy = true && (isSetRequestedBy());
		builder.append(present_requestedBy);
		if (present_requestedBy)
			builder.append(requestedBy);

		boolean present_requestedOn = true;
		builder.append(present_requestedOn);
		if (present_requestedOn)
			builder.append(requestedOn);

		boolean present_approvedBy = true && (isSetApprovedBy());
		builder.append(present_approvedBy);
		if (present_approvedBy)
			builder.append(approvedBy);

		boolean present_approvedOn = true;
		builder.append(present_approvedOn);
		if (present_approvedOn)
			builder.append(approvedOn);

		boolean present_approvalStatus = true && (isSetApprovalStatus());
		builder.append(present_approvalStatus);
		if (present_approvalStatus)
			builder.append(approvalStatus);

		boolean present_expiresOn = true;
		builder.append(present_expiresOn);
		if (present_expiresOn)
			builder.append(expiresOn);

		boolean present_isExpired = true;
		builder.append(present_isExpired);
		if (present_isExpired)
			builder.append(isExpired);

		boolean present_isPriceNegotiation = true;
		builder.append(present_isPriceNegotiation);
		if (present_isPriceNegotiation)
			builder.append(isPriceNegotiation);

		boolean present_pricing = true && (isSetPricing());
		builder.append(present_pricing);
		if (present_pricing)
			builder.append(pricing);

		boolean present_payment = true && (isSetPayment());
		builder.append(present_payment);
		if (present_payment)
			builder.append(payment);

		boolean present_isScheduleNegotiation = true;
		builder.append(present_isScheduleNegotiation);
		if (present_isScheduleNegotiation)
			builder.append(isScheduleNegotiation);

		boolean present_schedule = true && (isSetSchedule());
		builder.append(present_schedule);
		if (present_schedule)
			builder.append(schedule);

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type);

		boolean present_initiatedByResource = true;
		builder.append(present_initiatedByResource);
		if (present_initiatedByResource)
			builder.append(initiatedByResource);

		boolean present_distanceToAssignment = true;
		builder.append(present_distanceToAssignment);
		if (present_distanceToAssignment)
			builder.append(distanceToAssignment);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Negotiation(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("encryptedId:");
		if (this.encryptedId == null) {
			sb.append("null");
		} else {
			sb.append(this.encryptedId);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("note:");
		if (this.note == null) {
			sb.append("null");
		} else {
			sb.append(this.note);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("requestedBy:");
		if (this.requestedBy == null) {
			sb.append("null");
		} else {
			sb.append(this.requestedBy);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("requestedOn:");
		sb.append(this.requestedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("approvedBy:");
		if (this.approvedBy == null) {
			sb.append("null");
		} else {
			sb.append(this.approvedBy);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("approvedOn:");
		sb.append(this.approvedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("approvalStatus:");
		if (this.approvalStatus == null) {
			sb.append("null");
		} else {
			sb.append(this.approvalStatus);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("expiresOn:");
		sb.append(this.expiresOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("isExpired:");
		sb.append(this.isExpired);
		first = false;
		if (!first) sb.append(", ");
		sb.append("isPriceNegotiation:");
		sb.append(this.isPriceNegotiation);
		first = false;
		if (!first) sb.append(", ");
		sb.append("pricing:");
		if (this.pricing == null) {
			sb.append("null");
		} else {
			sb.append(this.pricing);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("payment:");
		if (this.payment == null) {
			sb.append("null");
		} else {
			sb.append(this.payment);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("isScheduleNegotiation:");
		sb.append(this.isScheduleNegotiation);
		first = false;
		if (!first) sb.append(", ");
		sb.append("schedule:");
		if (this.schedule == null) {
			sb.append("null");
		} else {
			sb.append(this.schedule);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("initiatedByResource:");
		sb.append(this.initiatedByResource);
		first = false;
		if (!first) sb.append(", ");
		sb.append("distanceToAssignment:");
		sb.append(this.distanceToAssignment);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
