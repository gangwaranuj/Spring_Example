package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Point;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.AddressVerificationDTO;

public interface LocationService {

	Point geocode(final String address, final Optional<AddressDTO> addressDTO);

	Point geocode(final AddressDTO address);

	TimeZone requestTimeZone(final Double lat, final Double lng, final String timestamp);

	AddressVerificationDTO verify(final String address);

	AddressVerificationDTO verify(final AddressDTO address);

	AddressDTO reverseLookup(final Double lat, final Double lng);

	AddressDTO parse(final String address);

	long getGeocodeDelay();
}
