package com.workmarket.service.business.upload.transactional;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.work.model.WorkUploadMappingGroup;
import com.workmarket.thrift.work.uploader.DeleteMappingRequest;
import com.workmarket.thrift.work.uploader.FieldMappingGroup;
import com.workmarket.thrift.work.uploader.FindMappingsRequest;
import com.workmarket.thrift.work.uploader.FindMappingsResponse;
import com.workmarket.thrift.work.uploader.RenameMappingRequest;
import com.workmarket.thrift.work.uploader.SaveMappingRequest;
import com.workmarket.thrift.work.uploader.WorkUploadDuplicateMappingGroupNameException;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;

import java.util.List;
import java.util.Map;

public interface WorkUploadMappingService {

	void deleteMapping(DeleteMappingRequest request) throws WorkUploadException;

	void renameMapping(RenameMappingRequest request) throws WorkUploadException;

	FindMappingsResponse findMappings(FindMappingsRequest request) throws WorkUploadException;

	FieldMappingGroup saveMapping(SaveMappingRequest request) throws WorkUploadException, WorkUploadDuplicateMappingGroupNameException;

	FieldMappingGroup getMappingGroupById(WorkUploadRequest request, List<String[]> assns, WorkUploadResponse response);

	FieldMappingGroup createMappingGroup(List<String[]> assns, Boolean isHeaderProvided);

	ImmutableList<Map> getProjectedMappings(String[] fields) throws Exception;

	WorkUploadMappingGroup getByMappingGroupId(Long id);

	WorkUploadMappingGroup saveMappingGroup(WorkUploadMappingGroup mappingGroup);
}
