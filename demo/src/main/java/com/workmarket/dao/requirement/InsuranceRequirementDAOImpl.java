package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class InsuranceRequirementDAOImpl extends AbstractDAO<InsuranceRequirement> implements InsuranceRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return InsuranceRequirement.class;
	}
}
