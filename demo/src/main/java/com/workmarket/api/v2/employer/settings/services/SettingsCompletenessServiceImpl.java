package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.settings.models.SettingsCompletenessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsCompletenessServiceImpl implements SettingsCompletenessService {

	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public SettingsCompletenessDTO calculateCompletedPercentage() {
		return useCaseFactory
			.getUseCase(GetSettingsCompletenessUseCase.class)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
