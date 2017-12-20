package com.workmarket.service.business.dto;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WorkResourceDetailPagination extends AbstractPagination<WorkResourceDetail> implements Pagination<WorkResourceDetail> {

	boolean includeApplyNegotiation = false;
	boolean includeNotes = false;
	boolean includeLabels = false;

	public WorkResourceDetailPagination() {}
	public WorkResourceDetailPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		WORK_RESOURCE_STATUS_CODE("resource.work_resource_status_type_code"),
		WORK_RESOURCE_COMPANY_ID("company.id");

		private String column;

		FILTER_KEYS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public enum SORTS {
		LAST_NAME("user.last_name"),
		ACTIVE_AND_LAST_NAME("user.last_name"),
		NEGOTIATION_CREATED_ON("negotiationRequestedOn"),
		NEGOTIATION_SCHEDULE_FROM("negotiationScheduleFrom"),
		NEGOTIATION_TOTAL_COST("negotiationSpendLimit"),
		AVG_RATING("rating_average"),
		ONTIME_PERCENTAGE("onTimePercentage"),
		DELIVERABLE_ON_TIME_PERCENTAGE("deliverableOnTimePercentage"),
		DISTANCE("distance");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public boolean isIncludeNotes() {
		return includeNotes;
	}
	public void setIncludeNotes(boolean includeNotes) {
		this.includeNotes = includeNotes;
	}

	public boolean isIncludeLabels() {
		return includeLabels;
	}
	public WorkResourceDetailPagination setIncludeLabels(boolean includeLabels) {
		this.includeLabels = includeLabels;
		return this;
	}

	public boolean isIncludeApplyNegotiation() {
		return includeApplyNegotiation;
	}
	public void setIncludeApplyNegotiation(boolean includeApplyNegotiation) {
		this.includeApplyNegotiation = includeApplyNegotiation;
	}
}
