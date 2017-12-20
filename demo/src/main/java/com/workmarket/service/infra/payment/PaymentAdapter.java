package com.workmarket.service.infra.payment;

import com.workmarket.domains.model.account.authorization.TransactionAuthorizationAudit;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;

public interface PaymentAdapter {
	
	PaymentResponseDTO doCardPayment(PaymentDTO paymentDTO);
	
	PaymentResponseDTO doCardPaymentSkipAuth(PaymentDTO paymentDTO);

	PaymentResponseDTO auditCreditCardTransaction(PaymentResponseDTO paymentResponseDTO, TransactionAuthorizationAudit authorizationAudit);
}