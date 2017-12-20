/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkResourceTransaction;
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
public class FinishedWorkFeeLane2 extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(FinishedWorkFeeLane2.class);

	public FinishedWorkFeeLane2() {
		setPending(Boolean.FALSE);
	}


	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.NEW_WORK_LANE_2);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) throws InsufficientFundsException {
		if (((WorkResourceTransaction) workResourceTransaction).isBundlePayment()) {
			return;
		}
		logger.debug(toString("FinishLane2 subtracted by", workResourceTransaction));
		logger.debug(workResourceTransaction.getAmount());
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();

		//If it's batch payment only update the actual cash
		if (!((WorkResourceTransaction) workResourceTransaction).isBatchPayment()) {

			if (workResourceTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1) {
				throw new InsufficientFundsException("There isn't enough available cash for to pay work fees transaction...");
			}

			accountRegisterSummaryFields.setAvailableCash(accountRegisterSummaryFields.getAvailableCash().add(workResourceTransaction.getAmount()));
			updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, workResourceTransaction);
		}
		accountRegisterSummaryFields.setActualCash(accountRegisterSummaryFields.getActualCash().add(workResourceTransaction.getAmount()));
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("FinishLane2 reversed by", workResourceTransaction));
		return Boolean.FALSE;
	}
}
