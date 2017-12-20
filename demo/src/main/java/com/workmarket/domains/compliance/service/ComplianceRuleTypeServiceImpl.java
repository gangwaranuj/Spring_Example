package com.workmarket.domains.compliance.service;

import com.workmarket.domains.compliance.dao.ComplianceRuleTypeDAO;
import com.workmarket.domains.compliance.model.ComplianceRuleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplianceRuleTypeServiceImpl implements ComplianceRuleTypeService {
	@Autowired private ComplianceRuleTypeDAO dao;

	@Override
	public List<ComplianceRuleType> findAll() { return dao.findAll(); }

	@Override
	public ComplianceRuleType findByName(String name) { return dao.findByName(name); }
}
