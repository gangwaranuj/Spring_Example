package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class CreateBankAccountUseCase extends
	AbstractSettingsUseCase<CreateBankAccountUseCase, ACHBankAccountDTO> {

	public CreateBankAccountUseCase(ACHBankAccountDTO achBankAccountDTO) {
		this.achBankAccountDTO = achBankAccountDTO;
	}

	@Override
	protected CreateBankAccountUseCase me() {
		return this;
	}

	@Override
	protected CreateBankAccountUseCase handleExceptions() throws ValidationException {
		handleValidationException();
		handleBeansException();
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(achBankAccountDTO);
	}

	@Override
	protected void init() {
		getUserDetails();
	}

	@Override
	protected void prepare() {
		copyACHBankAccountDTO();
		validateBankAccount();
	}

	@Override
	protected void save() throws ValidationException {
		saveBankAccount();
	}

	@Override
	protected void finish() {
		loadACHBankAccountDTO();
	}

	@Override
	public ACHBankAccountDTO andReturn() {
		return achBankAccountDTOBuilder.build();
	}
}
