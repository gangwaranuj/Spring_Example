package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("UAR")
@AuditChanges
public abstract class UserAclRoleChangeLog extends UserChangeLog {
	private static final long serialVersionUID = 1L;

	@NotNull
	private AclRole aclRole;

	public UserAclRoleChangeLog() {
	}

	public UserAclRoleChangeLog(AclRole aclRole) {
		this.aclRole = aclRole;
	}

	public UserAclRoleChangeLog(Long user, Long actor, Long masqueradeActor, AclRole aclRole) {
		super(user, actor, masqueradeActor);
		this.aclRole = aclRole;
	}

	@ManyToOne(cascade = {}, optional = false)
	@JoinColumn(name = "acl_role_id", nullable = false, unique = false)
	public AclRole getAclRole() {
		return aclRole;
	}

	public void setAclRole(AclRole aclRole) {
		this.aclRole = aclRole;
	}
}
