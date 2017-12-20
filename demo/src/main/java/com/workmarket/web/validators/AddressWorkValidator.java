package com.workmarket.web.validators;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class AddressWorkValidator extends AddressValidator {

	@Autowired private InvariantDataService invariantService;

	@Override
	public boolean supports(Class<?> aClass) {
		return AddressDTO.class.isAssignableFrom(aClass);
	}

	public void validate(AddressDTO addressDTO, BindingResult errors) {
		super.validate(addressDTO, errors);
		Country country = Country.valueOf(addressDTO.getCountry());

		if ((country != null && (country.getId().equals("USA") || country.getId().equals("CAN"))) && StringUtils.isNotBlank(addressDTO.getPostalCode())) {
			PostalCode code = invariantService.findOrCreatePostalCode(addressDTO);
			if (code == null) {
				errors.rejectValue(postalCodeAttribute, "NotNull");
			} else {
				String stateName = StringUtils.isNotBlank(addressDTO.getState()) ? addressDTO.getState() : "the selected state";

				if (StringUtils.isNotBlank(addressDTO.getState()) &&
					!(StringUtils.equals(code.getStateProvince().getShortName(), addressDTO.getState()) || StringUtils.equals(code.getStateProvince().getName(), addressDTO.getState()))) {
					errors.rejectValue("state", "Invalid", new Object[]{code.getPostalCode(), stateName, code.getStateProvince()}, "Invalid");
				}
				if (StringUtils.isNotBlank(addressDTO.getCountry()) && !StringUtils.equals(code.getCountry().getId(), Country.valueOf(addressDTO.getCountry()).getId())) {
					errors.rejectValue("country", "Invalid", new Object[]{code.getPostalCode(), addressDTO.getCountry()}, "Invalid");
				}
			}
		}
	}
}
