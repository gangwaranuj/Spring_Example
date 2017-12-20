package com.workmarket.data.solr.model;

public class SolrTimeRange {
	private int fromTime;
	private int toTime;
	private boolean allDay;
	public int getFromTime() {
		return fromTime;
	}
	public void setFromTime(int fromTime) {
		this.fromTime = fromTime;
	}
	public int getToTime() {
		return toTime;
	}
	public void setToTime(int toTime) {
		this.toTime = toTime;
	}
	public boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allDay ? 1231 : 1237);
		result = prime * result + fromTime;
		result = prime * result + toTime;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolrTimeRange other = (SolrTimeRange) obj;
		if (allDay != other.allDay)
			return false;
		if (fromTime != other.fromTime)
			return false;
		if (toTime != other.toTime)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SolrTimeRange [fromTime=" + fromTime + ", toTime=" + toTime
				+ ", allDay=" + allDay + "]";
	}
	
}
