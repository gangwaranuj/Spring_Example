package com.workmarket.dao.summary.user;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.summary.user.UserSummary;

import java.util.Calendar;
import java.util.List;

/**
 * Author: rocio
 */
public interface UserSummaryDAO extends DAOInterface<UserSummary> {

	UserSummary findByUser(long userId);

	void saveOrUpdateUserLastAssignedDate(long userId, Calendar date);

	List<UserSummary> findAllUsersWithLastAssignedDateBetweenDates(Calendar fromDate, Calendar throughDate);
}
