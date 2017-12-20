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
@Component
@Scope(value = "prototype")
public class CreditCardFee extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(CreditCardFee.class);


	public CreditCardFee() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.CREDIT_CARD_FEE);
	}

	@Override
	public void updateSummaries(RegisterTransaction wireTransaction) throws InsufficientFundsException {
		logger.debug(toString("Fee deducted by", wireTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = wireTransaction.getAccountRegister().getAccountRegisterSummaryFields();

		if (wireTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1)
			throw new InsufficientFundsException("There isn't enough available cash for to pay for a Credit Card/Pay Pal transaction...");

		updateActualCashAndAvailableCash(accountRegisterSummaryFields, wireTransaction);
		updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, wireTransaction);
		addGeneralCash(accountRegisterSummaryFields, wireTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		logger.debug(toString("Credit card fee or PayPal fee can't be reversed ", wireTransaction));
		return Boolean.FALSE;
	}
}
