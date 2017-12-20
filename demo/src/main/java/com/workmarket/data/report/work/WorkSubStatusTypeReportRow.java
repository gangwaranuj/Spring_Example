package com.workmarket.data.report.work;

import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;

import java.io.Serializable;

public class WorkSubStatusTypeReportRow implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long workSubStausTypeId;
	private String code;
	private String description;
	private String colorRgb;
	private Integer count;
	private WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType;
	
	public Long getWorkSubStausTypeId() {
		return workSubStausTypeId;
	}

	public void setWorkSubStausTypeId(Long workSubStausTypeId) {
		this.workSubStausTypeId = workSubStausTypeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColorRgb() {
		return colorRgb;
	}

	public void setColorRgb(String colorRgb) {
		this.colorRgb = colorRgb;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getCount() {
		return count;
	}

	public WorkSubStatusTypeCompanySetting.DashboardDisplayType getDashboardDisplayType() {
		return dashboardDisplayType;
	}

	public void setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType) {
		this.dashboardDisplayType = dashboardDisplayType;
	}

	@Override
	public String toString() {
		return "WorkSubStatusTypeReportRow [workSubStausTypeId=" + workSubStausTypeId + ", code=" + code + ", description=" + description + ", colorRgb=" + colorRgb + "], dashboardDisplayType=" + dashboardDisplayType + "]";
	}
}
