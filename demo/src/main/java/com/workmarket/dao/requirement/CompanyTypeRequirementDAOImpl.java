package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyTypeRequirementDAOImpl extends AbstractDAO<CompanyTypeRequirement> implements CompanyTypeRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return CompanyTypeRequirement.class;
	}
}
