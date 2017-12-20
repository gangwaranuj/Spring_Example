package com.workmarket.web.validators;

import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.service.business.CRMService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

//TODO: Merge this class with AddressWorkValidator
@Component("addressBookLocationValidator")
public class AddressBookLocationValidator extends AddressValidator {

	@Autowired private InvariantDataService invariantService;
	@Autowired private CRMService crmService;

	public static final String POSTAL_CODE_INVALID_MESSAGE = "Postal code is invalid";
	public static final String STATE_INVALID_MESSAGE = "State is invalid";
	public static final String CLIENT_COMPANY_INVALID_MESSAGE = "Client Company does not exist";

	@Override
	public boolean supports(Class<?> aClass) {
		return LocationDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object object, Errors errors) {
		super.validate(object, errors);
		LocationDTO locationDTO = (LocationDTO) object;

		if (StringUtils.isNotBlank(locationDTO.getPostalCode())) {
			PostalCode code = invariantService.findOrSavePostalCode(locationDTO);
			if (code == null) {
				rejectInvalidPostalCode(errors, locationDTO);
			} else {
				if (!StringUtilities.equalsIgnoreCaseAndSpaces(code.getPostalCode(), locationDTO.getPostalCode())) {
					rejectInvalidPostalCode(errors, locationDTO);
				}
				boolean validatedStateEqualsInputedState = StringUtilities.equalsIgnoreCaseAndSpaces(code.getStateProvince().getShortName(), locationDTO.getState()) ||
					StringUtilities.equalsIgnoreCaseAndSpaces(code.getStateProvince().getName(), locationDTO.getState());
				if (!validatedStateEqualsInputedState) {
					errors.rejectValue("state", "Invalid", new Object[]{ locationDTO.getState() }, STATE_INVALID_MESSAGE);
				}
			}
		}

		if (locationDTO.getClientCompanyId() != null && crmService.findClientCompanyById(locationDTO.getClientCompanyId()) == null) {
			errors.rejectValue("clientCompanyId", "Invalid", new Object[]{locationDTO.getClientCompanyId()}, CLIENT_COMPANY_INVALID_MESSAGE);
		}
	}

	private void rejectInvalidPostalCode(Errors errors, AddressDTO addressDTO) {
		errors.rejectValue(postalCodeAttribute, "Invalid", new Object[]{addressDTO.getPostalCode()}, POSTAL_CODE_INVALID_MESSAGE);
	}
}
