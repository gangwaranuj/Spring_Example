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
public class SubscriptionInvoicePayment extends InvoicePayment {

	private static final Log logger = LogFactory.getLog(SubscriptionInvoicePayment.class);

	public SubscriptionInvoicePayment() {
		setPending(false);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.SUBSCRIPTION_INVOICE_PAYMENT);
	}
}
