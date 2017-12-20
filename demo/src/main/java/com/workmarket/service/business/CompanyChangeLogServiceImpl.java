package com.workmarket.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workmarket.dao.changelog.company.CompanyChangeLogDAO;
import com.workmarket.domains.model.changelog.company.CompanyChangeLogPagination;

@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CompanyChangeLogServiceImpl implements CompanyChangeLogService {

	@Autowired private CompanyChangeLogDAO companyChangeLogDAO;

	@Override
	public CompanyChangeLogPagination findAllCompanyChangeLogsByCompanyId(Long companyId, CompanyChangeLogPagination pagination) throws Exception {
		return companyChangeLogDAO.findAllCompanyChangeLogsByCompanyId(companyId, pagination);
	}
}
