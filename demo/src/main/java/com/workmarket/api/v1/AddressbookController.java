package com.workmarket.api.v1;

import com.google.common.collect.Maps;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.model.resolver.ApiArgumentResolver;
import com.workmarket.api.v1.model.ApiClientCompanyIdDTO;
import com.workmarket.api.v1.model.ApiClientContactIdDTO;
import com.workmarket.api.v1.model.ApiClientLocationIdDTO;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.WebsiteDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.converters.AddressFormToAddressDTOConverter;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.forms.addressbook.ApiLocationForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.ApiAddressWorkValidator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = "Addressbook")
@Controller("apiAddressbookController")
@RequestMapping(value = {"/v1/employer/addressbook", "/api/v1/addressbook"})
public class AddressbookController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(AddressbookController.class);

	@Autowired private CRMService crmService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AddressFormToAddressDTOConverter addressFormToAddressDTOConverter;

	@Autowired @Qualifier("apiAddressWorkValidator") private ApiAddressWorkValidator addressValidator;

	/**
	 * Add a client.
	 * @param companyName (company_name) - company name (required)
	 * @param industryName (industry_name) - industry name
	 * @param customerID (company_id) - customer ID
	 * @param region - region
	 * @param division - division
	 * @param phoneNumbers
	 *                  phones[<N>]=212-333-1188
	 *                  phones[<N>][ext]=123
	 *                  phones[<N>][type]={work, home, other}
	 * @param websites
	 * 					websites[<N>]=www.workmarket.com
	 * 					websites[<N>][type]={work, home, other}
	 * @return ApiResponse populated with created client company ID.
	 */
	@ApiOperation(value = "Add client")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="clients/add", method=RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiClientCompanyIdDTO> addClient(
			@RequestParam(value="company_name", required=false) String companyName,
			@RequestParam(value="industry_name", required=false) String industryName,
			@RequestParam(value="customer_id", required=false) String customerID,
			@RequestParam(value="region", required=false) String region,
			@RequestParam(value="division", required=false) String division,
			@ApiArgumentResolver PhoneNumberDTO[] phoneNumbers,
			@ApiArgumentResolver WebsiteDTO[] websites) {

		if (logger.isDebugEnabled()) {
			logger.debug("adding a client: companyName={}, industryName={}, customerID={}, region={}, division={}, phoneNumbers={}, websites={}",
				companyName, industryName, customerID, region, division,
				ToStringBuilder.reflectionToString(phoneNumbers), ToStringBuilder.reflectionToString(websites));
		}

		if (!StringUtils.hasText(companyName)) {
			throw new HttpException400(messageHelper.getMessage("NotEmpty", "Company Name"));
		}

		ClientCompanyDTO companyDTO = new ClientCompanyDTO();
		companyDTO.setName(companyName);
		companyDTO.setCustomerId(customerID);
		companyDTO.setRegion(region);
		companyDTO.setDivision(division);
		companyDTO.setIndustryName(industryName);

		try {
			ClientCompany company = crmService.saveOrUpdateClientCompany(authenticationService.getCurrentUser().getId(), companyDTO, null);

			// add phone numbers
			for (PhoneNumberDTO phoneNumber : phoneNumbers) {
				crmService.addPhoneToClientCompany(company.getId(), phoneNumber);
			}

			// add websites
			for (WebsiteDTO website : websites) {
				crmService.addWebsiteToClientCompany(company.getId(), website);
			}

			final ApiClientCompanyIdDTO result = new ApiClientCompanyIdDTO.Builder()
				.withId(company.getId())
				.build();

			return new ApiV1Response<>(result);
		} catch (Exception ex) {
			if (logger.isErrorEnabled()) {
				logger.error("failed to save client: companyName={}, industryName={}, customerID={}, region={}, division={}, phoneNumbers={}, websites={}",
					new Object[] { companyName, industryName, customerID, region, division,
						ToStringBuilder.reflectionToString(phoneNumbers), ToStringBuilder.reflectionToString(websites) }, ex);
			}

			throw new HttpException400(messageHelper.getMessage("addressbook.add_company.exception.company_save"));
		}
	}

	/**
	 * Add a location to a client.
	 * @param phoneNumbers
	 *					phones[<N>]=212-333-1188
	 *					phones[<N>][ext]=123
	 *					phones[<N>][type]={work, home, other}
	 * @return ApiResponse populated with created location ID.
	 */
	@RequestMapping(value="clients/locations/add", method=RequestMethod.POST)
	public @ResponseBody ApiV1Response<ApiClientLocationIdDTO> addClientLocation(
			@Valid @ModelAttribute("form") ApiLocationForm form,
			BindingResult bindingResult,
			@ApiArgumentResolver PhoneNumberDTO[] phoneNumbers) {

		AddressDTO addressDTO = addressFormToAddressDTOConverter.convert(form);
		addressValidator.validate(addressDTO, bindingResult);

		if (bindingResult.hasErrors()) {
			List<String> errors = messageHelper.getAllFieldErrors(bindingResult);
			throw new HttpException400(CollectionUtilities.join(errors, ", "));
		}

		if (logger.isDebugEnabled()) {
			logger.debug("adding a location to a client: clientId={}, locationName={}, address1={}, " +
					"address2={}, city={}, state={}, postalCode={}, country={}, dressCode={}, locationType={}, firstName={}, lastName={}, title={}, manager={}, phoneNumbers={}",
					form.getClient_id(), form.getLocation_name(), form.getAddress1(), form.getAddress2(), form.getCity(), form.getState(), form.getPostal_code(), form.getCountry(),
					form.getDress_code(), form.getLocation_type(), form.getFirst_name(), form.getLast_name(), form.getTitle(),
					form.getIs_manager(), ToStringBuilder.reflectionToString(phoneNumbers));
		}

		ClientCompany clientCompany = crmService.findClientCompanyById(form.getClient_id());

		LocationDTO locationDTO = new LocationDTO();
		locationDTO.setName(form.getLocation_name());
		locationDTO.setLocationNumber(form.getLocation_number());
		locationDTO.setInstructions(form.getLocation_instructions());
		locationDTO.setCompanyId(clientCompany.getCompany().getId());
		locationDTO.setClientCompanyId(clientCompany.getId());
		locationDTO.setAddressFields(addressDTO);

		final MessageBundle bundle = messageHelper.newBundle();
		final ApiClientLocationIdDTO.Builder builder = new ApiClientLocationIdDTO.Builder();

		try {
			final ClientLocation location = crmService.saveOrUpdateClientLocation(form.getClient_id(), locationDTO, null);

			// add phone numbers
			for (PhoneNumberDTO phoneNumber : phoneNumbers) {
				crmService.addPhoneToClientLocation(location.getId(), phoneNumber);
			}

			builder.withId(location.getId());

			if (StringUtilities.any(form.getFirst_name(), form.getLast_name())) {
				final ClientContactDTO clientContactDTO = new ClientContactDTO();
				final boolean isManager = "1".equals(form.getIs_manager());

				clientContactDTO.setClientLocationId(location.getId());
				clientContactDTO.setFirstName(form.getFirst_name());
				clientContactDTO.setLastName(form.getLast_name());
				clientContactDTO.setJobTitle(form.getTitle());
				clientContactDTO.setManager(isManager);

				final ClientContact contact = crmService.saveOrUpdateClientContact(authenticationService.getCurrentUser().getCompany().getId(), clientContactDTO, null);

				// add phone numbers
				for (PhoneNumberDTO phoneNumber : phoneNumbers) {
					crmService.addPhoneToClientContact(contact.getId(), phoneNumber);
				}

				builder.withContactId(contact.getId());
			}

			return new ApiV1Response<>(builder.withStatus(true).build());
		} catch (Exception ex) {
			logger.error("error saving location", ex);
			messageHelper.addError(bundle, "addressbook.add_location.exception.location_save");
		}

		return new ApiV1Response<>(
			builder.withStatus(false).build(),
			bundle.getErrors(),
			HttpStatus.SC_BAD_REQUEST
		);
	}

	/**
	 * Add a contact to a client.
	 * @param firstName (first_name) - first name
	 * @param lastName (last_name) - last name
	 * @param title - job title
	 * @param manager - contact is a location manager ("1" = true)
	 * @param emails
	 * 					emails[<N>]=info@workmarket.com
	 * 					emails[<N>][type]={work, home, other}
	 * @param phoneNumbers
	 *					phones[<N>]=212-333-1188
	 *					phones[<N>][ext]=123
	 *					phones[<N>][type]={work, home, other}
	 * @return ApiResponse populated with created contact ID.
	 */
	@ApiOperation(value = "Add contact to client")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="clients/contacts/add", method=RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiClientContactIdDTO> addClientContact(
			@RequestParam(value="client_id", required=false) Long clientId,
			@RequestParam(value="first_name", required=false) String firstName,
			@RequestParam(value="last_name", required=false) String lastName,
			@RequestParam(value="title", required=false) String title,
			@RequestParam(value="manager", required=false) String manager,
			@ApiArgumentResolver EmailAddressDTO[] emails,
			@ApiArgumentResolver PhoneNumberDTO[] phoneNumbers) {

//		TODO: This does not check for clientId, though the docs say it's required.
//		Probably not a good idea to change this now though.

		if (logger.isDebugEnabled()) {
			logger.debug("adding a contact to a client: firstName={}, lastName={}, title={}, manager={}, " +
					"emails={}, phoneNumbers={}",
				firstName, lastName, title, manager,
				ToStringBuilder.reflectionToString(emails), ToStringBuilder.reflectionToString(phoneNumbers));
		}

		ClientContactDTO clientContactDTO = new ClientContactDTO();
		clientContactDTO.setClientCompanyId(clientId);
		clientContactDTO.setManager("1".equals(manager));
		clientContactDTO.setFirstName(firstName);
		clientContactDTO.setLastName(lastName);
		clientContactDTO.setJobTitle(title);

		try {
			ClientContact contact = crmService.saveOrUpdateClientContact(authenticationService.getCurrentUser().getCompany().getId(), clientContactDTO, null);

			// add email addresses
			for (EmailAddressDTO email : emails) {
				crmService.addEmailToClientContact(contact.getId(), email);
			}

			// add phone numbers
			for (PhoneNumberDTO phoneNumber : phoneNumbers) {
				crmService.addPhoneToClientContact(contact.getId(), phoneNumber);
			}

			final ApiClientContactIdDTO result = new ApiClientContactIdDTO.Builder()
				.withId(contact.getId())
				.build();

			return new ApiV1Response<>(result);
		} catch (Exception ex) {
			if (logger.isErrorEnabled()) {
				logger.error("failed to add a contact to a client: firstName={}, lastName={}, title={}, " +
					"manager={}, emails={}, phoneNumbers={}",
					firstName, lastName, title, manager,
					ToStringBuilder.reflectionToString(emails), ToStringBuilder.reflectionToString(phoneNumbers));
			}

			throw new HttpException400(messageHelper.getMessage("addressbook.add_contact.exception.contact_save"));
		}
	}

	/**
	 * Add a contact to a location.
	 * @param locationId (location_id) - location Id (required)
	 * @param firstName (first_name) - first name
	 * @param lastName (last_name) - last name
	 * @param title - job title
	 * @param manager - contact is a location manager ("1" = true)
	 * @param emails
	 *	 				emails[<N>]=info@workmarket.com
	 * 					emails[<N>][type]={work, home, other}
	 * @param phoneNumbers
	 *					phones[<N>]=212-333-1188
	 *					phones[<N>][ext]=123
	 *					phones[<N>][type]={work, home, other}
	 * @return ApiResponse populated with created contact ID.
	 */
	@ApiOperation(value = "Add contact to location")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="locations/contacts/add", method=RequestMethod.POST)
	@ResponseBody
	public ApiV1Response<ApiClientContactIdDTO> addLocationContact(
			@RequestParam(value="location_id", required=false) Long locationId,
			@RequestParam(value="first_name", required=false) String firstName,
			@RequestParam(value="last_name", required=false) String lastName,
			@RequestParam(value="title", required=false) String title,
			@RequestParam(value="manager", required=false) String manager,
			@ApiArgumentResolver EmailAddressDTO[] emails,
			@ApiArgumentResolver PhoneNumberDTO[] phoneNumbers) {

		if (logger.isDebugEnabled()) {
			logger.debug("adding a contact to a location: locationId={}, firstName={}, lastName={}, title={}, manager={}, emails={}, phoneNumbers={}",
				locationId, firstName, lastName, title, manager,
				ToStringBuilder.reflectionToString(emails), ToStringBuilder.reflectionToString(phoneNumbers));
		}

		if (locationId == null) {
			throw new HttpException400("Missing locationId");
		}

		ClientContactDTO clientContactDTO = new ClientContactDTO();
		clientContactDTO.setClientLocationId(locationId);
		clientContactDTO.setFirstName(firstName);
		clientContactDTO.setLastName(lastName);
		clientContactDTO.setJobTitle(title);
		clientContactDTO.setManager("1".equals(manager));

		try {
			ClientContact contact = crmService.saveOrUpdateClientContact(authenticationService.getCurrentUser().getCompany().getId(), clientContactDTO, null);

			// add email addresses
			for (EmailAddressDTO email : emails) {
				crmService.addEmailToClientContact(contact.getId(), email);
			}

			// add phone numbers
			for (PhoneNumberDTO phoneNumber : phoneNumbers) {
				crmService.addPhoneToClientContact(contact.getId(), phoneNumber);
			}

			final ApiClientContactIdDTO result = new ApiClientContactIdDTO.Builder()
				.withId(contact.getId())
				.build();

			return new ApiV1Response<>(result);
		} catch (Exception ex) {
			if (logger.isErrorEnabled()) {
				logger.error("failed to add a contact to a location: locationId={}, firstName={}, lastName={}, title={}, manager={}, emails={}, phoneNumbers={}",
					locationId, firstName, lastName, title, manager,
					ToStringBuilder.reflectionToString(emails), ToStringBuilder.reflectionToString(phoneNumbers));
			}

			throw new HttpException400(messageHelper.getMessage("addressbook.add_contact.exception.contact_save"));
		}
	}
}
