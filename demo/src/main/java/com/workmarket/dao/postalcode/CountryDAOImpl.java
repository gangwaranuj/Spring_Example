package com.workmarket.dao.postalcode;

import java.util.List;

import com.workmarket.domains.model.postalcode.Country;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;

@Repository
public class CountryDAOImpl extends AbstractDAO<Country> implements CountryDAO {

	@Override
	protected Class<Country> getEntityClass() {
		return Country.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Country> findCountries() {
		return getFactory().getCurrentSession().createQuery("from country order by name asc").list();
	}

	@Override
	public Country findCountryById(String countryId) {
		return (Country)getFactory().getCurrentSession().createQuery("select c from country c where id = :countryId")
			.setParameter("countryId", countryId).uniqueResult();
	}
	@Override
	public Country findCountryByIso(String countryIso) {
		return (Country)getFactory().getCurrentSession().createQuery("select c from country c where iso = :countryIso")
			.setParameter("countryIso", countryIso).uniqueResult();
	}
}
