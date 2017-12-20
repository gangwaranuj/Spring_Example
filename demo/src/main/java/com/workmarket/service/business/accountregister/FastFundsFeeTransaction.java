package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class FastFundsFeeTransaction extends RegisterTransactionExecutor {
	private static final Log logger = LogFactory.getLog(FastFundsFeeTransaction.class);


	public FastFundsFeeTransaction() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.FAST_FUNDS_FEE);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) throws InsufficientFundsException {
		logger.debug(toString("Charge fast funds Fee ", registerTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		if (registerTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1) {
			throw new InsufficientFundsException("There isn't enough available cash to charge the fast funds fee");
		}
		updateActualCashAndAvailableCash(accountRegisterSummaryFields, registerTransaction);
		addGeneralCash(accountRegisterSummaryFields, registerTransaction);
		updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, registerTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction registerTransaction) {
		logger.debug(toString("Fast funds fee can't be reversed ", registerTransaction));
		return false;
	}
}
