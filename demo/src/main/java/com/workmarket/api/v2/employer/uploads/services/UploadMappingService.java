package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.models.MappingsDTO;
import com.workmarket.thrift.work.uploader.WorkUploadDuplicateMappingGroupNameException;
import com.workmarket.thrift.work.uploader.WorkUploadException;

public interface UploadMappingService {
	MappingsDTO create(MappingsDTO mappingsDTO) throws WorkUploadException, WorkUploadDuplicateMappingGroupNameException;
	MappingsDTO get(Long id);
	MappingsDTO update(Long id, MappingsDTO mappingsDTO);
	void delete(Long id) throws WorkUploadException;
}
