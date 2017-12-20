package com.workmarket.search.model;

import com.workmarket.search.request.work.WorkSearchRequestUserType;

public class WorkSearchTransientData extends AbstractSearchTransientData {
	private Long companyId;
	private WorkSearchRequestUserType userType;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public WorkSearchRequestUserType getUserType() {
		return userType;
	}

	public void setUserType(WorkSearchRequestUserType userType) {
		this.userType = userType;
	}
}
