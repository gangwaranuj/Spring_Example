package com.workmarket.domains.model.linkedin;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import com.google.code.linkedinapi.schema.Location;

@Embeddable
@Access(AccessType.FIELD)
public class LinkedInLocation implements Serializable {
	private static final long serialVersionUID = -6392474566329926582L;

	@Column(name="location_name")
	private String name;
	@Embedded
	private LinkedInCountry country;

	public LinkedInLocation() {
	}

	public LinkedInLocation(Location location) {
		this.name = location.getName();
		if(location.getCountry() != null)
			this.country = new LinkedInCountry(location.getCountry());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedInCountry getCountry() {
		return country;
	}

	public void setCountry(LinkedInCountry country) {
		this.country = country;
	}
}
