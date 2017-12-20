package com.workmarket.service.business.event;

import java.util.List;

public class MigrateBankAccountsEvent extends Event {
	private static final long serialVersionUID = -912995886554473977L;

	private final List<Long> bankAccountIds;

	public MigrateBankAccountsEvent(List<Long> bankAccountIds) {
		this.bankAccountIds = bankAccountIds;
	}

	public List<Long> getBankAccountIds() {
		return bankAccountIds;
	}
}
