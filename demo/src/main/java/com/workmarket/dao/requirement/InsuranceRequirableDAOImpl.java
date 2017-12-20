package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirable;
import org.springframework.stereotype.Repository;

@Repository
public class InsuranceRequirableDAOImpl extends AbstractDAO<InsuranceRequirable> implements InsuranceRequirableDAO {
	@Override
	protected Class<?> getEntityClass() {
		return InsuranceRequirable.class;
	}
}
