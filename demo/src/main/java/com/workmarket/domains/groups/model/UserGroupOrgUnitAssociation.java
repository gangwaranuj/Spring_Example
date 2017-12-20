package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "userGroupOrgUnitAssociation")
@Table(name = "user_group_org_unit_association")
@AuditChanges
public class UserGroupOrgUnitAssociation extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private String userGroupUuid;
	private String orgUnitUuid;

	public UserGroupOrgUnitAssociation() {
	}

	public UserGroupOrgUnitAssociation(final String userGroupUuid, final String orgUnitUuid) {
		this.userGroupUuid = userGroupUuid;
		this.orgUnitUuid = orgUnitUuid;
	}

	@Column(name = "user_group_uuid", updatable = false)
	public String getUserGroupUuid() {
		return userGroupUuid;
	}

	public void setUserGroupUuid(final String userGroupUuid) {
		this.userGroupUuid = userGroupUuid;
	}

	@Column(name = "org_unit_uuid", updatable = false)
	public String getOrgUnitUuid() {
		return orgUnitUuid;
	}

	public void setOrgUnitUuid(final String orgUnitUuid) {
		this.orgUnitUuid = orgUnitUuid;
	}
}
