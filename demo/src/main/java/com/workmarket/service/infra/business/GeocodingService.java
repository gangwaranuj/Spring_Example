package com.workmarket.service.infra.business;

import com.vividsolutions.jts.geom.Point;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.AddressVerificationDTO;
import com.workmarket.service.exception.geo.GeocodingException;

import java.math.BigDecimal;

public interface GeocodingService {

	Point geocode(Long addressId);

	Point geocode(Address address);

	Point geocode(AddressDTO address) throws GeocodingException;

	Point geocode(String address) throws GeocodingException;

	Address geocodeReturnAddress(Address address) throws GeocodingException;

	AddressDTO parseAddress(String address);

	AddressVerificationDTO verify(String address) throws Exception;

	AddressVerificationDTO verify(AddressDTO address) throws Exception;

	TimeZone getTimeZone(final Double lat, final Double lng, final String timestamp);

	String reverseGeocodePostalCode(Double lat, Double lng) throws Exception;

	Double calculateDistance(Coordinate a, Coordinate b);

	Coordinate newCoordinate(Double latitude, Double longitude);
}
