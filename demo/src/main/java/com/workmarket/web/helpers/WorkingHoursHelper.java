package com.workmarket.web.helpers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.model.user.WorkAvailability;
import com.workmarket.service.business.dto.UserAvailabilityDTO;
import com.workmarket.web.forms.HoursForm;

import java.util.*;

public class WorkingHoursHelper {
	public static HoursForm getForm(Collection<? extends UserAvailability> workingHours, String timeZone) {

		Map<Integer, UserAvailabilityDTO> userWorkingHours = Maps.newTreeMap();

		for (UserAvailability availability : workingHours) {
			UserAvailabilityDTO dto = new UserAvailabilityDTO(
					availability.getWeekDay(),
					availability.getFromTime(),
					availability.getToTime(),
					availability.isAllDayAvailable()
			);
			dto.setDeleted(availability.getDeleted());
			userWorkingHours.put(availability.getWeekDay(), dto);
		}

		Set<Integer> delta = Sets.newHashSet(
				UserAvailability.SUNDAY,
				UserAvailability.MONDAY,
				UserAvailability.TUESDAY,
				UserAvailability.WEDNESDAY,
				UserAvailability.THURSDAY,
				UserAvailability.FRIDAY,
				UserAvailability.SATURDAY
		);

		delta.removeAll(userWorkingHours.keySet()); // ex: if Mon-Fri is populated, we need to account for missed Sat and Sun

		Calendar defaultFrom= WorkAvailability.getDefaultFromTime(timeZone);
		Calendar defaultThrough = WorkAvailability.getDefaultToTime(timeZone);

		if (!delta.isEmpty()) {
			for (Integer weekDay : delta) {
				UserAvailabilityDTO dto = new UserAvailabilityDTO(weekDay, defaultFrom, defaultThrough, Boolean.FALSE);
				dto.setActive(Boolean.FALSE);
				userWorkingHours.put(weekDay, dto);
			}
		}

		return new HoursForm(userWorkingHours);
	}
}
