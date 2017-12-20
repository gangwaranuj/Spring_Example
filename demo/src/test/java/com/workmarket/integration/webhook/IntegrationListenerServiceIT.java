package com.workmarket.integration.webhook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;
import com.workmarket.service.business.integration.event.IntegrationEvent;
import com.workmarket.service.business.integration.event.IntegrationListenerService;
import com.workmarket.service.business.integration.hooks.webhook.WebHookIntegrationService;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class IntegrationListenerServiceIT extends BaseServiceIT {

	@Autowired IntegrationListenerService integrationListenerService;
	@Autowired AssetManagementService assetManagementService;
	@Autowired WebHookIntegrationService webHookIntegrationService;
	@Autowired WorkNoteService workNoteService;
	@Autowired WorkStatusService workStatusService;
	@Autowired WorkSubStatusService workSubStatusService;
	@Autowired WorkNegotiationService workNegotiationService;
	@Autowired UserService userService;

	IntegrationListenerService integrationListenerServiceSpy;

	private static final String url = "https://www.workmarket.com";
	private String uniqueId;

	@Before
	public void before() throws Exception {
		uniqueId = RandomUtilities.generateAlphaNumericString(10);

		integrationListenerServiceSpy = spy(integrationListenerService);

		doReturn(true)
			.when(integrationListenerServiceSpy)
			.doRequest(any(WebHook.class), any(ParsedWebHookDTO.class));

		authenticationService.setCurrentUser(ANONYMOUS_USER_ID);
	}

	@After
	public void after() throws Exception {
		deleteTestFile(uniqueId);
	}

	private WebHook createNewWebHook(String body, WebHook.ContentType contentType, String url) throws MalformedURLException {
		return createNewWebHook(IntegrationEventType.newInstance("workAccept"), body, contentType, url);
	}

	private WebHook createNewWebHook(IntegrationEventType eventType, String body, WebHook.ContentType contentType, String url) throws MalformedURLException {
		User user = userService.getUser(ANONYMOUS_USER_ID);

		AbstractWebHookClient abstractWebHookClient = new AbstractWebHookClient();
		abstractWebHookClient.setCompany(user.getCompany());
		abstractWebHookClient.setDateFormat(AbstractWebHookClient.DateFormat.ISO_8601);

		WebHook webHook = new WebHook();
		webHook.setWebHookClient(abstractWebHookClient);
		webHook.setContentType(contentType);
		webHook.setUrl(new URL(url));
		webHook.setBody(body);
		webHook.setMethodType(WebHook.MethodType.POST);
		webHook.setCallOrder(0);
		webHook.setIntegrationEventType(eventType);
		webHook.setEnabled(true);
		webHookIntegrationService.saveOrUpdate(webHook);

		return webHook;
	}

	@Test
	public void OnWorkCreated() throws Exception {
		Work work = newWork(ANONYMOUS_USER_ID);
		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));

		String body = "{\"start_date_time\" : \"${start_date_time}\", \"end_date_time\" : \"${end_date_time}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);

		assertTrue(integrationListenerServiceSpy.onWorkCreated(work.getId(), webHook.getId()));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkSent() throws Exception {
		Work work = newWork(ANONYMOUS_USER_ID);
		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));

		String body = "{\"start_date_time\" : \"${start_date_time}\", \"end_date_time\" : \"${end_date_time}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);

		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
				IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
				IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkSent(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkApplied_checkRenderedTemplate() throws Exception {
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newWorkWithApplyEnabled(user.getId());
		workService.saveOrUpdateWork(work);
		User resource = newRegisteredWorker();
		resource.getProfile().setOverview("Overview");
		profileService.saveOrUpdateProfile(resource.getProfile());

		laneService.addUsersToCompanyLane2(ImmutableList.of(resource.getId()), user.getCompany().getId());
		WorkRoutingResponseSummary workRoutingResponseSummary = workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		WorkNegotiationDTO negotiationDTO = new WorkNegotiationDTO();
		negotiationDTO.setPricingStrategyId(work.getPricingStrategy().getId());
		String note = "I'm good at this";
		negotiationDTO.setNote(note);
		negotiationDTO.setInitiatedByResource(true);
		negotiationDTO.setBudgetIncrease(true);
		negotiationDTO.setAdditionalExpenses(5.00);
		negotiationDTO.setPriceNegotiation(true);
		negotiationDTO.setScheduleFromString(DateUtilities.getISO8601(DateUtilities.getMidnightNextWeek()));
		negotiationDTO.setScheduleNegotiation(true);
		negotiationDTO.setFlatPrice(20.00);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createApplyNegotiation(work.getId(), resource.getId(), negotiationDTO);

		Map<String, String> bodyTemplate = ImmutableMap.<String, String>builder()
			.put("resource_id", "${resource_id}")
			.put("resource_uuid", "${resource_uuid}")
			.put("resource_first_name", "${resource_first_name}")
			.put("resource_overview", "${resource_overview}")
			.put("resource_company_id", "${resource_company_id}")
			.put("resource_company_name", "${resource_company_name}")
			.put("resource_company_uuid", "${resource_company_uuid}")
			.put("proposed_flat_price", "${proposed_flat_price}")
			.put("proposed_expense_reimbursement_amount", "${proposed_expense_reimbursement_amount}")
			.put("proposed_start_date_time", "${proposed_start_date_time}")
			.put("note", "${note}")
			.build();

		Map<String, String> bodyRendered = ImmutableMap.<String, String>builder()
			.put("resource_id", resource.getUserNumber())
			.put("resource_uuid", resource.getUuid())
			.put("resource_first_name", resource.getFirstName())
			.put("resource_overview", resource.getProfile().getOverview())
			.put("resource_company_id", resource.getCompany().getCompanyNumber())
			.put("resource_company_name", resource.getCompany().getName())
			.put("resource_company_uuid", resource.getCompany().getUuid())
			.put("proposed_flat_price", BigDecimal.valueOf(negotiationDTO.getFlatPrice()).setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString())
			.put("proposed_expense_reimbursement_amount", BigDecimal.valueOf(negotiationDTO.getAdditionalExpenses()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
			.put("proposed_start_date_time", negotiationDTO.getScheduleFromString())
			.put("note", note)
			.build();

		String bodyTemplateJson = new ObjectMapper().writeValueAsString(bodyTemplate);
		String bodyRenderedJson = new ObjectMapper().writeValueAsString(bodyRendered);

		WebHook webHook = createNewWebHook(
			IntegrationEventType.newInstance(IntegrationEventType.WORK_NEGOTIATION_REQUEST),
			bodyTemplateJson,
			WebHook.ContentType.JSON,
			url);


		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.NEGOTIATION_ID, negotiationResponse.getWorkNegotiationId(),
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.NOTIFY_MBO, Boolean.FALSE,
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkNegotiationRequested(work.getId(), eventArgs));

		ParsedWebHookDTO parsedWebHookDTO = new ParsedWebHookDTO();
		parsedWebHookDTO.setBody(bodyTemplateJson);
		parsedWebHookDTO.setHeaders(ImmutableMap.of("Content-Type", "application/json"));
		parsedWebHookDTO.setUri(new URI(url));

		ArgumentCaptor<WebHook> webhookArg = ArgumentCaptor.forClass(WebHook.class);
		ArgumentCaptor<ParsedWebHookDTO> webhookDtoArg = ArgumentCaptor.forClass(ParsedWebHookDTO.class);

		verify(integrationListenerServiceSpy, timeout(30000)).doRequest(webhookArg.capture(),  webhookDtoArg.capture());
		assertEquals(bodyRenderedJson, webhookDtoArg.getValue().getBody());
	}


	@Test
	public void OnWorkAccepted() throws Exception {
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newInternalWork(user.getId());
		User resource = newInternalUser();

		laneService.addUserToCompanyLane1(resource.getId(), user.getCompany().getId());
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		AcceptWorkResponse response = workService.acceptWork(resource.getId(), work.getId());

		String body = "{\"resource_id\" : \"${resource_id}\", \"resource_first_name\" : \"${resource_first_name}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
				IntegrationEvent.RESOURCE_ID, response.getActiveResource().getId(),
				IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
				IntegrationEvent.NOTIFY_MBO, Boolean.FALSE,
				IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkAccepted(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkComplete() throws Exception {
		Work work = newWork(ANONYMOUS_USER_ID);
		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		workService.completeWork(work.getId(), new CompleteWorkDTO());

		String body = "{\"pricing_type\" : \"${pricing_type}\", \"end_date_time\" : \"${end_date_time}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
				IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
				IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkComplete(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkApproved() throws Exception {
		Work work = newWork(ANONYMOUS_USER_ID);
		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		AbstractWork abstractWork = workService.findWork(work.getId());
		workService.completeWork(abstractWork.getId(), new CompleteWorkDTO());
		workService.closeWork(abstractWork.getId());

		String body = "{\"resolution\" : \"${resolution}\", \"end_date_time\" : \"${end_date_time}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
				IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
				IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkApproved(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkVoided() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));

		assertTrue(workService.voidWork(work.getId(), "voided").isEmpty());

		String body = "{\"now\" : \"${now}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
				IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
				IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkVoided(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkCancelled() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.BUYER_CANCELLED);
		cancelWorkDTO.setNote("Cancellation note");
		cancelWorkDTO.setPrice(Double.parseDouble("200"));
		cancelWorkDTO.setWorkId(work.getId());
		assertTrue(workService.cancelWork(cancelWorkDTO).isEmpty());

		String body = "{\"resolution\" : \"${resolution}\", \"start_date_time\" : \"${start_date_time}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
				IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
				IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkCancelled(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkConfirmed() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmation(employee.getId(), "2013-04-04");
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.confirmWorkResource(contractor.getId(), work.getId());

		String body = "{\"resolution\" : \"${resolution}\", \"start_date_time\" : \"${start_date_time}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);

		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
				IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
				IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkConfirmed(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnCheckInOut() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		workService.confirmWorkResource(contractor.getId(), work.getId());
		TimeTrackingResponse timeTrackingResponse = workService.checkInActiveResource(new TimeTrackingRequest()
				.setWorkId(work.getId())
				.setDate(new GregorianCalendar()));


		String body = "{\"check_in_out_id\" : \"${check_in_out_id}\", \"checked_in_on\" : \"${checked_in_on}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
				IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
				IntegrationEvent.TIME_TRACKING_ID, timeTrackingResponse.getTimeTracking().getId(),
				IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onCheckIn(work.getId(), eventArgs));

		body = "{\"check_in_out_id\" : \"${check_in_out_id1}\", \"checked_in_on\" : \"${checked_in_on}\"}";

		webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		eventArgs.put(IntegrationEvent.WEBHOOK_ID, webHook.getId());

		Assert.assertFalse(integrationListenerServiceSpy.onCheckIn(work.getId(), eventArgs));

		timeTrackingResponse = workService.checkOutActiveResource(new TimeTrackingRequest().setWorkId(work.getId()).setDate(new GregorianCalendar()));
		eventArgs.put(IntegrationEvent.TIME_TRACKING_ID, timeTrackingResponse.getTimeTracking().getId());

		body = "{\"check_in_out_id\" : \"${check_in_out_id}\", \"checked_out_on\" : \"${checked_out_on}\"}";

		webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		eventArgs.put(IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onCheckOut(work.getId(), eventArgs));

		verify(integrationListenerServiceSpy, times(3)).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnCustomFieldsUpdated() throws Exception {
		Work work = newWork(ANONYMOUS_USER_ID);
		WorkCustomFieldGroup fieldGroup = addCustomFieldsToWork(work.getId());

		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));

		WorkCustomField field = fieldGroup.getActiveWorkCustomFields().get(0);

		customFieldService.saveWorkCustomFieldForWork(new WorkCustomFieldDTO(field.getId(), "value"), work.getId());

		String body = "{\"now\" : \"${now}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkCustomFieldsUpdated(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnAttachmentAdded() throws Exception {
		initializeTestFile(uniqueId);

		Work work = newWork(ANONYMOUS_USER_ID);
		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		AssetDTO assetDTO = new AssetDTO();
		assetDTO.setSourceFilePath(STORAGE_TEST_FILE + uniqueId);
		assetDTO.setName("name");
		assetDTO.setDescription("description");
		assetDTO.setMimeType(MimeType.TEXT_PLAIN.getMimeType());

		WorkAssetAssociation workAssetAssociation = assetManagementService.storeAssetForWork(assetDTO, work.getId());

		String body = "{\"file_name\" : \"${file_name}\", \"file_description\" : \"${file_description}\", \"file_uuid\" : \"${file_uuid}\", \"file_data\" : \"${file_data}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.ASSET_ID, workAssetAssociation.getAsset().getId(),
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onAttachmentAdded(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnAttachmentRemoved() throws Exception {
		initializeTestFile(uniqueId);

		Work work = newWork(ANONYMOUS_USER_ID);
		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		AssetDTO assetDTO = new AssetDTO();
		assetDTO.setSourceFilePath(STORAGE_TEST_FILE + uniqueId);
		assetDTO.setName("name");
		assetDTO.setDescription("description");
		assetDTO.setMimeType(MimeType.TEXT_PLAIN.getMimeType());

		WorkAssetAssociation workAssetAssociation = assetManagementService.storeAssetForWork(assetDTO, work.getId());
		Asset asset = workAssetAssociation.getAsset();
		assetManagementService.removeAssetFromWork(asset.getId(), work.getId());

		String body = "{\"file_name\" : \"${file_name}\", \"file_description\" : \"${file_description}\", \"file_uuid\" : \"${file_uuid}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.ASSET_ID, asset.getId(),
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onAttachmentRemoved(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnNoteAdded() throws Exception {
		initializeTestFile();

		Work work = newWork(ANONYMOUS_USER_ID);
		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		Note note = workNoteService.addNoteToWork(work.getId(), new NoteDTO("Note testing"));

		String body = "{\"note\" : \"${note}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.NOTE_ID, note.getId(),
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onNoteAdded(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnLabelAddedRemoved() throws Exception {
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newWork(user.getId());
		User resource = newInternalUser();

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		work.setWorkStatusType(new WorkStatusType(WorkStatusType.SENT));
		workService.acceptWork(resource.getId(), work.getId());

		WorkSubStatusTypeDTO workSubStatusDTO = new WorkSubStatusTypeDTO();
		workSubStatusDTO.setCode("new " + RandomUtilities.generateNumericString(5));
		workSubStatusDTO.setDescription("some description");
		workSubStatusDTO.setAlert(true);
		workSubStatusDTO.setCompanyId(user.getCompany().getId());
		workSubStatusDTO.setNotifyResourceEnabled(true);

		WorkSubStatusType subStatus = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusDTO);
		assertNotNull(subStatus);

		WorkNote workNote = new WorkNote("Something called note", work);
		workNote.setIsPrivate(false);
		workNoteService.saveOrUpdate(workNote);

		WorkSubStatusTypeAssociation association = new WorkSubStatusTypeAssociation();
		association.setWork(work);
		association.setWorkSubStatusType(subStatus);
		association.setTransitionNote(workNote);
		workSubStatusService.saveOrUpdateAssociation(association);

		String body = "{\"label_name\" : \"${label_name}\", \"label_id\" : \"${label_id}\", \"is_negotiation\" : \"${is_negotiation}\", \"note\" : \"${note}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.WORK_SUBSTATUS_TYPE_ASSOCIATION_ID, association.getId(),
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onLabelAdded(work.getId(), eventArgs));

		body = "{\"label_name\" : \"${label_name}\", \"label_id\" : \"${label_id}\", \"is_negotiation\" : \"${is_negotiation}\", \"note\" : \"${note}\"}";

		webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		eventArgs.put(IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onLabelRemoved(work.getId(), eventArgs));

		verify(integrationListenerServiceSpy, times(2)).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkReschedule_checkRenderedTemplate() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO negotiationDTO = new WorkNegotiationDTO();
		String note = "Gotta reschedule";
		negotiationDTO.setNote(note);
		negotiationDTO.setInitiatedByResource(true);
		negotiationDTO.setScheduleFromString(DateUtilities.getISO8601(DateUtilities.getMidnightNextWeek()));
		negotiationDTO.setScheduleNegotiation(true);

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), negotiationDTO);

		assertNotNull(negotiation);

		Map<String, String> bodyTemplate = ImmutableMap.<String, String>builder()
			.put("proposed_start_date_time", "${proposed_start_date_time}")
			.put("note", "${note}")
			.build();

		Map<String, String> bodyRendered = ImmutableMap.<String, String>builder()
			.put("proposed_start_date_time", negotiationDTO.getScheduleFromString())
			.put("note", note)
			.build();

		String bodyTemplateJson = new ObjectMapper().writeValueAsString(bodyTemplate);
		String bodyRenderedJson = new ObjectMapper().writeValueAsString(bodyRendered);

		WebHook webHook = createNewWebHook(
			IntegrationEventType.newInstance(IntegrationEventType.WORK_RESCHEDULE_REQUEST),
			bodyTemplateJson,
			WebHook.ContentType.JSON,
			url);

		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.NEGOTIATION_ID, negotiation.getId(),
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.NOTIFY_MBO, Boolean.FALSE,
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onWorkRescheduleRequested(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));

		ParsedWebHookDTO parsedWebHookDTO = new ParsedWebHookDTO();
		parsedWebHookDTO.setBody(bodyTemplateJson);
		parsedWebHookDTO.setHeaders(ImmutableMap.of("Content-Type", "application/json"));
		parsedWebHookDTO.setUri(new URI(url));

		ArgumentCaptor<WebHook> webhookArg = ArgumentCaptor.forClass(WebHook.class);
		ArgumentCaptor<ParsedWebHookDTO> webhookDtoArg = ArgumentCaptor.forClass(ParsedWebHookDTO.class);

		verify(integrationListenerServiceSpy).doRequest(webhookArg.capture(), webhookDtoArg.capture());
		assertEquals(bodyRenderedJson, webhookDtoArg.getValue().getBody());
	}

	@Test
	public void OnWorkBudgetIncreaseRequest_checkRenderedTemplate() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());

		String note = "Need more!";
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setFlatPrice(30.00);
		dto.setInitiatedByResource(true);
		dto.setBudgetIncrease(true);
		dto.setPriceNegotiation(true);
		dto.setNote(note);

		authenticationService.setCurrentUser(contractor);
		WorkNegotiationResponse response = workNegotiationService.createBudgetIncreaseNegotiation(work.getId(), dto);

		Map<String, String> bodyTemplate = ImmutableMap.<String, String>builder()
			.put("proposed_flat_price", "${proposed_flat_price}")
			.put("note", "${note}")
			.build();

		Map<String, String> bodyRendered = ImmutableMap.<String, String>builder()
			.put("proposed_flat_price", BigDecimal.valueOf(dto.getFlatPrice()).setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString())
			.put("note", note)
			.build();

		String bodyTemplateJson = new ObjectMapper().writeValueAsString(bodyTemplate);
		String bodyRenderedJson = new ObjectMapper().writeValueAsString(bodyRendered);

		WebHook webHook = createNewWebHook(
			IntegrationEventType.newInstance(IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST),
			bodyTemplateJson,
			WebHook.ContentType.JSON,
			url);

		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.NEGOTIATION_ID, response.getWorkNegotiationId(),
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.NOTIFY_MBO, Boolean.FALSE,
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onBudgetIncreaseRequested(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));

		ParsedWebHookDTO parsedWebHookDTO = new ParsedWebHookDTO();
		parsedWebHookDTO.setBody(bodyTemplateJson);
		parsedWebHookDTO.setHeaders(ImmutableMap.of("Content-Type", "application/json"));
		parsedWebHookDTO.setUri(new URI(url));

		ArgumentCaptor<WebHook> webhookArg = ArgumentCaptor.forClass(WebHook.class);
		ArgumentCaptor<ParsedWebHookDTO> webhookDtoArg = ArgumentCaptor.forClass(ParsedWebHookDTO.class);

		verify(integrationListenerServiceSpy).doRequest(webhookArg.capture(), webhookDtoArg.capture());
		assertEquals(bodyRenderedJson, webhookDtoArg.getValue().getBody());
	}

	@Test
	public void OnWorkExpense() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());

		Calendar reschedule = DateUtilities.newCalendar(2011, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("Need more!");
		dto.setInitiatedByResource(true);

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse response = workNegotiationService.createExpenseIncreaseNegotiation(work.getId(), dto);
		assertNotNull(response);
		WorkExpenseNegotiation negotiation = (WorkExpenseNegotiation) workNegotiationService.findById(response.getWorkNegotiationId());
		negotiation.setApprovedBy(employee);

		assertNotNull(negotiation);

		String body = "{\"note\" : \"${note}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.NEGOTIATION_ID, negotiation.getId(),
			IntegrationEvent.AMOUNT, BigDecimal.valueOf(5L),
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onExpenseReimbursementRequested(work.getId(), eventArgs));
		assertTrue(integrationListenerServiceSpy.onExpenseReimbursementAdded(work.getId(), eventArgs));
		assertTrue(integrationListenerServiceSpy.onExpenseReimbursementApproved(work.getId(), eventArgs));
		assertTrue(integrationListenerServiceSpy.onExpenseReimbursementDeclined(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy, times(4)).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}

	@Test
	public void OnWorkBonus() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());

		Calendar reschedule = DateUtilities.newCalendar(2011, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("Need more!");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse response = workNegotiationService.createBonusNegotiation(work.getId(), dto);
		assertNotNull(response);
		WorkBonusNegotiation negotiation = (WorkBonusNegotiation) workNegotiationService.findById(response.getWorkNegotiationId());
		negotiation.setApprovedBy(employee);

		assertNotNull(negotiation);
		String body = "{\"note\" : \"${note}\"}";

		WebHook webHook = createNewWebHook(body, WebHook.ContentType.JSON, url);
		Map<String, Object> eventArgs = CollectionUtilities.newObjectMap(
			IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
			IntegrationEvent.NEGOTIATION_ID, negotiation.getId(),
			IntegrationEvent.AMOUNT, BigDecimal.valueOf(5L),
			IntegrationEvent.WEBHOOK_ID, webHook.getId());

		assertTrue(integrationListenerServiceSpy.onBonusRequested(work.getId(), eventArgs));
		assertTrue(integrationListenerServiceSpy.onBonusAdded(work.getId(), eventArgs));
		assertTrue(integrationListenerServiceSpy.onBonusApproved(work.getId(), eventArgs));
		assertTrue(integrationListenerServiceSpy.onBonusDeclined(work.getId(), eventArgs));
		verify(integrationListenerServiceSpy, times(4)).runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
	}
}
