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
public class BuyerAuthorizationImmediateWorkPayment extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(BuyerAuthorizationImmediateWorkPayment.class);


	public BuyerAuthorizationImmediateWorkPayment() {
		setPending(Boolean.TRUE);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.BUYER_COMMITMENT_TO_PAY);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) throws InsufficientFundsException {
		logger.debug(toString("AvailableCash, DepositedCash, WithdrawalableCash, PendingCommitments deducted by", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setAvailableCash(accountRegisterSummaryFields.getAvailableCash().add(workResourceTransaction.getAmount()));
		updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, workResourceTransaction);
		accountRegisterSummaryFields.setPendingCommitments(accountRegisterSummaryFields.getPendingCommitments().add(workResourceTransaction.getAmount().abs()));
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("AvailableCash, DepositedCash, PendingCommitments reversed by", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setAvailableCash(accountRegisterSummaryFields.getAvailableCash().subtract(workResourceTransaction.getAmount()));
		//The decision has been made to reverse any combination deposited cash and withdrawable cash transactions simply to deposited cash. 
		//If funds had to be debited from both fields, it's hard to determine the exact break-out.
		accountRegisterSummaryFields.setDepositedCash(accountRegisterSummaryFields.getDepositedCash().subtract(workResourceTransaction.getAmount()));
		accountRegisterSummaryFields.setPendingCommitments(accountRegisterSummaryFields.getPendingCommitments().subtract(workResourceTransaction.getAmount().abs()));

		return Boolean.TRUE;
	}
}
