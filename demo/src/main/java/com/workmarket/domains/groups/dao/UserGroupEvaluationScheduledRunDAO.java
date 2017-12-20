package com.workmarket.domains.groups.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.groups.model.ScheduledRun;
import com.workmarket.domains.groups.model.UserGroupEvaluationScheduledRun;

public interface UserGroupEvaluationScheduledRunDAO extends DAOInterface<UserGroupEvaluationScheduledRun> {

	ScheduledRun findNextScheduledRunForActiveGroup(long userGroupId);

	ScheduledRun findNextFutureScheduledRunForActiveOrInactiveGroup(long userGroupId);
}
