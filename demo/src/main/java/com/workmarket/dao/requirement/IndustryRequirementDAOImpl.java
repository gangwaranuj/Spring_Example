package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class IndustryRequirementDAOImpl extends AbstractDAO<IndustryRequirement> implements IndustryRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return IndustryRequirement.class;
	}
}
