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
 * @since 5/2/2011
 */
@Component(value = "backgroundCheckExec")
@Scope(value = "prototype")
public class BackgroundCheck extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(BackgroundCheck.class);


	public BackgroundCheck() {
		setPending(Boolean.FALSE);
	}


	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.BACKGROUND_CHECK);
	}

	@Override
	public void updateSummaries(RegisterTransaction wireTransaction) throws InsufficientFundsException {
		logger.debug(toString("Background Check deducted by", wireTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = wireTransaction.getAccountRegister().getAccountRegisterSummaryFields();

		if (wireTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1)
			throw new InsufficientFundsException("There isn't enough available cash for to pay for a Background Check transaction...");

		updateActualCashAndAvailableCash(accountRegisterSummaryFields, wireTransaction);
		updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, wireTransaction);
		addGeneralCash(accountRegisterSummaryFields, wireTransaction);

	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		logger.debug(toString("Background Check can't be reversed by", wireTransaction));
		return Boolean.FALSE;
	}
}
