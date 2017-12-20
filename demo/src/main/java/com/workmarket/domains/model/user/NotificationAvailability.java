package com.workmarket.domains.model.user;

import java.util.Calendar;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;

@Entity(name="notificationAvailability")
@DiscriminatorValue("notify")
@AuditChanges
public class NotificationAvailability extends UserAvailability {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_FROM_HOUR = 9;
	public static final int DEFAULT_TO_HOUR = 18;

	public NotificationAvailability() {}
	public NotificationAvailability(User user, Integer weekDay) {
		super(user, weekDay);
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User getUser() {
		return super.getUser();
	}
	public void setUser(User user) {
		super.setUser(user);
	}

	public static Calendar getDefaultFromTime(String timeZoneId) {
		return DateUtilities.getCalendarWithTime(DEFAULT_FROM_HOUR, 0, timeZoneId);
	}

	public static Calendar getDefaultToTime(String timeZoneId) {
		return DateUtilities.getCalendarWithTime(DEFAULT_TO_HOUR, 0, timeZoneId);
	}
}
