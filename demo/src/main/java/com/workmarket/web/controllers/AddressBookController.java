package com.workmarket.web.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.configuration.Constants;
import com.workmarket.data.dataimport.adapter.ContactManagerImportRowLimitExceededException;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressUtilities;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.Sort;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientCompanyPagination;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactPagination;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.crm.ClientLocationPagination;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.directory.Website;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolRequest;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolResponse;
import com.workmarket.search.gen.GroupMessages.SearchType;
import com.workmarket.search.gen.GroupMessages.SortField;
import com.workmarket.search.gen.GroupMessages.TalentPool;
import com.workmarket.search.gen.Common.SortDirectionType;
import com.workmarket.search.request.user.GroupPeopleSearchRequest;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.WebsiteDTO;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.search.SearchService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.converters.LocationFormToLocationDTOConverter;
import com.workmarket.web.forms.addressbook.ClientCompanyForm;
import com.workmarket.web.forms.addressbook.ContactForm;
import com.workmarket.web.forms.addressbook.LocationForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.AddressBookLocationValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/addressbook")
public class AddressBookController extends BaseController {

	private static final Log logger = LogFactory.getLog(AddressBookController.class);

	private static final int FORM_RESULTS_LIMIT = 10;

	@Autowired private CRMService crmService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private FormOptionsDataHelper formOptionsDataHelper;
	@Autowired private SearchService peopleSearchService;
	@Autowired private SearchService searchService;
	@Autowired private WorkService workService;
	@Autowired private AddressBookLocationValidator addressValidator;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private LocationFormToLocationDTOConverter locationFormToLocationDTOConverter;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private FeatureEntitlementService featureEntitlementService;

	@RequestMapping(method = GET)
	public String index(Model model) {

		model.addAttribute("contactForm", new ContactForm());
		model.addAttribute("locationForm", new LocationForm());
		model.addAttribute("clientCompanyForm", new ClientCompanyForm());
		model.addAttribute("fluid", "1");

		model.addAttribute("user_id", getCurrentUser().getId());
		model.addAttribute("company_id", getCurrentUser().getCompanyId());

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "addressbook",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/addressbook/index";
	}

	@RequestMapping(
		value = "/group/get_groups",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getGroups() {

		int MAX_GROUP_FETCH_COUNT = 1000;
		List<Map<String, Object>> groups = Lists.newArrayList();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (featureEntitlementService.hasPercentRolloutFeatureToggle(Constants.SEARCH_SERVICE_GROUP)) {
			FindTalentPoolRequest request = FindTalentPoolRequest.newBuilder()
				.setStart(0)
				.setRows(MAX_GROUP_FETCH_COUNT)
				.setUserId(getCurrentUser().getId())
				.setCompanyId(getCurrentUser().getCompanyId())
				.setSortField(SortField.MEMBER_COUNT)
				.setSortDirection(SortDirectionType.desc)
				.setSearchType(SearchType.SEARCH_COMPANY_GROUPS_LOCATION_MANAGER)
				.build();

			FindTalentPoolResponse resp = searchService.findTalentPools(request);
			if (resp.getStatus().getSuccess()) {
				response.setSuccessful(true);
			}
			for (TalentPool talentPool : resp.getTalentPoolsList()) {
				if (!Constants.MY_COMPANY_FOLLOWERS.equals(talentPool.getName())) {
					groups.add(CollectionUtilities.newObjectMap(
						"id", talentPool.getId(),
						"name", talentPool.getName(),
						"memberCount", talentPool.getMemberCount()));
				}
			}
		} else {
			GroupSolrDataPagination pagination = new GroupSolrDataPagination();
			pagination.getSearchFilter().setCompanyId(getCurrentUser().getCompanyId());
			pagination.getSearchFilter().setUserId(getCurrentUser().getId());
			pagination.setSearchType(GroupSolrDataPagination.SEARCH_TYPE.SEARCH_COMPANY_GROUPS_LOCATION_MANAGER); // fetch public and private groups
			pagination.setResultsLimit(MAX_GROUP_FETCH_COUNT);
			final Sort sort = new Sort("member_count", com.workmarket.domains.model.Pagination.SORT_DIRECTION.DESC);
			pagination.setSorts(Lists.newArrayList(sort));

			GroupSolrDataPagination userGroups = null;
			try {
				userGroups = searchService.searchAllGroups(pagination);
				response.setSuccessful(true);
			} catch (Exception e) {
				logger.error("There was an error searching for groups", e);
			}

			if (userGroups != null && userGroups.getResults() != null) {
				for (GroupSolrData group : userGroups.getResults()) {
					if (!Constants.MY_COMPANY_FOLLOWERS.equals(group.getName())) {
						groups.add(CollectionUtilities.newObjectMap(
							"id", group.getId(),
							"name", group.getName(),
							"memberCount", group.getMemberCount())
						);
					}
				}
			}
		}

		response.addData("groupsList", groups);
		return response;
	}

	@RequestMapping(
		value = "/group/get_members/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getGroupMembers(@PathVariable("id") Long groupId) {

		int MAX_WORKER_COUNT_FOR_MAP = 20000;
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		PeopleSearchRequest searchRequest =
			new PeopleSearchRequest()
				.setUserId(getCurrentUser().getId())
				.setGroupMemberFilter(true)
				.setGroupOverrideMemberFilter(true)
				.setNoFacetsFlag(true)
				.setPaginationRequest(
					new Pagination()
						.setCursorPosition(0)
						.setPageSize(MAX_WORKER_COUNT_FOR_MAP)
				);

		GroupPeopleSearchRequest groupSearchRequest = new GroupPeopleSearchRequest()
			.setGroupId(groupId)
			.setRequest(searchRequest);

		PeopleSearchResponse searchResponse;
		try {
			searchResponse = peopleSearchService.searchGroupMembers(groupSearchRequest);
		} catch (SearchException e) {
			messageHelper.addMessage(response, "addressbook.map.groups.workers");
			return response;
		}

		List<Map<String, Object>> result = Lists.newArrayList();
		for (PeopleSearchResult user : searchResponse.getResults()) {
			result.add(CollectionUtilities.newObjectMap(
				"latitude", user.getLocationPoint().getLatitude(),
				"longitude", user.getLocationPoint().getLongitude(),
				"resourceName", user.getName().getFullName(),
				"companyName", user.getCompanyName(),
				"userNumber", user.getUserNumber()
			));
		}

		response.addData("users", result);
		response.setSuccessful(true);
		return response;
	}

	@RequestMapping(
		value = "/client/get_all",
		method = GET)
	public void getAllClients(Model model, HttpServletRequest httpRequest) throws IllegalAccessException, InstantiationException {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			0, ClientCompanyPagination.SORTS.NAME.toString(),
			1, ClientCompanyPagination.SORTS.CUSTOMER_ID.toString()
		));

		ClientCompanyPagination pagination = request.newPagination(ClientCompanyPagination.class);
		pagination = crmService.findAllClientCompanyByUser(getCurrentUser().getId(), pagination);
		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		for (ClientCompany company : pagination.getResults()) {
			List<String> row = Lists.newArrayList();
			row.add(company.getName());
			row.add(company.getCustomerId());
			row.add(company.getRegion());
			row.add(company.getDivision());
			row.add(company.getIndustry() != null ? company.getIndustry().getName() : company.getIndustryName());
			row.add(null != company.getLastWebsite() ? company.getLastWebsite().getWebsite() : StringUtils.EMPTY);
			row.add(formatPhoneNumberForTable(company.getLastPhone()));

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"id", company.getId(),
				"name", company.getName()
			);
			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	// TODO API
	@RequestMapping(
		value = "/clients",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	ApiV2Response getProjects(HttpServletResponse httpResponse) throws IllegalAccessException, InstantiationException {
		ApiV2Response response;

		try {
			List<ClientCompany> clients = crmService.findAllClientCompanyByUser(getCurrentUser().getId());
			response = ApiV2Response.valueWithResults(ImmutableList.copyOf(crmService.mapClientCompanyToForm(clients)));
		} catch (Exception e) {
			response = ApiV2Response.valueWithMessage(messageHelper.getMessage("addressbook.clients.fetch"),
																								HttpStatus.BAD_REQUEST);
			httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching clients for company %s: ", getCurrentUser().getCompanyId()), e);
		}

		return response;
	}

	@RequestMapping(
		value = "/client/get_all_full",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getAllClientsFull() {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		List<Map<String, Object>> clients = crmService.findAllClientCompaniesByCompanyWithLocationCount(getCurrentUser().getCompanyId());

		if (clients != null) {
			response.addData("clients", clients);
			response.setSuccessful(true);
		}
		return response;
	}

	@RequestMapping(
		value = "/client/manage",
		method = GET)
	public String displayClient(@ModelAttribute("clientCompanyForm") ClientCompanyForm form, Model model, RedirectAttributes flash) {

		if (form == null) {
			messageHelper.addError(messageHelper.newFlashBundle(flash), "clients.manage.fetch.exception");
			return "web/partials/addressbook/client_form";
		}

		if (form.getId() != null) {
			ClientCompany company = crmService.findClientCompanyById(form.getId());
			if (company == null) {
				messageHelper.addError(messageHelper.newFlashBundle(flash), "clients.manage.not_found");
				return "web/partials/addressbook/client_form";
			}
			form.setCompany_name(company.getName());
			form.setCustomer_id(company.getCustomerId());
			form.setRegion(company.getRegion());
			form.setDivision(company.getDivision());
			form.setIndustry_name(company.getIndustry() != null ? company.getIndustry().getName() : company.getIndustryName());
			form.setWebsite(null != company.getLastWebsite() ? company.getLastWebsite().getWebsite() : null);
			form.setWork_phone(null != company.getLastPhone() ? company.getLastPhone().getPhone() : null);
			form.setWork_phone_ext(null != company.getLastPhone() ? company.getLastPhone().getExtension() : null);
		}

		model.addAttribute("industries", formOptionsDataHelper.getIndustries());

		return "web/partials/addressbook/client_form";
	}

	@RequestMapping(
		value = "/client/manage",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveClient(@ModelAttribute("clientCompanyForm") ClientCompanyForm form) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messages = messageHelper.newBundle();

		if (form == null) {
			messageHelper.addMessage(response, "clients.manage.edit.exception");
			return response;
		} else {
			if (form.getCompany_name().isEmpty()) {
				messageHelper.addMessage(response, "clients.manage.name.null");
			}

			if (!form.getWebsite().isEmpty() && !StringUtilities.validateWebAddressPatternWithOptionalProtocol(form.getWebsite())) {
				messageHelper.addMessage(response, "addressbook.website.invalid");
			}
			if (StringUtils.isNotEmpty(form.getWork_phone())
				&& form.getWork_phone().length() < Phone.phoneLengthWithFormat) {
				messageHelper.addMessage(response, "addressbook.phone.invalid");
			}
			if (response.hasMessages()) {
				return response;
			}
		}

		ClientCompanyDTO clientCompanyDTO = new ClientCompanyDTO();
		clientCompanyDTO.setName(form.getCompany_name());
		clientCompanyDTO.setCustomerId(form.getCustomer_id());
		clientCompanyDTO.setDivision(form.getDivision());
		clientCompanyDTO.setIndustryName(form.getIndustry_name());
		clientCompanyDTO.setRegion(form.getRegion());
		clientCompanyDTO.setClientCompanyId(form.getId());

		try {
			ClientCompany clientCompany = crmService.saveOrUpdateClientCompany(getCurrentUser().getId(), clientCompanyDTO, messages);

			Website existingWebsite = clientCompany.getLastWebsite();
			if (StringUtils.isEmpty(form.getWebsite())) {
				if (existingWebsite != null) {
					crmService.removeWebsiteAssociationsFromClientCompany(clientCompany.getId());
				}
			} else {
				if (existingWebsite == null || !form.getWebsite().equals(existingWebsite.getWebsite())) {
					WebsiteDTO websiteDTO = new WebsiteDTO();
					websiteDTO.setWebsite(String.valueOf(form.getWebsite()));
					crmService.addWebsiteToClientCompany(clientCompany.getId(), websiteDTO);
				}
			}

			Phone existingPhone = clientCompany.getLastPhone();
			if (StringUtils.isEmpty(form.getWork_phone())) {
				if (existingPhone != null) {
					crmService.removePhoneAssociationsFromClientCompany(clientCompany.getId());
				}
			} else {
				if (existingPhone == null || !form.getWork_phone().equals(existingPhone.getPhone())
					|| !form.getWork_phone_ext().equals(existingPhone.getExtension())) {
					PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
					phoneNumberDTO.setPhone(form.getWork_phone());
					if (StringUtils.isNotEmpty(form.getWork_phone_ext())) {
						phoneNumberDTO.setExtension(form.getWork_phone_ext());
					}
					crmService.addPhoneToClientCompany(clientCompany.getId(), phoneNumberDTO);
				}
			}
			response.addData("clientData", ImmutableMap.of("id", clientCompany.getId(), "name", clientCompany.getName()));

		} catch (Exception ex) {
			if (messages.hasErrors()) {
				for (String message : messages.getErrors()) {
					messageHelper.addMessage(response, message);
				}
			} else {
				messageHelper.addMessage(response, "clients.manage.edit.exception");
			}
			return response;
		}

		response.setSuccessful(true);
		return response;
	}

	@RequestMapping(
		value = "/client/delete/{id}",
		method = DELETE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteClient(@PathVariable("id") Long id) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messages = messageHelper.newBundle();

		if (workService.doesClientCompanyHaveActiveAssignments(id)) {
			messageHelper.addMessage(response, "clients.manage.delete.activeassignments");
			return response;
		}

		try {
			crmService.deleteClientCompanyByIdAndCompany(id, getCurrentUser().getCompanyId(), messages);
		} catch (Exception ex) {
			if(messages.hasErrors()) {
				for(String message : messages.getErrors()) {
					messageHelper.addMessage(response, message);
				}
			} else {
				messageHelper.addMessage(response, "clients.manage.delete.exception");
			}
			return response;
		}

		response.addData("clientId", id);
		response.setSuccessful(true);
		return response;
	}

	@RequestMapping(
		value = "/location/get_all",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getAllClientCompanyLocations(Model model, HttpServletRequest httpRequest, RedirectAttributes flash) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			1, ClientLocationPagination.SORTS.LOCATION_NAME.toString(),
			2, ClientLocationPagination.SORTS.LOCATION_ID.toString(),
			5, ClientLocationPagination.SORTS.LOCATION_TYPE.toString()
		));
		request.setFilterMapping(ImmutableMap.<String, Enum<?>>of(
			"sSearch_3", ClientLocationPagination.FILTER_KEYS.CLIENT_ID,
			"sSearch_6", ClientLocationPagination.FILTER_KEYS.CONTACT_NAME
		));

		ClientLocationPagination pagination;
		DataTablesResponse<List<String>, Map<String, Object>> response = new DataTablesResponse<>();
		try {
			pagination = request.newPagination(ClientLocationPagination.class);
			pagination = crmService.findAllLocations(getCurrentUser().getCompanyId(), pagination);
			response = DataTablesResponse.newInstance(request, pagination);

			for (ClientLocation location : pagination.getResults()) {
				List<String> row = Lists.newArrayList();

				row.add(location.getName());
				row.add(location.getLocationNumber());

				ClientCompany clientCompany = location.getClientCompany();
				String clientId = "";
				if (clientCompany != null) {
					row.add(clientCompany.getName());
					clientId = String.valueOf(clientCompany.getId());
				} else {
					row.add("");
				}

				Address address = location.getAddress();
				if (address != null) {
					row.add(AddressUtilities.formatAddressLongWithLongState(address, ", "));
					if (address.getLocationType() != null) {
						row.add(address.getLocationType().getDescription());
					} else {
						row.add("");
					}
				} else {
					row.add("");
					row.add("");
				}

				int contactCount = crmService.getContactCountByClientLocation(location.getId());
				String contactName = contactCount == 0 ? "" : crmService.findFirstContactNameByClientLocation(location.getId());
				row.add("");

				Map<String,Object> meta = CollectionUtilities.newObjectMap(
					"id", location.getId(),
					"name", location.getName(),
					"contactName", contactName,
					"moreContacts", contactCount - 1,
					"clientId", clientId
				);
				row.add("");
				row.add("");

				response.addRow(row, meta);
			}
		} catch (Exception ex) {
			messageHelper.addError(messageHelper.newFlashBundle(flash), "locations.manage.exception");
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/location/manage",
		method = GET)
	public String displayLocation(@ModelAttribute("locationForm") LocationForm form, Model model, RedirectAttributes flash) {

		if (form == null) {
			messageHelper.addError(messageHelper.newFlashBundle(flash), "locations.manage.exception");
			return "redirect:/addressbook";
		}

		Set<Long> selectedContacts = Sets.newHashSet();
		if (form.getId() != null) {
			ClientLocation location = crmService.findClientLocationById(form.getId());
			if (location == null) {
				messageHelper.addError(messageHelper.newFlashBundle(flash), "locations.manage.exception");
				return "redirect:/addressbook";
			}

			form.setName(location.getName());
			if (location.getClientCompany() != null) {
				form.setClient_company(location.getClientCompany().getId());
			}
			form.setNumber(location.getLocationNumber());
			form.setInstructions(location.getInstructions());
			form.setAddress1(location.getAddress().getAddress1());
			form.setAddress2(location.getAddress().getAddress2());
			form.setCity(location.getAddress().getCity());
			form.setState(location.getAddress().getState().getShortName());
			form.setPostalCode(location.getAddress().getPostalCode());
			form.setCountry(location.getAddress().getCountry().getId());
			form.setLongitude(location.getAddress().getLongitude() != null ? location.getAddress().getLongitude().toString() : null);
			form.setLatitude(location.getAddress().getLatitude() != null ? location.getAddress().getLatitude().toString() : null);
			form.setLocation_type(location.getAddress().getLocationType().getId());

			List<ClientContact> contacts = crmService.findAllClientContactsByLocation(location.getId(), false);
			List<String> clientContacts = Lists.newArrayList();
			for (ClientContact con : contacts) {
				clientContacts.add(con.getFullName());
				selectedContacts.add(con.getId());
			}
			form.setContacts(clientContacts);
		}

		List<Map<String, Object>> clients = crmService.findAllClientCompaniesByCompany(getCurrentUser().getCompanyId(), "id", "name");

		List<Map<String, Object>> locationTypes = Lists.newArrayList();
		locationTypes.add(
			CollectionUtilities.newObjectMap(
				"id", LocationType.COMMERCIAL_CODE,
				"name", "Commercial"
			)
		);
		locationTypes.add(
			CollectionUtilities.newObjectMap(
				"id", LocationType.RESIDENTIAL_CODE,
				"name", "Residential"
			)
		);
		locationTypes.add(
			CollectionUtilities.newObjectMap(
				"id", LocationType.GOVERNMENT_CODE,
				"name", "Government"
			)
		);
		locationTypes.add(
			CollectionUtilities.newObjectMap(
				"id", LocationType.EDUCATION_CODE,
				"name", "Education"
			)
		);

		Map<String, Map<Long, String>> states = Maps.newHashMap();

		states.put("United States", formOptionsDataHelper.getStates(Country.USA));
		states.put("Canada", formOptionsDataHelper.getStates(Country.CANADA));

		model.addAttribute("clientsList", clients);
		model.addAttribute("states", states);
		model.addAttribute("locationTypesList", locationTypes);
		return "web/partials/addressbook/location_form";
	}

	@RequestMapping(
		value = "/location/manage",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveLocation(@ModelAttribute("locationForm") LocationForm form) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messages = messageHelper.newBundle();

		if (form == null) {
			messageHelper.addMessage(response, "locations.manage.form.null");
			return response;
		} else if (!StringUtilities.all(form.getName(), form.getAddress1(), form.getCity())) {
			messageHelper.addMessage(response, "locations.manage.fields.empty");
			return response;
		}

		try {
			// Validate address
			LocationDTO locationDTO = locationFormToLocationDTOConverter.convert(form);
			locationDTO.setCompanyId(authenticationService.getCurrentUserCompanyId());
			BindingResult binding = new DataBinder(locationDTO).getBindingResult();
			addressValidator.validate(locationDTO, binding);

			// Check for validation errors
			if (binding.hasErrors()) {
				messageHelper.addMessage(response, "locations.manage.invalid.address");
				return response;
			}

			// Fetch and update Location
			ClientLocation location = crmService.saveOrUpdateClientLocation(form.getClient_company(), locationDTO, messages);
			Assert.notNull(location);

			// Update location contacts
			crmService.removeContactAssociationFromClientContact(location.getId());
			if (CollectionUtils.isNotEmpty(form.getContacts())) {
				for (String contactId : form.getContacts()) {
					crmService.addLocationToClientContact(Long.valueOf(contactId), location.getId());
				}
			}

			// Send back some info about the location added for front-end uses
			response.addData("location", formatLocation(location));
		} catch (Exception ex) {
			logger.error("There was an error editing/adding the location", ex);
			if (messages.hasErrors()) {
				for (String message : messages.getErrors()) {
					messageHelper.addMessage(response, message);
				}
			} else {
				messageHelper.addMessage(response, "locations.manage.exception");
			}
			return response;
		}

		response.setSuccessful(true);
		return response;
	}

	@RequestMapping(
		value = "/location/delete/{id}",
		method = DELETE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteLocation(@PathVariable("id") Long id) {

		AjaxResponseBuilder response = AjaxResponseBuilder.success();
		try {
			ClientLocation deletedLocation = crmService.deleteClientLocationByIdAndCompany(id, getCurrentUser().getCompanyId());
			response.addData("location", ImmutableMap.of("id", deletedLocation.getId()));
		} catch (Exception ex) {
			messageHelper.addMessage(response, "locations.manage.delete.exception");
			response.setSuccessful(false);
		}

		return response;
	}

	@RequestMapping(
			value = "/location/delete",
			method = POST,
			produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteLocations(@RequestParam(value = "ids[]") List<Long> ids) {
		AjaxResponseBuilder response = AjaxResponseBuilder.success();
		Long companyId = getCurrentUser().getCompanyId();
		for (Long id: ids) {
			try {
				crmService.deleteClientLocationByIdAndCompany(id, companyId);
			} catch (Exception ex) {
				messageHelper.addMessage(response, "locations.manage.delete.exception");
			}
		}

		return response;
	}

	@RequestMapping(
		value = "/contact/get_all",
		method = GET)
	public void getAllClientCompanyContacts(Model model, HttpServletRequest httpRequest, RedirectAttributes flash) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			1, ClientContactPagination.SORTS.CLIENT_CONTACT_NAME.toString(),
			2, ClientContactPagination.SORTS.CLIENT_CONTACT_TITLE.toString(),
			4, ClientContactPagination.SORTS.CLIENT_CONTACT_EMAIL.toString()

		));
		request.setFilterMapping(ImmutableMap.<String, Enum<?>>of(
			"sSearch_3", ClientContactPagination.FILTER_KEYS.CLIENT_COMPANY_ID,
			"sSearch_7", ClientContactPagination.FILTER_KEYS.CLIENT_LOCATION_NAME
		));

		ClientContactPagination pagination;
		DataTablesResponse<List<String>, Map<String, Object>> response = new DataTablesResponse<>();
		try {
			pagination = request.newPagination(ClientContactPagination.class);
			pagination = crmService.findAllClientContactsByUser(getCurrentUser().getId(), pagination);
			response = DataTablesResponse.newInstance(request, pagination);
			for (ClientContact clientContact : pagination.getResults()) {
				List<String> row = Lists.newArrayList();
				row.add(clientContact.getFullName());
				row.add(clientContact.getJobTitle());
				if (clientContact.getClientCompany() != null) {
					row.add(clientContact.getClientCompany().getName());
				} else {
					row.add("");
				}

				row.add((clientContact.getMostRecentEmail() != null) ? clientContact.getMostRecentEmail().getEmail() : StringUtils.EMPTY);
				row.add(formatPhoneNumberForTable(clientContact.getMostRecentWorkPhone()));
				row.add(formatPhoneNumberForTable(clientContact.getMostRecentMobilePhone()));

				int locationCount = crmService.getLocationCountByClientContact(clientContact.getId());
				String locationName = locationCount == 0 ? "" : crmService.findFirstLocationNameByClientContact(clientContact.getId());
				row.add("");

				row.add(StringUtilities.toYesNo(clientContact.isManager()));
				row.add("");
				row.add("");

				Map<String,Object> meta = CollectionUtilities.newObjectMap(
					"id", clientContact.getId(),
					"name", clientContact.getFullName(),
					"locationName", locationName,
					"moreLocations", locationCount - 1
				);
				response.addRow(row, meta);
			}
		} catch (Exception ex) {
			messageHelper.addError(messageHelper.newFlashBundle(flash), "contacts.manage.exception");
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/contact/manage",
		method = GET)
	public String displayContact(@ModelAttribute("contactForm") ContactForm form, Model model, RedirectAttributes flash) {

		if (form == null) {
			messageHelper.addError(messageHelper.newFlashBundle(flash), "contacts.manage.exception");
			return "web/partials/addressbook/contact_form";
		}

		if (form.getId() != null) {
			ClientContact contact = crmService.findClientContactById(form.getId());
			if (contact == null) {
				messageHelper.addError(messageHelper.newFlashBundle(flash), "contacts.manage.exception");
				return "web/partials/addressbook/contact_form";
			}

			model.addAttribute("isManager", contact.isManager());
			form.setFirst_name(contact.getFirstName());
			form.setLast_name(contact.getLastName());
			form.setTitle(contact.getJobTitle());
			if (contact.getClientCompany() != null) {
				form.setClient_company(contact.getClientCompany().getId());
			}
			if (contact.getMostRecentEmail() != null) {
				form.setEmail(contact.getMostRecentEmail().getEmail());
			}

			Set<Phone> phoneNumbers = contact.getPhoneNumbers();
			for (Phone phone : phoneNumbers) {
				if (phone.getContactContextType().equals(ContactContextType.HOME)){
					form.setMobile_phone(phone.getPhone());
				} else if (phone.getContactContextType().equals(ContactContextType.WORK)) {
					form.setWork_phone(phone.getPhone());
					form.setWork_phone_ext(phone.getExtension());
				}
			}
		}

		List<Map<String, Object>> clients = crmService.findAllClientCompaniesByCompany(getCurrentUser().getCompanyId(), "id", "name");
		model.addAttribute("clientsList", clients);

		return "web/partials/addressbook/contact_form";
	}

	@RequestMapping(
		value = "/contact/manage",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveContact(@ModelAttribute("contactForm") ContactForm form) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messages = messageHelper.newBundle();

		if (StringUtils.isEmpty(form.getFirst_name())
			|| StringUtils.isEmpty(form.getLast_name())
			|| StringUtils.isEmpty(form.getWork_phone())) {
			messageHelper.addMessage(response, "contacts.manage.fields.empty");
		}
		if (StringUtils.isNotEmpty(form.getEmail())
			&& !EmailValidator.getInstance().isValid(form.getEmail())) {
			messageHelper.addMessage(response, "addressbook.email.invalid");
		}
		if (StringUtils.isNotEmpty(form.getWork_phone())
			&& form.getWork_phone().length() < Phone.phoneLengthWithFormat) {
			messageHelper.addMessage(response, "addressbook.phone.invalid");
		}
		if (StringUtils.isNotEmpty(form.getMobile_phone())
			&& form.getMobile_phone().length() < Phone.phoneLengthWithFormat) {
			messageHelper.addMessage(response, "addressbook.mobile.invalid");
		}

		if (response.hasMessages()) {
			return response;
		}

		ClientContactDTO clientContactDTO = new ClientContactDTO();
		clientContactDTO.setClientCompanyId(form.getClient_company());
		clientContactDTO.setFirstName(form.getFirst_name());
		clientContactDTO.setLastName(form.getLast_name());
		clientContactDTO.setJobTitle(form.getTitle());
		clientContactDTO.setEmail(form.getEmail());
		clientContactDTO.setManager(form.getTitle().equalsIgnoreCase("manager") || form.getIs_manager() != null);

		if (form.getId() != null) {
			clientContactDTO.setContactId(form.getId());
		}

		ClientContact contact;
		try {
			contact = crmService.saveOrUpdateClientContact(getCurrentUser().getCompanyId(), clientContactDTO, messages);
			Assert.notNull(contact);

			crmService.removeLocationAssociationsFromClientContact(contact.getId());
			if (!CollectionUtilities.isEmpty(form.getLocations())) {
				crmService.removeLocationAssociationsFromClientContact(contact.getId());
				for (String locationId : form.getLocations()) {
					crmService.addLocationToClientContact(contact.getId(), Long.valueOf(locationId));
				}
			}

			if (StringUtils.isEmpty(form.getEmail())) {
				crmService.removeEmailAssociationsFromClientContact(contact.getId());
			} else {
				EmailAddressDTO emailAddressDTO = new EmailAddressDTO();
				emailAddressDTO.setEmail(form.getEmail());
				emailAddressDTO.setContactContextType(ContactContextType.WORK);
				crmService.addEmailToClientContact(contact.getId(), emailAddressDTO);
			}

			if (StringUtils.isEmpty(form.getWork_phone())) {
				crmService.removePhoneAssociationsFromClientContact(contact.getId(), ContactContextType.WORK);
			} else {
				PhoneNumberDTO workPhoneNumberDTO = new PhoneNumberDTO();
				workPhoneNumberDTO.setPhone(form.getWork_phone());
				if (StringUtils.isNotEmpty(form.getWork_phone_ext())) {
					workPhoneNumberDTO.setExtension(form.getWork_phone_ext());
				}
				workPhoneNumberDTO.setContactContextType(ContactContextType.WORK);
				crmService.addPhoneToClientContact(contact.getId(), workPhoneNumberDTO);
			}

			if (StringUtils.isEmpty(form.getMobile_phone())) {
				crmService.removePhoneAssociationsFromClientContact(contact.getId(), ContactContextType.HOME);
			} else {
				PhoneNumberDTO mobilePhoneNumberDTO = new PhoneNumberDTO();
				mobilePhoneNumberDTO.setPhone(form.getMobile_phone());
				mobilePhoneNumberDTO.setContactContextType(ContactContextType.HOME);
				crmService.addPhoneToClientContact(contact.getId(), mobilePhoneNumberDTO);
			}

		} catch (Exception ex) {
			if (messages.hasErrors()) {
				for (String message : messages.getErrors()) {
					messageHelper.addMessage(response, message);
				}
			} else {
				messageHelper.addMessage(response, "contacts.manage.exception");
			}
			return response;
		}

		response.setSuccessful(true);
		return response;
	}

	@RequestMapping(
		value = "/contact/delete/{id}",
		method = DELETE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteContact(@PathVariable("id") Long id) {

		AjaxResponseBuilder response = AjaxResponseBuilder.success();
		try {
			crmService.deleteClientContactByIdAndCompany(getCurrentUser().getCompanyId(), id);
		} catch (Exception ex) {
			messageHelper.addMessage(response, "clients.manage.delete.exception");
			response.setSuccessful(false);
		}
		return response;
	}

	@RequestMapping(
		value = "/location/get_contacts/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getLocationContacts(@PathVariable("id") Long id) {

		AjaxResponseBuilder response = AjaxResponseBuilder.success();

		List<Map<String, Object>> formattedResults;
		try {
			formattedResults = formatContactsForFormSelection(crmService.findAllClientContactsByLocation(id, true));
		} catch (Exception e) {
			formattedResults = Lists.newArrayList();
			messageHelper.addMessage(response, "locations.manage.contacts");
			response.setSuccessful(false);
		}

		response.addData("contacts", formattedResults);
		return response;
	}

	@RequestMapping(
		value = "/contact/get_locations/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getContactLocations(@PathVariable("id") Long id) {

		AjaxResponseBuilder response = AjaxResponseBuilder.success();

		List<ClientLocation> locations;
		try {
			locations = crmService.findAllLocationsByClientContact(id);
		} catch (Exception ex) {
			messageHelper.addMessage(response, "contacts.manage.locations");
			response.setSuccessful(false);
			return response;
		}

		List<Map<String, Object>> formattedResults = Lists.newArrayList();
		for (ClientLocation location : locations) {
			formattedResults.add(ImmutableMap.<String, Object>of(
				"id", location.getId(),
				"name", location.getName(),
				"number", location.getLocationNumber() == null ? "" : location.getLocationNumber()
			));
		}

		response.addData("locations", formattedResults);
		return response;
	}

	@RequestMapping(
		value = "/get_locations/{client_id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getLocationsByClient(@PathVariable("client_id") Long clientId) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		List<ClientLocation> locations = crmService.findAllLocationsByClientCompany(getCurrentUser().getCompanyId(), clientId);

		List<Map<Long, String>> data = Lists.newArrayList();
		for (ClientLocation location : locations) {
			Map<Long, String> entry = Maps.newLinkedHashMap();
			entry.put(location.getId(), location.getName());
			data.add(entry);
		}

		response.addData("locations", data);
		response.setSuccessful(true);
		return response;
	}

	@RequestMapping(
		value = "/get_clientcontacts.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getClientContacts(
		@RequestParam(required = false) Long id,
		@RequestParam String contactFilter) {

		AjaxResponseBuilder response = AjaxResponseBuilder.success();

		ClientContactPagination pagination = new ClientContactPagination();
		pagination.setResultsLimit(FORM_RESULTS_LIMIT);
		pagination.addFilter(ClientContactPagination.FILTER_KEYS.CLIENT_CONTACT_NAME, contactFilter);

		if (id != null) {
			pagination.addFilter(ClientContactPagination.FILTER_KEYS.CLIENT_COMPANY_ID, id);
		}

		List<Map<String, Object>> formattedResults;
		try {
			pagination = crmService.findAllClientContactsByUser(getCurrentUser().getId(), pagination);
			formattedResults = formatContactsForFormSelection(pagination.getResults());
		} catch (Exception e) {
			formattedResults = Lists.newArrayList();
			messageHelper.addMessage(response, "locations.manage.contacts");
			response.setSuccessful(false);
		}

		response.addData("contacts", formattedResults);
		return response;
	}

	@RequestMapping(
		value = "/get_clientlocations.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getClientLocations(
		@RequestParam(required = false) Long id,
		@RequestParam String locationFilter) {

		AjaxResponseBuilder response = AjaxResponseBuilder.success();

		ClientLocationPagination pagination = new ClientLocationPagination();
		pagination.setResultsLimit(FORM_RESULTS_LIMIT);
		pagination.addFilter(ClientLocationPagination.FILTER_KEYS.LOCATION_NAME, locationFilter);

		List<Map<String, Object>> formattedResults;
		try {
			if (id == null) {
				pagination = crmService.findAllLocations(getCurrentUser().getCompanyId(), pagination);
			} else {
				pagination.addFilter(ClientLocationPagination.FILTER_KEYS.CLIENT_ID, id);
				pagination = crmService.findAllLocationsByClientCompany(getCurrentUser().getCompanyId(), id, pagination);
			}
			formattedResults = formatLocationsForFormSelection(pagination.getResults());
		} catch (Exception e) {
			formattedResults = Lists.newArrayList();
			messageHelper.addMessage(response, "contacts.manage.locations.general");
			response.setSuccessful(false);
		}

		response.addData("locations", formattedResults);
		return response;
	}

	@RequestMapping(
		value = "/import",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder uploadContent(@RequestParam("type") String importType, @RequestParam("uuid") String uuid) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		MessageBundle messages = messageHelper.newBundle();

		if (StringUtils.isEmpty(importType)) {
			messageHelper.addError(messages, "addressbook.upload.invalid.type");
			return response;
		} else if (StringUtils.isEmpty(uuid)){
			messageHelper.addError(messages, "addressbook.upload.file.error");
			return response;
		}

		try {
			crmService.bulkUploadFileRead(getCurrentUser().getCompanyId(), importType, uuid, messages);
		} catch (ContactManagerImportRowLimitExceededException ex) {
			messageHelper.addError(messages, ex.getWhy());
		} catch (Exception ex) {
			messageHelper.addError(messages, "addressbook.upload.file.error");
		}

		if (messages.hasErrors()) {
			for (String error : messages.getErrors()) {
				messageHelper.addMessage(response, error);
			}
			return response;
		}

		response.setSuccessful(true);
		return response;
	}

	@RequestMapping(
		value = "/location/map/{client_id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getLocationsForMap(@PathVariable("client_id") Long clientId) {

		AjaxResponseBuilder response = AjaxResponseBuilder.success();

		try {
			List<ClientLocation> locations = crmService.findAllLocationsByClientCompany(getCurrentUser().getCompanyId(), clientId);
			response.addData("locations", formatLocations(locations));
		} catch (Exception ex) {
			logger.error("There was an error fetching client locations for contact manager map", ex);
			messageHelper.addMessage(response, "locations.map.exception");
			response.setSuccessful(false);
		}

		return response;
	}

	private List<Map<String, Object>> formatLocations(List<ClientLocation> locations) {

		List<Map<String, Object>> formattedLocations = Lists.newArrayList();
		for (ClientLocation location : locations) {
			formattedLocations.add(formatLocation(location));
		}

		return formattedLocations;
	}

	private Map<String, Object> formatLocation(ClientLocation location) {
		String clientCompanyName = "";
		Long clientId = null;
		ClientCompany clientCompany = location.getClientCompany();
		if (clientCompany != null && clientCompany.getName() != null) {
			clientCompanyName = clientCompany.getName();
			clientId = clientCompany.getId();
		}

		String longitude = "";
		String latitude = "";
		Address address = location.getAddress();
		if (address != null && address.getLongitude() != null && address.getLatitude() != null) {
			longitude = String.valueOf(address.getLongitude());
			latitude =  String.valueOf(address.getLatitude());
		}

		int contactCount = crmService.getContactCountByClientLocation(location.getId());
		String firstContactName = contactCount == 0 ? "" : crmService.findFirstContactNameByClientLocation(location.getId());

		return CollectionUtilities.newObjectMap(
			"id", location.getId(),
			"number", location.getLocationNumber() == null ? "" : location.getLocationNumber(),
			"longitude", longitude,
			"latitude", latitude,
			"name", location.getName(),
			"client", clientCompanyName,
			"clientId", clientId,
			"address", AddressUtilities.formatAddressLongWithLongState(address, ", "),
			"contact", firstContactName,
			"moreContacts", contactCount - 1);
	}

	private List<Map<String, Object>> formatContactsForFormSelection(List<ClientContact> contacts) {
		List<Map<String, Object>> formattedResults = Lists.newArrayList();
		for (ClientContact contact : contacts) {
			formattedResults.add(ImmutableMap.<String, Object>of(
				"id", contact.getId(),
				"name", contact.getFullName(),
				"number", contact.getMostRecentWorkPhone() == null ? "" : contact.getMostRecentWorkPhone().getPhone(),
				"email", contact.getMostRecentEmail() == null ? "" : contact.getMostRecentEmail().getEmail()
			));
		}
		return formattedResults;
	}

	private List<Map<String, Object>> formatLocationsForFormSelection(List<ClientLocation> locations) {
		List<Map<String, Object>> formattedResults = Lists.newArrayList();
		for (ClientLocation location : locations) {
			formattedResults.add(ImmutableMap.<String, Object>of(
				"id", location.getId(),
				"name", location.getName(),
				"number", location.getLocationNumber() == null ? "" : location.getLocationNumber()
			));
		}
		return formattedResults;
	}

	private static String formatPhoneNumberForTable(Phone workPhone) {
		return workPhone != null ?
			StringUtilities.formatPhoneNumber(workPhone.getPhone()) + (workPhone.getExtension() != null ? " ext " + workPhone.getExtension() : StringUtils.EMPTY) :
			StringUtils.EMPTY;
	}
}
