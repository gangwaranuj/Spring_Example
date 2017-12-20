package com.workmarket.reporting.service;

import com.workmarket.reporting.model.EvidenceReport;
import com.workmarket.reporting.model.EvidenceReportRow;

import java.util.List;

public interface EvidenceReportMapperService {

	public List<EvidenceReportRow> mapEvidenceReportToDataTable(Long groupId,
	                                                            List<EvidenceReport> evidenceReports,
	                                                            String screeningType);

	public List<String[]> mapEvidenceReportToCSV(List<EvidenceReport> evidenceReports);

}
