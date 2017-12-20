package com.workmarket.data.report.work;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class DecoratedWorkReportRow implements Serializable {

	private static final long serialVersionUID = -8792255727118350147L;
	
	private Long workId;
	private List<CustomFieldReportRow> customFields = Lists.newArrayList();
	private List<WorkSubStatusTypeReportRow> workSubStatusTypes = Lists.newArrayList();

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public List<CustomFieldReportRow> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomFieldReportRow> customFields) {
		this.customFields = customFields;
	}
	
	public void setWorkSubStatusTypes(List<WorkSubStatusTypeReportRow> workSubStatusTypes) {
		this.workSubStatusTypes = workSubStatusTypes;
	}

	public List<WorkSubStatusTypeReportRow> getWorkSubStatusTypes() {
		if (workSubStatusTypes == null) workSubStatusTypes = Lists.newArrayList();
		return workSubStatusTypes;
	}
}
