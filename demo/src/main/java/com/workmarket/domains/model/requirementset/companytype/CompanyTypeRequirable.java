package com.workmarket.domains.model.requirementset.companytype;

import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.domains.model.requirementset.Requirable;

public class CompanyTypeRequirable implements Requirable {

	private static final long serialVersionUID = 1201014086177607592L;

	private Long id;
	private String name;

	public CompanyTypeRequirable() {}
	public CompanyTypeRequirable(CompanyType companyType) {
		this.id = companyType.getId();
		this.name = companyType.getDescription();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
