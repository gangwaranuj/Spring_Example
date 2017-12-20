package com.workmarket.service.business.tax.report;

import com.workmarket.domains.model.tax.AbstractTaxReportSet;

/**
 * Author: rocio
 */
public interface TaxReportGenerator<T extends AbstractTaxReportSet> {

	void setTaxReportSet(T taxReportSet);

	T generateTaxReport();
}