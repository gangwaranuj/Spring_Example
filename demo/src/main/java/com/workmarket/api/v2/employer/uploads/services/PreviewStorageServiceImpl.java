package com.workmarket.api.v2.employer.uploads.services;

import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.redis.PreviewStoreAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreviewStorageServiceImpl implements PreviewStorageService {
	public static final long A_DAY_IN_SECONDS = 60 * 60 * 24;
	@Autowired private PreviewStoreAdapter previewStoreAdapter;

	private static final String KEY = "uploads:%s";

	@Override
	public void add(String uuid, PreviewDTO previewDTO) {
		String key = getKey(uuid);
		previewStoreAdapter.add(key, previewDTO);
		previewStoreAdapter.expire(key, A_DAY_IN_SECONDS);
	}

	@Override
	public List<PreviewDTO> addAll(String uuid, List<PreviewDTO> previewDTOs) {
		String key = getKey(uuid);
		previewStoreAdapter.addAll(key, previewDTOs);
		previewStoreAdapter.expire(key, A_DAY_IN_SECONDS);
		return previewDTOs;
	}

	@Override
	public Optional<PreviewDTO> get(String uuid, long index) {
		return previewStoreAdapter.get(getKey(uuid), index);
	}

	@Override
	public List<PreviewDTO> get(String uuid, long startIndex, long endIndex) {
		return previewStoreAdapter.subList(getKey(uuid), startIndex, endIndex);
	}

	@Override
	public PreviewDTO set(String uuid, long index, PreviewDTO previewDTO) {
		String key = getKey(uuid);
		previewStoreAdapter.set(key, index, previewDTO);
		previewStoreAdapter.expire(key, A_DAY_IN_SECONDS);
		return previewDTO;
	}

	@Override
	public void destroy(String uuid) {
		previewStoreAdapter.destroy(getKey(uuid));
	}

	@Override
	public long size(String uuid) {
		return previewStoreAdapter.size(getKey(uuid));
	}

	@Override
	public boolean isEmpty(String uuid) {
		return previewStoreAdapter.isEmpty(getKey(uuid));
	}

	private String getKey(String uuid) {
		return String.format(KEY, uuid);
	}
}
