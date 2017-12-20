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
public class ResourceAuthorizationPaymentTermsWorkPayment extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(ResourceAuthorizationPaymentTermsWorkPayment.class);


	public ResourceAuthorizationPaymentTermsWorkPayment() {
		setPending(Boolean.TRUE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.RESOURCE_PAYMENT_TERMS_COMMITMENT_TO_RECEIVE_PAY);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("PendingEarnedCash, AccountsReceivableBalance amout of:", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setPendingEarnedCash(accountRegisterSummaryFields.getPendingEarnedCash().add(workResourceTransaction.getAmount().abs()));
		accountRegisterSummaryFields.setAccountsReceivableBalance(accountRegisterSummaryFields.getAccountsReceivableBalance().add(workResourceTransaction.getAmount().abs()));
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("PendingEarnedCash, AccountsReceivableBalance reversed by:", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setPendingEarnedCash(accountRegisterSummaryFields.getPendingEarnedCash().subtract(workResourceTransaction.getAmount().abs()));
		accountRegisterSummaryFields.setAccountsReceivableBalance(accountRegisterSummaryFields.getAccountsReceivableBalance().subtract(workResourceTransaction.getAmount().abs()));
		return Boolean.TRUE;
	}
}
