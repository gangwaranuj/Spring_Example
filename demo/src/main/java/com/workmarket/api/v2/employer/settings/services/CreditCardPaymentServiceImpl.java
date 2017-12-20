package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentResponseDTO;
import com.workmarket.service.exception.account.CreditCardErrorException;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditCardPaymentServiceImpl implements CreditCardPaymentService {

	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public CreditCardPaymentResponseDTO addFunds(CreditCardPaymentDTO creditCardPaymentDTO) throws ValidationException, CreditCardErrorException {
		return useCaseFactory
			.getUseCase(CreateCreditCardPaymentUseCase.class, creditCardPaymentDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
