package com.workmarket.domains.work.model.negotiation;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;

import java.util.Calendar;

public interface ScheduleNegotiation {
	public Calendar getScheduleFrom();
	public Calendar getScheduleThrough();

	public Boolean getScheduleRangeFlag();
	public Boolean isScheduleNegotiation();

	public User getRequestedBy();
	public Work getWork();
}
