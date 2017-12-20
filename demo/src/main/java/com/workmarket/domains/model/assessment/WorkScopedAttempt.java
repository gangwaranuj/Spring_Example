package com.workmarket.domains.model.assessment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="workScopedAssessmentAttempt")
@DiscriminatorValue(Attempt.WORK_SCOPED_ATTEMPT_TYPE)
@AuditChanges
public class WorkScopedAttempt extends Attempt {

	private static final long serialVersionUID = 1L;

	private Work work;
	private User behalfOf;

	public WorkScopedAttempt() {}
	public WorkScopedAttempt(Work work, User behalfOf) {
		this.work = work;
		this.behalfOf = behalfOf;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="work_id")
	public Work getWork() {
		return work;
	}
	public void setWork(Work work) {
		this.work = work;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="on_behalf_of")
	public User getBehalfOf() {
		return behalfOf;
	}
	public void setBehalfOf(User behalfOf) {
		this.behalfOf = behalfOf;
	}
}
