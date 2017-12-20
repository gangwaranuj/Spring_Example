package com.workmarket.service.business.dto;

import com.google.common.collect.Maps;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.dto.AggregatesDTO;

import java.util.Map;

public class WorkAggregatesDTO extends AggregatesDTO {

	private final Map<Long, WorkSubStatusTypeReportRow> workSubStatusCounts = Maps.newLinkedHashMap();

	public void setCountForWorkSubStatus(Long subStatusId, WorkSubStatusTypeReportRow row) {
		workSubStatusCounts.put(subStatusId, row);
	}

	public WorkSubStatusTypeReportRow getWorkSubStatusTypeReportRow(Long subStatusId) {
		return (workSubStatusCounts.containsKey(subStatusId)) ? workSubStatusCounts.get(subStatusId) : new WorkSubStatusTypeReportRow();
	}

	public Map<Long, WorkSubStatusTypeReportRow> getWorkSubStatusCounts() {
		return workSubStatusCounts;
	}

	@Override
	public String toString() {
		return "WorkAggregatesDTO{" +
			"workSubStatusCounts=" + workSubStatusCounts +
			'}';
	}
}
