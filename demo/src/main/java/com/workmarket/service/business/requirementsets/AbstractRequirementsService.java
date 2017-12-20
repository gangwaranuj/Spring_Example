package com.workmarket.service.business.requirementsets;

import com.workmarket.domains.model.requirementset.AbstractRequirement;

import java.util.List;

public interface AbstractRequirementsService {
	List<AbstractRequirement> findAllByRequirementSetId(long requirementSetId);
}
