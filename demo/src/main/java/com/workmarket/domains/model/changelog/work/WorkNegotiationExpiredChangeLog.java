package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue(WorkChangeLog.WORK_NEGOTIATION_EXPIRED)
@AuditChanges
public class WorkNegotiationExpiredChangeLog extends WorkChangeLog {


	/**
	 *
	 */
	private static final long serialVersionUID = -4722042925427292071L;
	private WorkNegotiation negotiation;

	public WorkNegotiationExpiredChangeLog() {}
	public WorkNegotiationExpiredChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor, WorkNegotiation negotiation) {
		super(work, actor, masqueradeActor, onBehalfOfActor);
		this.negotiation = negotiation;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {})
	@JoinColumn(name = "work_negotiation_id")
	public WorkNegotiation getNegotiation() {
		return negotiation;
	}
	public void setNegotiation(WorkNegotiation negotiation) {
		this.negotiation = negotiation;
	}

	@Transient
	public static String getDescription() {
		return "Counteroffer expired";
	}
}
