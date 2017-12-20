package com.workmarket.service.business;

import au.com.bytecode.opencsv.CSVReader;
import com.google.api.client.util.Maps;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.ClientLocationDAO;
import com.workmarket.dao.DressCodeDAO;
import com.workmarket.dao.LocationDAO;
import com.workmarket.dao.LocationTypeDAO;
import com.workmarket.dao.UserAvailabilityDAO;
import com.workmarket.dao.crm.ClientCompanyDAO;
import com.workmarket.dao.crm.ClientCompanyPhoneAssociationDAO;
import com.workmarket.dao.crm.ClientCompanyWebsiteAssociationDAO;
import com.workmarket.dao.crm.ClientContactDAO;
import com.workmarket.dao.crm.ClientContactEmailAssociationDAO;
import com.workmarket.dao.crm.ClientContactLocationAssociationDAO;
import com.workmarket.dao.crm.ClientContactPhoneAssociationDAO;
import com.workmarket.dao.crm.ClientLocationPhoneAssociationDAO;
import com.workmarket.dao.postalcode.StateDAO;
import com.workmarket.data.dataimport.adapter.ContactManagerImportRowLimitExceededException;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientCompanyPagination;
import com.workmarket.domains.model.crm.ClientCompanyPhoneAssociation;
import com.workmarket.domains.model.crm.ClientCompanyWebsiteAssociation;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactEmailAssociation;
import com.workmarket.domains.model.crm.ClientContactLocationAssociation;
import com.workmarket.domains.model.crm.ClientContactPagination;
import com.workmarket.domains.model.crm.ClientContactPhoneAssociation;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.crm.ClientLocationAvailability;
import com.workmarket.domains.model.crm.ClientLocationPagination;
import com.workmarket.domains.model.crm.ClientLocationPhoneAssociation;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.directory.Website;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.model.user.WorkAvailability;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.WebsiteDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.ProjectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.ClientCompanyForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.AddressBookContactValidator;
import com.workmarket.web.validators.AddressBookLocationValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.service.business.CRMServiceImpl.ContactColumn.EMAIL;
import static com.workmarket.service.business.CRMServiceImpl.ContactColumn.FIRST_NAME;
import static com.workmarket.service.business.CRMServiceImpl.ContactColumn.LAST_NAME;
import static com.workmarket.service.business.CRMServiceImpl.ContactColumn.MANAGER;
import static com.workmarket.service.business.CRMServiceImpl.ContactColumn.MOBILE_NUMBER;
import static com.workmarket.service.business.CRMServiceImpl.ContactColumn.TITLE;
import static com.workmarket.service.business.CRMServiceImpl.ContactColumn.WORK_NUMBER;
import static com.workmarket.service.business.CRMServiceImpl.LocationColumn.LOCATION_ADDRESS;
import static com.workmarket.service.business.CRMServiceImpl.LocationColumn.LOCATION_ADDRESS_2;
import static com.workmarket.service.business.CRMServiceImpl.LocationColumn.LOCATION_CITY;
import static com.workmarket.service.business.CRMServiceImpl.LocationColumn.LOCATION_NAME;
import static com.workmarket.service.business.CRMServiceImpl.LocationColumn.LOCATION_STATE;
import static com.workmarket.service.business.CRMServiceImpl.LocationColumn.LOCATION_TYPE;
import static com.workmarket.service.business.CRMServiceImpl.LocationColumn.LOCATION_ZIP;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class CRMServiceImpl implements CRMService {

	private static final Log logger = LogFactory.getLog(CRMServiceImpl.class);

	public final static String CONTACT = "contact";
	public final static String LOCATION = "location";
	public final static int MAX_UPLOAD_CONTACTS_LOCATIONS = 500;
	public final static int RESULTS_LIMIT = 10;

	@Autowired private ClientCompanyDAO clientCompanyDAO;
	@Autowired private LocationDAO locationDAO;
	@Autowired private ClientLocationDAO clientLocationDAO;
	@Autowired private DressCodeDAO dressCodeDAO;
	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private LocationTypeDAO locationTypeDAO;
	@Autowired private CompanyService companyService;
	@Autowired private ClientContactDAO clientContactDAO;
	@Autowired private DirectoryService directoryService;
	@Autowired private ClientContactPhoneAssociationDAO clientContactPhoneAssociationDAO;
	@Autowired private ClientCompanyWebsiteAssociationDAO clientCompanyWebsiteAssociationDAO;
	@Autowired private ClientCompanyPhoneAssociationDAO clientCompanyPhoneAssociationDAO;
	@Autowired private ClientContactEmailAssociationDAO clientContactEmailAssociationDAO;
	@Autowired private ClientLocationPhoneAssociationDAO clientLocationPhoneAssociationDAO;
	@Autowired private ClientContactLocationAssociationDAO clientContactLocationAssociationDAO;
	@Autowired private UserAvailabilityDAO userAvailabilityDAO;
	@Autowired private AddressService addressService;
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AddressBookLocationValidator addressValidator;
	@Autowired private AddressBookContactValidator contactValidator;
	@Autowired private StateDAO stateDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private LocationService locationService;

	@Override
	public ClientCompany saveOrUpdateClientCompany(Long userId, ClientCompanyDTO clientCompanyDTO, MessageBundle messages) {
		Assert.notNull(userId);
		Assert.notNull(clientCompanyDTO);

		ClientCompany clientCompany;

		if (clientCompanyDTO.getClientCompanyId() != null) {
			clientCompany = clientCompanyDAO.findClientCompanyById(clientCompanyDTO.getClientCompanyId());
			Assert.notNull(clientCompany, "Unable to find client company");
		} else {
			Company company = profileService.findCompany(userId);
			Assert.notNull(company);
			clientCompany = findClientCompanyByName(company.getId(), clientCompanyDTO.getName());
			if (clientCompany == null) {
				clientCompany = new ClientCompany(company);
			} else if(clientCompany.getDeleted()) {
				clientCompany.setDeleted(false);
			} else {
				if(messages != null) {
					messageHelper.addError(messages, "Client company with this name already exists.");
				}
				Assert.state(false, "Client company with this name already exists.");
			}
		}

		Industry industry = null;
		if (clientCompanyDTO.getIndustryId() != null) {
			industry = invariantDataService.findIndustry(clientCompanyDTO.getIndustryId());
		}

		clientCompany.setIndustry(industry);
		BeanUtils.copyProperties(clientCompanyDTO, clientCompany);
		clientCompanyDAO.saveOrUpdate(clientCompany);

		if (StringUtils.isNotBlank(clientCompanyDTO.getWebsite())) {
			//Should always be just one so this for loop is ok.
			for (ClientCompanyWebsiteAssociation websiteAssociation : clientCompany.getWebsiteAssociations()) {
				websiteAssociation.setDeleted(true);
			}
			WebsiteDTO websiteDTO = new WebsiteDTO();
			websiteDTO.setWebsite(clientCompanyDTO.getWebsite());
			addWebsiteToClientCompany(clientCompany.getId(), websiteDTO);
		}

		if (StringUtils.isNotBlank(clientCompanyDTO.getPhoneNumber())) {
			// Should always be just one so this for loop is ok.
			for (ClientCompanyPhoneAssociation phoneAssociation : clientCompany.getPhoneAssociations()) {
				phoneAssociation.setDeleted(true);
			}
			PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
			phoneNumberDTO.setPhone(clientCompanyDTO.getPhoneNumber());
			if (StringUtils.isNotBlank(clientCompanyDTO.getPhoneExtension())) {
				phoneNumberDTO.setExtension(clientCompanyDTO.getPhoneExtension());
			}
			addPhoneToClientCompany(clientCompany.getId(), phoneNumberDTO);
		}

		return clientCompany;
	}

	@Override
	public ClientCompany saveOrUpdateClientCompany(ClientCompany clientCompany) {
		Assert.notNull(clientCompany);
		clientCompanyDAO.saveOrUpdate(clientCompany);
		return clientCompany;
	}

	@Override
	public ClientLocation saveOrUpdateClientLocation(Long clientCompanyId, LocationDTO locationDTO, MessageBundle bundle) {
		Assert.notNull(locationDTO);

		ClientLocation clientLocation;
		Address address;

		if (locationDTO.getId() != null) {
			clientLocation = locationDAO.findLocationById(ClientLocation.class, locationDTO.getId());
			Assert.notNull(clientLocation);

			address = clientLocation.getAddress();
			if (address == null) {
				address = new Address(AddressType.CLIENT_LOCATION);
			}
		} else {
			clientLocation = new ClientLocation();
			address = new Address(AddressType.CLIENT_LOCATION);
			Assert.notNull(locationDTO.getCompanyId());
			Company company = companyService.findCompanyById(locationDTO.getCompanyId());
			Assert.notNull(company);
			clientLocation.setCompany(company);
		}

		if (locationDTO.getPostalCode() == null) {
			locationDTO.setPostalCode("");
		}
		if (StringUtils.isBlank(locationDTO.getState())) {
			locationDTO.setState(Constants.NO_STATE);
		}
		BeanUtils.copyProperties(locationDTO, clientLocation);
		BeanUtils.copyProperties(locationDTO, address, "country", "state", "id");

		State state = invariantDataService.findStateWithCountryAndState(
				Country.valueOf(locationDTO.getCountry()).getId(), StringUtils.isEmpty(locationDTO.getState()) ? Constants.NO_STATE : locationDTO.getState()
		);
		if (state != null) {
			address.setState(state);
			address.setCountry(state.getCountry());
		} else {
			addressService.addNewStateToAddress(address, locationDTO.getCountry(), locationDTO.getState());
			address.setCountry(Country.valueOf(locationDTO.getCountry()));
		}

		DressCode dressCode = dressCodeDAO.findDressCodeById(locationDTO.getDressCodeId() != null ?
				locationDTO.getDressCodeId() : DressCode.BUSINESS_CASUAL);

		Assert.notNull(dressCode);
		address.setDressCode(dressCode);

		LocationType locationType = locationTypeDAO.findLocationTypeById(locationDTO.getLocationTypeId() != null ?
				locationDTO.getLocationTypeId() : LocationType.COMMERCIAL_CODE);

		Assert.notNull(locationType);
		address.setLocationType(locationType);

		Address verifiedAddress = null;
		try {
			verifiedAddress = addressService.verifyAndSave(address, bundle);
		}  catch (Exception e) {
			logger.error("There was an error parsing address: " + address.getFullAddress(), e);
		}
		if (verifiedAddress == null) {
			return null;
		}

		clientLocation.setAddress(verifiedAddress);

		if (clientCompanyId != null) {
			ClientCompany clientCompany = clientCompanyDAO.get(clientCompanyId);
			Assert.notNull(clientCompany);
			clientLocation.setClientCompany(clientCompany);
		} else {
			clientLocation.setClientCompany(null);
		}
		locationDAO.saveOrUpdate(clientLocation);

		return clientLocation;
	}

	@Override
	public ClientContact saveOrUpdateClientContact(Company company, ClientContactDTO clientContactDTO) {
		Assert.notNull(company);
		Assert.notNull(clientContactDTO);

		Long clientLocationClientCompanyId = null;
		ClientCompany clientCompany = null;
		ClientContact clientContact;
		Location location;
		ClientLocation clientLocation;

		// Check if contact already exists. If not, create one.
		if (clientContactDTO.getContactId() != null) {
			clientContact = clientContactDAO.get(clientContactDTO.getContactId());
			Assert.notNull(clientContact, "Unable to find Client Contact");
		} else {
			clientContact = getNewClientContact(company); // for CRMServiceTest
		}

		// Check for association with client company
		if (clientContactDTO.getClientCompanyId() != null) {
			clientCompany = clientCompanyDAO.get(clientContactDTO.getClientCompanyId());
			Assert.notNull(clientCompany, "Unable to find Client Company");
		}

		// Check for association with location
		if (clientContactDTO.getClientLocationId() != null) {
			location = locationDAO.findLocationById(Location.class, clientContactDTO.getClientLocationId());
			Assert.notNull(location, "Unable to find location for location contact");

			// Test to see if the location was saved as a client location
			clientLocation = locationDAO.findLocationById(ClientLocation.class, location.getId());
			if (clientLocation != null && clientLocation.getClientCompany() != null) {
				clientLocationClientCompanyId = clientLocation.getClientCompany().getId();
			}
		}

		// If contact is associated with a client company, and is associated with a client location
		// Assert that the client company id is the same as the client company id associated with the location
		if (clientCompany != null && clientLocationClientCompanyId != null) {
			Assert.isTrue(clientCompany.getId().equals(clientLocationClientCompanyId), "Contact's client company and contact's location's client company don't match");
		}

		BeanUtils.copyProperties(clientContactDTO, clientContact);
		clientContact.setClientCompany(clientCompany);
		clientContactDAO.saveOrUpdate(clientContact);

		return clientContact;

	}

	@Override
	public ClientContact saveOrUpdateClientContact(Long companyId, ClientContactDTO clientContactDTO, MessageBundle bundle) {
		Assert.notNull(clientContactDTO);
		Assert.notNull(companyId, "Company Id can't be null");
		Assert.notNull(clientContactDTO.getLastName(), "Last name can't be null");
		Assert.notNull(clientContactDTO.getFirstName(), "First name can't be null");

		ClientContact clientContact;
		ClientCompany clientCompany = null;
		Location location;
		ClientLocation clientLocation;
		Long clientLocationId = null;
		Long clientLocationClientCompanyId = null;

		Company company = companyService.findCompanyById(companyId);
		Assert.notNull(company, "Unable to find Company");

		// Check if contact already exists. If not, create one.
		if (clientContactDTO.getContactId() != null) {
			clientContact = clientContactDAO.get(clientContactDTO.getContactId());
			Assert.notNull(clientContact, "Unable to find Client Contact");
		} else {
			clientContact = getNewClientContact(company); // for CRMServiceTest
		}

		// Check for association with client company
		if (clientContactDTO.getClientCompanyId() != null) {
			clientCompany = clientCompanyDAO.get(clientContactDTO.getClientCompanyId());
			Assert.notNull(clientCompany, "Unable to find Client Company");
		}

		// Check for association with location
		if (clientContactDTO.getClientLocationId() != null) {
			location = locationDAO.findLocationById(Location.class, clientContactDTO.getClientLocationId());
			Assert.notNull(location, "Unable to find location for location contact");

			// Test to see if the location was saved as a client location
			clientLocation = locationDAO.findLocationById(ClientLocation.class, location.getId());
			if (clientLocation != null) {
				clientLocationId = location.getId();
				if (clientLocation.getClientCompany() != null) {
					clientLocationClientCompanyId = clientLocation.getClientCompany().getId();
				}
			}
		}

		// If contact is associated with a client company, and is associated with a client location
		// Assert that the client company id is the same as the client company id associated with the location
		if (clientCompany != null && clientLocationClientCompanyId != null) {
			Assert.isTrue(clientCompany.getId().equals(clientLocationClientCompanyId), "Contact's client company and contact's location's client company don't match");
		}

		BeanUtils.copyProperties(clientContactDTO, clientContact);
		clientContact.setClientCompany(clientCompany);
		clientContactDAO.saveOrUpdate(clientContact);

		if (clientLocationId != null) {
			addLocationToClientContact(clientContact.getId(), clientLocationId);
		}

		for (EmailAddressDTO dto : clientContactDTO.getEmails()) {
			addEmailToClientContact(clientContact.getId(), dto);
		}

		for (PhoneNumberDTO dto : clientContactDTO.getPhoneNumbers()) {
			addPhoneToClientContact(clientContact.getId(), dto);
		}

		return clientContact;
	}

	public ClientContact getNewClientContact(Company company) {
		return new ClientContact(company);
	}

	@Override
	public ClientCompany findClientCompanyById(Long id) {
		Assert.notNull(id);
		return clientCompanyDAO.findClientCompanyById(id);
	}

	@Override
	public ClientCompany findClientCompanyByIdAndCompany(Long clientCompanyId, Long companyId) {
		Assert.notNull(clientCompanyId);
		Assert.notNull(companyId);
		return clientCompanyDAO.findClientCompanyByIdAndCompany(clientCompanyId, companyId);
	}

	@Override
	public List<ClientCompany> findClientCompanyByNumberAndCompany(String clientCompanyNumber, Long companyId) {
		Assert.notNull(clientCompanyNumber);
		Assert.notNull(companyId);
		return clientCompanyDAO.findClientCompanyByNumberAndCompany(clientCompanyNumber, companyId);
	}

	@Override
	public ClientContact findClientContactByIdAndCompany(Long companyId, Long contactId) {
		Assert.notNull(contactId);
		return clientContactDAO.findClientCompanyByIdAndCompany(companyId, contactId);
	}

	@Override
	public ClientCompany findClientCompanyByName(long companyId, String name) {
		Assert.hasText(name);
		return clientCompanyDAO.findClientCompanyByName(companyId, name);
	}

	@Override
	public ClientLocation findClientLocationById(Long id) {
		Assert.notNull(id);
		ClientLocation location = locationDAO.findLocationById(ClientLocation.class, id);
		if (location != null) {
			Hibernate.initialize(location.getPhoneAssociations());
			Hibernate.initialize(location.getAvailableHours());
			if (location.getClientCompany() != null) {
				Hibernate.initialize(location.getClientCompany());
			}
		}
		return location;
	}

	@Override
	public ClientLocation findClientLocationByIdAndCompany(Long locationId, Long companyId) {
		Assert.notNull(locationId);
		ClientLocation location = locationDAO.findLocationByIdAndCompany(ClientLocation.class, locationId, companyId);
		if (location != null) {
			Hibernate.initialize(location.getPhoneAssociations());
			Hibernate.initialize(location.getAvailableHours());
		}
		return location;
	}

	@Override
	public ClientContact findClientContactById(Long id) {
		Assert.notNull(id);
		return clientContactDAO.findContactById(id);
	}

	@Override
	public ClientCompanyPagination findAllClientCompanyByUser(Long userId, ClientCompanyPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		Company company = profileService.findCompany(userId);
		Assert.notNull(company);
		return clientCompanyDAO.findClientCompanyByCompanyId(company.getId(), pagination);
	}

	@Override
	public List<ClientCompany> findAllClientCompanyByUser(Long userId) {
		Assert.notNull(userId);
		Company company = profileService.findCompany(userId);
		Assert.notNull(company);
		return clientCompanyDAO.findClientCompanyByCompanyId(company.getId());
	}

	@Override
	public List<ClientCompany> findAllClientCompanyByCompany(Long companyId) {
		Assert.notNull(companyId);
		return clientCompanyDAO.findClientCompanyByCompanyId(companyId);
	}

	@Override
	public List<Map<String, Object>> findAllClientCompaniesByCompany(Long companyId, String... fields) {
		Assert.notNull(companyId);
		return clientCompanyDAO.findAllClientCompaniesByCompanyId(companyId, fields);
	}

	@Override
	public List<Map<String, Object>> findAllClientCompaniesByCompanyWithLocationCount(Long companyId) {
		Assert.notNull(companyId);
		return clientCompanyDAO.findAllClientCompaniesByCompanyWithLocationCount(companyId);
	}

	@Override
	public List<ClientLocation> findAllLocationsByClientCompany(Long companyId, Long clientCompanyId) {
		Assert.notNull(companyId);
		return locationDAO.findAllLocationsByClientCompany(companyId, clientCompanyId);
	}

	@Override
	public List<ClientLocation> findAllLocationsByClientContact(Long clientContactId) {
		Assert.notNull(clientContactId);
		return clientContactLocationAssociationDAO.findAllLocationsByClientContact(clientContactId);
	}

	@Override
	public List<ClientLocation> findLocationByNumberAndCompany(String locationNumber, Long companyId) {
		Assert.hasText(locationNumber);
		Assert.notNull(companyId);
		return locationDAO.findLocationByNumberAndCompany(locationNumber, companyId);
	}

	@Override
	public String findFirstLocationNameByClientContact(Long clientContactId) {
		Assert.notNull(clientContactId);
		return clientContactLocationAssociationDAO.findFirstLocationNameByClientContact(clientContactId);
	}

	@Override
	public String findFirstContactNameByClientLocation(Long clientLocationId) {
		Assert.notNull(clientLocationId);
		return clientContactLocationAssociationDAO.findFirstContactNameByClientLocation(clientLocationId);

	}

	@Override
	public int getContactCountByClientLocation(Long clientLocationId) {
		Assert.notNull(clientLocationId);
		return clientContactLocationAssociationDAO.getContactCountByClientLocation(clientLocationId);
	}

	@Override
	public int getLocationCountByClientContact(Long clientContactId) {
		Assert.notNull(clientContactId);
		return clientContactLocationAssociationDAO.getLocationCountByClientContact(clientContactId);
	}

	@Override
	public ClientLocationPagination findAllLocationsByClientCompany(Long companyId, Long clientCompanyId, ClientLocationPagination pagination) {
		Assert.notNull(clientCompanyId);
		Assert.notNull(pagination);
		Assert.notNull(companyId);
		return locationDAO.findAllLocationsByClientCompany(companyId, clientCompanyId, pagination);
	}

	@Override
	public ClientLocationPagination findAllLocations(Long companyId, ClientLocationPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return locationDAO.findAllLocations(companyId, pagination);
	}

	@Override
	public List<ClientLocation> findLocationsByNumberAndClientCompany(String locationNumber, Long clientCompanyId) {
		Assert.hasText(locationNumber);
		Assert.notNull(clientCompanyId);
		return locationDAO.findLocationsByNumberAndClientCompany(locationNumber, clientCompanyId);
	}

	@Override
	public ClientContactPagination findAllClientContactsByUser(Long userId, ClientContactPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		Company company = profileService.findCompany(userId);
		Assert.notNull(company);

		if (pagination.getFilters() == null) {
			pagination.setFilters(new HashMap<String, String>());
		}
		pagination.getFilters().put(ClientContactPagination.FILTER_KEYS.COMPANY_ID.toString(), company.getId().toString());

		return clientContactDAO.findClientContacts(pagination);
	}

	@Override
	public List<ClientContact> findAllClientContactsByCompany(Long companyId) {
		Assert.notNull(companyId);

		return clientContactDAO.findClientContactsByCompany(companyId);
	}

	@Override
	public List<ClientContact> findAllClientContactsByLocation(Long clientLocationId, boolean withAssociations) {
		Assert.notNull(clientLocationId);
		List<ClientContact> contacts = clientContactLocationAssociationDAO.findAllClientContactsByLocation(clientLocationId);

		if (withAssociations) {
			for (ClientContact contact : contacts) {
				Hibernate.initialize(contact.getPhoneAssociations());
				Hibernate.initialize(contact.getEmailAssociations());
				Hibernate.initialize(contact.getWebsiteAssociations());
			}
		}

		return contacts;
	}

	@Override
	public List<ClientContact> findIndividualClientContactsByClientCompanyId(Long clientCompanyId) {
		Assert.notNull(clientCompanyId);
		return clientContactDAO.findIndividualClientContactsByClientCompanyId(clientCompanyId);
	}

	@Override
	public ClientContact findClientContactByClientLocationAndName(Long clientLocationId, String firstName, String lastName) {
		return clientContactDAO.findClientContactByClientLocationAndName(clientLocationId, firstName, lastName);
	}

	@Override
	public ClientContact findClientContactByCompanyIdAndName(Long companyId, String firstName, String lastName) {
		return clientContactDAO.findClientContactByCompanyIdAndName(companyId, firstName, lastName);
	}

	@Override
	public ClientContact findClientContactByClientLocationNamePhone(Long clientLocationId, String firstName, String lastName, String phone, String extension) {
		return clientContactDAO.findClientContactByClientLocationNamePhone(clientLocationId, firstName, lastName, phone, extension);
	}

	@Override
	public ClientContact findClientContactByCompanyIdNamePhone(Long companyId, String firstName, String lastName, String phone, String extension) {
		return clientContactDAO.findClientContactByCompanyIdNamePhone(companyId, firstName, lastName, phone, extension);
	}

	@Override
	public List<DressCode> findAllDressCodes() {
		return dressCodeDAO.findAllDressCodes();
	}

	@Override
	public void updateClientCompanyForClientLocation(Long clientLocationId, Long clientCompanyId) {
		Assert.notNull(clientCompanyId);
		Assert.notNull(clientLocationId);

		ClientLocation location = locationDAO.findLocationById(ClientLocation.class, clientLocationId);
		Assert.notNull(location);

		ClientCompany company = clientCompanyDAO.findClientCompanyById(clientCompanyId);
		Assert.notNull(company);

		location.setClientCompany(company);

		clientLocationDAO.saveOrUpdate(location);
	}

	@Override
	public void updateClientCompanyForClientContact(Long clientContactId, Long clientCompanyId) {
		Assert.notNull(clientCompanyId);
		Assert.notNull(clientContactId);

		ClientContact contact = clientContactDAO.findContactById(clientContactId);
		Assert.notNull(contact);

		ClientCompany company = clientCompanyDAO.findClientCompanyById(clientCompanyId);
		Assert.notNull(company);

		contact.setClientCompany(company);

		clientContactDAO.saveOrUpdate(contact);
	}

	@Override
	public void addWebsiteToClientCompany(Long clientCompanyId, WebsiteDTO websiteDTO) {
		ClientCompany clientCompany = findClientCompanyById(clientCompanyId);
		Assert.notNull(clientCompany, "Unable to find Client Company");

		Website webSite = directoryService.saveOrUpdateWebsite(websiteDTO);

		removeWebsiteAssociationsFromClientCompany(clientCompany.getId());

		ClientCompanyWebsiteAssociation websiteAssociation = new ClientCompanyWebsiteAssociation(clientCompany, webSite);
		clientCompanyWebsiteAssociationDAO.saveOrUpdate(websiteAssociation);

		clientCompany.getWebsiteAssociations().add(websiteAssociation);
	}

	@Override
	public void removeWebsiteAssociationsFromClientCompany(Long clientCompanyId) {
		List<ClientCompanyWebsiteAssociation> existingAssociations = clientCompanyWebsiteAssociationDAO.findAllByClientCompanyId(clientCompanyId);
		for (ClientCompanyWebsiteAssociation association : existingAssociations) {
			association.setDeleted(true);
		}
	}

	@Override
	public void addPhoneToClientCompany(Long clientCompanyId, PhoneNumberDTO phoneDTO) {
		ClientCompany clientCompany = findClientCompanyById(clientCompanyId);
		Assert.notNull(clientCompany, "Unable to find Client Company");

		Phone phone = directoryService.saveOrUpdatePhoneNumber(phoneDTO);

		removePhoneAssociationsFromClientCompany(clientCompany.getId());

		ClientCompanyPhoneAssociation phoneAssociation = new ClientCompanyPhoneAssociation(clientCompany, phone);
		clientCompanyPhoneAssociationDAO.saveOrUpdate(phoneAssociation);

		clientCompany.getPhoneAssociations().add(phoneAssociation);
	}

	@Override
	public void removePhoneAssociationsFromClientCompany(Long clientCompanyId) {
		List<ClientCompanyPhoneAssociation> associations = clientCompanyPhoneAssociationDAO.findAllByClientCompanyId(clientCompanyId);
		for(ClientCompanyPhoneAssociation association : associations) {
			association.setDeleted(true);
		}
	}

	@Override
	public void removeContactAssociationFromClientContact(Long clientLocationId) {
		Assert.notNull(clientLocationId);

		List<ClientContactLocationAssociation> associations = clientContactLocationAssociationDAO.findClientContactLocationAssociationByClientLocation(clientLocationId);
		for(ClientContactLocationAssociation association : associations) {
			association.setDeleted(true);
		}
	}

	@Override
	public void addEmailToClientContact(Long clientContactId, EmailAddressDTO emailDTO) {
		if (StringUtils.isNotBlank(emailDTO.getEmail())) {
			ClientContact clientContact = clientContactDAO.get(clientContactId);
			Assert.notNull(clientContact, "Unable to find Client Contact");

			Email emailAddress = directoryService.saveOrUpdateEmailAddress(emailDTO);

			removeEmailAssociationsFromClientContact(clientContactId);

			ClientContactEmailAssociation emailAssociation = new ClientContactEmailAssociation(clientContact, emailAddress);
			clientContactEmailAssociationDAO.saveOrUpdate(emailAssociation);

			clientContact.getEmailAssociations().add(emailAssociation);
		}
	}

	@Override
	public void addEmailsToClientContact(Long clientContactId, List<EmailAddressDTO> emailAddressDTOs) {
		Assert.notNull(clientContactId);
		Assert.notNull(emailAddressDTOs);

		for (EmailAddressDTO emailAddressDTO : emailAddressDTOs) {
			addEmailToClientContact(clientContactId, emailAddressDTO);
		}
	}

	@Override
	public void addPhoneToClientContact(Long clientContactId, PhoneNumberDTO phoneDTO) {
		if (StringUtils.isNotBlank(phoneDTO.getPhone())) {
			ClientContact clientContact = clientContactDAO.get(clientContactId);
			Assert.notNull(clientContact, "Unable to find Client Contact");

			Phone phone = directoryService.saveOrUpdatePhoneNumber(phoneDTO);

			removePhoneAssociationsFromClientContact(clientContactId, phone.getContactContextType());

			ClientContactPhoneAssociation phoneAssociation = new ClientContactPhoneAssociation(clientContact, phone);
			clientContactPhoneAssociationDAO.saveOrUpdate(phoneAssociation);

			clientContact.getPhoneAssociations().add(phoneAssociation);
		}

	}

	@Override
	public void addPhonesToClientContact(Long clientContactId, List<PhoneNumberDTO> phoneNumberDTOs) {
		Assert.notNull(clientContactId);
		Assert.notNull(phoneNumberDTOs);

		for (PhoneNumberDTO phoneNumberDTO : phoneNumberDTOs) {
			addPhoneToClientContact(clientContactId, phoneNumberDTO);
		}

	}

	@Override
	public void addLocationToClientContact(Long clientContactId, Long clientLocationId) {
		Assert.notNull(clientContactId);
		Assert.notNull(clientLocationId);
		ClientContactLocationAssociation association =
				clientContactLocationAssociationDAO.findClientContactLocationAssociationByClientContactAndClientLocation(clientContactId, clientLocationId);
		if (association == null) {
			ClientContact contact = clientContactDAO.findContactById(clientContactId);
			Assert.notNull(contact, "Error in finding contact to add location");

			ClientLocation location = clientLocationDAO.findLocationById(clientLocationId);
			Assert.notNull(location, "Error in finding location");
			association = new ClientContactLocationAssociation(contact, location);

		}
		association.setDeleted(false);
		clientContactLocationAssociationDAO.saveOrUpdate(association);
	}

	@Override
	public void removeLocationAssociationsFromClientContact(Long clientContactId) {
		Assert.notNull(clientContactId);

		List<ClientContactLocationAssociation> associations = clientContactLocationAssociationDAO.findClientContactLocationAssociationByClientContact(clientContactId);
		for (ClientContactLocationAssociation association : associations) {
			association.setDeleted(true);
		}
	}

	@Override
	public void removeEmailAssociationsFromClientContact(Long clientContactId) {
		Assert.notNull(clientContactId);
		List<ClientContactEmailAssociation> associations = clientContactEmailAssociationDAO.findAllByClientContactId(clientContactId);
		for (ClientContactEmailAssociation association : associations) {
			association.setDeleted(true);
		}
	}

	@Override
	public void removePhoneAssociationsFromClientContact(Long clientContactId, ContactContextType type) {
		Assert.notNull(clientContactId);
		List<ClientContactPhoneAssociation> associations = clientContactPhoneAssociationDAO.findByClientContactId(clientContactId);
		for (ClientContactPhoneAssociation association : associations) {
			if(type.equals(association.getPhone().getContactContextType())) {
				association.setDeleted(true);
			}
		}
	}

	@Override
	public void removePhoneFromClientContact(Long clientContactId, Long phoneId) {
		ClientContactPhoneAssociation association = clientContactPhoneAssociationDAO.findByClientContactIdAndPhoneId(clientContactId, phoneId);

		if (association != null) {
			association.setDeleted(true);
		}
	}

	@Override
	public void addPhoneToClientLocation(Long clientLocationId, PhoneNumberDTO phoneDTO) {
		ClientLocation clientLocation = findClientLocationById(clientLocationId);
		Assert.notNull(clientLocation, "Unable to find Client Location");

		Phone phone = directoryService.saveOrUpdatePhoneNumber(phoneDTO);

		ClientLocationPhoneAssociation phoneAssociation = new ClientLocationPhoneAssociation(clientLocation, phone);
		clientLocationPhoneAssociationDAO.saveOrUpdate(phoneAssociation);

		clientLocation.getPhoneAssociations().add(phoneAssociation);
	}

	@Override
	public List<UserAvailability> findClientLocationWeeklyWorkingHours(Long clientLocationId) {
		List<UserAvailability> configuredHours = userAvailabilityDAO.findWeeklyHoursByClientLocation(clientLocationId);
		ClientLocation location = findClientLocationById(clientLocationId);
		Assert.notNull(location, "Unable to find client location");

		String timezoneId = Constants.DEFAULT_TIMEZONE;

		if (location.getAddress() != null) {
			PostalCode postal = invariantDataService.getPostalCodeByCodeCountryStateCity(location.getAddress().getPostalCode(), location.getAddress().getCountry().getId(), location.getAddress().getState().getShortName(), location.getAddress().getCity());
			if (postal != null) {
				timezoneId = postal.getTimeZone().getTimeZoneId();
			}
		}

		// Return a list composed of configured availability for the entire week.
		// Set default values for any days not configured.
		UserAvailability[] hours = new UserAvailability[7];
		for (UserAvailability h : configuredHours)
			hours[h.getWeekDay()] = h;
		for (int i = 0; i < 7; i++) {
			if (hours[i] != null) continue;

			ClientLocationAvailability h = new ClientLocationAvailability();
			h.setWeekDay(i);
			h.setAllDayAvailable(false);
			h.setFromTime(WorkAvailability.getDefaultFromTime(timezoneId));
			h.setToTime(WorkAvailability.getDefaultToTime(timezoneId));
			h.setDeleted((i == Calendar.SATURDAY - 1 || i == Calendar.SUNDAY - 1));
			hours[i] = h;
		}
		return Arrays.asList(hours);
	}


	@Override
	public void deleteClientCompanyByIdAndCompany(Long clientCompanyId, Long companyId, MessageBundle bundle) {
		ClientCompany clientCompany = findClientCompanyByIdAndCompany(clientCompanyId, companyId);
		Assert.notNull(clientCompany, "Unable to find client company");

		if (bundle != null && !clientCompany.isDeletable()) {
			bundle.addError("Unable to delete client " + clientCompany.getName()
					+ " because there are existing locations and contacts associated with this client.");
		}
		Assert.state(clientCompany.isDeletable(), "Unable to delete client "
			+ clientCompany.getName() + " because there are existing locations and contacts associated with this client.");

		String newName = StringUtilities.getDeletedName(clientCompany.getName(), 120);
		clientCompany.setName(newName);
		clientCompany.setDeleted(true);
	}

	@Override
	public ClientLocation deleteClientLocationByIdAndCompany(Long clientLocationId, Long companyId) {
		ClientLocation clientLocation = findClientLocationByIdAndCompany(clientLocationId, companyId);
		Assert.notNull(clientLocation, "Unable to find client location");

		removeContactAssociationFromClientContact(clientLocation.getId());
		clientLocation.setDeleted(true);
		return clientLocation;
	}

	@Override
	public void deleteClientContactByIdAndCompany(Long companyId, Long contactId) {
		Assert.notNull(contactId);
		Assert.notNull(companyId);
		ClientContact clientContact = clientContactDAO.findClientCompanyByIdAndCompany(companyId, contactId);
		Assert.notNull(clientContact, "Unable to find client contact");

		removeLocationAssociationsFromClientContact(contactId);
		clientContact.setDeleted(true);
	}

	@Override
	public void updateClientContactProperties(Long clientContactId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		Assert.notNull(clientContactId, "Unable to find client contact");
		Assert.notNull(properties, "Properties must be provided");

		ClientContact clientContact = findClientContactById(clientContactId);
		Assert.notNull(clientContact, "Unable to find client contact");

		if (properties.keySet().contains("clientCompany.id")) {
			ClientCompany clientCompany = null;
			String value = properties.get("clientCompany.id");

			if (!value.toLowerCase().equals("null")) {
				clientCompany = clientCompanyDAO.get(Long.parseLong(properties.get("clientCompany.id")));
				Assert.notNull(clientCompany);
			}

			clientContact.setClientCompany(clientCompany);
			properties.remove("clientCompany.id");
		}

		if (properties.keySet().contains("clientLocation.id")) {
			ClientLocation clientLocation = null;
			String value = properties.get("clientLocation.id");

			if (!value.toLowerCase().equals("null")) {
				clientLocation = findClientLocationById(Long.parseLong(properties.get("clientLocation.id")));
				Assert.notNull(clientLocation, "Unable to find ClientLocation");
			}

			clientContact.setClientLocation(clientLocation);
			properties.remove("clientLocation.id");
		}

		BeanUtilities.updateProperties(clientContact, properties);

	}

	// statically imported above
	enum LocationColumn {
		LOCATION_NAME(0, "LocationName"), LOCATION_NUMBER(1, "LocationNumber"), CLIENT_NUMBER(2, "ClientNumber"),
		LOCATION_ADDRESS(3, "LocationAddress"), LOCATION_ADDRESS_2(4, "LocationAddress2"), LOCATION_CITY(5, "LocationCity"),
		LOCATION_STATE(6, "LocationState"), LOCATION_ZIP(7, "LocationZip"), LOCATION_TYPE(8, "LocationType");

		private final int index;
		private final String name;
		private LocationColumn(int index, String name) {
			this.index = index;
			this.name = name;
		}

		public String getValue(String[] row) {
			return (row != null && row.length > index) ? row[index] : null;
		}

	}
	private static final int NUM_LOCATION_COLUMNS = LocationColumn.values().length;
	private static List<String> getLocationColumns() {
		List<String> locationColumns = Lists.newArrayListWithCapacity(NUM_LOCATION_COLUMNS);
		for (LocationColumn column : LocationColumn.values()) {
			locationColumns.add(column.name);
		}
		return locationColumns;
	}
	private static final long COMMERCIAL = 1L;
	private static final long RESIDENTIAL = 2L;

	enum ContactColumn {
		FIRST_NAME(0, "FirstName"), LAST_NAME(1, "LastName"), TITLE(2, "Title"), CLIENT_NUMBER(3, "ClientNumber"),
		EMAIL(4, "Email"), WORK_NUMBER(5, "WorkNumber"), MOBILE_NUMBER(6, "MobileNumber"), LOCATION_NUMBER(7, "LocationNumber"), MANAGER(8, "Manager");

		private final int index;
		private final String name;
		private ContactColumn(int index, String name) {
			this.index = index;
			this.name = name;
		}

		public String getValue(String[] row) {
			return (row != null && row.length > index) ? row[index] : null;
		}
	}
	private static final int NUM_CONTACT_COLUMNS = ContactColumn.values().length;
	private static List<String> getContactColumns() {
		List<String> contactColumns = Lists.newArrayListWithCapacity(NUM_CONTACT_COLUMNS);
		for (ContactColumn column : ContactColumn.values()) {
			contactColumns.add(column.name);
		}
		return contactColumns;
	}

	private void handleLocations(Long companyId, CSVReader reader, MessageBundle messages) throws IOException, ContactManagerImportRowLimitExceededException {
		String[] headerRow = reader.readNext();

		// if file has 'old' format (without address 2)
		if (locationHeaderInOldFormat(Arrays.asList(headerRow))) {
			// show error explaining that the format has been updated
			messageHelper.addError(messages, "addressbook.upload.outdated_format");
			return;
		}

		handleHeader(headerRow, getLocationColumns(), messages);
		if (messages.hasErrors()) {
			return;
		}

		String[] row;
		// header already read
		int rowNumber = 1;

		List<Map<String, Object>> validatedLocations = Lists.newArrayList();
		while ((row = reader.readNext()) != null) {
			rowNumber++;
			if (row.length < NUM_LOCATION_COLUMNS) {
				messageHelper.addError(messages, "addressbook.upload.missing_columns", rowNumber);
				return;
			}

			LocationDTO locationDTO = new LocationDTO();
			locationDTO.setName(LOCATION_NAME.getValue(row));
			locationDTO.setLocationNumber(LocationColumn.LOCATION_NUMBER.getValue(row));
			locationDTO.setAddress1(LOCATION_ADDRESS.getValue(row));
			locationDTO.setAddress2(LOCATION_ADDRESS_2.getValue(row));
			locationDTO.setCity(LOCATION_CITY.getValue(row));
			locationDTO.setState(LOCATION_STATE.getValue(row));
			locationDTO.setPostalCode(LOCATION_ZIP.getValue(row));
			if (LOCATION_TYPE.getValue(row).equalsIgnoreCase("commercial")) {
				locationDTO.setLocationTypeId(COMMERCIAL);
			} else if (LOCATION_TYPE.getValue(row).equalsIgnoreCase("residential")) {
				locationDTO.setLocationTypeId(RESIDENTIAL);
			}
			locationDTO.setCountry(Country.getCountry(LOCATION_ZIP.getValue(row)));
			locationDTO.setCompanyId(companyId);

			DataBinder dataBinder = new DataBinder(locationDTO);
			BindingResult binding = dataBinder.getBindingResult();

			// Client company is optional
			Long clientCompanyId = mapClientNumberToId(
				LocationColumn.CLIENT_NUMBER.getValue(row), companyId, binding
			);
			locationDTO.setClientCompanyId(clientCompanyId);
			addressValidator.validate(locationDTO, binding);

			// Combine errors from client id mapping and validator functions
			if (binding.hasErrors()) {
				List<String> errorMessages = extract(binding.getAllErrors(), on(ObjectError.class).getDefaultMessage());
				messageHelper.addError(messages, "addressbook.upload.row_read.error", rowNumber, StringUtils.join(errorMessages, ", "));
				continue;
			}

			Map<String, Object> validatedLocation = Maps.newHashMap();
			validatedLocation.put("clientCompanyId", clientCompanyId);
			validatedLocation.put("locationDTO", locationDTO);
			validatedLocations.add(validatedLocation);
		}

		if (!messages.hasErrors()) {

			final long throttleDelay = locationService.getGeocodeDelay();
			for (Map<String, Object> location : validatedLocations) {

				// Slowing bulk uploads down a bit to avoid indigestion
				try {
					Thread.sleep(throttleDelay);
				} catch (final InterruptedException e) {
					logger.error("[geo] Interrupted", e);
					Thread.currentThread().interrupt();
				}

				ClientLocation savedLocation = saveOrUpdateClientLocation(
					(Long) location.get("clientCompanyId"),
					(LocationDTO) location.get("locationDTO"),
					messages
				);

				// Should have caught all errors by this point, but keep this check just in case...
				if (savedLocation == null) {
					messageHelper.addError(messages, "addressbook.upload.row_save.error", validatedLocations.indexOf(location), StringUtils.join(messages.getErrors(), ", "));
				}
			}
		}
	}

	/**
	 * Check if rawHeader matches 'old' location columns (current location columns minus 'address 2' column)
	 * http://stackoverflow.com/questions/2762093/java-compare-two-lists
	 */
	private boolean locationHeaderInOldFormat(List<String> rawHeader) {
		List<String> oldLocationColumns = getLocationColumns();
		oldLocationColumns.remove(LOCATION_ADDRESS_2.name);
		return CollectionUtils.isEqualCollection(rawHeader, oldLocationColumns);
	}

	private void handleContacts(Long companyId, CSVReader reader, MessageBundle messages) throws IOException, ContactManagerImportRowLimitExceededException {
		handleHeader(reader.readNext(), getContactColumns(), messages);
		if (messages.hasErrors()) {
			return;
		}

		String[] row;
		// header already read
		int rowNumber = 1;

		List<ClientContactDTO> validatedContacts = Lists.newArrayList();
		while ((row = reader.readNext()) != null) {
			rowNumber++;
			if (row.length < NUM_CONTACT_COLUMNS) {
				messageHelper.addError(messages, "addressbook.upload.missing_columns", rowNumber);
				return;
			}

			ClientContactDTO clientContactDTO = new ClientContactDTO();
			clientContactDTO.setFirstName(FIRST_NAME.getValue(row));
			clientContactDTO.setLastName(LAST_NAME.getValue(row));
			clientContactDTO.setJobTitle(TITLE.getValue(row));
			clientContactDTO.setEmail(EMAIL.getValue(row));
			clientContactDTO.setWorkPhone(WORK_NUMBER.getValue(row));
			clientContactDTO.setMobilePhone(MOBILE_NUMBER.getValue(row));
			clientContactDTO.setManager(Boolean.valueOf(MANAGER.getValue(row)));

			DataBinder dataBinder = new DataBinder(clientContactDTO);
			BindingResult binding = dataBinder.getBindingResult();

			// Client company is optional
			String clientNumber = ContactColumn.CLIENT_NUMBER.getValue(row);
			Long clientId = mapClientNumberToId(
				ContactColumn.CLIENT_NUMBER.getValue(row), companyId, binding
			);
			clientContactDTO.setClientCompanyId(clientId);

			// Location is optional
			Long locationId = mapLocationNumberToId(
				ContactColumn.LOCATION_NUMBER.getValue(row), clientNumber, clientId, companyId, binding
			);
			clientContactDTO.setClientLocationId(locationId);

			contactValidator.validate(clientContactDTO, binding);

			// Combine errors from client id/location id mapping and validator functions
			if (binding.hasErrors() || messages.hasErrors()) {
				List<String> errorMessages = extract(binding.getAllErrors(), on(ObjectError.class).getDefaultMessage());
				messageHelper.addError(messages, "addressbook.upload.row_read.error", rowNumber, StringUtils.join(errorMessages, ", "));
				continue;
			}

			validatedContacts.add(clientContactDTO);
		}

		if (!messages.hasErrors()) {
			for (ClientContactDTO validatedContact : validatedContacts) {
				ClientContact contact = saveOrUpdateClientContact(companyId, validatedContact, null);

				// Should have caught all errors by this point, but keep this check just in case...
				if (contact == null) {
					messageHelper.addError(messages, "addressbook.upload.row_save.error", rowNumber, "Please check your data.");
				} else {
					if (validatedContact.getEmail() != null) {
						EmailAddressDTO emailAddressDTO = new EmailAddressDTO();
						emailAddressDTO.setEmail(validatedContact.getEmail());
						addEmailToClientContact(contact.getId(), emailAddressDTO);
					}

					if (validatedContact.getWorkPhone() != null) {
						PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
						phoneNumberDTO.setContactContextType(ContactContextType.WORK);
						phoneNumberDTO.setPhone(validatedContact.getWorkPhone());
						phoneNumberDTO.setExtension(validatedContact.getWorkPhoneExtension());
						addPhoneToClientContact(contact.getId(), phoneNumberDTO);
					}

					if (validatedContact.getMobilePhone() != null) {
						PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
						phoneNumberDTO.setContactContextType(ContactContextType.HOME);
						phoneNumberDTO.setPhone(validatedContact.getMobilePhone());
						addPhoneToClientContact(contact.getId(), phoneNumberDTO);
					}
				}
			}
		}
	}

	@Override
	public void bulkUploadFileRead(Long companyId, String importType, String fileUUID, MessageBundle messages) throws IOException, HostServiceException, ContactManagerImportRowLimitExceededException {
		Assert.notNull(companyId);
		File file = remoteFileAdapter.getFile(RemoteFileType.TMP, fileUUID);
		checkIfImportSizeExceeded(file);
		CSVReader reader = new CSVReader(new FileReader(file));
		bulkUploadFileRead(companyId, importType, reader, messages);
	}

	@Override
	public ImmutableList<ClientCompanyForm> mapClientCompanyToForm(List<ClientCompany> companies) {
		if (companies == null) {
			return null;
		}

		List<ClientCompanyForm> companiesForm = new ArrayList<>(companies.size());

		for (ClientCompany company : companies) {
			companiesForm.add(mapClientCompanyToForm(company));
		}

		return ImmutableList.copyOf(companiesForm);
	}

	@Override
	public ClientCompanyForm mapClientCompanyToForm(ClientCompany company) {
		if (company == null) {
			return null;
		}

		ClientCompanyForm clientCompanyForm = new ClientCompanyForm();

		clientCompanyForm.setId(company.getId());
		clientCompanyForm.setName(company.getName());
		clientCompanyForm.setCustomerId(company.getCustomerId());
		clientCompanyForm.setRegion(company.getRegion());
		clientCompanyForm.setDivision(company.getDivision());
		clientCompanyForm.setIndustryName(company.getIndustryName());

		if (company.getIndustry() != null) {
			clientCompanyForm.setIndustryId(company.getIndustry().getId());
			clientCompanyForm.setIndustryName(company.getIndustry().getName()); //respect the name from industry table
		}

		return clientCompanyForm;
	}

	@Override
	public ImmutableList<Map> getProjectedClientCompanies(String[] fields) throws Exception {
		Long companyId = authenticationService.getCurrentUserCompanyId();
		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(fields, findAllClientCompanyByCompany(companyId)));
	}

	@Override
	public ImmutableList<Map> getProjectedClientLocations(final Long clientId, final String locationName, String[] fields) throws Exception {
		ClientLocationPagination pagination = new ClientLocationPagination();
		pagination.setResultsLimit(RESULTS_LIMIT);
		pagination.addFilter(ClientLocationPagination.FILTER_KEYS.LOCATION_NAME, locationName);

		if (clientId == null) {
			pagination = findAllLocations(authenticationService.getCurrentUserCompanyId(), pagination);
		} else {
			pagination.addFilter(ClientLocationPagination.FILTER_KEYS.CLIENT_ID, clientId);
			pagination = findAllLocationsByClientCompany(authenticationService.getCurrentUserCompanyId(), clientId, pagination);
		}
		pagination.setProjection(fields);
		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(pagination.getProjection(), pagination.getResults()));
	}

	@Override
	public ImmutableList<Map> getProjectedClientContacts(final Long clientId, final String contactName, String[] fields) throws Exception {
		ClientContactPagination pagination = new ClientContactPagination();
		pagination.setResultsLimit(RESULTS_LIMIT);
		pagination.addFilter(ClientContactPagination.FILTER_KEYS.CLIENT_CONTACT_NAME, contactName);

		if (clientId != null) {
			pagination.addFilter(ClientContactPagination.FILTER_KEYS.CLIENT_COMPANY_ID, clientId);
		}
		pagination = findAllClientContactsByUser(authenticationService.getCurrentUserId(), pagination);
		pagination.setProjection(fields);
		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(
			pagination.getProjection(),
			ImmutableMap.of(
				"name", "fullName",
				"email", "mostRecentEmail.email",
				"number", "mostRecentWorkPhone.phone"),
			pagination.getResults()));
	}

	private void checkIfImportSizeExceeded(File file) throws IOException, ContactManagerImportRowLimitExceededException {
		if (getNumberOfLines(file) > MAX_UPLOAD_CONTACTS_LOCATIONS) {
			throw new ContactManagerImportRowLimitExceededException(
				String.format("Your file exceeds the limit of %s records.", MAX_UPLOAD_CONTACTS_LOCATIONS));
		}
	}

	private int getNumberOfLines(File file) throws IOException {
		InputStream is = new BufferedInputStream((new FileInputStream(file)));
		byte[] c = new byte[1024];
		int count = 0;
		int readChars;
		boolean empty = true;
		while ((readChars = is.read(c)) != -1) {
			empty = false;
			for (int i = 0; i < readChars; ++i) {
				if (c[i] == '\n') {
					++count;
				}
			}
		}
		is.close();
		return (count == 0 && !empty) ? 1 : count;
	}

	@Override
	public void bulkUploadFileRead(Long companyId, String importType, CSVReader reader, MessageBundle messages) throws IOException, ContactManagerImportRowLimitExceededException {
		switch (importType) {
			case LOCATION:
				handleLocations(companyId, reader, messages);
				break;
			case CONTACT:
				handleContacts(companyId, reader, messages);
				break;
			default:
				messageHelper.addError(messages, "addressbook.upload.invalid.type");
				break;
		}
	}

	private void handleHeader(String[] rawHeader, List<String> columns, MessageBundle messages) {
		List<String> header = cleanImportHeader(rawHeader);

		SortedSet<String> missingColumns = ImmutableSortedSet.copyOf(caseInsensitiveDifference(header, columns));
		SortedSet<String> invalidColumns = ImmutableSortedSet.copyOf(caseInsensitiveDifference(columns, header));
		String message = "";

		if (isNotEmpty(missingColumns)) {
			message += "Your upload is missing " + StringUtils.join(missingColumns, ", ") + ". ";
		}
		if (isNotEmpty(invalidColumns)) {
			message +=
				StringUtils.join(invalidColumns, ", ") +
				(invalidColumns.size() == 1 ? " is not a" : " are not") +
				" valid column " +
				StringUtilities.pluralize("name", invalidColumns.size()) +
				". ";
		}
		if (isEmpty(missingColumns) && isEmpty(invalidColumns) && header.size() > columns.size()) {
			message = "Your upload contains a duplicate column. ";
		}

		if (StringUtilities.isNotEmpty(message)) {
			messageHelper.addError(messages, message + "Please check your data.");
		}
	}

	/**
	 * @param rawHeaders -
	 * @return - A list of Strings with all whitespace removed
	 */
	private static List<String> cleanImportHeader(String[] rawHeaders) {
		List<String> trimmedStrings = Lists.newArrayListWithCapacity(rawHeaders.length);
		for (String e : rawHeaders) {
			trimmedStrings.add(e.replaceAll("\\s",""));
		}
		return trimmedStrings;
	}

	/**
	 * @param list -
	 * @param otherList -
	 * @return - The set of Strings present in list, but not present in otherList
	 */
	private Set<String> caseInsensitiveDifference(List<String> list, List<String> otherList) {
		Set<String> compareAgainst = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		compareAgainst.addAll(list);

		Set<String> difference = Sets.newHashSet();
		for (String s : otherList) {
			if (!compareAgainst.contains(s)) {
				difference.add(s);
			}
		}
		return difference;
	}

	private Long mapLocationNumberToId(String locationNumber, String clientNumber, Long clientId, Long companyId, Errors errors) {
		if (StringUtils.isBlank(locationNumber) || companyId == null) {
			return null;
		}

		List<ClientLocation> locations;
		if (clientId != null) {
			locations = findLocationsByNumberAndClientCompany(locationNumber, clientId);
		} else {
			locations = findLocationByNumberAndCompany(locationNumber, companyId);
		}

		if (locations.isEmpty()) {
			if (clientId != null) {
				errors.rejectValue("", "Invalid", messageHelper.getMessage("addressbook.upload.nolocationwithclient", locationNumber, clientNumber));
			} else {
				errors.rejectValue("", "Invalid", messageHelper.getMessage("addressbook.upload.nolocation", locationNumber));
			}
			return null;

		} else if (locations.size() > 1) {
			errors.rejectValue("", "Invalid", messageHelper.getMessage("addressbook.upload.multiplelocations", locationNumber));
			return null;
		}

		return locations.get(0).getId();
	}

	private Long mapClientNumberToId(String clientNumber, Long companyId, Errors errors) {
		if (StringUtils.isBlank(clientNumber) || companyId == null) {
			return null;
		}

		List<ClientCompany> clientCompanies = findClientCompanyByNumberAndCompany(clientNumber, companyId);
		if (clientCompanies.isEmpty()) {
			errors.rejectValue("clientCompanyId", "Invalid", messageHelper.getMessage("addressbook.upload.noclient", clientNumber));
			return null;

		} else if (clientCompanies.size() > 1) {
			errors.rejectValue("clientCompanyId", "Invalid", messageHelper.getMessage("addressbook.upload.multipleclients", clientNumber));
			return null;
		}
		return clientCompanies.get(0).getId();
	}

}
