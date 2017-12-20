package com.workmarket.service.business;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientCompanyPagination;
import com.workmarket.domains.model.crm.ClientCompanyPhoneAssociation;
import com.workmarket.domains.model.crm.ClientCompanyWebsiteAssociation;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactEmailAssociation;
import com.workmarket.domains.model.crm.ClientContactPhoneAssociation;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.crm.ClientLocationPhoneAssociation;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.WebsiteDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.AddressBookLocationValidator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CRMServiceIT extends BaseServiceIT {

	protected final static String TMP_FILE = "/tmp/crm-import";

	@Autowired private CRMService crmService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired protected UploadService uploadService;
	@Autowired protected DirectoryService directoryService;
	@Autowired protected MessageBundleHelper messageHelper;

	private User user;
	private ClientCompany clientCompany;
	private ClientContact clientContact;
	private ClientLocation clientLocation;

	@Before
	public void before() throws Exception {
		user = userService.findUserById(ANONYMOUS_USER_ID);
		clientCompany = newClientCompany(user.getId());
		clientContact = newClientContactForCompany(user.getCompany().getId());
		clientLocation = newClientLocationForClientCompany(user.getCompany().getId(), clientCompany.getId());
		clientContact.setClientLocation(clientLocation);
		crmService.addLocationToClientContact(clientContact.getId(), clientLocation.getId());
	}

	@Test
	@Transactional
	public void test_saveOrUpdateClientCompany() throws Exception {

		String companyName = RandomUtilities.generateAlphaNumericString(10);
		ClientCompanyDTO companyDTO = new ClientCompanyDTO();
		companyDTO.setName(companyName);
		companyDTO.setIndustryName("IT");

		ClientCompany clientCompany = crmService.saveOrUpdateClientCompany(user.getId(), companyDTO, null);

		assertNotNull(clientCompany);
		assertEquals(clientCompany.getName(), companyName);
		assertEquals(clientCompany.getIndustryName(), "IT");
	}

	@Test
	@Transactional
	public void test_saveOrUpdateClientLocation() throws Exception {

		LocationDTO locationDTO = createLocationDTO();

		List<DressCode> dressCodeList = crmService.findAllDressCodes();

		assertNotNull(dressCodeList);
		assertTrue(dressCodeList.size() == 7);

		locationDTO.setDressCodeId(dressCodeList.get(2).getId());
		locationDTO.setLocationNumber("5623212");
		locationDTO.setCompanyId(COMPANY_ID);

		ClientLocation clientLocation = crmService.saveOrUpdateClientLocation(CLIENT_COMPANY_ID, locationDTO, null);
		assertNotNull(clientLocation);
		assertTrue(clientLocation.getAddress().getDressCode().getId() == 3);
		assertTrue(clientLocation.getAddress().getLocationType().getId() == 1);
		assertEquals(clientLocation.getLocationNumber(), "5623212");
	}

	@Test
	@Transactional
	public void test_saveOrUpdateClientContact() throws Exception {

		ClientContactDTO clientContactDTO = createClientContactDTO();

		ClientContact clientContact = crmService.saveOrUpdateClientContact(COMPANY_ID, clientContactDTO, null);

		assertNotNull(clientContact);
		assertTrue(clientContact.getFirstName().equals("John"));
		assertTrue(clientContact.getLastName().equals("Smith"));
		assertTrue(clientContact.isManager());
	}

	@Test
	@Transactional
	public void test_addWebsiteToClientCompany() throws Exception {
		WebsiteDTO dto = new WebsiteDTO();
		dto.setContactContextType(ContactContextType.HOME.toString());
		dto.setWebsite("www.mywebsite.com");

		crmService.addWebsiteToClientCompany(clientCompany.getId(), dto);

		clientCompany = crmService.findClientCompanyById(clientCompany.getId());
		assertFalse(clientCompany.getWebsiteAssociations().isEmpty());

		for (ClientCompanyWebsiteAssociation e : clientCompany.getWebsiteAssociations()) {
			assertNotNull(e.getWebsite().getWebsite());
		}
	}

	@Test
	@Transactional
	public void test_addPhoneToClientCompany() throws Exception {
		PhoneNumberDTO dto = new PhoneNumberDTO();
		dto.setPhone("203-562311245");
		dto.setContactContextType(ContactContextType.WORK);

		crmService.addPhoneToClientCompany(clientCompany.getId(), dto);

		clientCompany = crmService.findClientCompanyById(clientCompany.getId());
		assertFalse(clientCompany.getPhoneAssociations().isEmpty());

		for (ClientCompanyPhoneAssociation e : clientCompany.getPhoneAssociations()) {
			assertNotNull(e.getPhone().getPhone());
		}
	}

	@Test
	@Transactional
	public void test_addEmailToClientContact() throws Exception {
		EmailAddressDTO dto = new EmailAddressDTO();
		dto.setEmail("some@email.com");
		dto.setContactContextType(ContactContextType.OTHER.toString());

		crmService.addEmailToClientContact(clientContact.getId(), dto);

		clientContact = crmService.findClientContactById(clientContact.getId());
		assertFalse(clientContact.getEmailAssociations().isEmpty());

		assertTrue(clientContact.getEmailAssociations().size() == clientContact.getEmails().size());

		for (ClientContactEmailAssociation e : clientContact.getEmailAssociations()) {
			assertNotNull(e.getEmail());
		}
	}

	@Test
	@Transactional
	public void test_addPhoneToClientContact() throws Exception {
		PhoneNumberDTO dto = new PhoneNumberDTO();
		dto.setPhone("56231215478");
		dto.setExtension("623");
		dto.setContactContextType(ContactContextType.WORK);

		crmService.addPhoneToClientContact(clientContact.getId(), dto);
		clientContact = crmService.findClientContactById(clientContact.getId());
		assertFalse(clientContact.getPhoneAssociations().isEmpty());

		assertTrue(clientContact.getPhoneAssociations().size() == clientContact.getPhoneNumbers().size());

		for (ClientContactPhoneAssociation e : clientContact.getPhoneAssociations()) {
			assertNotNull(e.getPhone());
			assertEquals(e.getPhone().getPhone(), "56231215478");
		}

		for (ClientContactPhoneAssociation e : clientContact.getPhoneAssociations()) {
			crmService.removePhoneFromClientContact(clientContact.getId(), e.getPhone().getId());
		}

		clientContact = crmService.findClientContactById(clientContact.getId());
	}

	@Test
	@Transactional
	public void testFindDefaultAvailableHours() throws Exception {
		List<UserAvailability> availableHours = crmService.findClientLocationWeeklyWorkingHours(CLIENT_LOCATION_ID);

		assertEquals(7, availableHours.size());
		for (UserAvailability wh : availableHours) {
			if (wh.getWeekDay() == Calendar.SATURDAY - 1 || wh.getWeekDay() == Calendar.SUNDAY - 1) {
				assertTrue(wh.getDeleted());
			} else {
				assertFalse(wh.getDeleted());
			}
		}
	}

	@Test
	@Transactional
	public void test_addPhoneToClientLocation() throws Exception {
		PhoneNumberDTO dto = new PhoneNumberDTO();
		dto.setPhone("203-562311245");
		dto.setContactContextType(ContactContextType.WORK);

		crmService.addPhoneToClientLocation(CLIENT_LOCATION_ID, dto);

		ClientLocation location = crmService.findClientLocationById(CLIENT_LOCATION_ID);
		assertFalse(location.getPhoneAssociations().isEmpty());

		for (ClientLocationPhoneAssociation e : location.getPhoneAssociations()) {
			assertNotNull(e.getPhone().getPhone());
		}
	}

	@Test
	@Transactional
	public void test_removeClientCompany() throws Exception {
		LocationDTO locationDTO = createLocationDTO();

		List<DressCode> dressCodeList = crmService.findAllDressCodes();

		assertNotNull(dressCodeList);
		assertTrue(dressCodeList.size() == 7);

		locationDTO.setDressCodeId(dressCodeList.get(2).getId());

		ClientLocation clientLocation = crmService.saveOrUpdateClientLocation(clientCompany.getId(), locationDTO, null);

		assertNotNull(clientLocation);
		Long companyId = authenticationService.getCurrentUser().getCompany().getId();
		List<ClientLocation> locations = crmService.findAllLocationsByClientCompany(companyId, clientCompany.getId());

		assertFalse(locations.isEmpty());

		crmService.deleteClientLocationByIdAndCompany(clientLocation.getId(), companyId);
		crmService.deleteClientCompanyByIdAndCompany(clientCompany.getId(), companyId, null);

		assertTrue(crmService.findClientCompanyById(clientCompany.getId()).getDeleted());
	}

	@Test(expected = IllegalStateException.class)
	@Transactional
	public void test_duplicateClientCompanyName() throws Exception {
		ClientCompanyDTO companyDTO = new ClientCompanyDTO();
		companyDTO.setName(clientCompany.getName());
		companyDTO.setAddress1("20 20st");
		companyDTO.setCity("New York");
		companyDTO.setState("NY");
		companyDTO.setPostalCode("10011");
		companyDTO.setCountry("USA");

		clientCompany = crmService.saveOrUpdateClientCompany(user.getId(), companyDTO, null);
	}

	@Test
	@Transactional
	public void test_saveOrUpdateClientContactAdvancedOptions() throws Exception {

		// Create location associated with client
		LocationDTO locationDTO = createLocationDTO();
		String locationNumber = locationDTO.getLocationNumber();
		ClientLocation clientLocation = crmService.saveOrUpdateClientLocation(clientCompany.getId(), locationDTO, null);

		// Create contact associated with client and client's location
		ClientContactDTO clientContactDTO = createClientContactDTO();
		clientContactDTO.setClientCompanyId(clientCompany.getId());
		clientContactDTO.setClientLocationId(clientLocation.getId());
		ClientContact clientContact = crmService.saveOrUpdateClientContact(COMPANY_ID, clientContactDTO, null);

		assertNotNull(clientContact);
		assertTrue(clientContact.getFirstName().equals("John"));
		assertTrue(clientContact.getLastName().equals("Smith"));
		assertTrue(clientContact.isManager());

		ClientCompany clientCompany1 = crmService.findClientCompanyById(clientContact.getClientCompany().getId());
		assertNotNull(clientCompany1);
		assertEquals(clientCompany.getId(), clientCompany1.getId());
		assertEquals(crmService.getLocationCountByClientContact(clientContact.getId()), 1);

		clientLocation = crmService.findAllLocationsByClientContact(clientContact.getId()).get(0);
		assertEquals(clientLocation.getClientCompany(), clientContact.getClientCompany());
		assertEquals(clientLocation.getLocationNumber(), locationNumber);

		crmService.updateClientContactProperties(clientContact.getId(), CollectionUtilities.newStringMap("firstName", "Jeff"));

		clientContact = crmService.findClientContactById(clientContact.getId());
		assertEquals(clientContact.getFirstName(), "Jeff");
	}

	@Test
	public void test_findAllClientCompaniesByCompany() {
		List<Map<String, Object>> companies = crmService.findAllClientCompaniesByCompany(user.getCompany().getId(), "id", "name");
		assertNotNull(companies);

		ClientCompanyPagination companiesPagination = crmService.findAllClientCompanyByUser(user.getCompany().getId(), new ClientCompanyPagination());
		assertNotNull(companiesPagination);
		assertNotNull(companiesPagination.getResults());
		assertTrue(companiesPagination.getResults().size() <= companiesPagination.getResultsLimit());
	}

	@Test(expected = IllegalArgumentException.class)
	@Transactional
	public void test_saveOrUpdateClientContact_contactClientLocationClientMismatch() throws Exception {
		User user = newCompanyEmployee(COMPANY_ID);

		// Create client company
		ClientCompany clientCompany1 = newClientCompany(user.getId());
		// Create location associated with that client company
		ClientLocation clientLocation = crmService.saveOrUpdateClientLocation(clientCompany1.getId(), createLocationDTO(), null);

		// Create another client company
		ClientCompany clientCompany2 = newClientCompany(user.getId());

		// Create ContactDTO
		ClientContactDTO clientContactDTO = createClientContactDTO();

		// Set the contact's location id to client company 1's location
		clientContactDTO.setClientLocationId(clientLocation.getId());
		// Set the contact's client company to client company 2
		clientContactDTO.setClientCompanyId(clientCompany2.getId());

		crmService.saveOrUpdateClientContact(COMPANY_ID, clientContactDTO, null);
	}

	@Test
	public void findLocationsByNumberAndClientCompany_invalidClientCompanyID_returnEmptyList() {
		List<ClientLocation> locations = crmService.findLocationsByNumberAndClientCompany("124", 0L);
		assertNotNull(locations);
	}

	@Test
	public void findLocationByNumberAndCompany_invalidCompanyId_returnEmptyList() {
		List<ClientLocation> locations = crmService.findLocationByNumberAndCompany("124", 0L);
		assertNotNull(locations);
	}

	@Test
	public void bulkUploadFileRead_US_pass() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER, getCorrectLocationRowUs());

		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);

		assertFalse(messages.hasErrors());
	}

	@Test
	public void bulkUploadFileRead_CAN_pass() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER, getCorrectLocationRowCan());

		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);

		assertFalse(messages.hasErrors());
	}

	@Test
	public void bulkUploadLocation_usingOldFormat() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER_OLD, getOldLocationRowUs());

		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);

		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(
			messages.getErrors().get(0), messageHelper.getMessage("addressbook.upload.outdated_format")
		);
	}

	@Test
	public void bulkUploadFileRead_location_clientCompanyDoesNotExist_fail() throws Exception {
		String[] rowWithNoPostalCode = getCorrectLocationRowUs();
		String invalidClientCompanyNumber = "0";
		rowWithNoPostalCode[2] = invalidClientCompanyNumber;
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER, rowWithNoPostalCode);

		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);

		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(
			messages.getErrors().get(0),
			messageHelper.getMessage("addressbook.upload.row_read.error", 2, messageHelper.getMessage("addressbook.upload.noclient", invalidClientCompanyNumber))
		);
	}

	@Test
	public void bulkUploadFileRead_location_noPostalCode_fail() throws Exception {
		String[] rowWithNoPostalCode = getCorrectLocationRowUs();
		rowWithNoPostalCode[7] = "";
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER, rowWithNoPostalCode);

		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);

		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(
			messages.getErrors().get(0),
			messageHelper.getMessage("addressbook.upload.row_read.error", 2,
			    messageHelper.getMessage("userRegistration.validation.address.empty_postalCode")));
	}

	@Test
	public void bulkUploadFileRead_location_invalidPostalCode_fail() throws Exception {
		String[] rowWithInvalidPostalCode = getCorrectLocationRowUs();
		rowWithInvalidPostalCode[7] = "1234";

		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER, rowWithInvalidPostalCode);

		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);

		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(
			messages.getErrors().get(0),
			messageHelper.getMessage("addressbook.upload.row_read.error", 2, AddressBookLocationValidator.POSTAL_CODE_INVALID_MESSAGE)
		);

	}

	@Test
	public void bulkUploadFileRead_location_twoMissingColumnsInHeader_fail() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER_WITH_TWO_MISSING_COLUMNS, getCorrectLocationRowUs());
		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);
		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(messages.getErrors().get(0), "Your upload is missing LocationType, LocationZip. Please check your data.");
	}

	@Test
	public void bulkUploadFileRead_location_oneMissingColumnInHeader_fail() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER_WITH_ONE_MISSING_COLUMN, getCorrectLocationRowUs());
		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);
		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(messages.getErrors().get(0), "Your upload is missing LocationType. Please check your data.");
	}

	@Test
	public void bulkUploadFileRead_location_invalidColumnInHeader_fail() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER_WITH_INVALID_COLUMN, getCorrectLocationRowUs());
		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);
		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(messages.getErrors().get(0), CRMServiceTest.INVALID_COLUMN + " is not a valid column name. Please check your data.");
	}

	@Test
	public void bulkUploadFileRead_location_duplicateColumnInHeader_fail() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER_WITH_DUPLICATE_COLUMN, getCorrectLocationRowUs());
		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);
		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(messages.getErrors().get(0), "Your upload contains a duplicate column. Please check your data.");
	}

	@Test
	public void bulkUploadFileRead_location_invalidColumnAndMissingColumn_fail() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.LOCATIONS_HEADER_WITH_INVALID_COLUMN_AND_MISSING_COLUMN, getCorrectLocationRowUs());
		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.LOCATION, UUID, messages);
		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(messages.getErrors().get(0), "Your upload is missing LocationState. " + CRMServiceTest.INVALID_COLUMN + " is not a valid column name. Please check your data.");
	}

	@Test
	public void bulkUploadFileRead_contact_invalidColumnAndMissingColumn_fail() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.CONTACTS_HEADER_WITH_INVALID_COLUMN_AND_MISSING_COLUMN, getCorrectContactRow());
		MessageBundle messages = new MessageBundle();
		crmService.bulkUploadFileRead(1L, CRMServiceImpl.CONTACT, UUID, messages);
		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(messages.getErrors().get(0), "Your upload is missing Manager. " + CRMServiceTest.INVALID_COLUMN + " is not a valid column name. Please check your data.");
	}

	@Test
	public void bulkUploadFileRead_contact_invalidLocationNumber_fail() throws Exception {
		String invalidLocationNumber = RandomUtilities.generateNumericString(6);
		String UUID = generateCSVFile(CRMServiceTest.CONTACTS_HEADER, getContactRowWithLocationNumber(invalidLocationNumber));
		MessageBundle messages = new MessageBundle();
		Company company = newCompany();

		crmService.bulkUploadFileRead(company.getId(), CRMServiceImpl.CONTACT, UUID, messages);

		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		messageHelper.getMessage("addressbook.upload.row_read.error", 2, messageHelper.getMessage("addressbook.upload.nolocation", invalidLocationNumber));
	}

	@Test
	public void bulkUploadFileRead_contact_locationNotAssociatedWithClient_fail() throws Exception {
		MessageBundle messages = new MessageBundle();
		Company company =  newCompany();
		User user = newCompanyEmployee(company.getId());
		ClientCompany clientCompany = newClientCompany(user.getId());
		Location location = createAndSaveLocation(company.getId());

		String UUID = generateCSVFile(CRMServiceTest.CONTACTS_HEADER, getContactRowWithLocationNumberAndClientId(location.getLocationNumber(), clientCompany.getCustomerId()));

		crmService.bulkUploadFileRead(company.getId(), CRMServiceImpl.CONTACT, UUID, messages);

		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(
			messages.getErrors().get(0),
			messageHelper.getMessage("addressbook.upload.row_read.error", 2, messageHelper.getMessage("addressbook.upload.nolocationwithclient", location.getLocationNumber(), clientCompany.getCustomerId()))
		);
	}

	@Test
	public void bulkUploadFileRead_contact_ambiguousMatchLocationNumber_fail() throws Exception {
		String locationNumber = "1000";
		String UUID = generateCSVFile(CRMServiceTest.CONTACTS_HEADER, getContactRowWithLocationNumber(locationNumber));
		MessageBundle messages = new MessageBundle();
		Company company =  newCompany();
		LocationDTO locationDTO = createLocationDTO();
		locationDTO.setLocationNumber(locationNumber);
		locationDTO.setCompanyId(company.getId());

		Location location1 = crmService.saveOrUpdateClientLocation(null, locationDTO, messages);
		Location location2 = crmService.saveOrUpdateClientLocation(null, locationDTO, messages);

		crmService.bulkUploadFileRead(company.getId(), CRMServiceImpl.CONTACT, UUID, messages);

		assertTrue(messages.hasErrors());
		assertEquals(messages.getErrors().size(), 1);
		assertEquals(
			messages.getErrors().get(0),
			messageHelper.getMessage("addressbook.upload.row_read.error", 2, messageHelper.getMessage("addressbook.upload.multiplelocations", locationNumber))
		);

		directoryService.deleteLocation(location1);
		directoryService.deleteLocation(location2);
	}

	@Test
	public void bulkUploadFileRead_contact_withLocationNumber_success() throws Exception {
		String UUID = generateCSVFile(CRMServiceTest.CONTACTS_HEADER, getContactRowWithLocationNumber("1000"));
		MessageBundle messages = new MessageBundle();
		Company company =  newCompany();
		LocationDTO locationDTO = createLocationDTO();
		locationDTO.setLocationNumber("1000");
		locationDTO.setCompanyId(company.getId());

		Location location1 = crmService.saveOrUpdateClientLocation(null, locationDTO, messages);
		crmService.bulkUploadFileRead(company.getId(), CRMServiceImpl.CONTACT, UUID, messages);

		assertFalse(messages.hasErrors());

		directoryService.deleteLocation(location1);
	}

	@Test
	public void testFindClientContactByClientLocationAndName() {

		ClientContact contact = crmService.findClientContactByClientLocationAndName(clientLocation.getId(),
			clientContact.getFirstName(), clientContact.getLastName());
		assertNotNull(contact);
	}

	@Test
	public void testFindClientContactByCompanyIdAndName() {

		ClientContact contact = crmService.findClientContactByCompanyIdAndName(clientLocation.getCompany().getId(),
			clientContact.getFirstName(), clientContact.getLastName());
		assertNotNull(contact);
	}

	@Test
	public void testFindClientContactByClientLocationNamePhone() {

		PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO("888-555-5785", "", ContactContextType.WORK);
		crmService.addPhoneToClientContact(clientContact.getId(), phoneNumberDTO);
		ClientContact contact = crmService.findClientContactByClientLocationNamePhone(clientLocation.getId(),
			clientContact.getFirstName(), clientContact.getLastName(), phoneNumberDTO.getPhone(),
			phoneNumberDTO.getExtension());
		assertNotNull(contact);
	}

	@Test
	public void testFindClientContactByCompanyIdNamePhone() {

		PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO("888-555-5785", "", ContactContextType.WORK);
		crmService.addPhoneToClientContact(clientContact.getId(), phoneNumberDTO);
		ClientContact contact = crmService.findClientContactByCompanyIdNamePhone(clientLocation.getCompany().getId(),
			clientContact.getFirstName(), clientContact.getLastName(), phoneNumberDTO.getPhone(),
			phoneNumberDTO.getExtension());
		assertNotNull(contact);
	}

	private static String[] getCorrectLocationRowUs() {
		return new String[]{"Alex Home","123","","1155 N Dearborn","","Chicago","IL","60610","commercial"};
	}

	private static String[] getCorrectLocationRowCan() {
		return new String[]{"Alex Canada Pad","123","","1205 PROSPERITY WAY","","WILLIAMS LAKE","BC","V2G3A7","commercial"};
	}

	private static String[] getOldLocationRowUs() {
		return new String[]{"Alex Home","123","","1155 N Dearborn","Chicago","IL","60610","commercial"};
	}

	private static String[] getCorrectContactRow() {
		return new String[]{ "Bob", "Smith", "Director", "", "bob@bob.com", "666-666-6666", "777-777-7777", "", "true"};
	}

	private static String[] getContactRowWithLocationNumber(String number) {
		return new String[]{ "Bob", "Smith", "Director", "", "bob@bob.com", "666-666-6666", "777-777-7777", number, "true"};
	}

	private static String[] getContactRowWithLocationNumberAndClientId(String number, String clientId) {
		return new String[]{ "Bob", "Smith", "Director", clientId, "bob@bob.com", "666-666-6666", "777-777-7777", number, "true"};
	}

	private String generateCSVFile(String[] csvHeader, String[] csvRows) throws Exception {
		// Prepare data
		String[] csvWithInvalidPostalCode = {
				StringUtils.join(csvHeader, ","),
				StringUtils.join(csvRows, ",")
		};
		String data = StringUtils.join(csvWithInvalidPostalCode, "\n");

		// Save CSV file
		InputStream is = new ByteArrayInputStream(data.getBytes());
		IOUtils.copy(is, new FileOutputStream(TMP_FILE));
		Upload upload = uploadService.storeUpload(TMP_FILE, "test", MimeType.TEXT_CSV.getMimeType());

		// Return UUID
		return upload.getUUID();
	}

	private ClientContactDTO createClientContactDTO() {
		ClientContactDTO clientContactDTO = new ClientContactDTO();

		clientContactDTO.setFirstName("John");
		clientContactDTO.setLastName("Smith");
		clientContactDTO.setJobTitle("Engineer");
		clientContactDTO.setManager(true);
		return clientContactDTO;
	}

	private LocationDTO createLocationDTO() {
		LocationDTO locationDTO = new LocationDTO();
		BeanUtilities.copyProperties(locationDTO, createAddressDTO());
		locationDTO.setName("Work Market");
		locationDTO.setCompanyId(COMPANY_ID);
		locationDTO.setLocationNumber("location " + RandomUtilities.nextLong());
		return locationDTO;
	}

}
