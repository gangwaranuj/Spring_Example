package com.workmarket.domains.model;

import com.workmarket.utility.DateUtilities;

public class DateRangeUtilities {
	public static DateRange getAppointmentTime(DateRange workSchedule, DateRange assignedResourceAppointment) {
		if (null == workSchedule && null == assignedResourceAppointment) {
			return null;
		}

		//old resource appointments may not be cleaned up, so make sure the appointment is still relevant
		return (assignedResourceAppointment != null && workSchedule != null && workSchedule.contains(assignedResourceAppointment)) ?
				new DateRange(assignedResourceAppointment) : new DateRange(workSchedule);
	}

	public static String format(String shortFormat, String longFormat, String separator, DateRange schedule, String timeZoneId) {
		if (schedule == null) return null;

		if (schedule.isRange()) {
			return String.format("%s%s%s",
					DateUtilities.format(shortFormat, schedule.getFrom(), timeZoneId),
					separator,
					DateUtilities.format(longFormat, schedule.getThrough(), timeZoneId)
			);
		}

		return DateUtilities.format(longFormat, schedule.getFrom(), timeZoneId);
	}

	public static DateRange getDateRange(long fromMillis, long throughMillis) {
		DateRange range = null;

		if (fromMillis > 0L) {
			if (throughMillis > 0L) {
				range = new DateRange(DateUtilities.getCalendarFromMillis(fromMillis), DateUtilities.getCalendarFromMillis(throughMillis));
			} else {
				range = new DateRange(DateUtilities.getCalendarFromMillis(fromMillis));
			}
		}

		return range;
	}
}
