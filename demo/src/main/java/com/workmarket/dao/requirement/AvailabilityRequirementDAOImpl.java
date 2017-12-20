package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.availability.AvailabilityRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class AvailabilityRequirementDAOImpl extends AbstractDAO<AvailabilityRequirement> implements AvailabilityRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return AvailabilityRequirement.class;
	}
}
