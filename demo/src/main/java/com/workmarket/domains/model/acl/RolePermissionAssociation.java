package com.workmarket.domains.model.acl;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

import com.workmarket.domains.model.AbstractEntity;

@Entity(name = "rolePermission")
@Table(name = "role_permission")
@NamedQueries({ 
})
public class RolePermissionAssociation extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private Permission permission;
	private AclRole role;

	public RolePermissionAssociation() {
	}

	public RolePermissionAssociation(Permission permission, AclRole role) {
		this.permission = permission;
		this.role = role;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "permission_code")
	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
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
