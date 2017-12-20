package com.workmarket.dao.postalcode;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.postalcode.Country;

public interface CountryDAO extends DAOInterface<Country> {

	public List<Country> findCountries();

	public Country findCountryById(String countryId);

	public Country findCountryByIso(String countryIso);

}
