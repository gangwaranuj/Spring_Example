package com.workmarket.service.infra.business;

import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;

public interface PaymentService {
	
	PaymentResponseDTO doCardPayment(PaymentDTO paymentDTO);
	
	PaymentResponseDTO doCardPaymentSkipAuth(PaymentDTO paymentDTO);

	PaymentResponseDTO auditPaymentTransaction(PaymentResponseDTO paymentResponseDTO);
}
