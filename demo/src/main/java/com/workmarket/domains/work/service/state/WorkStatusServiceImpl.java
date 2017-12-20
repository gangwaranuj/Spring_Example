package com.workmarket.domains.work.service.state;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.business.decision.gen.Messages.StartDecisionFlowResponse;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.decisionflow.CompanyToDecisionFlowTemplateAssociationDAO;
import com.workmarket.dao.decisionflow.WorkToDecisionFlowAssociationDAO;
import com.workmarket.dao.state.WorkStatusDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkPrice;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.InvoicePaymentTransaction;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.changelog.work.WorkCreatedChangeLog;
import com.workmarket.domains.model.changelog.work.WorkResourceStatusChangeChangeLog;
import com.workmarket.domains.model.changelog.work.WorkStatusChangeChangeLog;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceCollection;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.dao.WorkPriceDAO;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkMilestonesService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.StopPaymentDTO;
import com.workmarket.service.business.dto.WorkResourceLabelDTO;
import com.workmarket.service.business.event.CompanyDueInvoicesEvent;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.UnlockCompanyEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkClosedEvent;
import com.workmarket.service.business.event.work.WorkCompletedEvent;
import com.workmarket.service.business.event.work.WorkCreatedEvent;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkInvoiceSendType;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.queue.WorkEventProcessQueue;
import com.workmarket.service.business.status.CloseWorkStatus;
import com.workmarket.service.business.wrapper.CloseWorkResponse;
import com.workmarket.service.decisionflow.DecisionFlowService;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class WorkStatusServiceImpl implements WorkStatusService {

	private static final Log logger = LogFactory.getLog(WorkStatusServiceImpl.class);

	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private WorkStatusDAO workStatusDAO;
	@Qualifier("accountRegisterServicePaymentTermsImpl")
	@Autowired private AccountRegisterService accountRegisterServicePaymentTermsImpl;
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private WorkMilestonesService workMilestonesService;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private UserService userService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private RatingService ratingService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private DeliverableService deliverableService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private BillingService billingService;
	@Autowired private SummaryService summaryService;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private WorkEventProcessQueue workEventProcessQueue;
	@Autowired private RegisterTransactionDAO registerTransactionDAO;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private WorkPriceDAO workPriceDAO;
	@Autowired private AccountPricingService accountPricingService;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private ScorecardCache scorecardCache;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private ServiceMessageHelper messageHelper;
	@Autowired private WorkService workService;
	@Autowired private WorkResourceDetailCache workResourceDetailCache;
	@Autowired private CompanyToDecisionFlowTemplateAssociationDAO companyToDecisionFlowTemplateAssociationDAO;
	@Autowired private WorkToDecisionFlowAssociationDAO workToDecisionFlowAssociationDAO;
	@Autowired private DecisionFlowService decisionFlowService;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private MetricRegistry metricRegistry;

	private WMMetricRegistryFacade wmMetricRegistryFacade;

	@PostConstruct
	private void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "work-status-service");
	}

	@Override
	public WorkStatusType findWorkStatusTypeByCode(String code) {
		return workStatusDAO.findByCode(code);
	}


	@Override
	public List<WorkStatusType> findAllStatuses() {
		return workStatusDAO.getAll();
	}

	protected void onPostCreateAssignment(Long workId, Long userId, User onBehalOfUser, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(userId);
		Assert.notNull(companyId);
		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(workId);
		Assert.notNull(milestones);

		if (milestones.getCreatedOn() == null) {
			milestones.setCreatedOn(DateUtilities.getCalendarNow());
			milestones.setCompanyId(companyId);
		}
		if (milestones.getDraftOn() == null) {
			milestones.setDraftOn(DateUtilities.getCalendarNow());
		}

		WorkActionRequest workCreateRequest = createWorkActionRequest(workId, WorkAuditType.CREATE, userId, onBehalOfUser);
		WorkActionRequest workDraftRequest = createWorkActionRequest(workId, WorkAuditType.DRAFT, userId, onBehalOfUser);
		workAuditService.auditWork(workCreateRequest);
		workAuditService.auditAndReindexWork(workDraftRequest);
	}

	@Override
	public void flatWorkTransitionToCreated(Long workId, Long userId, Long companyId) {
		onPostCreateAssignment(workId, userId, null, companyId);
	}

	@Override
	public void transitionToCreated(Long workId, Long userId, User onBehalOfUser, Long companyId) {
		onPostCreateAssignment(workId, userId, onBehalOfUser, companyId);

		Work work = workService.findWork(workId);
		Assert.notNull(work);
		Long onBehalOfUserId = onBehalOfUser != null ? onBehalOfUser.getId() : null;
		workChangeLogService.saveWorkChangeLog(new WorkCreatedChangeLog(workId, authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), onBehalOfUserId));
		// Send the notification to the buyer
		eventRouter.sendEvent(new WorkCreatedEvent(work.getId()));
		summaryService.saveWorkHistorySummary(work);
		summaryService.saveWorkStatusTransitionHistorySummary(work, null, WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT), 0);

		wmMetricRegistryFacade.meter(work.getWorkStatusType().getCode()).mark();
	}

	@Override
	public void transitionToSend(Work work, WorkActionRequest workRequest) {
		Assert.notNull(work, "Work is required");

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.SENT);
		work.setWorkStatusType(newWorkStatus);

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), null, oldWorkStatus,
				newWorkStatus));

		if (work.getWorkStatusType().getCode().equals(WorkStatusType.SENT)) {
			webHookEventService.onWorkSent(work.getId(), work.getCompany().getId());
		}

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		if (milestones.getSentOn() == null) {
			milestones.setSentOn(DateUtilities.getCalendarNow());
		}

		/**
		 * Snapshot the current pricing type if different from when it was created
		 */
		AccountPricingType pricingType = work.getCompany().getAccountPricingType();
		if (!pricingType.getCode().equals(work.getAccountPricingType().getCode())) {
			work.setAccountPricingType(pricingType);
		}

		updateWorkAccountServiceType(work);

		summaryService.saveWorkHistorySummary(work);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getMilestonesFieldFromWorkStatus(oldWorkStatus), milestones.getSentOn()));

		workRequest.setAuditType(WorkAuditType.SEND);
		workAuditService.auditWork(workRequest);

		workSubStatusService.resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.RESOURCE_CANCELLED);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		wmMetricRegistryFacade.meter(work.getWorkStatusType().getCode()).mark();
	}

	@Override
	public void transitionToAccepted(WorkActionRequest workRequest, WorkResource workResource) {
		Assert.notNull(workResource, "WorkResource is required");
		Work work = workResource.getWork();

		updateWorkAccountServiceType(work);

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		Calendar calendar = DateUtilities.getCalendarNow();
		milestones.setAcceptedOn(calendar);
		milestones.setActiveOn(calendar);

		summaryService.saveWorkResourceHistorySummary(workResource);
		summaryService.saveWorkHistorySummary(work);
		summaryService.saveWorkStatusTransitionHistorySummary(work, WorkStatusType.newWorkStatusType(WorkStatusType.SENT), WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE), DateUtilities.getSecondsBetween(milestones.getSentOn(), calendar));

		workRequest.setAuditType(WorkAuditType.ACCEPT);
		workAuditService.auditAndReindexWork(workRequest);

		if (work.isDeliverableRequired()) {
			deliverableService.reactivateDeliverableDeadlineAndReminder(work.getDeliverableRequirementGroup());
		}

		workSubStatusService.resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.RESOURCE_CANCELLED);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);
		work.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));
		workResourceDetailCache.evict(work.getId());
		wmMetricRegistryFacade.meter(work.getWorkStatusType().getCode()).mark();
	}

	@Override
	public void transitionToIncomplete(Work work) {
		Assert.notNull(work);

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		milestones.setDueOn(null);
	}

	@Override
	public List<ConstraintViolation> transitionToComplete(WorkActionRequest workRequest, CompleteWorkDTO completeWorkDTO) {
		Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work is required");
		Work work = workService.findWork(workId);
		Assert.notNull(work);
		if (workRequest.getModifierId() == null) {
			workRequest.setModifierId(work.getBuyer().getId());
		}

		Boolean isOnBehalfOf = (workRequest.getOnBehalfOfId() != null);
		// validate
		List<ConstraintViolation> violations = workValidationService.validateComplete(work, completeWorkDTO, isOnBehalfOf);

		if (isNotEmpty(violations)) {
			return violations;
		}

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.COMPLETE);

		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);
		workSubStatusService.resolveSystemSubStatusByAction(work.getId(), WorkSubStatusType.RESOURCE_CHECKED_OUT);
		work.setResolution(completeWorkDTO.getResolution());

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(workId);
		milestones.setCompleteOn(DateUtilities.getCalendarNow());

		// Snapshot the in-progress time when check in is not required
		if (!(work.isCheckinRequired() || work.isCheckinCallRequired()) && milestones.getInProgressOn() == null) {
			milestones.setInProgressOn(work.getScheduleFrom());
		}

		WorkResource workResource = workResourceService.findActiveWorkResource(workId);

		if (work.getPricingStrategy() instanceof PerHourPricingStrategy || work.getPricingStrategy() instanceof BlendedPerHourPricingStrategy) {
			workResource.setHoursWorked(new BigDecimal(completeWorkDTO.getHoursWorked()));
		} else if (work.getPricingStrategy() instanceof PerUnitPricingStrategy) {
			workResource.setUnitsProcessed(new BigDecimal(completeWorkDTO.getUnitsProcessed()));
		}

		if (completeWorkDTO.getSalesTaxCollectedFlag()) {
			work.getPricingStrategy().getFullPricingStrategy().setSalesTaxCollectedFlag(true);
			work.getPricingStrategy().getFullPricingStrategy().setSalesTaxRate(BigDecimal.valueOf(completeWorkDTO.getSalesTaxRate()));
		}

		if (completeWorkDTO.getAdditionalExpenses() != null) {
			workResource.setAdditionalExpenses(BigDecimal.valueOf(completeWorkDTO.getAdditionalExpenses()));
		}
		if (completeWorkDTO.getBonus() != null) {
			workResource.setBonus(BigDecimal.valueOf(completeWorkDTO.getBonus()));
		}

		if (completeWorkDTO.getOverridePrice() != null) {
			work.getPricingStrategy().getFullPricingStrategy().setOverridePrice(BigDecimal.valueOf(completeWorkDTO.getOverridePrice()));
		} else {
			work.getPricingStrategy().getFullPricingStrategy().setOverridePrice(null);
		}

		// Mark any attempted assessments as complete now that the assignment is complete
		for (Attempt a : assessmentService.findLatestAttemptByUserAndWork(workResource.getUser().getId(), work.getId())) {
			assessmentService.completeAttemptForAssessment(a.getId());
		}

		summaryService.saveWorkHistorySummary(work, workResource);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getMilestonesFieldFromWorkStatus(oldWorkStatus), milestones.getCompleteOn()));

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId(),
				workRequest.getOnBehalfOfId(), oldWorkStatus,
				newWorkStatus));

		workRequest.setAuditType(WorkAuditType.COMPLETE);
		workAuditService.auditWork(workRequest);

		if (work.isComplete()) {
			webHookEventService.onWorkCompleted(workId, work.getCompany().getId());
			startDecisionFlow(work);
		}

		/**
		 * If the user selected an specific value for rating then use that value regardless of the auto-rate settings Otherwise, check for the auto-rate flag.
		 */
		RatingDTO ratingDTO = null;
		if (completeWorkDTO.hasRating() && completeWorkDTO.getRating().getValue() > 0) {
			ratingDTO = completeWorkDTO.getRating();
		}
		if (ratingDTO != null) {
			ratingService.createRatingForWork(workResource.getUser().getId(), work.getBuyer().getId(), workId, ratingDTO);
		}

		// TODO consider moving these to annotations that advise the completeWork method
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
		eventRouter.sendEvent(new WorkCompletedEvent(workId, isOnBehalfOf));
		wmMetricRegistryFacade.meter(work.getWorkStatusType().getCode()).mark();
		return violations;
	}

	private void startDecisionFlow(final Work work) {
		if (!featureEvaluator.hasFeature(work.getCompany().getId(), Constants.MULTIPLE_APPROVALS_FEATURE)) {
			return;
		}
		final List<String> decisionFlowTemplateUuids =
				companyToDecisionFlowTemplateAssociationDAO.findDecisionFlowTemplateUuids(work.getCompany().getId());
		if (CollectionUtils.isNotEmpty(decisionFlowTemplateUuids)) {
			// assumes that a company only has one decision flow template OR that the first one we find is the right one
			final StartDecisionFlowResponse startDecisionFlowResponse =
					decisionFlowService.start(decisionFlowTemplateUuids.get(0));
			if (startDecisionFlowResponse.getStatus().getSuccess()) {
				workToDecisionFlowAssociationDAO.addDecisionFlowAssociation(work, startDecisionFlowResponse.getFlowUuid());
			} else {
				logger.error("Unable to start decision flow because " + startDecisionFlowResponse.getStatus().getMessageList());
			}
		}
	}

	@Override
	public CloseWorkResponse transitionToClosed(WorkActionRequest workRequest, CloseWorkDTO closeWorkDTO) {
		Assert.notNull(workRequest);
		Assert.notNull(closeWorkDTO);
		Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work is required");
		Work work = workService.findWork(workId);
		Assert.notNull(work);

		List<ConstraintViolation> violations = workValidationService.validateClosed(work);

		if (isNotEmpty(violations)) {
			CloseWorkResponse response = CloseWorkResponse.fail();
			response.addAllMessages(messageHelper.getMessages(violations));
			return response;
		}

		auditCloseWork(workRequest, work);

		// Handle transition and any exceptions first...
		// If valid, continue on with any other necessary actions.

		WorkResource resource = workResourceService.findActiveWorkResource(workId);
		checkNotNull(resource);

		boolean workResourceIsEmployee = work.getCompany().getId().equals(resource.getUser().getCompany().getId());
		violations = (work.hasPaymentTerms() && !workResourceIsEmployee) && !workService.isOfflinePayment(work) ?
			transitionToPendingPayment(work) :
			transitionClosedToPaid(work, resource);

		if (isNotEmpty(violations)) {
			CloseWorkResponse response = CloseWorkResponse.fail();
			response.addAllMessages(messageHelper.getMessages(violations));
			return response;
		}

		//Add rating
		rateResourceOnWorkClosed(work, closeWorkDTO, resource);

		if (work.isClosed()) {
			webHookEventService.onWorkApproved(workId, work.getCompany().getId());
		}

		WorkClosedEvent event = eventFactory.buildWorkClosedEvent(workId, closeWorkDTO);
		if (event != null) {
			eventRouter.sendEvent(event.setUser(authenticationService.getCurrentUser()));
		}

		boolean workDoneByEmployee = work.getCompany().getId().equals(resource.getUser().getCompany().getId());
		if (workDoneByEmployee) {
			return CloseWorkResponse.create(CloseWorkStatus.CLOSED_BY_EMPLOYEE);
		}

		ManageMyWorkMarket mmw = work.getManageMyWorkMarket();
		if (!mmw.getAutoPayEnabled() && !work.hasPaymentTerms()) {
			return CloseWorkResponse.create(CloseWorkStatus.CLOSED_IMMEDIATELY);
		}

		if (work.getInvoice() != null) {
			CloseWorkResponse response = CloseWorkResponse.create()
					.setInvoiceNumber(work.getInvoice().getInvoiceNumber())
					.setPaymentTermsDays(mmw.getPaymentTermsDays());
			response.setStatus(mmw.getAutoPayEnabled() ? CloseWorkStatus.CLOSED_AND_AUTOPAID : CloseWorkStatus.CLOSED_AND_PAID);
			return response;
		}

		wmMetricRegistryFacade.meter(work.getWorkStatusType().getCode()).mark();
		return CloseWorkResponse.create(CloseWorkStatus.CLOSED).setPaymentTermsDays(mmw.getPaymentTermsDays());
	}

	private void auditCloseWork(WorkActionRequest workRequest, Work work) {
		Assert.notNull(work);
		Assert.notNull(workRequest);

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.CLOSED);
		work.setWorkStatusType(newWorkStatus);

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		Calendar closedOn = DateUtilities.getCalendarNow();
		milestones.setClosedOn(closedOn);
		work.setClosedOn(closedOn);
		milestones.setDueOn(closedOn);
		work.setDueOn(closedOn);

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), oldWorkStatus,
			newWorkStatus));

		summaryService.saveWorkHistorySummary(work);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getMilestonesFieldFromWorkStatus(oldWorkStatus), closedOn));

		workRequest.setAuditType(WorkAuditType.CLOSE);
		workAuditService.auditAndReindexWork(workRequest);
	}

	void rateResourceOnWorkClosed(Work work, CloseWorkDTO closeWorkDTO, WorkResource activeResource) {
		Assert.notNull(work);
		Assert.notNull(activeResource);
		Assert.notNull(closeWorkDTO);

		/**
		 * If the user selected an specific value for rating then use that value regardless of the auto-rate settings Otherwise, check for the auto-rate flag.
		 */
		RatingDTO ratingDTO = null;
		if (closeWorkDTO.hasRating() && NumberUtilities.isPositive(closeWorkDTO.getRating().getValue())) {
			ratingDTO = closeWorkDTO.getRating();
		} else if (work.getCompany() != null && work.getCompany().getManageMyWorkMarket() != null && work.getCompany().getManageMyWorkMarket().getAutoRateEnabledFlag()) {
			ratingDTO = new RatingDTO(Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, Rating.EXCELLENT, "");
		}

		if (ratingDTO != null) {
			try {
				ratingService.updateLatestRatingForUserForWork(work.getId(), activeResource.getUser().getId(), ratingDTO);
			} catch (Exception e) {
				logger.error("Error saving rating for work " + work.getId(), e);
			}
		}
	}

	@Override
	public void onPostTransitionToClosed(long workId, CloseWorkDTO closeWorkDTO) {
		Work work = workService.findWork(workId);
		Assert.notNull(work);
		Assert.notNull(closeWorkDTO);

		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);

		Long activeWorkerId = workService.findActiveWorkerId(workId);
		Assert.notNull(activeWorkerId);

		if (closeWorkDTO.isBlockResource()) {
			userService.blockUser(authenticationService.getCurrentUser().getId(), activeWorkerId);
		}

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));

		// update the worker
		final List<Long> companyIds = Lists.newArrayList();
		User worker = userService.getUser(activeWorkerId);
		if (worker != null && worker.getCompany() != null) {
			companyIds.add(worker.getCompany().getId());
		}

		// now do the same for our buyer
		Long buyerId = workService.getBuyerIdByWorkId(workId);
		User buyerUser = userService.getUser(buyerId);
		if (buyerUser != null && buyerUser.getCompany() != null) {
			companyIds.add(buyerUser.getCompany().getId());
		}

	}

	@Override
	public List<ConstraintViolation> transitionToStopPayment(WorkActionRequest workRequest, StopPaymentDTO stopPaymentDTO) {
		Assert.notNull(workRequest);
		Assert.notNull(stopPaymentDTO);
		Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work is required");

		Work work = workService.findWork(workId);
		Assert.notNull(work);

		// validate
		List<ConstraintViolation> violations = workValidationService.validateStopPayment(work);
		if (isNotEmpty(violations)) {
			return violations;
		}

		//set invoice to voided.
		boolean voidedInvoice = billingService.voidWorkInvoice(work);
		Assert.isTrue(voidedInvoice, "Invoice isn't void.");

		//the assignment goes back to ACTIVE status
		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE);
		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(workId);
		milestones.setActiveOn(Calendar.getInstance());

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), oldWorkStatus,
				newWorkStatus));

		summaryService.saveWorkHistorySummary(work);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getClosedOn(), milestones.getActiveOn()));

		//clear the due on and closed on date
		milestones.setClosedOn(null);
		milestones.setDueOn(null);
		work.setClosedOn(null);
		work.setDueOn(null);
		work.setInvoice(null);

		WorkActionRequest request = createWorkActionRequest(work.getId(), WorkAuditType.PENDING_PAYMENT, authenticationService.getCurrentUser().getId(), null);
		workAuditService.auditWork(request);

		//add sub status label
		workSubStatusService.addSystemSubStatus(workId, WorkSubStatusType.STOP_PAYMENT_PENDING, stopPaymentDTO.getReason());

		//sent email to the resource
		userNotificationService.onWorkStopPayment(workId, stopPaymentDTO.getReason());
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		wmMetricRegistryFacade.meter(WorkSubStatusType.STOP_PAYMENT_PENDING).mark();
		return violations;
	}

	protected List<ConstraintViolation> transitionClosedToPaid(Work work, WorkResource workResource) {
		Assert.notNull(work);
		List<ConstraintViolation> violations = workValidationService.validatePaid(work);

		if (isNotEmpty(violations)) {
			return violations;
		}

		Assert.notNull(workResource);
		accountRegisterAuthorizationService.authorizeOnCompleteWork(workResource);

		// Generate invoice
		billingService.generateInvoiceForWork(work);

		onPostPayAssignment(work, workResource.getId(), Calendar.getInstance(), authenticationService.getCurrentUser());
		return violations;
	}

	@Override
	public List<ConstraintViolation> transitionToFulfilledAndPaidFromInvoiceBulkPayment(long workId, long invoicePaymentTrxId, long userActorId) {
		Work work = workService.findWork(workId);
		Assert.notNull(work);
		InvoicePaymentTransaction paymentTrx = (InvoicePaymentTransaction) registerTransactionDAO.get(invoicePaymentTrxId);
		Assert.notNull(paymentTrx);
		AbstractInvoice invoiceSummary = paymentTrx.getInvoice();
		Assert.notNull(invoiceSummary);

		List<ConstraintViolation> violations = workValidationService.validatePaid(work);
		if (isNotEmpty(violations)) {
			return violations;
		}

		boolean fulfilled = accountRegisterServicePaymentTermsImpl.fulfillWorkPayment(work, invoiceSummary);
		if (fulfilled) {
			WorkResource workResource = workResourceService.findActiveWorkResource(work.getId());
			Assert.notNull(workResource);
			onPostFulfillAssignment(work, workResource.getId(), Calendar.getInstance(), userService.findUserById(userActorId), invoiceSummary);
		} else {
			violations.add(new ConstraintViolation(MessageKeys.Work.FAILED_FULFILLMENT));
		}
		return violations;
	}

	@Override
	public Map<String, List<ConstraintViolation>> transitionPaymentPendingToPaid(List<Long> assignmentIds) {
		Map<String, List<ConstraintViolation>> violations = Maps.newHashMap();
		if (isEmpty(assignmentIds)) {
			return violations;
		}

		List<WorkWorkResourceAccountRegister> workWorkResourceAccountRegisters = workService.findWorkAndWorkResourceForPayment(assignmentIds);
		Map<Long, Long> paidAssignmentsMap =
				accountRegisterServicePaymentTermsImpl.payPaymentTerms(workWorkResourceAccountRegisters);
		List<Long> paidAssignments = Lists.newArrayList(paidAssignmentsMap.keySet());

		//Removing the assignment that got paid from the original list
		assignmentIds.removeAll(paidAssignments);

		//For each assignment that got paid, update the status
		for (Map.Entry<Long, Long> entry : paidAssignmentsMap.entrySet()) {
			Long workId = entry.getKey();
			Work work = workService.findWork(workId);
			onPostPayAssignment(work, entry.getValue(), Calendar.getInstance(), authenticationService.getCurrentUser());
		}
		//For each assignment that didn't got paid, return the work number and the violation
		for (Long workId : assignmentIds) {
			Work work = workService.findWork(workId);
			violations.put(work.getWorkNumber(), Lists.newArrayList(new ConstraintViolation(MessageKeys.Work.UNABLE_TO_PAY_ASSIGNMENT)));
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(assignmentIds));
		return violations;
	}

	@Override
	public void onPostPayAssignment(Work work, Long workResourceId, Calendar paymentDate, User actor, WorkMilestones milestones) {
		Assert.notNull(work);
		Assert.notNull(actor);
		Assert.notNull(milestones);
		Company company = work.getCompany();
		Assert.notNull(company);

		final WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.PAID);

		if (work.isCancelled()) {
			newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.CANCELLED_WITH_PAY);
		}
		work.setWorkStatusType(newWorkStatus);

		logger.debug(String.format("assignment [%d] paid, updating invoice...", work.getId()));
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);

		if (work.isInvoiced()) {
			Invoice invoice = work.getInvoice();
			if(workService.isOfflinePayment(work)) {
				logger.debug(String.format("Paying invoice off platform [invoice.id: %d]", invoice.getId()));
				invoice.markAsPaidOffline(actor, paymentDate);
			}
			else {
				logger.debug(String.format("Paying invoice on platform [invoice.id: %d]", invoice.getId()));
				invoice.markAsPaid(actor, paymentDate);
			}
		}
		else {
			logger.warn(String.format("Paid non-invoiced assignment: %d", work.getId()));
		}

		if (work.hasStatement()) {
			Statement statement = billingService.findStatementById(work.getStatementId());
			BigDecimal statementRemainingBalance = statement.getRemainingBalance();
			statementRemainingBalance = statementRemainingBalance.subtract(work.getFulfillmentStrategy().getBuyerTotalCost());
			statement.setRemainingBalance(statementRemainingBalance);

			if (NumberUtilities.isZero(statementRemainingBalance)) {
				statement.markAsPaid(actor, paymentDate);
			}
		}

		if (company.getFirstPaidAssignmentOn() == null) {
			company.setFirstPaidAssignmentOn(paymentDate);
		}

		milestones.setPaidOn(paymentDate);
		milestones.setPaidWithPaymentTerms(work.hasPaymentTerms());
		if (milestones.getDueOn() != null && paymentDate.after(milestones.getDueOn())) {
			milestones.setLatePayment(!DateUtilities.isSameDay(milestones.getDueOn(), paymentDate));
		}

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(
				work.getId(), actor.getId(), authenticationService.getMasqueradeUserId(), null, oldWorkStatus, newWorkStatus)
		);

		WorkActionRequest request = createWorkActionRequest(work.getId(), WorkAuditType.PAY, actor.getId(), null);
		workAuditService.auditWork(request);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getClosedOn(), paymentDate));

		WorkResource workResource = workResourceService.findWorkResourceById(workResourceId);
		summaryService.saveWorkHistorySummary(work.getId(), WorkStatusType.PAID, workResource, paymentDate);

		workEventProcessQueue.onWorkPaid(work.getId(), workResourceId, paymentDate, actor);
		ratingService.markRatingsNonPending(workResource);

		if (work.getWorkStatusType().getCode().equals(WorkStatusType.PAID)) {
			webHookEventService.onWorkPaid(work.getId(), work.getCompany().getId());
			webHookEventService.onWorkApproved(work.getId(), work.getCompany().getId());
		} else if (work.getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED_WITH_PAY)) {
			webHookEventService.onWorkCancelled(work.getId(), work.getCompany().getId());
			webHookEventService.onWorkPaid(work.getId(), work.getCompany().getId());
		}

		// unlock company post pay assignment
		if (company.isLocked()) {
			eventRouter.sendEvent(new UnlockCompanyEvent(work.getCompany().getId()));
		}

		// check if there is still due/overdue invoices, if none, remove warning banner
		if (work.getCompany() != null) {
			eventRouter.sendEvent(new CompanyDueInvoicesEvent(work.getCompany().getId()));
		}
		eventRouter.sendEvent(new UserSearchIndexEvent(workResource.getUser().getId()));
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(request.getWorkId()));
		wmMetricRegistryFacade.meter(work.getWorkStatusType().getCode()).mark();
	}

	@Override
	public void onPostPayAssignment(Work work, Long workResourceId, Calendar paymentDate, User actor) {
		Assert.notNull(work);
		Assert.notNull(work.getId());
		onPostPayAssignment(work, workResourceId, paymentDate, actor, workMilestonesService.findWorkMilestonesByWorkId(work.getId()));
	}

	private void onPostFulfillAssignment(Work work, long workResourceId, Calendar paymentDate, User actor, AbstractInvoice invoiceSummary) {
		Assert.notNull(work);
		Assert.notNull(paymentDate);
		Assert.notNull(actor);
		Assert.notNull(invoiceSummary);
		onPostPayAssignment(work, workResourceId, paymentDate, actor);

		/**
		 * onPostPayAssignment updates the statement balance already (if any),
		 * so we just need to check if the payment was done via an invoice bundle payment and update it too.
		 *
		 */
		if (invoiceSummary.getType().equals(InvoiceSummary.INVOICE_SUMMARY_TYPE) ||
				invoiceSummary.getType().equals(InvoiceCollection.INVOICE_COLLECTION_TYPE)) {

			BigDecimal invoiceRemainingBalance = invoiceSummary.getRemainingBalance();
			invoiceRemainingBalance = invoiceRemainingBalance.subtract(work.getFulfillmentStrategy().getBuyerTotalCost());
			invoiceSummary.setRemainingBalance(invoiceRemainingBalance);

			if (invoiceRemainingBalance.compareTo(BigDecimal.ZERO) == 0) {
				invoiceSummary.setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLED));
				invoiceSummary.setPaymentDate(paymentDate);
				invoiceSummary.setPaidBy(actor);
			}
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
	}

	private WorkActionRequest createWorkActionRequest(Long workId, WorkAuditType auditType, Long userId, User onBehalfOf) {
		WorkActionRequest request = new WorkActionRequest();
		request.setModifierId(userId);
		User masqUser = authenticationService.getMasqueradeUser();
		if (masqUser != null) {
			request.setMasqueradeId(masqUser.getId());
		}
		if (onBehalfOf != null) {
			request.setOnBehalfOfId(onBehalfOf.getId());
		}
		request.setAuditType(auditType);
		request.setWorkId(workId);
		request.setLastActionOn(Calendar.getInstance());
		return request;
	}

	@Override
	public List<ConstraintViolation> transitionToVoid(WorkActionRequest workRequest) throws AccountRegisterConcurrentException {
		Assert.notNull(workRequest);
		Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work ID is required");

		List<ConstraintViolation> violations = workValidationService.validateVoid(workId);

		if (isNotEmpty(violations)) {
			return violations;
		}

		Work work = workService.findWork(workId);
		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.VOID);

		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);

		accountRegisterAuthorizationService.voidWork(work);

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(workId);
		milestones.setVoidOn(DateUtilities.getCalendarNow());

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), oldWorkStatus,
				newWorkStatus));

		summaryService.saveWorkHistorySummary(work);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getMilestonesFieldFromWorkStatus(oldWorkStatus), milestones.getVoidOn()));

		workRequest.setAuditType(WorkAuditType.VOID);
		workAuditService.auditWork(workRequest);

		if (isEmpty(violations) && WorkStatusType.VOID.equals(work.getWorkStatusType().getCode())) {
			wmMetricRegistryFacade.meter(work.getWorkStatusType().getCode()).mark();
		}

		return violations;
	}

	@Override
	public List<ConstraintViolation> transitionToCancel(WorkActionRequest workRequest, CancelWorkDTO cancelWorkDTO) {

		BigDecimal workPrice = new BigDecimal(cancelWorkDTO.getPrice());

		Long workId = workRequest.getWorkId();
		Assert.isTrue(cancelWorkDTO.getPrice() >= 0);
		Assert.notNull(workId, "Work ID is required");
		workRequest.validate();
		Work work = workService.findWork(workId);
		Assert.notNull(work, "Work is required");

		List<ConstraintViolation> violations = workValidationService.validateCancel(work);

		if (isNotEmpty(violations)) {
			return violations;
		}

		Calendar cancelledOn = DateUtilities.getCalendarNow();

		work.setCancelledOn(cancelledOn);
		work.setCancellationReasonType(new CancellationReasonType(cancelWorkDTO.getCancellationReasonTypeCode()));
		WorkResource workResource = workResourceService.findActiveWorkResource(workId);
		workResource.setCancelledAssignment(true);

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(workId);
		milestones.setCancelledOn(cancelledOn);

		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.CANCELLED);

		Boolean isPaid = workPrice.compareTo(BigDecimal.ZERO) > 0;

		if (isPaid) {
			// this will need a closed on date as well;
			work.setClosedOn(cancelledOn);

			if (cancelWorkDTO.getPrice() != null) {
				work.getPricingStrategy().getFullPricingStrategy().setOverridePrice(BigDecimal.valueOf(cancelWorkDTO.getPrice()));
			} else {
				// defensive; in case some other override price was in the
				// database. Setting to null prevents the transaction from running
				work.getPricingStrategy().getFullPricingStrategy().setOverridePrice(null);
			}

			if (work.hasPaymentTerms()) {
				newWorkStatus = new WorkStatusType(WorkStatusType.CANCELLED_PAYMENT_PENDING);
			} else {
				newWorkStatus = new WorkStatusType(WorkStatusType.CANCELLED_WITH_PAY);
			}
			accountRegisterAuthorizationService.authorizeOnCompleteWork(workResource);

			Calendar dueOn = work.getDueDate();
			work.setDueOn(dueOn);
			milestones.setDueOn(dueOn);
			work.setWorkStatusType(newWorkStatus);
			// always generate an invoice
			billingService.generateInvoiceForWork(work);

			if (!work.hasPaymentTerms()) {
				onPostPayAssignment(work, workResource.getId(), dueOn, authenticationService.getCurrentUser());
			}

		} else { // transition to cancelled status if not being paid
			accountRegisterAuthorizationService.voidWork(work);
			work.setWorkStatusType(newWorkStatus);
		}

		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);
		workService.cleanUpDeliverablesForReassignmentOrCancellation(workResource);
		userNotificationService.onWorkCancelled(work, workResource, cancelWorkDTO, isPaid);
		summaryService.saveWorkHistorySummary(work, workResource, WorkStatusType.CANCELLED);

		final WorkStatusType oldWorkStatus = work.getWorkStatusType();
		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(
				work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), oldWorkStatus, newWorkStatus
		));

		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getMilestonesFieldFromWorkStatus(oldWorkStatus), cancelledOn));

		workResourceService.addLabelToWorkResourceAfterCancellation(workResource.getId(), new CancellationReasonType(cancelWorkDTO.getCancellationReasonTypeCode()));
		scorecardCache.evictAllResourceScoreCardsForUser(workResource.getUser().getId());

		workRequest.setAuditType(WorkAuditType.CANCEL);
		workAuditService.auditWork(workRequest);

		if (work.getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED)
				|| work.getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED_PAYMENT_PENDING)
				|| work.getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED_WITH_PAY)) {
			webHookEventService.onWorkCancelled(workId, work.getCompany().getId());

			if (work.getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED_WITH_PAY)) {
				webHookEventService.onWorkPaid(workId, work.getCompany().getId());
			}
		}

		if (work.getInvoice() != null) {
			eventRouter.sendEvent(new WorkInvoiceGenerateEvent(work.getInvoice().getId(), work.getId(), WorkInvoiceSendType.ALL));
		}

		return violations;
	}

	@Override
	public void transitionToDeclined(WorkActionRequest workRequest) {
		Assert.notNull(workRequest);
		Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work is required");

		Work work = workService.findWork(workId);
		Assert.notNull(work, "Work is required");

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(workId);
		milestones.setDeclinedOn(DateUtilities.getCalendarNow());

		WorkStatusType oldWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.SENT);
		WorkStatusType newWorkStatusType = WorkStatusType.newWorkStatusType(WorkStatusType.DECLINED);

		summaryService.saveWorkHistorySummary(work);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatusType, DateUtilities.getSecondsBetween(milestones.getSentOn(), milestones.getDeclinedOn()));

		workRequest.setAuditType(WorkAuditType.DECLINE);
		workAuditService.auditWork(workRequest);

		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);
	}


	@Override
	public void transitionFromAbandonedToOpenWork(Long workerUserId, WorkActionRequest workRequest) {
		Assert.notNull(workRequest);
		Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work is required");

		Work work = workService.findWork(workId);
		Assert.notNull(work);

		logger.info("[transitionFromAbandonedToOpenWork] resource: " + workerUserId);
		WorkResource workResource = workResourceService.findWorkResource(workerUserId, workId);
		WorkResourceStatusType oldWorkResourceStatus = workResource.getWorkResourceStatusType();

		workResource.setWorkResourceStatusType(new WorkResourceStatusType(WorkResourceStatusType.OPEN));
		workResource.setAssignedToWork(false);

		if (work.isResourceConfirmationRequired()) {
			work.setConfirmed(false);
			workResource.setConfirmed(false);
		}

		if (work.isCheckinRequired() || work.isCheckinCallRequired()) {
			workResource.setCheckedIn(false);
		}

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.SENT);
		Long workResourceUserId = workResource.getUser().getId();

		workResourceService.ignoreWorkResourceLabel(workResource.getId(), WorkResourceLabelType.CANCELLED);
		workResourceService.deleteWorkResourceLabel(workResource.getId(), WorkResourceLabelType.CANCELLED);
		userIndexer.reindexById(workResource.getId());

		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);

		workChangeLogService.saveWorkChangeLog(new WorkResourceStatusChangeChangeLog(work.getId(), workResourceUserId,
			authenticationService.getMasqueradeUserId(), null, oldWorkResourceStatus, WorkResourceStatusType.OPEN_STATUS));

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), oldWorkStatus,
			newWorkStatus));

		if (work.getWorkStatusType().getCode().equals(WorkStatusType.SENT)) {
			webHookEventService.onWorkSent(workId, work.getCompany().getId());
		}

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));

		// send worker index event
		if (workResourceUserId != null) {
			eventRouter.sendEvent(new UserSearchIndexEvent(workResourceUserId));
		}
	}


	@Override
	public List<ConstraintViolation> transitionToExceptionAbandonedWork(Long contractorId, WorkActionRequest workRequest, String message) throws AccountRegisterConcurrentException {
		Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work is required");

		Work work = workService.findWork(workId);
		Assert.notNull(work);

		List<ConstraintViolation> violations = workValidationService.validateExceptionAbandonedWork(contractorId, workId, message);
		if (isNotEmpty(violations)) {
			return violations;
		}

		WorkResource workResource = workService.findActiveWorkResource(workId);

		Assert.notNull(workResource);

		workService.cleanUpDeliverablesForReassignmentOrCancellation(workResource);
		deliverableService.removeAllDeliverablesFromWork(workResource.getWork().getId());

		if (!contractorId.equals(workResource.getUser().getId())) {
			violations.add(new ConstraintViolation(MessageKeys.Work.NOT_ACTIVE_RESOURCE));
			return violations;
		}

		WorkResourceStatusType oldWorkResourceStatus = workResource.getWorkResourceStatusType();

		workResource.setWorkResourceStatusType(new WorkResourceStatusType(WorkResourceStatusType.CANCELLED));
		workResource.setAssignedToWork(false);

		if (work.isResourceConfirmationRequired()) {
			work.setConfirmed(false);
			workResource.setConfirmed(false);
		}

		if (work.isCheckinRequired() || work.isCheckinCallRequired()) {
			workResource.setCheckedIn(false);
		}

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.ABANDONED);

		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);
		workSubStatusService.addSystemSubStatus(work.getId(), contractorId, WorkSubStatusType.RESOURCE_CANCELLED, message);

		accountRegisterAuthorizationService.voidWork(work);

		//removes all reschedules + price negotiations + spend limit increases
		Integer numNegotiations = workNegotiationService.findAllNegotiationsByWorkId(workResource.getWork().getId()).size();
		if (numNegotiations > 0) {
			logger.info("transitionToExceptionAbandoned reverting " + numNegotiations + " negotiations ");
			workNegotiationService.cancelAllNegotiationsByCompanyForWork(workResource.getUser().getCompany().getId(), workResource.getWork().getId());
			List<WorkPrice> priceHistory = work.getPriceHistory();

			if (priceHistory.size() > 0) {
				work.setPricingStrategy(priceHistory.get(0).getPricingStrategy());
			}
			for (WorkPrice price : priceHistory) {
				logger.info("transitionToExceptionAbandoned deleting " + price.toString());
				workPriceDAO.delete(price);
			}

		}

		// remove previous rating
		Rating rating = ratingService.findLatestRatingForUserForWork(contractorId, workId);
		if (rating != null) {
			ratingService.deleteRating(rating.getId());
		}

		Long workResourceUserId = workResource.getUser().getId();
		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(workId);
		milestones.setExceptionOn(DateUtilities.getCalendarNow());

		workChangeLogService.saveWorkChangeLog(new WorkResourceStatusChangeChangeLog(work.getId(), workResourceUserId,
				authenticationService.getMasqueradeUserId(), null, oldWorkResourceStatus, WorkResourceStatusType.CANCELLED_STATUS));

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), oldWorkStatus,
				newWorkStatus));

		summaryService.saveWorkHistorySummary(work, workResource);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getMilestonesFieldFromWorkStatus(oldWorkStatus), milestones.getExceptionOn()));

		//check the appointment time to see if it's less than 24 hrs
		DateRange appointmentTime = DateRangeUtilities.getAppointmentTime(work.getSchedule(), workResource.getAppointment());
		boolean isLessThan24HrsFromAppointment = appointmentTime.getFrom().before(Calendar.getInstance()) || DateUtilities.getHoursBetweenFromNow(appointmentTime.getFrom()) < 24;

		WorkResourceLabelDTO workResourceLabelDTO = new WorkResourceLabelDTO(workResource.getId(), WorkResourceLabelType.CANCELLED, true);
		workResourceLabelDTO.setLessThan24HoursFromAppointmentTime(isLessThan24HrsFromAppointment);
		workResourceService.addLabelToWorkResource(workResourceLabelDTO);

		workRequest.setAuditType(WorkAuditType.ABANDONED_EXCEPTION);
		workAuditService.auditAndReindexWork(workRequest);

		//This logic was in the controller
		work.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.SENT));

		// Message all resources who are still available to take on the work
		WorkResourcePagination pagination = new WorkResourcePagination(true);
		pagination.getFilters().put(WorkResourcePagination.FILTER_KEYS.WORK_RESOURCE_STATUS.toString(), WorkResourceStatusType.OPEN);
		pagination = workService.findWorkResources(workId, pagination);

		if (!work.getPricingStrategyType().equals(PricingStrategyType.INTERNAL)) {
			userNotificationService.onWorkReinvited(work, pagination.getResults());
		}

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), WorkStatusType.newWorkStatusType(WorkStatusType.ABANDONED),
				WorkStatusType.newWorkStatusType(WorkStatusType.SENT)));

		summaryService.saveWorkHistorySummary(work);

		NotePagination notePagination = workNoteService.findAllNotesByWorkForCompany(workId, workResource.getUser().getCompany().getId(), new NotePagination(true));
		for (Note note : notePagination.getResults()) {
			if (note.getCreatorId().equals(workResource.getUser().getId())) {
				note.setPrivacy(PrivacyType.PRIVILEGED);
			}
		}

		if (workResourceUserId == null) {
			logger.debug("Trying to reindex null user for abandoned work transition");
		}

		if (isEmpty(violations) && work.getWorkStatusType().getCode().equals(WorkStatusType.SENT)) {
			webHookEventService.onWorkSent(workId, work.getCompany().getId());
		}

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));

		eventRouter.sendEvent(new UserSearchIndexEvent(workResourceUserId));
		return violations;
	}

	@Override
	public List<ConstraintViolation> transitionActiveToSent(WorkActionRequest workRequest, Boolean buyerInitiated) {
		Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work is required");

		Work work = workService.findWork(workId);
		Assert.notNull(work);

		WorkResource resource = workResourceService.findActiveWorkResource(workId);
		Assert.notNull(resource);

		unassignActiveWorkResource(work, resource, buyerInitiated);
		// we need to make sure work has no more resources
 		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.SENT);

		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(
			work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(),
			workRequest.getOnBehalfOfId(), oldWorkStatus, newWorkStatus
		));

		if (work.getWorkStatusType().getCode().equals(WorkStatusType.SENT)) {
			webHookEventService.onWorkSent(workId, work.getCompany().getId());
		}

		summaryService.saveWorkHistorySummary(work);
		workRequest.setAuditType(WorkAuditType.SEND);
		workAuditService.auditWork(workRequest);
		return Collections.emptyList();
	}

	private void unassignActiveWorkResource(Work work, WorkResource activeResource, Boolean buyerInitiated) {
		Assert.notNull(work);
		Assert.notNull(activeResource);
		WorkResourceStatusType oldStatus = activeResource.getWorkResourceStatusType();

		WorkResourceStatusType status = (buyerInitiated) ? WorkResourceStatusType.UNASSIGNED_STATUS : WorkResourceStatusType.CANCELLED_STATUS;
		activeResource.setWorkResourceStatusType(status);
		activeResource.setAssignedToWork(false);
		workResourceService.saveOrUpdate(activeResource);

		workChangeLogService.saveWorkChangeLog(new WorkResourceStatusChangeChangeLog(
			work.getId(), activeResource.getUser().getId(), authenticationService.getMasqueradeUserId(), null, oldStatus, status)
		);
		summaryService.saveWorkResourceHistorySummary(activeResource);
		workAuditService.auditAndReindexWork(
			workActionRequestFactory.create(work, work.getModifierId(), null, authenticationService.getMasqueradeUserId(), WorkAuditType.DECLINE)
		);
	}

	@Override
	public List<ConstraintViolation> transitionDeclinedToSent(WorkActionRequest workRequest) {
		Long workId = workRequest.getWorkId();
		List<ConstraintViolation> violations = Collections.emptyList();
		Work work = workService.findWork(workId);
		if (isNotEmpty(violations)) {
			return violations;
		}

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.SENT);
		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), workRequest.getOnBehalfOfId(), oldWorkStatus,
				newWorkStatus));

		summaryService.saveWorkHistorySummary(work);
		workRequest.setAuditType(WorkAuditType.SEND);
		workAuditService.auditAndReindexWork(workRequest);

		if (isEmpty(violations)) {
			if (work.getWorkStatusType().getCode().equals(WorkStatusType.SENT)) {
				webHookEventService.onWorkSent(workId, work.getCompany().getId());
			}
		}

		return violations;
	}

	private List<ConstraintViolation> transitionToPendingPayment(Work work) {
		Assert.notNull(work, "Unable to find work");

		List<ConstraintViolation> violations = workValidationService.validatePaymentPending(work.getId());

		if (isNotEmpty(violations)) {
			return violations;
		}

		if (work.isClosed()) {
			WorkResource workResource = workResourceService.findActiveWorkResource(work.getId());
			Assert.notNull(workResource);
			accountRegisterAuthorizationService.authorizeOnCompleteWork(workResource);
		}

		WorkStatusType oldWorkStatus = work.getWorkStatusType();
		WorkStatusType newWorkStatus = WorkStatusType.newWorkStatusType(WorkStatusType.PAYMENT_PENDING);

		work.setWorkStatusType(newWorkStatus);
		workSubStatusService.resolveAllInapplicableCustomWorkSubStatuses(work);

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		Calendar dueOn = work.getDueDate();
		milestones.setDueOn(dueOn);
		work.setDueOn(dueOn);

		workChangeLogService.saveWorkChangeLog(new WorkStatusChangeChangeLog(work.getId(), authenticationService.getCurrentUser().getId(), authenticationService.getMasqueradeUserId(), null, oldWorkStatus,
				newWorkStatus));

		summaryService.saveWorkHistorySummary(work);
		summaryService.saveWorkStatusTransitionHistorySummary(work, oldWorkStatus, newWorkStatus, DateUtilities.getSecondsBetween(milestones.getMilestonesFieldFromWorkStatus(oldWorkStatus), milestones.getClosedOn()));

		// Generate invoice
		billingService.generateInvoiceForWork(work);

		WorkActionRequest request = createWorkActionRequest(work.getId(), WorkAuditType.PENDING_PAYMENT, authenticationService.getCurrentUser().getId(), null);
		workAuditService.auditWork(request);

		if (isEmpty(violations) && work.getWorkStatusType().getCode().equals(WorkStatusType.PAYMENT_PENDING)) {
			webHookEventService.onWorkApproved(work.getId(), work.getCompany().getId());
			wmMetricRegistryFacade.meter(work.getWorkStatusType().getCode()).mark();
		}

		return violations;
	}


	protected void updateWorkAccountServiceType(Work work) {
		Assert.notNull(work);
		AccountServiceType serviceType = accountPricingService.findAccountServiceTypeConfiguration(work);
		Assert.notNull(serviceType);
		if (!serviceType.getCode().equals(work.getAccountServiceType().getCode())) {
			work.setAccountServiceType(serviceType);
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		}
	}
}
