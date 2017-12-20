package com.workmarket.domains.model.request;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="requestAssessmentInvitation")
@Table(name="request_assessment_invitation")
@AuditChanges
public class AssessmentInvitation extends Request {


	private static final long serialVersionUID = 1L;

	private AbstractAssessment assessment;

	public AssessmentInvitation() {}

	public AssessmentInvitation(User requestor, User invitedUser, AbstractAssessment assessment) {
		super(requestor, invitedUser);
		this.assessment = assessment;
	}

	public AssessmentInvitation(User requestor, Invitation invitation, AbstractAssessment assessment) {
		super(requestor, invitation);
		this.assessment = assessment;
	}

	@ManyToOne
	@JoinColumn(name = "assessment_id")
	public AbstractAssessment getAssessment() {
		return assessment;
	}

	public void setAssessment(AbstractAssessment assessment) {
		this.assessment = assessment;
	}
}
