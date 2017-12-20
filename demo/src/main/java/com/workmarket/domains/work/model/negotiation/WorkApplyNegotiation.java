package com.workmarket.domains.work.model.negotiation;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name="workApplyNegotiation")
@DiscriminatorValue(WorkNegotiation.APPLY)
@AuditChanges
public class WorkApplyNegotiation extends WorkNegotiation {
	@Transient
	public boolean hasNegotiations() {
		return isPriceNegotiation() || isScheduleNegotiation();
	}

	@Transient
	public String getNegotiationType() {
		return APPLY;
	}
}
