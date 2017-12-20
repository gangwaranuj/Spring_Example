package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class BuyerOfflineWorkPayment extends BuyerWorkPayment {

	private static final Log logger = LogFactory.getLog(BuyerOfflineWorkPayment.class);

	public BuyerOfflineWorkPayment() {
		setPending(Boolean.FALSE);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.BUYER_OFFLINE_WORK_PAYMENT);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) {
		// Overriding method from parent class to avoid updating account register
	}
}
