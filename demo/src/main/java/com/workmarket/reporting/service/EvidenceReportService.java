package com.workmarket.reporting.service;

import com.google.common.base.Optional;
import com.workmarket.reporting.model.*;

import java.util.List;


public interface EvidenceReportService {

	List<EvidenceReport> fetchDrugTestByGroupId(Long groupId);

	List<EvidenceReport> fetchBackgroundCheckByGroupId(Long groupId);

	List<EvidenceReport> fetchEvidenceReportByGroupId(Long groupId,String screeningType);

	List<EvidenceReportRow> generateEvidenceReportByGroup(Long groupId,String screeningType);

	Optional<String> generateBackgroundCheckPDF(Long userId, boolean generatePDF);

	void bulkDownloadEvidenceReportHandler(String toEmail,Long groupId,String screeningType);

	void bulkDownloadEvidenceReport(String toEmail,Long groupId,String screeningType);

	void exportToCSV(String toEmail,Long groupId,String screeningType);

	void exportToCSVHandler(String toEmail,Long groupId,String screeningType);

}
