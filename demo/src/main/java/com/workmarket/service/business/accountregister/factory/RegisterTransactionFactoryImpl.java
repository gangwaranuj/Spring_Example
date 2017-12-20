package com.workmarket.service.business.accountregister.factory;

import com.google.common.base.Optional;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.postalcode.Country;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class RegisterTransactionFactoryImpl implements RegisterTransactionFactory {

	private static final Log logger = LogFactory.getLog(RegisterTransactionFactoryImpl.class);

	@Override
	public RegisterTransactionType newBankAccountRegisterTransactionType(AbstractBankAccount bankAccount) {
		Assert.notNull(bankAccount);
		String transactionType = RegisterTransactionType.BANK_ACCOUNT_TRANSACTION;
		if (bankAccount instanceof PayPalAccount) {
			transactionType = RegisterTransactionType.PAYPAL_ACCOUNT_TRANSACTION;
		}
		return new RegisterTransactionType(transactionType);
	}

	@Override
	public RegisterTransactionType newWithdrawalFeeRegisterTransactionType(AbstractBankAccount bankAccount) {
		Assert.notNull(bankAccount);
		String transactionType = null;
		if (bankAccount instanceof PayPalAccount) {
			Optional<String> transactionTypeOpt = RegisterTransactionType.newPaypalFeeInstanceByCountry(bankAccount.getCountry());
			if (transactionTypeOpt.isPresent())
				transactionType = transactionTypeOpt.get();
			else
				logger.warn(String.format("Unknown transaction type for country %s", bankAccount.getCountry()));
		}
		if (StringUtils.isNotBlank(transactionType)) {
			return new RegisterTransactionType(transactionType);
		}
		return null;
	}

	@Override
	public RegisterTransactionType newSecretWithdrawalFeeRegisterTransactionType(AbstractBankAccount bankAccount) {
		Assert.notNull(bankAccount);
		String transactionType = null;
		if (bankAccount instanceof PayPalAccount) {
			Optional<String> transactionTypeOpt = RegisterTransactionType.newWMPaypalFeeInstanceByCountry(bankAccount.getCountry());
			if (transactionTypeOpt.isPresent())
				transactionType = transactionTypeOpt.get();
			else
				logger.warn(String.format("Unknown secret transaction type for country %s", bankAccount.getCountry()));
		}
		if (StringUtils.isNotBlank(transactionType)) {
			return new RegisterTransactionType(transactionType);
		}
		return null;
	}

	@Override
	public RegisterTransactionType newRemoveFundsRegisterTransactionType(AbstractBankAccount bankAccount) {
		Assert.notNull(bankAccount);
		String transactionType = RegisterTransactionType.REMOVE_FUNDS;
		if (bankAccount instanceof PayPalAccount) {
			transactionType = RegisterTransactionType.REMOVE_FUNDS_PAYPAL;
		} else if (bankAccount instanceof GlobalCashCardAccount) {
			transactionType = RegisterTransactionType.REMOVE_FUNDS_GCC;
		}
		return new RegisterTransactionType(transactionType);
	}

	@Override
	public RegisterTransactionType newBackgroundCheckRegisterTransactionType(String countryCode) {
		Assert.hasText(countryCode);
		if (StringUtils.equals(Country.USA, countryCode)) {
			return new RegisterTransactionType(RegisterTransactionType.BACKGROUND_CHECK);
		}
		return new RegisterTransactionType(RegisterTransactionType.BACKGROUND_CHECK_INTERNATIONAL);
	}
}
