package com.workmarket.dao.postalcode;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;

public interface PostalCodeDAO extends DAOInterface<PostalCode> {

	public PostalCode findByPostalCode(String postalCode,Country country);

	public PostalCode findByPostalCodeCountryStateCity(String postalCode, String countryId, State state, String city);
}
