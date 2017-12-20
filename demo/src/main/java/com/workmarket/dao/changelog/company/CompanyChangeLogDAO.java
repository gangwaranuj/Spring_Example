package com.workmarket.dao.changelog.company;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.changelog.company.CompanyChangeLog;
import com.workmarket.domains.model.changelog.company.CompanyChangeLogPagination;


public interface CompanyChangeLogDAO extends DAOInterface<CompanyChangeLog>
{
    CompanyChangeLogPagination findAllCompanyChangeLogsByCompanyId(Long companyId, CompanyChangeLogPagination pagination) throws Exception;
}
