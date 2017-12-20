package com.workmarket.dao.changelog.user;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.changelog.user.UserChangeLog;
import com.workmarket.domains.model.changelog.user.UserChangeLogPagination;

public interface UserChangeLogDAO<T extends UserChangeLog> extends DAOInterface<T> {

	UserChangeLogPagination findAllUserChangeLogByUserId(Long userId, UserChangeLogPagination pagination) throws Exception;

}
