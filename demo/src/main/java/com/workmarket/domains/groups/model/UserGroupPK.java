package com.workmarket.domains.groups.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserGroupPK implements Serializable {

	private Long userGroupId;

	public UserGroupPK() {
	}

	public UserGroupPK(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	@Column(name = "user_group_id", nullable = false, length = 11)
	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserGroupPK)) return false;

		UserGroupPK that = (UserGroupPK) o;

		if (userGroupId != null ? !userGroupId.equals(that.userGroupId) : that.userGroupId != null) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return userGroupId != null ? userGroupId.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "UserGroupEvaluationSchedulePK{" +
			"userGroupId=" + userGroupId +
			"}";
	}
	

}
