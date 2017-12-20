package com.workmarket.service.infra.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;
import com.workmarket.api.v2.model.NationalIdApiDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.BlacklistedDomainDAO;
import com.workmarket.dao.DressCodeDAO;
import com.workmarket.dao.LanguageDAO;
import com.workmarket.dao.LocationTypeDAO;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.banking.BankRoutingDAO;
import com.workmarket.dao.banking.BankingIntegrationGenerationRequestTypeDAO;
import com.workmarket.dao.callingcodes.CallingCodeDAO;
import com.workmarket.dao.datetime.TimeZoneDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.mobile.MobileProviderDAO;
import com.workmarket.dao.postalcode.CountryDAO;
import com.workmarket.dao.postalcode.PostalCodeDAO;
import com.workmarket.dao.postalcode.StateDAO;
import com.workmarket.dao.recruiting.RecruitingVendorDAO;
import com.workmarket.domains.model.BlacklistedDomain;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.IndustryPagination;
import com.workmarket.domains.model.Language;
import com.workmarket.domains.model.LanguageProficiencyType;
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
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.LocationService;
import com.workmarket.service.business.dto.CountryDTO;
import com.workmarket.service.business.dto.LocationTypeDTO;
import com.workmarket.service.business.dto.StateDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.ProjectionUtilities;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Service
public class InvariantDataServiceImpl implements InvariantDataService {

	@Autowired CountryDAO countryDAO;
	@Autowired StateDAO stateDAO;
	@Autowired PostalCodeDAO postalCodeDAO;
	@Autowired RecruitingVendorDAO recruitingVendorDAO;
	@Autowired LanguageDAO languageDAO;
	@Autowired LookupEntityDAO lookupEntityDAO;
	@Autowired BankRoutingDAO bankRoutingDAO;
	@Autowired IndustryDAO industryDAO;
	@Autowired CallingCodeDAO callingCodesDAO;
	@Autowired MobileProviderDAO mobileProviderDAO;
	@Autowired LocationTypeDAO locationTypeDAO;
	@Autowired BankingIntegrationGenerationRequestTypeDAO bankingIntegrationGenerationRequestTypeDAO;
	@Autowired TimeZoneDAO timeZoneDAO;
	@Autowired DressCodeDAO dressCodeDAO;
	@Autowired BlacklistedDomainDAO blacklistedDomainDAO;
	@Autowired LocationService locationService;
	@Autowired GeocodingService geocodingService;
	@Autowired RedisAdapter redisAdapter;

	public static final String
		UNIQUE_ACTIVE_CALLING_CODE_IDS = RedisConfig.UNIQUE_ACTIVE_CALLING_CODE_IDS,
		LOCATION_TYPES = RedisConfig.LOCATION_TYPES,
		STATES = RedisConfig.STATES,
		POSTAL_CODE_DISTANCE = RedisConfig.POSTAL_CODE_DISTANCE,
		COUNTRIES = RedisConfig.COUNTRIES,
		NATIONAL_IDS = RedisConfig.NATIONAL_IDS;

	private final static List<String> DAYSOFWEEK = new ImmutableList.Builder<String>()
		.add("Monday")
		.add("Tuesday")
		.add("Wednesday")
		.add("Thursday")
		.add("Friday")
		.add("Saturday")
		.add("Sunday")
		.build();

	private final static List<String> MONTHSOFYEAR = new ImmutableList.Builder<String>()
		.add("January")
		.add("February")
		.add("March")
		.add("April")
		.add("May")
		.add("June")
		.add("July")
		.add("August")
		.add("September")
		.add("October")
		.add("November")
		.add("December")
		.build();

	private static final Log logger = LogFactory.getLog(InvariantDataServiceImpl.class);

	@Override
	public List<Country> getCountries() {
		return countryDAO.findCountries();
	}

	@Override
	@Cacheable(
		value = COUNTRIES,
		key = "#root.target.COUNTRIES"
	)
	public List<CountryDTO> getCountryDTOs() {
		List<Country> countries = countryDAO.findCountries();
		List<CountryDTO> countryDTOs = Lists.newArrayListWithCapacity(countries.size());
		for (Country country : countries) {
			countryDTOs.add(new CountryDTO(country.getId(), country.getName()));
		}
		return countryDTOs;
	}

	@Override
	public Map<String, String> getAllCountries() {
		Map<String, String> countries = Maps.newHashMap();
		List<Locale> locales = LocaleUtils.availableLocaleList();
		for (Locale locale : locales) {
			try{
				countries.put(locale.getISO3Country(), locale.getDisplayCountry());
			} catch (MissingResourceException ex){
				//ToDo: some locales may cause exception like CS (Serbia and Montenegro) which was for some reason commented out in java 7
				ExceptionUtils.getMessage(ex);
			}
		}
		return CollectionUtilities.sortMapByValues(countries);
	}

	@Override
	public List<State> getStates() {
		return stateDAO.findStates();
	}

	@Override
	@Cacheable(
		value = STATES,
		key = "#root.target.STATES"
	)
	public List<StateDTO> getStateDTOs() {
		List<State> states = stateDAO.findStates();
		List<StateDTO> stateDTOs = Lists.newArrayListWithCapacity(states.size());
		for (State state : states) {
			stateDTOs.add(new StateDTO(state.getName(), state.getShortName(), state.getCountry().getName()));
		}
		return stateDTOs;
	}

	@Override
	public List<State> getStates(String countryId) {
		Assert.hasText(countryId);
		return stateDAO.findStates(countryId);
	}

	@Override
	public CallingCode findCallingCodeFromID(Long id) {
		Assert.notNull(id);
		return callingCodesDAO.findCallingCodeById(id);
	}

	@Override
	public CallingCode findCallingCodeFromCallingCodeId(String id) {
		Assert.notNull(id);
		return callingCodesDAO.findCallingCodeByCallingCodeId(id);
	}

	private PostalCode savePostalCode(AddressDTO addressDTO) {
		if (addressDTO.getLatitude() == null || addressDTO.getLongitude() == null) {
			logger.error("Attempt to create a new postal code with null Lat and/or Long: (" + addressDTO.getLatitude() + ", " + addressDTO.getLongitude() + ")");
			return null;
		}

		if (BigDecimal.ZERO.compareTo(addressDTO.getLatitude()) == 0 && BigDecimal.ZERO.compareTo(addressDTO.getLongitude()) == 0) {
			logger.error("Attempt to create a new postal code with zero value(s): (" + addressDTO.getLatitude() + ", " + addressDTO.getLongitude() + ")");
			return null;
		}

		final Country country = Country.valueOf(addressDTO.getCountry());
		PostalCode savedPostalCode = postalCodeDAO.findByPostalCode(
			isBlank(addressDTO.getPostalCode()) ? Constants.NO_POSTALCODE : trimAllWhitespace(addressDTO.getPostalCode()),
			country);
		if (savedPostalCode != null) {
			return savedPostalCode;
		}

		PostalCode postalCode = new PostalCode();
		postalCode.setAreaCode("000");
		postalCode.setCity(addressDTO.getCity());
		postalCode.setCountry(country);
		postalCode.setLatitude(addressDTO.getLatitude().doubleValue());
		postalCode.setLongitude(addressDTO.getLongitude().doubleValue());
		postalCode.setPostalCode(
			isBlank(addressDTO.getPostalCode()) ? Constants.NO_POSTALCODE : trimAllWhitespace(addressDTO.getPostalCode())
		);

		String countryCode = country.getId();
		String stateName;
		if (isBlank(addressDTO.getState())) {
			stateName = Constants.NO_STATE;
		} else {
			stateName = addressDTO.getState();
		}
		State state = findStateWithCountryAndState(countryCode, stateName);

		// We know state/province is valid due to geocoding, so save if not found
		if (state == null) {
			state = new State();
			state.setShortName(stateName);
			state.setName(stateName);
			state.setCountry(findCountryById(countryCode));
			stateDAO.saveOrUpdate(state);
		}

		postalCode.setStateProvince(findStateWithCountryAndState(Country.valueOf(addressDTO.getCountry()).getId(), stateName));

		String timeString = Constants.TIMESTAMP_DUMMY_VALUE;
		TimeZone theZone;
		try {
			theZone = locationService.requestTimeZone(
				addressDTO.getLatitude().doubleValue(),
				addressDTO.getLongitude().doubleValue(),
				timeString);
		} catch(Exception e) {
			theZone = timeZoneDAO.findTimeZonesByTimeZoneId(Constants.DEFAULT_TIMEZONE);
			logger.error("There was an error connecting to google timezone api: " + e);
		}
		postalCode.setTimeZone(theZone);

		postalCodeDAO.saveOrUpdate(postalCode);
		return postalCode;
	}

	@Override
	public PostalCode findOrCreatePostalCode(AddressDTO addressDTO) {
		Assert.notNull(addressDTO.getPostalCode());
		Point p = null;
		if (!addressDTO.isLatLongSet()) {
			// avoid saving invalid postal_code when geocoding not yet done
			p = locationService.geocode(addressDTO);
			if (p == null) {
				return null;
			}
		}

		String postalCodeStr =
			isBlank(addressDTO.getPostalCode()) ? Constants.NO_POSTALCODE : addressDTO.getPostalCode();

		// first check if postal code exists in the database note: using addressDTO postalCode if country is blank
		PostalCode postalCode = isNotBlank(addressDTO.getCountry()) ?
			getPostalCodeByCodeAndCountryId(postalCodeStr, addressDTO.getCountry()) :
			getPostalCodeByCode(addressDTO.getPostalCode());

		if (postalCode != null) { return postalCode; }

		// if the address is already geocoded, then save it
		if (addressDTO.isLatLongSet()) {
			return savePostalCode(addressDTO);
		}

		// second, try geo lookup by full address
		addressDTO.setLatitude(new BigDecimal(p.getY()));
		addressDTO.setLongitude(new BigDecimal(p.getX()));

		return savePostalCode(addressDTO);
	}

	// This method is preferable to findOrCreatePostalCode, where it's possible to save bad address data to the DB.
	// This one only saves postal codes that have been verified by Google.
	@Override
	public PostalCode findOrSavePostalCode(final AddressDTO addressDTO) {
		Assert.notNull(addressDTO.getPostalCode());
		String postalCodeStr = addressDTO.getPostalCode().isEmpty() ? Constants.NO_POSTALCODE : addressDTO.getPostalCode();

		// first check if postal code exists in the database note: using addressDTO postalCode if country is blank
		PostalCode postalCode = isNotBlank(addressDTO.getCountry()) ?
			getPostalCodeByCodeAndCountryId(postalCodeStr, addressDTO.getCountry()) :
			getPostalCodeByCode(addressDTO.getPostalCode());

		if (postalCode != null) { return postalCode; }

		// if the address is already geocoded, then save it
		if (addressDTO.getLatitude() != null && addressDTO.getLongitude() != null) {
			return savePostalCode(addressDTO);
		}

		// next, try geocoding the address. if successful, save the verified address to the DB
		final AddressDTO geocodedAddress = geocodingService.parseAddress(addressDTO.getAddressForGeocoder());
		if (geocodedAddress != null) {
			return savePostalCode(geocodedAddress);
		}

		// fail
		return null;
	}

	@Override
	public PostalCode findOrSavePostalCode(String p) {

		// first try to see if postal code exists in the database
		PostalCode postalCode = getPostalCodeByCode(p);
		if (postalCode != null) { return postalCode; }

		// next, try geocoding the address. if successful, save the verified address to the DB
		AddressDTO geocodedAddress = geocodingService.parseAddress(p);
		if (geocodedAddress != null) {
			return savePostalCode(geocodedAddress);
		}

		// fail
		return null;
	}

	@Override
	public List<NationalIdApiDTO> getAllNationalIds() {
		try {
			final String json =
					IOUtils.toString(
							getClass().getClassLoader().getResourceAsStream("tax/national-ids.js"), StandardCharsets.UTF_8);
			return new ObjectMapper()
					.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
					.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
					.readValue(json, new TypeReference<List<NationalIdApiDTO>>(){});
		} catch (final Exception e) {
			logger.error("Error fetching national IDs from disk.", e);
			return ImmutableList.of();
		}
	}

	@Override
	public PostalCode getPostalCodeByCodeAndCountryId(String postalCode, String countryId) {
		Assert.hasText(countryId);
		Country country = Country.valueOf(countryId);
		String code = postalCode.isEmpty() ? Constants.NO_POSTALCODE : trimAllWhitespace(postalCode);
		return postalCodeDAO.findByPostalCode(code, country);
	}

	@Override
	public PostalCode getPostalCodeByCode(String postalCode) {
		Assert.hasText(postalCode);
		Country country = findCountryById(Country.getCountry(postalCode));
		return postalCodeDAO.findByPostalCode(trimAllWhitespace(postalCode), country);
	}

	@Override
	public PostalCode getPostalCodeByCodeAndCounry(String postalCode, Country country) {
		Assert.hasText(postalCode);
		Assert.notNull(country);
		return postalCodeDAO.findByPostalCode(trimAllWhitespace(postalCode), country);
	}

	@Override
	public PostalCode getPostalCodeByCodeCountryStateCity(String postalCode, String country, String state, String city) {
		Assert.hasText(country);
		State stateEntity = findStateWithCountryAndState(country, state);
		String code = postalCode.isEmpty() ? Constants.NO_POSTALCODE : trimAllWhitespace(postalCode);
		return postalCodeDAO.findByPostalCodeCountryStateCity(code, country, stateEntity, city);
	}

	@Override
	public List<Language> getLanguages() throws Exception {
		return languageDAO.findLanguages();
	}

	@Override
	public List<? extends LookupEntity> getLanguageProficiencyTypes() throws Exception {
		return lookupEntityDAO.findLookupEntities(LanguageProficiencyType.class);
	}

	@Override
	public BankRouting getBankRouting(String routingNumber, String country) {
		return bankRoutingDAO.get(routingNumber, country);
	}

	@Override
	public Industry findIndustry(Long industryId) {
		return industryDAO.get(industryId);
	}

	@Override
	public List<MobileProvider> findAllMobileProviders() throws Exception {
		return mobileProviderDAO.findAllMobileProviders();
	}

	@Override
	public Country findCountryById(String countryId) {
		Assert.hasText(countryId);
		return countryDAO.findCountryById(countryId);
	}

	@Override
	public MobileProvider findMobileProvidersById(Long mobileProviderId) {
		return mobileProviderDAO.findMobileProviderById(mobileProviderId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaxEntityType> getTaxEntityTypes()  {
		return lookupEntityDAO.findLookupEntities(TaxEntityType.class);
	}

	@Override
	@Cacheable(
		value = LOCATION_TYPES,
		key = "#root.target.LOCATION_TYPES"
	)
	public List<LocationTypeDTO> getLocationTypeDTOs() {

		List<LocationType> locationTypes = locationTypeDAO.findAllLocationTypes();
		List<LocationTypeDTO> locationTypeDTOs = Lists.newArrayListWithCapacity(locationTypes.size());
		for (LocationType locationType : locationTypes) {
			locationTypeDTOs.add(new LocationTypeDTO(locationType.getId(), locationType.getDescription()));
		}

		return locationTypeDTOs;
	}

	@Override
	public List<LocationType> getLocationTypes() {
		return locationTypeDAO.findAllLocationTypes();
	}

	@Override
	public List<DressCode> getDressCodes() {
		return dressCodeDAO.findAllDressCodes();
	}

	@Override public List<String> getDaysOfWeek() {
		return DAYSOFWEEK;
	}

	@Override public List<String> getMonthsOfYear() {
		return MONTHSOFYEAR;
	}

	@Override
	public List<BlacklistedDomain> getBlacklistedDomains() {
		return blacklistedDomainDAO.getAll();
	}

	@Override
	public IndustryPagination findAllIndustries(IndustryPagination pagination) {
		return industryDAO.findAllIndustries(pagination);
	}

	@Override
	public List<TimeZone> findAllActiveTimeZones() {
		return timeZoneDAO.findAllActiveTimeZones();
	}

	@Override
	public List<CallingCode> findAllActiveCallingCodes() {
		return callingCodesDAO.findAllActiveCallingCodes();
	}

	@Override
	public ImmutableList<Map> getProjectedActiveCallingCodes(String[] fields) throws Exception {
		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(fields, findAllActiveCallingCodes()));
	}

	@Override
	@Cacheable(
		value = UNIQUE_ACTIVE_CALLING_CODE_IDS,
		key = "#root.target.UNIQUE_ACTIVE_CALLING_CODE_IDS"
	)
	public List<String> getAllUniqueActiveCallingCodeIds() {
		return callingCodesDAO.getAllUniqueActiveCallingCodeIds();
	}

	@Override
	public Map<TimeZone, String> findActiveTimeZonesWithShortNames() {
		List<TimeZone> timeZones = findAllActiveTimeZones();
		Map<TimeZone, String> result = Maps.newHashMap();
		for (TimeZone tz : timeZones) {
			result.put(tz, DateTimeZone.forID(tz.getTimeZoneId()).getShortName(Long.parseLong(Constants.TIMESTAMP_DUMMY_VALUE)));
		}
		return result;
	}

	@Override
	public TimeZone findTimeZonesById(Long timeZoneId) {
		return timeZoneDAO.findTimeZonesById(timeZoneId);
	}

	@Override
	public TimeZone findTimeZonesByTimeZoneId(String timeZoneId) {
		return timeZoneDAO.findTimeZonesByTimeZoneId(timeZoneId);
	}

	@Override
	public String getStateCode(String keyword) {
		List<StateDTO> stateDTOs = getStateDTOs();
		for (StateDTO dto : stateDTOs) {
			if (dto.getName().equals(keyword) || dto.getShortName().equals(keyword)) {
				return dto.getShortName();
			}
		}
		return StringUtils.EMPTY;
	}

	@Override
	public State findState(String keyword) {
		if (isNotBlank(keyword)) {
			return stateDAO.findStateByShortName(keyword);
		}
		return null;
	}

	@Override
	public State findStateWithCountryAndState(String country, String state) {
		if (isNotBlank(country) && isNotBlank(state)) {
			String countryId = Country.newInstance(country).getId();
			State provinceOrState = stateDAO.findStateWithCountryAndStateCode(countryId, state);
			if (provinceOrState == null) {
				return stateDAO.findStateWithCountryAndStateName(countryId, state);
			}
			return provinceOrState;
		}
		return null;
	}
}
