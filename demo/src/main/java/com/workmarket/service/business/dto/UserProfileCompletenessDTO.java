package com.workmarket.service.business.dto;

import java.util.List;

import com.workmarket.domains.model.ProfileActionType;

public class UserProfileCompletenessDTO {

	private Integer completedPercentage;
	private List<ProfileActionType> missingActions;
	
	public Integer getCompletedPercentage() {
		return completedPercentage;
	}

	public void setCompletedPercentage(Integer completedPercentage) {
		this.completedPercentage = completedPercentage;
	}

	public List<ProfileActionType> getMissingActions() {
		return missingActions;
	}

	public void setMissingActions(List<ProfileActionType> missingActions) {
		this.missingActions = missingActions;
	}
}
