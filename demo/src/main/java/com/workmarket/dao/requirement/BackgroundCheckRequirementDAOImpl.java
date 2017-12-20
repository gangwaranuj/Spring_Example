package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class BackgroundCheckRequirementDAOImpl extends AbstractDAO<BackgroundCheckRequirement> implements BackgroundCheckRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return BackgroundCheckRequirement.class;
	}
}
