package com.workmarket.service.business.dto;

import java.io.Serializable;

public class CountryDTO implements Serializable {
	private static final long serialVersionUID = -5508187417407940267L;

	public final String id;
	public final String name;

	public CountryDTO(final String id, final String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
}
