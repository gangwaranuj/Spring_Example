package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

// TODO Update this entity to a LookupEntity vs. an AbstractEntity.
@Entity(name="location_type")
@Table(name="location_type")
public class LocationType extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final Long
		COMMERCIAL_CODE = 1L,
		RESIDENTIAL_CODE = 2L,
		GOVERNMENT_CODE = 3L,
		EDUCATION_CODE = 4L;

	public static final String
		COMMERCIAL = "commerical",
		RESIDENTIAL = "residential",
		GOVERNMENT = "government",
		EDUCATION = "education";

	private String description;
	
	@Column(name = "description", length = 45, nullable = false)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public static Long valueOf(String value) {
		if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
			Long numericValue = Long.valueOf(value);
			if (numericValue <= EDUCATION_CODE)
				return numericValue;
			return COMMERCIAL_CODE;
		}
		
		if (StringUtils.isNotEmpty(value)) {
			value = value.toUpperCase().trim();
			if (value.toUpperCase().equals("COMMERCIAL"))
				return COMMERCIAL_CODE;
			if (value.toUpperCase().equals("RESIDENTIAL"))
				return RESIDENTIAL_CODE;
			if (value.toUpperCase().equals("GOVERNMENT"))
				return GOVERNMENT_CODE;
			if (value.toUpperCase().equals("EDUCATION"))
				return EDUCATION_CODE;
		}
		return COMMERCIAL_CODE;
	}

	@Transient
	public static String getName(Long code) {

		if (COMMERCIAL_CODE.equals(code)) {
			return COMMERCIAL;
		}
		if (RESIDENTIAL_CODE.equals(code)) {
			return RESIDENTIAL;
		}
		if (GOVERNMENT_CODE.equals(code)) {
			return GOVERNMENT;
		}
		if (EDUCATION_CODE.equals(code)) {
			return EDUCATION;
		}
		return COMMERCIAL;
	}
}
