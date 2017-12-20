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
public class ResourceWorkPayment extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(ResourceWorkPayment.class);

	public ResourceWorkPayment() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.RESOURCE_WORK_PAYMENT);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("WorkPayment added by", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setWithdrawableCash(accountRegisterSummaryFields.getWithdrawableCash().add(workResourceTransaction.getAmount()));
		updateActualCashAndAvailableCash(accountRegisterSummaryFields, workResourceTransaction);
		addGeneralCash(accountRegisterSummaryFields,workResourceTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("WorkPayment can't be reversed ", workResourceTransaction));
		return Boolean.FALSE;
	}
}
