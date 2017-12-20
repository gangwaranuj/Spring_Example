package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.country.CountryRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRequirementDAOImpl extends AbstractDAO<CountryRequirement> implements CountryRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return CountryRequirement.class;
	}
}
