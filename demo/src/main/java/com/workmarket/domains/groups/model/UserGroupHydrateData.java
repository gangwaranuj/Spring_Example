package com.workmarket.domains.groups.model;

import java.io.Serializable;

public class UserGroupHydrateData implements Serializable {

	private static final long serialVersionUID = 3649228449899557531L;
	private Long companyId;
	private Long groupId;
	private Boolean openMembership;
	private String groupName;
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Boolean getOpenMembership() {
		return openMembership;
	}
	public void setOpenMembership(Boolean openMembership) {
		this.openMembership = openMembership;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((openMembership == null) ? 0 : openMembership.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserGroupHydrateData other = (UserGroupHydrateData) obj;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (openMembership == null) {
			if (other.openMembership != null)
				return false;
		} else if (!openMembership.equals(other.openMembership))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "UserGroupHydrateData [companyId=" + companyId + ", groupId=" + groupId + ", openMembership=" + openMembership + ", groupName=" + groupName + "]";
	}

}
