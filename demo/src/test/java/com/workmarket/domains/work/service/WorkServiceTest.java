package com.workmarket.domains.work.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.dao.assessment.WorkAssessmentAssociationDAO;
import com.workmarket.dao.note.NoteDAO;
import com.workmarket.dao.postalcode.PostalCodeDAO;
import com.workmarket.dao.random.WorkRandomIdentifierDAO;
import com.workmarket.dao.requirement.RequirementSetDAO;
import com.workmarket.dao.summary.user.UserSummaryDAO;
import com.workmarket.dao.summary.work.WorkStatusTransitionDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkPrice;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.changelog.work.WorkPropertyChangeLog;
import com.workmarket.domains.model.changelog.work.WorkStatusChangeChangeLog;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.google.CalendarSyncSettings;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkPriceDAO;
import com.workmarket.domains.work.dao.WorkQuestionAnswerPairDAO;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.dao.WorkResourceTimeTrackingDAO;
import com.workmarket.domains.work.dao.WorkTemplateDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.resource.WorkResourceChangeLogService;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.status.AcceptWorkStatus;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.infra.URIService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GoogleCalendarService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.option.WorkOptionsService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.utility.DateUtilities;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkServiceTest {

	@Mock ProfileService profileService;
	@Mock WorkSubStatusService workSubStatusService;
	@Mock PricingService pricingService;
	@Mock WorkReportService workReportService;
	@Mock WorkStatusService workStatusService;
	@Mock WorkNegotiationService workNegotiationService;
	@Mock WorkNoteService workNoteService;
	@Mock WorkValidationService workValidationService;
	@Mock WorkRandomIdentifierDAO workNumberGenerator;
	@Mock URIService uriService;
	@Mock UserService userService;
	@Mock UserNotificationService userNotificationService;
	@Mock UserGroupService userGroupService;
	@Mock WorkRoutingService workRoutingService;
	@Mock WorkResourceChangeLogService workResourceChangeLogService;
	@Mock BaseWorkDAO abstractWorkDAO;
	@Mock NoteDAO noteDAO;
	@Mock WorkDAO workDAO;
	@Mock WorkResourceDAO workResourceDAO;
	@Mock WorkTemplateDAO workTemplateDAO;
	@Mock WorkResourceTimeTrackingDAO workResourceTimeTrackingDAO;
	@Mock PostalCodeDAO postalCodeDAO;
	@Mock WorkAssessmentAssociationDAO workAssessmentAssociationDAO;
	@Mock AbstractAssessmentDAO assessmentDAO;
	@Mock WorkPriceDAO workPriceDAO;
	@Mock WorkMilestonesService workMilestonesService;
	@Mock SummaryService summaryService;
	@Mock WorkAuditService workAuditService;
	@Mock WorkStatusTransitionDAO workStatusTransitionDAO;
	@Mock RequirementSetDAO requirementSetDAO;
	@Mock WorkQuestionAnswerPairDAO workQuestionAnswerPairDAO;
	@Mock LaneService laneService;
	@Mock ServiceMessageHelper messageHelper;
	@Mock AuthenticationService authenticationService;
	@Mock WorkActionRequestFactory workActionRequestFactory;
	@Mock WebHookEventService webHookEventService;
	@Mock AssetManagementService assetManagementService;
	@Mock DeliverableService deliverableService;
	@Mock EventRouter eventRouter;
	@Mock WorkResourceDetailCache workResourceDetailCache;
	@Mock WorkResourceService workResourceService;
	@Mock ScorecardCache scorecardCache;
	@Mock WorkChangeLogService workChangeLogService;
	@Mock AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Mock UserSummaryDAO userSummaryDAO;
	@Mock InvariantDataService invariantDataService;
	@Mock BillingService billingService;
	@Mock GoogleCalendarService googleCalendarService;
	@Mock WorkOptionsService workOptionsService;
	@InjectMocks WorkServiceImpl workService = spy(new WorkServiceImpl());

	Work work = mock(Work.class);
	Work differentWork = mock(Work.class);
	Address address = mock(Address.class);
	WorkResource workResource;
	User worker = mock(User.class);
	User user = mock(User.class);
	Company company = mock(Company.class);
	Company company2;
	PricingStrategy pricingStrategy = mock(PricingStrategy.class);
	FullPricingStrategy fullPricingStrategy = mock(FullPricingStrategy.class);
	PerHourPricingStrategy perHourPricingStrategy = mock(PerHourPricingStrategy.class);
	WorkResourcePagination workResourcePagination = mock(WorkResourcePagination.class);
	UnassignDTO unassignDTO = mock(UnassignDTO.class);
	AcceptWorkOfferRequest acceptWorkOfferRequest;
	WorkActionRequest workActionRequest;
	WorkResourceTimeTracking workResourceTimeTracking = mock(WorkResourceTimeTracking.class);
	Calendar startTime = mock(Calendar.class);
	Calendar endTime = mock(Calendar.class);
	Calendar workDueDate = mock(Calendar.class);
	List<WorkResourceTimeTracking> workResourceTimeTrackings;
	ProfileDTO profileDTO;
	CancelWorkDTO cancelWorkDTO;
	WorkMilestones workMilestones;
	WorkStatusType workStatusType;
	WorkStatusType workStatusType2;
	Coordinate workerCoordinate;
	BigDecimal latitude, longitude;
	Calendar from, through;
	WorkSchedule workSchedule;
	DateRange dateRange;

	private static final Long
		COMPANY1_ID = 1L,
		COMPANY2_ID = 4L,
		WORK_ID = 3L,
		WORK_ID_2 = 99L,
		WORKER_ID = 1L,
		USER_ID = 2L,
		USER2_ID = 5L,
		HOUR_AND_A_HALF = TimeUnit.MINUTES.toMillis(90L),
		WORK_RESOURCE_ID = 6L;

	private static final String
		UNASSIGN_NOTE_FROM_WORKER = "You've been unassigned",
		WORK_NUMBER = "12345",
		WORKER_NUMBER = "9876",
		USER_NUMBER = "19191",
		POSTAL_CODE1 = "10011",
		POSTAL_CODE2 = "10015";
	@Before
	public void setUp() throws Exception {
		workerCoordinate = new Coordinate(-74.003101, 40.724325);
		latitude = new BigDecimal(40.740256);
		longitude = new BigDecimal(-73.992391);
		company2 = mock(Company.class);
		when(company2.getId()).thenReturn(COMPANY2_ID);

		workStatusType2 = mock(WorkStatusType.class);

		acceptWorkOfferRequest = mock(AcceptWorkOfferRequest.class);
		workActionRequest = mock(WorkActionRequest.class);
		when(workActionRequest.getWorkNumber()).thenReturn(WORK_NUMBER);
		when(workActionRequest.getResourceUserNumber()).thenReturn(WORKER_NUMBER);
		when(workActionRequest.getOnBehalfOfUserNumber()).thenReturn(USER_NUMBER);
		when(workActionRequest.getOnBehalfOfId()).thenReturn(WORKER_ID);
		when(acceptWorkOfferRequest.getWorkAction()).thenReturn(workActionRequest);

		when(user.getId()).thenReturn(USER_ID);
		when(user.getCompany()).thenReturn(company);
		when(worker.getCompany()).thenReturn(company);
		when(worker.getId()).thenReturn(WORKER_ID);
		when(company.getId()).thenReturn(COMPANY1_ID);

		when(work.getId()).thenReturn(WORK_ID);
		when(work.getAddress()).thenReturn(address);
		when(work.getCompany()).thenReturn(company);
		when(work.getBuyer()).thenReturn(user);
		when(work.isSent()).thenReturn(true);
		when(work.getDueDate()).thenReturn(workDueDate);
		when(work.getWorkStatusType()).thenReturn(workStatusType2);

		when(differentWork.getId()).thenReturn(WORK_ID_2);

		workResource = mock(WorkResource.class);
		when(workResource.getUser()).thenReturn(worker);
		when(workResource.getId()).thenReturn(WORK_RESOURCE_ID);
		when(laneService.isLane3Active(any(Long.class))).thenReturn(false);
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(LaneType.LANE_1);

		when(address.getPostalCode()).thenReturn(POSTAL_CODE2);
		when(address.getLatitude()).thenReturn(latitude);
		when(address.getLongitude()).thenReturn(longitude);
		PostalCode postalCode = new PostalCode();
		postalCode.setPostalCode(POSTAL_CODE1);
		when(profileService.findPostalCodeForUser(anyLong())).thenReturn(Optional.of(postalCode));

		profileDTO = mock(ProfileDTO.class);
		when(profileDTO.getPostalCode()).thenReturn(POSTAL_CODE1);
		when(profileService.findProfileDTO(anyLong())).thenReturn(profileDTO);
		when(profileDTO.getCoordinate()).thenReturn(workerCoordinate);

		when(workResourceDAO.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(fullPricingStrategy.getPricingStrategyType()).thenReturn(PricingStrategyType.FLAT);
		when(perHourPricingStrategy.getMaxNumberOfHours()).thenReturn(new BigDecimal(2.5));
		when(pricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
		when(work.getPricingStrategy()).thenReturn(pricingStrategy);
		when(work.getPricingStrategyType()).thenReturn(PricingStrategyType.FLAT);
		when(workResource.getWork()).thenReturn(work);
		when(abstractWorkDAO.get(WORK_ID)).thenReturn(work);
		when(abstractWorkDAO.findById(WORK_ID)).thenReturn(work);
		when(workDAO.get(WORK_ID)).thenReturn(work);
		when(workDAO.findWorkByWorkNumber(WORK_NUMBER)).thenReturn(work);
		when(workResourceDAO.findByWork(anyLong(), any(WorkResourcePagination.class))).thenReturn(workResourcePagination);
		when(messageHelper.getMessage(anyString())).thenReturn("Message");
		when(authenticationService.getCurrentUser()).thenReturn(user);
		when(authenticationService.getMasqueradeUserId()).thenReturn(USER2_ID);
		when(workActionRequestFactory.create(any(AbstractWork.class), anyLong(), anyLong(), anyLong(), any(WorkAuditType.class))).thenReturn(mock(WorkActionRequest.class));
		when(workValidationService.isWorkResourceValidForWork(anyLong(), anyLong(), anyLong())).thenReturn(true);
		when(userService.getUser(WORKER_ID)).thenReturn(worker);
		when(userService.findUserByUserNumber(USER_NUMBER)).thenReturn(user);
		when(userService.findUserByUserNumber(WORKER_NUMBER)).thenReturn(worker);
		when(unassignDTO.getWorkId()).thenReturn(WORK_ID);
		when(unassignDTO.getCancellationReasonTypeCode()).thenReturn("");
		when(unassignDTO.getNote()).thenReturn(UNASSIGN_NOTE_FROM_WORKER);

		doReturn(WORKER_ID).when(workService).findActiveWorkerId(anyLong());
		when(startTime.getTimeInMillis()).thenReturn(0L);
		when(endTime.getTimeInMillis()).thenReturn(HOUR_AND_A_HALF);
		when(workResourceTimeTracking.getCheckedInOn()).thenReturn(startTime);
		when(workResourceTimeTracking.getCheckedOutOn()).thenReturn(endTime);
		workResourceTimeTrackings = Lists.newArrayList(workResourceTimeTracking);
		doReturn(workResourceTimeTrackings).when(workService).findTimeTrackingByWorkResource(WORKER_ID);
		doReturn(work).when(workService).findWorkByWorkNumber(WORK_NUMBER);
		doReturn(workResource).when(workService).findWorkResource(WORKER_ID, WORK_ID);

		cancelWorkDTO = mock(CancelWorkDTO.class);
		workMilestones = mock(WorkMilestones.class);
		workStatusType = mock(WorkStatusType.class);

		when(workMilestonesService.findWorkMilestonesByWorkId(WORK_ID)).thenReturn(workMilestones);
		when(cancelWorkDTO.isPaid()).thenReturn(true);

		from = mock(Calendar.class);
		through = mock(Calendar.class);
		when(from.clone()).thenReturn(through);

		dateRange = mock(DateRange.class);
		when(dateRange.getFrom()).thenReturn(from);
		when(dateRange.isRange()).thenReturn(false);

		workSchedule = mock(WorkSchedule.class);
		when(workSchedule.getDateRange()).thenReturn(dateRange);
		when(workSchedule.getWorkId()).thenReturn(WORK_ID);
	}

	@Test
	public void calculateDistanceToWork() {
		Assert.assertEquals(1.24, workService.calculateDistanceToWork(1L, work), 0.0);
	}

	@Test
	public void findLiteResourceByWorkNumber() {
		String workNumber = "100001";
		workService.findLiteResourceByWorkNumber(workNumber);
		verify(workResourceDAO).findLiteResourceByWorkNumber(workNumber);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInActiveResource_withNullRequest_fails() {
		workService.checkInActiveResource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInActiveResource_withNullRequestDate_fails() {
		workService.checkInActiveResource(new TimeTrackingRequest());
	}

	@Test
	public void checkInActiveResource_withFutureDate_returnsFailedResponse() {
		Calendar future = Calendar.getInstance();
		future.add(Calendar.DAY_OF_MONTH, 5);
		TimeTrackingResponse response = workService.checkInActiveResource(new TimeTrackingRequest()
				.setWorkId(1L)
				.setDate(future));
		assertNotNull(response);
		assertFalse(response.isSuccessful());
	}

	@Test
	public void checkInActiveResource_success() {
		when(work.isCheckinRequired()).thenReturn(true);
		TimeTrackingResponse response = workService.checkInActiveResource(new TimeTrackingRequest()
				.setWorkId(1L)
				.setDate(Calendar.getInstance())
				.setDistance(10.0)
				.setLongitude(74.0)
				.setLongitude(34.0));
		assertNotNull(response);
		verify(workResourceTimeTrackingDAO).saveOrUpdate(any(WorkResourceTimeTracking.class));
		verify(webHookEventService).onCheckInActiveResource(anyLong(), anyLong(), anyLong());
		assertTrue(response.isSuccessful());
	}

	@Test
	public void checkInActiveResource_failWithDifferentWorkIdTimeTrackingId() {
		doReturn(Boolean.FALSE).when(workService).compareWorkById(any(Work.class), any(Work.class));

		WorkResourceTimeTracking workResourceTimeTracking = mock(WorkResourceTimeTracking.class);
		when(workResourceTimeTracking.getWorkResource()).thenReturn(workResource);
		when(workResourceTimeTrackingDAO.findLatestByWorkResource(anyLong())).thenReturn(workResourceTimeTracking);
		when(workResourceTimeTrackingDAO.findById(anyLong())).thenReturn(workResourceTimeTracking);
		when(workResource.getWork()).thenReturn(work);

		final TimeTrackingResponse response = workService.checkInActiveResource(new TimeTrackingRequest()
				.setWorkId(work.getId())
				.setTimeTrackingId(1234L)
				.setDate(Calendar.getInstance())
				.setDistance(10.0)
				.setLongitude(74.0)
				.setLatitude(34.0));

		assertFalse(response.getMessage().equals("assignment.update_checkin" +
				".check_in_out_id_and_assignment_id_refer_to_different_work"));
		assertTrue(!response.isSuccessful());
	}

	@Test
	public void checkInActiveResource_successWithSameWorkIdTimeTrackingId() {
		doReturn(Boolean.TRUE).when(workService).compareWorkById(any(Work.class), any(Work.class));

		WorkResourceTimeTracking workResourceTimeTracking = mock(WorkResourceTimeTracking.class);
		when(workResourceTimeTracking.getWorkResource()).thenReturn(workResource);
		when(workResourceTimeTrackingDAO.findLatestByWorkResource(anyLong())).thenReturn(workResourceTimeTracking);
		when(workResourceTimeTrackingDAO.findById(anyLong())).thenReturn(workResourceTimeTracking);
		when(workResource.getWork()).thenReturn(work);

		when(work.isCheckinRequired()).thenReturn(true);

		final TimeTrackingResponse response = workService.checkInActiveResource(new TimeTrackingRequest()
				.setWorkId(work.getId())
				.setTimeTrackingId(1234L)
				.setDate(Calendar.getInstance())
				.setDistance(10.0)
				.setLongitude(74.0)
				.setLatitude(34.0));

		assertNotNull(response);
		verify(workResourceTimeTrackingDAO).saveOrUpdate(any(WorkResourceTimeTracking.class));
		verify(webHookEventService).onCheckInActiveResource(anyLong(), anyLong(), anyLong());
		assertTrue(response.isSuccessful());
	}

	@Test
	public void compareWorkById_true() {
		final Work workReferredByTimeTrackingId = work;
		final Work workReferredByWorkId = work;
		assertTrue(workService.compareWorkById(workReferredByTimeTrackingId, workReferredByWorkId));
	}

	@Test
	public void compareWorkById_false() {
		final Work workReferredByTimeTrackingId = work;
		final Work workReferredByWorkId = differentWork;
		assertFalse(workService.compareWorkById(workReferredByTimeTrackingId, workReferredByWorkId));
	}

	@Test
	public void checkOutActiveResource_success() {
		Location location = mock(Location.class);
		Address address = mock(Address.class);
		when(work.isCheckinRequired()).thenReturn(true);
		WorkResourceTimeTracking workResourceTimeTracking = mock(WorkResourceTimeTracking.class);
		when(workResourceTimeTrackingDAO.findLatestByWorkResource(anyLong())).thenReturn(workResourceTimeTracking);
		when(workResource.getWork()).thenReturn(work);
		when(work.getLocation()).thenReturn(location);
		when(location.getAddress()).thenReturn(address);
		when(address.getLatitude()).thenReturn(BigDecimal.valueOf(74.0));
		when(address.getLongitude()).thenReturn(BigDecimal.valueOf(34.0));

		Calendar now = Calendar.getInstance();

		TimeTrackingResponse response = workService.checkOutActiveResource(new TimeTrackingRequest()
				.setWorkId(1L)
				.setDate(now)
				.setLatitude(73.0)
				.setLongitude(33.0));
		assertNotNull(response);
		assertTrue(response.isSuccessful());

		verify(workSubStatusService).addSystemSubstatusAndResolve(any(User.class), anyLong(), anyString(), any(String[].class));
		verify(workResourceTimeTracking).setCheckedOutOn(eq(DateUtilities.getCalendarInUTC(now)));
		verify(workResourceTimeTracking).setCheckedOutBy(any(User.class));
		verify(workResourceTimeTracking).setLatitudeOut(73.0);
		verify(workResourceTimeTracking).setLongitudeOut(eq(33.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkOutActiveResource_withNullRequest_fails() {
		workService.checkOutActiveResource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkOutActiveResource_withNullRequestDate_fails() {
		workService.checkOutActiveResource(new TimeTrackingRequest());
	}

	@Test
	public void countWorkByCompanyByStatus_callDAO() {
		List<String> status = WorkStatusType.OPEN_WORK_STATUS_TYPES;
		workService.countWorkByCompanyByStatus(COMPANY1_ID, status);
		verify(workDAO).countWorkByCompanyByStatus(COMPANY1_ID, status);
	}

	@Test(expected = IllegalArgumentException.class)
	public void unassignWork_nullWorkResource_illegalArgumentException() {
		when(workResourceDAO.findActiveWorkResource(anyLong())).thenReturn(null);

		workService.unassignWork(unassignDTO);
	}

	@Test
	public void unassignWork_workIdAndUnassignNote_allDeliverablesRemovedFromWork() {
		workService.unassignWork(unassignDTO);

		verify(deliverableService).removeAllDeliverablesFromWork(anyLong());
	}

	@Test
	public void unassignWork_workIdAndUnassignNote_unassignNotificationsSent() {
		workService.unassignWork(unassignDTO);

		verify(userNotificationService).onWorkUnassigned(any(WorkResource.class), eq(UNASSIGN_NOTE_FROM_WORKER));
	}

	@Test
	public void unassignInternalWork_reinvitationSent() {
		when(work.getPricingStrategyType()).thenReturn(PricingStrategyType.INTERNAL);
		workService.unassignWork(unassignDTO);

		verify(userNotificationService).onWorkReinvited(any(Work.class), any(List.class));
	}

	@Test
	public void unassignWork_resourceConfirmationRequired_workAndWorkResourceConfirmationReset() {
		when(work.isResourceConfirmationRequired()).thenReturn(true);

		workService.unassignWork(unassignDTO);

		verify(work).setConfirmed(false);
		verify(workResource).setConfirmed(false);
	}

	@Test
	public void unassignWork_resourceConfirmationNotRequired_workAndWorkResourceConfirmationNotReset() {
		when(work.isResourceConfirmationRequired()).thenReturn(false);

		workService.unassignWork(unassignDTO);

		verify(work, never()).setConfirmed(anyBoolean());
		verify(workResource, never()).setConfirmed(anyBoolean());
	}

	@Test
	public void unassignWork_checkInRequired_workResourceCheckInReset() {
		when(work.isCheckinRequired()).thenReturn(true);

		workService.unassignWork(unassignDTO);

		verify(workResource).setCheckedIn(false);
	}

	@Test
	public void unassignWork_checkInCallRequired_workResourceCheckInReset() {
		when(work.isCheckinCallRequired()).thenReturn(true);

		workService.unassignWork(unassignDTO);

		verify(workResource).setCheckedIn(false);
	}

	@Test
	public void unassignWork_checkInAndCheckInCallNotRequired_workResourceCheckInNotReset() {
		when(work.isCheckinRequired()).thenReturn(false);
		when(work.isCheckinCallRequired()).thenReturn(false);

		workService.unassignWork(unassignDTO);

		verify(workResource, never()).setCheckedIn(anyBoolean());
	}

	@Test
	public void unassignWork_notSpecifiedReasonTypeCode_buyerPrivateNoteNotAdded() {
		unassignDTO.setCancellationReasonTypeCode(StringUtils.EMPTY);

		workService.unassignWork(unassignDTO);

		verify(workNoteService, never()).addNoteToWork(anyLong(), any(NoteDTO.class));
	}

	@Test
	public void unassignWork_cancelledReasonTypeCode_buyerPrivateNoteAdded() {
		when(unassignDTO.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.RESOURCE_CANCELLED);

		workService.unassignWork(unassignDTO);

		verify(workNoteService).addNoteToWork(anyLong(), any(NoteDTO.class));
	}

	@Test
	public void unassignWork_abandonedReasonTypeCode_buyerPrivateNoteAdded() {
		when(unassignDTO.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.RESOURCE_CANCELLED);

		workService.unassignWork(unassignDTO);

		verify(workNoteService).addNoteToWork(anyLong(), any(NoteDTO.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void handleIncompleteWork_nullWorkId_illegalArgumentException() {
		workService.handleIncompleteWork(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void handleIncompleteWork_nullWork_illegalArgumentException() {
		when(workDAO.get(anyLong())).thenReturn(null);

		workService.handleIncompleteWork(WORK_ID);
	}

	@Test
	public void handleIncompleteWork_workId_OverridePriceNulledOut() {
		workService.handleIncompleteWork(WORK_ID);

		verify(fullPricingStrategy).setOverridePrice(null);
	}

	@Test
	public void handleIncompleteWork_workId_WorkStatusChangedToActive() {
		workService.handleIncompleteWork(WORK_ID);

		verify(work).setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));
	}

	@Test
	public void handleIncompleteWork_workId_workTransitionedToIncomplete() {
		workService.handleIncompleteWork(WORK_ID);

		verify(workStatusService).transitionToIncomplete(work);
	}

	@Test
	public void handleIncompleteWork_workId_workChangeLogEntrySaved() {
		workService.handleIncompleteWork(WORK_ID);

		verify(workChangeLogService).saveWorkChangeLog(any(WorkStatusChangeChangeLog.class));
	}

	@Test
	public void acceptWork_IsInBundle_SentNotInWorkFeed_Success() {
		when(work.isInBundle()).thenReturn(true);
		when(work.isSent()).thenReturn(true);
		when(work.isShownInFeed()).thenReturn(false);

		when(messageHelper.getMessage("assignment.accept.notavailable")).thenReturn("");

		when(workResourceDAO.findByUserAndWork(WORKER_ID, WORK_ID)).thenReturn(null);
		when(workResourceDAO.createOpenWorkResource(work, worker, false, false)).thenReturn(workResource);

		when(workResource.getWorkResourceStatusType()).thenReturn(WorkResourceStatusType.OPEN_STATUS);
		when(work.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));

		when(workActionRequestFactory.create(work, WorkAuditType.ACCEPT)).thenReturn(workActionRequest);

		when(workResource.getUser()).thenReturn(worker);
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(LaneType.LANE_4);
		when(workValidationService.isWorkResourceValidForWork(any(Long.class), any(Long.class), any(Long.class))).thenReturn(true);
		AcceptWorkResponse acceptWorkResponse = workService.acceptWork(WORKER_ID, WORK_ID);
		assertEquals(AcceptWorkStatus.SUCCESS, acceptWorkResponse.getStatus());
	}

	@Test
	public void confirmRequired_ConfirmableOn_IsCorrect() {
		Double confirmThresholdHours = 12.00D;
		when(work.isResourceConfirmationRequired()).thenReturn(true);
		when(work.getResourceConfirmationHours()).thenReturn(confirmThresholdHours);

		Calendar now = DateUtilities.getCalendarNow();
		when(work.getScheduleFrom()).thenReturn(now);
		when(workService.getAppointmentTime(work)).thenReturn(DateRangeUtilities.getDateRange(now.getTimeInMillis(), now.getTimeInMillis()));
		Calendar confirmableOn = workService.calculateRequiredConfirmationNotificationDate(work);
		assertEquals(DateUtilities.getHoursBetween(confirmableOn, now), confirmThresholdHours + Constants.CONFIRM_ADJUSTMENT_HRS, 0);
	}

	@Test
	public void confirmRequired_ConfirmBy_IsCorrect() {
		Double confirmThresholdHours = 12.00D;
		when(work.isResourceConfirmationRequired()).thenReturn(true);
		when(work.getResourceConfirmationHours()).thenReturn(confirmThresholdHours);

		Calendar now = DateUtilities.getCalendarNow();
		when(work.getScheduleFrom()).thenReturn(now);
		when(workService.getAppointmentTime(work)).thenReturn(DateRangeUtilities.getDateRange(now.getTimeInMillis(), now.getTimeInMillis()));
		Calendar confirmBy = workService.calculateRequiredConfirmationDate(work);
		assertEquals(DateUtilities.getHoursBetween(confirmBy, now), confirmThresholdHours.intValue());
	}

	@Test
	public void findActiveWorkerTimeWorked_withWorkIdAndWorkerId_convertTimeCorrectly() {
		when(workResourceTimeTrackingDAO.findAllByWorkResourceId(workResource.getId())).thenReturn(workResourceTimeTrackings);
		Map<String, Object> timeWorked = workService.findActiveWorkerTimeWorked(work.getId(), worker.getId());

		assertEquals(1L, timeWorked.get("hours"));
		assertEquals(30L, timeWorked.get("minutes"));
	}

	@Test
	public void findActiveWorkerTimeWorked_withJustWorkId_convertTimeCorrectly() {
		when(workResourceTimeTrackingDAO.findAllByWorkResourceId(workResource.getId())).thenReturn(workResourceTimeTrackings);
		Map<String, Object> timeWorked = workService.findActiveWorkerTimeWorked(work.getId());

		assertEquals(1L, timeWorked.get("hours"));
		assertEquals(30L, timeWorked.get("minutes"));
		verify(workService).findActiveWorkerTimeWorked(work.getId(), worker.getId());
	}

	@Test
	public void acceptWorkOnBehalf_successfulResponse_setDispatcher() throws Exception {
		workService.acceptWorkOnBehalf(acceptWorkOfferRequest);

		verify(workResourceService).setDispatcherForWorkAndWorker(WORK_ID, WORKER_ID);
	}

	@Test
	public void acceptWorkOnBehalf_unsuccessfulResponse_doNotSetDispatcher() throws Exception {
		when(work.isSent()).thenReturn(false);
		workService.acceptWorkOnBehalf(acceptWorkOfferRequest);

		verify(workResourceService, never()).setDispatcherForWorkAndWorker(WORK_ID, WORKER_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isAuthorizedToAdminister_nullUserId_throwException() {
		workService.isAuthorizedToAdminister(null, COMPANY1_ID, WORK_NUMBER);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isAuthorizedToAdminister_nullCompanyId_throwException() {
		workService.isAuthorizedToAdminister(USER_ID, null, WORK_NUMBER);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isAuthorizedToAdminister_nullWorkNumber_throwException() {
		workService.isAuthorizedToAdminister(USER_ID, COMPANY1_ID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isAuthorizedToAdminister_noRecordForWork_throwException() {
		when(workDAO.findWorkByWorkNumber(WORK_NUMBER)).thenReturn(null);

		workService.isAuthorizedToAdminister(USER_ID, COMPANY1_ID, WORK_NUMBER);
	}

	@Test
	public void isAuthorizedToAdminister_userIsNotBuyer_returnFalse() {
		when(work.getBuyer()).thenReturn(worker);

		assertFalse(workService.isAuthorizedToAdminister(USER_ID, COMPANY1_ID, WORK_NUMBER));
	}

	@Test
	public void isAuthorizedToAdminister_userIsNotInBuyerCompany_returnFalse() {
		assertFalse(workService.isAuthorizedToAdminister(USER2_ID, COMPANY2_ID, WORK_NUMBER));
	}

	@Test
	public void isAuthorizedToAdminister_userIsInBuyerCompany_butDoesNotHavePermission_returnFalse() {
		when(authenticationService.authorizeUserByAclPermission(USER2_ID, Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS)).thenReturn(false);

		assertFalse(workService.isAuthorizedToAdminister(USER2_ID, COMPANY1_ID, WORK_NUMBER));
	}

	@Test
	public void isAuthorizedToAdminister_userIsInBuyerCompany_hasPermission_returnTrue() {
		when(authenticationService.authorizeUserByAclPermission(USER2_ID, Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS)).thenReturn(true);

		assertTrue(workService.isAuthorizedToAdminister(USER2_ID, COMPANY1_ID, WORK_NUMBER));
	}

	@Test
	public void findWorkForInvitation_initializeCustomFields() {
		workService.findWorkForInvitation(WORK_ID);

		verify(work).getWorkCustomFieldsForEmailDisplay();
	}

	@Test
	public void transitionWorkToCanceledState_doSetNewWorkStatus() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(work).setWorkStatusType(workStatusType);
	}

	@Test
	public void cancelWork_setCancelledAssignmentOnWorkResource() throws Exception {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(workResource).setCancelledAssignment(true);
	}

	@Test
	public void cancelWork_isPaid_setCanceledOn() throws Exception {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		ArgumentCaptor<Calendar> canceledOnCaptor1 = ArgumentCaptor.forClass(Calendar.class);
		ArgumentCaptor<Calendar> canceledOnCaptor2 = ArgumentCaptor.forClass(Calendar.class);
		ArgumentCaptor<Calendar> canceledOnCaptor3 = ArgumentCaptor.forClass(Calendar.class);

		verify(work).setCancelledOn(canceledOnCaptor1.capture());
		verify(workMilestones).setCancelledOn(canceledOnCaptor2.capture());
		verify(work).setClosedOn(canceledOnCaptor3.capture());

		assertEquals(canceledOnCaptor1.getValue(), canceledOnCaptor2.getValue());
		assertEquals(canceledOnCaptor2.getValue(), canceledOnCaptor3.getValue());
	}

	@Test
	public void cancelWork_isNotPaid_setCanceledOn() throws Exception {
		when(cancelWorkDTO.isPaid()).thenReturn(false);

		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		ArgumentCaptor<Calendar> canceledOnCaptor1 = ArgumentCaptor.forClass(Calendar.class);
		ArgumentCaptor<Calendar> canceledOnCaptor2 = ArgumentCaptor.forClass(Calendar.class);

		verify(work).setCancelledOn(canceledOnCaptor1.capture());
		verify(workMilestones).setCancelledOn(canceledOnCaptor2.capture());

		assertEquals(canceledOnCaptor1.getValue(), canceledOnCaptor2.getValue());
	}

	@Test
	public void transitionWorkToCanceledState_isPaid_setDueOn() throws Exception {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(workMilestones).setDueOn(workDueDate);
		verify(work).setDueOn(workDueDate);
	}

	@Test
	public void transitionWorkToCanceledState_ifPaid_doGenerateInvoice() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(billingService).generateInvoiceForWork(work);
	}

	@Test
	public void transitionWorkToCanceledState_ifNotPaid_doNotGenerateInvoice() {
		when(cancelWorkDTO.isPaid()).thenReturn(false);

		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(billingService, never()).generateInvoiceForWork(work);
	}

	@Test
	public void transitionWorkToCanceledState_ifPaid_ifNoPaymentTerms_doCallOnPostPayAssignment() {
		when(work.hasPaymentTerms()).thenReturn(false);

		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		ArgumentCaptor<Calendar> calendarCaptor = ArgumentCaptor.forClass(Calendar.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

		verify(workStatusService).onPostPayAssignment(eq(work), eq(WORK_RESOURCE_ID), calendarCaptor.capture(), userCaptor.capture(), eq(workMilestones));
		assertEquals(calendarCaptor.getValue(), work.getDueDate());
		assertEquals(userCaptor.getValue(), authenticationService.getCurrentUser());
	}

	@Test
	public void transitionWorkToCanceledState_ifNotPaid_doCallOnPostPayAssignment() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(workStatusService, never()).onPostPayAssignment(any(Work.class), anyLong(), any(Calendar.class), any(User.class));
	}

	@Test
	public void transitionWorkToCanceledState_resolveAllInapplicableCustomWorkSubStatuses() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(workSubStatusService).resolveAllInapplicableCustomWorkSubStatuses(work);
	}

	@Test
	public void transitionWorkToCanceledState_cleanUpDeliverablesForReassignmentOrCancellation() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(workService).cleanUpDeliverablesForReassignmentOrCancellation(workResource);
	}

	@Test
	public void transitionWorkToCanceledState_doCallOnWorkCancelled() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(userNotificationService).onWorkCancelled(work, workResource, cancelWorkDTO, true);
	}

	@Test
	public void transitionWorkToCanceledState_saveWorkHistorySummary() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(summaryService).saveWorkHistorySummary(work, workResource, WorkStatusType.CANCELLED);
	}

	@Test
	public void transitionWorkToCanceledState_saveWorkChangeLog() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		ArgumentCaptor<WorkStatusChangeChangeLog> logCaptor = ArgumentCaptor.forClass(WorkStatusChangeChangeLog.class);
		verify(workChangeLogService).saveWorkChangeLog(logCaptor.capture());
		assertEquals(logCaptor.getValue().getWorkId(), WORK_ID);
		assertEquals(logCaptor.getValue().getActorId(), USER_ID);
		assertEquals(logCaptor.getValue().getMasqueradeActorId(), USER2_ID);
		assertEquals(logCaptor.getValue().getOnBehalfOfActorId(), WORKER_ID);
		assertEquals(logCaptor.getValue().getOldStatus(), workStatusType2);
		assertEquals(logCaptor.getValue().getNewStatus(), workStatusType);
	}

	@Test
	public void transitionWorkToCanceledState_saveWorkStatusTransitionHistorySummary() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(summaryService).saveWorkStatusTransitionHistorySummary(eq(work), eq(workStatusType2), eq(workStatusType), anyInt());
	}

	@Test
	public void transitionWorkToCanceledState_addLabelToWorkResourceAfterCancellation() {
		when(unassignDTO.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.RESOURCE_CANCELLED);

		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		ArgumentCaptor<CancellationReasonType> cancellationReasonTypeCaptor = ArgumentCaptor.forClass(CancellationReasonType.class);
		verify(workResourceService).addLabelToWorkResourceAfterCancellation(eq(WORK_RESOURCE_ID), cancellationReasonTypeCaptor.capture());
		assertEquals(cancellationReasonTypeCaptor.getValue().getCode(), cancelWorkDTO.getCancellationReasonTypeCode());
	}

	@Test
	public void transitionWorkToCanceledState_evictAllResourceScoreCardsForUser() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(scorecardCache).evictAllResourceScoreCardsForUser(WORKER_ID);
	}

	@Test
	public void transitionWorkToCanceledState_auditWork() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(workActionRequest).setAuditType(WorkAuditType.CANCEL);
		verify(workAuditService).auditWork(workActionRequest);
	}

	@Test
	public void transitionWorkToCanceledState_noteIsBlank_doNotAddNoteToWork() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(workNoteService, never()).addNoteToWork(anyLong(), any(NoteDTO.class));
	}

	@Test
	public void transitionWorkToCanceledState_noteIsBlank_addNoteToWork() {
		when(cancelWorkDTO.getNote()).thenReturn("dude aint good");

		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		ArgumentCaptor<NoteDTO> noteDTOCaptor = ArgumentCaptor.forClass(NoteDTO.class);
		verify(workNoteService).addNoteToWork(eq(work), noteDTOCaptor.capture(), any(User.class));
		assertEquals(noteDTOCaptor.getValue().getContent(), cancelWorkDTO.getNote());
		assertEquals(noteDTOCaptor.getValue().getPrivileged(), true);
	}

	@Test
	public void transitionWorkToCanceledState_resolveSystemSubStatusByAction() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(workSubStatusService).resolveSystemSubStatusByAction(
			work.getId(), WorkSubStatusType.INCOMPLETE_WORK,
			WorkSubStatusType.EXPENSE_REIMBURSEMENT, WorkSubStatusType.RESCHEDULE_REQUEST,
			WorkSubStatusType.RESOURCE_CANCELLED
		);
	}

	@Test
	public void transitionWorkToCanceledState_updateCalendarEventStatus() {
		workService.transitionWorkToCanceledState(
			WORK_ID, cancelWorkDTO, workActionRequest, workStatusType
		);

		verify(googleCalendarService).updateCalendarEventStatus(work.getId(), CalendarSyncSettings.CANCELLED);
	}

	@Test
	public void rollbackWorkPrice_originalPrice(){

		when(work.getPricingStrategy().getFullPricingStrategy()).thenReturn(new FullPricingStrategy());

		Calendar lastMonth = Calendar.getInstance();
		lastMonth.add(Calendar.MONTH, -1);

		Calendar lastWeek = Calendar.getInstance();
		lastWeek.add(Calendar.DAY_OF_MONTH, -7);

		Calendar yesterday = Calendar.getInstance();
		lastWeek.add(Calendar.DAY_OF_MONTH, -1);

		Calendar today = Calendar.getInstance();

		WorkPrice originalWorkPrice = createWorkPrice(100.0, lastMonth);
		WorkPrice workPriceBeforeLastWeek = createWorkPrice(150.0, lastWeek);
		WorkPrice workPriceBeforeYesterday = createWorkPrice(200.0, yesterday);
		WorkPrice workPriceBeforeToday = createWorkPrice(250.0, today);

		when(workPriceDAO.findPriceHistoryForWork(WORK_ID)).thenReturn(Arrays.asList(new WorkPrice[]{workPriceBeforeToday, workPriceBeforeYesterday, workPriceBeforeLastWeek, originalWorkPrice}));
		when(workPriceDAO.findOriginalPriceHistoryForWork(WORK_ID)).thenReturn(Optional.fromNullable(originalWorkPrice));
		workService.rollbackToOriginalPricePrice(WORK_ID);

		assertEquals(BigDecimal.valueOf(100.0), work.getPricingStrategy().getFullPricingStrategy().getFlatPrice());
	}

	private WorkPrice createWorkPrice(double flatPrice, Calendar createdOn){

		WorkPrice workPrice = new WorkPrice();
		FullPricingStrategy pricingStrategy = new FullPricingStrategy();
		pricingStrategy.setFlatPrice(BigDecimal.valueOf(flatPrice));
		workPrice.setCreatedOn(createdOn);
		workPrice.setFullPricingStrategy(pricingStrategy);

		return workPrice;
	}

	@Test
	public void testPaymentTermsUpdate_saveWorkChangeLog_ok(){

		WorkDTO workDTO = new WorkDTO();
		workDTO.setPaymentTermsDays(0);
		Work work = new Work();
		work.setPaymentTermsDays(30);
		workService.saveWorkChangeLogOnPaymentTermsUpdate(work, workDTO);
		verify(workChangeLogService).saveWorkChangeLog(any(WorkPropertyChangeLog.class));

		ArgumentCaptor<WorkPropertyChangeLog> logCaptor = ArgumentCaptor.forClass(WorkPropertyChangeLog.class);
		verify(workChangeLogService).saveWorkChangeLog(logCaptor.capture());
		assertEquals(logCaptor.getValue().getPropertyName(), "paymentTermsDays");
		assertEquals(logCaptor.getValue().getOldValue(), String.valueOf(30));
		assertEquals(logCaptor.getValue().getNewValue(), String.valueOf(0));
	}

	@Test
	public void testAugmentWorkSchedule_ok(){
		when(work.getPricingStrategy()).thenReturn(perHourPricingStrategy);
		when(work.getPricingStrategyType()).thenReturn(PricingStrategyType.PER_HOUR);
		workService.augmentWorkSchedule(workSchedule);

		verify(through).add(anyInt(), anyInt());
		verify(dateRange).setThrough(any(Calendar.class));
	}
}
