package com.workmarket.service.business;

import com.workmarket.domains.model.changelog.user.UserChangeLog;
import com.workmarket.domains.model.changelog.user.UserChangeLogPagination;

public interface UserChangeLogService {
	UserChangeLogPagination findAllUserChangeLogsByUserId(Long userId, UserChangeLogPagination pagination) throws Exception;
	public void createChangeLog(UserChangeLog changeLog);
}
