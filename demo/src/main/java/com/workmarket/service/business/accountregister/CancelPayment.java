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
 * @since 5/1/2011
 */
@Component
@Scope(value = "prototype")
public class CancelPayment extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(CancelPayment.class);


	public CancelPayment() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.CANCEL_PAYMENT);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("CancelPayment added to", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		//It appears that one must also debit deposited cash so the combination of deposited cash and withdrawable cash equal available cash.
		//accountRegisterSummaryFields.setDepositedCash(accountRegisterSummaryFields.getDepositedCash().add(workResourceTransaction.getAmount()));
		//On 6/10/2011, switched to withdrawableCash instead of depositedCash. The work resources requested to withdrawal their funds. 
		accountRegisterSummaryFields.setWithdrawableCash(accountRegisterSummaryFields.getWithdrawableCash().add(workResourceTransaction.getAmount()));
		updateActualCashAndAvailableCash(accountRegisterSummaryFields, workResourceTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("Can't reverse a cancel payment", workResourceTransaction));
		return Boolean.FALSE;
	}

}
