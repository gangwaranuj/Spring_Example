package com.workmarket.web.converters;

import com.workmarket.domains.model.Address;
import com.workmarket.dto.AddressDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AddressToAddressDTOConverter implements Converter<Address, AddressDTO> {

	@Override
	public AddressDTO convert(Address address) {
		if (address == null) {
			return null;
		}

		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddressId(address.getId());
		addressDTO.setAddress1(address.getAddress1());
		addressDTO.setAddress2(address.getAddress2());
		addressDTO.setCity(address.getCity());
		addressDTO.setState(address.getState() == null ? null : address.getState().getShortName());
		addressDTO.setPostalCode(address.getPostalCode());
		addressDTO.setCountry(address.getCountry() == null ? null : address.getCountry().getId());
		addressDTO.setLocationTypeId(address.getLocationType() == null ? null : address.getLocationType().getId());
		addressDTO.setLatitude(address.getLatitude());
		addressDTO.setLongitude(address.getLongitude());

		return addressDTO;
	}
}
