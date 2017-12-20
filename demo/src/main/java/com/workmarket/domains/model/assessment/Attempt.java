package com.workmarket.domains.model.assessment;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity(name="assessmentAttempt")
@Table(name="assessment_attempt")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(Attempt.ATTEMPT_TYPE)
@NamedQueries({})
@AuditChanges
public class Attempt extends AuditedEntity {

	public static final String ATTEMPT_TYPE = "attempt";
	public static final String WORK_SCOPED_ATTEMPT_TYPE = "workAttempt";

	private static final long serialVersionUID = 1L;

	private AssessmentUserAssociation assessmentUserAssociation;
	private Set<AttemptResponse> responses = Sets.newLinkedHashSet();

	private Calendar completedOn = null;
	private Calendar gradedOn = null;
	private AttemptStatusType status = new AttemptStatusType(AttemptStatusType.INPROGRESS);

	private Boolean passedFlag = false;
	private BigDecimal score = null;
	private Boolean allQuestionsRespondedTo = false;

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional=false)
	@JoinColumn(name="assessment_user_association_id", updatable = false)
	public AssessmentUserAssociation getAssessmentUserAssociation() {
		return assessmentUserAssociation;
	}
	public void setAssessmentUserAssociation(AssessmentUserAssociation assessmentUserAssociation) {
		this.assessmentUserAssociation = assessmentUserAssociation;
	}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="attempt")
	@Where(clause = "deleted = 0")
	public Set<AttemptResponse> getResponses() {
		return responses;
	}
	public void setResponses(Set<AttemptResponse> responses) {
		this.responses = responses;
	}

	@Column(name="completed_on", nullable=true)
	public Calendar getCompletedOn() {
		return completedOn;
	}
	public void setCompletedOn(Calendar completedOn) {
		this.completedOn = completedOn;
	}

	@Column(name="graded_on", nullable=true)
	public Calendar getGradedOn() {
		return gradedOn;
	}
	public void setGradedOn(Calendar gradedOn) {
		this.gradedOn = gradedOn;
	}

	@ManyToOne
	@JoinColumn(name = "attempt_status_type_code", referencedColumnName = "code", nullable = false)
	public AttemptStatusType getStatus() {
		return status;
	}
	public void setStatus(AttemptStatusType status) {
		this.status = status;
	}

	@Column(name="passed_flag", nullable=true)
	public Boolean getPassedFlag() {
		return passedFlag;
	}
	public void setPassedFlag(Boolean passedFlag) {
		this.passedFlag = passedFlag;
	}

	@Column(name="score", nullable=true)
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
	}

	@Column(name="all_questions_responded_to", nullable=true)
	public Boolean getAllQuestionsRespondedTo() {
		return allQuestionsRespondedTo;
	}
	public void setAllQuestionsRespondedTo(Boolean allQuestionsRespondedTo) {
		this.allQuestionsRespondedTo = allQuestionsRespondedTo;
	}

	@Transient
	public Boolean isComplete() {
		return status.isComplete() || status.isGraded();
	}

	@Transient
	public AbstractAssessment getAssessment() {
		return assessmentUserAssociation.getAssessment();
	}

	@Transient
	public User getUser() {
		return assessmentUserAssociation.getUser();
	}
}
