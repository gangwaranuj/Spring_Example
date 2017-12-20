package com.workmarket.vault.services;

import com.google.common.base.Optional;
import com.workmarket.common.exceptions.BadRequestException;
import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.vault.models.VaultKeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VaultServerServiceRedisImpl implements VaultServerService {

	@Autowired private RedisAdapter redisAdapter;

	@Override
	public void remove(String key) throws ServiceUnavailableException {
		redisAdapter.delete(key);
	}

	@Override
	public void remove(List<VaultKeyValuePair> pairs) throws ServiceUnavailableException {
		for (VaultKeyValuePair pair : pairs) {
			remove(pair.getId());
		}
	}

	@Override
	public void post(VaultKeyValuePair pair) throws ServiceUnavailableException, BadRequestException {
		redisAdapter.set(pair.getId(), pair.getValue());
	}

	@Override
	public void post(List<VaultKeyValuePair> pairs) throws ServiceUnavailableException, BadRequestException {
		for (VaultKeyValuePair pair : pairs) {
			post(pair);
		}
	}

	@Override
	public VaultKeyValuePair get(String key) throws ServiceUnavailableException {
		Optional<Object> keyValuePairOptional = redisAdapter.get(key);
		if (!keyValuePairOptional.isPresent()) {
			return new VaultKeyValuePair();
		}

		return new VaultKeyValuePair(key, (String)keyValuePairOptional.get());
	}

	@Override
	public List<VaultKeyValuePair> get(List<String> keys) {
		List<VaultKeyValuePair> results = new ArrayList<>();
		for (String key : keys) {
			try {
				results.add(get(key));
			} catch (ServiceUnavailableException e) {
				return new ArrayList<>();
			}
		}
		return results;
	}
}
