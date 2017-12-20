package com.workmarket.domains.groups.model;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class UserGroupEvaluationScheduledRunPK implements Serializable {

	private UserGroup userGroup;
	private ScheduledRun scheduledRun;

	public UserGroupEvaluationScheduledRunPK() { }

	public UserGroupEvaluationScheduledRunPK(UserGroup userGroup, ScheduledRun scheduledRun) {
		this.userGroup = userGroup;
		this.scheduledRun = scheduledRun;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_group_id", updatable = false)
	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "scheduled_run_id")
	public ScheduledRun getScheduledRun() {
		return scheduledRun;
	}

	public void setScheduledRun(ScheduledRun scheduledRun) {
		this.scheduledRun = scheduledRun;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final UserGroupEvaluationScheduledRunPK that = (UserGroupEvaluationScheduledRunPK) o;

		return userGroup != null ? userGroup.equals(that.userGroup) : that.userGroup == null &&
			(scheduledRun != null ? scheduledRun.equals(that.scheduledRun) : that.scheduledRun == null);

	}

	@Override
	public int hashCode() {
		int result = userGroup != null ? userGroup.hashCode() : 0;
		result = 31 * result + (scheduledRun != null ? scheduledRun.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "UserGroupEvaluationScheduledRunPK{" +
			"userGroup=" + userGroup +
			", scheduledRun=" + scheduledRun +
			'}';
	}
}
