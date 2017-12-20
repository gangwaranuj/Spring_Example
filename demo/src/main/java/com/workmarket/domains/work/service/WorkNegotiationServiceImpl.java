package com.workmarket.domains.work.service;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.compliance.model.BaseComplianceCriterion;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.WorkPrice;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.changelog.work.WorkNegotiationRequestedChangeLog;
import com.workmarket.domains.model.changelog.work.WorkNegotiationStatusChangeChangeLog;
import com.workmarket.domains.model.changelog.work.WorkRescheduleRequestedChangeLog;
import com.workmarket.domains.model.google.CalendarSyncSettings;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsModelRope;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkNegotiationDAO;
import com.workmarket.domains.work.dao.WorkPriceDAO;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.dao.state.WorkSubStatusTypeAssociationDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.SpendLimitNegotiationType;
import com.workmarket.domains.work.model.negotiation.WorkApplyNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiationPagination;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.WorkBundleCancelSubmitEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.status.WorkNegotiationResponseStatus;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GoogleCalendarService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.velvetrope.Doorman;
import groovy.lang.Tuple2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Service
public class WorkNegotiationServiceImpl implements WorkNegotiationService {

	@Autowired private WorkNegotiationDAO workNegotiationDAO;

	@Autowired private AuthenticationService authenticationService;
	@Autowired private PricingService pricingService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private UserService userService;
	@Autowired private WorkService workService;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private WorkStatusService workStatusService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private WorkDAO workDAO;
	@Autowired private BaseWorkDAO abstractWorkDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkAuditService workAuditService;
	@Autowired private WorkSubStatusTypeAssociationDAO workSubStatusTypeAssociationDAO;
	@Autowired private WorkPriceDAO workPriceDAO;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private GoogleCalendarService googleCalendarService;
	@Autowired private WorkResourceDetailCache workResourceDetailCache;
	@Autowired private ServiceMessageHelper messageHelper;
	@Autowired private WorkBundleRouting workBundleRouting;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private VendorService vendorService;
	@Autowired private ComplianceService complianceService;
	@Qualifier("avoidScheduleConflictsModelDoorman")
	@Autowired private Doorman doorman;

	private static final Log logger = LogFactory.getLog(WorkNegotiationServiceImpl.class);

	private static final Set<Class<? extends AbstractWorkNegotiation>> PRICING_NEGOTIATION_TYPES = ImmutableSet.of(
			WorkBudgetNegotiation.class, WorkExpenseNegotiation.class, WorkBonusNegotiation.class
	);

	@Override
	public AbstractWorkNegotiation findById(Long negotiationId) {
		Assert.notNull(negotiationId);
		return workNegotiationDAO.get(negotiationId);
	}

	@Override
	public WorkNegotiationPagination findByWork(Long workId, WorkNegotiationPagination pagination) {
		Assert.notNull(workId);
		return workNegotiationDAO.findByWork(workId, pagination);
	}

	@Override
	public WorkNegotiationPagination findByUserForWork(Long userId, Long workId, WorkNegotiationPagination pagination) {
		Assert.notNull(workId);
		return workNegotiationDAO.findByUserForWork(userId, workId, pagination);
	}

	@Override
	public WorkNegotiationPagination findByCompanyForWork(Long companyId, Long workId, WorkNegotiationPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(workId);
		return workNegotiationDAO.findByCompanyForWork(companyId, workId, pagination);
	}

	@Override
	public WorkNegotiation findLatestByUserForWork(Long userId, Long workId) {
		Assert.notNull(userId);
		Assert.notNull(workId);
		final WorkNegotiation negotiation = workNegotiationDAO.findLatestByUserForWork(userId, workId);
		if (negotiation != null) {
			Hibernate.initialize(negotiation.getRequestedBy());
			Hibernate.initialize(negotiation.getApprovedBy());
		}
		return negotiation;
	}

	@Override
	public WorkNegotiation findLatestApprovedByUserForWork(Long userId, Long workId) {
		Assert.notNull(userId);
		Assert.notNull(workId);
		return workNegotiationDAO.findLatestApprovedByUserForWork(userId, workId);
	}

	@Override
	public WorkNegotiation findLatestApprovedByCompanyForWork(Long companyId, Long workId) {
		Assert.notNull(companyId);
		Assert.notNull(workId);
		return workNegotiationDAO.findLatestApprovedByCompanyForWork(companyId, workId);
	}

	@Override
	public WorkRescheduleNegotiation findLatestApprovedRescheduleRequestForWork(Long workId) {
		Assert.notNull(workId);
		return workNegotiationDAO.findLatestApprovedRescheduleRequestForWork(workId);
	}

	@Override
	public WorkRescheduleNegotiation findLatestActiveRescheduleRequestByCompanyForWork(boolean isResource, Long companyId, Long workId) {
		Assert.notNull(companyId);
		Assert.notNull(workId);
		return workNegotiationDAO.findLatestActiveRescheduleRequestByCompanyForWork(isResource, companyId, workId);
	}

	@Override
	public List<WorkExpenseNegotiation> findPreCompletionExpenseIncreasesForWork(Long workId) {
		Assert.notNull(workId);
		return workNegotiationDAO.findPreCompletionExpenseIncreasesForWork(workId);
	}

	@Override
	public List<WorkBonusNegotiation> findPreCompletionBonusesForWork(Long workId) {
		Assert.notNull(workId);
		return workNegotiationDAO.findPreCompletionBonusesForWork(workId);
	}

	@Override
	public Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationByUserForWork(Long userId, Long workId) throws Exception {
		Assert.notNull(workId);
		Assert.notNull(userId);
		Optional<WorkExpenseNegotiation> latestActiveExpenseNegotiationOpt = workNegotiationDAO.findLatestActiveExpenseNegotiationByUserForWork(userId, workId);

		if (!latestActiveExpenseNegotiationOpt.isPresent())
			return latestActiveExpenseNegotiationOpt;

		WorkExpenseNegotiation latestExpenseNegotiation = latestActiveExpenseNegotiationOpt.get();
		setStandaloneAdditionalExpenses(latestExpenseNegotiation, workId);

		return latestActiveExpenseNegotiationOpt;
	}

	@Override
	public Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationByCompanyForWork(Long companyId, Long workId) throws Exception {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Optional<WorkExpenseNegotiation> latestActiveExpenseNegotiationOpt = workNegotiationDAO.findLatestActiveExpenseNegotiationByCompanyForWork(companyId, workId);

		if (!latestActiveExpenseNegotiationOpt.isPresent()) {
			return latestActiveExpenseNegotiationOpt;
		}

		WorkExpenseNegotiation latestExpenseNegotiation = latestActiveExpenseNegotiationOpt.get();
		setStandaloneAdditionalExpenses(latestExpenseNegotiation, workId);

		return latestActiveExpenseNegotiationOpt;
	}

	private void setStandaloneAdditionalExpenses(WorkExpenseNegotiation latestExpenseNegotiation, Long workId) {
		// get the newest active expense negotiation and then subtract the highest approved one to get this one negotiation's value
		Optional<WorkExpenseNegotiation> newestOpt = workNegotiationDAO.findLatestApprovedExpenseIncreaseForWork(workId);
		BigDecimal negAdditional = latestExpenseNegotiation.getFullPricingStrategy().getAdditionalExpenses();

		if (newestOpt.isPresent()) {

			BigDecimal newestAdditional = newestOpt.get().getFullPricingStrategy().getAdditionalExpenses();

			if (negAdditional != null && newestAdditional != null && negAdditional.compareTo(newestAdditional) == 1) {
				negAdditional = negAdditional.subtract(newestAdditional);
				latestExpenseNegotiation.setStandaloneAdditionalExpenses(negAdditional);
			}
		}

		// make sure counteroffer expenses are backed out too
		WorkNegotiation counteroffer = workNegotiationDAO.findLatestApprovedForWork(workId);

		if (counteroffer != null) {
			BigDecimal counterofferExp = NumberUtilities.defaultValue(counteroffer.getFullPricingStrategy().getAdditionalExpenses());
			if (negAdditional != null && counterofferExp.compareTo(negAdditional) == -1) {
				latestExpenseNegotiation.setStandaloneAdditionalExpenses(negAdditional.subtract(counterofferExp));
			}
		}
	}

	@Override
	public Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationByUserForWork(Long userId, Long workId) {
		Assert.notNull(workId);
		Assert.notNull(userId);
		Optional<WorkBonusNegotiation> latestBonusOpt = workNegotiationDAO.findLatestActiveBonusNegotiationByUserForWork(userId, workId);

		if (!latestBonusOpt.isPresent())
			return latestBonusOpt;

		WorkBonusNegotiation latestBonus = latestBonusOpt.get();
		setStandaloneBonus(latestBonus, workId);

		return latestBonusOpt;
	}

	@Override
	public Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationByCompanyForWork(Long companyId, Long workId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Optional<WorkBonusNegotiation> latestBonusOpt = workNegotiationDAO.findLatestActiveBonusNegotiationByCompanyForWork(companyId, workId);

		if (!latestBonusOpt.isPresent()) {
			return latestBonusOpt;
		}

		WorkBonusNegotiation latestBonus = latestBonusOpt.get();
		setStandaloneBonus(latestBonus, workId);

		return latestBonusOpt;
	}

	private void setStandaloneBonus(WorkBonusNegotiation latestBonus, Long workId) {
		// get the newest active bonus and then subtract the highest approved one to get this one negotiation's value
		Optional<WorkBonusNegotiation> newestOpt = workNegotiationDAO.findLatestApprovedBonusForWork(workId);
		BigDecimal negAdditional = latestBonus.getFullPricingStrategy().getBonus();

		if (newestOpt.isPresent()) {

			BigDecimal newestAdditional = newestOpt.get().getFullPricingStrategy().getBonus();

			if (negAdditional != null && newestAdditional != null && negAdditional.compareTo(newestAdditional) == 1) {
				negAdditional = negAdditional.subtract(newestAdditional);
				latestBonus.setStandaloneBonus(negAdditional);
			}
		}
	}

	@Override
	public void cancelPendingNegotiationsByUserForWork(Long userId, Long workId) throws Exception {
		WorkNegotiationPagination pagination = new WorkNegotiationPagination(true);
		pagination.getFilters().put(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS.toString(), ApprovalStatus.PENDING.toString());

		WorkNegotiationPagination results = findByUserForWork(userId, workId, pagination);

		for (AbstractWorkNegotiation n : results.getResults()) {
			cancelNegotiation(n.getId());
		}
	}

	@Override
	public void cancelPendingNegotiationsByCompanyForWork(Long companyId, Long workId) {
		WorkNegotiationPagination pagination = new WorkNegotiationPagination(true);
		pagination.getFilters().put(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS.toString(), ApprovalStatus.PENDING.toString());

		pagination = findByCompanyForWork(companyId, workId, pagination);

		for (AbstractWorkNegotiation n : pagination.getResults()) {
			n.setApprovalStatus(ApprovalStatus.REMOVED);
		}
		WorkActionRequest actionRequest = new WorkActionRequest();
		actionRequest.setWorkId(workId);
		actionRequest.setModifierId(authenticationService.getCurrentUser().getId());
		actionRequest.setLastActionOn(Calendar.getInstance());
		actionRequest.setAuditType(WorkAuditType.OFFER_CANCEL);
		workAuditService.auditAndReindexWork(actionRequest);
	}

	@Override
	public void cancelAllNegotiationsByUserForWork(Long userId, Long workId) {
		WorkNegotiationPagination pagination = new WorkNegotiationPagination(true);

		pagination = findByUserForWork(userId, workId, pagination);

		for (AbstractWorkNegotiation n : pagination.getResults()) {
			n.setApprovalStatus(ApprovalStatus.REMOVED);
			n.setDeleted(true);
		}
		WorkActionRequest actionRequest = new WorkActionRequest();
		actionRequest.setWorkId(workId);
		actionRequest.setModifierId(userId);
		actionRequest.setLastActionOn(Calendar.getInstance());
		actionRequest.setAuditType(WorkAuditType.OFFER_CANCEL);
		workAuditService.auditAndReindexWork(actionRequest);
	}

	@Override
	public void cancelAllNegotiationsByCompanyForWork(Long companyId, Long workId) {
		WorkNegotiationPagination pagination = new WorkNegotiationPagination(true);

		pagination = findByCompanyForWork(companyId, workId, pagination);

		for (AbstractWorkNegotiation n : pagination.getResults()) {
			n.setApprovalStatus(ApprovalStatus.REMOVED);
			n.setDeleted(true);
		}
		WorkActionRequest actionRequest = new WorkActionRequest();
		actionRequest.setWorkId(workId);
		actionRequest.setModifierId(authenticationService.getCurrentUser().getId());
		actionRequest.setLastActionOn(Calendar.getInstance());
		actionRequest.setAuditType(WorkAuditType.OFFER_CANCEL);
		workAuditService.auditAndReindexWork(actionRequest);
	}

	@Override
	public void cancelAllNegotiationsForWork(Long workId) {
		WorkNegotiationPagination pagination = new WorkNegotiationPagination(true);

		pagination = findByWork(workId, pagination);

		for (AbstractWorkNegotiation negotiation : pagination.getResults()) {
			negotiation.setApprovalStatus(ApprovalStatus.REMOVED);
			negotiation.setDeleted(true);
		}

		Work work = workService.findWork(workId, false);
		WorkActionRequest actionRequest = new WorkActionRequest(work);
		actionRequest.setLastActionOn(Calendar.getInstance());
		actionRequest.setAuditType(WorkAuditType.OFFER_CANCEL);
		workAuditService.auditAndReindexWork(actionRequest);
	}

	@Override
	public WorkNegotiationResponse createExpenseIncreaseNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception {
		Work work = workDAO.get(checkNotNull(workId));
		User currentUser = checkNotNull(userService.getUser(authenticationService.getCurrentUser().getId()));
		validatePriceNegotiationDto(WorkBonusNegotiation.class, dto, checkNotNull(work), currentUser);

		// if resource is initiating a second request, this should overwrite any others
		if (dto.isInitiatedByResource()) {
			List<WorkExpenseNegotiation> existingExpNegs = workNegotiationDAO.findAllActiveExpenseNegotiationsByCompanyAndWork(currentUser.getCompany().getId(), work.getId());

			if (CollectionUtils.isNotEmpty(existingExpNegs)) {
				for (WorkExpenseNegotiation n : existingExpNegs)
					n.setApprovalStatus(ApprovalStatus.REMOVED);
			}
		}

		WorkExpenseNegotiation negotiation = createPriceNegotiationFromDto(WorkExpenseNegotiation.class, dto, work, currentUser);

		if (!negotiation.isInitiatedByResource()) {
			validateNegotiationSpend(negotiation.getPricingStrategy(), work);
		}
		WorkNote note = saveNote(dto.getNote(), negotiation);
		negotiation.setNote(note);

		workNegotiationDAO.saveOrUpdate(negotiation);
		workSubStatusService.addSystemSubStatus(work.getId(), WorkSubStatusType.EXPENSE_REIMBURSEMENT, StringUtils.EMPTY);
		userNotificationService.onWorkNegotiationRequested(negotiation);
		auditNegotiation(negotiation, dto.getOnBehalfOfId(), WorkAuditType.OFFER);

		if (dto.isInitiatedByResource()) {
			webHookEventService.onNegotiationRequested(workId, work.getCompany().getId(), negotiation);
		} else {
			webHookEventService.onNegotiationAdded(workId, work.getCompany().getId(), negotiation);
		}

		if (dto.isPreapproved()) {
			return approveNegotiation(negotiation.getId());
		}
		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS, negotiation.getId());
	}


	@Override
	public WorkNegotiationResponse createBudgetIncreaseNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception {
		Work work = workDAO.get(checkNotNull(workId));
		User currentUser = checkNotNull(userService.getUser(authenticationService.getCurrentUser().getId()));
		validatePriceNegotiationDto(WorkBonusNegotiation.class, dto, checkNotNull(work), currentUser);

		// if resource is initiating a second request, this should overwrite any others
		if (dto.isInitiatedByResource()) {
			List<WorkBudgetNegotiation> existingBudgetNegs = workNegotiationDAO.findAllActiveBudgetNegotiationsByCompanyAndWork(currentUser.getCompany().getId(), work.getId());

			if (CollectionUtils.isNotEmpty(existingBudgetNegs)) {
				for (WorkBudgetNegotiation n : existingBudgetNegs)
					n.setApprovalStatus(ApprovalStatus.REMOVED);
			}
		}

		WorkBudgetNegotiation negotiation = createPriceNegotiationFromDto(WorkBudgetNegotiation.class, dto, work, currentUser);

		if (!negotiation.isInitiatedByResource()) {
			validateNegotiationSpend(negotiation.getPricingStrategy(), work);
		}

		WorkNote note = saveNote(dto.getNote(), negotiation);
		negotiation.setNote(note);

		workNegotiationDAO.saveOrUpdate(negotiation);
		workSubStatusService.addSystemSubStatus(work.getId(), WorkSubStatusType.BUDGET_INCREASE, StringUtils.EMPTY);
		userNotificationService.onWorkNegotiationRequested(negotiation);
		auditNegotiation(negotiation, dto.getOnBehalfOfId(), WorkAuditType.OFFER);

		if (dto.isInitiatedByResource()) {
			webHookEventService.onNegotiationRequested(workId, work.getCompany().getId(), negotiation);
		} else {
			webHookEventService.onNegotiationAdded(workId, work.getCompany().getId(), negotiation);
		}

		if (dto.isPreapproved())
			return approveNegotiation(negotiation.getId());

		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS, negotiation.getId());
	}


	@Override
	public WorkNegotiationResponse createBonusNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception {

		Work work = workDAO.get(checkNotNull(workId));
		User currentUser = checkNotNull(userService.getUser(authenticationService.getCurrentUser().getId()));
		validatePriceNegotiationDto(WorkBonusNegotiation.class, dto, checkNotNull(work), currentUser);

		// if resource is initiating a second request, they will be unable to and will receive error message
		if (dto.isInitiatedByResource()) {
			List<WorkBonusNegotiation> existingBonuses = workNegotiationDAO.findAllActiveBonusNegotiationsByCompanyAndWork(currentUser.getCompany().getId(), work.getId());

			if (CollectionUtils.isNotEmpty(existingBonuses)) {
				WorkNegotiationResponse response = new WorkNegotiationResponse(WorkNegotiationResponseStatus.DUPLICATES);
				return response;
			}
		}
		WorkBonusNegotiation negotiation = createPriceNegotiationFromDto(WorkBonusNegotiation.class, dto, work, currentUser);

		if (!negotiation.isInitiatedByResource()) {
			validateNegotiationSpend(negotiation.getPricingStrategy(), work);
		}

		WorkNote note = saveNote(dto.getNote(), negotiation);
		negotiation.setNote(note);

		workNegotiationDAO.saveOrUpdate(negotiation);
		workSubStatusService.addSystemSubStatus(work.getId(), WorkSubStatusType.BONUS, StringUtils.EMPTY);
		userNotificationService.onWorkNegotiationRequested(negotiation);
		auditNegotiation(negotiation, dto.getOnBehalfOfId(), WorkAuditType.OFFER);

		if (dto.isInitiatedByResource()) {
			webHookEventService.onNegotiationRequested(workId, work.getCompany().getId(), negotiation);
		} else {
			webHookEventService.onNegotiationAdded(workId, work.getCompany().getId(), negotiation);
		}

		if (dto.isPreapproved()) {
			return approveNegotiation(negotiation.getId());
		}

		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS, negotiation.getId());
	}

	@Override
	public Map<String, BigDecimal> findTotalAdditionalExpensesPaidToCompany(Long companyId, DateRange dateRange) {
		Assert.notNull(dateRange);
		return workNegotiationDAO.findTotalAdditionalExpensesPaidToCompany(companyId, dateRange);
	}

	@Override
	public BigDecimal findTotalAdditionalExpensesPaidToCompanyByBuyer(Long resourceCompanyId, Long buyerCompanyId, DateRange dateRange, List<String> accountServiceType) {
		Assert.notNull(dateRange);
		Assert.notEmpty(accountServiceType);
		return workNegotiationDAO.findTotalAdditionalExpensesPaidToCompanyByBuyer(resourceCompanyId, buyerCompanyId, dateRange, accountServiceType);
	}

	/**
	 * Request a work assignment reschedule.
	 *
	 * @param workId assignment
	 * @param dateRange date range of reschedule
	 * @param notes note
	 * @return a tuple result: a success message on the right (if successful) and a list of errors on the left (if with errors)
	 */
	@Override
	public Tuple2<ImmutableList<String>, String> reschedule(
		final long workId,
		final DateRange dateRange,
		final String notes) {
		final ImmutableList.Builder<String> errorBuilder = ImmutableList.builder();
		final Compliance compliance = complianceService.getComplianceFor(workId, dateRange);

		if (!compliance.isCompliant()) {
			errorBuilder.add(messageHelper.getMessage("assignment.negotiation.schedule.compliance"));
			for (final BaseComplianceCriterion complianceCriterion : compliance.getComplianceCriteria()) {
				if (!complianceCriterion.isMet()) {
					for (final String message : complianceCriterion.getMessages()) {
						errorBuilder.add(messageHelper.getMessage(message));
					}
				}
			}

			return new Tuple2<>(errorBuilder.build(), "");
		}

		String successMessage = "";
		try {
			workService.setAppointmentTime(workId, dateRange, notes);

			if (workService.isAutomaticAppointmentChange(workId, dateRange)) {
				successMessage = messageHelper.getMessage("assignment.set_appointment.success");
			} else {
				successMessage = messageHelper.getMessage("assignment.reschedule.success");
			}
		} catch (Exception e) {
			errorBuilder.add(messageHelper.getMessage("assignment.reschedule.exception"));
		}

		return new Tuple2<>(errorBuilder.build(), successMessage);
	}

	private void validateNegotiationSpend(PricingStrategy pricingStrategy, Work work) {
		// Only allow requests for MORE money
		BigDecimal newSpend = pricingService.calculateMaximumResourceCost(pricingStrategy);
		BigDecimal oldSpend = pricingService.calculateMaximumResourceCost(work.getPricingStrategy());

		Assert.state(newSpend.compareTo(BigDecimal.ZERO) > 0, "Spend limit must be greater than $0");
		Assert.state(newSpend.compareTo(oldSpend) > 0, "New spend limit must be greater than original spend limit.");
	}

	private WorkNote saveNote(String noteText, AbstractWorkNegotiation negotiation) {
		if (StringUtils.isNotEmpty(noteText) && negotiation != null && negotiation.getWork() != null) {
			WorkNote note = new WorkNote(noteText, negotiation.getWork(), PrivacyType.PRIVILEGED);
			WorkResource workResource = workService.findActiveWorkResource(negotiation.getWork().getId());
			if (workResource == null) {
				if (negotiation.getRequestedBy() != null) {
					note.setReplyToId(negotiation.getRequestedBy().getId());
				}
			} else {
				note.setReplyToId(workResource.getUser().getId());
			}
			workNoteService.saveOrUpdate(note);
			return note;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractWorkNegotiation> T createPriceNegotiationFromDto(
			Class<T> clazz, WorkNegotiationDTO dto, Work work, User currentUser) {

		checkState(PRICING_NEGOTIATION_TYPES.contains(clazz));
		AbstractWorkNegotiation negotiation;
		if (clazz.equals(WorkExpenseNegotiation.class)) {
			negotiation = new WorkExpenseNegotiation();

			WorkExpenseNegotiation eNegotiation = (WorkExpenseNegotiation) negotiation;
			eNegotiation.setPricingStrategy(work.getPricingStrategy());

			// If there was a previous expense increase, we want to add to it; otherwise initialize to 0.
			BigDecimal additionalExpenses = eNegotiation.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses();
			eNegotiation.getPricingStrategy().getFullPricingStrategy().setAdditionalExpenses(
					NumberUtilities.nullSafeAddDoubleToBigDecimal(dto.getAdditionalExpenses(), additionalExpenses));

			eNegotiation.setSpendLimitNegotiationType(new SpendLimitNegotiationType(dto.getSpendLimitNegotiationTypeCode()));

		} else if (clazz.equals(WorkBudgetNegotiation.class)) {
			negotiation = new WorkBudgetNegotiation();
			WorkBudgetNegotiation bNegotiation = (WorkBudgetNegotiation) negotiation;
			bNegotiation.setPricingStrategy(work.getPricingStrategy());
			dto.copyAndAddExpensesToFullPricingStrategy(bNegotiation.getPricingStrategy().getFullPricingStrategy());

		} else {
			negotiation = new WorkBonusNegotiation();

			WorkBonusNegotiation bNegotiation = (WorkBonusNegotiation) negotiation;
			bNegotiation.setPricingStrategy(work.getPricingStrategy());

			// If there was a previous bonus, we want to add to it; otherwise initialize to 0.
			BigDecimal bonus = bNegotiation.getPricingStrategy().getFullPricingStrategy().getBonus();
			bNegotiation.getPricingStrategy().getFullPricingStrategy().setBonus(
					NumberUtilities.nullSafeAddDoubleToBigDecimal(dto.getBonus(), bonus));
		}

		negotiation.setApprovalStatus(ApprovalStatus.PENDING);
		negotiation.setInitiatedByResource(dto.isInitiatedByResource());
		negotiation.setWork(work);
		negotiation.setRequestedBy(currentUser);
		negotiation.setRequestedOn(DateUtilities.getCalendarNow());
		negotiation.setVerificationStatus(VerificationStatus.VERIFIED);
		negotiation.setDuringCompletion(work.isComplete());

		return (T) negotiation;
	}


	private void validatePriceNegotiationDto(Class clazz, WorkNegotiationDTO dto, Work work, User currentUser) {
		checkNotNull(dto);

		checkNotNull(!work.getManageMyWorkMarket().getDisablePriceNegotiation(),
				"Price negotiation is disabled.");

		// type specific validations
		if (WorkExpenseNegotiation.class.equals(clazz)) {
			checkNotNull(dto.getSpendLimitNegotiationTypeCode());
			String typeCode = dto.getSpendLimitNegotiationTypeCode();
			if (typeCode.equals(SpendLimitNegotiationType.NEED_MORE_EXPENSES)
					|| typeCode.equals(SpendLimitNegotiationType.NEED_MORE_TIME_AND_EXPENSES)
					|| typeCode.equals(SpendLimitNegotiationType.BONUS)) {
				checkState(dto.getAdditionalExpenses().intValue() > 0, "Need to provide additional expenses amount.");
			}
		} else if (WorkBudgetNegotiation.class.equals(clazz)) {
			checkState(!work.isComplete(), "Budget increases cannot be added after completion");
		}

		if (BooleanUtils.isTrue(dto.isInitiatedByResource())) {
			Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(currentUser.getId());
			boolean isDispatcher = personaPreferenceOptional.isPresent() && personaPreferenceOptional.get().isDispatcher();
			checkState(
				workService.isUserWorkResourceForWork(currentUser.getId(), work.getId()) || isDispatcher,
				"Resources can only negotiate for work they are assigned to"
			);
		} else {
			checkState(
				workService.isAuthorizedToAdminister(work.getId(), currentUser.getId()),
				"Only admins can give auto-approved negotiations on work."
			);
		}
	}

	@Override
	public WorkNegotiationResponse createNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception {
		return createNegotiation(workId, dto, dto.getOnBehalfOfId());
	}

	@Override
	public WorkNegotiationResponse createNegotiation(Long workId, WorkNegotiationDTO dto, Long onBehalfOfId) throws Exception {
		Assert.notNull(workId);
		Assert.notNull(dto);

		Work work = workDAO.get(workId);
		User user = onBehalfOfId != null ? userService.getUser(onBehalfOfId) : authenticationService.getCurrentUser();

		Assert.notNull(work);
		Assert.notNull(user);
		Assert.state(work.isNegotiable(), "Can only negotiate on work that is not accepted.");
		Assert.state(dto.isPriceNegotiation() || dto.isScheduleNegotiation(), "Nothing to negotiate.");

		WorkResource workResource = workService.findWorkResource(user.getId(), work.getId());

		if (work.shouldOpenForWorkResource(workResource) || onBehalfOfId != null) {
			workResource = workResourceDAO.createOpenWorkResource(work, user, false, false);
		}

		Assert.notNull(workResource, "Only resources can negotiate work.");

		validateNegotiationExpiry(dto, work.getSchedule());
		cancelLatestNegotiation(user, work);

		WorkNegotiation negotiation = buildNegotiation(work, dto, user, WorkNegotiation.class);

		userNotificationService.onWorkNegotiationRequested(negotiation);

		if (dto.isInitiatedByResource()) {
			webHookEventService.onNegotiationRequested(workId, work.getCompany().getId(), negotiation);
		} else {
			webHookEventService.onNegotiationAdded(workId, work.getCompany().getId(), negotiation);
		}

		logNegotiation(negotiation, user, onBehalfOfId);
		workResourceDetailCache.evict(work.getId());
		auditNegotiation(negotiation, onBehalfOfId, WorkAuditType.OFFER);
		workResourceService.setDispatcherForWorkAndWorker(work.getId(), user.getId());

		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS, negotiation.getId());
	}

	@Override
	public WorkNegotiationResponse createApplyNegotiation(Long workId, WorkNegotiationDTO dto) throws Exception {
		User currentUser = authenticationService.getCurrentUser();
		return createApplyNegotiation(workId, currentUser.getId(), dto);
	}

	@Override
	public WorkNegotiationResponse createApplyNegotiation(Long workId, Long workerId, WorkNegotiationDTO dto) throws Exception {
		Assert.notNull(workId);
		Assert.notNull(workerId);
		Assert.notNull(dto);

		User user = userDAO.get(workerId);
		Work work = workDAO.get(workId);

		Assert.notNull(user);
		Assert.notNull(work);
		Assert.isTrue(work.getWorkStatusType().isSent(),"Assignment not available for application.");
		Assert.state(!work.getManageMyWorkMarket().getAssignToFirstResource(), "Assignment not available for application.");

		List<AbstractWork> conflicts = Lists.newArrayList();
		doorman.welcome(
			new UserGuest(work.getBuyer()),
			new AvoidScheduleConflictsModelRope(
				workResourceDAO,
				workService,
				work,
				user.getId(),
				conflicts
			)
		);
		Assert.state(conflicts.isEmpty(), messageHelper.getMessage("assignment.apply.user_has_conflicts"));

		boolean isTargetedVendor = vendorService.isVendorInvitedToWork(user.getCompany().getId(), workId);
		if (work.shouldOpenForWorkResource() || isTargetedVendor) {
			if (!workService.isUserWorkResourceForWork(user.getId(), work.getId())) {
				workResourceDAO.createOpenWorkResource(work, user, isTargetedVendor, isTargetedVendor);
			}
		} else {
			Assert.state(workService.isUserWorkResourceForWork(user.getId(), work.getId()), "Only resources can negotiate work.");
		}

		validateNegotiationExpiry(dto, work.getSchedule());
		cancelLatestNegotiation(user, work);

		WorkApplyNegotiation negotiation = buildNegotiation(work, dto, user, WorkApplyNegotiation.class);

		if (!userService.isUserBlockedForCompany(user.getId(), user.getCompany().getId(), work.getCompany().getId())) {
			userNotificationService.onWorkNegotiationRequested(negotiation);
		}

		webHookEventService.onNegotiationRequested(workId, work.getCompany().getId(), negotiation);
		workResourceDetailCache.evict(work.getId());
		auditNegotiation(negotiation, null, WorkAuditType.RESOURCE_APPLY);
		workResourceService.setDispatcherForWorkAndWorker(work.getId(), workerId);

		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS, negotiation.getId());
	}

	private <T extends WorkNegotiation> T buildNegotiation(Work work, WorkNegotiationDTO dto, Class<T> clazz) throws InstantiationException, IllegalAccessException {
		User currentUser = authenticationService.getCurrentUser();
		return buildNegotiation(work, dto, currentUser, clazz);
	}

	private <T extends WorkNegotiation> T buildNegotiation(Work work, WorkNegotiationDTO dto, User user, Class<T> clazz) throws InstantiationException, IllegalAccessException {
		T negotiation = clazz.newInstance();
		negotiation.setWork(work);
		negotiation.setRequestedBy(user);
		negotiation.setRequestedOn(DateUtilities.getCalendarNow());
		negotiation.setApprovalStatus(ApprovalStatus.PENDING);
		negotiation.setVerificationStatus(VerificationStatus.VERIFIED);

		if (dto.getExpiresOn() != null) {
			negotiation.setExpiresOn(DateUtilities.getCalendarFromISO8601(dto.getExpiresOn()));
		}

		if (dto.isPriceNegotiation()) {
			logger.debug("[negotiation] Price negotiation");

			Assert.state(!work.getManageMyWorkMarket().getDisablePriceNegotiation(), "Price negotiation is disabled.");

			negotiation.setPriceNegotiation(true);
			negotiation.setPricingStrategy(pricingService.findPricingStrategyById(dto.getPricingStrategyId()));

			BeanUtilities.copyPropertiesZeroDefault(negotiation.getPricingStrategy().getFullPricingStrategy(), dto);

			BigDecimal newSpend = pricingService.calculateMaximumResourceCost(negotiation.getPricingStrategy());

			Assert.state(newSpend.compareTo(BigDecimal.ZERO) > 0, "Spend limit must be greater than $0");
		}

		if (dto.isScheduleNegotiation()) {
			Assert.hasText(dto.getScheduleFromString());
			logger.debug("[negotiation] Schedule negotiation");

			negotiation.setScheduleNegotiation(true);
			negotiation.setScheduleRangeFlag(dto.getIsScheduleRange());
			negotiation.setScheduleFrom(DateUtilities.getCalendarFromISO8601(dto.getScheduleFromString()));
			if (dto.getIsScheduleRange()) {
				negotiation.setScheduleThrough(DateUtilities.getCalendarFromISO8601(dto.getScheduleThroughString()));
			}
		}

		if (StringUtils.isNotBlank(dto.getNote())) {
			WorkNote note = saveNote(dto.getNote(), negotiation);
			negotiation.setNote(note);
		}

		workNegotiationDAO.saveOrUpdate(negotiation);

		return negotiation;
	}

	private void validateNegotiationExpiry(WorkNegotiationDTO dto, DateRange schedule) {
		if (dto.getExpiresOn() != null) {
			// Definitely overkill validation. Just let them set what they want...
			if (dto.isScheduleNegotiation()) {
				if (dto.getIsScheduleRange() && dto.getScheduleThroughString() != null) {
					Assert.state(DateUtilities.getCalendarFromISO8601(dto.getExpiresOn()).compareTo(DateUtilities.getCalendarFromISO8601(dto.getScheduleThroughString())) < 0, "Expiration must occur prior to the end of the proposed scheduled date range.");
				} else {
					Assert.state(DateUtilities.getCalendarFromISO8601(dto.getExpiresOn()).compareTo(DateUtilities.getCalendarFromISO8601(dto.getScheduleFromString())) < 0, "Expiration must occur prior to the proposed scheduled date.");
				}
			} else {
				if (schedule.isRange() && schedule.getFrom() != null) {
					Assert.state(DateUtilities.getCalendarFromISO8601(dto.getExpiresOn()).compareTo(schedule.getThrough()) < 0, "Expiration must occur prior to the end of the scheduled work range.");
				} else {
					Assert.state(DateUtilities.getCalendarFromISO8601(dto.getExpiresOn()).compareTo(schedule.getFrom()) < 0, "Expiration must occur prior to scheduled work.");
				}
			}
		}
	}

	private void cancelLatestNegotiation(User currentUser, Work work) {
		WorkNegotiation latestNegotiation = findLatestByUserForWork(currentUser.getId(), work.getId());
		if (latestNegotiation != null) {
			latestNegotiation.setApprovalStatus(ApprovalStatus.REMOVED);
		}
	}

	private void logNegotiation(WorkNegotiation negotiation, User currentUser, Long onBehalfOfId) {
		workChangeLogService.saveWorkChangeLog(
			new WorkNegotiationRequestedChangeLog(
				negotiation.getWork().getId(), currentUser.getId(), authenticationService.getMasqueradeUserId(), onBehalfOfId, negotiation));
	}

	private void auditNegotiation(AbstractWorkNegotiation negotiation, Long onBehalfOfId, WorkAuditType auditType) {
		WorkActionRequest request = new WorkActionRequest();
		request.setAuditType(auditType);
		request.setOnBehalfOfId(onBehalfOfId);
		request.setLastActionOn(Calendar.getInstance());
		User masqueradeUser = authenticationService.getMasqueradeUser();
		if (masqueradeUser != null) {
			request.setMasqueradeId(masqueradeUser.getId());
		}
		request.setWorkId(negotiation.getWork().getId());
		request.setModifierId(negotiation.getModifierId());
		workAuditService.auditAndReindexWork(request);
	}

	@Override
	//TODO C&P of createNegotiation - build interfaces
	public WorkRescheduleNegotiation createRescheduleNegotiation(Long workId, WorkNegotiationDTO dto) {
		Assert.notNull(workId);
		Assert.notNull(dto);

		Work work = workDAO.get(workId);
		User currentUser = authenticationService.getCurrentUser();

		Assert.notNull(work);
		Assert.state(work.isReschedulable(), "Can only reschedule work that is active.");

		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(currentUser.getId());
		boolean isDispatcher = personaPreferenceOptional.isPresent() && personaPreferenceOptional.get().isDispatcher();

		Boolean isActiveResource = workService.isUserActiveResourceForWork(currentUser.getId(), work.getId());
		Boolean isAdmin = workService.isAuthorizedToAdminister(work.getId(), currentUser.getId());

		Assert.state(isActiveResource || isAdmin || isDispatcher, "Only an admin, active resource, or dispatcher can reschedule work.");

		cancelLastestRescheduleNegotiation(isActiveResource || isDispatcher, currentUser, work);

		WorkRescheduleNegotiation negotiation = savedWorkRescheduleNegotiation(isActiveResource || isDispatcher, currentUser, work, dto);

		logger.debug("[negotiation] Re-schedule negotiation!");

		workSubStatusService.addSystemSubStatus(negotiation.getWork().getId(), WorkSubStatusType.RESCHEDULE_REQUEST, StringUtils.EMPTY);
		userNotificationService.onWorkNegotiationRequested(negotiation);

		workChangeLogService.saveWorkChangeLog(new WorkRescheduleRequestedChangeLog(workId, currentUser.getId(), authenticationService.getMasqueradeUserId(), null, negotiation));
		auditNegotiation(negotiation, null, WorkAuditType.OFFER);

		if (dto.isInitiatedByResource()) {
			webHookEventService.onNegotiationRequested(workId, work.getCompany().getId(), negotiation);
		} else {
			webHookEventService.onNegotiationAdded(workId, work.getCompany().getId(), negotiation);
		}

		googleCalendarService.updateCalendarEventStatus(workId, CalendarSyncSettings.TENTATIVE);

		cancellAllNegotiationsForWork(workId);

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
		return negotiation;
	}

	private WorkRescheduleNegotiation savedWorkRescheduleNegotiation(boolean isResource, User currentUser, Work work, WorkNegotiationDTO dto) {
		WorkRescheduleNegotiation negotiation = new WorkRescheduleNegotiation();
		negotiation.setWork(work);
		negotiation.setRequestedBy(currentUser);
		negotiation.setRequestedOn(DateUtilities.getCalendarNow());
		negotiation.setApprovalStatus(ApprovalStatus.PENDING);
		negotiation.setVerificationStatus(VerificationStatus.VERIFIED);
		negotiation.setInitiatedByResource(isResource);

		negotiation.setScheduleRangeFlag(dto.getIsScheduleRange());
		negotiation.setScheduleFrom(DateUtilities.getCalendarFromISO8601(dto.getScheduleFromString()));
		if (dto.getIsScheduleRange()) {
			negotiation.setScheduleThrough(DateUtilities.getCalendarFromISO8601(dto.getScheduleThroughString()));
		}

		if (StringUtils.isNotEmpty(dto.getNote())) {
			WorkNote note = saveNote(dto.getNote(), negotiation);
			negotiation.setNote(note);
		}

		if (StringUtils.isNotEmpty(dto.getDeclinedNote())) {
			WorkNote note = saveNote(dto.getDeclinedNote(), negotiation);
			negotiation.setDeclineNote(note);
		}

		//This negotiation was triggered after setting a sub-status
		if (dto.getAssociatedWorkSubStatusTypeId() != null) {
			WorkSubStatusTypeAssociation workSubStatusTypeAssociation = workSubStatusTypeAssociationDAO.findByWorkSubStatusAndWorkId(dto.getAssociatedWorkSubStatusTypeId(), negotiation.getWork().getId());
			if (workSubStatusTypeAssociation.getWorkSubStatusType().isScheduleRequired()) {
				negotiation.setWorkSubStatusTypeAssociation(workSubStatusTypeAssociation);
			} else {
				logger.error("Work sub status : " + dto.getAssociatedWorkSubStatusTypeId() + "doesn't require a reschedule");
			}
		}
		workNegotiationDAO.saveOrUpdate(negotiation);
		return negotiation;
	}

	private void cancelLastestRescheduleNegotiation(boolean isResource, User currentUser, Work work) {
		WorkRescheduleNegotiation latestNegotiation = findLatestActiveRescheduleRequestByCompanyForWork(isResource, currentUser.getCompany().getId(), work.getId());
		if (latestNegotiation != null) {
			latestNegotiation.setApprovalStatus(ApprovalStatus.REMOVED);
		}
	}

	private void cancellAllNegotiationsForWork(long workId) {
		// find an apply negotiation for the same work request (if there is one), and set its approval status to removed
		Collection<WorkNegotiation> negotiations = findAllNegotiationsByWorkId(workId);
		for(WorkNegotiation n : negotiations) {
			if(n.getNegotiationType().equals(WorkNegotiation.APPLY)){
				n.setApprovalStatus(ApprovalStatus.REMOVED);
			}
		}
	}

	@Override
	public WorkNegotiationResponse approveNegotiation(Long negotiationId) throws Exception {
		return approveNegotiation(negotiationId, null);
	}

	@Override
	public WorkNegotiationResponse approveNegotiation(Long negotiationId, Long onBehalfOfUserId) throws Exception {
		Assert.notNull(negotiationId);

		AbstractWorkNegotiation negotiation = workNegotiationDAO.findById(negotiationId);
		Assert.notNull(negotiation);
		WorkNegotiationResponse response = new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS);

		StopWatch timer = new StopWatch("WorkNegotiationService.approveNegotiation");
		timer.start("1. validate negotiation");
		List<String> violations = workValidationService.validateApproveWorkNegotiation(negotiationId, onBehalfOfUserId);
		timer.stop();

		if (CollectionUtils.isNotEmpty(violations)) {
			response.setStatus(WorkNegotiationResponseStatus.FAILURE);
			response.addAllMessages(violations);

			logger.debug("Found violations during approveNegotiation.\n" + timer.prettyPrint());

			return response;
		}

		AbstractWork work = abstractWorkDAO.get(negotiation.getWork().getId());

		// get negotiation requested amount (bonus/expense) before saving,
		// because we can't calculate anymore once saved
		BigDecimal requestedAmount = getRequestedAmount(work, negotiation);

		boolean isAssignmentBundle = workBundleService.isAssignmentBundle(work);

		if (isAssignmentBundle) {
			if (workBundleRouting.isWorkBundlePendingRouting(work.getId())) {
				response.setStatus(WorkNegotiationResponseStatus.FAILURE);
				response.addMessage(messageHelper.getMessage("assignment_bundle.accept.fail.pending_routing"));
				return response;
			}
			for (Work bundledWork : ((WorkBundle)work).getBundle()) {
				if (bundledWork.isSent()) {
					continue;
				}
				Long activeWorkerId = workService.findActiveWorkerId(bundledWork.getId());
				if (activeWorkerId != null && !activeWorkerId.equals(negotiation.getRequestedBy().getId())) {
					// all work in bundle must be assigned to a single worker
					User assignedWorker = userService.getUser(activeWorkerId);
					String acceptUserName = negotiation.getRequestedBy().getFullName();
					String acceptUserEmail = negotiation.getRequestedBy().getEmail();
					String workUserName = assignedWorker.getFullName();
					String workUserEmail = assignedWorker.getEmail();
					response.setStatus(WorkNegotiationResponseStatus.FAILURE);
					response.addMessage(messageHelper.getMessage("assignment_bundle.accept.fail.wronguser", acceptUserName, acceptUserEmail, workUserName, workUserEmail));
					return response;
				}
			}
		}

		User currentUser = authenticationService.getCurrentUser();

		final User approverUser = (onBehalfOfUserId == null) ? currentUser : userDAO.findUserById(onBehalfOfUserId);

		ApprovalStatus oldStatus = negotiation.getApprovalStatus();

		negotiation.setApprovalStatus(ApprovalStatus.APPROVED);
		negotiation.setApprovedBy(approverUser);
		negotiation.setApprovedOn(DateUtilities.getCalendarNow());
		negotiation.setDuringCompletion(work.isComplete());

		timer.start("3. negotiation approved handler");
		// TODO we should not be thowing a 401, instead we could create a InvalidWorkNegotiationException or something similar
		if (negotiation instanceof WorkNegotiation) {
			onNegotiationApproved((WorkNegotiation) negotiation);
		} else if (negotiation instanceof WorkRescheduleNegotiation) {
			onNegotiationApproved((WorkRescheduleNegotiation) negotiation);
		} else if (negotiation instanceof WorkBudgetNegotiation) {
			onNegotiationApproved((WorkBudgetNegotiation) negotiation);
		} else if (negotiation instanceof WorkExpenseNegotiation) {
			onNegotiationApproved((WorkExpenseNegotiation) negotiation);
		} else if (negotiation instanceof WorkBonusNegotiation) {
			onNegotiationApproved((WorkBonusNegotiation) negotiation);
		}
		timer.stop();

		timer.start("4. saving work change log.");
		workChangeLogService.saveWorkChangeLog(new WorkNegotiationStatusChangeChangeLog(
				negotiation.getWork().getId(),
				currentUser.getId(),
				authenticationService.getMasqueradeUserId(),
				approverUser.getId(),
				negotiation,
				oldStatus,
				ApprovalStatus.APPROVED));
		timer.stop();

		timer.start("5. notifying. for bundle? " + isAssignmentBundle);
		if (isAssignmentBundle) {
			userNotificationService.onWorkBundleNegotiationApproved(negotiation);
		} else {
			userNotificationService.onWorkNegotiationApproved(negotiation);
		}
		timer.stop();

		timer.start("6. auditing negotiation.");
		auditNegotiation(negotiation, onBehalfOfUserId, WorkAuditType.OFFER_APPROVE);
		timer.stop();

		// For budget/expense/bonus negotiations initiated by buyer, there's no approval required
		// so we don't trigger a "[Negotiation Type] Approved" event... we only send the "[Negotiation Type] Added" event for these.
		if (negotiation.isInitiatedByResource() || negotiation instanceof WorkRescheduleNegotiation) {
			webHookEventService.onNegotiationApproved(work.getId(), work.getCompany().getId(), negotiation, requestedAmount);
		}

		if (negotiation instanceof WorkRescheduleNegotiation) {
			WorkResource workResource = workService.findActiveWorkResource(work.getId());
			if (workResource != null) {
				workResource.setCheckedIn(false);
			}
			googleCalendarService.updateCalendarEventSchedule(work.getId());
		}

		logger.debug("finished negotiation processing: " + timer.prettyPrint());
		workResourceDetailCache.evict(work.getId());

		response.setWorkNegotiationId(negotiation.getId());
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
		return response;
	}


	private void onNegotiationApproved(WorkNegotiation negotiation) throws Exception {
		if (negotiation.getExpiresOn() != null) {
			Assert.state(negotiation.getExpiresOn().compareTo(DateUtilities.getCalendarNow()) >= 0, "Negotiation expired.");
		}

		// Note - By accepting first and then updating the necessary data, we bypass (at least the in the current notification scheme)
		// all change notifications to the resource for assignments that are still not accepted (i.e. "sent").

		User user = negotiation.getRequestedBy();
		Work work = negotiation.getWork();

		tWorkFacadeService.acceptWork(user, work);

		if (negotiation.isPriceNegotiation()) {
			workService.repriceWork(work.getId(), negotiation.newDTO(), negotiation);
		}

		if (negotiation.isScheduleNegotiation()) {
			workService.updateWorkProperties(negotiation.getWork().getId(), CollectionUtilities.newStringMap(
					"scheduleRangeFlag", negotiation.getScheduleRangeFlag() ? "true" : "false",
					"scheduleFrom", DateUtilities.getISO8601(negotiation.getScheduleFrom()),
					"scheduleThrough", negotiation.getScheduleRangeFlag() ? DateUtilities.getISO8601(negotiation.getScheduleThrough()) : null
			));
		}
	}

	private void onNegotiationApproved(WorkRescheduleNegotiation negotiation) throws Exception {
		Work work = negotiation.getWork();
		Long workId = work.getId();
		Calendar scheduleThrough = DateUtilities.cloneCalendar(negotiation.getScheduleThrough());

		workService.updateWorkProperties(workId, CollectionUtilities.newStringMap(
				"scheduleRangeFlag", negotiation.getScheduleRangeFlag() ? "true" : "false",
				"scheduleFrom", DateUtilities.getISO8601(negotiation.getScheduleFrom()),
				"scheduleThrough", negotiation.getScheduleRangeFlag() ? DateUtilities.getISO8601(scheduleThrough) : null
		));

		WorkResource workResource = workService.findActiveWorkResource(work.getId());

		if (workResource != null) {
			workResource.setAppointment(new DateRange(negotiation.getScheduleFrom(), negotiation.getScheduleThrough()));
			workResourceDAO.saveOrUpdate(workResource);
		}

		// if they were checked in then check them out
		if (workService.isActiveResourceCurrentlyCheckedIn(workId)) {
			workService.checkOutActiveResource(new TimeTrackingRequest()
					.setWorkId(workId)
					.setDate(DateUtilities.getCalendarNow())
					.setNotifyOnCheckOut(false));
		}

		workSubStatusService.resolveSystemSubStatusByAction(workId,
				WorkSubStatusType.RESCHEDULE_REQUEST,
				WorkSubStatusType.RESOURCE_NO_SHOW,
				WorkSubStatusType.RESOURCE_CHECKED_OUT);

		workService.cleanUpDeliverablesForReschedule(workResource);

		workSubStatusService.resolveRequiresRescheduleSubStatus(authenticationService.getCurrentUser().getId(), workId);
	}

	private void onNegotiationApproved(WorkBudgetNegotiation negotiation) throws Exception {

		Long workId = negotiation.getWork().getId();
		List<ConstraintViolation> violations = workService.repriceWork(workId, negotiation.toDTO());
		if (violations.isEmpty()) {

			// if there are other price negotiations on the work, update them with the new information (add them)
			// (this solves for when there are buyer and resource initiated negotiations at the same time)
			Optional<WorkBudgetNegotiation> existingBudget = workNegotiationDAO.findLatestActiveBudgetNegotiationForWork(workId);
			Optional<WorkExpenseNegotiation> existingExpense = workNegotiationDAO.findLatestActiveExpenseNegotiationForWork(workId);
			Optional<WorkBonusNegotiation> existingBonus = workNegotiationDAO.findLatestActiveBonusNegotiationForWork(workId);

			if (existingBudget.isPresent() || existingExpense.isPresent() || existingBonus.isPresent()) {

				if (existingBudget.isPresent()) {

					// :scream:
					Optional<WorkPrice> previousWorkPrice = workPriceDAO.findOriginalPriceHistoryForWork(workId);
					FullPricingStrategy basePs = previousWorkPrice.isPresent() ?
							previousWorkPrice.get().getFullPricingStrategy() :
							workDAO.findWorkById(workId).getPricingStrategy().getFullPricingStrategy();

					FullPricingStrategy newPs = negotiation.getFullPricingStrategy();
					FullPricingStrategy existingPs = existingBudget.get().getFullPricingStrategy();

					// set the new price value of existing neg to be: (existing neg - previous cost) + newly approved neg
					// note that these only copy the allowable editable value
					BigDecimal previousDiff;
					switch (existingPs.getPricingStrategyType()) {
						case FLAT:
							previousDiff = newPs.getFlatPrice().subtract(basePs.getFlatPrice());
							existingPs.setFlatPrice(existingPs.getFlatPrice().add(previousDiff));
							break;
						case PER_HOUR:
							previousDiff = newPs.getMaxNumberOfHours().subtract(basePs.getMaxNumberOfHours());
							existingPs.setMaxNumberOfHours(existingPs.getMaxNumberOfHours().add(previousDiff));
							break;
						case PER_UNIT:
							previousDiff = newPs.getMaxNumberOfUnits().subtract(basePs.getMaxNumberOfUnits());
							existingPs.setMaxNumberOfUnits(existingPs.getMaxNumberOfUnits().add(previousDiff));
							break;
						case BLENDED_PER_HOUR:
							previousDiff = newPs.getMaxBlendedNumberOfHours().subtract(basePs.getMaxBlendedNumberOfHours());
							existingPs.setMaxBlendedNumberOfHours(existingPs.getMaxBlendedNumberOfHours().add(previousDiff));
							break;
						case BLENDED_PER_UNIT:
							previousDiff = newPs.getMaxBlendedNumberOfUnits().subtract(basePs.getMaxBlendedNumberOfUnits());
							existingPs.setMaxBlendedNumberOfUnits(existingPs.getMaxBlendedNumberOfUnits().add(previousDiff));
							break;
					}
				}

				// copy everything except expenses value to existing pending expense negotiations (expenses can't increase here)
				if (existingExpense.isPresent()) {
					FullPricingStrategy existingNegPs = existingExpense.get().getFullPricingStrategy();
					FullPricingStrategy newNegPs = negotiation.getFullPricingStrategy();

					// set the new price value of existing neg to be: (existing neg - previous cost) + newly approved neg
					// note that these only copy the allowable editable value
					switch (existingNegPs.getPricingStrategyType()) {
						case FLAT:
							existingNegPs.setFlatPrice(newNegPs.getFlatPrice());
							break;
						case PER_HOUR:
							existingNegPs.setMaxNumberOfHours(newNegPs.getMaxNumberOfHours());
							break;
						case PER_UNIT:
							existingNegPs.setMaxNumberOfUnits(newNegPs.getMaxNumberOfUnits());
							break;
						case BLENDED_PER_HOUR:
							existingNegPs.setMaxBlendedNumberOfHours(newNegPs.getMaxBlendedNumberOfHours());
							break;
						case BLENDED_PER_UNIT:
							existingNegPs.setMaxBlendedNumberOfUnits(newNegPs.getMaxBlendedNumberOfUnits());
							break;
					}
				}

				// copy everything except expenses value to existing pending bonus negotiations (bonuses can't increase here)
				if (existingBonus.isPresent()) {
					FullPricingStrategy existingBonusPs = existingBonus.get().getFullPricingStrategy();
					FullPricingStrategy newBonusPs = negotiation.getFullPricingStrategy();

					// set the new price value of existing neg to be: (existing neg - previous cost) + newly approved neg
					// note that these only copy the allowable editable value
					switch (existingBonusPs.getPricingStrategyType()) {
						case FLAT:
							existingBonusPs.setFlatPrice(newBonusPs.getFlatPrice());
							break;
						case PER_HOUR:
							existingBonusPs.setMaxNumberOfHours(newBonusPs.getMaxNumberOfHours());
							break;
						case PER_UNIT:
							existingBonusPs.setMaxNumberOfUnits(newBonusPs.getMaxNumberOfUnits());
							break;
						case BLENDED_PER_HOUR:
							existingBonusPs.setMaxBlendedNumberOfHours(newBonusPs.getMaxBlendedNumberOfHours());
							break;
						case BLENDED_PER_UNIT:
							existingBonusPs.setMaxBlendedNumberOfUnits(newBonusPs.getMaxBlendedNumberOfUnits());
							break;
					}
				}
			}
			workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.BUDGET_INCREASE);
		}
	}


	private void onNegotiationApproved(WorkExpenseNegotiation negotiation) throws Exception {

		Long workId = negotiation.getWork().getId();
		BigDecimal existingExpenseAmount = negotiation.getWork().getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses();
		BigDecimal newTotalExpenseAmount = negotiation.getFullPricingStrategy().getAdditionalExpenses();

		List<ConstraintViolation> violations = workService.repriceWork(workId, negotiation.toDTO());

		if (violations.isEmpty()) {

			// if there are other price negotiations on the work, update it with the new information (add them)
			// (this solves for when there are buyer and resource initiated negotiations at the same time)
			Optional<WorkBudgetNegotiation> existingBudget = workNegotiationDAO.findLatestActiveBudgetNegotiationForWork(workId);
			Optional<WorkExpenseNegotiation> existingExpense = workNegotiationDAO.findLatestActiveExpenseNegotiationForWork(workId);
			Optional<WorkBonusNegotiation> existingBonus = workNegotiationDAO.findLatestActiveBonusNegotiationForWork(workId);

			// need to add only the latest bonus amount, not the cumulative total
			BigDecimal newExpenseAmount = NumberUtilities.isPositive(existingExpenseAmount) ?
					newTotalExpenseAmount.subtract(existingExpenseAmount) :
					newTotalExpenseAmount;

			if (existingBudget.isPresent()) {
				FullPricingStrategy ps = existingBudget.get().getFullPricingStrategy();
				ps.setAdditionalExpenses(ps.getAdditionalExpenses().add(newExpenseAmount));
			}
			if (existingExpense.isPresent()) {
				FullPricingStrategy ps = existingExpense.get().getFullPricingStrategy();
				ps.setAdditionalExpenses(ps.getAdditionalExpenses().add(newExpenseAmount));
			}
			if (existingBonus.isPresent()) {
				FullPricingStrategy ps = existingBonus.get().getFullPricingStrategy();
				ps.setAdditionalExpenses(ps.getAdditionalExpenses().add(newExpenseAmount));
			}

			workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.EXPENSE_REIMBURSEMENT);
		}
	}

	private void onNegotiationApproved(WorkBonusNegotiation negotiation) throws Exception {

		Long workId = negotiation.getWork().getId();
		BigDecimal existingBonusAmount = negotiation.getWork().getPricingStrategy().getFullPricingStrategy().getBonus();
		BigDecimal newTotalBonusAmount = negotiation.getFullPricingStrategy().getBonus();

		List<ConstraintViolation> violations = workService.repriceWork(workId, negotiation.toDTO());

		if (violations.isEmpty()) {

			// if there are other price negotiations on the work, update them with the new information (add them)
			// (this solves for when there are buyer and resource initiated negotiations at the same time)
			Optional<WorkBudgetNegotiation> existingBudget = workNegotiationDAO.findLatestActiveBudgetNegotiationForWork(workId);
			Optional<WorkExpenseNegotiation> existingExpense = workNegotiationDAO.findLatestActiveExpenseNegotiationForWork(workId);
			Optional<WorkBonusNegotiation> existingBonus = workNegotiationDAO.findLatestActiveBonusNegotiationForWork(workId);

			// need to add only the latest bonus amount, not the cumulative total
			BigDecimal newBonusAmount = NumberUtilities.isPositive(existingBonusAmount) ?
					newTotalBonusAmount.subtract(existingBonusAmount) :
					newTotalBonusAmount;

			if (existingBudget.isPresent()) {
				FullPricingStrategy ps = existingBudget.get().getFullPricingStrategy();
				ps.setBonus(ps.getBonus().add(newBonusAmount));
			}
			if (existingExpense.isPresent()) {
				FullPricingStrategy ps = existingExpense.get().getFullPricingStrategy();
				ps.setBonus(ps.getBonus().add(newBonusAmount));
			}
			if (existingBonus.isPresent()) {
				FullPricingStrategy ps = existingBonus.get().getFullPricingStrategy();
				ps.setBonus(ps.getBonus().add(newBonusAmount));
			}

			workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.BONUS);
		}
	}

	@Override
	public WorkNegotiationResponse declineNegotiation(Long negotiationId, String declineNote, Long onBehalfOfUserId) throws Exception {
		Assert.notNull(negotiationId);

		AbstractWorkNegotiation negotiation = workNegotiationDAO.get(negotiationId);
		Assert.notNull(negotiation);

		List<String> violations = workValidationService.validateDeclineWorkNegotiation(negotiationId, onBehalfOfUserId);

		if (CollectionUtils.isNotEmpty(violations)) {
			WorkNegotiationResponse response = new WorkNegotiationResponse(WorkNegotiationResponseStatus.FAILURE);
			response.addAllMessages(violations);
			return response;
		}

		User currentUser = authenticationService.getCurrentUser();

		ApprovalStatus oldStatus = negotiation.getApprovalStatus();

		negotiation.setApprovalStatus(ApprovalStatus.DECLINED);

		if (negotiation instanceof WorkNegotiation) {
			onNegotiationDeclined((WorkNegotiation) negotiation);
		} else if (negotiation instanceof WorkRescheduleNegotiation) {
			onNegotiationDeclined((WorkRescheduleNegotiation) negotiation);
		} else if (negotiation instanceof WorkBudgetNegotiation) {
			onNegotiationDeclined((WorkBudgetNegotiation) negotiation);
		} else if (negotiation instanceof WorkExpenseNegotiation) {
			onNegotiationDeclined((WorkExpenseNegotiation) negotiation);
		} else if (negotiation instanceof WorkBonusNegotiation) {
			onNegotiationDeclined((WorkBonusNegotiation) negotiation);
		}

		if (StringUtils.isNotBlank(declineNote)) {
			WorkNote note = new WorkNote(declineNote, negotiation.getWork(), PrivacyType.PRIVILEGED);
			note.setReplyToId(negotiation.getCreatorId());
			workNoteService.saveOrUpdate(note);
			negotiation.setDeclineNote(note);
		}

		Work work = negotiation.getWork();
		workChangeLogService.saveWorkChangeLog(
			new WorkNegotiationStatusChangeChangeLog(
				work.getId(), currentUser.getId(), authenticationService.getMasqueradeUserId(), onBehalfOfUserId, negotiation, oldStatus, ApprovalStatus.DECLINED
			)
		);
		userNotificationService.onWorkNegotiationDeclined(negotiation);
		auditNegotiation(negotiation, onBehalfOfUserId, WorkAuditType.OFFER_DECLINE);
		webHookEventService.onNegotiationDeclined(work.getId(), work.getCompany().getId(), negotiation);
		workResourceDetailCache.evict(work.getId());
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));

		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS, negotiation.getId());
	}

	private void onNegotiationDeclined(WorkNegotiation negotiation) {
		if (negotiation.getExpiresOn() != null) {
			Assert.state(negotiation.getExpiresOn().compareTo(DateUtilities.getCalendarNow()) >= 0, "Negotiation expired.");
		}
	}

	//DM 11-19 added call to transitionToExceptionAbandonedWork as it will clear out status and resources accordingly
	private void onNegotiationDeclined(WorkRescheduleNegotiation negotiation) throws Exception {
		WorkActionRequest workRequest = new WorkActionRequest();
		workRequest.setModifierId(authenticationService.getCurrentUser().getId());
		User masqUser = authenticationService.getMasqueradeUser();

		if (masqUser != null) {
			workRequest.setMasqueradeId(masqUser.getId());
		}

		Long workId = negotiation.getWork().getId();

		// if they were checked in then check them out
		if (workService.isActiveResourceCurrentlyCheckedIn(workId)) {
			workService.checkOutActiveResource(new TimeTrackingRequest()
					.setWorkId(workId)
					.setDate(DateUtilities.getCalendarNow())
					.setNotifyOnCheckOut(false));
		}

		workRequest.setAuditType(WorkAuditType.ABANDON);
		workRequest.setWorkId(workId);
		workStatusService.transitionToExceptionAbandonedWork(authenticationService.getCurrentUser().getId(), workRequest, "reschedule cancel");

		workService.removeWorkerFromWork(workId, false);
		workSubStatusService.resolveSystemSubStatusByAction(workId,
				WorkSubStatusType.RESCHEDULE_REQUEST,
				WorkSubStatusType.RESOURCE_CHECKED_OUT);

		workSubStatusService.resolveRequiresRescheduleSubStatus(authenticationService.getCurrentUser().getId(), negotiation.getWork().getId());

		// In the event that a resource declined a buyer's negotiation, update the schedule per the negotiation
		// since the resource has been unassigned and thus has no say in the matter anymore.
		if (!negotiation.isInitiatedByResource()) {
			workService.updateWorkProperties(negotiation.getWork().getId(), CollectionUtilities.newStringMap(
					"scheduleRangeFlag", negotiation.getScheduleRangeFlag() ? "true" : "false",
					"scheduleFrom", DateUtilities.getISO8601(negotiation.getScheduleFrom()),
					"scheduleThrough", negotiation.getScheduleRangeFlag() ? DateUtilities.getISO8601(negotiation.getScheduleThrough()) : null
			));

			negotiation.setApprovedBy(negotiation.getRequestedBy());
			negotiation.setApprovedOn(DateUtilities.getCalendarNow());
		}
	}


	private void onNegotiationDeclined(WorkBudgetNegotiation negotiation) {
		workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.BUDGET_INCREASE);
	}

	private void onNegotiationDeclined(WorkExpenseNegotiation negotiation) {
		workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.EXPENSE_REIMBURSEMENT);
	}

	private void onNegotiationDeclined(WorkBonusNegotiation negotiation) {
		workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.BONUS);
	}

	@Override
	public WorkNegotiationResponse cancelNegotiation(Long negotiationId) throws Exception {
		Assert.notNull(negotiationId);

		AbstractWorkNegotiation negotiation = workNegotiationDAO.get(negotiationId);
		Assert.notNull(negotiation);

		List<String> violations = workValidationService.validateCancelWorkNegotiation(negotiationId);

		if (CollectionUtils.isNotEmpty(violations)) {
			WorkNegotiationResponse response = new WorkNegotiationResponse(WorkNegotiationResponseStatus.FAILURE);
			response.addAllMessages(violations);
			return response;
		}

		negotiation.setApprovalStatus(ApprovalStatus.REMOVED);
		auditNegotiation(negotiation, null, WorkAuditType.OFFER_CANCEL);

		Work work = negotiation.getWork();

		// resolve the appropriate "___ Requested" label if present
		if (negotiation instanceof WorkRescheduleNegotiation) {
			workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.RESCHEDULE_REQUEST);
		}
		if (negotiation instanceof WorkBudgetNegotiation) {
			workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.BUDGET_INCREASE);
		}
		if (negotiation instanceof WorkExpenseNegotiation) {
			workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.EXPENSE_REIMBURSEMENT);
		}
		if (negotiation instanceof WorkBonusNegotiation) {
			workSubStatusService.resolveSystemSubStatusByAction(negotiation.getWork().getId(), WorkSubStatusType.BONUS);
		}
		workResourceDetailCache.evict(work.getId());

		if (work.isWorkBundle()) {
			WorkBundleCancelSubmitEvent event = eventFactory.buildWorkBundleCancelSubmitEvent(work.getId());
			event.setUser(authenticationService.getCurrentUser());
			eventRouter.sendEvent(event);
		}

		return new WorkNegotiationResponse(WorkNegotiationResponseStatus.SUCCESS, negotiation.getId());
	}

	@Override
	public void extendNegotiationExpiration(Long negotiationId, Integer time, String unit) throws Exception {
		Assert.notNull(negotiationId);

		WorkNegotiation negotiation = (WorkNegotiation) workNegotiationDAO.get(negotiationId);

		User currentUser = userService.getUser(authenticationService.getCurrentUser().getId());

		Assert.state(negotiation.getRequestedBy().equals(currentUser), "Only the requester can extend the expiration.");

		Calendar currentExpiration = (negotiation.getExpiresOn() != null) ? negotiation.getExpiresOn() : DateUtilities.getCalendarNow();
		Calendar updatedTime = DateUtilities.addTime(currentExpiration, time, unit);

		Assert.state(updatedTime.compareTo(negotiation.getWork().getScheduleFrom()) < 0, "Expiration must occur prior to scheduled work.");

		negotiation.setExpiresOn(updatedTime);

		userNotificationService.onWorkNegotiationExpirationExtended(negotiation);
		auditNegotiation(negotiation, null, WorkAuditType.OFFER_EXTEND);
	}

	@Override
	public Collection<WorkNegotiation> findAllNegotiationsByWorkId(Long workId) {
		Assert.notNull(workId);
		return workNegotiationDAO.findAllByWork(workId);
	}

	private BigDecimal getRequestedAmount(AbstractWork work, AbstractWorkNegotiation negotiation) {
		if (negotiation instanceof WorkExpenseNegotiation &&
				((WorkExpenseNegotiation) negotiation).getFullPricingStrategy().getAdditionalExpenses() != null) {
			// subtract the already approved expenses to get the requested amount
			return ((WorkExpenseNegotiation) negotiation).getFullPricingStrategy().getAdditionalExpenses().subtract(
					work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());
		} else if (negotiation instanceof WorkBonusNegotiation &&
				((WorkBonusNegotiation) negotiation).getFullPricingStrategy().getBonus() != null) {
			// subtract the already approved expenses to get the requested amount
			return ((WorkBonusNegotiation) negotiation).getFullPricingStrategy().getBonus().subtract(
					work.getPricingStrategy().getFullPricingStrategy().getBonus());
		}

		return null;
	}
}
