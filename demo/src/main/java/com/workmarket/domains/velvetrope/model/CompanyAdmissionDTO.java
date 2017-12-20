package com.workmarket.domains.velvetrope.model;

import com.workmarket.domains.model.Company;

public class CompanyAdmissionDTO {

	private long companyId;
	private String companyName;
	private long admissionId;

	public CompanyAdmissionDTO(Company company, Admission admission) {
		this.companyId = company.getId();
		this.companyName = company.getName();
		this.admissionId = admission.getId();
	}
}
