package com.workmarket.service.business.event;

import com.workmarket.domains.model.User;

public class RestoreBankAccountNumbersFromVault extends Event {
	private static final long serialVersionUID = -2659852670154610129L;

	private final String fromCreatedOnDate;

	public RestoreBankAccountNumbersFromVault(final String fromCreatedOnDate) {
		this.fromCreatedOnDate = fromCreatedOnDate;
	}

	public String getFromCreatedOnDate() {
		return fromCreatedOnDate;
	}
}
