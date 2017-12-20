package com.workmarket.domains.model.banking;

import com.workmarket.factory.BankingIntegrationGenerationRequestRowMapper;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

public class PayPalRowMapper implements BankingIntegrationGenerationRequestRowMapper {
	@Override
	public String[] mapRow(BankAccountTransaction tx, String bankAccountNumber) {
		PayPalAccount account = (PayPalAccount)tx.getBankAccount();

		BigDecimal amount = (tx.getRegisterTransactionType().getCode().equals(RegisterTransactionType.REMOVE_FUNDS_PAYPAL)) ?
				tx.getAmount().negate() :
				tx.getAmount();

		if (amount.compareTo(BigDecimal.ZERO) == 0) return new String[0];
		Company company = tx.getAccountRegister().getCompany();

		return CollectionUtilities.newArray(
				account.getEmailAddress(),
				amount.toString(),
				Constants.DEFAULT_CURRENCY,
				StringUtils.abbreviate(String.format("%d %s", company.getId(), company.getEffectiveName()), 30),
				String.format("Transaction ID %d", tx.getId())
		);
	}
}
