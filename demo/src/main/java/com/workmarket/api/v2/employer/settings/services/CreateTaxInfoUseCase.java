package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope("prototype")
public class CreateTaxInfoUseCase extends
	AbstractSettingsUseCase<CreateTaxInfoUseCase, TaxInfoDTO> {

	public CreateTaxInfoUseCase(TaxInfoDTO taxInfoDTO) {
		this.taxInfoDTO = taxInfoDTO;
	}

	@Override
	protected CreateTaxInfoUseCase me() {
		return this;
	}

	@Override
	protected CreateTaxInfoUseCase handleExceptions() throws ValidationException {
		handleValidationException();
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(taxInfoDTO);
	}

	@Override
	protected void init() {
		getUser();
		getCompany();
	}

	@Override
	protected void prepare() {
		copyTaxInfoDTO();
		prepareTaxInfoDTO();
		validateTaxInfo();
	}

	@Override
	protected void process() {
		setTaxEntityVerificationStatus();
	}

	@Override
	protected void save() throws ValidationException {
		saveTaxInfo();
	}

	@Override
	public TaxInfoDTO andReturn() {
		return taxInfoDTOBuilder.build();
	}
}
