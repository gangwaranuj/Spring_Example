package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue(WorkChangeLog.WORK_RESCHEDULE_STATUS_CHANGE)
@AuditChanges
public class WorkRescheduleRequestedChangeLog extends WorkChangeLog {

	/**
	 *
	 */
	private static final long serialVersionUID = -5396164567112751537L;

	private WorkRescheduleNegotiation negotiation;

	public WorkRescheduleRequestedChangeLog() {}
	public WorkRescheduleRequestedChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor, WorkRescheduleNegotiation negotiation) {
		super(work, actor, masqueradeActor, onBehalfOfActor);
		this.negotiation = negotiation;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {})
	@JoinColumn(name = "work_negotiation_id")
	public WorkRescheduleNegotiation getNegotiation() {
		return negotiation;
	}
	public void setNegotiation(WorkRescheduleNegotiation negotiation) {
		this.negotiation = negotiation;
	}

	@Transient
	public static String getDescription() {
		return "Counteroffer requested";
	}
}
