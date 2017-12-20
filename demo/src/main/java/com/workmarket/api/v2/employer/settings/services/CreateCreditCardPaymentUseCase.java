package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentResponseDTO;
import com.workmarket.service.exception.account.CreditCardErrorException;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class CreateCreditCardPaymentUseCase
	extends AbstractSettingsUseCase<CreateCreditCardPaymentUseCase, CreditCardPaymentResponseDTO> {

	public CreateCreditCardPaymentUseCase(CreditCardPaymentDTO creditCardPaymentDTO) {
		this.creditCardPaymentDTO = creditCardPaymentDTO;
	}

	@Override
	protected CreateCreditCardPaymentUseCase me() {
		return this;
	}

	@Override
	protected CreateCreditCardPaymentUseCase handleExceptions() throws ValidationException, CreditCardErrorException {
		handleValidationException();
		handleCreditCardErrorException();
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(creditCardPaymentDTO);
	}

	@Override
	protected void init() {
		getUser();
		getCompany();
	}

	@Override
	protected void prepare() {
		copyCreditCardPaymentDTO();
		validateCreditCardPayment();
		validateCreditCardBillingAddress();
	}

	@Override
	protected void process() throws ValidationException, CreditCardErrorException {
		processCreditCardPayment();
	}

	@Override
	protected void finish() {
		loadPaymentResponseDTO();
	}

	@Override
	public CreditCardPaymentResponseDTO andReturn() {
		return creditCardPaymentResponseDTO;
	}
}
