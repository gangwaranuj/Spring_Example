package com.workmarket.dao.requirement;

import com.workmarket.domains.model.requirementset.RequirementType;

import java.util.List;

public interface RequirementTypeDAO {
	List<RequirementType> findAll() throws InstantiationException, IllegalAccessException, NoSuchFieldException;
	RequirementType findByName(String name);
}
