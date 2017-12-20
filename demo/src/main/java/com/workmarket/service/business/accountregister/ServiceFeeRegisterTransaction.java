package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.ServiceTransaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Author: rocio
 */
@Component
@Scope(value = "prototype")
public abstract class ServiceFeeRegisterTransaction extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(ServiceFeeRegisterTransaction.class);

	protected ServiceFeeRegisterTransaction() {
		setPending(true);
	}

	public abstract RegisterTransactionType getRegisterTransactionType();

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) {
		return;
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		return false;
	}

	@Override
	public RegisterTransaction onPostExecution(RegisterTransaction serviceTransaction) {
		logger.debug("[serviceFee] calling calculateRevenueEffectiveDate...");
		Assert.notNull(serviceTransaction);
		accountPricingService.saveServiceTransactionDeferredRevenueEffectiveDates((ServiceTransaction) serviceTransaction);
		return serviceTransaction;
	}
}
