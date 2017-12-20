package com.workmarket.integration.autotask;

import com.autotask.ws.*;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.dao.integration.autotask.AutotaskUserCustomFieldsPreferenceDAO;
import com.workmarket.domains.work.dao.WorkResourceTimeTrackingDAO;
import com.workmarket.integration.autotask.proxy.AutotaskProxyFactory;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.integration.autotask.AutotaskTicket;
import com.workmarket.domains.model.integration.autotask.AutotaskUserCustomFieldsPreference;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.integration.hooks.autotask.AutotaskIntegrationServiceImpl;
import com.workmarket.test.BrokenTest;
import com.workmarket.service.business.integration.hooks.autotask.AutotaskIntegrationService;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@ContextConfiguration(
		locations = {
				"classpath:/spring/webservices-test.xml"
		})
@Ignore
public class AutotaskIntegrationServiceIT extends BaseServiceIT {
	@Autowired private AutotaskIntegrationService autotaskIntegrationService;
	@Autowired WorkService workService;
	@Autowired AutotaskProxyFactory autotaskProxyFactory;
	@Autowired WorkResourceTimeTrackingDAO workResourceTimeTrackingDAO;
	@Autowired AutotaskUserCustomFieldsPreferenceDAO autotaskUserCustomFieldsPreferenceDAO;

	private Map<String, AutotaskUserCustomFieldsPreference> getAutotaskCustomUserPreference(Long autotaskUserId) {
		Optional<List<AutotaskUserCustomFieldsPreference>> optPreferences = autotaskUserCustomFieldsPreferenceDAO.findAllPreferencesByAutotaskUser(autotaskUserId);
		Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = new HashMap<>();

		if (optPreferences.isPresent()) {
			for (AutotaskUserCustomFieldsPreference preference : optPreferences.get()) {
				preferenceMap.put(preference.getIntegrationCustomField().getCode(), preference);
			}
			return preferenceMap;
		}

		return null;
	}

	private AutotaskUser createDemoUser() throws Exception{
		User user = newWMEmployee();
		AutotaskUser autotaskUser = new AutotaskUser();
		autotaskUser.setUser(user);
		autotaskUser.setUserName("PPamela@WorkMarketdemo.com");
		autotaskUser.setPassword("12345");
		autotaskUser.setZoneUrl("https://webservices2.autotask.net/ATServices/1.5/atws.asmx");//TODO

		registrationService.addNewIntegrationUser(autotaskUser);

		return registrationService.getIntegrationUserByUserId(user.getId()).get();
	}

	private AutotaskTicket createDemoAutotaskTicket() throws Exception{
		AutotaskTicket ticket = new AutotaskTicket();
		ticket.setTicketId(8680L);
		ticket.setTicketNumber("T20081029.0156");

		return ticket;
	}

	@Test
	@Transactional
	public void test_findAutotaskUserByUserId() throws Exception{
		AutotaskUser autotaskUser = createDemoUser();

		AutotaskUser actualAutotaskUser = (autotaskIntegrationService.findAutotaskUserByUserId(autotaskUser.getUserId())).get();

		Assert.assertEquals(autotaskUser, actualAutotaskUser);
	}

	@Test
	@Transactional
	public void test_findZoneUrl() throws Exception{
		String zoneUrl = autotaskIntegrationService.findZoneUrl("PPamela@WorkMarketdemo.com").get();
		String expectedZoneUrl = "https://webservices2.autotask.net/ATServices/1.5/atws.asmx";

		Assert.assertEquals(expectedZoneUrl, zoneUrl);
	}

	@Test
	@Transactional
	public void test_getThresholdAndUsageInfo() throws Exception{
		AutotaskUser autotaskUser = new AutotaskUser();
		autotaskUser.setUserName("PPamela@WorkMarketdemo.com");
		autotaskUser.setPassword("12345");

		String zoneUrl = autotaskIntegrationService.findZoneUrl(autotaskUser.getUserName()).get();

		autotaskUser.setZoneUrl(zoneUrl);

		String info =  autotaskIntegrationService.getThresholdAndUsageInfo(autotaskUser.getUserName(), autotaskUser.getPassword(), autotaskUser.getZoneUrl()).get();

		Assert.assertTrue(info.contains("thresholdOfExternalRequest"));
	}

	@Test
	@Transactional
	public void test_simple() throws Exception {
		autotaskIntegrationService.findAutotaskCustomFieldPreferencesByAutotaskUser(1L);
	}

	@Test
	@Transactional
	public void test_validateCredentials() throws Exception{
		AutotaskUser autotaskUser = new AutotaskUser();
		autotaskUser.setUserName("PPamela@WorkMarketdemo.com");
		autotaskUser.setPassword("12345");

		String zoneUrl = autotaskIntegrationService.findZoneUrl(autotaskUser.getUserName()).get();

		autotaskUser.setZoneUrl(zoneUrl);

		Assert.assertTrue(autotaskIntegrationService.validateCredentials(autotaskUser.getUserName(), autotaskUser.getPassword(), autotaskUser.getZoneUrl()));
	}

	@Test
	@Transactional
	public void test_updateTicketUserDefinedFields() throws Exception{
		Map<String, String> autotaskProps = autotaskIntegrationService.getAutotaskProps();
		UserDefinedField field = new UserDefinedField();
		ArrayOfUserDefinedField fields = new ArrayOfUserDefinedField();
		Ticket ticket = new Ticket();
		AutotaskUser user = createDemoUser();

		ticket.setTitle("Changed Title");
		ticket.setAccountID(296162);
		ticket.setStatus(10);
		ticket.setPriority(2);
		ticket.setId(8680);
		ticket.setAssignedResourceID(296068);
		ticket.setAssignedResourceRoleID(3905908);
		ticket.setDueDateTime(new Date(1311607740000L));

		if(autotaskProps != null) {
			field.setName(autotaskProps.get(AutotaskIntegrationServiceImpl.WM_STATUS));
			field.setValue("assigned");
		}

		Assert.assertNotNull(field.getName());
		Assert.assertNotNull(field.getValue());

		fields.getUserDefinedField().add(field);
		Assert.assertTrue(autotaskIntegrationService.updateTicketUserDefinedFields(user, ticket, fields));
	}

	@Test
	@Transactional
	public void test_updateTicketOnWorkCreated() throws Exception {
		AutotaskUser user =  createDemoUser();
		Work work = newWork(user.getUser().getId());
		AutotaskTicket ticket = new AutotaskTicket();
		ticket.setTicketId(8680L);
		Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(user.getId());

		Assert.assertTrue(autotaskIntegrationService.updateTicketOnWorkCreated(user, ticket, work, preferenceMap));
	}

	@Test
	@Transactional
	public void test_updateTicketOnWorkAccepted() throws Exception {
		AutotaskUser user = createDemoUser();
		AutotaskTicket ticket = createDemoAutotaskTicket();

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(user.getUser().getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		AbstractWork abstractWork = workService.findWork(work.getId());
		WorkResource workResource = workService.findActiveWorkResource(work.getId());
		Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(user.getId());

		Assert.assertTrue(autotaskIntegrationService.updateTicketOnWorkAccepted(user, ticket, abstractWork, workResource, preferenceMap));
	}

	@Test
	@Transactional
	public void test_updateTicketToCheckInOut() throws Exception {
		AutotaskUser user = createDemoUser();
		AutotaskTicket ticket = createDemoAutotaskTicket();

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(user.getUser().getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		WorkResource workResource = workService.findActiveWorkResource(work.getId());
		Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(user.getId());


		Assert.assertTrue(autotaskIntegrationService.updateTicketToCheckInOut(user, ticket, workResource, preferenceMap));
	}

	@Test
	public void test_findAutotaskCustomFieldPreferencesByAutotaskUser() throws Exception {
		List<AutotaskUserCustomFieldsPreference> preferences = autotaskIntegrationService.findAutotaskCustomFieldPreferencesByAutotaskUser(1002L);

		Assert.assertNotNull(preferences);
		Assert.assertTrue(preferences.size() > 0);

	}

	@Test
	@Transactional
	public void test_updateTicketOnWorkComplete() throws Exception {
		AutotaskUser user = createDemoUser();
		AutotaskTicket ticket = createDemoAutotaskTicket();

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(user.getUser().getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		AbstractWork abstractWork = workService.findWork(work.getId());
		PaymentSummaryDTO paymentDTO = paymentSummaryService.generatePaymentSummaryForWork(work.getId());
		Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(user.getId());


		Assert.assertTrue(autotaskIntegrationService.updateTicketOnWorkComplete(user, ticket, abstractWork, paymentDTO, preferenceMap));
	}

	@Test
	@Transactional
	public void test_updateTicketOnWorkApproved() throws Exception {
		AutotaskUser user = createDemoUser();
		AutotaskTicket ticket = createDemoAutotaskTicket();

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(user.getUser().getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		AbstractWork abstractWork = workService.findWork(work.getId());
		PaymentSummaryDTO paymentDTO = paymentSummaryService.generatePaymentSummaryForWork(work.getId());
		Map<String, AutotaskUserCustomFieldsPreference> preferenceMap = getAutotaskCustomUserPreference(user.getId());

		Assert.assertTrue(autotaskIntegrationService.updateTicketOnWorkApproved(user, ticket, abstractWork, paymentDTO, preferenceMap));

	}

	@Test
	@Transactional
	public void test_updateTicketAttachmentData() throws Exception {
		AutotaskUser autotaskUser = createDemoUser();
		AutotaskTicket ticket = createDemoAutotaskTicket();

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(autotaskUser.getUser().getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		Asset asset = new Asset();
		asset.setRemoteUri("http://www.google.com/sample2.pdf");
		asset.setName("sample2.pdf");
		asset.setFileByteSize(1234);

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		Assert.assertTrue(autotaskIntegrationService.updateTicketOnAttachmentData(autotaskUser, ticket, asset));
	}

	@Test
	@Transactional
	public void test_updateTicketOnNoteAdded() throws Exception {
		AutotaskUser user = createDemoUser();
		AutotaskTicket ticket = createDemoAutotaskTicket();

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(user.getUser().getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		Note note = new Note();
		note.setContent("My new note for work");
		note.setCreatorId(resource.getId());

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));

		Assert.assertTrue(autotaskIntegrationService.updateTicketOnNoteAdded(user, ticket, note));
	}
}
