package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("UGMA")
@AuditChanges
public class UserGroupMembershipApprovedChangeLog extends UserGroupChangeLog {
	private static final long serialVersionUID = 1L;

	public UserGroupMembershipApprovedChangeLog() {
	}

	public UserGroupMembershipApprovedChangeLog(Long user, Long actor, Long masqueradeActor, UserGroup group) {
		super(user, actor, masqueradeActor, group);
	}

	@Transient
	@Override
	public String getDescription() {
		return "Group membership approved";
	}
}
