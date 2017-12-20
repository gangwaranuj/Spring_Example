package com.workmarket.domains.work.dao;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.WorkUploadMapping;

public interface WorkUploadMappingDAO extends DAOInterface<WorkUploadMapping> {
	List<WorkUploadMapping> findByMappingGroupId(Long mappingGroupId);
}