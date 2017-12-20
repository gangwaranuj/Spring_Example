package com.workmarket.service.business.event;

public class RestoreTaxEntityTaxNumbersFromVault extends Event {
	private static final long serialVersionUID = -7991035979602128718L;
	private final String fromModifiedOnDate;

	public RestoreTaxEntityTaxNumbersFromVault(final String fromModifiedOnDate) {
		this.fromModifiedOnDate = fromModifiedOnDate;
	}

	public String getFromModifiedOnDate() {
		return fromModifiedOnDate;
	}
}
