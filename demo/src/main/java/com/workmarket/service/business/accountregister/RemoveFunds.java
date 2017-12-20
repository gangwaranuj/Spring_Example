/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @since 5/5/2011
 */
@Component(value = "removeFunds")
@Scope(value = "prototype")
public class RemoveFunds extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(RemoveFunds.class);


	public RemoveFunds() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.REMOVE_FUNDS);
	}

	@Override
	public void updateSummaries(RegisterTransaction removeFundsTransaction) throws InsufficientFundsException {
		logger.debug(toString("Remove Funds subtracted", removeFundsTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = removeFundsTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		if (removeFundsTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1)
			throw new InsufficientFundsException("There isn't enough available cash for remove funds transaction...");

		if (accountRegisterSummaryFields.getWithdrawableCash().compareTo(removeFundsTransaction.getAmount().abs()) == -1)
			throw new InsufficientFundsException("There isn't enough withdrawable cash for remove funds transaction...");

		updateActualCashAndAvailableCash(accountRegisterSummaryFields, removeFundsTransaction);
		accountRegisterSummaryFields.setWithdrawableCash(accountRegisterSummaryFields.getWithdrawableCash().add(removeFundsTransaction.getAmount()));
		addGeneralCash(accountRegisterSummaryFields, removeFundsTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("Remove Funds can't be reversed ", workResourceTransaction));
		return Boolean.FALSE;
	}
}
