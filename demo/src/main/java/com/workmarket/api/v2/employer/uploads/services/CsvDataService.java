package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.models.DataDTO;
import com.workmarket.api.v2.employer.uploads.models.ErrorsDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.service.exception.HostServiceException;

import java.io.IOException;
import java.util.List;

public interface CsvDataService {
	void create(String uuid) throws IOException, HostServiceException;
	void create(String uuid, List<MappingDTO> mappingDTOs) throws IOException, HostServiceException;
	void create(String uuid, List<MappingDTO> headers, long index) throws IOException, HostServiceException;
	void validate(String uuid, long index);
	DataDTO get(String uuid);
	DataDTO get(String uuid, long page, long size);
	ErrorsDTO getParsingErrors(String uuid);
	ErrorsDTO getParsingErrors(String uuid, long page, long size);
}
