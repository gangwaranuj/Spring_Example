package com.workmarket.service.business.requirementsets;

import com.workmarket.domains.model.requirementset.RequirementType;

import java.util.List;

public interface RequirementTypesService {
	List<RequirementType> findAll() throws InstantiationException, IllegalAccessException, NoSuchFieldException;
}
