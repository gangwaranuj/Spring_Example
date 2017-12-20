package com.workmarket.service.business.dto;

import java.io.Serializable;

public class StateDTO implements Serializable {
	private static final long serialVersionUID = 7695105414375342776L;

	public final String name;
	public final String shortName;
	private final String country;

	public StateDTO(final String name, final String shortName, final String country) {
		this.name = name;
		this.shortName = shortName;
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public String getCountry() {
		return country;
	}
}
