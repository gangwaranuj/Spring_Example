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
public class BuyerAuthorizationImmediateWorkBundlePayment extends BuyerAuthorizationImmediateWorkPayment {

	private static final Log logger = LogFactory.getLog(BuyerAuthorizationImmediateWorkBundlePayment.class);


	public BuyerAuthorizationImmediateWorkBundlePayment() {
		setPending(Boolean.TRUE);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.BUYER_COMMITMENT_TO_PAY_WORK_BUNDLE);
	}
}
