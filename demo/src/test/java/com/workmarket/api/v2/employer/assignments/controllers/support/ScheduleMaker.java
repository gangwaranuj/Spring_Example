package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ScheduleMaker {
	public static final Property<ScheduleDTO, String> from = newProperty();
	public static final Property<ScheduleDTO, String> through = newProperty();
	private static final Property<ScheduleDTO, Boolean> range = newProperty();
	private final static String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mmaa";

	public static final Instantiator<ScheduleDTO> ScheduleDTO = new Instantiator<ScheduleDTO>() {
		@Override
		public ScheduleDTO instantiate(PropertyLookup<ScheduleDTO> lookup) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
			String defaultFrom = formatter.print(
				DateTime.now()
				.plusDays(1)
				.withHourOfDay(13)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0)
			);

			String defaultThrough = formatter.print(
				DateTime.now()
				.plusDays(1)
				.withHourOfDay(13)
				.withMinuteOfHour(30)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0)
			);

			return new ScheduleDTO.Builder()
				.setFrom(lookup.valueOf(from, defaultFrom))
				.setThrough(lookup.valueOf(through, defaultThrough))
				.setRange(lookup.valueOf(range, true))
				.build();
		}
	};
}
