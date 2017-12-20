package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.license.LicenseRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class LicenseRequirementDAOImpl extends AbstractDAO<LicenseRequirement> implements LicenseRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return LicenseRequirement.class;
	}
}
