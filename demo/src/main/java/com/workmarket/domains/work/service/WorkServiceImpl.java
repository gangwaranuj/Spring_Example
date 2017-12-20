package com.workmarket.domains.work.service;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.istack.Nullable;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.wrapper.response.BaseResponse;
import com.workmarket.common.service.wrapper.response.Response;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.ClientLocationDAO;
import com.workmarket.service.business.dto.BuyerIdentityDTO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.random.WorkRandomIdentifierDAO;
import com.workmarket.dao.requirement.RequirementSetDAO;
import com.workmarket.dao.summary.user.UserSummaryDAO;
import com.workmarket.dao.summary.work.WorkStatusTransitionDAO;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.dao.WorkGroupAssociationDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.WorkGroupAssociation;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkPrice;
import com.workmarket.domains.model.WorkProperties;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.pricing.AccountPricingServiceTypeEntity;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.audit.ViewType;
import com.workmarket.domains.model.changelog.work.WorkNotifyChangeLog;
import com.workmarket.domains.model.changelog.work.WorkPropertyChangeLog;
import com.workmarket.domains.model.changelog.work.WorkRescheduleAutoApprovedChangeLog;
import com.workmarket.domains.model.changelog.work.WorkResourceStatusChangeChangeLog;
import com.workmarket.domains.model.changelog.work.WorkStatusChangeChangeLog;
import com.workmarket.domains.model.changelog.work.WorkUnassignChangeLog;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.google.CalendarSyncSettings;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.resource.LiteResource;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.OfflinePayAllRope;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkPriceDAO;
import com.workmarket.domains.work.dao.WorkQuestionAnswerPairDAO;
import com.workmarket.domains.work.dao.WorkRecurrenceAssociationDAO;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.dao.WorkResourceTimeTrackingDAO;
import com.workmarket.domains.work.dao.WorkTemplateDAO;
import com.workmarket.domains.work.dao.WorkUniqueIdDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.WorkDue;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.model.WorkRecurrenceAssociation;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.WorkUniqueId;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.ScheduleNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.StopPaymentDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkBundleDeclinedEvent;
import com.workmarket.service.business.event.work.WorkResendInvitationsEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.status.AcceptWorkStatus;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.business.wrapper.CloseWorkResponse;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.DuplicateWorkNumberException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.CalendarService;
import com.workmarket.service.infra.business.GoogleCalendarService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.index.UpdateWorkSearchIndex;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkActionResponseCodeType;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.GeoUtilities;
import com.workmarket.velvetrope.Doorman;
import net.fortuna.ical4j.model.ValidationException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.util.MathUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Calendar.HOUR;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class WorkServiceImpl implements WorkService {

	private static final Log logger = LogFactory.getLog(WorkServiceImpl.class);

	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private AddressService addressService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CalendarService calendarService;
	@Autowired private DirectoryService directoryService;
	@Autowired private LaneService laneService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private ProfileService profileService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private PricingService pricingService;
	@Autowired private WorkReportService workReportService;
	@Autowired private WorkStatusService workStatusService;
	@Autowired private BillingService billingService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private WorkRandomIdentifierDAO workNumberGenerator;
	@Autowired private UserService userService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private BaseWorkDAO abstractWorkDAO;
	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private WorkTemplateDAO workTemplateDAO;
	@Autowired private WorkResourceTimeTrackingDAO workResourceTimeTrackingDAO;
	@Autowired private WorkGroupAssociationDAO workGroupAssociationDAO;
	@Autowired private WorkPriceDAO workPriceDAO;
	@Autowired private SummaryService summaryService;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private WorkStatusTransitionDAO workStatusTransitionDAO;
	@Autowired private RequirementSetDAO requirementSetDAO;
	@Autowired private WorkQuestionAnswerPairDAO workQuestionAnswerPairDAO;
	@Autowired private ServiceMessageHelper messageHelper;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private AccountPricingService accountPricingService;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private EventRouter eventRouter;
	@Autowired private UserSummaryDAO userSummaryDAO;
	@Autowired private GoogleCalendarService googleCalendarService;
	@Autowired private DeliverableService deliverableService;
	@Autowired private EventFactory eventFactory;
	@Autowired private WorkResourceDetailCache workResourceDetailCache;
	@Autowired private ScorecardCache scorecardCache;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private IndustryService industryService;
	@Autowired private VendorService vendorService;
	@Autowired private WorkMilestonesService workMilestonesService;
	@Autowired private WorkUniqueIdDAO workUniqueIdDAO;
	@Autowired private UserRoleService userRoleService;
	@Autowired private ClientLocationDAO clientLocationDAO;
	@Autowired private WorkSearchService workSearchService;
	@Qualifier("workOptionsService") @Autowired private OptionsService<AbstractWork> workOptionsService;
	@Autowired private WorkRecurrenceAssociationDAO workRecurrenceAssociationDAO;

	@Autowired @Qualifier("offlinePayAllDoorman")
	private Doorman offlinePayAllDoorman;

	@Override
	public void saveOrUpdateWork(Work work) {
		workDAO.saveOrUpdate(work);
	}

	@Override
	public Long findWorkId(String workNumber) {
		return abstractWorkDAO.findWorkId(workNumber);
	}

	@Override
	public Long findBuyerCompanyId(final long workId) {
		return workDAO.findBuyerCompanyIdByWorkId(workId);
	}

	@Override
	public List<Long> findWorkIdsByBuyerAndStatus(Long buyerId, String... workStatusType) {
		return workDAO.findWorkIdsForBuyer(buyerId, workStatusType);
	}

	@Override
	public List<Long> findAllWorkIdsByUUIDs(final List<String> workUUIDs) {
		return workDAO.findAllWorkIdsByUUIDs(workUUIDs);
	}

	@Override
	public void deleteDraftAndSent(Long user, List<Long> deleteAssignments, List<Long> voidAssignments) {
		if (isNotEmpty(deleteAssignments)) {
			deleteDraft(user, deleteAssignments);
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(deleteAssignments, true));
		}
		if (isNotEmpty(voidAssignments)) {
			voidMultipleWork(voidAssignments, "void assignment");
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(voidAssignments, true));
		}
	}

	@Override
	public int countWorkByCompanyUserRangeAndStatus(Long companyId, Long userId, List<Long> excludeIds, Calendar fromDate, Calendar toDate, List<String> statuses) {
		return workDAO.countWorkByCompanyUserRangeAndStatus(companyId, userId, excludeIds, fromDate, toDate, statuses);
	}

	@Override
	public List<ConstraintViolation> voidWork(Long workId, String message) throws AccountRegisterConcurrentException {
		Assert.notNull(workId);
		Work work = workDAO.get(workId);
		Assert.notNull(work, "Unable to find work");
		WorkActionRequest request = workActionRequestFactory.create(work, WorkAuditType.VOID);
		List<ConstraintViolation> violations = workStatusService.transitionToVoid(request);
		if (violations.isEmpty()) {
			if (StringUtils.isNotBlank(message)) {
				workNoteService.addNoteToWork(workId, message);
			}
			webHookEventService.onWorkVoided(workId, work.getCompany().getId());
		}
		// TODO: move this outside the transaction
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
		return violations;
	}

	private Map<Long, List<ConstraintViolation>> voidMultipleWork(List<Long> workIds, String message) throws AccountRegisterConcurrentException {
		Assert.notNull(workIds);
		Map<Long, List<ConstraintViolation>> mapOfConstraintViolations = Maps.newHashMap();
		for (Long workId : workIds) {
			List<ConstraintViolation> constraintViolation = voidWork(workId, message);
			if (isNotEmpty(constraintViolation)) {
				mapOfConstraintViolations.put(workId, constraintViolation);
			}
		}
		return mapOfConstraintViolations;
	}

	public List<ConstraintViolation> cancelWork(CancelWorkDTO cancelWorkDTO) throws AccountRegisterConcurrentException {
		Assert.isTrue(CancellationReasonType.cancellationReasons.contains(cancelWorkDTO.getCancellationReasonTypeCode()));

		WorkActionRequest workRequest = workActionRequestFactory.create(cancelWorkDTO.getWorkId(), WorkAuditType.CANCEL);
		Assert.notNull(workRequest);
		workRequest.setAuditType(WorkAuditType.CANCEL);
		workRequest.validate();
		List<ConstraintViolation> violations = workStatusService.transitionToCancel(workRequest, cancelWorkDTO);
		Long workId = workRequest.getWorkId();
		final List<Long> companyIds = Lists.newArrayList();

		if (isEmpty(violations)) {
			if (isNotBlank(cancelWorkDTO.getNote())) {
				NoteDTO noteDTO = new NoteDTO(cancelWorkDTO.getNote()).setPrivileged(true);
				workNoteService.addNoteToWork(workId, noteDTO);
			}
			workSubStatusService.resolveSystemSubStatusByAction(workId, WorkSubStatusType.INCOMPLETE_WORK,
				WorkSubStatusType.EXPENSE_REIMBURSEMENT, WorkSubStatusType.RESCHEDULE_REQUEST,
				WorkSubStatusType.RESOURCE_CANCELLED);
		}

		if (workRequest.getMasqueradeId() == null) {
			logger.debug("Trying to reindex null user for work cancel");
		}

		try {
			googleCalendarService.updateCalendarEventStatus(workId, CalendarSyncSettings.CANCELLED);
		} catch (Exception e) {
			logger.error("[cancelWork] Google Calendar Service call failed. " + e);
		}

		// TODO: move these outside the transaction
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));

		// find the company of the user on this work assignment
		Long buyerId = getBuyerIdByWorkId(workId);
		if (buyerId != null) {
			companyIds.add(buyerId);
		}

		// do the same for the seller
		WorkResource wr = findActiveWorkResource(workId);
		if (wr != null) {
			if (wr.getUser() != null && wr.getUser().getCompany() != null) {
				companyIds.add(wr.getUser().getCompany().getId());
			}
		}

		eventRouter.sendEvent(new UserSearchIndexEvent(workRequest.getMasqueradeId()));
		return violations;
	}

	@Override
	public void transitionWorkToCanceledState(
		Long workId,
		CancelWorkDTO cancelWorkDTO,
		WorkActionRequest workRequest,
		final WorkStatusType newWorkStatus) {

		final WorkResource workResource = findActiveWorkResource(workId);
		final Work work = workResource.getWork();
		final Calendar cancelledOn = DateUtilities.getCalendarNow();
		final WorkStatusType oldWorkStatus = work.getWorkStatusType();

		work.setCancelledOn(cancelledOn);
		work.setCancellationReasonType(new CancellationReasonType(cancelWorkDTO.getCancellationReasonTypeCode()));
		work.setWorkStatusType(newWorkStatus);

		workResource.setCancelledAssignment(true);

		final WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		milestones.setCancelledOn(cancelledOn);

		if (cancelWorkDTO.isPaid()) {
			final Calendar dueOn = work.getDueDate();
			milestones.setDueOn(dueOn);
			work.setDueOn(dueOn);
			work.setClosedOn(cancelledOn);

			// always generate an invoice
			billingService.generateInvoiceForWork(work);

			if (!work.hasPaymentTerms()) {
				workStatusService.onPostPayAssignment(work, workResource.getId(), work.getDueDate(), authenticationService.getCurrentUser(), milestones);
			}
		}

		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);
		cleanUpDeliverablesForReassignmentOrCancellation(workResource);
		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, cancelWorkDTO.isPaid());
		summaryService.saveWorkHistorySummary(work, workResource, WorkStatusType.CANCELLED);

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(
				work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), oldWorkStatus, newWorkStatus
		));
		summaryService.saveWorkStatusTransitionHistorySummary(
			work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getMilestonesFieldFromWorkStatus(oldWorkStatus), cancelledOn)
		);

		workResourceService.addLabelToWorkResourceAfterCancellation(workResource.getId(), new CancellationReasonType(cancelWorkDTO.getCancellationReasonTypeCode()));
		scorecardCache.evictAllResourceScoreCardsForUser(workResource.getUser().getId());

		workRequest.setAuditType(WorkAuditType.CANCEL);
		workAuditService.auditWork(workRequest);

		if (isNotBlank(cancelWorkDTO.getNote())) {
			NoteDTO noteDTO = new NoteDTO(cancelWorkDTO.getNote()).setPrivileged(true);
			workNoteService.addNoteToWork(work, noteDTO, null);
		}
		workSubStatusService.resolveSystemSubStatusByAction(
			work.getId(), WorkSubStatusType.INCOMPLETE_WORK,
			WorkSubStatusType.EXPENSE_REIMBURSEMENT, WorkSubStatusType.RESCHEDULE_REQUEST,
			WorkSubStatusType.RESOURCE_CANCELLED
		);

		// TODO: Alex - Refactor into async event?
		try {
			googleCalendarService.updateCalendarEventStatus(work.getId(), CalendarSyncSettings.CANCELLED);
		} catch (Exception e) {
			logger.error("[cancelWork] Google Calendar Service call failed. " + e);
		}

	}

	@Override
	public void handleIncompleteWork(Long workId) {
		Assert.notNull(workId);
		Work work = workDAO.get(workId);
		Assert.notNull(work, "Work cannot be null");

		// Do not persist the requested override price when returning an assignment
		// back to the resource. If they really wanted it, they can ask for it again.
		work.getPricingStrategy().getFullPricingStrategy().setOverridePrice(null);

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE);
		work.setWorkStatusType(newWorkStatus);

		workStatusService.transitionToIncomplete(work);

		// TODO: move these outside the transaction
		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(workId, authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId(), null, oldWorkStatus,
			newWorkStatus));
	}

	@Override
	public void incompleteWork(Long workId, String message) {
		Assert.notNull(workId);
		Work work = workDAO.get(workId);
		Assert.notNull(work, "Unable to find work");

		// Do not persist the requested override price when returning an assignment
		// back to the resource. If they really wanted it, they can ask for it again.
		work.getPricingStrategy().getFullPricingStrategy().setOverridePrice(null);

		// Reset sales tax collected flag
		work.getPricingStrategy().getFullPricingStrategy().setSalesTaxCollectedFlag(false);

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE);

		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.addSystemSubStatus(workId, WorkSubStatusType.INCOMPLETE_WORK, message);
		User currentUser = authenticationService.getCurrentUser();
		if (currentUser == null) {
			currentUser = work.getBuyer();
		}

		workStatusService.transitionToIncomplete(work);

		// TODO: move these outside the transaction
		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(workId, authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), null, oldWorkStatus,
			newWorkStatus));
		WorkActionRequest workActionRequest = workActionRequestFactory.create(work, currentUser.getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.INCOMPLETE);
		workAuditService.auditWork(workActionRequest);
		userNotificationService.onWorkIncomplete(work, message);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}


	@Override
	public List<ConstraintViolation> stopWorkPayment(Long workId, StopPaymentDTO dto) {
		Assert.notNull(workId);
		Work work = workDAO.get(workId);
		Assert.notNull(work, "Unable to find work");

		return stopWorkPayment(work, dto);

	}

	private List<ConstraintViolation> stopWorkPayment(Work work, StopPaymentDTO dto) {
		Assert.notNull(work);
		Assert.notNull(dto);

		WorkActionRequest workRequest = workActionRequestFactory.create(work, WorkAuditType.ACCEPT);
		return workStatusService.transitionToStopPayment(workRequest, dto);
	}

	@Override
	public CloseWorkResponse closeWork(Long workId) {
		return closeWork(workId, new CloseWorkDTO());
	}

	@Override
	@UpdateWorkSearchIndex(workIdArgumentPosition = 1)
	public CloseWorkResponse closeWork(Long workId, CloseWorkDTO dto) {
		checkNotNull(workId);
		WorkActionRequest workRequest = workActionRequestFactory.create(workId, WorkAuditType.CLOSE);
		return workStatusService.transitionToClosed(workRequest, dto);
	}

	@Override
	public WorkActionResponse acceptWorkOnBehalf(AcceptWorkOfferRequest request) throws WorkActionException {
		Assert.notNull(request.getWorkAction());
		Assert.notNull(request.getWorkAction().getWorkNumber());

		final User user = userService.findUserByUserNumber(request.getWorkAction().getResourceUserNumber());

		User onBehalfOfUser = userService.findUserByUserNumber(request.getWorkAction().getOnBehalfOfUserNumber());
		if (onBehalfOfUser == null) {
			logger.error("Need an on behalf of user number: " + request);
			throw new WorkActionException("There was no on behalf of user " + request);
		}

		Work work = findWorkByWorkNumber(request.getWorkAction().getWorkNumber());
		AcceptWorkResponse response = acceptWork(user, onBehalfOfUser.getId(), work);

		if (response.isSuccessful()) {
			workResourceService.setDispatcherForWorkAndWorker(work.getId(), user.getId());
			return new WorkActionResponse().setResponseCode(WorkActionResponseCodeType.SUCCESS);
		}

		return new WorkActionResponse()
			.setResponseCode(WorkActionResponseCodeType.WORK_SERVICE_EXCEPTION)
			.setMessage(StringUtils.join(response.getMessages(), "\n"));
	}

	// Work Lifecycle: Work resource actions
	// NOTE We should capture timestamp/audit information for work resources for
	// the following events: invited, accepted, declined

	// This is ONLY the Transactional part of the acceptWork flow... don't call these directly, use workFacadeService

	@Override
	public AcceptWorkResponse acceptWork(Long userId, Long workId) {
		Assert.notNull(workId);
		return acceptWork(userService.getUser(userId), null, (Work) abstractWorkDAO.findById(workId));
	}

	@Override
	public AcceptWorkResponse acceptWork(User user, Work work) {
		Assert.notNull(user);
		Assert.notNull(work);
		return acceptWork(user, null, work);
	}

	private AcceptWorkResponse acceptWork(User workResourceUser, Long onBehalfOfUserId, Work work) {
		Assert.notNull(work);

		WorkResource workResource = findWorkResource(workResourceUser.getId(), work.getId());
		Boolean isDispatched = workResource != null && workResource.getDispatcherId() != null;
		if (!isDispatched && onBehalfOfUserId != null) {
			User user = userService.getUser(onBehalfOfUserId);
			if (user != null && userRoleService.isDispatcher(user)) {
				isDispatched = true;
			}
		}
		if (!workValidationService.isWorkResourceValidForWork(workResourceUser.getId(), workResourceUser.getCompany().getId(), work.getCompany().getId()) &&
			!(isDispatched && workValidationService.isWorkResourceValidForDispatch(workResourceUser.getId(), work.getCompany().getId()))) {
			boolean subjectIsAccepter = !work.isInBundle() && work.isAssignToFirstResourceEnabled() && onBehalfOfUserId == null;
			String subject = subjectIsAccepter ? "You" : workResourceUser.getFullName();
			String verb = subjectIsAccepter ? "are" : "is";
			String pronoun = subjectIsAccepter ? "you" : "they";

			return new AcceptWorkResponse(
				work,
				AcceptWorkStatus.INVALID_RESOURCE,
				messageHelper.getMessage("assignment.accept.invalid_resource", subject, verb, pronoun, work.getCompany().getEffectiveName()));
		}

		Assert.notNull(work);

		if (!work.isSent()) {
			return new AcceptWorkResponse(
				work,
				AcceptWorkStatus.NOT_SENT_STATUS,
				messageHelper.getMessage("assignment.accept.notavailable"));
		}

		if (workResource == null &&
			(work.isShownInFeed() || work.isInBundle() ||
				(onBehalfOfUserId != null && getWorkContext(work, onBehalfOfUserId).contains(WorkContext.DISPATCHER)))) {
			boolean isDispatch = getWorkContext(work, onBehalfOfUserId).contains(WorkContext.DISPATCHER);
			workResource = workResourceDAO.createOpenWorkResource(work, workResourceUser, false, isDispatch);
		}
		Assert.notNull(workResource);

		WorkResourceStatusType oldStatus = workResource.getWorkResourceStatusType();
		workResource.setWorkResourceStatusType(new WorkResourceStatusType(WorkResourceStatusType.ACTIVE));
		workResource.setAssignedToWork(true);
		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE);

		work.setWorkStatusType(newWorkStatus);

		//Check the resource lane
		LaneType laneType = laneService.getLaneTypeForUserAndCompany(workResourceUser.getId(), work.getCompany().getId());
		Assert.notNull(laneType);
		//Check the pricing strategy
		if (laneType.isEmployeeLane() && !PricingStrategyType.INTERNAL.equals(work.getPricingStrategyType()) && !isOfflinePayment(work)) {
			work.setPricingStrategy(new InternalPricingStrategy());
		}

		workNegotiationService.cancelPendingNegotiationsByCompanyForWork(workResourceUser.getCompany().getId(), work.getId());
		WorkActionRequest workRequest = workActionRequestFactory.create(work, WorkAuditType.ACCEPT);
		workStatusService.transitionToAccepted(workRequest, workResource);

		accountRegisterAuthorizationService.acceptWork(workResource);

		userSummaryDAO.saveOrUpdateUserLastAssignedDate(workResourceUser.getId(), Calendar.getInstance());

		// TODO: get this outta here.
		workChangeLogService.saveWorkChangeLog(new WorkResourceStatusChangeChangeLog(work.getId(), workResourceUser.getId(),
			authenticationService.getMasqueradeUserId(), onBehalfOfUserId, oldStatus, WorkResourceStatusType.ACTIVE_STATUS));

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), workResourceUser.getId(),
			authenticationService.getMasqueradeUserId(), onBehalfOfUserId, oldWorkStatus, newWorkStatus));

		return AcceptWorkResponse
			.success()
			.setWork(work)
			.setActiveResource(workResource);
	}

	@Override
	public void declineWork(Long userId, Long workId) {
		declineWork(userId, workId, null);
	}

	@Override
	public void declineWork(Long userId, Long workId, Long onBehalfOfUserId) {
		WorkResource workResource = findWorkResource(userId, workId);
		Assert.notNull(workResource);

		if (workResource.getWork().isWorkBundle()) {
			WorkBundleDeclinedEvent workBundleDeclinedEvent = eventFactory.buildWorkBundleDeclinedEvent(userId, workId, onBehalfOfUserId);
			workBundleDeclinedEvent.setUser(authenticationService.getCurrentUser());
			eventRouter.sendEvent(workBundleDeclinedEvent);
		}

		WorkResourceStatusType oldStatus = workResource.getWorkResourceStatusType();

		workResource.setAssignedToWork(false);
		workResource.setWorkResourceStatusType(new WorkResourceStatusType(WorkResourceStatusType.DECLINED));

		workNegotiationService.cancelPendingNegotiationsByCompanyForWork(workResource.getUser().getCompany().getId(), workId);
		WorkActionRequest workRequest = workActionRequestFactory.create(workResource.getWork(), workResource.getUser().getId(), onBehalfOfUserId, authenticationService.getMasqueradeUserId(), WorkAuditType.RESOURCE_DECLINE);
		workAuditService.auditWork(workRequest);

		userNotificationService.onWorkDeclined(workResource.getWork(), workResource);
		workResourceDetailCache.evict(workId);

		// If all work resources have declined the work, mark it as declined.
		// Otherwise leave it open.
		if (isNotEmpty(workResourceDAO.findNotDeclinedForWork(workId))) {
			return;
		}

		WorkStatusType oldWorkStatus = workResource.getWork().getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.DECLINED);
		workResource.getWork().setWorkStatusType(newWorkStatus);

		workStatusService.transitionToDeclined(workRequest);

		// TODO: move these outside the transaction
		workChangeLogService.saveWorkChangeLog(new WorkResourceStatusChangeChangeLog(workId, userId,
			authenticationService.getMasqueradeUserId(), onBehalfOfUserId, oldStatus, new WorkResourceStatusType(WorkResourceStatusType.DECLINED)));

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(workId, authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(),
			onBehalfOfUserId, oldWorkStatus, newWorkStatus));

		workRequest = workActionRequestFactory.create(workResource.getWork(), workResource.getUser().getId(), onBehalfOfUserId, authenticationService.getMasqueradeUserId(), WorkAuditType.DECLINE);
		workAuditService.auditAndReindexWork(workRequest);

		summaryService.saveWorkResourceHistorySummary(workResource);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	public void undeclineWork(WorkResource workResource) {

		Assert.notNull(workResource);

		WorkResourceStatusType oldStatus = workResource.getWorkResourceStatusType();

		logger.info("[undeclineWork] undeclining " + workResource.getId() + " work " + workResource.getWork().getId());
		workResource.setWorkResourceStatusType(new WorkResourceStatusType(WorkResourceStatusType.OPEN));
		workResource.setAssignedToWork(false);
		WorkActionRequest workRequest = workActionRequestFactory.create(workResource.getWork(), workResource.getUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.RESEND_UNDECLINE);
		workAuditService.auditWork(workRequest);

		// TODO: move these outside the transaction
		workChangeLogService.saveWorkChangeLog(new WorkResourceStatusChangeChangeLog(workResource.getWork().getId(), workResource.getUser().getId(),
			authenticationService.getMasqueradeUserId(), null, oldStatus, new WorkResourceStatusType(WorkResourceStatusType.OPEN)));

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workResource.getWork().getId()));
	}

	private void resolveWorkResourceStatus(WorkResource workResource) {
		Assert.notNull(workResource);
		WorkResourceStatusType oldStatus = workResource.getWorkResourceStatusType();
		logger.info("[resolveWorkResourceStatus] resource user id: " + workResource.getUser().getId() + " workId: " + workResource.getWork().getId() + "work resource status: " + oldStatus.getCode());

		workResourceService.deleteWorkResourceLabel(workResource.getId(), WorkResourceLabelType.CANCELLED);

		if (WorkResourceStatusType.DECLINED.equals(oldStatus.getCode())) {
			undeclineWork(workResource);

		} else if (WorkResourceStatusType.CANCELLED.equals(oldStatus.getCode())) {
			WorkActionRequest workRequest = workActionRequestFactory.create(workResource.getWork(), workResource.getUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.RESEND_UNDECLINE);
			workAuditService.auditWork(workRequest);

			workStatusService.transitionFromAbandonedToOpenWork(workResource.getUser().getId(), workRequest);
		}
	}

	private boolean deleteDraft(Long userId, List<Long> workIds) {
		Assert.notNull(userId);
		Assert.notNull(workIds);
		for (Long workId : workIds) {
			deleteDraft(userId, workId);
		}
		return true;
	}

	@Override
	public boolean deleteDraft(Long userId, Long workId) {
		Assert.notNull(userId);
		Assert.notNull(workId);

		AbstractWork work = abstractWorkDAO.get(workId);

		Assert.state(work.isDraft(), "Assignment is not in DRAFT status");

		List<WorkContext> context = getWorkContext(workId, userId);

		Assert.state(context.contains(WorkContext.OWNER) || context.contains(WorkContext.COMPANY_OWNED),
			"User is not the owner of the assignment.");

		// deleted assignments must be removed from draft bundle
		workBundleService.removeFromBundle(workId);

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.DELETED);

		work.setWorkStatusType(newWorkStatus);
		work.setDeleted(true);

		// TODO: move these outside the transaction
		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(workId, authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), null, oldWorkStatus,
			newWorkStatus));
		WorkActionRequest workActionRequest = workActionRequestFactory.create(work, authenticationService.getCurrentUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.DELETE);
		workAuditService.auditWork(workActionRequest);
		workStatusTransitionDAO.deleteWorkStatusTransition(workId, WorkStatusType.DRAFT);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId, true));
		return work.getDeleted();
	}

	@Override
	public List<ConstraintViolation> abandonWork(Long userId, Long workId, String message) {
		Assert.notNull(userId);
		Assert.notNull(workId);
		WorkActionRequest workRequest = new WorkActionRequest();
		workRequest.setModifierId(userId);
		User masqUser = authenticationService.getMasqueradeUser();
		if (masqUser != null) {
			workRequest.setMasqueradeId(masqUser.getId());
		}
		workRequest.setAuditType(WorkAuditType.ABANDON);
		workRequest.setWorkId(workId);

		//Note: message is never used in this function call
		return workStatusService.transitionToExceptionAbandonedWork(userId, workRequest, message);
	}

	@Override
	public void unassignWork(UnassignDTO unassignDTO) {
		Assert.notNull(unassignDTO);
		Long workId = unassignDTO.getWorkId();
		WorkResource workResource = findActiveWorkResource(workId);
		Assert.notNull(workResource);
		Work work = workResource.getWork();
		Assert.notNull(work);

		if (isResourceCurrentlyCheckedIn(work)) {
			checkOutActiveResource(new TimeTrackingRequest()
				.setWorkId(workId)
				.setDate(DateUtilities.getCalendarNow())
				.setNotifyOnCheckOut(false));
		}

		if (work.isResourceConfirmationRequired()) {
			work.setConfirmed(false);
			workResource.setConfirmed(false);
		}

		if (work.isCheckinRequired() || work.isCheckinCallRequired()) {
			workResource.setCheckedIn(false);
		}

		removeWorkerFromWork(workId, true, unassignDTO.isRollbackToOriginalPrice());
		workResourceDetailCache.evict(workId);

		cleanUpDeliverablesForReassignmentOrCancellation(workResource);
		deliverableService.removeAllDeliverablesFromWork(work.getId());

		workResourceService.addLabelToWorkResourceAfterCancellation(workResource.getId(), new CancellationReasonType(unassignDTO.getCancellationReasonTypeCode()));
		scorecardCache.evictAllResourceScoreCardsForUser(workResource.getUser().getId());

		userNotificationService.onWorkUnassigned(workResource, unassignDTO.getNote());

		if ((work.getPricingStrategyType() == PricingStrategyType.INTERNAL && !unassignDTO.getNote().isEmpty()) ||
			CancellationReasonType.UNASSIGN_REASON_MAP.containsKey(unassignDTO.getCancellationReasonTypeCode())) {
			NoteDTO noteDTO = new NoteDTO();
			String unassignReason =
				work.getPricingStrategyType() == PricingStrategyType.INTERNAL ? "Not specified" :
				CancellationReasonType.UNASSIGN_REASON_MAP.get(unassignDTO.getCancellationReasonTypeCode());
			String unassignNote = messageHelper.getMessage("assignment.unassign.private_note",
				workResource.getUser().getFullName(),
				unassignReason,
				unassignDTO.getNote());
			noteDTO.setContent(unassignNote);
			noteDTO.setIsPrivate(true);

			workNoteService.addNoteToWork(work.getId(), noteDTO);
		}

		WorkUnassignChangeLog workUnassignChangeLog = new WorkUnassignChangeLog(
			workId, authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), null, workResource.getUser().getFullName()
		);
		workChangeLogService.saveWorkChangeLog(workUnassignChangeLog);
	}

	@Override
	public List<ConstraintViolation> unassignWorker(UnassignDTO unassignDTO) {

		final Work work = findWork(unassignDTO.getWorkId());

		WorkStatusType workStatusType = work.getWorkStatusType();

		List<ConstraintViolation> violations = workValidationService.validateUnassign(workStatusType, unassignDTO);
		if (!isEmpty(violations)) {
			return violations;
		}

		ConstraintViolation invoicedViolation = workValidationService.validateNotInvoiced(work.getId());
		if (invoicedViolation != null) {
			violations.add(invoicedViolation);
			return violations;
		}

		if (work.isWorkBundle()) { // This was previously incorrectly checking if the assignment was IN a bundle
			if (!workBundleService.unassignBundle(unassignDTO)) {
				violations.add(new ConstraintViolation("assignment.unassign.notallowed"));
				return violations;
			}

			workSearchService.reindexWorkAsynchronous(workBundleService.getAllWorkIdsInBundle(work.getId()));
		} else {
			if (work.isInBundle()) {
				workBundleService.removeFromBundle(work.getParent().getId(), work.getId());
			}
			unassignWork(unassignDTO);
			workSearchService.reindexWorkAsynchronous(work.getId());
		}

		return violations;
	}

	@Override
	public void removeWorkerFromWork(Long workId, Boolean buyerInitiated) {

		removeWorkerFromWork(workId, buyerInitiated, false);
	}

	private void removeWorkerFromWork(Long workId, Boolean buyerInitiated, boolean rollbackToOriginalPrice){
		AbstractWork work = abstractWorkDAO.get(workId);
		Assert.notNull(work);

		WorkActionRequest workRequest = workActionRequestFactory.create(work, WorkAuditType.SEND);
		if (work.isActive()) {
			workStatusService.transitionActiveToSent(workRequest, buyerInitiated);
			if (rollbackToOriginalPrice) {
				rollbackToOriginalPricePrice(workId);
			}
		} else if (work.isDeclined()) {
			workStatusService.transitionDeclinedToSent(workRequest);
		} else if (work.isComplete()) {
			handleIncompleteWork(workId);
			workStatusService.transitionActiveToSent(workRequest, buyerInitiated);
		}

		// Message all resources who are still available to take on the work
		WorkResourcePagination pagination = new WorkResourcePagination(true);
		pagination.getFilters().put(WorkResourcePagination.FILTER_KEYS.WORK_RESOURCE_STATUS.toString(), WorkResourceStatusType.OPEN);
		pagination = findWorkResources(workId, pagination);

		userNotificationService.onWorkReinvited((Work) work, pagination.getResults());

		workSubStatusService.resolveSystemSubStatusByAction(workId, WorkSubStatusType.INCOMPLETE_WORK,
			WorkSubStatusType.RESCHEDULE_REQUEST, WorkSubStatusType.EXPENSE_REIMBURSEMENT,
			WorkSubStatusType.RESOURCE_CANCELLED, WorkSubStatusType.RESOURCE_CONFIRMED,
			WorkSubStatusType.RESOURCE_CHECKED_OUT);

		// TODO What if no resources?
		// TODO: move these outside the transaction
		WorkActionRequest workActionRequest = workActionRequestFactory.create(work, authenticationService.getCurrentUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.UNASSIGN);
		workAuditService.auditWork(workActionRequest);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	public List<ConstraintViolation> completeWork(Long workId, CompleteWorkDTO dto) {
		return completeWork(workId, null, dto);
	}

	@Override
	public List<ConstraintViolation> completeWork(Long workId, Long onBehalfOfId, CompleteWorkDTO dto) {
		Assert.notNull(workId);
		Assert.notNull(dto);
		WorkActionRequest request = workActionRequestFactory.create(workId, WorkAuditType.COMPLETE);
		request.setOnBehalfOfId(onBehalfOfId);
		return workStatusService.transitionToComplete(request, dto);
	}

	// NOTE: This is called from WorkFacadeService.saveOrUpdateWork
	// You should probably be calling it from there too.
	@Override
	public Work saveOrUpdateWork(Long userId, WorkDTO workDTO) {
		Work work;

		boolean initialize = false;
		boolean paymentTermsEnabled = false;
		boolean priceChanged = false;

		if (workDTO.getId() != null) {
			work = workDAO.get(workDTO.getId());
			paymentTermsEnabled = work.getManageMyWorkMarket().getPaymentTermsEnabled();

			// Checking if the pricing strategy has changed
			if (work.isPricingEditable()) {
				PricingStrategy newPricing = pricingService.findPricingStrategyById(workDTO.getPricingStrategyId());
				BeanUtilities.copyProperties(newPricing, workDTO);
				if (workDTO.getUseMaxSpendPricingDisplayModeFlag()) {
					newPricing = pricingService.adjustPricingByCompanyFeePercentage(newPricing, work.getCompany().getId(), work.getId());
				}
				priceChanged = !newPricing.equals(work.getPricingStrategy());
			}

			//Audit changes in payment terms
			saveWorkChangeLogOnPaymentTermsUpdate(work, workDTO);

		} else {
			initialize = true;
			work = new Work();
		}

		work = buildWork(userId, workDTO, work, initialize);

		workDAO.saveOrUpdate(work);
		// If company requires a unique external id, update the value if one already exists. Otherwise create a new one
		WorkUniqueId workUniqueId = work.getWorkUniqueId();
		CompanyPreference companyPreference = work.getCompany().getCompanyPreference();
		if(companyPreference.isExternalIdActive()) {
			if (workUniqueId != null) {
				// If this assignment had a unique external id value but now it's been removed, delete the work_unique_id record
				if(StringUtils.isEmpty(workDTO.getUniqueExternalId())){
					workUniqueIdDAO.delete(workUniqueId);
					work.setWorkUniqueId(null);
				} else { // otherwise update it
					workUniqueId.setIdValue(workDTO.getUniqueExternalId());
				}
			} else if (!StringUtils.isEmpty(workDTO.getUniqueExternalId())) {
				workUniqueId = new WorkUniqueId();
				workUniqueId.setWorkId(work.getId());
				workUniqueId.setCompany(work.getCompany());
				workUniqueId.setDisplayName(companyPreference.getExternalIdDisplayName());
				workUniqueId.setVersion(companyPreference.getExternalIdVersion());
				workUniqueId.setIdValue(workDTO.getUniqueExternalId());

				work.setWorkUniqueId(workUniqueId);
				workUniqueIdDAO.saveOrUpdate(workUniqueId);
			}
		}

		workOptionsService.setOption(work, WorkOption.DOCUMENTS_ENABLED, String.valueOf(workDTO.isDocumentsEnabled()));

		setOfflinePayment(work, workDTO.isOfflinePayment());

		if (initialize) {
			workStatusService.transitionToCreated(work.getId(), userId, null, work.getCompany().getId());
		}

		if (workDTO.getId() != null && work.isPricingEditable()) {
			if ((paymentTermsEnabled != workDTO.isPaymentTermsEnabled() && !work.isDraft()) || (work.isSent() && priceChanged)) {
				repriceWork(work.getId(), workDTO);
			} else if (work.getPricingStrategy() != null) {
				if (work.getFulfillmentStrategy() == null) {
					work.setFulfillmentStrategy(new FulfillmentStrategy());
				}
				work.getFulfillmentStrategy().setWorkPricePriorComplete(pricingService.calculateMaximumResourceCost(work.getPricingStrategy()));
			}
		}

		if (userId == null) {
			logger.debug("Trying to reindex null user for update work");
		}

		work.setPartOfBulk(workDTO.isPartOfBulk());

		return work;
	}

	@Override
	public <T extends AbstractWork> T buildWork(Long userId, WorkDTO workDTO, T work, boolean initialize) {
		Assert.notNull(userId);
		Assert.notNull(workDTO);

		if (workDTO.getBuyerId() != null) {
			userId = workDTO.getBuyerId();
		}

		boolean isTemplate = (work instanceof WorkTemplate);

		if (!isTemplate) {
			if (workDTO.isSetOnsiteAddress() && workDTO.getIsOnsiteAddress() && (workDTO.getLocationId() == null)) {
				Assert.notNull(workDTO.getAddressId());
			}
		}

		User user = userService.getUser(userId);
		Assert.notNull(user.getCompany(), "User must have a company");
		Company company = user.getCompany();
		Address address = null;

		work.setBuyer(user);
		work.setCompany(company);

		if (initialize) {
			work.setWorkNumber(workNumberGenerator.generateUniqueNumber());
			work.setIsOnsiteAddress(workDTO.getIsOnsiteAddress());
			Assert.notNull(company, "Unable to find a company");
			BeanUtilities.copyProperties(work.getManageMyWorkMarket(), company.getManageMyWorkMarket());
		}

		if (workDTO.isSetOnsiteAddress() && workDTO.getIsOnsiteAddress()) {
			if (workDTO.getLocationId() != null) {
				/*
				 * We want to associate the address to work even when it's assigned at the location level, because if later the location's address change we won't lose the history of where the work
				 * was performed.
				 */
				Location location = directoryService.findLocationById(workDTO.getLocationId());
				Assert.notNull(location);
				work.setLocation(location);
				address = location.getAddress();
			} else if (workDTO.getAddressId() != null) {
				address = addressService.findById(workDTO.getAddressId());
			}

			if (address != null) {
				address.setDeactivatedFlag(false);
				work.setAddress(address);
			}
		} else if (work.isSetOnsiteAddress() && work.getIsOnsiteAddress()) {
			// TODO All work address are part of the address book. At which
			// point are they considered 'deactivated'?
			if (!work.isClientLocationAddress()) {
				address = work.getAddress();
				if (address != null) {
					address.setDeactivatedFlag(true);
				}
			}
			work.setAddress(null);
			work.setLocation(null);
		}

		if (initialize) {
			/*
			 * Snapshot the current pricing and service types.
			 * It will be re-set when paying the assignment.
			 */
			AccountPricingServiceTypeEntity accountPricingServiceTypeEntity = new AccountPricingServiceTypeEntity();
			accountPricingServiceTypeEntity.setAccountPricingType(work.getCompany().getAccountPricingType());
			accountPricingServiceTypeEntity.setAccountServiceType(accountPricingService.findAccountServiceTypeConfiguration(work));
			work.setAccountPricingServiceTypeEntity(accountPricingServiceTypeEntity);
		}

		if (!isTemplate) {
			// If the work was created based on a template
			if (workDTO.getWorkTemplateId() != null) {
				WorkTemplate template = workTemplateDAO.findWorkTemplateById(workDTO.getWorkTemplateId());
				work.setTemplate(template);
			}
		}

		if (workDTO.getIndustryId() != null) {
			Industry industry = invariantDataService.findIndustry(workDTO.getIndustryId());
			work.setIndustry(industry);
		} else {
			//Default to the buyer's industry
			work.setIndustry(industryService.getDefaultIndustryForProfile(user.getProfile().getId()));
		}

		// Client Company
		ClientCompany clientCompany = null;
		if (workDTO.getClientCompanyId() != null) {
			clientCompany = directoryService.findClientCompanyById(workDTO.getClientCompanyId());
		}
		work.setClientCompany(clientCompany);

		// Buyer Support User
		User buyerSupportUser = null;
		if (workDTO.getBuyerSupportUserId() != null) {
			buyerSupportUser = userService.getUser(workDTO.getBuyerSupportUserId());
		}
		work.setBuyerSupportUser(buyerSupportUser);

		// Client Contacts
		work.setServiceClientContactId(workDTO.getServiceClientContactId());
		work.setSecondaryServiceClientContactId(workDTO.getSecondaryClientContactId());

		work.setResourceConfirmationRequired(workDTO.isResourceConfirmationRequired());
		work.getManageMyWorkMarket().setCheckinRequiredFlag(workDTO.isCheckinRequired());
		work.getManageMyWorkMarket().setShowCheckoutNotesFlag(workDTO.getShowCheckoutNotes());
		work.getManageMyWorkMarket().setCheckoutNoteRequiredFlag(workDTO.isCheckoutNoteRequired());
		work.getManageMyWorkMarket().setCheckoutNoteInstructions(workDTO.getCheckoutNoteInstructions());
		work.getManageMyWorkMarket().setUseMaxSpendPricingDisplayModeFlag(workDTO.getUseMaxSpendPricingDisplayModeFlag());
		work.getManageMyWorkMarket().setDisablePriceNegotiation(workDTO.getDisablePriceNegotiation());
		work.getManageMyWorkMarket().setAssignToFirstResource(workDTO.isAssignToFirstResource());
		work.getManageMyWorkMarket().setShowInFeed(workDTO.isShowInFeed());
		work.getManageMyWorkMarket().setBadgeShowClientName(workDTO.isBadgeShowClientName());
		work.getManageMyWorkMarket().setAutoPayEnabled(workDTO.isAutoPayEnabled());

		work.getManageMyWorkMarket().setUseRequirementSets(workDTO.getUseRequirementSets());
		work.getManageMyWorkMarket().setCustomFieldsEnabledFlag(workDTO.isCustomFieldsEnabledFlag());
		work.getManageMyWorkMarket().setCustomCloseOutEnabledFlag(workDTO.isCustomCloseOutEnabledFlag());
		work.getManageMyWorkMarket().setAssessmentsEnabled(workDTO.isAssessmentsEnabled());
		work.getManageMyWorkMarket().setPartsLogisticsEnabledFlag(workDTO.isPartsLogisticsEnabledFlag());

		work.setCheckinCallRequired(workDTO.isCheckinCallRequired());

		work.setIvrActive(workDTO.isIvrActive());

		Calendar scheduleFrom = DateUtilities.getCalendarFromISO8601(workDTO.getScheduleFromString());
		work.setScheduleFrom(scheduleFrom);
		work.setScheduleRangeFlag(workDTO.getIsScheduleRange());
		if (workDTO.getIsScheduleRange()) {
			Calendar scheduleThrough = DateUtilities.getCalendarFromISO8601(workDTO.getScheduleThroughString());
			work.setScheduleThrough(scheduleThrough);
			if (StringUtils.isNotBlank(workDTO.getAppointmentTimeString())) {
				Calendar apptTime = DateUtilities.getCalendarFromISO8601(workDTO.getAppointmentTimeString());
				Assert.state(DateUtilities.isInFuture(apptTime), "Appointment time should be in the future.");
				Assert.state(apptTime.before(scheduleThrough) && apptTime.after(scheduleFrom),
					"Appointment time is outside the time window.");
			}
		} else {
			work.setScheduleThrough(null);
		}

		if (work.isPricingEditable() && workDTO.getPricingStrategyId() != null && workDTO.getPricingStrategyId() > 0L) {
			PricingStrategy pricing = pricingService.findPricingStrategyById(workDTO.getPricingStrategyId());
			BeanUtilities.copyProperties(pricing, workDTO);
			if (workDTO.getUseMaxSpendPricingDisplayModeFlag() && !workDTO.isOfflinePayment()) {
				pricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, company.getId(), work.getId());
			}

			work.setPricingStrategy(pricing);

			BigDecimal newSpend = pricingService.calculateMaximumResourceCost(pricing);

			Assert.state(newSpend.compareTo(BigDecimal.ZERO) > -1, "Spend limit must be $0 or greater");
		}

		BeanUtilities.copyProperties(work.getManageMyWorkMarket(), workDTO);
		BeanUtilities.copyProperties(work, workDTO);

		// If statements are enabled, we don't care what the DTO says.
		// Take the correct values back from the company's configuration.
		if (company.getManageMyWorkMarket().getStatementsEnabled()) {
			work.getManageMyWorkMarket().setPaymentTermsDays(company.getManageMyWorkMarket().getPaymentTermsDays());
			work.getManageMyWorkMarket().setPaymentTermsEnabled(company.getManageMyWorkMarket().getPaymentTermsEnabled());
		}

		work.setTimeZone(findAssignmentTimeZone(work));
		return work;
	}

	@Override
	public boolean doesWorkerHaveWorkWithCompany(Long companyId, Long contractorUserId, List<String> statuses) {
		return workDAO.doesWorkerHaveWorkWithCompany(companyId, contractorUserId, statuses);
	}

	@Override
	public void updateWorkProperties(Long workId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		Assert.notNull(workId, "Work id must be provided");
		Assert.notNull(properties, "Properties must be provided");

		AbstractWork work = abstractWorkDAO.get(workId);

		Assert.notNull(work, "Unable to find work");

		if (properties.keySet().contains(WorkProperties.DESCRIPTION.getName())) {
			work.setDescription(properties.get(WorkProperties.DESCRIPTION.getName()));
		}

		if (properties.keySet().contains(WorkProperties.INSTRUCTIONS.getName())) {
			work.setInstructions(properties.get(WorkProperties.INSTRUCTIONS.getName()));
		}

		if (properties.keySet().contains(WorkProperties.RESOLUTION.getName())) {
			work.setResolution(properties.get(WorkProperties.RESOLUTION.getName()));
		}

		if (properties.keySet().contains(WorkProperties.EXTERNAL_ID.getName())) {
			if(work instanceof Work)
				((Work)work).getWorkUniqueId().setIdValue(properties.get(WorkProperties.EXTERNAL_ID.getName()));
			properties.remove(WorkProperties.EXTERNAL_ID.getName());
		}

		if (properties.keySet().contains("industry.id")) {
			Industry industry = invariantDataService.findIndustry(Long.parseLong(properties.get("industry.id")));

			Assert.notNull(industry);

			work.setIndustry(industry);

			properties.remove("industry.id");
		}

		if (properties.keySet().contains("clientCompany.id")) {
			ClientCompany clientCompany = null;
			String value = properties.get("clientCompany.id");

			if (StringUtils.isNotBlank(value) && !value.toLowerCase().equals("null")) {
				clientCompany = directoryService.findClientCompanyById(Long.parseLong(value));
				Assert.notNull(clientCompany);
			}

			work.setClientCompany(clientCompany);
			properties.remove("clientCompany.id");
		}

		if (properties.keySet().contains("address.id")) {
			Address address = addressService.findById(Long.parseLong(properties.get("address.id")));

			Assert.notNull(address);

			work.setAddress(address);
			work.setIsOnsiteAddress(true);

			properties.remove("address.id");
		}

		if (properties.keySet().contains("clientLocation.id")) {
			ClientLocation clientLocation = null;
			String value = properties.get("clientLocation.id");

			if (StringUtils.isNotBlank(value) && !value.toLowerCase().equals("null")) {
				clientLocation = directoryService.findClientLocationById(Long.parseLong(value));
				Assert.notNull(clientLocation, "Unable to find ClientLocation");
			}

			work.setLocation(clientLocation);
			if (clientLocation != null && clientLocation.getAddress() != null) {
				if (properties.keySet().contains("address.id")) {
					properties.remove("address.id");
				}
				work.setAddress(clientLocation.getAddress());
			}

			work.setIsOnsiteAddress(true);

			properties.remove("clientLocation.id");
		}

		if (properties.keySet().contains("scheduleRangeFlag")) {
			work.setScheduleRangeFlag(Boolean.parseBoolean(properties.get("scheduleRangeFlag")));
			properties.remove("scheduleRangeFlag");
		}

		if (properties.keySet().contains("scheduleFrom")) {
			work.setScheduleFrom(DateUtilities.getCalendarFromISO8601(properties.get("scheduleFrom")));
			properties.remove("scheduleFrom");
		}

		if (properties.keySet().contains("scheduleThrough")) {
			work.setScheduleThrough(DateUtilities.getCalendarFromISO8601(properties.get("scheduleThrough")));

			properties.remove("scheduleThrough");
		}

		if (properties.keySet().contains("buyerSupportUser.id")) {
			User user = userService.getUser(Long.parseLong(properties.get("buyerSupportUser.id")));

			Assert.notNull(user);

			work.setBuyerSupportUser(user);

			properties.remove("buyerSupportUser.id");
		}

		if (properties.keySet().contains("internalOwner.id")) {
			User user = userService.getUser(Long.parseLong(properties.get("internalOwner.id")));

			Assert.notNull(user);

			work.setBuyer(user);

			properties.remove("internalOwner.id");
		}

		if (properties.keySet().contains("serviceClientContact.id")) {

			String value = properties.get("serviceClientContact.id");

			if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value) && !value.toLowerCase().equals("null")) {
				work.setServiceClientContactId(Long.parseLong(value));
			}

			properties.remove("serviceClientContact.id");
		}

		if (properties.keySet().contains("secondaryServiceClientContact.id")) {

			String value = properties.get("secondaryServiceClientContact.id");

			if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value) && !value.toLowerCase().equals("null")) {
				work.setSecondaryServiceClientContactId(Long.parseLong(value));
			}

			properties.remove("secondaryServiceClientContact.id");
		}

		if (!properties.isEmpty()) {
			BeanUtilities.updateProperties(work, properties);
		}
	}

	@Override
	public <T extends AbstractWork> T findWork(Long workId) {
		return findWork(workId, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractWork> T findWork(Long workId, boolean loadEverything) {
		Assert.notNull(workId);
		AbstractWork work = abstractWorkDAO.findById(workId);
		if (work != null && loadEverything) {
			Hibernate.initialize(work.getProject());
			Hibernate.initialize(work.getPriceHistory());
			Hibernate.initialize(work.getWorkSubStatusTypeAssociations());
			Hibernate.initialize(work.getWorkCustomFieldGroupAssociations());
			Hibernate.initialize(work.getAssessments());
			if (work instanceof WorkBundle) {
				Hibernate.initialize(((WorkBundle) work).getBundle());
			}
		}
		return (T) work;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractWork> T findWorkForInvitation(Long workId) {
		Assert.notNull(workId);
		AbstractWork work = abstractWorkDAO.findById(workId);
		if (work != null) {
			// Has side-effect of initializing all the custom fields data needed for display in Work notifs
			work.getWorkCustomFieldsForEmailDisplay();
			if (work instanceof WorkBundle) {
				Hibernate.initialize(((WorkBundle) work).getBundle());
			}
		}
		return (T) work;
	}

	@Override
	public <T extends AbstractWork> T findWorkByWorkNumber(String workNumber) {
		return findWorkByWorkNumber(workNumber, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractWork> T findWorkByWorkNumber(String workNumber, boolean loadEverything) {
		Assert.notNull(workNumber);
		AbstractWork work = abstractWorkDAO.findByWorkNumber(workNumber);

		if (loadEverything && work != null) {
			Hibernate.initialize(work.getProject());
			Hibernate.initialize(work.getPriceHistory());
			Hibernate.initialize(work.getWorkSubStatusTypeAssociations());
			Hibernate.initialize(work.getWorkCustomFieldGroupAssociations());
		}

		return (T) work;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AbstractWork> findWorkByWorkNumbers(List<String> workNumbers) {
		Assert.notNull(workNumbers);
		List<AbstractWork> works = abstractWorkDAO.findByWorkNumbers(workNumbers);
		for (AbstractWork work : works) {
			if (work != null) {
				Hibernate.initialize(work.getProject());
				Hibernate.initialize(work.getPriceHistory());
				Hibernate.initialize(work.getWorkSubStatusTypeAssociations());
				Hibernate.initialize(work.getWorkCustomFieldGroupAssociations());
			}
		}
		return works;
	}

	@Override
	public WorkPagination findWorkByWorkResource(Long userId, WorkPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		return workDAO.findByWorkResource(userId, pagination);
	}

	@Override
	public WorkPagination findWorkByBuyerAndWorkResource(Long buyerId, Long resourceUserId, WorkPagination pagination) {
		Assert.notNull(buyerId);
		Assert.notNull(resourceUserId);
		Assert.notNull(pagination);
		return workDAO.findWorkByBuyerAndWorkResource(buyerId, resourceUserId, pagination);
	}

	@Override
	public WorkAggregatesDTO countWorkByCompany(Long companyId) {
		Assert.notNull(companyId);
		WorkSearchDataPagination pagination = new WorkSearchDataPagination();
		WorkAggregatesDTO dto = workReportService.generateWorkDashboardStatusAggregate(companyId, pagination);
		dto.setCountForStatus(WorkStatusType.INPROGRESS_PREFUND, workReportService.countInprogressAssignmentsPrefundByCompany(companyId, pagination));
		dto.setCountForStatus(WorkStatusType.INPROGRESS_PAYMENT_TERMS, workReportService.countInprogressAssignmentsWithPaymentTermsByCompany(companyId, pagination));
		return dto;
	}

	@Override
	public List<ConstraintViolation> repriceWork(Long workId, WorkDTO workDTO) {
		return repriceWork(workId, workDTO, false);
	}

	@Override
	public List<ConstraintViolation> repriceWork(Long workId, WorkDTO workDTO, AbstractWorkNegotiation abstractWorkNegotiation) {
		Assert.notNull(abstractWorkNegotiation);

		//removed constraint on "allowLowerCostOnActive"
		return repriceWork(workId, workDTO, true);
	}

	private List<ConstraintViolation> repriceWork(Long workId, WorkDTO workDTO, boolean allowLowerCostOnActive) {
		Assert.notNull(workId);
		Work work = workDAO.get(workId);
		Assert.notNull(work);

		List<WorkResource> workResources = Lists.newArrayList();
		if (work.isSent()) {
			workResources.addAll(workResourceDAO.findNotDeclinedForWork(workId));
		} else {
			workResources.add(findActiveWorkResource(work.getId()));
		}

		PricingStrategy newPricing = pricingService.findPricingStrategyById(workDTO.getPricingStrategyId());
		BeanUtilities.copyProperties(newPricing.getFullPricingStrategy(), workDTO);
		if (workDTO.getUseMaxSpendPricingDisplayModeFlag()) {
			newPricing = pricingService.adjustPricingByCompanyFeePercentage(newPricing, work.getCompany().getId(), work.getId());
		}
		work.getManageMyWorkMarket().setUseMaxSpendPricingDisplayModeFlag(workDTO.getUseMaxSpendPricingDisplayModeFlag());

		// if an expense reimbursement or bonus increase, copy the additional expenses and bonus
		if (workDTO instanceof WorkNegotiationDTO) {
			WorkNegotiationDTO negotiationDTO = (WorkNegotiationDTO) workDTO;

			newPricing.getFullPricingStrategy().setAdditionalExpenses((negotiationDTO.getAdditionalExpenses() > 0) ?
				BigDecimal.valueOf(negotiationDTO.getAdditionalExpenses()) :
				work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());

			newPricing.getFullPricingStrategy().setBonus((negotiationDTO.getBonus() > 0) ?
				BigDecimal.valueOf(negotiationDTO.getBonus()) :
				work.getPricingStrategy().getFullPricingStrategy().getBonus());
		} else {
			newPricing.getFullPricingStrategy().setAdditionalExpenses(work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());
			newPricing.getFullPricingStrategy().setBonus(work.getPricingStrategy().getFullPricingStrategy().getBonus());
		}

		List<ConstraintViolation> violations = workValidationService.validateRepriceWork(workId, newPricing, workResources, allowLowerCostOnActive);
		if (!violations.isEmpty()) {
			return violations;
		}

		// Store the price history and update the current values on the work
		WorkPrice price = new WorkPrice();
		price.setWork(work);
		price.setPricingStrategy(work.getPricingStrategy());
		workPriceDAO.saveOrUpdate(price);

		work.getPriceHistory().add(price);
		work.setPricingStrategy(newPricing);

		// NOTE If a draft, skip resource notifications and account register
		// actions.
		if (work.isDraft()) {
			return violations;
		}

		// TODO Authorization only changes things if a lane fee has changed.
		// Needs to account for the changed assignment price.
		accountRegisterAuthorizationService.repriceWork(work);
		workAuditService.auditAndReindexWork(workActionRequestFactory.create(work, work.getBuyer().getId(), workDTO.getOnBehalfOfId(), authenticationService.getMasqueradeUserId(), WorkAuditType.REPRICE));

		return violations;
	}

	@Override
	public Boolean isWorkInProgress(Long workId) {
		Assert.notNull(workId);

		AbstractWork work = abstractWorkDAO.get(workId);

		Assert.notNull(work);

		if (!work.isActive()) {
			return false;
		}

		WorkResource activeResource = findActiveWorkResource(workId);
		if (activeResource == null) {
			return false;
		}

		// if the assignment has been re-scheduled since the latest check in then
		// it should still be in the assigned status
		WorkRescheduleNegotiation workRescheduleNegotiation = workNegotiationService.findLatestApprovedRescheduleRequestForWork(workId);
		boolean hasResourceCheckedIn = activeResource.isCheckedIn();

		if (hasResourceCheckedIn && workRescheduleNegotiation != null) {
			WorkResourceTimeTracking workResourceTimeTracking = workResourceTimeTrackingDAO.findLatestByWorkResource(activeResource.getId());
			hasResourceCheckedIn = workResourceTimeTracking.getCheckedInOn().after(workRescheduleNegotiation.getApprovedOn());
		}

		// Don't just change this logic - there are various queries that mirror the same logic
		// Look at the other changes in this commit to find them
		return hasResourceCheckedIn ||
			(!work.isCheckinRequired() && !work.isCheckinCallRequired() && DateUtilities.isInPast(work.getScheduleFrom()));
	}

	@Override
	public Boolean isWorkShownInFeed(Long workId) {
		Assert.notNull(workId);

		AbstractWork work = abstractWorkDAO.get(workId);
		return work.isShownInFeed();
	}

	@Override
	public boolean doesClientCompanyHaveActiveAssignments(Long clientCompanyId) {
		Assert.notNull(clientCompanyId);
		return workDAO.doesClientCompanyHaveActiveAssignments(clientCompanyId);
	}

	@Override
	public List<Long> findWorkerIdsForWork(Long workId) {
		Assert.notNull(workId);
		return workResourceDAO.findWorkerIdsForWork(workId);
	}

	@Override
	public WorkResource findActiveWorkResource(Long workId) {
		Assert.notNull(workId);
		return workResourceDAO.findActiveWorkResource(workId);
	}

	@Override
	public Long findActiveWorkerId(Long workId) {
		Assert.notNull(workId);
		return workResourceDAO.findActiveWorkerId(workId);
	}

	@Override
	public WorkResourcePagination findWorkResources(Long workId, WorkResourcePagination pagination) {
		Assert.notNull(workId);
		return workResourceDAO.findByWork(workId, pagination);
	}

	@Override
	public WorkResource findWorkResource(Long userId, Long workId) {
		Assert.notNull(userId);
		Assert.notNull(workId);
		return workResourceDAO.findByUserAndWork(userId, workId);
	}

	@Override
	public boolean isUserWorkResourceForWork(Long userId, Long workId) {
		return (findWorkResource(userId, workId) != null);
	}

	@Override
	public boolean isUserActiveResourceForWork(Long userId, Long workId) {
		WorkResource resource = findActiveWorkResource(workId);
		return resource != null && resource.getUser().getId().equals(userId) && resource.isActive() && resource.isAssignedToWork();

	}

	@Override
	public boolean isUserActiveResourceForWorkWithAssessment(Long userId, Long assessmentId) {
		return workResourceDAO.isUserActiveResourceForWorkWithAssessment(userId, assessmentId);
	}

	@Override
	public boolean isWorkResourceConfirmationValid(Long userId, Long workId) {
		Assert.notNull(userId);
		Assert.notNull(workId);
		AbstractWork work = abstractWorkDAO.get(workId);
		Assert.notNull(work);

		if (work.isActive() && work.isResourceConfirmationRequired()) {
			boolean isActiveResource = isUserActiveResourceForWork(userId, workId);

			logger.info(String.format("Work is active with confirmation required, user %d is: active resource = %b",
				userId, isActiveResource));

			if (isActiveResource) {
				// Check that the user hasn't confirmed
				WorkResource resource = findWorkResource(userId, workId);
				logger.info("Resource is confirmed: " + resource.isConfirmed());
				return (!resource.isConfirmed());
			}
		}
		return false;
	}

	@Override
	public WorkResource confirmWorkResource(Long userId, Long workId) {

		Assert.isTrue(isWorkResourceConfirmationValid(userId, workId),
			"Invalid action. The assignment may not require confirmation or the user is not the active resource.");

		WorkResource workResource = findWorkResource(userId, workId);

		workResource.setConfirmed(true);
		workResource.setConfirmedOn(DateUtilities.getCalendarNow());

		workResource.getWork().setConfirmed(true);

		workSubStatusService.addSystemSubstatusAndResolve(workResource.getUser(), workId, WorkSubStatusType.RESOURCE_CONFIRMED,
			WorkSubStatusType.RESOURCE_NOT_CONFIRMED);

		// TODO: move these outside the transaction
		userNotificationService.onWorkResourceConfirmed(workResource.getId());
		workAuditService.auditAndReindexWork(workActionRequestFactory.create(workResource.getWork(), workResource.getUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.RESOURCE_CONFIRM));

		webHookEventService.onWorkConfirmed(workId, workResource.getWork().getCompany().getId());
		return workResource;
	}

	@Override
	public TimeTrackingResponse checkInActiveResource(TimeTrackingRequest timeTrackingRequest) {
		Assert.notNull(timeTrackingRequest);
		Assert.notNull(timeTrackingRequest.getDate());

		long workId = timeTrackingRequest.getWorkId();
		WorkResource workResource = findActiveWorkResource(workId);
		Assert.notNull(workResource, "Unable to find Active Resource");

		TimeTrackingResponse checkinResponse = new TimeTrackingResponse();

		// make sure it's not in the future
		Calendar date = DateUtilities.getCalendarInUTC(timeTrackingRequest.getDate());
		if (DateUtilities.isInFutureWithBuffer(date, 5, Constants.MINUTE)) {
			checkinResponse.setMessage(messageHelper.getMessage("assignment.update_checkin.in_future"));
			return checkinResponse;
		}

		WorkResourceTimeTracking timeTracker;
		boolean isUpdate = false;

		if (timeTrackingRequest.getTimeTrackingId() != null) {
			timeTracker = workResourceTimeTrackingDAO.findById(timeTrackingRequest.getTimeTrackingId());
			WorkResourceTimeTracking timeTrackerByWorkId = workResourceTimeTrackingDAO.findLatestByWorkResource(workResource.getId());

			if (!compareWorkById(timeTracker.getWorkResource().getWork(), timeTrackerByWorkId.getWorkResource().getWork())) {
				checkinResponse.setMessage(messageHelper.getMessage("assignment.update_checkin" +
						".check_in_out_id_and_assignment_id_refer_to_different_work"));
				return checkinResponse;
			}

			isUpdate = true;
			Assert.notNull(timeTracker, "Unable to find Time Tracking record");

			// make sure it's BEFORE the corresponding check-out time, if any
			if (timeTracker.getCheckedOutOn() != null && DateUtilities.isBefore(timeTracker.getCheckedOutOn(), date)) {
				checkinResponse.setMessage(messageHelper.getMessage("assignment.update_checkin.after_checkout"));
				return checkinResponse;
			}
		} else {
			WorkResourceTimeTracking lastTimeTracker = workResourceTimeTrackingDAO.findLatestByWorkResource(workResource.getId());
			if (lastTimeTracker != null && lastTimeTracker.isCheckedIn() && !lastTimeTracker.isCheckedOut()) {
				checkinResponse.setMessage(messageHelper.getMessage("assignment.create_checkin.multiple"));
				return checkinResponse;
			}
			timeTracker = new WorkResourceTimeTracking(workResource);
		}

		timeTracker.setCheckedInOn(date);
		timeTracker.setCheckedInBy(authenticationService.getCurrentUser());
		timeTracker.setLatitudeIn(timeTrackingRequest.getLatitude());
		timeTracker.setLongitudeIn(timeTrackingRequest.getLongitude());
		if (!isUpdate && (timeTrackingRequest.getLatitude() != null && timeTrackingRequest.getLongitude() != null)) {
			timeTrackingRequest.setDistance(GeoUtilities.distanceInMiles(
				timeTrackingRequest.getLatitude(),
				timeTrackingRequest.getLongitude(),
				workResource.getWork().getLocation().getAddress().getLatitude().doubleValue(),
				workResource.getWork().getLocation().getAddress().getLongitude().doubleValue()
			));
			timeTracker.setDistanceIn(timeTrackingRequest.getDistance());
		}
		workResourceTimeTrackingDAO.saveOrUpdate(timeTracker);
		workResource.setCheckedIn(true);
		Long timeTrackingId = timeTracker.getId();

		// Snapshot the in-progress time
		if (workResource.getWork().isCheckinRequired() || workResource.getWork().isCheckinCallRequired()) {
			WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(workId);
			if (milestones != null && milestones.getInProgressOn() == null) {
				milestones.setInProgressOn(date);
			}
			summaryService.saveWorkHistorySummary(workResource.getWork(), workResource, WorkStatusType.INPROGRESS);
		}

		if (!isUpdate) {
			workSubStatusService.addSystemSubstatusAndResolve(authenticationService.getCurrentUser(), workId, WorkSubStatusType.RESOURCE_CHECKED_IN,
				WorkSubStatusType.RESOURCE_NO_SHOW, WorkSubStatusType.RESOURCE_CONFIRMED, WorkSubStatusType.RESOURCE_CHECKED_OUT);
		}

		WorkActionRequest workActionRequest = workActionRequestFactory.create(workResource.getWork(), workResource.getUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.RESOURCE_ACTIVE_CHECK_IN);
		workAuditService.auditWork(workActionRequest);

		//Check if the date is in the THRESHOLD of being Late
		Calendar maxAppointmentDate = calculateMaxAppointmentDateLatenessThreshold(workResource.getWork());
		if (date.before(maxAppointmentDate)) {
			workResourceService.ignoreWorkResourceLabel(workResource.getId(), WorkResourceLabelType.LATE);
		}

		checkinResponse.setTimeTracking(timeTracker);
		checkinResponse.setSuccessful(true);

		if (!isUpdate) {
			userNotificationService.onWorkResourceCheckedIn(workResource.getWork());
		}

		webHookEventService.onCheckInActiveResource(workId, workResource.getWork().getCompany().getId(), timeTrackingId);
		return checkinResponse;
	}

	public Boolean compareWorkById(final Work workReferredByTimeTrackingId, final Work workReferredByWorkId) {
		return workReferredByTimeTrackingId.getId() == workReferredByWorkId.getId();
	}

	@Override
	public TimeTrackingResponse checkOutActiveResource(TimeTrackingRequest timeTrackingRequest) {
		Assert.notNull(timeTrackingRequest);
		Assert.notNull(timeTrackingRequest.getDate());

		WorkResource workResource = findActiveWorkResource(timeTrackingRequest.getWorkId());
		Assert.notNull(workResource, "Unable to find Active Resource");

		Work work = workResource.getWork();

		TimeTrackingResponse checkoutResponse = new TimeTrackingResponse();

		// make sure it's not in the future
		Calendar date = DateUtilities.getCalendarInUTC(timeTrackingRequest.getDate());
		if (DateUtilities.isInFutureWithBuffer(date, 5, Constants.MINUTE)) {
			checkoutResponse.setMessage(messageHelper.getMessage("assignment.update_checkout.in_future"));
			logger.debug(String.format("[checkouts] workid %s: Tried to check out in future at %s when the current time is %s",
				timeTrackingRequest.getWorkId(), DateUtilities.format("yyyy/MM/dd hh:mm:ss", date), DateUtilities.format("yyyy/MM/dd hh:mm:ss", DateUtilities.getCalendarNowUtc())));
			return checkoutResponse;
		}

		WorkResourceTimeTracking timeTracker;
		if (timeTrackingRequest.getTimeTrackingId() != null) {
			timeTracker = workResourceTimeTrackingDAO.findById(timeTrackingRequest.getTimeTrackingId());
		} else {
			timeTracker = workResourceTimeTrackingDAO.findLatestByWorkResource(workResource.getId());
		}

		Assert.notNull(timeTracker, "Unable to find Time Tracking record");

		boolean isUpdate = timeTracker.getCheckedOutOn() != null;

		// make sure it's AFTER the corresponding check-in time
		if (timeTracker.getCheckedInOn() != null && DateUtilities.isBefore(date, timeTracker.getCheckedInOn())) {
			checkoutResponse.setMessage(messageHelper.getMessage("assignment.update_checkout.before_checkin"));
			return checkoutResponse;
		}

		if (StringUtils.isBlank(timeTrackingRequest.getNoteOnCheckOut())) {
			if (work.getCheckoutNoteRequiredFlag() && timeTracker.getNote() == null) {
				checkoutResponse.setMessage(messageHelper.getMessage("assignment.add_checkout_note.empty"));
				return checkoutResponse;
			}
		} else {
			NoteDTO dto = new NoteDTO();
			dto.setContent(timeTrackingRequest.getNoteOnCheckOut());

			Note note = workNoteService.addNoteToWork(work.getId(), dto);
			if (note == null) {
				checkoutResponse.setMessage(messageHelper.getMessage("assignment.add_checkout_note.exception"));
				return checkoutResponse;
			}
			timeTracker.setNote(note);
		}

		timeTracker.setCheckedOutOn(date);
		timeTracker.setCheckedOutBy(authenticationService.getCurrentUser());

		if (!isUpdate) {
			workSubStatusService.addSystemSubstatusAndResolve(workResource.getUser(), timeTrackingRequest.getWorkId(), WorkSubStatusType.RESOURCE_CHECKED_OUT,
				WorkSubStatusType.RESOURCE_CHECKED_IN);
		}

		// TODO: move this outside the transaction
		workAuditService.auditAndReindexWork(workActionRequestFactory.create(workResource.getWork(), workResource.getUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.RESOURCE_ACTIVE_CHECK_OUT));

		timeTracker.setLatitudeOut(timeTrackingRequest.getLatitude());
		timeTracker.setLongitudeOut(timeTrackingRequest.getLongitude());

		if (!isUpdate && (timeTrackingRequest.getLatitude() != null && timeTrackingRequest.getLongitude() != null)) {
			timeTrackingRequest.setDistance(GeoUtilities.distanceInMiles(
				timeTrackingRequest.getLatitude(),
				timeTrackingRequest.getLongitude(),
				workResource.getWork().getLocation().getAddress().getLatitude().doubleValue(),
				workResource.getWork().getLocation().getAddress().getLongitude().doubleValue()
			));
			timeTracker.setDistanceOut(timeTrackingRequest.getDistance());
		}
		timeTracker.setDistanceOut(timeTrackingRequest.getDistance());
		checkoutResponse.setTimeTracking(timeTracker);
		checkoutResponse.setSuccessful(true);

		if (timeTrackingRequest.isNotifyOnCheckOut()) {
			if (!isUpdate) {
				userNotificationService.onWorkResourceCheckedOut(workResource.getWork());
			}
			webHookEventService.onCheckOutActiveResource(timeTrackingRequest.getWorkId(), workResource.getWork().getCompany().getId(), timeTrackingRequest.getTimeTrackingId());
		}

		return checkoutResponse;
	}

	@Override
	@Nullable
	public void deleteCheckInResource(Long workId, Long timeTrackingId) {
		Assert.notNull(workId);
		WorkResource workResource = findActiveWorkResource(workId);
		Assert.notNull(workResource, "Unable to find Active Resource");

		WorkResourceTimeTracking timeTracker;
		if (timeTrackingId != null) {
			timeTracker = workResourceTimeTrackingDAO.findById(timeTrackingId);
		} else {
			timeTracker = workResourceTimeTrackingDAO.findLatestByWorkResource(workResource.getId());
		}

		Assert.notNull(timeTracker, "Unable to find Time Tracking record");

		WorkResourceTimeTracking latest = workResourceTimeTrackingDAO.findLatestByWorkResource(workResource.getId());
		boolean isCheckedIn = false;
		if (latest != null && !latest.getDeleted()) {
			isCheckedIn = latest.isCheckedIn() && !latest.isCheckedOut();
		}

		boolean isDeletingLatest = latest != null && latest.getId().equals(timeTrackingId);

		if (isCheckedIn && isDeletingLatest) {
			workSubStatusService.addSystemSubstatusAndResolve(workResource.getUser(), workId, WorkSubStatusType.RESOURCE_CHECKED_OUT,
				WorkSubStatusType.RESOURCE_CHECKED_IN);
		}

		timeTracker.setDeleted(true);
	}

	@Override
	@Nullable
	public void deleteCheckOutResource(Long workId, Long timeTrackingId) {
		Assert.notNull(workId);
		WorkResource workResource = findActiveWorkResource(workId);
		Assert.notNull(workResource, "Unable to find Active Resource");

		WorkResourceTimeTracking timeTracker;
		if (timeTrackingId != null) {
			timeTracker = workResourceTimeTrackingDAO.findById(timeTrackingId);
		} else {
			timeTracker = workResourceTimeTrackingDAO.findLatestByWorkResource(workResource.getId());
		}

		Assert.notNull(timeTracker, "Unable to find Time Tracking record");

		timeTracker.setCheckedOutBy(null);
		timeTracker.setCheckedOutOn(null);
		workSubStatusService.addSystemSubstatusAndResolve(workResource.getUser(), workId, WorkSubStatusType.RESOURCE_CHECKED_IN,
			WorkSubStatusType.RESOURCE_CHECKED_OUT);
	}


	@Override
	public WorkResourceTimeTracking findLatestTimeTrackRecordByWorkResource(Long workResourceId) {
		Assert.notNull(workResourceId);
		return workResourceTimeTrackingDAO.findLatestByWorkResource(workResourceId);
	}

	@Override
	public List<WorkResourceTimeTracking> findTimeTrackingByWorkResource(Long workResourceId) {
		Assert.notNull(workResourceId);
		return workResourceTimeTrackingDAO.findAllByWorkResourceId(workResourceId);
	}

	@Override
	public boolean isWorkResourceCheckInValid(Long userId, Long workId) {
		Assert.notNull(userId);
		Assert.notNull(workId);

		AbstractWork work = abstractWorkDAO.get(workId);
		Assert.notNull(work);

		if (work.isActive() && (work.isCheckinRequired() || work.isCheckinCallRequired())) {
			if (isUserActiveResourceForWork(userId, workId)) {
				// Check that the user hasn't checked In
				WorkResource workResource = findWorkResource(userId, workId);
				return (!workResource.isCheckedIn());
			}
		}

		return false;
	}

	@Override
	public boolean isActiveResourceCurrentlyCheckedIn(Long workId) {
		Assert.notNull(workId);
		AbstractWork work = abstractWorkDAO.get(workId);

		return work.isActive() && isResourceCurrentlyCheckedIn((Work) work);
	}

	@Override
	public boolean isResourceCurrentlyCheckedIn(Work work) {
		Assert.notNull(work);

		WorkResource workResource = findActiveWorkResource(work.getId());
		WorkResourceTimeTracking latestCheckInOut = findLatestTimeTrackRecordByWorkResource(workResource.getId());

		return latestCheckInOut != null && (latestCheckInOut.isCheckedIn() && !latestCheckInOut.isCheckedOut());
	}

	@Override
	public void resendInvitations(Long workId, List<Long> resourcesIds) {
		Assert.notNull(workId);
		Work work = workDAO.get(workId);
		Assert.notNull(work, "Unable to find work");

		if (work.isSent()) {
			if (resourcesIds != null && isNotEmpty(resourcesIds)) {
				resendInvitations(work, resourcesIds);
			} else {
				resendInvitationsToAllInvitedResources(work);
			}
			WorkActionRequest workActionRequest = workActionRequestFactory.create(
				work, authenticationService.getCurrentUserId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.RESEND
			);
			workAuditService.auditWork(workActionRequest);
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
		}
	}

	private void resendInvitations(Work work, List<Long> resourcesIds) {
		if (CollectionUtils.isEmpty(resourcesIds)) return;
		HashSet<Long> inviteeIds = new HashSet<>(resourcesIds);
		for (Long resourceId : resourcesIds) {
			WorkResource workResource = findWorkResource(resourceId, work.getId());
			resolveWorkResourceStatus(workResource);  // rolls back declined or canceled assignments
			List<Long> dispatcherIds = workResourceService.getAllDispatcherIdsForWorker(resourceId);
			if (!dispatcherIds.isEmpty()) {
				inviteeIds.addAll(dispatcherIds);
			}
		}
		userNotificationService.onWorkInvitation(work.getId(), Lists.newArrayList(inviteeIds), false);
	}

	private void resendInvitationsToAllInvitedResources(Work work) {
		Collection<WorkNegotiation> offers = workNegotiationService.findAllNegotiationsByWorkId(work.getId());
		List<WorkQuestionAnswerPair> questions = workQuestionAnswerPairDAO.findByWork(work.getId());

		Collection<WorkResource> resources = workResourceService.findAllResourcesForWork(work.getId());
		List<Long> resourceIds = Lists.newArrayListWithExpectedSize(resources.size());
		for (WorkResource resource : resources) {
			if (!resource.isDeclined() && hasNoOffers(resource, offers) && hasNoQuestions(resource, questions)) {
				resourceIds.add(resource.getUser().getId());
				resolveWorkResourceStatus(resource);
			}
		}
		userNotificationService.onWorkInvitation(work.getId(), resourceIds, false);
	}

	private boolean hasNoQuestions(WorkResource resource, List<WorkQuestionAnswerPair> questions) {

		if (isEmpty(questions)) {
			return true;
		}
		for (WorkQuestionAnswerPair question : questions) {
			if (question.getQuestionerId().equals(resource.getUser().getId())) {
				return false;
			}
		}
		return true;
	}

	private boolean hasNoOffers(WorkResource resource, Collection<WorkNegotiation> offers) {

		if (isEmpty(offers)) {
			return true;
		}
		for (WorkNegotiation offer : offers) {
			if (offer.getRequestedBy().getId().equals(resource.getUser().getId())) {
				return false;
			}
		}
		return false;
	}

	@Override
	public Response resendInvitationsAsync(Long workId, List<Long> resourcesIds) {
		if (isNotEmpty(resourcesIds) && workId != null) {
			eventRouter.sendEvent(new WorkResendInvitationsEvent(workId, resourcesIds));
			return new BaseResponse(BaseStatus.SUCCESS);
		}
		return new BaseResponse(BaseStatus.FAILURE);
	}

	@Override
	public Response resendInvitationsAsync(String workNumber) {
		if (StringUtils.isNotBlank(workNumber)) {
			Work work = findWorkByWorkNumber(workNumber);
			if (work != null && work.isSent()) {
				eventRouter.sendEvent(new WorkResendInvitationsEvent(work.getId()));
				return new BaseResponse(BaseStatus.SUCCESS);
			}
		}
		return new BaseResponse(BaseStatus.FAILURE);
	}


	@Override
	public void remindResourceToComplete(Long workId, String message) {
		Assert.notNull(workId);
		Assert.notNull(message);
		Work work = workDAO.get(workId);
		Assert.notNull(work);

		WorkResource resource = findActiveWorkResource(workId);
		resource.setLastRemindedToCompleteOn(DateUtilities.getCalendarNow());

		WorkNote note = new WorkNote(message, work, PrivacyType.PUBLIC);
		workNoteService.saveOrUpdate(note);

		userNotificationService.onWorkRemindResourceToComplete(work, resource.getUser(), note);

		workAuditService.auditWork(workActionRequestFactory.create(work, WorkAuditType.SEND_REMINDER_TO_COMPLETE));
	}

	@Override
	public void clearGroupsForWork(Long workId) {
		Assert.notNull(workId);

		for (WorkGroupAssociation a : workGroupAssociationDAO.findAllByWork(workId)) {
			a.setDeleted(true);
			workGroupAssociationDAO.saveOrUpdate(a);
		}
	}

	@Override
	public void addFirstToAcceptGroupsForWork(Collection<Long> groupIds, Long workId) {
		Assert.notNull(workId);

		for (Long groupId : groupIds){
			addGroupToWork(groupId, workId, true);
		}
	}

	@Override
	public void addGroupsForWork(Collection<Long> groupIds, Long workId) {
		Assert.notNull(workId);

		for (Long groupId : groupIds){
			addGroupToWork(groupId, workId, false);
		}
	}

	private void addGroupToWork(Long groupId, Long workId, boolean assignToFirstToAccept) {
		Assert.notNull(groupId);
		Assert.notNull(workId);

		WorkGroupAssociation a = workGroupAssociationDAO.findByWorkAndGroupDeleted(workId, groupId);
		if (a == null) {
			a = new WorkGroupAssociation(abstractWorkDAO.get(workId), userGroupDAO.get(groupId), assignToFirstToAccept);
		}
		else {
			a.setDeleted(false);
			a.setAssignToFirstToAccept(assignToFirstToAccept);
		}

		workGroupAssociationDAO.saveOrUpdate(a);
	}

	@Override
	public List<Work> findAllWorkByProject(Long projectId) {
		Assert.notNull(projectId);
		return workDAO.findAllWorkByProject(projectId);
	}

	@Override
	public List<Work> findAllWorkByProjectByStatus(Long projectId, String... status) {
		Assert.notNull(projectId);
		Assert.notEmpty(status);
		return workDAO.findAllWorkByProjectByStatus(projectId, status);
	}

	/**
	 * Adds the paid workers to the auto created group My Paid Resources. The group is a Company group.
	 * <p/>
	 * Refer to: WORK-166
	 *
	 * @param workId
	 * @param workResourceId
	 * @
	 */
	@Override
	public void updateMyPaidResourcesGroup(Long workId, Long workResourceId, User actor) {
		Work work = findWork(workId);
		WorkResource resource = findWorkResourceById(workResourceId);
		if (work != null && resource != null) {
			UserGroup companyGroup = userGroupService.findOrCreateCompanyGroup(work.getCompany().getId(), Constants.MY_PAID_RESOURCES_GROUP_NAME, actor, Constants.LEGACY_MY_PAID_RESOURCES_GROUP_NAME);
			userGroupService.applyToGroup(companyGroup.getId(), resource.getUser().getId());
		}
	}

	@Override
	public WorkResource findWorkResourceById(Long workResourceId) {
		return workResourceDAO.findById(workResourceId);
	}

	@Override
	public String createCalendar(Long userId, Long workId) throws IOException, ValidationException {
		return calendarService.createWorkCalendar(userId, workId);
	}

	private List<WorkContext> getWorkContext(AbstractWork work, Long userId) {
		User user = userService.getUser(userId);
		Assert.notNull(user, "Unable to find user");
		return getWorkContext(work, user);
	}

	@Override
	public List<WorkContext> getWorkContext(long workId, long userId) {
		AbstractWork work = abstractWorkDAO.get(workId);
		Assert.notNull(work, "Unable to find assignment");
		User user = userService.getUser(userId);
		Assert.notNull(user, "Unable to find user");
		return getWorkContext(work, user);
	}

	@Override
	public boolean isAuthorizedToAdminister(Long userId, Long userCompanyId, String workNumber) {
		Assert.notNull(userId);
		Assert.notNull(userCompanyId);
		Assert.notNull(workNumber);

		Work work = workDAO.findWorkByWorkNumber(workNumber);
		Assert.notNull(work);

		return work.getBuyer().getId().equals(userId) ||
			(work.getCompany().getId().equals(userCompanyId) &&
				authenticationService.authorizeUserByAclPermission(userId, Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS));
	}

	@Override
	public List<WorkContext> getWorkContext(AbstractWork work, User user) {
		Assert.notNull(work, "Work is required");
		Assert.notNull(user, "User is required");
		List<WorkContext> context = new ArrayList<>();

		WorkResource invited = findWorkResource(user.getId(), work.getId());
		WorkResource active = findActiveWorkResource(work.getId());

		if (work.getBuyer().getId().equals(user.getId())) {
			context.add(WorkContext.OWNER);
		} else if (work.getCompany().getId().equals(user.getCompany().getId())) {
			context.add(WorkContext.COMPANY_OWNED);
		}

		if (invited != null) {
			if (invited.isAssignedToWork()) {
				context.add(WorkContext.ACTIVE_RESOURCE);
			} else if (invited.isCancelled()) {
				context.add(WorkContext.CANCELLED_RESOURCE);
			} else if (invited.isDeclined()) {
				context.add(WorkContext.DECLINED_RESOURCE);
			} else if (active != null) {
				context.add(WorkContext.INVITED_INACTIVE);
			} else {
				context.add(WorkContext.INVITED);
			}
		}

		// Checking company wide context assigned resource
		if (context.isEmpty()) {
			if (active != null && user.getCompany().getId().equals(active.getUser().getCompany().getId())) {
				context.add(WorkContext.ASSIGNED_COMPANY);
			} else {
				context.add(WorkContext.UNRELATED);
			}
		}

		User currentUser = authenticationService.getCurrentUser();
		if (currentUser != null && userRoleService.isInternalUser(currentUser)) {
			context.add(WorkContext.WORK_MARKET_INTERNAL);
		}

		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(user.getId());
		boolean isDispatcher = personaPreferenceOptional.isPresent() && personaPreferenceOptional.get().isDispatcher();
		boolean isUserDispatcherAndFromActiveResourceCompany = false;
		boolean isAtLeastOneWorkerFromUserCompanyInvitedToWorkAndUserIsDispatcher = false;
		boolean isVendorInvitedToWork = false;

		// To avoid expensive (and sometimes unnecessary calls to the DB) I added extra logic to short circuit these boolean calculations
		if (isDispatcher) {
			isUserDispatcherAndFromActiveResourceCompany = active != null && active.getUser().getCompany().getId().equals(user.getCompany().getId());
			if (!isUserDispatcherAndFromActiveResourceCompany && active == null) {
				isAtLeastOneWorkerFromUserCompanyInvitedToWorkAndUserIsDispatcher = workResourceDAO.isAtLeastOneWorkerFromCompanyInvitedToWork(currentUser.getCompany().getId(), work.getId());
				if (!isAtLeastOneWorkerFromUserCompanyInvitedToWorkAndUserIsDispatcher) {
					isVendorInvitedToWork = vendorService.isVendorInvitedToWork(user.getCompany().getId(), work.getId());
				}
			}
		}

		if (isUserDispatcherAndFromActiveResourceCompany || isAtLeastOneWorkerFromUserCompanyInvitedToWorkAndUserIsDispatcher || isVendorInvitedToWork) {
			context.add(WorkContext.DISPATCHER);
		}

		return context;

	}

	@Override
	public boolean isAuthorizedToAdminister(Long workId, Long userId) {
		List<WorkContext> contexts = getWorkContext(workId, userId);
		return isAuthorizedToAdminister(contexts, userId);
	}

	@Override
	public boolean isAuthorizedToAdminister(String workNumber, Long userId) {
		Assert.hasText(workNumber);
		AbstractWork work = abstractWorkDAO.findByWorkNumber(workNumber);
		Assert.notNull(work, "Unable to find assignment");
		return isAuthorizedToAdminister(getWorkContext(work, userId), userId);
	}

	private boolean isAuthorizedToAdminister(List<WorkContext> contexts, Long userId) {
		return contexts.contains(WorkContext.OWNER)
			|| (contexts.contains(WorkContext.COMPANY_OWNED) && authenticationService.authorizeUserByAclPermission(userId,
			Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS));
	}

	@Override
	public boolean isAuthorizedToAccept(String workNumber, Long userId) {
		Assert.hasText(workNumber);
		AbstractWork work = abstractWorkDAO.findByWorkNumber(workNumber);
		Assert.notNull(work, "Unable to find assignment");
		return isAuthorizedToAccept(getWorkContext(work, userId), userId);
	}
	private boolean isAuthorizedToAccept(List<WorkContext> contexts, Long userId) {
		User user = userService.getUser(userId);
		return contexts.contains(WorkContext.OWNER)
				|| (contexts.contains(WorkContext.COMPANY_OWNED) && (userRoleService.hasAnyAclRole(user, AclRole.ACL_DEPUTY, AclRole.ACL_ADMIN)))
				|| contexts.contains(WorkContext.DISPATCHER);
	}

	@Override
	public boolean isAuthorizedToAcceptNegotiation(Long workId, Long userId, AbstractWorkNegotiation negotiation) {
		List<WorkContext> contexts = getWorkContext(workId, userId);
		return isAuthorizedToAcceptNegotiation(contexts, userId, negotiation);
	}

	private boolean isAuthorizedToAcceptNegotiation(List<WorkContext> contexts, Long userId, AbstractWorkNegotiation negotiation) {
		User user = userService.getUser(userId);
		return contexts.contains(WorkContext.OWNER)
				|| (contexts.contains(WorkContext.COMPANY_OWNED) && (userRoleService.hasAnyAclRole(user, AclRole.ACL_DEPUTY, AclRole.ACL_ADMIN)))
				|| (negotiation instanceof ScheduleNegotiation && contexts.contains(WorkContext.DISPATCHER));
	}

	@Override
	public TimeZone findAssignmentTimeZone(Long workId) {
		Work work = workDAO.get(workId);
		Assert.notNull(work);

		return findAssignmentTimeZone(work);
	}

	private <T extends AbstractWork> TimeZone findAssignmentTimeZone(T work) {
		// TODO: support locations that don't have postal codes
		if (work.isSetOnsiteAddress() && work.getIsOnsiteAddress()) {
			Assert.notNull(work.getAddress(), "Address must be set");
			Assert.notNull(work.getAddress().getPostalCode(), "Postal code must be set");
			PostalCode postal = invariantDataService.getPostalCodeByCodeCountryStateCity(
				work.getAddress().getPostalCode(),
				work.getAddress().getCountry().getId(),
				work.getAddress().getState().getShortName(),
				work.getAddress().getCity());
			if (postal != null) {
				return postal.getTimeZone();
			}
		}
		return profileService.getTimeZoneByUserId(work.getBuyer().getId());
	}

	@Override
	public boolean isAutomaticAppointmentChange(Long workId, DateRange appointment) {
		Work work = workDAO.get(workId);
		return isUserActiveResourceForWork(authenticationService.getCurrentUser().getId(), workId)
			&& work.getSchedule().contains(appointment);
	}

	@Override
	public DateRange getAppointmentTime(Long workId) {
		Work work = workDAO.findWorkById(workId);
		return getAppointmentTime(work);
	}

	@Override
	public DateRange getAppointmentTime(AbstractWork work) {
		Assert.notNull(work);

		WorkResource resource = workResourceDAO.findActiveWorkResource(work.getId());

		DateRange resourceAppointment = (resource != null) ? resource.getAppointment() : null;
		return DateRangeUtilities.getAppointmentTime(work.getSchedule(), resourceAppointment);
	}

	@Override
	public void setAppointmentTime(Long workId, Calendar apptTime, String message) {
		setAppointmentTime(workId, new DateRange(apptTime), message);
	}

	@Override
	public void setAppointmentTime(Long workId, DateRange appointment, String message) {
		Work work = workDAO.get(workId);
		Assert.notNull(work);
		Assert.state(appointment.getFrom().after(Calendar.getInstance()), "Appointment time should be in the future.");

		// If the initiator is the resource and the appointment time is within the work schedule window, pre-approve;
		// otherwise initiate a negotiation.
		if (isAutomaticAppointmentChange(work.getId(), appointment)) {
			WorkResource resource = findActiveWorkResource(workId);
			if (resource == null) {
				return;
			}
			resource.setAppointment(appointment);

			if (StringUtils.isNotBlank(message)) {
				workNoteService.addNoteToWork(workId, message);
			}

			workResourceDAO.saveOrUpdate(resource);
			summaryService.saveWorkResourceHistorySummary(resource);

			//If it was auto-approved
			if (this.isAutomaticAppointmentChange(workId, appointment)) {
				//Adds a record in the Activity log.
				workChangeLogService.saveWorkChangeLog(new WorkRescheduleAutoApprovedChangeLog(workId, work.getBuyer().getId()));
			}

			// Send email to buyer and resource
			userNotificationService.onWorkAppointmentSet(workId);
			WorkActionRequest workActionRequest = workActionRequestFactory.create(work, authenticationService.getCurrentUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.CHANGE_APPOINTMENT);
			workAuditService.auditWork(workActionRequest);

		} else {
			WorkNegotiationDTO dto = new WorkNegotiationDTO();
			dto.setScheduleNegotiation(true);
			dto.setTimeZoneId(work.getTimeZone().getId());
			dto.setScheduleFromString(DateUtilities.getISO8601(appointment.getFrom()));
			dto.setNote(message);
			if (appointment.isRange()) {
				dto.setIsScheduleRange(true);
				dto.setScheduleThroughString(DateUtilities.getISO8601(appointment.getThrough()));
			}

			workNegotiationService.createRescheduleNegotiation(workId, dto);
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	public Calendar calculateMaxAppointmentDate(AbstractWork work) {
		DateRange workTime = getAppointmentTime(work);
		if (workTime != null) {
			return (workTime.isRange()) ? workTime.getThrough() : workTime.getFrom();
		}
		return null;
	}

	@Override
	public Calendar calculateMaxAppointmentDateLatenessThreshold(AbstractWork work) {
		Calendar maxAppointmentDate = calculateMaxAppointmentDate(work);
		/*
		* Alert: 15 minutes after the assignment schedule time if there's a window do it after the schedule through time
		*/
		if (maxAppointmentDate != null) {
			maxAppointmentDate.add(Calendar.MINUTE, Constants.WORK_RESOURCE_LATE_LABEL_GRACE_PERIOD_MINUTES);
		}
		return maxAppointmentDate;
	}

	@Override
	public Calendar calculateRequiredCheckinDate(AbstractWork work) {
		Calendar checkinDate = Calendar.getInstance();
		checkinDate.clear();
		logger.debug("Checkin required : " + work.isCheckinRequired() + ", checkin call required: " + work.isCheckinCallRequired());
		if (work.isCheckinRequired() || work.isCheckinCallRequired()) {
			/*
			 * Alert: at 5 minutes after the assignment schedule time if there's a window do it after the schedule through time
			 */
			checkinDate = calculateMaxAppointmentDate(work);
			DateUtilities.addMinutes(checkinDate, Constants.WORK_RESOURCE_CHECKIN_GRACE_PERIOD_MINUTES);
		}
		logger.debug("Checkin date for work id: " + work.getId() + " is: " + DateUtilities.formatDateForEmail(checkinDate));
		return checkinDate;
	}

	@Override
	public Calendar calculateRequiredCheckinReminderDate(AbstractWork work) {
		// Reminder: 10 minutes before the assignment schedule time
		Calendar date = getAppointmentTime(work).getFrom();
		DateUtilities.subtractTime(date, Constants.WORK_RESOURCE_CHECKIN_REMINDER_MINUTES, Constants.MINUTE);
		return date;
	}

	@Override
	public Calendar calculateRequiredCheckinReminderDate(Long workId) {
		Work work =  workDAO.get(workId);
		Assert.notNull(work);
		return calculateRequiredCheckinReminderDate(work);

	}

	@Override
	public Calendar calculateRequiredConfirmationNotificationDate(AbstractWork work) {
		Calendar confirmationDate = Calendar.getInstance();
		confirmationDate.clear();

		if (work.isResourceConfirmationRequired()) {
			// Reminder: if NULL then default to 24 hours
			confirmationDate = getAppointmentTime(work).getFrom();
			double hours = work.getResourceConfirmationHours();
			if (hours > 0) {
				DateUtilities.subtractTime(confirmationDate, hours + Constants.EMAIL_CONFIRM_ADJUSTMENT_HRS, Constants.HOUR);
			} else {
				DateUtilities.subtractTime(confirmationDate, 1, Constants.DAY);
			}
		}
		logger.debug("[confirmationDate] " + DateUtilities.formatDateForEmail(confirmationDate));
		return confirmationDate;
	}

	@Override
	public Calendar calculateRequiredConfirmationNotificationDate(Long workId) {
		Work work = workDAO.get(workId);
		Assert.notNull(work);
		return calculateRequiredConfirmationNotificationDate(work);
	}

	@Override
	public Calendar calculateRequiredConfirmationDate(AbstractWork work) {
		Calendar confirmationDate = Calendar.getInstance();
		confirmationDate.clear();

		if (work.isResourceConfirmationRequired()) {
			// Reminder: if NULL then default to 24 hours
			confirmationDate = getAppointmentTime(work).getFrom();
			double hours = work.getResourceConfirmationHours();
			if (hours > 0) {
				DateUtilities.subtractTime(confirmationDate, hours, Constants.HOUR);
			} else {
				DateUtilities.subtractTime(confirmationDate, 1, Constants.DAY);
			}
		}
		logger.debug("[confirmationDate] " + DateUtilities.formatDateForEmail(confirmationDate));
		return confirmationDate;
	}

	@Override
	public boolean isConfirmableNow(AbstractWork work) {
		if (!work.isActive())
			return false;
		if (!work.isResourceConfirmationRequired())
			return false;
		if (work.isConfirmed())
			return false;

		boolean isConfirmableDateInFuture = DateUtilities.isInFuture(calculateRequiredConfirmationNotificationDate(work));
		return !isConfirmableDateInFuture;
	}

	@Override
	public Long getBuyerIdByWorkNumber(String workNumber) throws DuplicateWorkNumberException {
		return workDAO.getBuyerIdByWorkNumber(workNumber);
	}

	@Override
	public List<BuyerIdentityDTO> findBuyerIdentitiesByWorkIds(Collection<Long> workIds) {
		if (CollectionUtils.isEmpty(workIds)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		return workDAO.findBuyerIdentitiesByWorkIds(workIds);
	}

	@Override
	public Long getBuyerIdByWorkId(Long workId) throws DuplicateWorkNumberException {
		return workDAO.getBuyerIdByWorkId(workId);
	}

	@Override
	public boolean isWorkStatusForWorkByWorkNumber(String workNumber, String status) {
		return workDAO.isWorkStatusForWorkByWorkNumber(workNumber, status);
	}

	@Override
	public boolean isWorkStatusAccessibleForUser(String workNumber, Long userId) {
		User user = userService.findUserById(userId);
		if (user == null) {
			return false;
		}
		Work work = workDAO.findWorkByWorkNumber(workNumber);
		if (work == null) {
			return false;
		}

		if (workResourceDAO.isUserResourceForWork(work.getId(), user.getId())) {
			return true;
		} else {
			//if work belongs to an employee of the user company (if user is the work owner is included on this condition)
			Company userCompany = user.getCompany();
			Company workCompany = work.getCompany();
			if (userCompany != null && workCompany != null && userCompany.getId().equals(workCompany.getId())) {
				return true;
			}
		}

		//none of above
		return false;
	}

	@Override
	public void markWorkViewed(Long workId, Long workResourceUserId, ViewType workViewType) {
		Assert.notNull(workId);
		Assert.notNull(workResourceUserId);
		WorkResource workResource = findWorkResource(workResourceUserId, workId);
		if (workResource != null && workResource.getViewedOn() == null) {
			workResource.setViewedOn(Calendar.getInstance());
			workResource.setViewType((workViewType != null ? workViewType.typeString : ViewType.WEB.typeString));
		}
	}

	private Coordinate findLatLongForWork(AbstractWork work) {
		if (work.isOffsite()) {
			return null;
		}
		Address workAddress = work.getAddress();
		if (workAddress != null &&
			workAddress.getLatitude() != null &&
			workAddress.getLongitude() != null) {
			return new Coordinate(workAddress.getLongitude().doubleValue(), workAddress.getLatitude().doubleValue());
		}
		return null;
	}

	private Address getWorkAddress(AbstractWork work) {
		if (work.isOffsite()) {
			return null;
		}
		return work.getAddress();
	}

	@Override
	public double calculateDistanceToWork(Long userId, AbstractWork work) {
		double result = 0.0;
		Address workAddress = getWorkAddress(work);
		if (workAddress == null) {
			return result;
		}

		ProfileDTO profileDTO = profileService.findProfileDTO(userId);

		Coordinate userCoordinate = profileDTO.getCoordinate();
		Coordinate workCoordinate = findLatLongForWork(work);
		if (userCoordinate != null && workCoordinate != null &&
			(userCoordinate.getLatitude() != 0D || userCoordinate.getLongitude() != 0D) &&
			(workCoordinate.getLatitude() != 0D || workCoordinate.getLongitude() != 0D)) {
			result = MathUtils.round(userCoordinate.distanceInMiles(workCoordinate), 2);
		}

		return result;
	}

	@Override
	public boolean isWorkPendingFulfillment(Long workId) {
		return workDAO.isWorkPendingFulfillment(workId);
	}

	@Override
	public int countWorkByCompanyByStatus(Long companyId, List<String> statuses) {
		return workDAO.countWorkByCompanyByStatus(companyId, statuses);
	}

	@Deprecated
	@Override
	public Integer countAllAssignmentsPaymentPendingByCompany(Long companyId) {
		return workDAO.countAllAssignmentsPaymentPendingByCompany(companyId);
	}

	@Override
	public Integer countAllDueWorkByCompany(long companyId) {
		return workDAO.countAllDueWorkByCompany(companyId);
	}

	@Override
	public int reassignWorkOwnership(Long fromId, Long toId) {
		List<Long> workIds = findWorkIdsByBuyerAndStatus(fromId, "sent", "active");
		List<String> workStatusCodes = Collections.unmodifiableList(Lists.newArrayList("sent", "active"));
		int updated = workDAO.updateWorkBuyerUserId(toId, workIds, workStatusCodes);
		if (updated > 0) {
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workIds));
		}
		logger.debug("Work assignments updated: " + updated);
		return updated;
	}

	@Override
	public void addRequirementSetsToWork(AbstractWork work, List<Long> requirementSetIds) {
		if (CollectionUtils.isEmpty(requirementSetIds)) {
			work.setRequirementSets(new ArrayList<RequirementSet>());
		} else {
			work.setRequirementSets(requirementSetDAO.get(requirementSetIds));
		}
	}

	@Override
	public List<Work> findAllWorkWhereWorkStatusTypeNotInAndWorkSubStatusTypeIn(long companyId, long subStatusId, String[] statusCodes) {
		return workDAO.findAllWorkWhereWorkStatusTypeNotInAndWorkSubStatusTypeIn(companyId, subStatusId, statusCodes);
	}

	@Override
	public List<Work> findAllWorkWhereTemplatesNotInAndWorkSubStatusTypeIn(long companyId, long subStatusId, Long[] templateIds) {
		return workDAO.findAllWorkWhereTemplatesNotInAndWorkSubStatusTypeIn(companyId, subStatusId, templateIds);
	}

	@Override
	public void validateResourceCheckIn(Long workId) {
		Work work = findWork(workId);
		WorkResource resource = findActiveWorkResource(work.getId());

		if ((work.isCheckinRequired() || work.isCheckinCallRequired()) && work.isActive() && !isWorkInProgress(workId)) {
			// Ensure that the scheduled time for the assignment hasn't changed.
			Calendar requiredCheckIn = calculateRequiredCheckinDate(work);

			if (!DateUtilities.withinIntervalWindow(Calendar.MINUTE, 5, requiredCheckIn)) {
				return;
			}
			/*
			 * Since this is processed in a different server that could be seconds behind, we'll subtract 1 minute to the actual date
			 * to prevent the isInPast validation from failing.
			 * E.g.
			 * 20:34:59 DEBUG [eventJMSContainer-8]:service.business.WorkServiceImpl.calculateRequiredCheckinDate()3005 Checkin date for work id: 43260 is: Fri, 16 Dec 2011 08:35 PM
			 * The checkin date was 8:35 but it was processed at 8:34:59
			 */
			Calendar checkinIdDate = DateUtilities.cloneCalendar(requiredCheckIn);
			checkinIdDate.add(Calendar.MINUTE, -1);
			if (DateUtilities.isInPast(checkinIdDate)) {
				userNotificationService.onWorkResourceNotCheckedIn(resource);
			}
		}
	}

	@Override
	public List<Long> findDeclinedResourceIds(Long workId) {
		Assert.notNull(workId);
		return workResourceDAO.findUserIdsDeclinedForWork(workId);
	}

	public Work findWorkByInvoice(Long invoiceId) {
		List<Long> workIds = workDAO.findWorkIdsByInvoiceId(invoiceId);
		if (isNotEmpty(workIds)) {
			return workDAO.get(workIds.get(0));
		}
		return null;
	}

	/**
	 * Calculates the total time worked based on all of the active resource's time tracker entries.
	 * @param workId Work id
	 * @return A map containing the hours and minutes worked.
	 */
	@Override
	public Map<String, Object> findActiveWorkerTimeWorked(Long workId) {
		Assert.notNull(workId);

		Long activeWorkerId = findActiveWorkerId(workId);
		Assert.notNull(activeWorkerId);

		return findActiveWorkerTimeWorked(workId, activeWorkerId);
	}

	@Override
	public Map<String, Object> findActiveWorkerTimeWorked(Long workId, Long activeWorkerId) {
		Assert.notNull(workId);
		Assert.notNull(activeWorkerId);

		long totalTimeMillis = 0;
		WorkResource workResource = findWorkResource(activeWorkerId, workId);
		for (WorkResourceTimeTracking tt : findTimeTrackingByWorkResource(workResource.getId())) {
			if (tt.getCheckedInOn() != null && tt.getCheckedOutOn() != null) {
				totalTimeMillis += DateUtilities.getDuration(tt.getCheckedInOn(), tt.getCheckedOutOn());
			}
		}

		return ImmutableMap.<String, Object>of(
			"hours", TimeUnit.MILLISECONDS.toHours(totalTimeMillis),
			"minutes", TimeUnit.MILLISECONDS.toMinutes(totalTimeMillis % TimeUnit.HOURS.toMillis(1))
		);
	}

	@Override
	public List<Integer> findAssignmentsMissingResourceNoShow() {
		return workDAO.findAssignmentsMissingResourceNoShow();
	}

	@Override
	public List<String> findAssignmentsWithDeliverablesDue() {
		return workDAO.findAssignmentsWithDeliverablesDue();
	}

	@Override
	public List<Work> findAssignmentsRequiringDeliverableDueReminder() {
		return workDAO.findAssignmentsRequiringDeliverableDueReminder();
	}

	@Override
	public void cleanUpDeliverablesForReassignmentOrCancellation(WorkResource workResource) {
		Assert.notNull(workResource);
		Work work = workResource.getWork();
		Assert.notNull(work);

		if (work.isDeliverableRequired()) {
			workSubStatusService.resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.DELIVERABLE_LATE, WorkSubStatusType.DELIVERABLE_REJECTED);
			workResourceService.ignoreWorkResourceLabel(workResource.getId(), WorkResourceLabelType.LATE_DELIVERABLE);
			deliverableService.disableDeliverableDeadline(work.getWorkNumber());
		}
	}

	@Override
	public void cleanUpDeliverablesForReschedule(WorkResource workResource) {
		Assert.notNull(workResource);
		Work work = workResource.getWork();
		Assert.notNull(work);

		if (work.isDeliverableRequired()) {
			workSubStatusService.resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.DELIVERABLE_LATE);
			workResourceService.ignoreWorkResourceLabel(workResource.getId(), WorkResourceLabelType.LATE_DELIVERABLE);
			deliverableService.reactivateDeliverableDeadlineAndReminder(work.getDeliverableRequirementGroup());
		}
	}

	@Override
	public List<Long> findOpenWorkIdsBetweenUserAndCompany(Long userId, Long companyId) {
		return workDAO.findOpenWorkIdsBetweenUserAndCompany(userId, companyId);
	}

	@Override
	public Integer countAllDueWorkByCompany(Calendar dueDateFrom, Calendar dueDateThrough, Long companyId) {
		return workDAO.countAllDueWorkByCompany(dueDateFrom, dueDateThrough, companyId);
	}

	/**
	 * Use this method for the force reschedule on behalf of work owner
	 * In this case only work that is NOT Paid,Canceled or Pending Approval
	 * for the cases where negotiations should be used use setAppointmentTime method
	 */
	@Override
	public Work updateWorkSchedule(Work work, DateRange appointment, String message) {
		Assert.notNull(work);
		Assert.state(appointment.getFrom().after(Calendar.getInstance()), "Appointment time should be in the future.");
		WorkResource resource = findActiveWorkResource(work.getId());
		Assert.isNull(resource, "This work has resource assigned please use setAppointmentTime to reschedule");
		work.setSchedule(appointment);
		workDAO.saveOrUpdate(work);
		if (StringUtils.isNotBlank(message)) {
			workNoteService.addNoteToWork(work.getId(), message);
		}
		workChangeLogService.saveWorkChangeLog(new WorkRescheduleAutoApprovedChangeLog(work.getId(), work.getBuyer().getId()));
		workAuditService.auditAndReindexWork(workActionRequestFactory.create(work, authenticationService.getCurrentUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.CHANGE_APPOINTMENT));
		return work;
	}

	@Override
	public Work updateClientAndProject(ClientCompany client, Project project, long workId) {
		Assert.notNull(client);
		Work work = findWork(workId);
		Assert.notNull(work);

		Location oldLocation = work.getLocation();
		if (oldLocation != null) {
			ClientLocation clientLocation =
				clientLocationDAO.findBy("address", oldLocation.getAddress(), "clientCompany", client);

			if (clientLocation == null) {
				clientLocation = new ClientLocation();
				BeanUtils.copyProperties(oldLocation, clientLocation, "id", "availableHours", "phoneAssociations");
				if (clientLocation.getName().isEmpty()) {
					clientLocation.setName(clientLocation.getAddress().getAddress1());
				}
				clientLocation.setClientCompany(client);
				clientLocationDAO.saveOrUpdate(clientLocation);
			}
			work.setLocation(clientLocation);
		}

		work.setClientCompany(client);
		work.setProject(project);
		workDAO.saveOrUpdate(work);
		workAuditService.auditAndReindexWork(workActionRequestFactory.create(work, authenticationService.getCurrentUser().getId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.CHANGE_CLIENT_PROJECT));
		return work;
	}

	@Override
	public List<LiteResource> findLiteResourceByWorkNumber(String workNumber) {
		Assert.notNull(workNumber);
		return workResourceDAO.findLiteResourceByWorkNumber(workNumber);
	}

	@Override
	public List<Long> findWorkIdsByInvoiceId(Long... invoiceIds) {
		return workDAO.findWorkIdsByInvoiceId(invoiceIds);
	}

	@Override
	public List<Integer> getAutoPayWorkIds(Date dueOn, List<String> workStatusTypes) {
		return workDAO.getAutoPayWorkIds(dueOn, workStatusTypes);
	}

	@Override
	public List<WorkWorkResourceAccountRegister> findWorkAndWorkResourceForPayment(List<Long> assignmentIds) {
		return workDAO.findWorkAndWorkResourceForPayment(assignmentIds);
	}

	@Override
	public Set<WorkDue> findAllAssignmentsPastDue(Calendar dueDate) {
		Assert.notNull(dueDate);
		return workDAO.findAllAssignmentsPastDue(dueDate);
	}

	@Override
	public Set<WorkDue> findAllDueAssignmentsByDueDate(Calendar dueDateFrom, Calendar dueDateThrough) {
		Assert.notNull(dueDateFrom);
		Assert.notNull(dueDateThrough);
		return workDAO.findAllDueAssignmentsByDueDate(dueDateFrom, dueDateThrough);
	}

	@Override
	public boolean isWorkNotifyAllowed(Long workId) {
		Assert.notNull(workId);
		Work work = workDAO.get(workId);
		if (!work.isSent()) {
			return true;
		}

		Calendar cutoff = DateUtilities.subtractTime(Calendar.getInstance(), Constants.WORK_NOTIFY_THROTTLE_HOURS,
			Constants.HOUR);

		return workChangeLogService.getWorkNotifyLogCountSinceDate(workId, cutoff) == 0;
	}

	@Override
	public boolean isWorkNotifyAvailable(Long workId) {
		return workResourceDAO.isWorkNotifyAvailable(workId, NotificationType.RESOURCE_WORK_INVITED, true);
	}

	@Override
	public void workNotifyResourcesForWork(Long workId) throws OperationNotSupportedException {
		Work work = workDAO.get(workId);

		Assert.notNull(work);

		if (!work.isSent()) {
			throw new OperationNotSupportedException("Work must have SENT status.");
		}

		if (!isWorkNotifyAvailable(workId)) {
			throw new OperationNotSupportedException("None of the workers have opted to receive notifications.");
		} else if (!isWorkNotifyAllowed(workId)) {
			throw new OperationNotSupportedException("Notifying workers is limited to once per hour.");
		}

		List<Long> usersToNotify = workResourceDAO.findAllResourcesUserIdsForWorkWithNotificationAllowed(work.getId(), NotificationType.RESOURCE_WORK_INVITED, true);
		if (!usersToNotify.isEmpty()) {
			userNotificationService.sendWorkNotifyInvitations(work.getId(), usersToNotify);
		}

		WorkActionRequest workActionRequest = workActionRequestFactory.create(
			work,  authenticationService.getCurrentUserId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.WORK_NOTIFY
		);

		workAuditService.auditWork(workActionRequest);

		workChangeLogService.saveWorkChangeLog(
			new WorkNotifyChangeLog(work.getId(), authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId(), null)
		);
	}

	/**
	 * Revert assignment pricing to original pricing
	 */
	@Override
	public void rollbackToOriginalPricePrice(Long workId) {

		Work work = workDAO.get(workId);
		Assert.notNull(work);

		PricingStrategy pricingStrategy = getOriginalWorkPricingStategy(workId);
		if (pricingStrategy != null) {
			BeanUtilities.copyProperties(work.getPricingStrategy().getFullPricingStrategy(), pricingStrategy.getFullPricingStrategy());
		}
	}

	@Override
	public PricingStrategy getOriginalWorkPricingStategy(Long workId) {

		PricingStrategy pricingStrategy = null;
		Optional<WorkPrice> originalWorkPrice = workPriceDAO.findOriginalPriceHistoryForWork(workId);
		if (originalWorkPrice.isPresent()) {
			pricingStrategy = originalWorkPrice.get().getPricingStrategy();
		}
		else{
			Work work = workDAO.get(workId);
			pricingStrategy = work.getPricingStrategy();
		}

		return pricingStrategy;
	}

	@Override
	public PricingStrategy getCurrentWorkPricingStategy(Long workId) {

		Work work = workDAO.get(workId);
		Assert.notNull(work);

		return work.getPricingStrategy();
	}

	@Override
	public WorkUniqueId findUniqueIdByCompanyVersionIdValue(Long companyId, int version, String idValue) {
		return this.workUniqueIdDAO.findByCompanyVersionIdValue(companyId, version, idValue);
	}

	@Override
	public boolean isOfflinePayment(AbstractWork work) {
		return workOptionsService.hasOption(work, WorkOption.OFFLINE_PAYMENT, "true");
	}

	@Override
	public void setOfflinePayment(AbstractWork work, boolean payAssignmentOffline) {
		// OfflinePayAllRope updates payAllAssignmentsOffline to true if guest can enter
		MutableBoolean payAllAssignmentsOffline = new MutableBoolean(false);
		offlinePayAllDoorman.welcome(new UserGuest(work.getBuyer()), new OfflinePayAllRope(payAllAssignmentsOffline));

		workOptionsService.setOption(work, WorkOption.OFFLINE_PAYMENT,
			payAllAssignmentsOffline.booleanValue() || payAssignmentOffline ? "true" : "false");
	}

	/**
	 * Capture payment terms update and persist in the work change log
	 * @param work
	 * @param dto
	 */
	public void saveWorkChangeLogOnPaymentTermsUpdate(Work work, WorkDTO dto) {

		Assert.notNull(work);
		Assert.notNull(dto);
		if (work.getPaymentTermsDays() != dto.getPaymentTermsDays()) {
			WorkPropertyChangeLog propertyChangeLog = new WorkPropertyChangeLog(work.getId(), authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId(), null);
			propertyChangeLog.setPropertyName("paymentTermsDays");
			propertyChangeLog.setOldValue(String.valueOf(work.getPaymentTermsDays()));
			propertyChangeLog.setNewValue(String.valueOf(dto.getPaymentTermsDays()));
			workChangeLogService.saveWorkChangeLog(propertyChangeLog);
		}
	}

	@Override
	public String getRecurrenceUUID(Long workId) {
		WorkRecurrenceAssociation workRecurrenceAssociation = workRecurrenceAssociationDAO.findWorkRecurrenceAssociation(workId);
		if(workRecurrenceAssociation != null) {
			return workRecurrenceAssociation.getWorkRecurrence().getRecurrenceUUID();
		}
		return null;
	}

	@Override
	public void saveWorkRecurrence(Long workId, Long recurringWorkId, String recurrenceUUID) {
		WorkRecurrenceAssociation workRecurrenceAssociation =
				new WorkRecurrenceAssociation(workId, recurringWorkId, recurrenceUUID);
		workRecurrenceAssociationDAO.saveOrUpdate(workRecurrenceAssociation);
	}

	@Override
	public List<WorkSchedule> augmentWorkSchedules(List<WorkSchedule> workSchedules) {
		if (workSchedules == null) {
			return workSchedules;
		}

		for (WorkSchedule workSchedule : workSchedules) {
			augmentWorkSchedule(workSchedule);
		}
		return workSchedules;
	}

	@Override
	public WorkSchedule augmentWorkSchedule(WorkSchedule workSchedule) {
		if (workSchedule == null || workSchedule.getDateRange().isRange()) {
			return workSchedule;
		}

		Work work = findWork(workSchedule.getWorkId());
		if (work == null) {
			return workSchedule;
		}

		Calendar through = (Calendar)workSchedule.getDateRange().getFrom().clone();

		if (work.getPricingStrategyType() == PricingStrategyType.PER_HOUR) {
			through.add(HOUR, ((PerHourPricingStrategy)work.getPricingStrategy()).getMaxNumberOfHours().intValue());
		} else if (work.getPricingStrategyType() == PricingStrategyType.BLENDED_PER_HOUR) {
			through.add(HOUR, ((BlendedPerHourPricingStrategy)work.getPricingStrategy()).getInitialNumberOfHours().intValue());
			through.add(HOUR, ((BlendedPerHourPricingStrategy)work.getPricingStrategy()).getMaxBlendedNumberOfHours().intValue());
		} else {
			return workSchedule;
		}

		workSchedule.getDateRange().setThrough(through);

		return workSchedule;
	}

	@Override
	public Map<String, Object> getAssignmentDataOne(final Map<String, Object> params) {
		return workDAO.getAssignmentDataOne(params);
	}
}
