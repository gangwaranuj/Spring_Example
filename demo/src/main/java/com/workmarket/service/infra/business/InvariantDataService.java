package com.workmarket.service.infra.business;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.v2.model.NationalIdApiDTO;
import com.workmarket.domains.model.BlacklistedDomain;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.IndustryPagination;
import com.workmarket.domains.model.Language;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.LookupEntity;
import com.workmarket.domains.model.MobileProvider;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.CountryDTO;
import com.workmarket.service.business.dto.LocationTypeDTO;
import com.workmarket.service.business.dto.StateDTO;

import java.util.List;
import java.util.Map;

public interface InvariantDataService {

	Country findCountryById(String countryId);

	List<Country> getCountries();

	List<CountryDTO> getCountryDTOs();

	Map<String, String> getAllCountries();

	List<State> getStates();

	List<StateDTO> getStateDTOs();

	List<State> getStates(String countryId);

	String getStateCode(String keyword);

	State findState(String keyword);

	State findStateWithCountryAndState(String country, String state);

	PostalCode getPostalCodeByCodeCountryStateCity(String postalCode, String country, String state, String city);

	PostalCode getPostalCodeByCodeAndCountryId(String postalCode, String countryId);

	PostalCode getPostalCodeByCode(String postalCode);

	PostalCode getPostalCodeByCodeAndCounry(String postalCode, Country country);

	List<CallingCode> findAllActiveCallingCodes();

	ImmutableList<Map> getProjectedActiveCallingCodes(String[] fields) throws Exception;

	List<String> getAllUniqueActiveCallingCodeIds();

	CallingCode findCallingCodeFromID(Long id);

	CallingCode findCallingCodeFromCallingCodeId(String id);

	List<TimeZone> findAllActiveTimeZones();

	Map<TimeZone, String> findActiveTimeZonesWithShortNames();

	TimeZone findTimeZonesById(Long timeZoneId);

	TimeZone findTimeZonesByTimeZoneId(String timeZoneId);

	List<LocationTypeDTO> getLocationTypeDTOs();

	List<LocationType> getLocationTypes();

	List<DressCode> getDressCodes();

	Industry findIndustry(Long industryId);

	IndustryPagination findAllIndustries(IndustryPagination pagination);

	List<Language> getLanguages() throws Exception;

	List<? extends LookupEntity> getLanguageProficiencyTypes() throws Exception;

	List<MobileProvider> findAllMobileProviders() throws Exception;

	MobileProvider findMobileProvidersById(Long mobileProviderId);

	BankRouting getBankRouting(String routingNumber, String country);

	List<TaxEntityType> getTaxEntityTypes();

	List<String> getDaysOfWeek();

	List<String> getMonthsOfYear();

	List<BlacklistedDomain> getBlacklistedDomains();

	PostalCode findOrCreatePostalCode(AddressDTO addressDTO);

	/***
	 * Checks DB for record of postal code.
	 * Uses Google geocoder if not present. If Google is successful, saves the postal code to DB.
	 * Otherwise returns null.
	 * This method is preferable to findOrCreatePostalCode, where it's possible to save bad address data to the DB.
	 * This one only saves new postal codes after they have been verified by Google.
	 * @param addressDTO
	 * @return PostalCode
	 */
	PostalCode findOrSavePostalCode(AddressDTO addressDTO);

	/**
	 * Same functionality as findOrSavePostalCode(AddressDTO addressDTO) but accepts String postalCode
	 * @param postalCode
	 * @return
	 */
	PostalCode findOrSavePostalCode(String postalCode);

	List<NationalIdApiDTO> getAllNationalIds();
}
