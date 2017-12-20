package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class DrugTestRequirementDAOImpl extends AbstractDAO<DrugTestRequirement> implements DrugTestRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return DrugTestRequirement.class;
	}
}
