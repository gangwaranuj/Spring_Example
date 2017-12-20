package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.license.LicenseRequirable;
import org.springframework.stereotype.Repository;

@Repository
public class LicenseRequirableDAOImpl extends AbstractDAO<LicenseRequirable> implements LicenseRequirableDAO {
	@Override
	protected Class<?> getEntityClass() {
		return LicenseRequirable.class;
	}
}
