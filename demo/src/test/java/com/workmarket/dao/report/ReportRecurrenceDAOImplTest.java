package com.workmarket.dao.report;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.reporting.ReportRecurrence;
import com.workmarket.utility.CollectionUtilities;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by nick on 9/12/12 11:20 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportRecurrenceDAOImplTest {

	@Mock private SessionFactory sessionFactory;
	@Mock private HibernateTemplate hibernateTemplate;

	@InjectMocks private ReportRecurrenceDAOImpl dao;

	// server times, they are UTC
	DateTime utcDate = new DateTime(2012, 11, 11, 11, 11, 11, 11, DateTimeZone.UTC); // Sunday Nov 11 2012 11:11:11 AM
	DateTime pstDate = new DateTime(2012, 11, 11, 2, 0, 0, 0, DateTimeZone.UTC); // Sunday Nov 11 2012 2 AM

	String utc = DateTimeZone.UTC.getID();
	String pst = DateTimeZone.forID("America/Los_Angeles").toString();

	List<Map<String, Object>> utcResults = Lists.newArrayList(
			CollectionUtilities.newObjectMap(                 // daily
					"recurrence_type", ReportRecurrence.DAILY,
					"time_zone_id", utc,
					"daily_weekdays_only_flag", false,
					"report_id", 1
			),
			CollectionUtilities.newObjectMap(                 // daily weekdays only
					"recurrence_type", ReportRecurrence.DAILY,
					"time_zone_id", utc,
					"daily_weekdays_only_flag", true,
					"report_id", 2
			),
			CollectionUtilities.newObjectMap(                 // weekly valid
					"recurrence_type", ReportRecurrence.WEEKLY,
					"time_zone_id", utc,
					"weekly_days", 65,
					"report_id", 3
			),
			CollectionUtilities.newObjectMap(                 // weekly invalid
					"recurrence_type", ReportRecurrence.WEEKLY,
					"time_zone_id", utc,
					"weekly_days", 33,
					"report_id", 4
			),
			CollectionUtilities.newObjectMap(                 // monthly day of
					"recurrence_type", ReportRecurrence.MONTHLY,
					"time_zone_id", utc,
					"monthly_use_day_of_month_flag", 1,
					"monthly_frequency_day", 11,
					"report_id", 5
			),
			CollectionUtilities.newObjectMap(                 // monthly day of invalid
					"recurrence_type", ReportRecurrence.MONTHLY,
					"time_zone_id", utc,
					"monthly_use_day_of_month_flag", 1,
					"monthly_frequency_day", 12,
					"report_id", 6
			),
			CollectionUtilities.newObjectMap(                 // monthly ordinal
					"recurrence_type", ReportRecurrence.MONTHLY,
					"time_zone_id", utc,
					"monthly_use_day_of_month_flag", 0,
					"monthly_frequency_weekday", 7,         // Sunday
					"monthly_frequency_weekday_ordinal", 2,
					"report_id", 7
			),
			CollectionUtilities.newObjectMap(                 // monthly ordinal invalid
					"recurrence_type", ReportRecurrence.MONTHLY,
					"time_zone_id", utc,
					"monthly_use_day_of_month_flag", 0,
					"monthly_frequency_weekday", 6,
					"monthly_frequency_weekday_ordinal", 1,
					"report_id", 8
			)
	);

	List<Map<String, Object>> pstResults = Lists.newArrayList(
			CollectionUtilities.newObjectMap(                 // daily
					"recurrence_type", ReportRecurrence.DAILY,
					"time_zone_id", pst,
					"daily_weekdays_only_flag", false,
					"report_id", 9
			),
			CollectionUtilities.newObjectMap(                 // daily weekdays only
					"recurrence_type", ReportRecurrence.DAILY,
					"time_zone_id", pst,
					"daily_weekdays_only_flag", true,
					"report_id", 10
			),
			CollectionUtilities.newObjectMap(                 // weekly valid
					"recurrence_type", ReportRecurrence.WEEKLY,
					"time_zone_id", pst,
					"weekly_days", 33,
					"report_id", 11
			),
			CollectionUtilities.newObjectMap(                 // weekly invalid
					"recurrence_type", ReportRecurrence.WEEKLY,
					"time_zone_id", pst,
					"weekly_days", 65,
					"report_id", 12
			),
			CollectionUtilities.newObjectMap(                 // monthly day of invalid
					"recurrence_type", ReportRecurrence.MONTHLY,
					"time_zone_id", pst,
					"monthly_use_day_of_month_flag", 1,
					"monthly_frequency_day", 11,
					"report_id", 13
			),
			CollectionUtilities.newObjectMap(                 // monthly day of invalid
					"recurrence_type", ReportRecurrence.MONTHLY,
					"time_zone_id", pst,
					"monthly_use_day_of_month_flag", 1,
					"monthly_frequency_day", 12,
					"report_id", 14
			),
			CollectionUtilities.newObjectMap(                 // monthly ordinal invalid
					"recurrence_type", ReportRecurrence.MONTHLY,
					"time_zone_id", pst,
					"monthly_use_day_of_month_flag", 0,
					"monthly_frequency_weekday", 7,         // Sunday
					"monthly_frequency_weekday_ordinal", 2,
					"report_id", 15
			),
			CollectionUtilities.newObjectMap(                 // monthly ordinal valid
					"recurrence_type", ReportRecurrence.MONTHLY,
					"time_zone_id", pst,
					"monthly_use_day_of_month_flag", 0,
					"monthly_frequency_weekday", 6,          // Saturday
					"monthly_frequency_weekday_ordinal", 2,
					"report_id", 16
			)
	);


	@Before
	public void init() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		NamedParameterJdbcTemplate jdbcTemplate = mock(NamedParameterJdbcTemplate.class);

		String utcQuery = callBuildScheduledJobQuery(utcDate);
		when(jdbcTemplate.queryForList(utcQuery, new HashMap<String, Object>()))
				.thenReturn(utcResults);

		String pstQuery = callBuildScheduledJobQuery(pstDate);
		when(jdbcTemplate.queryForList(pstQuery, new HashMap<String, Object>()))
				.thenReturn(pstResults);

		dao.setJdbcTemplate(jdbcTemplate);
	}

	@Test
	public void findReportIdsByRecurringDateTimeTest() {
		List<Long> results = dao.findReportIdsByRecurringDateTime(utcDate);
		ArrayList<Long> correct = Lists.newArrayList(1L, 3L, 5L, 7L);
		Assert.assertArrayEquals(correct.toArray(), results.toArray());

		results = dao.findReportIdsByRecurringDateTime(pstDate);
		correct = Lists.newArrayList(9L, 11L, 16L);
		Assert.assertArrayEquals(correct.toArray(), results.toArray());
	}

	private String callBuildScheduledJobQuery(DateTime input) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = ReportRecurrenceDAOImpl.class.getDeclaredMethod("buildScheduledJobQuery", DateTime.class);
		method.setAccessible(true);
		return (String) method.invoke(dao, input);
	}
}
