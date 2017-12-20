package com.workmarket.dao.google;


import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.google.WorkCalendarEvent;

import java.util.List;

public interface WorkCalendarEventDAO extends DAOInterface<WorkCalendarEvent> {
	public List<WorkCalendarEvent> findByWork(Long workId);

	public List<WorkCalendarEvent> findByCalendarSyncSettings(Long calendarSyncSettingsId);
}
