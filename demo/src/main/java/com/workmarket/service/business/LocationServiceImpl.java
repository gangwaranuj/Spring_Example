
package com.workmarket.service.business;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.datetime.TimeZoneDAO;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.dto.AddressDTO;
import com.workmarket.location.client.LocationClient;
import com.workmarket.location.vo.Address;
import com.workmarket.location.vo.AddressVerification;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.dto.AddressVerificationDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.functions.Func1;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class LocationServiceImpl implements LocationService {
	private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);
	private static final String GEOCODE_DELAY_REDIS_KEY = "geocodeDelayInMillis";
	private static final long GEOCODE_DEFAULT_DELAY = 175;
	static final String COUNTRY_TYPE = "country";
	static final String STATE_TYPE = "state";
	private static final String CITY_TYPE = "city";
	private static final String POSTAL_CODE_TYPE = "postalCode";

	private LocationClient locationClient;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private TimeZoneDAO timeZoneDAO;
	@Autowired private RedisAdapter redis;

	@PostConstruct
	public void postConstruct() {
		final MetricRegistryFacade metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "locationservice");

		locationClient = new LocationClient(metricRegistryFacade);
	}

	@Override
	public Point geocode(final String address, final Optional<AddressDTO> optionalInputAddressToLogMismatches) {
		return locationClient.forward(address)
			.map(new Func1<Address, Point>() {
				@Override
				public Point call(final Address addressDTO) {
					if (optionalInputAddressToLogMismatches.isPresent()) {
						// if no postal code returned for a place that should have one, then fail to avoid creating
						// invalid postal_code data
						if (isEmpty(addressDTO.getPostalCode()) &&
							!Constants.COUNTRIES_WITH_NO_POSTALCODE.contains(addressDTO.getCountry())) {
							logger.error(String.format("Geocoding failure on (%s). Result is missing a Postal Code", address));
							return null;
						}
						// if no state returned for a place that should have one, then fail to avoid creating
						// invalid state data
						if (isEmpty(addressDTO.getState()) &&
							!Constants.COUNTRIES_WITH_NO_STATE.contains(addressDTO.getCountry())) {
							logger.error(String.format("Geocoding failure on (%s). Result is missing a State", address));
							return null;
						}

						final AddressDTO inputAddress = optionalInputAddressToLogMismatches.get();
						final List<String> mismatches = new ArrayList<>(4);

						addMismatchToArray(mismatches, COUNTRY_TYPE, inputAddress.getCountry(), addressDTO.getCountry());
						addMismatchToArray(mismatches, STATE_TYPE, inputAddress.getState(), addressDTO.getState());
						addMismatchToArray(mismatches, CITY_TYPE, inputAddress.getCity(), addressDTO.getCity());
						addMismatchToArray(mismatches, POSTAL_CODE_TYPE, inputAddress.getPostalCode(), addressDTO.getPostalCode());

						if (!mismatches.isEmpty()) {
							// correct any mis-matched fields
							inputAddress.setCountry(addressDTO.getCountry());
							inputAddress.setState(addressDTO.getState());
							inputAddress.setCity(addressDTO.getCity());
							inputAddress.setPostalCode(addressDTO.getPostalCode());
							final String mismatchString = StringUtils.join(mismatches, ", ");
							logger.info(String.format("Geocoding mismatch on (%s). Mismatches were {%s}", address, mismatchString));
						}
					}
					return convertAddressToPoint(addressDTO);
				}
			})
			.toBlocking().singleOrDefault(null);
	}

	/**
	 * If queryPropertyValue is not equal to resultPropertyValue, add an error string to mismatches.
	 * @param propertyName
	 * @param queryPropertyValue
	 * @param resultPropertyValue
	 * @return
	 */
	@VisibleForTesting
	void addMismatchToArray(final List<String> mismatches, final String propertyName, final String queryPropertyValue, final String resultPropertyValue) {
		final String canonicalizedQueryPropertyValue;
		if (propertyName.equalsIgnoreCase(COUNTRY_TYPE)) {
			canonicalizedQueryPropertyValue = Country.newInstance(queryPropertyValue).getISO();
		} else {
			canonicalizedQueryPropertyValue = queryPropertyValue;
		}
		if (!StringUtils.equalsIgnoreCase(canonicalizedQueryPropertyValue, resultPropertyValue)) {
			final String error = String.format("\"%s\": {\"query\": \"%s\", \"return\": \"%s\"}",
				propertyName,
				queryPropertyValue,
				resultPropertyValue);

			mismatches.add(error);
		}
	}

	@Override
	public Point geocode(final AddressDTO address) {
		return geocode(address.getAddressForGeocoder(), Optional.of(address));
	}

	@Override
	public TimeZone requestTimeZone(final Double lat, final Double lng, final String timestamp) {
		return convertLocationTZToDateTimeTZ(locationClient.geocodeTimezone(lat, lng).toBlocking().single());
	}

	@Override
	public AddressVerificationDTO verify(final String address) {
		return convertAddressVerificationToDTO(locationClient.verify(address).toBlocking().single());
	}

	@Override
	public AddressVerificationDTO verify(final AddressDTO address) {
		return verify(address.getAddressForGeocoder());
	}

	@Override
	public AddressDTO reverseLookup(final Double lat, final Double lng) {
		return convertAddressToDTO(locationClient.reverse(lat, lng).toBlocking().single());
	}

	@Override
	public AddressDTO parse(final String address) {
		return convertAddressToDTO(locationClient.forward(address).toBlocking().single());
	}

	@Override
	public long getGeocodeDelay() {
		long delay = GEOCODE_DEFAULT_DELAY;

		try {
			final Optional<Object> redisValue = redis.get(GEOCODE_DELAY_REDIS_KEY);
			delay = Long.valueOf(String.valueOf(redisValue.or(GEOCODE_DEFAULT_DELAY)));
		} catch (final NumberFormatException e) {
			logger.error("[geo] Redis delay value not parseable (key: {})", GEOCODE_DELAY_REDIS_KEY, e);
		}

		return delay;
	}

	private Point convertAddressToPoint(final Address address) {
		final double longitude = address.getLongitude();
		final double latitude = address.getLatitude();
		final GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createPoint(new Coordinate(longitude, latitude));
	}

	private AddressDTO convertAddressToDTO(final Address address) {
		final AddressDTO dto = new AddressDTO();

		dto.setLatitude(BigDecimal.valueOf(address.getLatitude()));
		dto.setLongitude(BigDecimal.valueOf(address.getLongitude()));
		dto.setAddress1(address.getAddress1());
		dto.setAddress2(address.getAddress2());
		dto.setCity(address.getCity());
		dto.setState(address.getState());
		dto.setPostalCode(address.getPostalCode());

		if (Country.US.equals(address.getCountry())) {
			dto.setCountry(Country.USA);
		} else if (Country.ISO2_CANADA.equals(address.getCountry())) {
			dto.setCountry(Country.CANADA);
		} else {
			dto.setCountry(address.getCountry());
		}

		return dto;
	}

	private AddressVerificationDTO convertAddressVerificationToDTO(final AddressVerification addressVerification) {
		final AddressVerificationDTO dto = new AddressVerificationDTO();

		final List<AddressDTO> componentMatches = new ArrayList<>();
		for (final Address address : addressVerification.getComponents()) {
			componentMatches.add(convertAddressToDTO(address));
		}
		dto.setComponentMatches(componentMatches);

		dto.setMatches(addressVerification.getMatchedAddresses());
		dto.setVerified(addressVerification.getVerified());
		return dto;
	}

	private TimeZone convertLocationTZToDateTimeTZ(final com.workmarket.location.vo.TimeZone timeZone) {
		return timeZoneDAO.findTimeZonesByTimeZoneId(timeZone.getId());
	}
}
