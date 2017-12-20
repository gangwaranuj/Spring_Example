package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.SettingsCompletenessDTO;

public interface SettingsCompletenessService {
	SettingsCompletenessDTO calculateCompletedPercentage();
}
