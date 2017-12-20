package com.workmarket.domains.model.skill;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
public class CompanySkillPK implements Serializable {

	private static final long serialVersionUID = -4613725074806114069L;

	private Long companyId;
	private Long skillId;

	public CompanySkillPK() {

	}
	
	public CompanySkillPK(Long companyId, Long skillId) {
		this.companyId = companyId;
		this.skillId = skillId;
	}

	@Column(name = "company_id", nullable = false)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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

		final CompanySkillPK that = (CompanySkillPK) o;

		return companyId != null ? companyId.equals(that.companyId) : that.companyId == null &&
			(skillId != null ? skillId.equals(that.skillId) : that.skillId == null);

	}

	@Override
	public int hashCode() {
		int result = companyId != null ? companyId.hashCode() : 0;
		result = 31 * result + (skillId != null ? skillId.hashCode() : 0);
		return result;
	}
}
