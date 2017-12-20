package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.models.ErrorsDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewsDTO;

public interface CsvPreviewsService {
	void create(String uuid);
	void create(String uuid, long index);
	void validate(String uuid, long index);
	PreviewsDTO get(String uuid);
	PreviewsDTO get(String uuid, long page, long size);
	ErrorsDTO getValidationErrors(String uuid);
	ErrorsDTO getValidationErrors(String uuid, long page, long size);
}
