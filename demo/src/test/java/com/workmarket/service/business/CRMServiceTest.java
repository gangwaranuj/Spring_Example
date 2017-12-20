package com.workmarket.service.business;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import com.workmarket.dao.ClientLocationDAO;
import com.workmarket.dao.DressCodeDAO;
import com.workmarket.dao.LocationDAO;
import com.workmarket.dao.LocationTypeDAO;
import com.workmarket.dao.crm.ClientCompanyDAO;
import com.workmarket.dao.crm.ClientContactDAO;
import com.workmarket.dao.crm.ClientContactEmailAssociationDAO;
import com.workmarket.dao.crm.ClientContactLocationAssociationDAO;
import com.workmarket.dao.crm.ClientContactPhoneAssociationDAO;
import com.workmarket.data.dataimport.adapter.ContactManagerImportRowLimitExceededException;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientCompanyPagination;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.crm.ClientLocationPagination;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.AddressBookContactValidator;
import com.workmarket.web.validators.AddressBookLocationValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CRMServiceTest {

	@Mock LocationDAO locationDAO;
	@Mock ClientContactDAO clientContactDAO;
	@Mock ClientCompanyDAO clientCompanyDAO;
	@Mock ClientLocationDAO clientLocationDAO;
	@Mock RemoteFileAdapter remoteFileAdapter;
	@Mock LocationTypeDAO locationTypeDAO;
	@Mock DressCodeDAO dressCodeDAO;
	@Mock AddressServiceImpl addressService;
	@Mock MessageBundleHelper messageHelper;
	@Mock InvariantDataService invariantDataService;
	@Mock CompanyService companyService;
	@Mock DirectoryService directoryService;
	@Mock ClientContactEmailAssociationDAO clientContactEmailAssociationDAO;
	@Mock ClientContactPhoneAssociationDAO clientContactPhoneAssociationDAO;
	@Mock ClientContactLocationAssociationDAO clientContactLocationAssociationDAO;
	@Mock AddressBookLocationValidator addressBookLocationValidator;
	@Mock AddressBookContactValidator addressBookContactValidator;
	@InjectMocks CRMServiceImpl crmService = spy(new CRMServiceImpl());

	User user;
	Company company;
	ClientLocation location;
	ClientContact contact;
	ClientCompany clientCompany;
	ClientCompanyPagination companyPagination;
	ClientLocationPagination locationPagination;
	LocationType locationTypeCommercial = new LocationType();
	LocationType locationTypeResidential = new LocationType();
	DressCode dressCode;
	MessageBundle bundle;
	CSVReader csvReaderLocation;
	List<String[]> entries = Lists.newArrayList();
	List<ClientLocation> locations = Lists.newArrayList();
	List<ClientContact> contacts = Lists.newArrayList();

	@Before
	public void setup() throws IOException {
		user = mock(User.class);
		company = mock(Company.class);
		location = mock(ClientLocation.class);
		contact = mock(ClientContact.class);
		clientCompany = mock(ClientCompany.class);
		bundle = mock(MessageBundle.class);
		csvReaderLocation = mock(CSVReader.class);
		dressCode = mock(DressCode.class);
		locationPagination = mock(ClientLocationPagination.class);
		companyPagination = mock(ClientCompanyPagination.class);
		locationTypeCommercial.setId(LocationType.COMMERCIAL_CODE);
		locationTypeResidential.setId(LocationType.RESIDENTIAL_CODE);

		when(user.getId()).thenReturn(1L);
		when(company.getId()).thenReturn(1L);
		when(user.getCompany()).thenReturn(company);
		when(companyService.findCompanyById(company.getId())).thenReturn(company);
		when(location.getId()).thenReturn(1L);
		when(location.getClientCompany()).thenReturn(clientCompany);
		when(contact.getId()).thenReturn(1L);
		when(contact.getClientCompany()).thenReturn(clientCompany);
		when(clientCompany.getId()).thenReturn(1L);
		when(locationPagination.getResults()).thenReturn(locations);
		when(dressCodeDAO.findDressCodeById(DressCode.BUSINESS_CASUAL)).thenReturn(dressCode);
		when(locationDAO.findLocationById(ClientLocation.class, location.getId())).thenReturn(location);
		when(locationDAO.findAllLocations(user.getCompany().getId(), locationPagination)).thenReturn(locationPagination);
		when(clientCompanyDAO.findClientCompanyById(clientCompany.getId())).thenReturn(clientCompany);
		when(clientContactDAO.findClientContactsByCompany(user.getCompany().getId())).thenReturn(contacts);
		when(clientContactDAO.findContactById(contact.getId())).thenReturn(contact);
		when(clientLocationDAO.get(location.getId())).thenReturn(location);
		when(clientCompanyDAO.get(clientCompany.getId())).thenReturn(clientCompany);
		when(csvReaderLocation.readNext()).thenReturn("".split("")); //dummy String[]
		when(csvReaderLocation.readAll()).thenReturn(entries);
		when(locationTypeDAO.findLocationTypeById(LocationType.COMMERCIAL_CODE)).thenReturn(locationTypeCommercial);
		when(locationTypeDAO.findLocationTypeById(LocationType.RESIDENTIAL_CODE)).thenReturn(locationTypeResidential);
	}

	@Test
	public void test_updateClientCompanyForClientLocation() {
		crmService.updateClientCompanyForClientLocation(location.getId(), clientCompany.getId());
		Assert.notNull(location);
		Assert.notNull(location.getClientCompany());
		Assert.isTrue(location.getClientCompany().equals(clientCompany));
	}

	@Test
	public void test_updateClientCompanyForClientContact() {
		crmService.updateClientCompanyForClientContact(contact.getId(), clientCompany.getId());
		Assert.notNull(contact);
		Assert.notNull(contact.getClientCompany());
		Assert.isTrue(contact.getClientCompany().equals(clientCompany));
	}

	@Test
	public void test_findAllLocations() {
		locationPagination = crmService.findAllLocations(user.getCompany().getId(), locationPagination);
		Assert.notNull(locationPagination);
		Assert.notNull(locationPagination.getResults());
	}

	@Test
	public void test_findAllClientContactsByCompany() {
		List<ClientContact> contacts = crmService.findAllClientContactsByCompany(user.getCompany().getId());
		Assert.notNull(contacts);
	}

	public static final String[] LOCATIONS_HEADER = new String[]{ "LocationName", "LocationNumber", "ClientNumber", "LocationAddress", "LocationAddress2", "LocationCity", "LocationState", "LocationZip", "LocationType" };
	public static final String[] LOCATIONS_HEADER_OLD = new String[]{ "LocationName", "LocationNumber", "ClientNumber", "LocationAddress", "LocationCity", "LocationState", "LocationZip", "LocationType" };
	public static final String INVALID_COLUMN = "Boog";
	public static final String[] LOCATIONS_HEADER_WITH_INVALID_COLUMN = new String[]{ "LocationName", "LocationNumber", "ClientNumber", "LocationAddress", "LocationAddress2", "LocationCity", "LocationState", "LocationZip", "LocationType", INVALID_COLUMN };
	public static final String[] LOCATIONS_HEADER_WITH_DUPLICATE_COLUMN = new String[]{ "LocationName", "LocationNumber", "ClientNumber", "LocationAddress", "LocationAddress2", "LocationCity", "LocationState", "LocationZip", "LocationZip", "LocationType"};
	public static final String[] LOCATIONS_HEADER_WITH_INVALID_COLUMN_AND_MISSING_COLUMN = new String[]{ "LocationName", "LocationNumber", "ClientNumber", "LocationAddress", "LocationAddress2", "LocationCity", "LocationZip", "LocationType", INVALID_COLUMN};
	public static final String[] LOCATIONS_HEADER_WITH_ONE_MISSING_COLUMN = new String[]{ "LocationName", "LocationNumber", "ClientNumber", "LocationAddress", "LocationAddress2", "LocationCity", "LocationState", "LocationZip" };
	public static final String[] LOCATIONS_HEADER_WITH_TWO_MISSING_COLUMNS = new String[]{ "LocationName", "LocationNumber", "ClientNumber", "LocationAddress", "LocationAddress2", "LocationCity", "LocationState" };

	@Test
	public void bulkUploadFileRead_Locations_BadNumColumns() throws HostServiceException, IOException, ContactManagerImportRowLimitExceededException {
		MessageBundle messages = mock(MessageBundle.class);
		CSVReader reader = mock(CSVReader.class);
		when(reader.readNext()).thenReturn(
			LOCATIONS_HEADER,
			// only 8 columns, should be 9
			new String[]{ "1003 - BRIDGEWATER", "1003", "14569", "60 PINE GROVE ROAD", "APARTMENT 2", "BRIDGEWATER", "NS", "B4V 4H2" },
			null
		);

		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, reader, messages);

		verify(messageHelper, times(1)).addError(messages, "addressbook.upload.missing_columns", 2);
	}

	@Test
	public void bulkUploadFileRead_Locations_OldFileFormat() throws HostServiceException, IOException, ContactManagerImportRowLimitExceededException {
		MessageBundle messages = mock(MessageBundle.class);
		CSVReader reader = mock(CSVReader.class);
		when(reader.readNext()).thenReturn(
			LOCATIONS_HEADER_OLD,
			// only 8 columns, should be 9
			new String[]{ "1003 - BRIDGEWATER", "1003", "14569", "60 PINE GROVE ROAD", "APARTMENT 2", "BRIDGEWATER", "NS", "B4V 4H2", "commercial" },
			null
		);

		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, reader, messages);

		verify(messageHelper, times(1)).addError(messages, "addressbook.upload.outdated_format");
	}

	public static final String[] CONTACTS_HEADER = new String[]{ "First Name", "Last Name", "Title", "Client Number", "Email", "Work Number", "Mobile Number", "Location Number", "Manager" };
	public static final String[] CONTACTS_HEADER_WITH_INVALID_COLUMN_AND_MISSING_COLUMN = new String[]{ "First Name", "Last Name", "Title", "Client Number", "Email", "Work Number", "Mobile Number", "Location Number", INVALID_COLUMN };

	public void bulkUploadFileRead_Contacts_BadNumColumns() throws HostServiceException, IOException, ContactManagerImportRowLimitExceededException {
		MessageBundle messages = mock(MessageBundle.class);
		CSVReader reader = mock(CSVReader.class);
		when(reader.readNext()).thenReturn(
				CONTACTS_HEADER,
				// 8 columns, should be 9
				new String[]{ "Bob", "Smith", "Director", "14569", "bob@bob.com", "666-666-6666", "777-777-7777", "1000" },
				null
		);

		crmService.bulkUploadFileRead(1L, CRMServiceImpl.CONTACT, reader, messages);

		verify(messageHelper, times(1)).addError(messages, "addressbook.upload.row.error", 2);
	}

	@Test
	public void bulkUploadFileRead_Contacts_Success() throws HostServiceException, IOException, ContactManagerImportRowLimitExceededException {
		MessageBundle messages = mock(MessageBundle.class);
		CSVReader reader = mock(CSVReader.class);
		when(reader.readNext()).thenReturn(
				CONTACTS_HEADER,
				new String[]{ "Bob", "Smith", "Director", "14569", "bob@bob.com", "666-666-6666", "777-777-7777", "1000", "true" },
				null
		);

		when(locationDAO.findLocationById(Location.class, 1000L)).thenReturn(location);
		when(location.getId()).thenReturn(12L);
		when(clientLocationDAO.findLocationById(12L)).thenReturn(location);
		when(clientCompanyDAO.get(14569L)).thenReturn(clientCompany);

		when(clientContactDAO.get(contact.getId())).thenReturn(contact);
		doReturn(contact)
				.when(crmService)
				.getNewClientContact(company);

		Phone phone = mock(Phone.class);
		when(phone.getContactContextType()).thenReturn(ContactContextType.WORK);
		when(directoryService.saveOrUpdatePhoneNumber(any(PhoneNumberDTO.class))).thenReturn(phone);

		crmService.bulkUploadFileRead(1L, CRMServiceImpl.CONTACT, reader, messages);

		verify(messageHelper, times(0)).addError(eq(messages), eq("addressbook.upload.row.error"), anyInt());
	}

	@Test
	public void findClientLocationByIdAndCompany_verifyDaoCall_pass() {
		Long companyID = 1L;
		Long locationId = 2L;
		crmService.findClientLocationByIdAndCompany(companyID, locationId);
		verify(locationDAO).findLocationByIdAndCompany(ClientLocation.class, companyID, locationId);
	}

}
