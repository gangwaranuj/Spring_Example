package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("US")
@AuditChanges
public class UserStatusChangeLog extends UserChangeLog {
	private static final long serialVersionUID = 1L;

	private UserStatusType oldStatus;
	private UserStatusType newStatus;

	public UserStatusChangeLog() {
	}

	public UserStatusChangeLog(Long user, Long actor, Long masqueradeActor, UserStatusType oldStatus, UserStatusType newStatus) {
		super(user, actor, masqueradeActor);
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}

	@ManyToOne
	@JoinColumn(name = "old_user_status_type_code", referencedColumnName = "code", nullable = true)
	public UserStatusType getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(UserStatusType oldStatus) {
		this.oldStatus = oldStatus;
	}

	@ManyToOne
	@JoinColumn(name = "new_user_status_type_code", referencedColumnName = "code", nullable = false)
	public UserStatusType getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(UserStatusType newStatus) {
		this.newStatus = newStatus;
	}

	@Transient
	@Override
	public String getDescription() {
		return "Status changed";
	}
}
