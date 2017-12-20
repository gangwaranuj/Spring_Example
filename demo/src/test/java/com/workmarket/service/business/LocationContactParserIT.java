package com.workmarket.service.business;

import com.google.api.client.util.Maps;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.upload.parser.LocationContactParser;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.work.Work;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class LocationContactParserIT extends BaseServiceIT {

	@Autowired private LocationContactParser locationContactParser;
	@Autowired private CRMService crmService;

	private User user;
	private Company company;
	private ClientCompany clientCompany;
	private ClientContact clientContact;
	private ClientLocation clientLocation;

	@Before
	public void before() throws Exception {
		user = userService.findUserById(ANONYMOUS_USER_ID);
		company = new Company(
			user.getCompany().getId(),
			user.getCompany().getCompanyNumber(),
			user.getCompany().getUuid(),
			user.getCompany().getName(),
			null,
			null,
			null,
			null,
			1,
			1,
			new Date().getTime(),
			null);
		clientCompany = newClientCompany(user.getId());
		clientContact = newClientContactForCompany(user.getCompany().getId());
		clientLocation = newClientLocationForClientCompany(user.getCompany().getId(), clientCompany.getId());
		clientContact.setClientLocation(clientLocation);
		crmService.addLocationToClientContact(clientContact.getId(), clientLocation.getId());
	}

	@Test
	public void testFindContactByCompanyIdFirstLastName() {

		Work work = new Work();
		work.setCompany(company);
		work.setLocation(new Location());
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(work);

		Map<String,String> types = Maps.newHashMap();
		types.put(WorkUploadColumn.CONTACT_FIRST_NAME.getUploadColumnName(), clientContact.getFirstName());
		types.put(WorkUploadColumn.CONTACT_LAST_NAME.getUploadColumnName(), clientContact.getLastName());
//			types.put(WorkUploadColumn.CONTACT_EMAIL.getUploadColumnName(), user.getFirstName());
		WorkUploaderBuildData buildData = new WorkUploaderBuildData();
		buildData.setTypes(types);

		locationContactParser.build(response, buildData);

		assertNotNull(response.getWork().getLocationContact());
		assertNotNull(response.getWork().getLocationContact().getId());
	}

	@Test
	public void testFindContactByLocationIdFirstLastName() {

		Work work = new Work();
		work.setCompany(company);
		work.setLocation(new Location(clientLocation.getId(), clientLocation.getLocationNumber(), clientLocation.getName(), null));
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(work);

		Map<String,String> types = Maps.newHashMap();
		types.put(WorkUploadColumn.CONTACT_FIRST_NAME.getUploadColumnName(), clientContact.getFirstName());
		types.put(WorkUploadColumn.CONTACT_LAST_NAME.getUploadColumnName(), clientContact.getLastName());
		WorkUploaderBuildData buildData = new WorkUploaderBuildData();
		buildData.setTypes(types);

		locationContactParser.build(response, buildData);

		assertNotNull(response.getWork().getLocationContact());
		assertNotNull(response.getWork().getLocationContact().getId());
	}

	@Test
	public void testFindContactByCompanyIdFirstLastNamePhone() {

		PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO("8888888888", "", ContactContextType.WORK);
		crmService.addPhoneToClientContact(clientContact.getId(), phoneNumberDTO);

		Work work = new Work();
		work.setCompany(company);
		work.setLocation(new Location());
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(work);

		Map<String,String> types = Maps.newHashMap();
		types.put(WorkUploadColumn.CONTACT_FIRST_NAME.getUploadColumnName(), clientContact.getFirstName());
		types.put(WorkUploadColumn.CONTACT_LAST_NAME.getUploadColumnName(), clientContact.getLastName());
		types.put(WorkUploadColumn.CONTACT_PHONE.getUploadColumnName(), phoneNumberDTO.getPhone());
//			types.put(WorkUploadColumn.CONTACT_EMAIL.getUploadColumnName(), user.getFirstName());
		WorkUploaderBuildData buildData = new WorkUploaderBuildData();
		buildData.setTypes(types);

		locationContactParser.build(response, buildData);

		assertNotNull(response.getWork().getLocationContact());
		assertNotNull(response.getWork().getLocationContact().getId());
	}

	@Test
	public void testFindContactByLocationIdFirstLastNamePhone() {

		PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO("8888888888", "", ContactContextType.WORK);
		crmService.addPhoneToClientContact(clientContact.getId(), phoneNumberDTO);

		Work work = new Work();
		work.setCompany(company);
		work.setLocation(new Location(clientLocation.getId(), clientLocation.getLocationNumber(), clientLocation.getName(), null));
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(work);

		Map<String,String> types = Maps.newHashMap();
		types.put(WorkUploadColumn.CONTACT_FIRST_NAME.getUploadColumnName(), clientContact.getFirstName());
		types.put(WorkUploadColumn.CONTACT_LAST_NAME.getUploadColumnName(), clientContact.getLastName());
		types.put(WorkUploadColumn.CONTACT_PHONE.getUploadColumnName(), phoneNumberDTO.getPhone());
		WorkUploaderBuildData buildData = new WorkUploaderBuildData();
		buildData.setTypes(types);

		locationContactParser.build(response, buildData);

		assertNotNull(response.getWork().getLocationContact());
		assertNotNull(response.getWork().getLocationContact().getId());
	}
}
