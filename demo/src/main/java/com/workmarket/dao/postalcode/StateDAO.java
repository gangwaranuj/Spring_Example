package com.workmarket.dao.postalcode;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.postalcode.State;

import java.util.List;

public interface StateDAO extends DAOInterface<State> {

	List<State> findStates();

	List<State> findStates(String countryId);

	State findStateByShortName(String shortName);

	State findStateWithPostalCodeAndShortName(String postalCode, String shortName);

	State findStateWithCountryAndStateCode(String country, String state);

	State findStateWithCountryAndStateName(String country, String state);


}
