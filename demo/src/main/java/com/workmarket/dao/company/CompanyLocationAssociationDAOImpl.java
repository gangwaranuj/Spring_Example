package com.workmarket.dao.company;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.skill.CompanyLocationAssociation;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class CompanyLocationAssociationDAOImpl extends AbstractDAO<CompanyLocationAssociation> implements
	CompanyLocationAssociationDAO {

	protected Class<CompanyLocationAssociation> getEntityClass() {
		return CompanyLocationAssociation.class;
	}

	@Override
	public void addCompanyLocation(Location location, Company company) {
		Assert.notNull(location);
		Assert.notNull(company);

		CompanyLocationAssociation locationServiced = findCompanyLocationAssociation(location, company);

		if(locationServiced == null) {
			locationServiced = new CompanyLocationAssociation(company, location);
			saveOrUpdate(locationServiced);
		} else {
			locationServiced.setDeleted(false);
		}
	}

	@Override
	public void removeCompanyLocation(Location location, Company company) {
		Assert.notNull(location);
		Assert.notNull(company);

		CompanyLocationAssociation association = findCompanyLocationAssociation(location, company);

		Assert.notNull(association);

		association.setDeleted(true);
	}

	@Override
	public CompanyLocationAssociation findCompanyLocationAssociation(Location location, Company company) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("company", company))
			.add(Restrictions.eq("location", location));
		return (CompanyLocationAssociation)criteria.uniqueResult();
	}

	@Override
	public List<CompanyLocationAssociation> findCompanyLocationAssociations(Company company) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.createAlias("company", "company")
			.createAlias("location", "location")
			.add(Restrictions.eq("company", company))

			.add(Restrictions.eq("deleted", Boolean.FALSE))
			.add(Restrictions.eq("location.deleted", Boolean.FALSE));;
		return (List<CompanyLocationAssociation>)criteria.list();
	}


	public List<Location> findCompanyLocations(Company company) {
		List<Location> locations  = Lists.newArrayList();

		for(CompanyLocationAssociation locationServiced : findCompanyLocationAssociations(company))
			locations.add(locationServiced.getLocation());

		return locations;
	}
}
