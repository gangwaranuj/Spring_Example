package com.workmarket.domains.work.model.state;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WorkSubStatusTypeDashboard {

	private List<WorkSubStatusType> workSubStatusList = Lists.newArrayList();
	private Map<Long, WorkSubStatusTypeCompanySetting> workSubStatusTypeCompanySettingsMap = Maps.newLinkedHashMap();

	public List<WorkSubStatusType> getWorkSubStatusList() {
		return workSubStatusList;
	}

	public void setWorkSubStatusList(List<WorkSubStatusType> workSubStatusList) {
		this.workSubStatusList = workSubStatusList;
	}

	public Map<Long, WorkSubStatusTypeCompanySetting> getWorkSubStatusTypeCompanySettingsMap() {
		return workSubStatusTypeCompanySettingsMap;
	}

	public void setWorkSubStatusTypeCompanySettingsMap(Map<Long, WorkSubStatusTypeCompanySetting> workSubStatusTypeCompanySettingsMap) {
		this.workSubStatusTypeCompanySettingsMap = workSubStatusTypeCompanySettingsMap;
	}

	public WorkSubStatusTypeCompanySetting getSettingsByWorkSubStatus(long workSubStatusId) {
		if (workSubStatusTypeCompanySettingsMap.containsKey(workSubStatusId)) {
			return workSubStatusTypeCompanySettingsMap.get(workSubStatusId);
		}
		return null;
		
	}

	@Override
	public String toString() {
		return "WorkSubStatusTypeDashboard [workSubStatusList=" + workSubStatusList + ", workSubStatusTypeCompanySettingsMap=" + workSubStatusTypeCompanySettingsMap + "]";
	}
}
