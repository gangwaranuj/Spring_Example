package com.workmarket.common.template.pdf;

import com.workmarket.domains.model.tax.EarningReport;

public class TaxEarningsReportPDFTemplate extends PDFTemplate {
	private static final long serialVersionUID = 1L;
	
	private EarningReport earningReport;
	
	public TaxEarningsReportPDFTemplate(EarningReport earningReport) {
		super();
		this.earningReport = earningReport;
		if (earningReport != null) {
			setOutputFileName("Earning_Report_" + earningReport.getId());
		}
	}
	
	public EarningReport getEarningReport() {
		return earningReport;
	}

}
