package com.workmarket.web.controllers.assignments;

import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.service.business.CRMService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping("/assignments")
public class WorkFormCRMController extends BaseController {

	@Autowired private CRMService crmService;
	@Autowired private ProjectService projectService;
	@Autowired MessageBundleHelper messageBundleHelper;

	@RequestMapping(value = "/get_clientlocation_contacts", method = RequestMethod.GET)
	public void getClientlocationContacts(@RequestParam("id") Long locationId, Model model) {

		List<ClientContact> contacts = crmService.findAllClientContactsByLocation(locationId, false);

		ClientLocation location = crmService.findClientLocationByIdAndCompany(locationId, getCurrentUser().getCompanyId());

		if (location == null) {
			model.addAttribute("response", newObjectMap(
				"success", false,
				"messages", new String[]{messageBundleHelper.getMessage("addressbook.get_client_locations.exception")}
			));
			return;
		}

		model.addAttribute("response", newObjectMap(
			"success", true,
			"onsite", CollectionUtilities.extractKeyValues(contacts, "id", "id", "value", "fullName"),
			"address", newObjectMap(
				"location_name_text", location.getName(),
				"location_id", location.getId(),
				"location_number_text", location.getLocationNumber() != null ? location.getLocationNumber() : StringUtils.EMPTY,
				"location_instructions", (location.getInstructions() != null ) ? location.getInstructions() : StringUtils.EMPTY,
				"address_one_text", location.getAddress() != null ? location.getAddress().getAddress1() : StringUtils.EMPTY,
				"address_two_text", location.getAddress() != null ? location.getAddress().getAddress2() : StringUtils.EMPTY,
				"city_text", location.getAddress() != null ? location.getAddress().getCity() : StringUtils.EMPTY,
				"state_dropdown", (location.getAddress() != null && location.getAddress().getState() != null) ? location.getAddress().getState().getShortName() : StringUtils.EMPTY,
				"zip_text", location.getAddress() != null ? location.getAddress().getPostalCode() : StringUtils.EMPTY,
				"location_country", location.getAddress() != null ? location.getAddress().getCountry() : StringUtils.EMPTY,
				"dress_code_select", (location.getAddress() != null && location.getAddress().getDressCode() != null ) ? location.getAddress().getDressCode().getDescription() : StringUtils.EMPTY,
				"location_type", (location.getAddress() != null && location.getAddress().getLocationType() != null ) ? location.getAddress().getLocationType().getDescription() : StringUtils.EMPTY,
				"location_type_id", (location.getAddress() != null && location.getAddress().getLocationType() != null ) ? location.getAddress().getLocationType().getId() : StringUtils.EMPTY,
				"latitude", (location.getAddress() != null && location.getAddress().getLatitude() != null ) ? location.getAddress().getLatitude() : StringUtils.EMPTY,
				"longitude", (location.getAddress() != null && location.getAddress().getLongitude() != null ) ? location.getAddress().getLongitude() : StringUtils.EMPTY
			)
		));
	}

	@RequestMapping(
		value = "/get_client_location_contact",
		method = GET)
	public void getClientLocationContact(@RequestParam("id") Long contactId, Model model) {
		ClientContact contact = crmService.findClientContactByIdAndCompany(getCurrentUser().getCompanyId(), contactId);

		if (contact == null) {
			model.addAttribute("response", newObjectMap("successful", false));
			return;
		}
		List<Map<String,Object>> emails = CollectionUtilities.extractPropertiesList(contact.getEmails(), "email");
		List<Map<String,Object>> phones = CollectionUtilities.extractPropertiesList(contact.getPhoneNumbers(), "phone", "extension");

		model.addAttribute("response", newObjectMap(
			"successful", true,
			"first_name", contact.getFirstName(),
			"last_name", contact.getLastName(),
			"emails", emails,
			"phones", phones
		));
	}

	@RequestMapping(
		value = "/get_client_projects",
		method = GET)
	public void getClientProjects(@RequestParam("id") Long clientId, Model model) {
		ProjectPagination pagination = new ProjectPagination(true);
		pagination.addFilter(ProjectPagination.FILTER_KEYS.ACTIVE, true);
		pagination = projectService.findAllProjectsForClientCompany(getCurrentUser().getCompanyId(), clientId, pagination);

		model.addAttribute("response", newObjectMap(
			"success", true,
			"data", CollectionUtilities.extractKeyValues(pagination.getResults(), "id", "id", "value", "name")
		));
	}
}
