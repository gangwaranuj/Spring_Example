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
public class ServiceFeeWithdrawalReturn extends NonRecurringServiceFeeRegisterTransaction {

	private static final Log logger = LogFactory.getLog(ServiceFeeWithdrawalReturn.class);

	public ServiceFeeWithdrawalReturn() {
		super();
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.SERVICE_FEE_WITHDRAWAL_RETURN);
	}
}
