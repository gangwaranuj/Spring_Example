package com.workmarket.dao.requirement;

import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;

import java.util.List;

public interface CompanyTypeDAO {
	List<CompanyTypeRequirable> findAll();
}
