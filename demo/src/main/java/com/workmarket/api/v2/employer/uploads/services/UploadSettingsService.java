package com.workmarket.api.v2.employer.uploads.services;

import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;

public interface UploadSettingsService {
	SettingsDTO create(String uuid, SettingsDTO settingsDTO);
	Optional<SettingsDTO> get(String uuid);
}
