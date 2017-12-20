package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.RegisterTransaction;
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
public abstract class NonRecurringServiceFeeRegisterTransaction extends ServiceFeeRegisterTransaction {

	private static final Log logger = LogFactory.getLog(NonRecurringServiceFeeRegisterTransaction.class);

	protected NonRecurringServiceFeeRegisterTransaction() {
		super();
	}

	@Override
	public RegisterTransaction onPostExecution(RegisterTransaction serviceTransaction) {
		logger.debug("[serviceFee] calling calculateRevenueEffectiveDate...");
		Assert.notNull(serviceTransaction);
		accountPricingService.saveServiceTransactionImmediateRevenueEffectiveDate((ServiceTransaction) serviceTransaction);
		return serviceTransaction;
	}
}
