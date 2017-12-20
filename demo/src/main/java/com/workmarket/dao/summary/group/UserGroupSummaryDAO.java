package com.workmarket.dao.summary.group;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.summary.group.UserGroupSummary;

/**
 * User: ianha
 * Date: 11/24/13
 * Time: 10:03 PM
 */
public interface UserGroupSummaryDAO extends DAOInterface<UserGroupSummary> {

	UserGroupSummary findByUserGroup(long userGroupId);
}
