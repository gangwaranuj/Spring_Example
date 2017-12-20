package com.workmarket.dto;

import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.domains.model.ObjectiveType;

import java.util.List;

public class UserGroupDTO {

	private Long userGroupId;
	private String name;
	private String description;
	private Long industryId;
	private boolean openMembership = Boolean.FALSE;
	private boolean requiresApproval = Boolean.TRUE;
	private boolean activeFlag = Boolean.TRUE;
	private ObjectiveType objectiveType;
	private String objective;
	private boolean paymentTermsRequired = false;
	private boolean searchable = true;
	private boolean isPublic = false;
	private Long companyId;
	private Long ownerId;
	private List<Long> skillIds;
	private List<String> orgUnitUuids;

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public boolean getOpenMembership() {
		return openMembership;
	}

	public void setOpenMembership(boolean openMembership) {
		this.openMembership = openMembership;
	}

	public boolean getRequiresApproval() {
		return requiresApproval;
	}

	public void setRequiresApproval(boolean requiresApproval) {
		this.requiresApproval = requiresApproval;
	}

	public boolean getActiveFlag() {
		return this.activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public ObjectiveType getObjectiveType() {
		return objectiveType;
	}

	public void setObjectiveType(ObjectiveType objectiveType) {
		this.objectiveType = objectiveType;
	}

	public void setObjectiveType(String objectiveType) {
		this.objectiveType = new ObjectiveType(objectiveType);
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public boolean isPaymentTermsRequired() {
		return paymentTermsRequired;
	}

	public void setPaymentTermsRequired(boolean paymentTermsRequired) {
		this.paymentTermsRequired = paymentTermsRequired;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public void setSkillIds(List<Long> skillIds) {
		this.skillIds = skillIds;
	}

	public List<Long> getSkillIds() {
		return skillIds;
	}

	public List<String> getOrgUnitUuids() {
		return orgUnitUuids;
	}

	public void setOrgUnitUuids(final List<String> orgUnitUuids) {
		this.orgUnitUuids = orgUnitUuids;
	}
}
