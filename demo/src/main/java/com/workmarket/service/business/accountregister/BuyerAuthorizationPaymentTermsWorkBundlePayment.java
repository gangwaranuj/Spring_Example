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
public class BuyerAuthorizationPaymentTermsWorkBundlePayment extends BuyerAuthorizationPaymentTermsWorkPayment {

	private static final Log logger = LogFactory.getLog(BuyerAuthorizationPaymentTermsWorkBundlePayment.class);


	public BuyerAuthorizationPaymentTermsWorkBundlePayment() {
		setPending(Boolean.TRUE);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT_WORK_BUNDLE);
	}
}
