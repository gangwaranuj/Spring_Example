package com.workmarket.common.template.pdf;

import com.workmarket.domains.model.tax.EarningReport;

/**
 * User: iloveopt
 * Date: 1/13/14
 */
public class NewTaxEarningsReportPDFTemplate extends PDFTemplate {
	private static final long serialVersionUID = 1L;

	private EarningReport earningReport;

	public NewTaxEarningsReportPDFTemplate(EarningReport earningReport) {
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
