package com.workmarket.service.business;

import com.workmarket.domains.model.changelog.company.CompanyChangeLogPagination;

public interface CompanyChangeLogService {
	CompanyChangeLogPagination findAllCompanyChangeLogsByCompanyId(Long companyId, CompanyChangeLogPagination pagination) throws Exception;
}
