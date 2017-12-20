package com.workmarket.api.v2.employer.uploads.services;

import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.JsonSerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadSettingsServiceImpl implements UploadSettingsService {
	private static final String KEY = "uploads:%s:settings";

	@Autowired private RedisAdapter redisAdapter;
	@Autowired private JsonSerializationService jsonSerializationService;

	@Override
	public SettingsDTO create(String uuid, SettingsDTO settingsDTO) {
		redisAdapter.set(getKey(uuid), jsonSerializationService.toJson(settingsDTO));
		return settingsDTO;
	}

	@Override
	public Optional<SettingsDTO> get(String uuid) {
		Optional<Object> optional = redisAdapter.get(getKey(uuid));
		if (optional.isPresent()) {
			return Optional.of(jsonSerializationService.fromJson((String) optional.get(), SettingsDTO.Builder.class).build());
		}
		return Optional.absent();
	}

	private String getKey(String uuid) {
		return String.format(KEY, uuid);
	}
}
