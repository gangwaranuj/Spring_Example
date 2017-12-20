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

@Entity(name="workAvailability")
@DiscriminatorValue("work")
@AuditChanges
public class WorkAvailability extends UserAvailability {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_FROM_HOUR = 8;
	public static final int DEFAULT_FROM_MINUTE = 30;
	public static final int DEFAULT_TO_HOUR = 18;
	public static final int DEFAULT_TO_MINUTE = 30;

	public WorkAvailability() {}
	public WorkAvailability(User user, Integer weekDay) {
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
		return DateUtilities.getCalendarWithTime(DEFAULT_FROM_HOUR, DEFAULT_FROM_MINUTE, timeZoneId);
	}

	public static Calendar getDefaultToTime(String timeZoneId) {
		return DateUtilities.getCalendarWithTime(DEFAULT_TO_HOUR, DEFAULT_TO_MINUTE, timeZoneId);
	}
}
