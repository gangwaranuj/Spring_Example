package com.workmarket.thrift.work;

import java.util.Calendar;

public class TimeTrackingRequest {

	private long workId;
	private Long timeTrackingId;
	private Calendar date;
	private Double latitude;
	private Double longitude;
	private Double distance;
	private String noteOnCheckOut;
	private boolean notifyOnCheckOut = true;

	public Calendar getDate() {
		return date;
	}

	public TimeTrackingRequest setDate(Calendar date) {
		this.date = date;
		return this;
	}

	public Double getDistance() {
		return distance;
	}

	public TimeTrackingRequest setDistance(Double distance) {
		this.distance = distance;
		return this;
	}

	public Double getLatitude() {
		return latitude;
	}

	public TimeTrackingRequest setLatitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}

	public Double getLongitude() {
		return longitude;
	}

	public TimeTrackingRequest setLongitude(Double longitude) {
		this.longitude = longitude;
		return this;
	}

	public Long getTimeTrackingId() {
		return timeTrackingId;
	}

	public TimeTrackingRequest setTimeTrackingId(Long timeTrackingId) {
		this.timeTrackingId = timeTrackingId;
		return this;
	}

	public long getWorkId() {
		return workId;
	}

	public TimeTrackingRequest setWorkId(long workId) {
		this.workId = workId;
		return this;
	}

	public String getNoteOnCheckOut() {
		return noteOnCheckOut;
	}

	public TimeTrackingRequest setNoteOnCheckOut(String noteOnCheckOut) {
		this.noteOnCheckOut = noteOnCheckOut;
		return this;
	}

	public boolean isNotifyOnCheckOut() {
		return notifyOnCheckOut;
	}

	public TimeTrackingRequest setNotifyOnCheckOut(boolean notifyOnCheckOut) {
		this.notifyOnCheckOut = notifyOnCheckOut;
		return this;
	}
}
