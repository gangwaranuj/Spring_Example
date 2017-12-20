package com.workmarket.domains.model.summary.user;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

/**
 * Author: rocio
 */
@Entity(name = "userSummary")
@Table(name = "user_summary")
@AuditChanges
public class UserSummary extends AbstractEntity {

	private Long userId;
	private Calendar lastAssignedWorkDate;

	public UserSummary() {
	}

	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "last_assigned_work_date", nullable = true)
	public Calendar getLastAssignedWorkDate() {
		return lastAssignedWorkDate;
	}

	public void setLastAssignedWorkDate(Calendar lastAssignedWorkDate) {
		this.lastAssignedWorkDate = lastAssignedWorkDate;
	}
}
