package com.workmarket.dao.requirement;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.requirementset.RequirementSet;

public interface RequirementSetDAO extends DAOInterface<RequirementSet> {
	void merge(RequirementSet requirementSet);
	void save(RequirementSet requirementSet);
}
