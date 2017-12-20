package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.country.CountryRequirable;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRequirableDAOImpl extends AbstractDAO<CountryRequirable> implements CountryRequirableDAO {
	@Override
	protected Class<?> getEntityClass() {
		return CountryRequirable.class;
	}
}
