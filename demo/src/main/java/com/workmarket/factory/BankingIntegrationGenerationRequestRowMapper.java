package com.workmarket.factory;

import com.workmarket.domains.model.account.BankAccountTransaction;

public interface BankingIntegrationGenerationRequestRowMapper {
	String[] mapRow(BankAccountTransaction tx, String bankAccountNumber);
}
