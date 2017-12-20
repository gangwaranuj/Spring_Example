package com.workmarket.service.business.requirementsets;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.requirementset.RequirementSet;

import java.util.List;
import java.util.Map;

public interface RequirementSetsService {
	List<RequirementSet> findAll();
	List<RequirementSet> findAllActive();
	RequirementSet find(Long id);
	void update(RequirementSet requirementSet);
	void save(RequirementSet requirementSet);
	void destroy(Long id);
	int getMandatoryRequirementCountByWorkId(Long workId);

	ImmutableList<Map> getProjectedRequirementSets(String[] fields) throws Exception;
}
