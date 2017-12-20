package com.workmarket.dao.summary.user;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.summary.user.UserSummary;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;

/**
 * Author: rocio
 */
@Repository
public class UserSummaryDAOImpl extends AbstractDAO<UserSummary> implements UserSummaryDAO {

	@Override
	protected Class<UserSummary> getEntityClass() {
		return UserSummary.class;
	}

	@Override
	public UserSummary findByUser(long userId) {
		return (UserSummary) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("userId", userId)).uniqueResult();
	}

	@Override
	public void saveOrUpdateUserLastAssignedDate(long userId, Calendar date) {
		Assert.notNull(date);
		getFactory().getCurrentSession()
			.createSQLQuery("INSERT INTO user_summary (user_id, last_assigned_work_date) VALUES (:user_id, :last_assigned_work_date) ON DUPLICATE KEY UPDATE last_assigned_work_date = VALUES(last_assigned_work_date);")
			.setParameter("last_assigned_work_date", date)
			.setParameter("user_id", userId)
			.executeUpdate();
	}

	@Override
	public List<UserSummary> findAllUsersWithLastAssignedDateBetweenDates(Calendar fromDate, Calendar throughDate) {
		Assert.notNull(fromDate);
		Assert.notNull(throughDate);
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.ge("lastAssignedWorkDate", fromDate))
				.add(Restrictions.le("lastAssignedWorkDate", throughDate))
				.list();
	}
}
