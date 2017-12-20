package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceTypeRequirementDAOImpl extends AbstractDAO<ResourceTypeRequirement> implements ResourceTypeRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return ResourceTypeRequirement.class;
	}
}
