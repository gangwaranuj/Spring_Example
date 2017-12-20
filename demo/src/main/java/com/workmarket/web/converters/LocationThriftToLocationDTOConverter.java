package com.workmarket.web.converters;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.model.LocationType;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Location;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LocationThriftToLocationDTOConverter implements Converter<Location, LocationDTO> {

	@Override
	public LocationDTO convert(Location location) {
		if (location == null) {
			return null;
		}

		LocationDTO locationDTO = new LocationDTO();
		locationDTO.setId(location.getId());
		locationDTO.setName(location.getName());
		locationDTO.setLocationNumber(location.getNumber());

		com.workmarket.thrift.core.Company company = location.getCompany();
		if (company != null) {
			locationDTO.setCompanyId(company.getId());
		}

		Address address = location.getAddress();
		if (address != null) {
			locationDTO.setAddress1(address.getAddressLine1());
			locationDTO.setAddress2(address.getAddressLine2());
			locationDTO.setCity(address.getCity());
			locationDTO.setState(address.getState());
			locationDTO.setPostalCode(address.getZip());
			locationDTO.setCountry(address.getCountry());
			locationDTO.setLocationTypeId(LocationType.valueOf(address.getType()));
			GeoPoint latLong = address.getPoint();
			if (latLong != null) {
				double latitude = latLong.getLatitude();
				double longitude = latLong.getLongitude();
				locationDTO.setLatitude(latitude == 0 ? null : new BigDecimal(latitude));
				locationDTO.setLongitude(longitude == 0 ? null : new BigDecimal(longitude));
			}
		}

		return locationDTO;
	}
}
