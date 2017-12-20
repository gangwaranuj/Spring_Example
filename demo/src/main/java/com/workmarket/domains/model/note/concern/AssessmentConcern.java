package com.workmarket.domains.model.note.concern;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="assessmentConcern")
@DiscriminatorValue("assessment")
@AuditChanges
public class AssessmentConcern extends Concern {

	private static final long serialVersionUID = 1L;

	private AbstractAssessment assessment;

	public AssessmentConcern() {
		super();
	}

	public AssessmentConcern(String message, AbstractAssessment assessment) {
		super(message);
		this.setAssessment(assessment);
	}

	@ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="assessment_id", referencedColumnName="id")
	public AbstractAssessment getAssessment() {
		return assessment;
	}

	public void setAssessment(AbstractAssessment assessment) {
		this.assessment = assessment;
	}

	@Transient
	public String getType() {
		return "assessment";
	}

	@Override
	@Transient
	public Long getEntityId() {
		return assessment.getId();
	}

	@Override
	@Transient
	public String getEntityNumber() {
		return assessment.getId().toString();
	}
}
