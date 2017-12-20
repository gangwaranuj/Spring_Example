package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@AuditChanges
@Entity(name = "userGroupEvaluationScheduledRun")
@Table(name = "user_group_evaluation_scheduled_run")
public class UserGroupEvaluationScheduledRun {

	private UserGroupEvaluationScheduledRunPK userGroupEvaluationScheduledRunPK;

	public UserGroupEvaluationScheduledRun() { }

	public UserGroupEvaluationScheduledRun(final UserGroupEvaluationScheduledRunPK userGroupEvaluationScheduledRunPK) {
		this.userGroupEvaluationScheduledRunPK = userGroupEvaluationScheduledRunPK;
	}

	public UserGroupEvaluationScheduledRun(UserGroup userGroup, ScheduledRun scheduledRun) {
		this.userGroupEvaluationScheduledRunPK = new UserGroupEvaluationScheduledRunPK(userGroup, scheduledRun);
	}

	@EmbeddedId
	public UserGroupEvaluationScheduledRunPK getUserGroupEvaluationScheduledRunPK() {
		return userGroupEvaluationScheduledRunPK;
	}

	public void setUserGroupEvaluationScheduledRunPK(final UserGroupEvaluationScheduledRunPK userGroupEvaluationScheduledRunPK) {
		this.userGroupEvaluationScheduledRunPK = userGroupEvaluationScheduledRunPK;
	}
}
