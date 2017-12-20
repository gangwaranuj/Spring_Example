package com.workmarket.dao.integration.autotask;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;

/**
 * Created by nick on 2012-12-23 9:27 AM
 */
public interface AutotaskUserDAO extends DAOInterface<AutotaskUser> {
	Optional<AutotaskUser> findUserByUserName(String username);

	Optional<AutotaskUser> findUserByUserId(Long userId);

	AutotaskUser findUserByCompanyId(Long companyId);

	void addUser(AutotaskUser autotaskUser);

	void removeUser(Long userId);
}
