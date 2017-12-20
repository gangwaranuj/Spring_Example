package com.workmarket.service.business.event;

import com.workmarket.domains.model.tax.AbstractTaxReportSet;

public class TaxReportPublishedEvent<T extends AbstractTaxReportSet> extends Event {

	private static final long serialVersionUID = 325478912072537141L;

	private T taxReportSet;

	public TaxReportPublishedEvent() {
		super();
	}

	public TaxReportPublishedEvent(T taxReportSet) {
		this.taxReportSet = taxReportSet;
	}

	public T getTaxReportSet() {
		return taxReportSet;
	}
}
