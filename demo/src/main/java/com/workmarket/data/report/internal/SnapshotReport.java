package com.workmarket.data.report.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by theogugoiu on 4/10/14.
 */

public class SnapshotReport {
	private Calendar fromDate;
	private Calendar toDate;
	private List<SnapshotDataPoint> snapshotDataPoints = new ArrayList<>();

	public List<SnapshotDataPoint> getSnapshotDataPoints() {
		return snapshotDataPoints;
	}

	public void setSnapshotDataPoints(List<SnapshotDataPoint> snapshotDataPoints) {
		this.snapshotDataPoints = snapshotDataPoints;
	}

	public void addDataPoint(SnapshotDataPoint dataPoint){
		snapshotDataPoints.add(dataPoint);
	}

	public boolean addDataAtYearAndMonth(SnapshotDataPoint dataPoint, int year, int month){
		for (SnapshotDataPoint dp : snapshotDataPoints){

			if (dp.getYear() == year && dp.getMonth() == month){
				dp.setAssignmentsSent(dp.getAssignmentsSent() + dataPoint.getAssignmentsSent());
				dp.setAssignmentsSentCount(dp.getAssignmentsSentCount() + dataPoint.getAssignmentsSentCount());
				dp.setVoidRate(dp.getVoidRate() + dataPoint.getVoidRate());
				dp.setLifeCycleDays(dp.getLifeCycleDays() + dataPoint.getLifeCycleDays());
				dp.setTimeInMillis(dataPoint.getTimeInMillis());
				return true;
			}
		}
		dataPoint.setMonth(month);
		dataPoint.setYear(year);
		addDataPoint(dataPoint);

		return false;
	}

	public Calendar getFromDate() {
		return fromDate;
	}

	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}

	public Calendar getToDate() {
		return toDate;
	}

	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}

	public static class SnapshotDataPoint{

		private Double assignmentsSent = 0.0;
		private Double assignmentsSentCount = 0.0;
		private Double voidRate = 0.0;
		private Double lifeCycleDays = 0.0;
		private int year;
		private int month;
		private long timeInMillis;

		public long getTimeInMillis() {
			return timeInMillis;
		}

		public void setTimeInMillis(long timeInMillis) {
			this.timeInMillis = timeInMillis;
		}

		public Double getAssignmentsSent() {
			return assignmentsSent;
		}

		public void setAssignmentsSentCount(Double assignmentsSentCount) {
			this.assignmentsSentCount = assignmentsSentCount;
		}

		public Double getAssignmentsSentCount() {
			return assignmentsSentCount;
		}

		public void setAssignmentsSent(Double assignmentsSent) {
			this.assignmentsSent = assignmentsSent;
		}

		public Double getVoidRate() {
			return voidRate;
		}

		public void setVoidRate(Double voidRate) {
			this.voidRate = voidRate;
		}

		public Double getLifeCycleDays() {
			return lifeCycleDays;
		}

		public void setLifeCycleDays(Double lifeCycleDays) {
			this.lifeCycleDays = lifeCycleDays;
		}

		public int getYear() { return year; }

		public void setYear(int year) { this.year = year; }

		public int getMonth() { return month;}

		public void setMonth(int month) { this.month = month; }
	}
}
