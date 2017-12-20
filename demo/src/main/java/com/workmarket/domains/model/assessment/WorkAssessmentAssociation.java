package com.workmarket.domains.model.assessment;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "workAssessmentAssociation")
@Table(name = "work_assessment_association")
@AuditChanges
public class WorkAssessmentAssociation extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private AbstractWork work;
	private AbstractAssessment assessment;
	private Boolean required = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "work_id", updatable = false)
	public AbstractWork getWork() {
		return work;
	}

	public void setWork(AbstractWork work) {
		this.work = work;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "assessment_id")
	public AbstractAssessment getAssessment() {
		return assessment;
	}

	public void setAssessment(AbstractAssessment assessment) {
		this.assessment = assessment;
	}

	@Column(name = "required", nullable = false)
	public Boolean isRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}
}
