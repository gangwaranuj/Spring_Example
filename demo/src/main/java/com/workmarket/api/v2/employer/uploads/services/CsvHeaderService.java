package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.service.exception.HostServiceException;

import java.io.IOException;
import java.util.List;

public interface CsvHeaderService {
	List<MappingDTO> get(String uuid) throws HostServiceException, IOException;
	List<MappingDTO> get(String uuid, List<MappingDTO> adHocMappings) throws HostServiceException, IOException;
}
