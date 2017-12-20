package com.workmarket.service.business;

import com.workmarket.dao.requirement.CompanyTypeDAO;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyTypeServiceImpl implements CompanyTypeService {
	@Autowired private CompanyTypeDAO dao;

	@Override
	public List<CompanyTypeRequirable> findAll() {
		return dao.findAll();
	}
}
