package com.workmarket.dao.requirement;

import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;

import java.util.List;

public interface ResourceTypeDAO {
	List<ResourceTypeRequirable> findAll();
}
