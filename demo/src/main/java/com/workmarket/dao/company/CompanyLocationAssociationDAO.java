package com.workmarket.dao.company;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.skill.CompanyLocationAssociation;

import java.util.List;

public interface CompanyLocationAssociationDAO extends DAOInterface<CompanyLocationAssociation>{

	@SuppressWarnings("unchecked")

	List<Location> findCompanyLocations(Company company);

	void addCompanyLocation(Location location, Company company);

	void removeCompanyLocation(Location location, Company company);

	CompanyLocationAssociation findCompanyLocationAssociation(Location location, Company company);

	List<CompanyLocationAssociation> findCompanyLocationAssociations(Company company);

}
