package com.workmarket.domains.model.banking;

import com.workmarket.factory.BankingIntegrationGenerationRequestRowMapper;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.utility.CollectionUtilities;

import java.math.BigDecimal;


public class GlobalCashCardRowMapper implements BankingIntegrationGenerationRequestRowMapper {

	@Override
	public String[] mapRow(BankAccountTransaction tx, String bankAccountNumber) {
		GlobalCashCardAccount account = (GlobalCashCardAccount) tx.getBankAccount();

		BigDecimal amount = (tx.getRegisterTransactionType().getCode().equals(RegisterTransactionType.REMOVE_FUNDS_GCC)) ?
				tx.getAmount().negate() :
				tx.getAmount();

		if (amount.compareTo(BigDecimal.ZERO) == 0) return new String[0];

		return CollectionUtilities.newArray(
				account.getAccountNumber(),
				String.format("Funds from Work Market"),
				amount.toString()
		);
	}
}