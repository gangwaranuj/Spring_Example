package com.workmarket.web.converters;

import com.workmarket.dto.AddressDTO;
import com.workmarket.web.forms.base.AddressForm;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AddressDTOToAddressFormConverter implements Converter<AddressDTO, AddressForm> {

	@Override
	public AddressForm convert(AddressDTO addressDTO) {
		if (addressDTO == null) {
			return null;
		}

		AddressForm form = new AddressForm();

		form.setAddress1(addressDTO.getAddress1());
		form.setAddress2(addressDTO.getAddress2());
		form.setCity(addressDTO.getCity());
		form.setState(addressDTO.getState());
		form.setPostalCode(addressDTO.getPostalCode());
		form.setCountry(addressDTO.getCountry());
		form.setLocation_type(addressDTO.getLocationTypeId());

		return form;
	}
}
