package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("UG")
@AuditChanges
public class UserGroupChangeLog extends UserChangeLog {
	private static final long serialVersionUID = 1L;

	private UserGroup group;

	public UserGroupChangeLog() {
	}

	public UserGroupChangeLog(Long user, Long actor, Long masqueradeActor, UserGroup group) {
		super(user, actor, masqueradeActor);
		this.group = group;
	}

	public void setGroup(UserGroup group) {
		this.group = group;
	}

	@ManyToOne
	@JoinColumn(name="group_id", referencedColumnName="id")
	public UserGroup getGroup() {
		return group;
	}

}
