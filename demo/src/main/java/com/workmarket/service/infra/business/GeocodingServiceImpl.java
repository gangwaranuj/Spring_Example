package com.workmarket.service.infra.business;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Point;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.AddressDAO;
import com.workmarket.dao.datetime.TimeZoneDAO;
import com.workmarket.dao.postalcode.CountryDAO;
import com.workmarket.dao.postalcode.StateDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.LocationService;
import com.workmarket.service.business.dto.AddressVerificationDTO;
import com.workmarket.service.exception.geo.GeocodingException;
import com.workmarket.service.infra.geo.GeocodingErrorType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@Service
public class GeocodingServiceImpl implements GeocodingService {

	private static final Log logger = LogFactory.getLog(GeocodingServiceImpl.class);

	// This is public because there's a caching annotation below which requires it.
	public static final String GEOCODE = RedisConfig.GEOCODE;

	@Autowired private LocationService locationService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private AddressDAO addressDAO;
	@Autowired private TimeZoneDAO timeZoneDAO;
	@Autowired private StateDAO stateDAO;
	@Autowired private CountryDAO countryDAO;

	@Override
	public Point geocode(Long addressId) {
		Assert.notNull(addressId);
		return geocode(addressDAO.get(addressId));
	}

	@Override
	public TimeZone getTimeZone(final Double lat, final Double lng, final String timestamp) {
		Assert.notNull(lat);
		Assert.notNull(lng);
		Assert.notNull(timestamp);
		TimeZone theZone;
		try{
			theZone = locationService.requestTimeZone(lat, lng, timestamp);
		}catch(Exception e){
			theZone = timeZoneDAO.findTimeZonesByTimeZoneId(Constants.DEFAULT_TIMEZONE);
			logger.error("There was an error connecting to google timezone api: " + e);
		}
		return theZone;
	}

	@Override
	public Point geocode(Address address) {
		Assert.notNull(address);

		logger.info("Geocoding address id " + address.getId() + " - " + address.toText());
		Point point = null;
		AddressDTO addressDTO = AddressDTO.newGeocodeDTO(address);
		try {
			point = geocode(addressDTO);
		} catch (RuntimeException e) {
			// Despite the failure, we still handle null points down below.
			logger.error(e.getMessage());
		}

		geocodePostSteps(address, addressDTO, point);
		return point;
	}

	@Override
	public Address geocodeReturnAddress(Address address) throws GeocodingException {
		Assert.notNull(address);

		logger.info("Geocoding address id " + address.getId() + " - " + address.toText());
		Point point = null;
		AddressDTO addressDTO = AddressDTO.newGeocodeDTO(address);
		try {
			point = geocode(addressDTO);
		} catch (GeocodingException e) {

			// Despite the failure, we still handle null points down below.
			logger.error(e.getMessage());

			// But throw exception up chain if it's a postal code mismatch
			if (e.getErrorType().equals(GeocodingErrorType.POSTAL_CODE_MISMATCH)) {
				throw new GeocodingException("Mismatching postal code.", GeocodingErrorType.POSTAL_CODE_MISMATCH);
			}
		}

		return geocodePostSteps(address, addressDTO, point);
	}

	@Override
	public Point geocode(AddressDTO address) throws GeocodingException {
		Assert.notNull(address);
		return locationService.geocode(address);
	}

	@Override
	@Cacheable(
		value = GEOCODE,
		key = "#root.target.GEOCODE + #address"
	)
	public Point geocode(String address) throws GeocodingException {
		Assert.notNull(address);
		return locationService.geocode(address, Optional.<AddressDTO>absent());
	}

	private Address geocodePostSteps(Address address, AddressDTO addressDTO, Point point) {
		if (point != null) {
			BigDecimal lat = new BigDecimal(point.getY());
			BigDecimal lng = new BigDecimal(point.getX());
			address.setLatitude(lat);
			address.setLongitude(lng);

			// update address with geocoded values
			if (addressDTO != null) {
				Country country = countryDAO.findCountryById(Country.valueOf(addressDTO.getCountry()).getId());
				if (country != null) {
					State state = stateDAO.findStateWithCountryAndStateCode(
						Country.valueOf(addressDTO.getCountry()).getId(),
						StringUtils.isBlank(addressDTO.getState()) ? Constants.NO_STATE : addressDTO.getState()
					);
					if (state != null) {
						// cannot save address with null postalcode
						address.setPostalCode(addressDTO.getPostalCode() == null ? "" : addressDTO.getPostalCode());
						address.setState(state);
						address.setCountry(country);
						address.setCity(addressDTO.getCity());
					}
				}
			}

			logger.info("SUCCESS [verification status code 1]: "
					+ "address id: " + address.getId()
					+ " (" + lat + ", " + lng + ") - " + address.toText());
			address.setGeocodeVerificationStatus(VerificationStatus.VERIFIED);
		} else {
			// Despite failing, the address will still have a postal code that we can use
			// for providing a centered lat/long and mapping the associated entity.

			PostalCode postal = invariantDataService.getPostalCodeByCodeCountryStateCity(address.getPostalCode(), address.getCountry().getId(), address.getState().getShortName(), address.getCity());
			if (postal != null) {
				BigDecimal lat = new BigDecimal(postal.getLatitude());
				BigDecimal lng = new BigDecimal(postal.getLongitude());
				logger.info("FAIL [verification status code 2]: Falling back to postal_code table, "
						+ "address id: " + address.getId()
						+ ", postal code id: " + postal.getId()
						+ ", (" + lat + ", " + lng + ") - " + address.toText());
				address.setLatitude(lat);
				address.setLongitude(lng);
			} else {
				logger.info("FAIL [verification status code 2]: Address could not be geocoded, "
						+ "address id: " + address.getId()
						+ " - " + address.toText()
						+ ", Country: " + address.getCountry().getId());
			}
			address.setGeocodeVerificationStatus(VerificationStatus.FAILED);
		}
		return address;
	}

	@Override
	public Double calculateDistance(Coordinate a, Coordinate b) {
		return a.distanceInMiles(b);
	}

	@Override
	public AddressDTO parseAddress(String address) {
		Assert.notNull(address);

		AddressDTO response;
		try {
			response = locationService.parse(address);
		}
		catch (GeocodingException e) {
			logger.error(e.getErrorType().getGoogleCode(), e);
			response = new AddressDTO();
		}
		catch (Exception e) {
			logger.error(e);
			response = new AddressDTO();
		}
		return response;
	}

	@Override
	public AddressVerificationDTO verify(String address) throws Exception {
		Assert.notNull(address);
		return locationService.verify(address);
	}

	@Override
	public AddressVerificationDTO verify(AddressDTO address) throws Exception {
		Assert.notNull(address);
		return locationService.verify(address);
	}

	@Override
	public String reverseGeocodePostalCode(Double lat, Double lng) throws Exception {
		Assert.notNull(lat);
		Assert.notNull(lng);

		return locationService.reverseLookup(lat, lng).getPostalCode();
	}

	@Override
	public Coordinate newCoordinate(Double latitude, Double longitude) {
		return new Coordinate(longitude, latitude);
	}
}
