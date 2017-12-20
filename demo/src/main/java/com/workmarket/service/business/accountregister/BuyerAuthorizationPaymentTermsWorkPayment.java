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
public class BuyerAuthorizationPaymentTermsWorkPayment extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(BuyerAuthorizationPaymentTermsWorkPayment.class);


	public BuyerAuthorizationPaymentTermsWorkPayment() {
		setPending(Boolean.TRUE);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("AccountsPayableBalance, PendingCommitments amout of", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setAccountsPayableBalance(accountRegisterSummaryFields.getAccountsPayableBalance().add(workResourceTransaction.getAmount().abs()));
		accountRegisterSummaryFields.setPendingCommitments(accountRegisterSummaryFields.getPendingCommitments().add(workResourceTransaction.getAmount().abs()));
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("AccountsPayableBalance, PendingCommitments reversed by", workResourceTransaction));

		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		logger.debug(toString(accountRegisterSummaryFields));
		accountRegisterSummaryFields.setAccountsPayableBalance(accountRegisterSummaryFields.getAccountsPayableBalance().subtract(workResourceTransaction.getAmount().abs()));
		accountRegisterSummaryFields.setPendingCommitments(accountRegisterSummaryFields.getPendingCommitments().subtract(workResourceTransaction.getAmount().abs()));
		return Boolean.TRUE;
	}
}
