package com.workmarket.web.converters;

import com.workmarket.domains.model.Location;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LocationToLocationDTOConverter implements Converter<Location, LocationDTO> {

	@Autowired AddressToAddressDTOConverter addressToAddressDTOConverter;

	@Override
	public LocationDTO convert(Location location) {
		if (location == null) {
			return null;
		}

		LocationDTO locationDTO = new LocationDTO();

		if (location.getAddress() != null) {
			AddressDTO addressDTO = addressToAddressDTOConverter.convert(location.getAddress());
			locationDTO.setAddressFields(addressDTO);
		}
		if (location.getCompany() != null) {
			locationDTO.setCompanyId(location.getCompany().getId());
		}
		locationDTO.setId(location.getId());
		locationDTO.setName(location.getName());
		locationDTO.setLocationNumber(location.getLocationNumber());

		return locationDTO;
	}
}
