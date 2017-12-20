package com.workmarket.common.template;

import com.workmarket.domains.model.tax.EarningReport;

public class EarningReportAvailableNotificationTemplate extends AbstractTaxReportAvailableNotificationTemplate<EarningReport> {

	private static final long serialVersionUID = -195063673459541526L;

	public EarningReportAvailableNotificationTemplate(Long toId, EarningReport taxReport) {
		super(toId, taxReport);
	}

}
