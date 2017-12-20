package com.workmarket.dao.requirement;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.requirementset.AbstractRequirement;

public interface AbstractRequirementDAO extends DAOInterface<AbstractRequirement> {
	int getMandatoryRequirementCountByWorkId(Long workId);
}
