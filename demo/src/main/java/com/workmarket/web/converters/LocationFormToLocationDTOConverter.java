package com.workmarket.web.converters;

import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.web.forms.addressbook.LocationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LocationFormToLocationDTOConverter implements Converter<LocationForm, LocationDTO> {

	@Autowired AddressFormToAddressDTOConverter addressFormToAddressDTOConverter;

	@Override
	public LocationDTO convert(LocationForm locationForm) {
		if (locationForm == null) {
			return null;
		}

		LocationDTO locationDTO = new LocationDTO();

		locationDTO.setId(locationForm.getId());
		locationDTO.setClientCompanyId(locationForm.getClient_company());
		locationDTO.setName(locationForm.getName());
		locationDTO.setLocationNumber(locationForm.getNumber());
		locationDTO.setInstructions(locationForm.getInstructions());
		locationDTO.setAddressFields(addressFormToAddressDTOConverter.convert(locationForm));
		
		return locationDTO;
	}
}
