package com.workmarket.domains.model.linkedin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.google.code.linkedinapi.schema.Company;
import com.workmarket.configuration.Constants;

@Embeddable
public class LinkedInCompany implements Serializable {
	private static final long serialVersionUID = -158555763447671188L;

	private String type;
	private String name;
	private String industry;


	public LinkedInCompany() {
	}

	public LinkedInCompany(Company company) {
		this.type = company.getType();
		this.name = company.getName();
		this.industry = company.getIndustry();
	}

	@Column(name = "company_type", nullable = true, length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "company_name", nullable = true, length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "company_industry", nullable = true, length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}
}
