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
public class AssignmentBatchPayment extends InvoicePayment {

	private static final Log logger = LogFactory.getLog(AssignmentBatchPayment.class);

	public AssignmentBatchPayment() {
		setPending(false);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.ASSIGNMENT_BATCH_PAYMENT);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) {
		logger.debug(toString("Batch Payment added by", registerTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();

		if (registerTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1) {
			throw new InsufficientFundsException("There isn't enough available cash for an invoice payment transaction...");
		}

		/**
		 * Only updates the available cash not the actual cash
		 */
		accountRegisterSummaryFields.setAvailableCash(accountRegisterSummaryFields.getAvailableCash().add(registerTransaction.getAmount()));
		updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, registerTransaction);
	}
}
