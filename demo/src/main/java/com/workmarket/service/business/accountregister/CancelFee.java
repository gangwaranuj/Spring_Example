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
 * @since 5/1/2011
 */
@Component
@Scope(value = "prototype")
public class CancelFee extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(CancelFee.class);


	public CancelFee() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.CANCEL_FEE);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) throws InsufficientFundsException {
		logger.debug(toString("CancelFee deducted by", registerTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();

		if (registerTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1)
			throw new InsufficientFundsException("There isn't enough available cash for a cancel fee.");

		//Adding a negative value
		updateActualCashAndAvailableCash(accountRegisterSummaryFields, registerTransaction);
		updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, registerTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("Can't reverse a cancel fee", workResourceTransaction));
		return Boolean.FALSE;
	}
}
