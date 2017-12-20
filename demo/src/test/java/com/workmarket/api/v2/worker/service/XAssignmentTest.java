package com.workmarket.api.v2.worker.service;

import com.google.api.client.util.Sets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.worker.model.AbandonAssignmentDTO;
import com.workmarket.api.v2.worker.model.AddDeliverableDTO;
import com.workmarket.api.v2.worker.model.AddLabelDTO;
import com.workmarket.api.v2.worker.model.CheckInDTO;
import com.workmarket.api.v2.worker.model.CheckOutDTO;
import com.workmarket.api.v2.worker.model.CompleteDTO;
import com.workmarket.api.v2.worker.model.RescheduleDTO;
import com.workmarket.api.v2.worker.model.SaveCustomFieldsDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.AssetService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.core.Status;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.SerializationUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.CustomFieldGroupSaveRequestValidator;
import com.workmarket.web.validators.DateRangeValidator;
import com.workmarket.web.validators.DeliverableValidator;
import com.workmarket.web.validators.FilenameValidator;
import groovy.lang.Tuple2;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XAssignmentTest {

  enum WorkNumberIDPairs {
    SUCCESS("987654321", 88888888L),
    ALREADY_ACCEPTED("123456789", 22222222L),
    OWNER_OF_WORK("8484848484", 11111111L),
    NON_COMPLIANT_WORK("66666666", 99999999L),
    UNABLE_TO_CONFIRM("12344321", 90909090L),
    UNABLE_TO_ABANDON("12133678", 99090909L),BAD_WORK_STATUS("77777777", 55555555L);

		private String number;
		private long id;

		WorkNumberIDPairs(String number, long id) {
			this.number = number;
			this.id = id;
		}
	}

	private static final long USER_ID = 1234L;
	private AcceptWorkResponse acceptResponse;

	@Rule public ExpectedException thrown = ExpectedException.none();

	@Mock private XWork xWork;
	@Mock private MessageBundleHelper messageHelper;
	@Mock private WorkSubStatusService workSubStatusService;
	@Mock private DateRangeValidator dateRangeValidator;
	@Mock private WorkNegotiationService workNegotiationService;
	@Mock private WorkService workService;
	@Mock private ComplianceService complianceService;
	@Mock private TWorkFacadeService workFacadeService;
	@Mock private ProfileService profileService;
	@Mock private PricingService pricingService;
	@Mock private WorkSearchService workSearchService;
	@Mock private AssetService assetService;
	@Mock private AssetManagementService assetManagementService;
	@Mock private DeliverableService deliverableService;
	@Spy private FilenameValidator filenameValidator;
	@Spy private CustomFieldGroupSaveRequestValidator customFieldGroupSaveRequestValidator;
	@Mock private DeliverableValidator deliverableValidator;
	@Mock private CustomFieldService customFieldService;
	@Mock Doorman doorman;
	@InjectMocks private XAssignment xAssignment;


	ArgumentCaptor<TimeTrackingRequest> timeTrackingRequest = ArgumentCaptor.forClass(TimeTrackingRequest.class);

	@Before
	public void setUp() throws Exception {
		setupWorkMocks();
		setupComplianceMocks();
		setupMessageHelperMocks();
		filenameValidator = new FilenameValidator();
		customFieldGroupSaveRequestValidator = new CustomFieldGroupSaveRequestValidator();
	}

	private void setupMessageHelperMocks() {
		when(messageHelper.newBundle()).thenReturn(new MessageBundle());
	}


	@Test
	public void addLabelShouldRescheduleIfScheduleDefined() {
		final Long labelId = 1L;
		final WorkResponse workResponse = mock(WorkResponse.class);
		final Work work = mock(Work.class);
		final WorkSubStatusType workSubStatusType = new WorkSubStatusType();
		workSubStatusType.setId(labelId);
		workSubStatusType.setScheduleRequired(true);
		when(workResponse.getWork()).thenReturn(work);
		when(work.getTimeZone()).thenReturn("UTC");
		when(xWork.getWork(any(ExtendedUserDetails.class), any(String.class), any(Set.class), any(Set.class)))
			.thenReturn(workResponse);
		when(workSubStatusService.findAllEditableSubStatusesByWork(any(Long.class), any(WorkSubStatusTypeFilter.class)))
			.thenReturn(ImmutableList.of(workSubStatusType));


		final RescheduleDTO rescheduleDTO = new RescheduleDTO.Builder()
			.withStart(1475332200000L)
			.build(); // 10/01/2016, 10:30:00 AM EDT; 10/01/2016 14:30:00 GMT
		final AddLabelDTO dto = new AddLabelDTO.Builder()
			.withSchedule(rescheduleDTO)
			.build();
		xAssignment.addLabel(null, "work-number", labelId, dto, new MapBindingResult(ImmutableMap.of(), "objectName"));

		verify(dateRangeValidator).validate(any(Object.class), any(Errors.class));
		verify(workSubStatusService).addSubStatus(any(List.class), any(Long.class), any(String.class), any(DateRange
			.class));
	}


	@Test
	public void rescheduleShouldCallValidatorAndDelegateRescheduling() {
		final TimeZone timeZone = new TimeZone();
		timeZone.setTimeZoneId("US/Eastern");
		final AbstractWork work = mock(AbstractWork.class);
		when(work.getTimeZone()).thenReturn(timeZone);
		when(workService.findWorkByWorkNumber(anyString())).thenReturn(work);
		final RescheduleDTO rescheduleDTO = new RescheduleDTO.Builder()
			.withStart(1475332200000L)
			.build();
		when(workNegotiationService.reschedule(any(Long.class), any(DateRange.class), any(String.class)))
			.thenReturn(new Tuple2(null, "success"));

		xAssignment.reschedule("some-work-number", rescheduleDTO);

		verify(dateRangeValidator).validate(any(Object.class), any(Errors.class));
		verify(workNegotiationService).reschedule(any(Long.class), any(DateRange.class), any(String.class));
	}

	@Test
	public void shouldReturnFirstAvailableDeliverableSlot() {
		final DeliverableRequirement deliverableRequirement = mock(DeliverableRequirement.class);
		when(deliverableRequirement.getNumberOfFiles())
			.thenReturn(3);

		when(deliverableService.findDeliverableRequirementById(anyLong()))
			.thenReturn(deliverableRequirement);

		when(assetService.findDeliverableAssetPositionsByDeliverableRequirementId(anyLong()))
			.thenReturn(ImmutableList.of(0, 2));
		int result = xAssignment.getNextAvailablePosition(1L);
		assertEquals(1, result);

		when(assetService.findDeliverableAssetPositionsByDeliverableRequirementId(anyLong()))
			.thenReturn(ImmutableList.of(0, 1, 2));
		result = xAssignment.getNextAvailablePosition(1L);
		assertEquals(3, result);

		when(assetService.findDeliverableAssetPositionsByDeliverableRequirementId(anyLong()))
			.thenReturn(ImmutableList.of(1, 2));
		result = xAssignment.getNextAvailablePosition(1L);
		assertEquals(0, result);

		when(assetService.findDeliverableAssetPositionsByDeliverableRequirementId(anyLong()))
			.thenReturn(ImmutableList.of(0, 1));
		result = xAssignment.getNextAvailablePosition(1L);
		assertEquals(2, result);

		when(assetService.findDeliverableAssetPositionsByDeliverableRequirementId(anyLong()))
			.thenReturn(ImmutableList.<Integer>of());
		result = xAssignment.getNextAvailablePosition(1L);
		assertEquals(0, result);
	}

	//ACCEPT TESTS

	@Test
	public void testAcceptSuccess() throws Exception {
		setupSuccessfulAcceptResponse();
		AcceptWorkResponse response = xAssignment.accept(USER_ID, WorkNumberIDPairs.SUCCESS.number);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void testAcceptFailsWhenWorkAlreadyAccepted() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("assignment.accept.closed");
		setupFailureAcceptForWorkAlreadyAccepted();
		xAssignment.accept(USER_ID, WorkNumberIDPairs.ALREADY_ACCEPTED.number);
	}

	@Test
	public void testAcceptFailsWhenWorkAlreadyOwned() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("assignment.accept.own");
		setupFailureAcceptForWorkWhereUserIsOwner();
		xAssignment.accept(USER_ID, WorkNumberIDPairs.OWNER_OF_WORK.number);
	}

	@Test
	public void testAcceptFailsWhenUserNotCompliant() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("assignment.compliance.user_accept_not_allowed");
		setupFailureAcceptForComplianceIssue();
		xAssignment.accept(USER_ID, WorkNumberIDPairs.NON_COMPLIANT_WORK.number);
	}

	@Test
	public void testAcceptFailureFromService() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("Failed to accept work");
		setupFailureAcceptResponse();
		xAssignment.accept(USER_ID, WorkNumberIDPairs.SUCCESS.number);//This is the case where "SUCCESSFUL_WORK_NUMBER"
		// should work, but doesn't because of a failure in the workFacadeService
	}

	//DECLINE TESTS

	@Test
	public void testDeclineFailsWhenWorkAlreadyOwned() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("assignment.decline.not_owner");
		setupOwnerWorkMocks();
		xAssignment.decline(USER_ID, WorkNumberIDPairs.OWNER_OF_WORK.number);
	}

	@Test
	public void testDeclineFailsWhenWorkNotSent() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("assignment.decline.invalid_status");
		createBadWorkObjectForInvitedStatus(WorkNumberIDPairs.BAD_WORK_STATUS);
		xAssignment.decline(USER_ID, WorkNumberIDPairs.BAD_WORK_STATUS.number);
	}

	@Test
	public void testDeclineSuccess() throws Exception {
		boolean success = xAssignment.decline(USER_ID, WorkNumberIDPairs.SUCCESS.number);
		assertTrue(success);
	}

	//CONFIRM TESTS

	@Test
	public void testConfirmSuccess() throws Exception {
		List results = xAssignment.confirm(mock(ExtendedUserDetails.class), WorkNumberIDPairs.SUCCESS.number);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testConfirmNoActiveResource() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("assignment.confirmation.exception");
		createWorkObjectFor(WorkNumberIDPairs.UNABLE_TO_CONFIRM, false);
		xAssignment.confirm(mock(ExtendedUserDetails.class), WorkNumberIDPairs.UNABLE_TO_CONFIRM.number);
	}

	//Abandon Tests
  @Test
  public void testAbandonSuccess() throws Exception {
    AbandonAssignmentDTO abandonMessage = new AbandonAssignmentDTO.Builder()
        .withMessage("Gotta run")
        .build();
    List results = xAssignment.abandonAssignment(mock(ExtendedUserDetails.class), WorkNumberIDPairs.SUCCESS.number, abandonMessage);
    assertTrue(results.isEmpty());
  }

  @Test
  public void testAbandonNoActiveResource() throws Exception {
    AbandonAssignmentDTO abandonMessage = new AbandonAssignmentDTO.Builder()
        .withMessage("Gotta run")
        .build();
    thrown.expect(ApiException.class);
    thrown.expectMessage("assignment.abandon_work.mobilenotauthorized");
    createWorkObjectFor(WorkNumberIDPairs.UNABLE_TO_ABANDON, false);
    xAssignment.abandonAssignment(mock(ExtendedUserDetails.class), WorkNumberIDPairs.UNABLE_TO_ABANDON.number, abandonMessage);
  }

  //Checkin Tests

	@Test
	public void testNewCheckinSuccess() throws Exception {
		CheckInDTO checkinData = new CheckInDTO.Builder()
			.withLongitude(44.56)
			.withLatitude(-72.56)
			.build();
		setupSuccessfulTimeTrackingResponse(true);
		TimeTrackingResponse response = xAssignment.checkIn(USER_ID, WorkNumberIDPairs.SUCCESS.number, null, checkinData);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void testNewCheckinSuccessWithoutCoordinates() throws Exception {
		setupSuccessfulTimeTrackingResponse(true);
		TimeTrackingResponse response = xAssignment.checkIn(USER_ID, WorkNumberIDPairs.SUCCESS.number, null, null);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void testUpdateCheckinSuccess() throws Exception {
		CheckInDTO checkinData = new CheckInDTO.Builder()
			.withLongitude(44.56)
			.withLatitude(-72.56)
			.build();

		setupSuccessfulTimeTrackingResponse(true);
		TimeTrackingResponse response = xAssignment.checkIn(USER_ID, WorkNumberIDPairs.SUCCESS.number, 12345L, checkinData);
		assertTrue(response.isSuccessful());
		TimeTrackingRequest request = timeTrackingRequest.getValue();
		assertTrue(request.getLongitude().equals(44.56));
		assertTrue(request.getLatitude().equals(-72.56));
		assertNotNull(request.getDate());
		assertEquals(request.getWorkId(), WorkNumberIDPairs.SUCCESS.id);
		assertEquals((long) request.getTimeTrackingId(), 12345L);
	}

	@Test
	public void testCheckinFailure() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("generic.error");
		setupFailureTimeTrackingResponse(true);
		xAssignment.checkIn(USER_ID, WorkNumberIDPairs.SUCCESS.number, 12345L, null);
	}

	//Checkout Tests

	@Test
	public void testNewCheckoutSuccess() throws Exception {
		CheckOutDTO checkoutData = new CheckOutDTO.Builder()
			.withLongitude(44.56)
			.withLatitude(-72.56)
			.build();
		setupSuccessfulTimeTrackingResponse(false);
		TimeTrackingResponse response = xAssignment.checkOut(USER_ID, WorkNumberIDPairs.SUCCESS.number, null, checkoutData);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void testNewCheckoutSuccessWithoutCoordinates() throws Exception {
		setupSuccessfulTimeTrackingResponse(false);
		TimeTrackingResponse response = xAssignment.checkOut(USER_ID, WorkNumberIDPairs.SUCCESS.number, null, null);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void testUpdateCheckoutSuccess() throws Exception {
		CheckOutDTO checkout = new CheckOutDTO.Builder()
			.withLongitude(44.56)
			.withLatitude(-72.56)
			.build();
		setupSuccessfulTimeTrackingResponse(false);
		TimeTrackingResponse response = xAssignment.checkOut(USER_ID, WorkNumberIDPairs.SUCCESS.number, 12345L, checkout);
		assertTrue(response.isSuccessful());
		TimeTrackingRequest request = timeTrackingRequest.getValue();
		assertTrue(request.getLongitude().equals(44.56));
		assertTrue(request.getLatitude().equals(-72.56));
		assertNotNull(request.getDate());
		assertEquals(request.getWorkId(), WorkNumberIDPairs.SUCCESS.id);
		assertEquals((long) request.getTimeTrackingId(), 12345L);
	}

	@Test
	public void testCheckoutFailure() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("generic.error");
		setupFailureTimeTrackingResponse(false);
		xAssignment.checkOut(USER_ID, WorkNumberIDPairs.SUCCESS.number, 12345L, null);
	}

	//Complete Tests

	@Test
	public void testCompleteSuccessPerHour() throws Exception {
		setupPricingAndWorkObjectForPerHour();

		CompleteDTO completeDTO = new CompleteDTO.Builder()
			.withOverrideMinutesWorked(123)
			.build();

		ExtendedUserDetails extendedUserDetails = createUserForComplete();
		List results = xAssignment.complete(extendedUserDetails, WorkNumberIDPairs.SUCCESS.number, completeDTO, null, null);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testCompleteFailurePerHourNoMinutes() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("overtime_minutes_worked_required");
		setupPricingAndWorkObjectForPerHour();

		CompleteDTO completeDTO = new CompleteDTO.Builder().build();

		ExtendedUserDetails extendedUserDetails = createUserForComplete();
		xAssignment.complete(extendedUserDetails, WorkNumberIDPairs.SUCCESS.number, completeDTO, null, null);
	}

	@Test
	public void testCompleteFailurePerHourZeroMinutes() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("assignment.complete.notime");
		setupPricingAndWorkObjectForPerHour();
		ExtendedUserDetails extendedUserDetails = createUserForComplete();

		CompleteDTO completeDTO = new CompleteDTO.Builder()
			.withOverrideMinutesWorked(0)
			.build();

		xAssignment.complete(extendedUserDetails, WorkNumberIDPairs.SUCCESS.number, completeDTO, null, null);
	}

	@Test
	public void testCompleteSuccessPerUnit() throws Exception {
		setupPricingAndWorkObjectForPerUnit();

		CompleteDTO completeDTO = new CompleteDTO.Builder()
			.withUnits(0)
			.build();//Zero units 'currently' is a valid input. Might not want it to be this case in the future
		ExtendedUserDetails extendedUserDetails = createUserForComplete();
		List results = xAssignment.complete(extendedUserDetails, WorkNumberIDPairs.SUCCESS.number, completeDTO, null, null);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testCompleteFailurePerUnit() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("units_processed_required");
		setupPricingAndWorkObjectForPerUnit();

		CompleteDTO completeDTO = new CompleteDTO.Builder()
			.withUnits(null)
			.build();
		ExtendedUserDetails extendedUserDetails = createUserForComplete();
		xAssignment.complete(extendedUserDetails, WorkNumberIDPairs.SUCCESS.number, completeDTO, null, null);
	}

	@Test
	public void testCompleteSuccessFlatRate() throws Exception {
		setupPricingAndWorkObjectForFlatRate();
		CompleteDTO completeDTO = new CompleteDTO.Builder()
			.withOverridePrice(22.22)
			.build();
		ExtendedUserDetails extendedUserDetails = createUserForComplete();
		List results = xAssignment.complete(extendedUserDetails, WorkNumberIDPairs.SUCCESS.number, completeDTO, null, null);
		assertTrue(results.isEmpty());
	}


	//DELIVERABLES TESTS

	@Test
	public void testAddDeliverableSuccessWithPosition() throws Exception {
		AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
			.withName("TestDeliverable")
			.withData("<somebinary>ads895h2lnahjsdf788923")
			.withDescription("TestDeliverableDesription")
			.withPosition(0)
			.build();
		setupDeliverableServices(WorkNumberIDPairs.SUCCESS, 5678L);
		List results = xAssignment.addDeliverable(mock(ExtendedUserDetails.class), WorkNumberIDPairs.SUCCESS.number,
			1234L, 5678L, addDeliverableDTO);
		assertTrue(!results.isEmpty());
	}


	@Test
	public void testAddDeliverableSuccessWithOutPosition() throws Exception {
		AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
			.withName("TestDeliverable")
			.withData("<somebinary>ads895h2lnahjsdf788923")
			.withDescription("TestDeliverableDesription")
			.build();
		setupDeliverableServices(WorkNumberIDPairs.SUCCESS, 5678L);
		List results = xAssignment.addDeliverable(mock(ExtendedUserDetails.class), WorkNumberIDPairs.SUCCESS.number,
			1234L, 5678L, addDeliverableDTO);
		assertTrue(!results.isEmpty());
	}

	@Test
	public void testAddDeliverableFailureFileNameValidation() throws Exception {
		thrown.expect(BindException.class);
		thrown.expectMessage("filename.invalid_chars");
		AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
			.withName("badName?<>")
			.withData("<somebinary>ads895h2lnahjsdf788923")
			.withDescription("TestDeliverableDesription")
			.build();
		xAssignment.addDeliverable(mock(ExtendedUserDetails.class), WorkNumberIDPairs.SUCCESS.number, 1234L, 5678L,
			addDeliverableDTO);
	}

	@Test
	public void testAddDeliverableFailureSizeLimit() throws Exception {
		thrown.expect(ApiException.class);
		thrown.expectMessage("assignment.add_attachment.exception");
		AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
			.withName("TestDeliverable")
			.withData(createLongDataString())
			.withDescription("TestDeliverableDesription")
			.build();
		xAssignment.addDeliverable(mock(ExtendedUserDetails.class), WorkNumberIDPairs.SUCCESS.number, 1234L, 5678L,
			addDeliverableDTO);
	}

	@Test
	public void testDeleteDeliverableSuccess() throws Exception {
		AddDeliverableDTO addDeliverableDTO = new AddDeliverableDTO.Builder()
			.withName("TestDeliverable")
			.withData("<somebinary>ads895h2lnahjsdf788923")
			.withDescription("TestDeliverableDesription")
			.build();
		List results = xAssignment.deleteDeliverable(mock(ExtendedUserDetails.class), WorkNumberIDPairs.SUCCESS.number,
			1234L);
		assertTrue(results.isEmpty());
	}

	//Custom Fields

	@Test
	public void testSaveCustomFieldsSuccess() throws Exception {

		List<CustomFieldGroupDTO> customFieldGroupDTOs = new ArrayList<>();
		Set<CustomFieldDTO.Builder> customFields = createAndPopulateFieldList();
		CustomFieldGroupDTO.Builder setDTO = new CustomFieldGroupDTO.Builder();
		setDTO.setId(8888L);
		setDTO.setFields(customFields);
		customFieldGroupDTOs.add(setDTO.build());

		SaveCustomFieldsDTO customFieldsToSave = new SaveCustomFieldsDTO.Builder()
			.withCustomFields(customFieldGroupDTOs)
			.build();

		final AbstractWork work = mock(AbstractWork.class);
		when(work.isFinished()).thenReturn(false);
		when(work.isSent()).thenReturn(true);
		when(workService.findWork(anyLong())).thenReturn(work);

		WorkCustomFieldGroup fieldGroup = mock(WorkCustomFieldGroup.class);
		when(fieldGroup.getActiveWorkCustomFields()).thenReturn(Collections.<WorkCustomField>emptyList());
		when(customFieldService.findWorkCustomFieldGroup(anyLong())).thenReturn(fieldGroup);

		xAssignment.saveCustomFields(mock(ExtendedUserDetails.class), WorkNumberIDPairs.SUCCESS.number,
			customFieldsToSave, false, mock(BindingResult.class));

		//TODO: This should probably be updated to test things more specifically
		verify(customFieldService).addWorkCustomFieldGroupToWork(anyLong(), anyLong(), anyInt());
		verify(customFieldService).saveWorkCustomFieldsForWorkAndIndex(any(WorkCustomFieldDTO[].class), anyLong());
	}


    /*

    Helper Methods

     */

	private Set<CustomFieldDTO.Builder> createAndPopulateFieldList() {
		Set<CustomFieldDTO.Builder> customFields = Sets.newHashSet();
		CustomFieldDTO.Builder customField = new CustomFieldDTO.Builder();
		customField.setId(1234L);
		customField.setValue("TestValue");
		customFields.add(customField);

		customField = new CustomFieldDTO.Builder();
		customField.setId(5678L);
		customField.setValue("TestValue2");
		customFields.add(customField);
		return customFields;
	}

	private String createLongDataString() {
		byte[] byteArray = RandomUtils.nextBytes(55 * 1024 * 1024);
		return SerializationUtilities.encodeBase64(byteArray);
	}

	private void setupDeliverableServices(WorkNumberIDPairs pair, long deliverableId) throws Exception {
		when(assetManagementService.findAssetAssociationsByWorkAndAsset(pair.id, deliverableId)).thenReturn(mock
			(WorkAssetAssociation.class));
		WorkAssetAssociation storedAsset = mock(WorkAssetAssociation.class);
		when(storedAsset.getAsset()).thenReturn(new Asset());
		when(deliverableService.addDeliverable(eq(pair.number), any(InputStream.class), any(AssetDTO.class))).thenReturn
			(storedAsset);
	}


	private void setupPricingAndWorkObjectForFlatRate() {
		com.workmarket.thrift.work.PricingStrategy pricingStrategy = new com.workmarket.thrift.work.PricingStrategy();
		pricingStrategy.setId(222L);
		when(pricingService.findPricingStrategyById(222L)).thenReturn(mock(FlatPricePricingStrategy.class));
		createWorkObjectFor(WorkNumberIDPairs.SUCCESS, pricingStrategy);
	}

	private void setupPricingAndWorkObjectForPerUnit() {
		com.workmarket.thrift.work.PricingStrategy pricingStrategy = new com.workmarket.thrift.work.PricingStrategy();
		pricingStrategy.setId(321L);
		when(pricingService.findPricingStrategyById(321L)).thenReturn(mock(PerUnitPricingStrategy.class));
		createWorkObjectFor(WorkNumberIDPairs.SUCCESS, pricingStrategy);
	}

	private ExtendedUserDetails createUserForComplete() {
		ExtendedUserDetails extendedUserDetails = mock(ExtendedUserDetails.class);
		when(extendedUserDetails.getCompanyId()).thenReturn(1234L);
		when(profileService.findCompanyById(1234L)).thenReturn(mock(Company.class));
		return extendedUserDetails;
	}


	private void setupPricingAndWorkObjectForPerHour() {
		com.workmarket.thrift.work.PricingStrategy pricingStrategy = new com.workmarket.thrift.work.PricingStrategy();
		pricingStrategy.setId(4321L);
		when(pricingService.findPricingStrategyById(4321L)).thenReturn(mock(PerHourPricingStrategy.class));
		createWorkObjectFor(WorkNumberIDPairs.SUCCESS, pricingStrategy);
	}

	private void setupSuccessfulTimeTrackingResponse(boolean isCheckin) {
		TimeTrackingResponse response = new TimeTrackingResponse();
		response.setSuccessful(true);
		if (isCheckin) {
			when(workFacadeService.checkInActiveResource(timeTrackingRequest.capture())).thenReturn(response);
		} else {
			when(workFacadeService.checkOutActiveResource(timeTrackingRequest.capture())).thenReturn(response);
		}
	}

	private void setupFailureTimeTrackingResponse(boolean isCheckin) {
		TimeTrackingResponse response = new TimeTrackingResponse();
		response.setSuccessful(false);
		if (isCheckin) {
			when(workFacadeService.checkInActiveResource(any(TimeTrackingRequest.class))).thenReturn(response);
		} else {
			when(workFacadeService.checkOutActiveResource(any(TimeTrackingRequest.class))).thenReturn(response);
		}
	}

	private void setupSuccessfulAcceptResponse() {
		com.workmarket.domains.work.model.Work work = mock(com.workmarket.domains.work.model.Work.class);
		when(work.getWorkNumber()).thenReturn(WorkNumberIDPairs.SUCCESS.number);
		acceptResponse.setWork(work);
		when(acceptResponse.isSuccessful()).thenReturn(true);
		when(workFacadeService.acceptWork(USER_ID, WorkNumberIDPairs.SUCCESS.id)).thenReturn(acceptResponse);
	}

	private void setupFailureAcceptResponse() {
		com.workmarket.domains.work.model.Work work = mock(com.workmarket.domains.work.model.Work.class);
		when(work.getWorkNumber()).thenReturn(WorkNumberIDPairs.SUCCESS.number);
		acceptResponse.setWork(work);
		when(acceptResponse.isSuccessful()).thenReturn(false);
		when(workFacadeService.acceptWork(USER_ID, WorkNumberIDPairs.SUCCESS.id)).thenReturn(acceptResponse);
	}

	private void setupFailureAcceptForWorkAlreadyAccepted() {
		com.workmarket.domains.work.model.Work work = mock(com.workmarket.domains.work.model.Work.class);
		when(work.getWorkNumber()).thenReturn(WorkNumberIDPairs.ALREADY_ACCEPTED.number);
		acceptResponse.setWork(work);
		when(acceptResponse.isSuccessful()).thenReturn(false);
		when(workFacadeService.acceptWork(USER_ID, WorkNumberIDPairs.ALREADY_ACCEPTED.id)).thenReturn(acceptResponse);
	}

	private void setupFailureAcceptForWorkWhereUserIsOwner() {
		com.workmarket.domains.work.model.Work work = setupOwnerWorkMocks();
		acceptResponse.setWork(work);
		when(acceptResponse.isSuccessful()).thenReturn(false);
		when(workFacadeService.acceptWork(USER_ID, WorkNumberIDPairs.OWNER_OF_WORK.id)).thenReturn(acceptResponse);
	}

	private com.workmarket.domains.work.model.Work setupOwnerWorkMocks() {
		com.workmarket.domains.work.model.Work work = mock(com.workmarket.domains.work.model.Work.class);
		when(work.getWorkNumber()).thenReturn(WorkNumberIDPairs.OWNER_OF_WORK.number);
		List<WorkContext> workContexts = new ArrayList<>();
		workContexts.add(WorkContext.OWNER);
		when(workService.getWorkContext(WorkNumberIDPairs.OWNER_OF_WORK.id, USER_ID)).thenReturn(workContexts);
		when(workService.isUserWorkResourceForWork(WorkNumberIDPairs.OWNER_OF_WORK.id, USER_ID)).thenReturn(false);
		return work;
	}

	private void setupFailureAcceptForComplianceIssue() {
		com.workmarket.domains.work.model.Work work = mock(com.workmarket.domains.work.model.Work.class);
		when(work.getWorkNumber()).thenReturn(WorkNumberIDPairs.NON_COMPLIANT_WORK.number);
		acceptResponse.setWork(work);
		when(acceptResponse.isSuccessful()).thenReturn(false);
		when(workFacadeService.acceptWork(USER_ID, WorkNumberIDPairs.NON_COMPLIANT_WORK.id)).thenReturn(acceptResponse);
	}


	private void setupComplianceMocks() {
		Compliance isCompliant = mock(Compliance.class);
		when(isCompliant.isCompliant()).thenReturn(true);

		Compliance isNotCompliant = mock(Compliance.class);
		when(isNotCompliant.isCompliant()).thenReturn(false);

		when(complianceService.getComplianceFor(USER_ID, WorkNumberIDPairs.SUCCESS.id)).thenReturn(isCompliant);
		when(complianceService.getComplianceFor(USER_ID, WorkNumberIDPairs.ALREADY_ACCEPTED.id)).thenReturn(isCompliant);
		when(complianceService.getComplianceFor(USER_ID, WorkNumberIDPairs.OWNER_OF_WORK.id)).thenReturn(isCompliant);
		when(complianceService.getComplianceFor(USER_ID, WorkNumberIDPairs.NON_COMPLIANT_WORK.id)).thenReturn
			(isNotCompliant);
	}

	private void setupWorkMocks() {
		acceptResponse = mock(AcceptWorkResponse.class);
		WorkResource workResource = mock(WorkResource.class);
		when(workResource.getId()).thenReturn(USER_ID);
		acceptResponse.setActiveResource(workResource);

		createWorkObjectFor(WorkNumberIDPairs.SUCCESS);
		createWorkObjectFor(WorkNumberIDPairs.ALREADY_ACCEPTED);
		createWorkObjectFor(WorkNumberIDPairs.OWNER_OF_WORK);
		createWorkObjectFor(WorkNumberIDPairs.NON_COMPLIANT_WORK);

		WorkResourcePagination workResources = mock(WorkResourcePagination.class);
		List<WorkResource> emptyList = new ArrayList<>();
		when(workResources.getResults()).thenReturn(emptyList);
		when(workService.findWorkResources(eq(WorkNumberIDPairs.SUCCESS.id), any(WorkResourcePagination.class)))
			.thenReturn(workResources);
		when(workService.findWorkResources(eq(WorkNumberIDPairs.OWNER_OF_WORK.id), any(WorkResourcePagination.class)))
			.thenReturn(workResources);
		when(workService.findWorkResources(eq(WorkNumberIDPairs.NON_COMPLIANT_WORK.id), any(WorkResourcePagination.class)
		)).thenReturn(workResources);
		setupWorkResourcesForAcceptedWork();
	}

	private void setupWorkResourcesForAcceptedWork() {
		WorkResourcePagination acceptedWorkResources = mock(WorkResourcePagination.class);
		List<WorkResource> alreadyAcceptedAssignment = new ArrayList<>();
		alreadyAcceptedAssignment.add(mock(WorkResource.class));
		when(acceptedWorkResources.getResults()).thenReturn(alreadyAcceptedAssignment);
		when(workService.findWorkResources(eq(WorkNumberIDPairs.ALREADY_ACCEPTED.id), any(WorkResourcePagination.class)))
			.thenReturn(acceptedWorkResources);
	}

	private void createWorkObjectFor(WorkNumberIDPairs pair) {
		createWorkObjectFor(pair, true, null);
	}

	private void createWorkObjectFor(WorkNumberIDPairs pair, com.workmarket.thrift.work.PricingStrategy pricingStrategy) {
		createWorkObjectFor(pair, true, pricingStrategy);
	}

	private void createWorkObjectFor(WorkNumberIDPairs pair, boolean withActiveResource) {
		createWorkObjectFor(pair, withActiveResource, null);
	}

	private void createWorkObjectFor(WorkNumberIDPairs pair, boolean withActiveResource, com.workmarket.thrift.work
		.PricingStrategy pricingStrategy) {
		AbstractWork work = mock(AbstractWork.class);
		when(work.getId()).thenReturn(pair.id);
		when(work.isSent()).thenReturn(true);
		when(xWork.getWorkByNumber(pair.number)).thenReturn(work);
		when(xWork.getWorkId(pair.number)).thenReturn(pair.id);
		Work thriftWork = mock(Work.class);
		when(thriftWork.getId()).thenReturn(pair.id);

		Status workStatus = new Status();
		workStatus.setCode(WorkStatusType.ACTIVE);//Need this for complete
		when(thriftWork.getStatus()).thenReturn(workStatus);
		if (pricingStrategy != null) {
			when(thriftWork.getPricing()).thenReturn(pricingStrategy);
		}
		if (withActiveResource) { //Need this for checkins/outs
			Resource resource = mock(Resource.class);
			com.workmarket.thrift.core.User user = mock(com.workmarket.thrift.core.User.class);
			when(user.getId()).thenReturn(pair.id);
			when(resource.getUser()).thenReturn(user);
			when(thriftWork.getActiveResource()).thenReturn(resource);
		}

		WorkResponse workResponse = mock(WorkResponse.class);
		when(workResponse.getWork()).thenReturn(thriftWork);
		when(xWork.getWork(any(ExtendedUserDetails.class),
			eq(pair.number),
			any(ImmutableSet.class),
			any(ImmutableSet.class))).thenReturn(workResponse);

	}

	private void createBadWorkObjectForInvitedStatus(WorkNumberIDPairs pair) {
		AbstractWork work = mock(AbstractWork.class);
		when(work.getId()).thenReturn(pair.id);
		when(work.isSent()).thenReturn(false);
		when(xWork.getWorkByNumber(pair.number)).thenReturn(work);
	}
}