package com.workmarket.service.business;

import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;

import java.util.List;

public interface CompanyTypeService {
	List<CompanyTypeRequirable> findAll();
}
