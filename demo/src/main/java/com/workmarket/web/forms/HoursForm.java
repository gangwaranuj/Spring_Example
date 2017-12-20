package com.workmarket.web.forms;

import com.workmarket.service.business.dto.UserAvailabilityDTO;

import java.io.Serializable;
import java.util.Map;

public class HoursForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<Integer,UserAvailabilityDTO> workingHours;

	public HoursForm() {}
	public HoursForm(Map<Integer, UserAvailabilityDTO> workingHours) {
		this.workingHours = workingHours;
	}

	public Map<Integer, UserAvailabilityDTO> getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(Map<Integer, UserAvailabilityDTO> workingHours) {
		this.workingHours = workingHours;
	}
}