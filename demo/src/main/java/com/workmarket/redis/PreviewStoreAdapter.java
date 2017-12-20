package com.workmarket.redis;

import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;

import java.util.List;

public interface PreviewStoreAdapter {
	void add(String key, PreviewDTO previewDTO);
	void addAll(String key, List<PreviewDTO> previewDTOs);
	Optional<PreviewDTO> get(String key, long index);
	List<PreviewDTO> subList(String key, long startIndex, long endIndex);
	void set(String key, long index, PreviewDTO previewDTO);
	void destroy(String key);
	long size(String key);
	boolean isEmpty(String key);
	void expire(String key, long seconds);
}
