package com.workmarket.service.business.dto;

import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;

public class WorkSubStatusTypeCompanySettingDTO {

	private Long workSubStatusTypeId;
	private String colorRgb;
	private WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType;

	public WorkSubStatusTypeCompanySetting.DashboardDisplayType getDashboardDisplayType() {
		return dashboardDisplayType;
	}

	public void setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType) {
		this.dashboardDisplayType = dashboardDisplayType;
	}

	public Long getWorkSubStatusTypeId() {
		return workSubStatusTypeId;
	}

	public void setWorkSubStatusTypeId(Long workSubStatusTypeId) {
		this.workSubStatusTypeId = workSubStatusTypeId;
	}

	public String getColorRgb() {
		return colorRgb;
	}

	public void setColorRgb(String colorRgb) {
		this.colorRgb = colorRgb;
	}

}
