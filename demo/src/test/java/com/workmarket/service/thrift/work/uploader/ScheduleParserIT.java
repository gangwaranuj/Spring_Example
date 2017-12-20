package com.workmarket.service.thrift.work.uploader;

import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.ScheduleParser;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduleParserIT extends BaseServiceIT {

	@Autowired private ScheduleParser scheduleParser;

	@Test
	public void testAmbiguousValues() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE_TIME.getUploadColumnName(), "12/19/2011 2:00pm",
			WorkUploadColumn.START_DATE.getUploadColumnName(), "12/19/2011",
			WorkUploadColumn.START_TIME.getUploadColumnName(), "2:00pm"
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.END_DATE_TIME.getUploadColumnName(), "12/19/2011 2:00pm",
			WorkUploadColumn.END_DATE.getUploadColumnName(), "12/19/2011",
			WorkUploadColumn.END_TIME.getUploadColumnName(), "2:00pm"
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	@Ignore
	public void testEmptyValues() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE_TIME.getUploadColumnName(), ""
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE.getUploadColumnName(), "",
			WorkUploadColumn.START_TIME.getUploadColumnName(), ""
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.END_DATE_TIME.getUploadColumnName(), ""
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.END_DATE.getUploadColumnName(), "",
			WorkUploadColumn.END_TIME.getUploadColumnName(), ""
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	public void testUnparsableValues() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE_TIME.getUploadColumnName(), "121911 2:00:00"
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE.getUploadColumnName(), "121911",
			WorkUploadColumn.START_TIME.getUploadColumnName(), "2:00:00"
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.END_DATE_TIME.getUploadColumnName(), "121911 2:00:00"
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.END_DATE.getUploadColumnName(), "121911",
			WorkUploadColumn.END_TIME.getUploadColumnName(), "2:00:00"
		)));

		Assert.assertNull(response.getWork().getSchedule());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	@Ignore
	public void testValidValues() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE_TIME.getUploadColumnName(), "12/19/2011 2:00:00"
		)));

		Assert.assertNotNull(response.getWork().getSchedule());
		Assert.assertEquals(1325142000000L, response.getWork().getSchedule().getFrom());
		Assert.assertTrue(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE_TIME.getUploadColumnName(), "12-19-2011 2:00am"
		)));

		Assert.assertNotNull(response.getWork().getSchedule());
		Assert.assertEquals(1325142000000L, response.getWork().getSchedule().getFrom());
		Assert.assertTrue(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE_TIME.getUploadColumnName(), "2011-12-19 2:00"
		)));

		Assert.assertNotNull(response.getWork().getSchedule());
		Assert.assertEquals(1325142000000L, response.getWork().getSchedule().getFrom());
		Assert.assertTrue(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE.getUploadColumnName(), "12/19/2011",
			WorkUploadColumn.START_TIME.getUploadColumnName(), "2:00:00"
		)));

		Assert.assertNotNull(response.getWork().getSchedule());
		Assert.assertEquals(1325142000000L, response.getWork().getSchedule().getFrom());
		Assert.assertTrue(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE.getUploadColumnName(), "12-19-2011",
			WorkUploadColumn.START_TIME.getUploadColumnName(), "2:00am"
		)));

		Assert.assertNotNull(response.getWork().getSchedule());
		Assert.assertEquals(1325142000000L, response.getWork().getSchedule().getFrom());
		Assert.assertTrue(response.getErrors().isEmpty());

		response = new WorkUploaderBuildResponse(new Work());
		scheduleParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.START_DATE.getUploadColumnName(), "2011-12-29",
			WorkUploadColumn.START_TIME.getUploadColumnName(), "2:00"
		)));

		Assert.assertNotNull(response.getWork().getSchedule());
		Assert.assertEquals(1325142000000L, response.getWork().getSchedule().getFrom());
		Assert.assertTrue(response.getErrors().isEmpty());
	}
}
