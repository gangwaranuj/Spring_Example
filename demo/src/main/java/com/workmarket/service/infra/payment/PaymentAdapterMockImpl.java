package com.workmarket.service.infra.payment;

import com.workmarket.domains.model.account.authorization.CreditCardTransactionAuthorizationAudit;
import com.workmarket.domains.model.account.authorization.TransactionAuthorizationAudit;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;

import java.util.Calendar;

public class PaymentAdapterMockImpl implements PaymentAdapter {

	@Override
	public PaymentResponseDTO doCardPayment(PaymentDTO paymentDTO) {
		
		PaymentResponseDTO response = new PaymentResponseDTO();
		response.setApproved(true);
		CreditCardTransactionAuthorizationAudit authorizationAudit = new CreditCardTransactionAuthorizationAudit();
		authorizationAudit.setProviderName("mock");
		authorizationAudit.setTransactionDate(Calendar.getInstance());
		return auditCreditCardTransaction(response, authorizationAudit);
	}

	@Override
	public PaymentResponseDTO doCardPaymentSkipAuth(PaymentDTO paymentDTO) {
		PaymentResponseDTO response = new PaymentResponseDTO();
		response.setApproved(false);
		response.setResponseMessage("Mock error");
		CreditCardTransactionAuthorizationAudit authorizationAudit = new CreditCardTransactionAuthorizationAudit();
		authorizationAudit.setProviderName("mock");
		authorizationAudit.setTransactionDate(Calendar.getInstance());
		return auditCreditCardTransaction(response, authorizationAudit);
	}

	@Override
	public PaymentResponseDTO auditCreditCardTransaction(PaymentResponseDTO paymentResponseDTO, TransactionAuthorizationAudit authorizationAudit) {
		if (paymentResponseDTO != null) {
			paymentResponseDTO.setAuthorizationAudit(authorizationAudit);
		}
		return paymentResponseDTO;
	}
}