/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @since 5/2/2011
 */
@Component
@Scope(value = "prototype")
public class AddFunds extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(AddFunds.class);

	public AddFunds() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.ADD_FUNDS);
	}

	 public void updateSummaries(RegisterTransaction wireTransaction) {
		logger.debug(toString("AddFunds deducted by ", wireTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = wireTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setDepositedCash(accountRegisterSummaryFields.getDepositedCash().add(wireTransaction.getAmount()));
		updateActualCashAndAvailableCash(accountRegisterSummaryFields, wireTransaction);
		addGeneralCash(accountRegisterSummaryFields, wireTransaction);
	}

	public boolean reverse(RegisterTransaction wireTransaction) {
		logger.debug(toString("AddFunds can't be reversed :", wireTransaction));
		return Boolean.FALSE;
	}

}
