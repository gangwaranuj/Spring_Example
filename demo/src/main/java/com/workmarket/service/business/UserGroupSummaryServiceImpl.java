package com.workmarket.service.business;

import com.workmarket.dao.summary.group.UserGroupSummaryDAO;
import com.workmarket.domains.model.summary.group.UserGroupSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: alexsilva Date: 3/10/14 Time: 5:49 PM
 */

@Service
public class UserGroupSummaryServiceImpl implements UserGroupSummaryService {

	@Autowired private UserGroupSummaryDAO userGroupSummaryDAO;

	@Override
	public void saveOrUpdate(UserGroupSummary userGroupSummary) {
		userGroupSummaryDAO.saveOrUpdate(userGroupSummary);
	}
}
