package com.workmarket.service.thrift.work.uploader;

import com.google.api.client.util.Lists;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.ScheduleParserImpl;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * User: micah
 * Date: 4/21/14
 * Time: 2:45 PM
 */
@RunWith(Parameterized.class)
public class ScheduleParserTest {
	@InjectMocks private ScheduleParserImpl scheduleParser;

	@Mock private WorkUploaderBuildResponse response;
	@Mock private WorkUploaderBuildData buildData;
	@Spy  private Work work = new Work();

	private Map<String, String> types = new HashMap<>();
	private long now;
	private Schedule s;

	private static final String TIME_ZONE_NY = "America/New_York";
	private static final String TIME_ZONE_GMT = "GMT";
	private String tz;

	public ScheduleParserTest(String tz) {
		this.tz = tz;
	}

	@Parameters
	public static Collection<Object[]> generateData() {
		List<Object[]> l = Lists.newArrayList();
		l.add(new Object[] { TIME_ZONE_NY });
		l.add(new Object[] { TIME_ZONE_GMT });
		return l;
	}

	@Before
	public void setup() {
		// needed since not using MockitoJUnitRunner
		MockitoAnnotations.initMocks(this);
		when(buildData.getTypes()).thenReturn(types);
		when(response.getWork()).thenReturn(work);
		when(buildData.getWork()).thenReturn(work);
		when(work.getTimeZone()).thenReturn(tz);

		now = new Date().getTime();
		s = new Schedule();
		s.setFrom(now);
		work.setSchedule(s);
	}

	@Test
	public void build_NoSchedule() {
		work.setSchedule(null);

		scheduleParser.build(response, buildData);

		assertEquals(null, response.getWork().getSchedule());
	}

	@Test
	public void build_StartDate_NoTime_TemplateHasTime() {
		String dateStr = "1/1/2001";

		types.put(WorkUploadColumn.START_DATE.getUploadColumnName(), dateStr);

		scheduleParser.build(response, buildData);

		String expected = "01/01/2001 " + DateUtilities.formatMillis("hh:mmaa", now, tz);
		assertEquals(expected, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getFrom(), tz));
	}

	@Test
	public void build_StartTime_NoDate_TemplateHasDate() {
		String timeStr = "08:30AM";

		types.put(WorkUploadColumn.START_TIME.getUploadColumnName(), timeStr);

		scheduleParser.build(response, buildData);

		String expected = DateUtilities.formatMillis("MM/dd/yyyy", now, tz) + " " + timeStr;
		assertEquals(expected, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getFrom(), tz));
	}

	@Test
	public void build_StartDateAndTime_TemplateHasDateAndTime() {
		String dateStr = "1/1/2001";
		String timeStr = "08:30AM";

		types.put(WorkUploadColumn.START_DATE.getUploadColumnName(), dateStr);
		types.put(WorkUploadColumn.START_TIME.getUploadColumnName(), timeStr);

		scheduleParser.build(response, buildData);

		String expected = "01/01/2001 08:30AM";
		assertEquals(expected, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getFrom(), tz));
	}

	@Test
	public void build_StartAndEndDate_NoTime_TemplateHasTime() {
		String startDateStr = "1/1/2001";
		String endDateStr = "1/2/2001";

		s.setThrough(now);

		types.put(WorkUploadColumn.START_DATE.getUploadColumnName(), startDateStr);
		types.put(WorkUploadColumn.END_DATE.getUploadColumnName(), endDateStr);

		scheduleParser.build(response, buildData);

		String expectedStart = "01/01/2001 " + DateUtilities.formatMillis("hh:mmaa", now, tz);
		String expectedEnd = "01/02/2001 " + DateUtilities.formatMillis("hh:mmaa", now, tz);
		assertEquals(expectedStart, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getFrom(), tz));
		assertEquals(expectedEnd, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getThrough(), tz));
	}

	@Test
	public void build_StartAndEndTime_NoDate_TemplateHasDate() {
		String startTimeStr = "08:30AM";
		String endTimeStr = "09:00PM";

		s.setThrough(now);

		types.put(WorkUploadColumn.START_TIME.getUploadColumnName(), startTimeStr);
		types.put(WorkUploadColumn.END_TIME.getUploadColumnName(), endTimeStr);

		scheduleParser.build(response, buildData);

		String expectedStart = DateUtilities.formatMillis("MM/dd/yyyy", now, tz) + " " + startTimeStr;
		String expectedEnd = DateUtilities.formatMillis("MM/dd/yyyy", now, tz) + " " + endTimeStr;
		assertEquals(expectedStart, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getFrom(), tz));
		assertEquals(expectedEnd, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getThrough(), tz));
	}

	@Test
	public void build_StartAndEndDateAndTime_TemplateHasDateAndTime() {
		String startDateStr = "1/1/2001";
		String startTimeStr = "08:30AM";
		String endDateStr = "1/2/2001";
		String endTimeStr = "09:30AM";

		types.put(WorkUploadColumn.START_DATE.getUploadColumnName(), startDateStr);
		types.put(WorkUploadColumn.START_TIME.getUploadColumnName(), startTimeStr);
		types.put(WorkUploadColumn.END_DATE.getUploadColumnName(), endDateStr);
		types.put(WorkUploadColumn.END_TIME.getUploadColumnName(), endTimeStr);

		scheduleParser.build(response, buildData);

		String expectedStart = "01/01/2001 08:30AM";
		String expectedEnd = "01/02/2001 09:30AM";
		assertEquals(expectedStart, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getFrom(), tz));
		assertEquals(expectedEnd, DateUtilities.formatMillis("MM/dd/yyyy hh:mmaa", work.getSchedule().getThrough(), tz));
	}
}
