package com.workmarket.domains.model.realtime;

import com.workmarket.thrift.services.realtime.RealtimePagination;

public class RealtimeRequest {

	private Long companyId;
	private RealtimePagination pagination;
	private RealtimeReportType realtimeReportType;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public RealtimePagination getPaginationRequest() {
		return pagination;
	}

	public void setPagination(RealtimePagination pagination) {
		this.pagination = pagination;
	}

	public RealtimeReportType getRealtimeReportType() {
		return realtimeReportType;
	}

	public void setRealtimeReportType(RealtimeReportType realtimeReportType) {
		this.realtimeReportType = realtimeReportType;
	}

}
