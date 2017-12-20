package com.workmarket.dao.google;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.google.CalendarSyncSettings;

public interface CalendarSyncSettingsDAO extends DAOInterface<CalendarSyncSettings> {
	public CalendarSyncSettings findByUser(Long userId);
	public CalendarSyncSettings findByUserAndDeleted(Long userId);
}
