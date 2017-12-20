package com.workmarket.domains.model.google;


import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.work.model.Work;

import javax.persistence.*;

@Entity(name = "workCalendarEvent")
@Table(name = "work_calendar_event")
public class WorkCalendarEvent extends AbstractEntity {

	private Work work;
	private CalendarSyncSettings calendarSyncSettings;

	private String eventId;
	private boolean deleted;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id", referencedColumnName = "id", updatable = false)
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_sync_settings_id", referencedColumnName = "id", updatable = false)
	public CalendarSyncSettings getCalendarSyncSettings() {
		return calendarSyncSettings;
	}

	public void setCalendarSyncSettings(CalendarSyncSettings calendarSyncSettings) {
		this.calendarSyncSettings = calendarSyncSettings;
	}

	@Column(name = "deleted", nullable=false, length=1)
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Column(name = "event_id", nullable=false, length = 100)
	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
}
