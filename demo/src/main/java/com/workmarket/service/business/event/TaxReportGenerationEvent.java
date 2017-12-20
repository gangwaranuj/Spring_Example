package com.workmarket.service.business.event;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;

public class TaxReportGenerationEvent<T extends AbstractTaxReportSet> extends Event {

	private static final long serialVersionUID = 325478912072537141L;

	private T taxReportSet;

	// TODO: Alex - replace reference to entity with id or code
	@Deprecated
	private User requestor;

	public TaxReportGenerationEvent() {
		super();
	}

	public TaxReportGenerationEvent(T taxReportSet, User requestor) {
		this.taxReportSet = taxReportSet;
		this.requestor = requestor;
	}

	public T getTaxReportSet() {
		return taxReportSet;
	}
	public User getRequestor() {
		return requestor;
	}
}
