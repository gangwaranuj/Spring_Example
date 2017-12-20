package com.workmarket.domains.model.google;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

@Entity(name = "calendarSyncSettings")
@Table(name = "calendar_sync_settings")
@AuditChanges
public class CalendarSyncSettings extends AuditedEntity {
	public static String CONFIRMED = "confirmed";
	public static String CANCELLED = "cancelled";
	public static String TENTATIVE = "tentative";

	private User user;
	private RefreshToken refreshToken;
	private boolean newCalendar = false;
	private boolean deleted = false;
	private String calendarId;
	private String calendarName;

	public CalendarSyncSettings(){}

	public CalendarSyncSettings(User user, RefreshToken refreshToken, String calendarId, String calendarName, boolean newCalendar) {
		this.user = user;
		this.refreshToken = refreshToken;
		this.calendarId = calendarId;
		this.calendarName = calendarName;
		this.newCalendar = newCalendar;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "refresh_token_id", referencedColumnName = "id", updatable = false)
	public RefreshToken getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(RefreshToken refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Column(name = "new_calendar_flag", nullable = false, length = 1)
	public boolean isNewCalendar() {
		return newCalendar;
	}

	public void setNewCalendar(boolean newCalendar) {
		this.newCalendar = newCalendar;
	}

	@Column(name = "deleted", nullable = false, length = 1)
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Column(name = "calendar_id", nullable=false, length = 100)
	public String getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	@Column(name = "calendar_summary", nullable=false, length = 100)
	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}
}
