package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("UATG")
@AuditChanges
public class UserAppliedToGroupChangeLog extends UserGroupChangeLog {
	private static final long serialVersionUID = 1L;

	public UserAppliedToGroupChangeLog() {
	}

	public UserAppliedToGroupChangeLog(Long user, Long actor, Long masqueradeActor, UserGroup group) {
		super(user, actor, masqueradeActor, group);
	}

	@Transient
	@Override
	public String getDescription() {
		return "Applied to a group";
	}
}
