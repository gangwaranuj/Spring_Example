package com.workmarket.dao.google;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.google.CalendarSyncSettings;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class CalendarSyncSettingsDAOImpl extends AbstractDAO<CalendarSyncSettings> implements CalendarSyncSettingsDAO {

	@Override
	protected Class<CalendarSyncSettings> getEntityClass() {
		return CalendarSyncSettings.class;
	}

	@Override
	public CalendarSyncSettings findByUser(Long userId) {
		return (CalendarSyncSettings) getFactory().getCurrentSession().createCriteria(CalendarSyncSettings.class)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", false)).setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public CalendarSyncSettings findByUserAndDeleted(Long userId) {
		return (CalendarSyncSettings) getFactory().getCurrentSession().createCriteria(CalendarSyncSettings.class)
				.add(Restrictions.eq("user.id", userId))
				.uniqueResult();
	}
}
