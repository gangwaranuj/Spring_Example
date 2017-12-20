package com.workmarket.domains.model.acl;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "userRole")
@Table(name = "user_acl_role")
@AuditChanges
public class UserAclRoleAssociation extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private User user;
	private AclRole role;

	public UserAclRoleAssociation() {
	}

	public UserAclRoleAssociation(User user, AclRole role) {
		this.user = user;
		this.role = role;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "acl_role_id")
	public AclRole getRole() {
		return role;
	}

	public void setRole(AclRole role) {
		this.role = role;
	}
}
