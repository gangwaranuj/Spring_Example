package com.workmarket.web.converters;

import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.web.forms.addressbook.LocationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LocationDTOToLocationFormConverter implements Converter<LocationDTO, LocationForm> {

	@Autowired AddressDTOToAddressFormConverter addressDTOToAddressFormConverter;

	@Override
	public LocationForm convert(LocationDTO locationDTO) {
		if (locationDTO == null) {
			return null;
		}

		LocationForm form = new LocationForm();

		form.setId(locationDTO.getId());
		form.setClient_company(locationDTO.getClientCompanyId());
		form.setName(locationDTO.getName());
		form.setNumber(locationDTO.getLocationNumber());
		form.updateAddressFields(addressDTOToAddressFormConverter.convert(locationDTO));

		return form;
	}
}
