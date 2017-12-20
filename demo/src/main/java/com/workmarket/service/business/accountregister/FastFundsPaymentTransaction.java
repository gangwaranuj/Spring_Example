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
public class FastFundsPaymentTransaction extends RegisterTransactionExecutor {
	private static final Log logger = LogFactory.getLog(FastFundsFeeTransaction.class);


	public FastFundsPaymentTransaction() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.FAST_FUNDS_PAYMENT);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) throws InsufficientFundsException {
		logger.debug(toString("Fast Funds Payment added: ", registerTransaction));

		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setWithdrawableCash(accountRegisterSummaryFields.getWithdrawableCash().add(registerTransaction.getAmount()));
		updateActualCashAndAvailableCash(accountRegisterSummaryFields, registerTransaction);
		addGeneralCash(accountRegisterSummaryFields,registerTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction registerTransaction) {
		logger.debug(toString("Fast funds payment can't be reversed ", registerTransaction));
		return false;
	}
}