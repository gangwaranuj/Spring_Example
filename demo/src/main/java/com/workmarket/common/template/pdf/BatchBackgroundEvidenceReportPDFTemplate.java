package com.workmarket.common.template.pdf;

import com.workmarket.reporting.model.EvidenceReport;
import org.springframework.util.ClassUtils;

import java.util.List;

public class BatchBackgroundEvidenceReportPDFTemplate extends PDFTemplate {

	private static final long serialVersionUID = 1L;

	private List<EvidenceReport> evidenceReports;
	private String sterlingLogoUrl;

	private static final String srcSterlingImageLogo = "images/sterling-logo.png";

	public BatchBackgroundEvidenceReportPDFTemplate(List<EvidenceReport> evidenceReports, Long groupId) {
		super();
		this.evidenceReports = evidenceReports;
		this.sterlingLogoUrl = ClassUtils.getDefaultClassLoader().getResource(srcSterlingImageLogo).getPath();
		if (groupId != null) {
			setOutputFileName("evidencereport_" + groupId);
		}
	}


	public List<EvidenceReport> getEvidenceReports() {
		return evidenceReports;
	}

	public void setEvidenceReports(List<EvidenceReport> evidenceReports) {
		this.evidenceReports = evidenceReports;
	}

	public String getSterlingLogoUrl() {
		return sterlingLogoUrl;
	}

	public void setSterlingLogoUrl(String sterlingLogoUrl) {
		this.sterlingLogoUrl = sterlingLogoUrl;
	}
}
