package com.workmarket.dao.google;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.google.WorkCalendarEvent;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkCalendarEventDAOImpl extends AbstractDAO<WorkCalendarEvent> implements WorkCalendarEventDAO {

	@Override
	protected Class<WorkCalendarEvent> getEntityClass() {
		return WorkCalendarEvent.class;
	}

	@Override
	public List<WorkCalendarEvent> findByWork(Long workId) {
		return (List<WorkCalendarEvent>) getFactory().getCurrentSession().createCriteria(WorkCalendarEvent.class)
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}

	@Override
	public List<WorkCalendarEvent> findByCalendarSyncSettings(Long calendarSyncSettingsId) {
		return (List<WorkCalendarEvent>) getFactory().getCurrentSession().createCriteria(WorkCalendarEvent.class)
				.add(Restrictions.eq("calendarSyncSettings.id", calendarSyncSettingsId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}
}
