package com.workmarket.web.helpers;

import com.workmarket.domains.model.WorkStatusType;

import java.util.HashSet;
import java.util.Set;

/**
 * Date: 9/4/13
 * Time: 10:44 AM
 */
public class WorkStatusFilterHelper {
	public static Set<WorkStatusType> createWorkStatusFilter(String status) {
		Set<WorkStatusType> workStatusFilterList = new HashSet<>();
		switch (status) {
			case WorkStatusType.DRAFT:
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.DRAFT));
				break;
			case WorkStatusType.ACTIVE:
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.ACTIVE));
				break;
			case WorkStatusType.SENT:
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.SENT));
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.AVAILABLE));
				break;
			case WorkStatusType.INPROGRESS:
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.INPROGRESS));
				break;
			default:
				// Default DRAFT SENT, ACTIVE, INPROGRESS for map view
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.DRAFT));
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.ACTIVE));
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.INPROGRESS));
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.SENT));
				workStatusFilterList.add(new WorkStatusType(WorkStatusType.AVAILABLE));
				break;
		}
		return workStatusFilterList;
	}
}
