package com.workmarket.web.converters;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.dto.AddressDTO;
import com.workmarket.web.forms.base.AddressForm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AddressFormToAddressDTOConverter implements Converter<AddressForm, AddressDTO> {

	private static final Log LOGGER = LogFactory.getLog(AddressFormToAddressDTOConverter.class);

	@Override
	public AddressDTO convert(AddressForm addressForm) {
		if (addressForm == null) {
			return null;
		}

		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1(addressForm.getAddress1());
		addressDTO.setAddress2(addressForm.getAddress2());
		addressDTO.setCity(addressForm.getCity());
		addressDTO.setState(addressForm.getState());
		addressDTO.setPostalCode(addressForm.getPostalCode());
		addressDTO.setCountry(Country.valueOf(addressForm.getCountry()).getId());
		addressDTO.setLocationTypeId(addressForm.getLocation_type());

		String latitude = null;
		String longitude = null;
		try {
			latitude = addressForm.getLatitude();
			longitude = addressForm.getLongitude();
			addressDTO.setLatitude(StringUtils.isBlank(latitude) ? null : new BigDecimal(latitude));
			addressDTO.setLongitude(StringUtils.isBlank(longitude) ? null : new BigDecimal(longitude));
		} catch (NumberFormatException e) {
			LOGGER.error(String.format("Error parsing lat/long String to BigDecimal. Lat: %s, Long: %s", latitude, longitude), e);
			addressDTO.setLatitude(null);
			addressDTO.setLongitude(null);
		}

		return addressDTO;
	}
}
