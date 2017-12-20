package com.workmarket.web.validators;

import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.dto.ClientContactDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("addressBookContactValidator")
public class AddressBookContactValidator implements Validator {

	@Autowired CRMService crmService;
	@Autowired DirectoryService directoryService;

	@Override public boolean supports(Class<?> clazz) {
		return ClientContactDTO.class == clazz;
	}

	@Override public void validate(Object o, Errors errors) {
		ClientContactDTO clientContactDTO = (ClientContactDTO) o;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotNull", "Last name is missing.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotNull", "First name is missing.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "workPhone", "NotNull", "Work phone is missing.");

		Long clientLocationClientCompanyId = null;
		ClientCompany clientCompany = null;

		if (clientContactDTO.getContactId() != null && crmService.findClientContactById(clientContactDTO.getContactId()) == null) {
			errors.rejectValue("contactId", "Invalid", "Unable to find contact.");
		}

		if (clientContactDTO.getClientCompanyId() != null) {
			clientCompany = crmService.findClientCompanyById(clientContactDTO.getClientCompanyId());
			if (clientCompany == null) {
				errors.rejectValue("clientCompanyId", "Invalid", "Unable to find client company.");
			}
		}

		// Check for association with location
		if (clientContactDTO.getClientLocationId() != null) {
			Location location = directoryService.findLocationById(clientContactDTO.getClientLocationId());
			if (location == null) {
				errors.rejectValue("clientLocationId", "Invalid", "Unable to find location.");
			} else {
				// Test to see if the location was saved as a client location
				ClientLocation clientLocation = directoryService.findClientLocationById(location.getId());
				if (clientLocation != null) {
					if (clientLocation.getClientCompany() != null) {
						clientLocationClientCompanyId = clientLocation.getClientCompany().getId();
					}
				}
			}
		}

		// If contact is associated with a client company AND a client location
		// Assert that the client company is the same as the client company associated with the location
		if (clientCompany != null && clientLocationClientCompanyId != null) {
			if (!clientLocationClientCompanyId.equals(clientCompany.getId())) {
				errors.rejectValue("clientCompanyId", "Invalid", "Contact's client company and contact's location's client company don't match.");
			}
		}

	}
}
