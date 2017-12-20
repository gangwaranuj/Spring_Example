/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.RegisterTransactionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class SubscriptionSetupFeeTransaction extends ServiceFeeRegisterTransaction {

	private static final Log logger = LogFactory.getLog(SubscriptionSetupFeeTransaction.class);

	public SubscriptionSetupFeeTransaction() {
		super();
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.SUBSCRIPTION_SETUP_FEE_PAYMENT);
	}
}
