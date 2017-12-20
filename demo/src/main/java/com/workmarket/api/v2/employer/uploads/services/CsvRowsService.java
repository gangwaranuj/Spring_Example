package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.models.RowsDTO;
import com.workmarket.service.exception.HostServiceException;

import java.io.IOException;

public interface CsvRowsService {
	void create(String uuid) throws HostServiceException, IOException;
	RowsDTO get(String uuid);
	RowsDTO get(String uuid, long page, long size);
}
