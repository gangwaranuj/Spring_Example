package com.workmarket.service.business.dto;

import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;

import java.util.Calendar;

public class UserAvailabilityDTO {

	private Integer weekDay;
	private Calendar fromTime;
	private Calendar toTime;
	private Boolean deleted = Boolean.FALSE;
	private Boolean allDayAvailable = Boolean.FALSE;

	public UserAvailabilityDTO() {}
	public UserAvailabilityDTO(Integer weekDay, Calendar fromTime, Calendar toTime, Boolean allDayAvailable) {
		this.weekDay = weekDay;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.allDayAvailable = allDayAvailable;
	}

	public static UserAvailabilityDTO newDTO(UserAvailability userAvailability) {
		return BeanUtilities.newBean(UserAvailabilityDTO.class, userAvailability);
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getActive() {
		return !deleted;
	}

	public void setActive(Boolean active) {
		this.deleted = !active;
	}

	public Integer getWeekDay() {
		return weekDay;
	}

	public String getWeekDayName() {
		return DateUtilities.getWeekdayName(weekDay);
	}

	public void setWeekDay(Integer weekDay) {
		this.weekDay = weekDay;
	}

	public Calendar getFromTime() {
		return fromTime;
	}

	public void setFromTime(Calendar fromTime) {
		this.fromTime = fromTime;
	}

	public Calendar getToTime() {
		return toTime;
	}

	public void setToTime(Calendar toTime) {
		this.toTime = toTime;
	}

	public void setAllDayAvailable(Boolean allDayAvailable) {
		this.allDayAvailable = allDayAvailable;
	}

	public boolean isAllDayAvailable() {
		return allDayAvailable;
	}

	public void setFromTimeFromMilitaryTime(Integer hours) {
		this.fromTime = DateUtilities.newCalendarFromMilitaryTime(hours);
	}

	public void setToTimeFromMilitaryTime(Integer hours) {
		this.toTime = DateUtilities.newCalendarFromMilitaryTime(hours);
	}
}
