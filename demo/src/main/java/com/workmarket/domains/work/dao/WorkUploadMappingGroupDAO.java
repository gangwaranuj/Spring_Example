package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.WorkUploadMappingGroup;
import com.workmarket.domains.work.model.WorkUploadMappingGroupPagination;

public interface WorkUploadMappingGroupDAO extends DAOInterface<WorkUploadMappingGroup> {
	WorkUploadMappingGroupPagination findByCompanyId(Long companyId, WorkUploadMappingGroupPagination pagination);
	WorkUploadMappingGroup findByMappingGroupId(Long mappingId);
}