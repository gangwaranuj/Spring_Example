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
public class ResourceAuthorizationImmediateWorkPayment extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(ResourceAuthorizationImmediateWorkPayment.class);


	public ResourceAuthorizationImmediateWorkPayment() {
		setPending(Boolean.TRUE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.RESOURCE_COMMITMENT_TO_RECEIVE_PAY);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("Pending Earned Cash amout of", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setPendingEarnedCash(accountRegisterSummaryFields.getPendingEarnedCash().add(workResourceTransaction.getAmount()));
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("Pending Earned Cash reversed by", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setPendingEarnedCash(accountRegisterSummaryFields.getPendingEarnedCash().subtract(workResourceTransaction.getAmount()));
		return Boolean.TRUE;
	}

}
