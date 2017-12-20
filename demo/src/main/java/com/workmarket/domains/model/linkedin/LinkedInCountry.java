package com.workmarket.domains.model.linkedin;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.google.code.linkedinapi.schema.Country;

@Embeddable
@Access(AccessType.FIELD)
public class LinkedInCountry implements Serializable {
	private static final long serialVersionUID = -8283312523046128107L;

	@Column(name="location_country_code")
	private String code;

	public LinkedInCountry() {
	}

	public LinkedInCountry(Country country) {
		this.code = country.getCode();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
