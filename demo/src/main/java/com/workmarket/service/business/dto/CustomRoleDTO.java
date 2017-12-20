package com.workmarket.service.business.dto;

import java.util.List;

import com.workmarket.domains.model.acl.Permission;

public class CustomRoleDTO extends AbstractDTO {

	private String name;
	private String description;
	private List<Permission> permissionList;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<Permission> getPermissionList() {
		return permissionList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPermissionList(List<Permission> permissionList) {
		this.permissionList = permissionList;
	}

}
