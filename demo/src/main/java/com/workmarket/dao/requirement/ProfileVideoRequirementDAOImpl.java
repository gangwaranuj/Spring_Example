package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileVideoRequirementDAOImpl extends AbstractDAO<ProfileVideoRequirement> implements ProfileVideoRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return ProfileVideoRequirement.class;
	}
}
