package com.workmarket.service.thrift;

import com.workmarket.service.exception.HostServiceException;
import com.workmarket.thrift.work.uploader.*;

import java.util.List;

/**
 * Author: rocio
 */
public interface TWorkUploadService {

	List<FieldCategory> getFieldCategories();

	List<FieldCategory> getFieldCategoriesForUpload(WorkUploadRequest request, WorkUploadResponse response);

	List<FieldCategory> getFieldCategoriesForTemplate(long templateId);

	FindMappingsResponse findMappings(FindMappingsRequest request) throws WorkUploadException;

	FieldMappingGroup saveMapping(SaveMappingRequest request) throws WorkUploadException, WorkUploadDuplicateMappingGroupNameException;

	void deleteMapping(DeleteMappingRequest request) throws WorkUploadException;

	void renameMapping(RenameMappingRequest request) throws WorkUploadException;

	WorkUploadResponse uploadWorkPreview(WorkUploadRequest request)
			throws WorkUploadException, WorkUploadInvalidFileTypeException, WorkUploadRowLimitExceededException, HostServiceException;

	WorkUploadResponse uploadWork(WorkUploadRequest request)
			throws WorkUploadException, WorkUploadInvalidFileTypeException, WorkUploadRowLimitExceededException, HostServiceException;

}
