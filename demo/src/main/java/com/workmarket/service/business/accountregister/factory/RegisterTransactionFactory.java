package com.workmarket.service.business.accountregister.factory;

import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;

public interface RegisterTransactionFactory {

	RegisterTransactionType newBankAccountRegisterTransactionType(AbstractBankAccount bankAccount);

	RegisterTransactionType newWithdrawalFeeRegisterTransactionType(AbstractBankAccount bankAccount);

	RegisterTransactionType newSecretWithdrawalFeeRegisterTransactionType(AbstractBankAccount bankAccount);

	RegisterTransactionType newRemoveFundsRegisterTransactionType(AbstractBankAccount bankAccount);

	/**
	 * Background checks are uniquely priced by country. Since this isn't specifically an i18n/currency
	 * concern, we treat each country-specific background check as a distinct product.
	 * This method returns the transaction type (ultimately tied to a cost) per country.
	 *
	 * @param countryCode
	 * @return
	 */
	RegisterTransactionType newBackgroundCheckRegisterTransactionType(String countryCode);
}
