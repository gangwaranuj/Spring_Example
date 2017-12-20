package com.workmarket.domains.model.skill;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
public class UserGroupSkillPK implements Serializable {
	private Long userGroupId;
	private Long skillId;

	public UserGroupSkillPK() {

	}

	public UserGroupSkillPK(Long userGroupId, Long skillId) {
		this.userGroupId = userGroupId;
		this.skillId = skillId;
	}

	@Column(name = "user_group_id", nullable = false)
	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	@Column(name = "skill_id", nullable = false)
	public Long getSkillId() {
		return skillId;
	}

	public void setSkillId(Long skillId) {
		this.skillId = skillId;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final UserGroupSkillPK that = (UserGroupSkillPK) o;

		return userGroupId != null ? userGroupId.equals(that.userGroupId) : that.userGroupId == null &&
			(skillId != null ? skillId.equals(that.skillId) : that.skillId == null);

	}

	@Override
	public int hashCode() {
		int result = userGroupId != null ? userGroupId.hashCode() : 0;
		result = 31 * result + (skillId != null ? skillId.hashCode() : 0);
		return result;
	}
}
