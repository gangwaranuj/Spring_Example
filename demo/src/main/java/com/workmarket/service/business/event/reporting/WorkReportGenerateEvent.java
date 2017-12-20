package com.workmarket.service.business.event.reporting;

import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.service.business.event.Event;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;

public class WorkReportGenerateEvent extends Event {

	private ReportRequestData entityRequestForReport;
	private String directory;
	private Set<String> recipients;
	private Long reportId;

	public WorkReportGenerateEvent(ReportRequestData entityRequestForReport, String directory, Set<String> recipients,Long reportId) {
		this.entityRequestForReport = entityRequestForReport;
		this.directory = directory;
		this.recipients = recipients;
		this.reportId = reportId;
	}

	public ReportRequestData getReportRequestData() {
		return entityRequestForReport;
	}

	public void setReportRequestData(ReportRequestData entityRequestForReport) {
		this.entityRequestForReport = entityRequestForReport;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Set<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
	}

	public boolean hasRecipients() {
		return CollectionUtils.isNotEmpty(recipients);
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
}

