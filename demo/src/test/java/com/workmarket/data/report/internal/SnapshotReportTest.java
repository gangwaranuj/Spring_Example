package com.workmarket.data.report.internal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by theogugoiu on 4/17/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class SnapshotReportTest {
	SnapshotReport snapshotReport = new SnapshotReport();

	@Before
	public void setUp(){
		snapshotReport = new SnapshotReport();
	}

	@Test
	public void testSnapshotReport_StartsWithZeroSnapshotDataPoint() {
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 0);
	}

	@Test
	public void testSnapshotReport_insertADataPoint() {
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 0);
		SnapshotReport.SnapshotDataPoint newDataPoint = new SnapshotReport.SnapshotDataPoint();
		newDataPoint.setMonth(1);
		newDataPoint.setYear(1991);
		snapshotReport.addDataAtYearAndMonth(newDataPoint, 1991, 1);
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 1);
	}

	@Test
	public void testSnapshotReport_insertTwoDataPointsAtSameTime() {
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 0);
		SnapshotReport.SnapshotDataPoint newDataPoint = new SnapshotReport.SnapshotDataPoint();
		newDataPoint.setMonth(1);
		newDataPoint.setYear(1991);
		snapshotReport.addDataAtYearAndMonth(newDataPoint, 1991, 1);
		SnapshotReport.SnapshotDataPoint newDataPointSameTime = new SnapshotReport.SnapshotDataPoint();
		newDataPointSameTime.setMonth(1);
		newDataPointSameTime.setYear(1991);
		snapshotReport.addDataAtYearAndMonth(newDataPointSameTime, 1991, 1);
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 1);
	}

	@Test
	public void testSnapshotReport_insertTwoDataPointsAtDifferentMonth() {
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 0);
		SnapshotReport.SnapshotDataPoint newDataPoint = new SnapshotReport.SnapshotDataPoint();
		newDataPoint.setMonth(1);
		newDataPoint.setYear(1991);
		snapshotReport.addDataAtYearAndMonth(newDataPoint, 1991, 1);
		SnapshotReport.SnapshotDataPoint newDataPointSameTime = new SnapshotReport.SnapshotDataPoint();
		newDataPointSameTime.setMonth(2);
		newDataPointSameTime.setYear(1991);
		snapshotReport.addDataAtYearAndMonth(newDataPointSameTime, 1991, 2);
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 2);
	}

	@Test
	public void testSnapshotReport_insertTwoDataPointsAtDifferentYear() {
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 0);
		SnapshotReport.SnapshotDataPoint newDataPoint = new SnapshotReport.SnapshotDataPoint();
		newDataPoint.setMonth(1);
		newDataPoint.setYear(1991);
		snapshotReport.addDataAtYearAndMonth(newDataPoint, 1991, 1);
		SnapshotReport.SnapshotDataPoint newDataPointSameTime = new SnapshotReport.SnapshotDataPoint();
		newDataPointSameTime.setMonth(1);
		newDataPointSameTime.setYear(2001);
		snapshotReport.addDataAtYearAndMonth(newDataPointSameTime, 2001, 1);
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 2);
	}

	@Test
	public void testSnapshotReport_insertTwoDataPointsAtSameTime_TestDataUpdated() {
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 0);
		SnapshotReport.SnapshotDataPoint newDataPoint = new SnapshotReport.SnapshotDataPoint();
		newDataPoint.setMonth(1);
		newDataPoint.setYear(1991);
		newDataPoint.setVoidRate(0.5);
		snapshotReport.addDataAtYearAndMonth(newDataPoint, 1991, 1);
		SnapshotReport.SnapshotDataPoint newDataPointSameTime = new SnapshotReport.SnapshotDataPoint();
		newDataPointSameTime.setMonth(1);
		newDataPointSameTime.setYear(1991);
		newDataPoint.setLifeCycleDays(100.0);
		snapshotReport.addDataAtYearAndMonth(newDataPointSameTime, 1991, 1);
		assertEquals(snapshotReport.getSnapshotDataPoints().size(), 1);
		assertEquals(snapshotReport.getSnapshotDataPoints().get(0).getVoidRate(), 0.5, 0.1);
		assertEquals(snapshotReport.getSnapshotDataPoints().get(0).getLifeCycleDays(), 100.0, 0.1);
	}
}
