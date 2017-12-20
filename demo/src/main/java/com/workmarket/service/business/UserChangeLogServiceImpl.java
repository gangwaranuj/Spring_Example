package com.workmarket.service.business;

import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.domains.model.changelog.user.UserChangeLog;
import com.workmarket.domains.model.changelog.user.UserChangeLogPagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserChangeLogServiceImpl implements UserChangeLogService {

	@Autowired private UserChangeLogDAO userChangeLogDAO;

	@Override
	public UserChangeLogPagination findAllUserChangeLogsByUserId(Long userId, UserChangeLogPagination pagination) throws Exception {
		return userChangeLogDAO.findAllUserChangeLogByUserId(userId, pagination);
	}

	@Override
	public void createChangeLog(UserChangeLog changeLog){
		userChangeLogDAO.saveOrUpdate(changeLog);
	}

}
