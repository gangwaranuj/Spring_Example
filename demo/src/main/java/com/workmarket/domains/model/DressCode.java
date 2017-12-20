package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang.StringUtils;

@Entity(name="dressCode")
@Table(name="dress_code")
@Deprecated
public class DressCode extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	public static final long BUSINESS_CASUAL = 1L;
	public static final long CASUAL = 2L;
	public static final long INDUSTRIAL = 3L;
	public static final long PROFESSIONAL = 4L;
	public static final long SUIT_AND_TIE = 5L;
	public static final long CASUAL_AND_NEAT = 6L;
	public static final long DOESNT_MATTER = 7L;
	
	private String description;
		
	@Column(name = "description", length = 50, nullable = false)
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
			if (numericValue <= DOESNT_MATTER) {
				return numericValue;
			}
			return BUSINESS_CASUAL;
		}
		
		if (StringUtils.isNotEmpty(value)) {
			value = value.toLowerCase().replaceAll(" ", "");
			switch (value) {
				case "businesscasual":
					return BUSINESS_CASUAL;
				case "casual":
					return CASUAL;
				case "industrial":
					return INDUSTRIAL;
				case "professional":
					return PROFESSIONAL;
				case "suitandtie":
					return SUIT_AND_TIE;
				case "casualandneat":
					return CASUAL_AND_NEAT;
				case "itjustdoesn'tmatter":
					return DOESNT_MATTER;
			}
		}
		return BUSINESS_CASUAL;
	}
}
