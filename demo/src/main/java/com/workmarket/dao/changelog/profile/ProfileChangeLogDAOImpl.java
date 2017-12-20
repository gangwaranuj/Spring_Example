package com.workmarket.dao.changelog.profile;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.changelog.profile.ProfileChangeLog;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileChangeLogDAOImpl extends AbstractDAO<ProfileChangeLog> implements ProfileChangeLogDAO {

	protected Class<ProfileChangeLog> getEntityClass() {
		return ProfileChangeLog.class;
	}

}
