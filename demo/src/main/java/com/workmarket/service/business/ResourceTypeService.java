package com.workmarket.service.business;

import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;

import java.util.List;

public interface ResourceTypeService {
	List<ResourceTypeRequirable> findAll();
}
