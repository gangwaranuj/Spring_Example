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

@Component
@Scope(value = "prototype")
public class InvoicePayment extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(InvoicePayment.class);

	public InvoicePayment() {
		setPending(false);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.INVOICE_PAYMENT);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) {
		logger.debug(toString("Invoice Payment added by", registerTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();

		if (registerTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1)
			throw new InsufficientFundsException("There isn't enough available cash for an invoice payment transaction...");

		updateActualCashAndAvailableCash(accountRegisterSummaryFields, registerTransaction);
		updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, registerTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		return false;
	}
}
