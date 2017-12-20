package com.workmarket.domains.model.assessment;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="assessmentUserAssocation")
@Table(name="assessment_user_association")
@NamedQueries({
	@NamedQuery(name="assessmentUserAssociation.byUserAndAssessment", query="from assessmentUserAssocation a join fetch a.user join fetch a.assessment where a.user.id = :user_id and a.assessment.id = :assessment_id order by a.modifiedOn desc"),
	@NamedQuery(name="assessmentUserAssociation.byUserAssessmentAndWork", query="select aua from assessmentUserAssocation aua, workScopedAssessmentAttempt wsaa join fetch aua.user join fetch aua.assessment where aua.user.id = :user_id and aua.assessment.id = :assessment_id and wsaa.assessmentUserAssociation.id = aua.id and wsaa.work.id = :work_id")
})
@AuditChanges
public class AssessmentUserAssociation extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private User user;
	private AbstractAssessment assessment;
	private List<Attempt> attempts = Lists.newArrayList();
	private Boolean reattemptAllowedFlag = false;

	private Calendar completedOn = null;
	private Calendar gradedOn = null;
	private AttemptStatusType status = new AttemptStatusType(AttemptStatusType.INPROGRESS);

	private Boolean passedFlag = false;
	private BigDecimal score = null;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="assessment_id")
	public AbstractAssessment getAssessment() {
		return assessment;
	}

	public void setAssessment(AbstractAssessment assessment) {
		this.assessment = assessment;
	}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="assessmentUserAssociation")
	@OrderBy("createdOn ASC")
	public List<Attempt> getAttempts() {
		return attempts;
	}

	public void setAttempts(List<Attempt> attempts) {
		this.attempts = attempts;
	}

	@Column(name="reattempt_allowed_flag", nullable=false, length=1)
	public Boolean getReattemptAllowedFlag() {
		return reattemptAllowedFlag;
	}

	public void setReattemptAllowedFlag(Boolean reattemptAllowedFlag) {
		this.reattemptAllowedFlag = reattemptAllowedFlag;
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

	@ManyToOne(fetch = FetchType.LAZY)
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

	@Transient
	public Boolean isComplete() {
		return status.isComplete() || status.isGraded();
	}

	@Transient
	public void snapshotAttempt(Attempt attempt) {
		setGradedOn(attempt.getGradedOn());
		setCompletedOn(attempt.getCompletedOn());
		setStatus(attempt.getStatus());
		setScore(attempt.getScore());
		setPassedFlag(attempt.getPassedFlag());
	}
}
