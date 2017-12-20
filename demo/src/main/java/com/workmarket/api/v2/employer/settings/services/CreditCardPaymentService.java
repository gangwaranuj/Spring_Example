package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentResponseDTO;
import com.workmarket.service.exception.account.CreditCardErrorException;
import com.workmarket.thrift.core.ValidationException;

public interface CreditCardPaymentService {
	CreditCardPaymentResponseDTO addFunds(CreditCardPaymentDTO paymentDTO) throws ValidationException, CreditCardErrorException;
}
