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
public class AdHocServiceInvoicePayment extends InvoicePayment {

	private static final Log logger = LogFactory.getLog(AdHocServiceInvoicePayment.class);

	public AdHocServiceInvoicePayment() {
		setPending(false);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.AD_HOC_SERVICE_INVOICE_PAYMENT);
	}
}
