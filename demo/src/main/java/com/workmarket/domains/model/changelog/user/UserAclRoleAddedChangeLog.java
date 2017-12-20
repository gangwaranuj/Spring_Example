package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("UARA")
@AuditChanges
public class UserAclRoleAddedChangeLog extends UserAclRoleChangeLog {
	private static final long serialVersionUID = 1L;

	public UserAclRoleAddedChangeLog() {
	}

	public UserAclRoleAddedChangeLog(AclRole aclRole) {
		super(aclRole);
	}

	public UserAclRoleAddedChangeLog(Long user, Long actor, Long masqueradeActor, AclRole aclRole) {
		super(user, actor, masqueradeActor, aclRole);
	}

	@Transient
	@Override
	public String getDescription() {
		return "ACL role added";
	}
}
