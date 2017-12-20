package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("UCR")
@AuditChanges
public class UserCreatedChangeLog extends UserChangeLog {
	private static final long serialVersionUID = 1L;

	public UserCreatedChangeLog() {
	}

	public UserCreatedChangeLog(Long user, Long actor, Long masqueradeActor) {
		super(user, actor, masqueradeActor);
	}

	@Transient
	@Override
	public String getDescription() {
		return "User created";
	}
}
