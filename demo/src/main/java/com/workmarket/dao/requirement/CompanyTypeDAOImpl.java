package com.workmarket.dao.requirement;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyTypeDAOImpl implements CompanyTypeDAO {
	private final List<CompanyTypeRequirable> types = Lists.newArrayList();
	private boolean loaded = false;

	@Override
	public List<CompanyTypeRequirable> findAll() {
		this.loadTypes();
		return this.types;
	}

	private void loadTypes() {
		if (this.loaded) {return;} // Should only load once
		for (CompanyType type : CompanyType.values()) {
			this.types.add(new CompanyTypeRequirable(type));
		}
		this.loaded = true;
	}
}
