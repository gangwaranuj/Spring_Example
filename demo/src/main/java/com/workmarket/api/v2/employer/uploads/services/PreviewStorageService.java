package com.workmarket.api.v2.employer.uploads.services;

import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;

import java.util.List;

public interface PreviewStorageService {
	void add(String uuid, PreviewDTO previewDTO);
	List<PreviewDTO> addAll(String uuid, List<PreviewDTO> previewDTOs);
	Optional<PreviewDTO> get(String uuid, long index);
	List<PreviewDTO> get(String uuid, long offset, long limit);
	PreviewDTO set(String uuid, long index, PreviewDTO previewDTO);
	void destroy(String uuid);
	long size(String uuid);
	boolean isEmpty(String uuid);
}
