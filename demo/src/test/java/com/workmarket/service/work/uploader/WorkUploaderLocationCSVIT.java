package com.workmarket.service.work.uploader;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.service.business.dto.WorkTemplateDTO;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.uploader.WorkUploadErrorType;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static com.jayway.awaitility.Awaitility.await;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.CLIENT_NAME;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.FAIL;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.PASS;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.addBlendedPerHourWithClientCompany;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.fixtures;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class WorkUploaderLocationCSVIT extends BaseWorkUploaderCSVIT {

	@Autowired TWorkService tWorkService;
	@Autowired DirectoryService directoryService;
	@Autowired WorkTemplateService workTemplateService;
	@Autowired SessionFactory sessionFactory;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired CRMService crmService;

	// new location new contact
	@Test
	public void newLocationNewContact_Success() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String csv = fixtures.get(PASS).get("new_location_with_new_contact");

		WorkUploadRequest request = buildRequest(csv, "new_location_with_new_contact.csv");
		WorkUploadResponse response = workUploader.uploadWork(request);
		assertEquals(0, response.getErrorUploadsSize());
	}

	// two different new locations
	@Test
	public void newLocationsSameInfo_NoDupes() throws Exception {
		String csv = fixtures.get(PASS).get("new_locations_no_duplicate");

		user = newFirstEmployeeWithCashBalance();
		WorkUploadRequest request = buildRequest(csv, "new_locations_no_client.csv");
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		Set<WorkRequestInfo> includes = ImmutableSet.of(
				WorkRequestInfo.LOCATION_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO);
		final Work work1 = tWorkFacadeService.findWork(new WorkRequest()
				.setWorkNumber(workNumbers.get(0))
				.setUserId(user.getId())
				.setIncludes(includes))
				.getWork();
		final Work work2 = tWorkFacadeService.findWork(new WorkRequest()
				.setWorkNumber(workNumbers.get(1))
				.setUserId(user.getId())
				.setIncludes(includes))
				.getWork();

		assertEquals(work1.getLocation().getId(), work2.getLocation().getId());
	}

	// two different new locations
	@Test
	public void newLocationsWithClientSameInfo_NoDupes() throws Exception {
		String csv = fixtures.get(PASS).get("new_locations_and_client_no_duplicate");

		user = newFirstEmployeeWithCashBalance();
		WorkUploadRequest request = buildRequest(csv, "new_locations_and_client.csv");

		createClientCompany(user.getId());
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		Set<WorkRequestInfo> includes = ImmutableSet.of(
				WorkRequestInfo.LOCATION_INFO,
				WorkRequestInfo.LOCATION_CONTACT_INFO,
				WorkRequestInfo.CLIENT_COMPANY_INFO);
		final Work work1 = tWorkFacadeService.findWork(new WorkRequest()
				.setWorkNumber(workNumbers.get(0))
				.setUserId(user.getId())
				.setIncludes(includes))
				.getWork();
		final Work work2 = tWorkFacadeService.findWork(new WorkRequest()
				.setWorkNumber(workNumbers.get(1))
				.setUserId(user.getId())
				.setIncludes(includes))
				.getWork();

		assertEquals(work1.getLocation().getId(), work2.getLocation().getId());
	}

	// two different new locations
	@Test
	public void newLocationsNoClient_Success() throws Exception {
		String csv = fixtures.get(PASS).get("new_locations_no_client");

		user = newFirstEmployeeWithCashBalance();
		WorkUploadRequest request = buildRequest(csv, "new_locations_no_client.csv");
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		assertEquals(2, workNumbers.size());
	}

	// new client location for existing client
	@Test
	public void newClientLocationExistingClient_Success() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		clientCompany = newClientCompany(user.getId());

		addBlendedPerHourWithClientCompany("blended_per_hour_with_client", clientCompany.getName());

		String blendedPerHour = fixtures.get(PASS).get("blended_per_hour_with_client");

		WorkUploadRequest request = buildRequest(blendedPerHour, "001-blended_per_hour.csv")
			.setMappingGroup(createStevesFieldMappingGroup());
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		List<ClientLocation> locations = crmService.findAllLocationsByClientCompany(clientCompany.getCompany().getId(), clientCompany.getId());
		final Work work = tWorkFacadeService.findWork(new WorkRequest()
			.setWorkNumber(workNumbers.get(0))
			.setUserId(user.getId())
			.setIncludes(ImmutableSet.of(
					WorkRequestInfo.LOCATION_INFO,
					WorkRequestInfo.LOCATION_CONTACT_INFO)))
			.getWork();

		Optional<ClientLocation> uploadedLocation = Iterables.tryFind(locations, new Predicate<ClientLocation>() {
			@Override public boolean apply(ClientLocation input) {
				return input.getId().equals(work.getLocation().getId());
			}
		});
		//assertEquals(0, response.getErrorUploadsSize());
		assertTrue(uploadedLocation.isPresent());
		assertTrue(uploadedLocation.get().getAddress().getAddress1().equals(work.getLocation().getAddress().getAddressLine1()));
	}

	// new locations with multiple countries
	@Test
	public void newLocationsWithMultipleCountries_Success() throws Exception {
		String csv = fixtures.get(PASS).get("new_locations_no_client_multiple_countries");

		user = newFirstEmployeeWithCashBalance();
		WorkUploadRequest request = buildRequest(csv, "new_locations_no_client_multiple_countries.csv");
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		assertEquals(2, workNumbers.size());
	}

	@Test
	public void existingLocationAndClient_ByLocationName_Success() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		CSVUploaderHelper.LOCATION_DTO_WITH_NO_LOCATION_NUMBER.setCompanyId(user.getCompany().getId());
		ClientCompany newClientCompany = createClientCompany(user.getId());
		ClientLocation existingLocation = crmService.saveOrUpdateClientLocation(newClientCompany.getId(), CSVUploaderHelper.LOCATION_DTO_WITH_NO_LOCATION_NUMBER, null);

		String csv = fixtures.get(PASS).get("existing_locations_by_name");

		WorkUploadRequest request = buildRequest(csv, "existing_locations_by_name.csv");
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		final Work work = tWorkFacadeService.findWork(new WorkRequest()
				.setWorkNumber(workNumbers.get(0))
				.setUserId(user.getId())
				.setIncludes(ImmutableSet.of(
						WorkRequestInfo.LOCATION_INFO,
						WorkRequestInfo.LOCATION_CONTACT_INFO)))
				.getWork();
		assertEquals(existingLocation.getId(), Long.valueOf(work.getLocation().getId()));
	}

	@Test
	public void existingLocationAndClient_ByLocationNumber_Success() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		CSVUploaderHelper.LOCATION_DTO.setCompanyId(user.getCompany().getId());
		ClientCompany newClientCompany = createClientCompany(user.getId());
		ClientLocation existingLocation = crmService.saveOrUpdateClientLocation(newClientCompany.getId(), CSVUploaderHelper.LOCATION_DTO, null);

		String csv = fixtures.get(PASS).get("existing_location_by_number");

		WorkUploadRequest request = buildRequest(csv, "existing_location_by_number.csv");
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		final Work work = tWorkFacadeService.findWork(new WorkRequest()
				.setWorkNumber(workNumbers.get(0))
				.setUserId(user.getId())
				.setIncludes(ImmutableSet.of(
						WorkRequestInfo.LOCATION_INFO,
						WorkRequestInfo.LOCATION_CONTACT_INFO)))
				.getWork();
		assertEquals(existingLocation.getId(), Long.valueOf(work.getLocation().getId()));
	}

	@Test
	public void newLocationWithProjectAndClient_ByLocationName_Success() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		ClientCompany newClientCompany = createClientCompany(user.getId());
		crmService.saveOrUpdateClientLocation(newClientCompany.getId(), CSVUploaderHelper.LOCATION_DTO_WITH_NO_LOCATION_NUMBER, null);

		User employee = newEmployeeWithCashBalance();
		WorkTemplateDTO workTemplateDTO = newWorkTemplateBlankDTO();

		WorkTemplate template = workTemplateService.saveOrUpdateWorkTemplate(employee.getId(), workTemplateDTO);
		Project project = new Project();
		project.setName("Project Title");
		project.setDescription("Project Description");
		project.setDueDate(Calendar.getInstance());
		project.setClientCompany(newClientCompany);
		project.setCompany(employee.getCompany());
		projectService.saveOrUpdate(project);

		template.setProject(project);

		String csv = fixtures.get(PASS).get("new_location_with_project_and_client");

		WorkUploadRequest request = buildRequest(csv, "new_location_with_project_and_client.csv")
			.setTemplateId(template.getId());
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		assertEquals(template.getId(), workService.findWorkByWorkNumber(workNumbers.get(0)).getTemplate().getId());
	}

	// new client location for unknown client - Fail
	@Test
	public void newClientLocationUnknownClient_Fail() throws Exception {
		String csv = fixtures.get(FAIL).get("new_locations_new_client");

		user = newFirstEmployeeWithCashBalance();
		WorkUploadRequest request = buildRequest(csv, "new_locations_new_client.csv");
		WorkUploadResponse response = workUploader.uploadWork(request);

		assertEquals(1, response.getErrorUploadsSize());
		assertEquals(WorkUploadErrorType.INVALID_DATA, response.getErrorUploads().get(0).getErrors().get(0).getErrorType());
	}

	@Test
	public void existingClientNewLocationWithoutAddress_ByName_Fail() throws Exception {

		user = newFirstEmployeeWithCashBalance();
		createClientCompany(user.getId());

		String csv = fixtures.get(FAIL).get("existing_client_new_location_by_name");

		WorkUploadRequest request = buildRequest(csv, "existing_client_new_location_by_name.csv");
		WorkUploadResponse response = workUploader.uploadWork(request);

		assertEquals(1, response.getErrorUploadsSize());
		assertEquals(WorkUploadErrorType.INVALID_DATA, response.getErrorUploads().get(0).getErrors().get(0).getErrorType());
	}

	@Test
	public void nonexistentLocationByNumber_Fail() throws Exception {
		String csv = fixtures.get(FAIL).get("nonexistent_location_by_number");

		user = newFirstEmployeeWithCashBalance();
		WorkUploadRequest request = buildRequest(csv, "nonexistent_location_by_number.csv");
		WorkUploadResponse response = workUploader.uploadWork(request);

		assertEquals(1, response.getErrorUploadsSize());
		assertEquals(WorkUploadErrorType.INVALID_DATA, response.getErrorUploads().get(0).getErrors().get(0).getErrorType());
	}

	private ClientCompany createClientCompany(Long userId) {
		ClientCompanyDTO clientDTO = new ClientCompanyDTO();
		clientDTO.setName(fixtures.get(PASS).get(CLIENT_NAME));
		clientDTO.setLocationTypeId(LocationType.EDUCATION_CODE);

		return crmService.saveOrUpdateClientCompany(userId, clientDTO, null);
	}
}
