package com.workmarket.data.report.work;

import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;

import java.io.Serializable;

public class WorkSubStatusTypeCompanyConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String CACHE_HASH_COLOR = "color";
	public static String CACHE_HASH_DISPLAY = "display";

	private WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType;
	private String colorRgb;
	private Long companyId;

	public WorkSubStatusTypeCompanySetting.DashboardDisplayType getDashboardDisplayType() {
		return dashboardDisplayType;
	}

	public void setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType) {
		this.dashboardDisplayType = dashboardDisplayType;
	}

	public String getColorRgb() {
		return colorRgb;
	}

	public void setColorRgb(String colorRgb) {
		this.colorRgb = colorRgb;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}
