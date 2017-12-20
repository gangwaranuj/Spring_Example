package com.workmarket.service.business.dto;

import java.io.Serializable;

public class LocationTypeDTO implements Serializable {
	private static final long serialVersionUID = -1291567789986808092L;

	private final Long id;
	private final String description;

	public LocationTypeDTO(final Long id, final String description) {
		this.id = id;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
}
