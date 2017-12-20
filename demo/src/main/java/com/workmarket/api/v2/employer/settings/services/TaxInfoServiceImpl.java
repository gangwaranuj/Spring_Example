package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxInfoServiceImpl implements TaxInfoService {

	@Autowired private UseCaseFactory useCaseFactory;

	@Override
	public TaxInfoDTO save(TaxInfoDTO taxInfoDTO) throws ValidationException {
		return useCaseFactory
			.getUseCase(CreateTaxInfoUseCase.class, taxInfoDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
