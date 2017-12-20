package com.workmarket.domains.model.decisionflow;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.work.model.AbstractWork;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name = "workToDecisionFlowAssociation")
@Table(name = "work_to_decision_flow_association")
@AuditChanges
public class WorkToDecisionFlowAssociation extends DeletableEntity {
	private AbstractWork work;
	private String decisionFlowUuid;

	public WorkToDecisionFlowAssociation() {
		super();
	}

	public WorkToDecisionFlowAssociation(AbstractWork work, String decisionFlowUuid) {
		super();
		this.work = work;
		this.decisionFlowUuid = decisionFlowUuid;
	}

	@OneToOne
	@JoinColumn(name = "work_id", referencedColumnName = "id", updatable = false)
	public AbstractWork getWork() {
		return work;
	}

	public void setWork(AbstractWork work) {
		this.work = work;
	}

	@Column(name = "decision_flow_uuid", nullable = false, length = 36)
	public String getDecisionFlowUuid() {
		return decisionFlowUuid;
	}

	public void setDecisionFlowUuid(String decisionFlowUuid) {
		this.decisionFlowUuid = decisionFlowUuid;
	}
}
