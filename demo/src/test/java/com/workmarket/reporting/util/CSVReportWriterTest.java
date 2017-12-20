package com.workmarket.reporting.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.reporting.Entity;
import com.workmarket.domains.model.reporting.GenericField;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.reporting.format.BooleanFormat;
import com.workmarket.reporting.format.CurrencyFormat;
import com.workmarket.reporting.format.DateFormat;
import com.workmarket.reporting.format.StriptHtmlFormat;
import com.workmarket.reporting.query.AbstractReportingQuery;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.junit.Assert.assertTrue;

/**
 * Created by nick on 7/5/12 2:38 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CSVReportWriterTest {

	private HashMap<String, Entity> entityMap;
	private ReportRequestData entityRequest;
	private List<Map<String, GenericField>> rows;
	private Calendar now;

	@Before
	public void init() {

		final String field1Name = "flag";
		final String field2Name = "<a href=\"http://www.workmarket.com/assignments/details/1234567890\">1234567890</a>";
		final String field3Name = "date";
		final String field4Name = "nothing";
		final String field5Name = "money";

		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		Entity entity4 = new Entity();
		Entity entity5 = new Entity();

		entity1.setDisplayNameM(new HashMap<Locale, String>() {{ put(Locale.US, "A flag"); }});
		entity2.setDisplayNameM(new HashMap<Locale, String>() {{ put(Locale.US, "Work of some kind"); }});
		entity3.setDisplayNameM(new HashMap<Locale, String>() {{ put(Locale.US, "A date of significance"); }});
		entity4.setDisplayNameM(new HashMap<Locale, String>() {{ put(Locale.US, "This is empty"); }});
		entity5.setDisplayNameM(new HashMap<Locale, String>() {{ put(Locale.US, "Money please"); }});

		entity1.setKeyName(field1Name);
		entity2.setKeyName(field2Name);
		entity3.setKeyName(field3Name);
		entity4.setKeyName(field4Name);
		entity5.setKeyName(field5Name);

		entity1.setFormat(new BooleanFormat());
		entity2.setFormat(new StriptHtmlFormat());
		entity3.setFormat(new DateFormat());
		entity4.setFormat(new StriptHtmlFormat());
		entity5.setFormat(new CurrencyFormat("$0.00"));

		entityMap = new HashMap<String, Entity>();
		entityMap.put("MyFlag", entity1);
		entityMap.put("AWork", entity2);
		entityMap.put("TheDate", entity3);
		entityMap.put("AbsolutelyNothing", entity4);
		entityMap.put("SomeMoney", entity5);

		entityRequest = new ReportRequestData();

		Set<String> displayKeys = Sets.newHashSet();
		displayKeys.add("MyFlag");
		displayKeys.add("AWork");
		displayKeys.add("TheDate");
		displayKeys.add("AbsolutelyNothing");
		displayKeys.add("SomeMoney");
		entityRequest.setDisplayKeys(displayKeys);

		entityRequest.setReportName("Test");

		now = DateUtilities.getCalendarNow(Constants.WM_TIME_ZONE);

		final GenericField field1 = new GenericField(field1Name, 1);
		final GenericField field2 = new GenericField(field2Name, "1234567890");
		final GenericField field3 = new GenericField(field3Name, now);
		final GenericField field4 = new GenericField(field4Name, null);
		final GenericField field5 = new GenericField(field5Name, new BigDecimal(10.23D));

		final GenericField field3tz = new GenericField(AbstractReportingQuery.TIME_ZONE_ID, Constants.WM_TIME_ZONE);

		rows = Lists.newArrayList();
		rows.add(new HashMap<String, GenericField>() {{	put(field1Name, field1); }});
		rows.add(new HashMap<String, GenericField>() {{	put(field2Name, field2); }});
		rows.add(new HashMap<String, GenericField>() {{
			put(field3Name, field3);
			put(AbstractReportingQuery.TIME_ZONE_ID, field3tz);
		}});
		rows.add(new HashMap<String, GenericField>() {{	put(field4Name, field4); }});
		rows.add(new HashMap<String, GenericField>() {{	put(field5Name, field5); }});
	}

	@Test
	public void testBooleanFormatRow() throws Exception {
		String result = new CSVReportWriter(entityRequest, entityMap, ".", "test").formatRow(rows.get(0)).get(0);
		assertTrue(result.length() > 0);
		assertTrue("yes".equals(result));
	}

	@Test
	public void testStringFormatRow() throws Exception {
		String result = new CSVReportWriter(entityRequest, entityMap, ".", "test").formatRow(rows.get(1)).get(0);
		Assert.hasLength(result);
		Assert.isTrue("1234567890".equals(result));
	}

	@Test
	public void testDateFormatRow() throws Exception {
		String result = new CSVReportWriter(entityRequest, entityMap, ".", "test").formatRow(rows.get(2)).get(0);
		Assert.hasLength(result);
		Assert.isTrue(DateUtilities.format("yyyy-MM-dd HH:mm", now, TimeZone.getTimeZone(Constants.WM_TIME_ZONE).getID()).equals(result));
	}

	@Test
	public void testEmptyFormatRow() throws Exception {
		List<String> result = new CSVReportWriter(entityRequest, entityMap, ".", "test").formatRow(rows.get(3));
		Assert.isTrue(result.size() == 1 && StringUtils.isBlank(result.get(0)));
	}

	@Test
	public void testCurrencyFormatRow() throws Exception {
		String result = new CSVReportWriter(entityRequest, entityMap, ".", "test").formatRow(rows.get(4)).get(0);
		Assert.hasLength(result);
		Assert.isTrue("$10.23".equals(result));
	}
}
