package com.workmarket.domains.model.acl;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;


public class UserCustomPermissionAssociation extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private User user;
	private Permission permission;
	private boolean enabled = Boolean.FALSE;

	public UserCustomPermissionAssociation() {}

	public UserCustomPermissionAssociation(User user, Permission permission, Boolean enabled) {
		this.user = user;
		this.permission = permission;
		this.enabled = enabled;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
