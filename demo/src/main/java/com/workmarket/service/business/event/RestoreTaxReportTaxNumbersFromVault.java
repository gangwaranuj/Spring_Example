package com.workmarket.service.business.event;

public class RestoreTaxReportTaxNumbersFromVault extends Event {
	private static final long serialVersionUID = -5069565221942014231L;
	private final String fromCreatedOn;

	public RestoreTaxReportTaxNumbersFromVault(final String fromCreatedOn) {
		this.fromCreatedOn = fromCreatedOn;
	}

	public String getFromCreatedOn() {
		return fromCreatedOn;
	}
}
