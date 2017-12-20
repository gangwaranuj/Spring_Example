package com.workmarket.domains.model.requirementset.availability;

import com.workmarket.domains.model.requirementset.Requirable;

public class WeekdayRequirable implements Requirable {
	private int id;
	private String name;

	public WeekdayRequirable() {}
	public WeekdayRequirable(Weekday weekday) {
		this.id = weekday.getId();
		this.name = weekday.getDescription();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
