package com.workmarket.domains.model.datetime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeZoneUtilities {
	/**
	 * This builds a map of timezone.id --> human readable description
	 * @param timezones
	 * @return
	 */
	public static Map<Long, String> getTimeZoneMapForDisplay(List<TimeZone> timezones) {
		Map<Long, String> result = new HashMap<Long, String>();

		if (timezones != null) for (TimeZone tz : timezones) {
			result.put(tz.getId(), tz.getTimeZoneId() + " " + tz.getName() );
		}
		return result;
	}
}
