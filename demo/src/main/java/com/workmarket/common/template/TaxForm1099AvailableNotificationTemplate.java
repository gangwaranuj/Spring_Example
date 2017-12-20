package com.workmarket.common.template;

import com.workmarket.domains.model.tax.TaxForm1099;

public class TaxForm1099AvailableNotificationTemplate extends AbstractTaxReportAvailableNotificationTemplate<TaxForm1099> {

	private static final long serialVersionUID = -195063673459541526L;

	public TaxForm1099AvailableNotificationTemplate(Long toId, TaxForm1099 taxReport) {
		super(toId, taxReport);
	}
}
