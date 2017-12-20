package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.rating.RatingRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class RatingRequirementDAOImpl extends AbstractDAO<RatingRequirement> implements RatingRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return RatingRequirement.class;
	}
}
