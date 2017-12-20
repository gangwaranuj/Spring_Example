package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class TravelDistanceRequirementDAOImpl extends AbstractDAO<TravelDistanceRequirement> implements TravelDistanceRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return TravelDistanceRequirement.class;
	}
}
