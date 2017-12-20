package com.workmarket.domains.work.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.dao.BlockedAssociationDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.assessment.AssessmentUserAssociationDAO;
import com.workmarket.dao.lane.LaneAssociationDAO;
import com.workmarket.domains.compliance.model.BaseComplianceCriterion;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLogPagination;
import com.workmarket.domains.model.changelog.work.WorkStatusChangeChangeLog;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.payments.dao.InvoiceDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkNegotiationDAO;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.ScheduleNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkApplyNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.exception.account.InvalidPricingException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class WorkValidationServiceImpl implements WorkValidationService {

	@Autowired private WorkService workService;
	@Autowired private WorkNegotiationDAO workNegotiationDAO;
	@Autowired private PricingService pricingService;
	@Autowired private LaneService laneService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private LaneAssociationDAO laneAssociationDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private BlockedAssociationDAO blockedAssociationDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private AssessmentUserAssociationDAO assessmentUserAssociationDAO;
	@Autowired private InvoiceDAO invoiceDAO;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private DeliverableService deliverableService;
	@Autowired private ComplianceService complianceService;
	@Autowired private UserService userService;
	@Autowired private AddressService addressService;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private WorkSaveRequestValidator workSaveRequestValidator;
	@Autowired private UserRoleService userRoleService;
	@Autowired private WorkChangeLogService workChangeLogService;

	private static final Log logger = LogFactory.getLog(WorkValidationServiceImpl.class);
	private static final Map<String, List<String>> negotiationWorkStatusValidationMap;

	static {
		negotiationWorkStatusValidationMap = Maps.newHashMapWithExpectedSize(6);
		negotiationWorkStatusValidationMap.put(AbstractWorkNegotiation.APPLY, Lists.newArrayList(WorkStatusType.SENT));
		negotiationWorkStatusValidationMap.put(AbstractWorkNegotiation.NEGOTIATION, Lists.newArrayList(WorkStatusType.SENT));
		negotiationWorkStatusValidationMap.put(AbstractWorkNegotiation.BUDGET_INCREASE, Lists.newArrayList(WorkStatusType.ACTIVE, WorkStatusType.COMPLETE));
		negotiationWorkStatusValidationMap.put(AbstractWorkNegotiation.EXPENSE, Lists.newArrayList(WorkStatusType.ACTIVE, WorkStatusType.COMPLETE));
		negotiationWorkStatusValidationMap.put(AbstractWorkNegotiation.BONUS, Lists.newArrayList(WorkStatusType.ACTIVE, WorkStatusType.COMPLETE));
		negotiationWorkStatusValidationMap.put(AbstractWorkNegotiation.RESCHEDULE, Lists.newArrayList(WorkStatusType.ACTIVE, WorkStatusType.SENT, WorkStatusType.DRAFT));
	}

	@Override
	public List<ConstraintViolation> validateSaveWorkResource(Long workId, Long userId) {
		Assert.notNull(workId);
		Assert.notNull(userId);

		List<ConstraintViolation> violations = Lists.newArrayList();

		Work work = workDAO.get(workId);
		Assert.notNull(work);

		User user = userDAO.get(userId);
		Assert.notNull(user);

		Company company = work.getCompany();
		LaneContext lane = laneService.getLaneContextForUserAndCompany(userId, company.getId());

		if (work.getPricingStrategy() instanceof InternalPricingStrategy) {
			if (lane == null || !lane.getLaneType().equals(LaneType.LANE_1)) {
				violations.add(new ConstraintViolation(MessageKeys.Work.LANE1_RESOURCE_REQUIRED));
			}
		}

		if (company.isLocked()) {
			if (lane == null || !lane.getLaneType().equals(LaneType.LANE_1)) {
				violations.add(new ConstraintViolation(MessageKeys.Work.LANE1_RESOURCE_REQUIRED));
			}
		}

		if (blockedAssociationDAO.isUserBlockedForCompany(userId, user.getCompany().getId(), company.getId())) {
			violations.add(new ConstraintViolation(MessageKeys.User.BLOCKED_USER_OR_COMPANY));
		}

		if (!validateAssignmentCountry(work, user)) {
			violations.add(new ConstraintViolation(MessageKeys.Work.INVALID_COUNTRY));
		}

		log(workId, violations);
		return violations;
	}

	@Override
	public boolean validateAssignmentCountry(Work work, User user) {
		Assert.notNull(work);
		if (work.isOffsite()) {
			return true;
		}

		Country userCountry = Country.USA_COUNTRY;
		Country workCountry = Country.USA_COUNTRY;

		if (user != null) {
			if (user.getProfile() != null) {
				Profile profile = user.getProfile();
				if (profile.getAddressId() != null) {
					userCountry = addressService.findById(profile.getAddressId()).getCountry();
				} else if (profile.getProfilePostalCode() != null) {
					userCountry = profile.getProfilePostalCode().getCountry();
				}
			}
		}

		if (work.getAddress() != null) {
			workCountry = work.getAddress().getCountry();
		} else if (work.getCompany().getAddress() != null) {
			workCountry = work.getCompany().getAddress().getCountry();
		}

		return (userCountry.getId().equals(workCountry.getId()));
	}

	@Override
	public boolean validateAssignmentCountry(Work work, PeopleSearchResult user) {
		Assert.notNull(work);
		if (work.isOffsite()) {
			return true;
		}

		String userCountry = Country.USA;
		String workCountry = Country.USA;

		if (user != null && user.getAddress() != null) {
			userCountry = user.getAddress().getCountry();
		}

		if (work.getAddress() != null) {
			workCountry = work.getAddress().getCountry().getId();
		} else if (work.getCompany().getAddress() != null) {
			workCountry = work.getCompany().getAddress().getCountry().getId();
		}

		return userCountry.equals(workCountry);
	}

	@Override
	public List<ConstraintViolation> validateComplete(Work work, CompleteWorkDTO dto, Boolean isOnBehalfOf) throws InvalidPricingException {
		Assert.notNull(work);

		List<ConstraintViolation> violations = Lists.newArrayList();
		if (StringUtils.isBlank(dto.getResolution()) && StringUtils.isBlank(work.getResolution())) {
			violations.add(new ConstraintViolation(MessageKeys.Work.RESOLUTION_REQUIRED));
		}

		WorkResource resource = workService.findActiveWorkResource(work.getId());

		if (work.isCheckinRequired() || work.isCheckinCallRequired()) {
			WorkResourceTimeTracking timeTrack = workService.findLatestTimeTrackRecordByWorkResource(resource.getId());
			if (timeTrack != null) {
				if (timeTrack.getCheckedInOn() == null) {
					violations.add(new ConstraintViolation(MessageKeys.Work.CHECKIN_REQUIRED));
				}
				if (timeTrack.getCheckedOutOn() == null) {
					violations.add(new ConstraintViolation(MessageKeys.Work.CHECKOUT_REQUIRED));
				}
			} else {
				violations.add(new ConstraintViolation(MessageKeys.Work.CHECKIN_REQUIRED));
				violations.add(new ConstraintViolation(MessageKeys.Work.CHECKOUT_REQUIRED));
			}
		}

		// Confirm that the resource hasn't exceeded the maximum allowed spend
		// limit.
		// Ensure hours/units validate first; otherwise the pricing service
		// throws a InvalidPricingException.

		BigDecimal maxSpend = pricingService.calculateMaximumResourceCost(work);
		BigDecimal actualSpend = BigDecimal.ZERO;

		if (work.getPricingStrategy() instanceof PerHourPricingStrategy) {
			if (dto.getHoursWorked() == null || dto.getHoursWorked() < 0) {
				violations.add(new ConstraintViolation(MessageKeys.Work.HOURS_WORKED_REQUIRED));
			} else {
				actualSpend = pricingService.calculateTotalResourceCost(work, dto);
			}
		} else if (work.getPricingStrategy() instanceof BlendedPerHourPricingStrategy) {
			if (dto.getHoursWorked() == null || dto.getHoursWorked() < 0) {
				violations.add(new ConstraintViolation(MessageKeys.Work.HOURS_WORKED_REQUIRED));
			} else {
				actualSpend = pricingService.calculateTotalResourceCost(work, dto);
			}
		} else if (work.getPricingStrategy() instanceof PerUnitPricingStrategy) {
			if (dto.getUnitsProcessed() == null) {
				violations.add(new ConstraintViolation(MessageKeys.Work.UNITS_PROCESSED_REQUIRED));
			} else {
				actualSpend = pricingService.calculateTotalResourceCost(work, dto);
			}
		} else {
			actualSpend = pricingService.calculateTotalResourceCost(work, dto);
		}

		if (actualSpend.compareTo(BigDecimal.ZERO) < 0) {
			violations.add(new ConstraintViolation(MessageKeys.Work.INVALID_SPEND_LIMIT));
		}

		if (actualSpend.setScale(2, RoundingMode.HALF_UP).compareTo(maxSpend.setScale(2, RoundingMode.HALF_UP)) > 0) {
			violations.add(new ConstraintViolation(MessageKeys.Work.MAX_SPEND_EXCEEDED));
		}

		if (work.getPricingStrategy().getFullPricingStrategy().getSalesTaxCollectedFlag()) {
			if (work.getPricingStrategy().getFullPricingStrategy().getSalesTaxRate() == null) {
				violations.add(new ConstraintViolation(MessageKeys.Work.TAX_RATE_REQUIRED));
			}
		}

		if (work.isDeliverableRequired()) {
			List<ConstraintViolation> errors = validateDeliverableRequirements(isOnBehalfOf, work);
			if (CollectionUtils.isNotEmpty(errors)) {
				violations.addAll(errors);
			}
		}

		if (resource == null) {
			violations.add(new ConstraintViolation(MessageKeys.Work.WORK_RESOURCE_REQUIRED));
		} else {
			// Verify that the resource has attempted any required assessments
			if (isNotEmpty(work.getRequiredAssessments())) {

				Set<Long> requiredAssessmentIds = Sets.newHashSet();
				for (AbstractAssessment a : work.getRequiredAssessments()) {
					requiredAssessmentIds.add(a.getId());
				}

				List<Long> attempts = assessmentUserAssociationDAO.findSurveysCompletedForWork(work.getId(), resource.getUser().getId());
				requiredAssessmentIds.removeAll(attempts);
				if (isOnBehalfOf) {
					attempts = assessmentUserAssociationDAO.findSurveysCompletedForWorkOnBehalf(work.getId(), resource.getUser().getId());
					requiredAssessmentIds.removeAll(attempts);
				}

				if (isNotEmpty(requiredAssessmentIds)) {
					violations.add(new ConstraintViolation(MessageKeys.Work.INCOMPLETE_ASSESSMENTS));
				}
			}
		}

		log(work.getId(), violations);
		return violations;
	}

	@Override
	public List<ConstraintViolation> validateDelegate(Long workId, Long delegateUserId) {
		Assert.notNull(workId);
		Assert.notNull(delegateUserId);

		List<ConstraintViolation> violations = Lists.newArrayList();

		Work work = workDAO.get(workId);

		Assert.notNull(work);

		User currentUser = authenticationService.getCurrentUser();

		Assert.notNull(currentUser);

		if (DateUtilities.isInPast(work.getScheduleFrom())) {
			violations.add(new ConstraintViolation(MessageKeys.Work.INVALID_TIMEFRAME));
		}

		if (!workService.isUserActiveResourceForWork(currentUser.getId(), workId)) {
			violations.add(new ConstraintViolation(MessageKeys.Work.NOT_ACTIVE_RESOURCE));
		}

		User delegateUser = userDAO.get(delegateUserId);

		LaneAssociation resourceLane = laneService.findActiveAssociationByUserIdAndCompanyId(delegateUserId, currentUser.getCompany().getId());

		if (resourceLane == null || !resourceLane.getLaneType().equals(LaneType.LANE_1) || !authenticationService.isLane1Active(delegateUser)) {
			violations.add(new ConstraintViolation(MessageKeys.Work.LANE1_RESOURCE_REQUIRED));
		}

		if (!laneService.isUserPartOfLane123(delegateUserId, work.getBuyer().getCompany().getId())) {

			LaneAssociation buyerLane = laneService.findActiveAssociationByUserIdAndCompanyId(currentUser.getId(), work.getBuyer().getCompany()
					.getId());

			Assert.notNull(buyerLane);

			if (buyerLane.getLaneType().equals(LaneType.LANE_2)) {
				if (!authenticationService.isLane2Active(delegateUser)) {
					violations.add(new ConstraintViolation(MessageKeys.Work.LANE2_ACTIVE_REQUIRED));
				}
			}
		}

		if (!userRoleService.isAdminOrManager(currentUser)) {
			violations.add(new ConstraintViolation(MessageKeys.Work.ADMIN_OR_MANAGER_ROLE_REQUIRED));
		}
		log(workId, violations);
		return violations;
	}

	@Override
	public List<ConstraintViolation> validateClosed(Work work) {
		List<ConstraintViolation> violations = Lists.newArrayList();
		Assert.notNull(work);

		User currentUser = authenticationService.getCurrentUser();

		Assert.notNull(currentUser);

		if (!currentUser.getCompany().getId().equals(work.getCompany().getId())) {
			violations.add(new ConstraintViolation(MessageKeys.Work.NOT_AUTHORIZED));
			return violations;
		}

		if (!work.isComplete()) {
			violations.add(new ConstraintViolation(MessageKeys.Work.COMPLETE_STATUS_REQUIRED));
		}

		if (!(userRoleService.hasPermissionsForCustomAuth(currentUser.getId(), "PERMISSION_APPROVEWORK"))) {
			violations.add(new ConstraintViolation(MessageKeys.Work.ADMIN_OR_MANAGER_ROLE_REQUIRED));
		}
		log(work.getId(), violations);
		return violations;
	}

	@Override
	public List<ConstraintViolation> validateStopPayment(Work work) {
		List<ConstraintViolation> violations = Lists.newArrayList();
		Assert.notNull(work);

		User currentUser = authenticationService.getCurrentUser();
		Assert.notNull(currentUser);

		if (!currentUser.getCompany().getId().equals(work.getCompany().getId())) {
			violations.add(new ConstraintViolation(MessageKeys.Work.NOT_AUTHORIZED));
			return violations;
		}

		if (!work.isPaymentPending()) {
			violations.add(new ConstraintViolation(MessageKeys.Work.PAYMENT_PENDING_STATUS_REQUIRED));
		}

		if (!(userRoleService.isAdmin(currentUser) || authenticationService.hasPaymentCenterAndEmailsAccess(currentUser.getId(), Boolean.TRUE))) {
			violations.add(new ConstraintViolation(MessageKeys.Work.ADMIN_ROLE_OR_PAYMENT_CENTER_REQUIRED));
		}

		if (work.getInvoice() != null) {
			if (!work.getInvoice().isEditable()) {
				violations.add(new ConstraintViolation(MessageKeys.Work.WORK_INVOICE_LOCKED));
			}
			if (work.getInvoice().isBundled()) {
				InvoiceSummary bundleInvoice = invoiceDAO.findInvoiceSummaryByInvoiceBundledId(work.getInvoice().getId());
				if (bundleInvoice != null) {
					if (!bundleInvoice.isEditable()) {
						violations.add(new ConstraintViolation(MessageKeys.Work.WORK_INVOICE_BUNDLED));
					}
				}
			}
			if (workDAO.isWorkPendingFulfillment(work.getId())) {
				violations.add(new ConstraintViolation(MessageKeys.Work.WORK_IS_PENDING_FULFILLMENT));
			}
		} else {
			violations.add(new ConstraintViolation(MessageKeys.Work.WORK_INVOICE_REQUIRED));
		}

		log(work.getId(), violations);
		return violations;
	}

	@Override
	public List<ConstraintViolation> validateVoid(Long workId) {
		Assert.notNull(workId);

		List<ConstraintViolation> violations = Lists.newArrayList();

		Work work = workDAO.get(workId);

		Assert.notNull(work);

		if (!work.isVoidable()) {
			violations.add(new ConstraintViolation(MessageKeys.Work.INVALID_STATUS_FOR_VOID));
		}
		log(workId, violations);
		return violations;
	}

	@Override
	public List<ConstraintViolation> validateCancel(Work work) {
		Assert.notNull(work);

		List<ConstraintViolation> violations = Lists.newArrayList();
		if (!work.isCancellable()) {
			violations.add(new ConstraintViolation(MessageKeys.Work.INVALID_STATUS_FOR_CANCEL));
		}

		WorkResource workResource = workService.findActiveWorkResource(work.getId());

		if (workResource == null) {
			violations.add(new ConstraintViolation(MessageKeys.Work.WORK_RESOURCE_REQUIRED));
		}
		log(work.getId(), violations);
		return violations;
	}

	@Override
	public List<ConstraintViolation> validatePaid(Work work) {
		List<ConstraintViolation> violations = Lists.newArrayList();
		Assert.notNull(work);

		if (work.isPaid()) {
			violations.add(new ConstraintViolation(MessageKeys.Work.PAID_ASSIGNMENT));
		}

		if (!WorkStatusType.CLOSED_WORK_STATUS_TYPES.contains(work.getWorkStatusType().getCode())) {
			violations.add(new ConstraintViolation(MessageKeys.Work.CLOSED_STATUS_REQUIRED));
		}

		if (work.isInvoiced()) {
			if (work.getFulfillmentStrategy() == null) {
				violations.add(new ConstraintViolation(MessageKeys.Work.WORK_INVOICE_FULFILLMENT_MISMATCH));
			} else if (work.getFulfillmentStrategy().getBuyerFee().compareTo(work.getInvoice().getWorkBuyerFee()) != 0 ||
					work.getFulfillmentStrategy().getWorkPrice().compareTo(work.getInvoice().getWorkPrice()) != 0) {
				violations.add(new ConstraintViolation(MessageKeys.Work.WORK_INVOICE_FULFILLMENT_MISMATCH));
			}
		}
		log(work.getId(), violations);
		return violations;
	}

	@Override
	public List<ConstraintViolation> validateExceptionAbandonedWork(Long userId, Long workId, String message) {
		Assert.notNull(workId);

		List<ConstraintViolation> violations = Lists.newArrayList();

		Work work = workDAO.get(workId);

		Assert.notNull(work);

		if (!WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE).equals(work.getWorkStatusType())) {
			violations.add(new ConstraintViolation(MessageKeys.Work.ACTIVE_STATUS_REQUIRED));
		}
		log(workId, violations);
		return violations;
	}

	@Override
	public List<ConstraintViolation> validatePaymentPending(Long workId) {
		Assert.notNull(workId);

		List<ConstraintViolation> violations = Lists.newArrayListWithCapacity(2);

		Work work = workDAO.get(workId);
		Assert.notNull(work);

		if (work.isPaid()) {
			violations.add(new ConstraintViolation(MessageKeys.Work.PAID_ASSIGNMENT));
		}

		if (!WorkStatusType.CLOSED_WORK_STATUS_TYPES.contains(work.getWorkStatusType().getCode())) {
			violations.add(new ConstraintViolation(MessageKeys.Work.CLOSED_STATUS_REQUIRED));
		}

		log(workId, violations);
		return violations;

	}

	private void validateNegotiationPending(AbstractWorkNegotiation negotiation, List<String> violations) {
		if (!ApprovalStatus.PENDING.equals(negotiation.getApprovalStatus())) {
			if (ApprovalStatus.APPROVED.equals(negotiation.getApprovalStatus())) {
				violations.add("assignment.negotiation.already_approved");
			} else if (ApprovalStatus.DECLINED.equals(negotiation.getApprovalStatus())) {
				violations.add("assignment.negotiation.already_declined");
			} else if (ApprovalStatus.REMOVED.equals(negotiation.getApprovalStatus())) {
				violations.add("assignment.negotiation.already_cancelled");
			} else {
				violations.add("assignment.negotiation.already_modified");
			}
		}
	}

	@Override
	public List<String> validateApproveWorkNegotiation(Long negotiationId, Long onBehalfOfUserId) {
		Assert.notNull(negotiationId);

		AbstractWorkNegotiation negotiation = workNegotiationDAO.get(negotiationId);
		Assert.notNull(negotiation);

		User currentUser = authenticationService.getCurrentUser();
		List<String> violations = Lists.newArrayListWithExpectedSize(4);

		Work work = negotiation.getWork();

		if (negotiation.isInitiatedByResource()) {
			if (currentUser.getCompany().isLocked()) {
				violations.add("assignment.accept_negotiation.locked_account");
				return violations;
			}
			if (!negotiation.isApproved()) {
				if(!workService.isAuthorizedToAdminister(work.getId(), currentUser.getId())) {
					violations.add("assignment.accept_negotiation.not_authorized");
					return violations;
				}
				if (onBehalfOfUserId != null) {
					violations.add("assignment.accept_negotiation.self_authorize");
					return violations;
				}
			}
			User requestor = negotiation.getRequestedBy();
			WorkResource workResource = workResourceDAO.findByUserAndWork(requestor.getId(), work.getId());
			Boolean isDispatched = workResource != null && workResource.getDispatcherId() != null;
			if (requestor != null) {
				if (work.isSent() &&
					!isWorkResourceValidForWork(requestor.getId(), requestor.getCompany().getId(), work.getCompany().getId()) &&
					!(isDispatched && isWorkResourceValidForDispatch(requestor.getId(), work.getCompany().getId()))) {
					violations.add(messageHelper.getMessage("assignment.invalid_user_for_work", requestor.getFullName(), work.getCompany().getEffectiveName()));
					return violations;
				}
			}
		} else if (!workService.isUserActiveResourceForWork(currentUser.getId(), work.getId()) && !workService.isAuthorizedToAcceptNegotiation(work.getId(), currentUser.getId(), negotiation)) {
			violations.add("assignment.accept_negotiation.not_authorized");
			return violations;
		}

		validateNegotiationPending(negotiation, violations);
		if (!violations.isEmpty()) {
			return violations;
		}

		//noinspection unchecked
		WorkStatusType workStatus = negotiation.getWork().getWorkStatusType();
		if (!((List<String>) MapUtils.getObject(negotiationWorkStatusValidationMap, negotiation.getNegotiationType(), Lists.newArrayList())).contains(workStatus.getCode())) {
			violations.add("assignment.accept_negotiation.invalid_assignment_status");
			return violations;
		}

		if (negotiation instanceof ScheduleNegotiation) {
			User worker = work.isSent() ? negotiation.getRequestedBy() : workService.findActiveWorkResource(work.getId()).getUser();
			Compliance compliance;

			if (negotiation.isScheduleNegotiation() || negotiation instanceof WorkRescheduleNegotiation) {
				ScheduleNegotiation scheduleNegotiation = (ScheduleNegotiation) negotiation;
				DateRange schedule = new DateRange(scheduleNegotiation.getScheduleFrom());
				if (scheduleNegotiation.getScheduleRangeFlag()) {
					schedule.setThrough(scheduleNegotiation.getScheduleThrough());
				}
				compliance = complianceService.getComplianceFor(worker, work, schedule);
			} else {
				compliance = complianceService.getComplianceFor(worker, work);
			}

			if (!compliance.isCompliant()) {
				if (worker.equals(currentUser)) {
					violations.add(messageHelper.getMessage("assignment.compliance.user_schedule_not_allowed", work.getCompany().getEffectiveName()));
				} else {
					violations.add("assignment.accept_negotiation.compliance");
					for (BaseComplianceCriterion complianceCriterion :  compliance.getComplianceCriteria()) {
						if (!complianceCriterion.isMet()) {
							for (String message : complianceCriterion.getMessages()){
								violations.add(message);
							}
						}
					}
				}
				return violations;
			}
		}

		boolean hasCounterOfferPermission = userRoleService.hasPermissionsForCustomAuth(currentUser.getId(), Permission.COUNTEROFFER_AUTH);
		boolean hasEditPricingPermission = userRoleService.hasPermissionsForCustomAuth(currentUser.getId(), Permission.EDIT_PRICING_AUTH);
		boolean hasPermission = true;

		if (negotiation instanceof WorkNegotiation // handles both Apply and First to Accept
				&& ((WorkNegotiation) negotiation).isPriceNegotiation()
				&& !hasCounterOfferPermission) {
			hasPermission = false;
		}
		else if (negotiation instanceof WorkBudgetNegotiation
				&& ((WorkBudgetNegotiation) negotiation).isPriceNegotiation()
				&& !hasEditPricingPermission) {
			hasPermission = false;
		}
		else if (negotiation instanceof WorkExpenseNegotiation
				&& ((WorkExpenseNegotiation) negotiation).isPriceNegotiation()
				&& !hasEditPricingPermission) {
			hasPermission = false;
		}
		else if (negotiation instanceof WorkBonusNegotiation
				&& ((WorkBonusNegotiation) negotiation).isPriceNegotiation()
				&& !hasEditPricingPermission) {
			hasPermission = false;
		}

		if (!hasPermission) {
			violations.add("assignment.accept_negotiation.permission");
			return violations;
		}

		BigDecimal spendLimit = currentUser.getSpendLimit();
		if (spendLimit != null) {
			switch (negotiation.getNegotiationType()) {
				case WorkNegotiation.APPLY:
					if (pricingService.calculateMaximumResourceCostPlusFee(
						((WorkApplyNegotiation) negotiation).getPricingStrategy(), work).compareTo(spendLimit) > 0) {
						violations.add(messageHelper.getMessage("assignment.accept.apply.negotiation.spend_limit_exceeded",
							String.format("$%.02f", spendLimit)));
					}
					break;
				case WorkNegotiation.BUDGET_INCREASE:
					if (!userRoleService.isAdmin(currentUser) && pricingService.calculateMaximumResourceCostPlusFee(
						((WorkBudgetNegotiation) negotiation).getPricingStrategy(), work).compareTo(spendLimit) > 0) {
						violations.add(messageHelper.getMessage("assignment.accept.budget.negotiation.spend_limit_exceeded",
							String.format("$%.02f", spendLimit)));
					}
					break;
				case WorkNegotiation.BONUS:
					if (pricingService.calculateMaximumResourceCostPlusFee(
						((WorkBonusNegotiation) negotiation).getPricingStrategy(), work).compareTo(spendLimit) > 0) {
						violations.add(messageHelper.getMessage("assignment.accept.bonus.negotiation.spend_limit_exceeded",
							String.format("$%.02f", spendLimit)));
					}
					break;
				case WorkNegotiation.EXPENSE:
					if (pricingService.calculateMaximumResourceCostPlusFee(
						((WorkExpenseNegotiation) negotiation).getPricingStrategy(), work).compareTo(spendLimit) > 0) {
						violations.add(messageHelper.getMessage("assignment.accept.expense.negotiation.spend_limit_exceeded",
							String.format("$%.02f", spendLimit)));
					}
					break;
			}
		}
		return violations;
	}

	@Override
	public List<String> validateDeclineWorkNegotiation(Long negotiationId, Long onBehalfOfUserId) {
		Assert.notNull(negotiationId);

		AbstractWorkNegotiation negotiation = workNegotiationDAO.get(negotiationId);
		Assert.notNull(negotiation);

		User currentUser = authenticationService.getCurrentUser();
		List<String> violations = Lists.newArrayListWithExpectedSize(3);

		validateNegotiationPending(negotiation, violations);

		if (!violations.isEmpty()) {
			return violations;
		}

		Work work = negotiation.getWork();
		boolean hasPermission = true;

		if (negotiation instanceof WorkNegotiation && ((WorkNegotiation) negotiation).isPriceNegotiation() && !userRoleService.hasPermissionsForCustomAuth(currentUser.getId(),
				work.isAssignToFirstResourceEnabled() ? Permission.COUNTEROFFER_AUTH : Permission.EDIT_PRICING_AUTH)) {
			hasPermission = false;
		} else if (negotiation instanceof WorkBudgetNegotiation && ((WorkBudgetNegotiation) negotiation).isPriceNegotiation() && !userRoleService.hasPermissionsForCustomAuth(currentUser.getId(), Permission.EDIT_PRICING_AUTH)) {
			hasPermission = false;
		} else if (negotiation instanceof WorkExpenseNegotiation && ((WorkExpenseNegotiation) negotiation).isPriceNegotiation() && !userRoleService.hasPermissionsForCustomAuth(currentUser.getId(), Permission.EDIT_PRICING_AUTH)) {
			hasPermission = false;
		} else if (negotiation instanceof WorkBonusNegotiation && ((WorkBonusNegotiation) negotiation).isPriceNegotiation() && !userRoleService.hasPermissionsForCustomAuth(currentUser.getId(), Permission.EDIT_PRICING_AUTH)) {
			hasPermission = false;
		}

		if (!hasPermission) {
			violations.add("assignment.decline_negotiation.permission");
			return violations;
		}

		if (negotiation.isInitiatedByResource()) {
			if (!workService.isAuthorizedToAdminister(work.getId(), currentUser.getId())) {
				violations.add("assignment.decline_negotiation.not_authorized");
			}
		} else {
			if (!workService.isUserActiveResourceForWork(currentUser.getId(), work.getId())) {
				violations.add("assignment.decline_negotiation.not_authorized");
			}
		}

		return violations;
	}

	@Override
	public List<String> validateCancelWorkNegotiation(Long negotiationId) {
		Assert.notNull(negotiationId);

		AbstractWorkNegotiation negotiation = workNegotiationDAO.get(negotiationId);
		Assert.notNull(negotiation);

		User currentUser = authenticationService.getCurrentUser();
		List<String> violations = Lists.newArrayListWithExpectedSize(3);

		validateNegotiationPending(negotiation, violations);

		if (!violations.isEmpty()) {
			return violations;
		}

		if (negotiation.isInitiatedByResource()) {
			Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(currentUser.getId());
			boolean isDispatcher = personaPreferenceOptional.isPresent() && personaPreferenceOptional.get().isDispatcher();
			if (!negotiation.getRequestedBy().equals(currentUser) && !isDispatcher) {
				violations.add("assignment.cancel_negotiation.not_initiator");
			}
		} else {
			if (!workService.isAuthorizedToAdminister(negotiation.getWork().getId(), currentUser.getId())) {
				violations.add("assignment.cancel_negotiation.not_admin");
			}
		}

		return violations;
	}

	@Override
	public List<ConstraintViolation> validateRepriceWork(Long workId, PricingStrategy newPricing, List<WorkResource> workResources, boolean allowLowerCostOnActive) {
		Assert.notNull(workId);

		List<ConstraintViolation> violations = Lists.newArrayListWithCapacity(2);

		Work work = workDAO.get(workId);
		Assert.notNull(work);


		if (!WorkStatusType.REPRICE_WORK_VALID_STATUS_TYPES.contains(work.getWorkStatusType().getCode())) {
			violations.add((new ConstraintViolation(MessageKeys.Work.INVALID_PRICING_CHANGE, work.getWorkStatusType().getDescription())));
			return violations;
		}

		if (!work.isDraft() &&
			(work.getPricingStrategyType() == PricingStrategyType.INTERNAL || newPricing instanceof InternalPricingStrategy)) {
			violations.add((new ConstraintViolation(MessageKeys.Work.INVALID_PRICING_TYPE_CHANGE)));
			return violations;
		}

		BigDecimal originalCost = pricingService.calculateMaximumResourceCost(work.getPricingStrategy());
		BigDecimal newCost = pricingService.calculateMaximumResourceCost(newPricing);

		if (!allowLowerCostOnActive) {
			if (work.isActive() && newCost.compareTo(originalCost) < 0) {
				violations.add(new ConstraintViolation(MessageKeys.Work.MAX_SPEND_TOO_LOW));
			}
		}

		if (!work.isDraft() && work.getCompany().isLocked()) {
			Set<Long> userIds = Sets.newHashSetWithExpectedSize(workResources.size());
			for (WorkResource r : workResources) {
				userIds.add(r.getUser().getId());
			}

			List<LaneAssociation> associations = laneAssociationDAO.findAllAssociationsWhereUserIdIn(work.getCompany().getId(), userIds);
			for (LaneAssociation a : associations) {
				if (!a.getLaneType().equals(LaneType.LANE_1)) {
					violations.add(new ConstraintViolation(MessageKeys.Work.LANE1_RESOURCE_REQUIRED));
					log(workId, violations);
					return violations;
				}
			}
		}
		log(workId, violations);
		return violations;
	}

	@Override
	public boolean isWorkResourceValidForDispatch(long userId, long workCompanyId) {
		User user = userService.getUser(userId);
		return user != null && user.getCompany().getId() != workCompanyId && authenticationService.isLane1Active(user);
	}

	@Override
	public boolean isWorkResourceValidForWork(Long userId, Long userCompanyId, Long workCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(userCompanyId);
		Assert.notNull(workCompanyId);

		// Worker is valid for work if...
		return
			// ... the user has an explicit lane association with the client (1, 2, or 3 all acceptable here)
			laneService.isUserPartOfLane123(userId, workCompanyId)
			// ... or the user is opted-in to search...
			|| laneService.isLane3Active(userId)
			// ... or the user's company is the same as the assignment's company (captures "lane 0")
			|| workCompanyId.equals(userCompanyId)
		;
	}

	public List<ConstraintViolation> validateDeliverableRequirements(boolean isOnBehalf, String workNumber) {
		Assert.hasText(workNumber);

		Work work = workDAO.findWorkByWorkNumber(workNumber);
		Assert.notNull(work);

		return validateDeliverableRequirements(isOnBehalf, work);
	}

	@Override
	public List<ConstraintViolation> validateDeliverableRequirements(boolean isOnBehalfOf, Work work) {
		Assert.notNull(work);

		List<ConstraintViolation> errors = Lists.newArrayList();
		if (!isOnBehalfOf) {
			List<DeliverableRequirement> deliverableRequirements = work.getDeliverableRequirementGroup().getDeliverableRequirements();
			for (DeliverableRequirement deliverableRequirement : deliverableRequirements) {
				if (!deliverableService.isDeliverableRequirementComplete(deliverableRequirement)) {
					errors.add(new ConstraintViolation("deliverable.validation.invalidNumberOfSubmittedFiles", deliverableRequirement.getType().getDescription()));
				}
			}
		}

		return errors;
	}
	@Override
	public List<ConstraintViolation> validateUnassign(WorkStatusType workStatusType, UnassignDTO dto) {
		Assert.notNull(workStatusType);
		Assert.notNull(dto);

		List<ConstraintViolation> errors = Lists.newArrayList();

		if (!WorkStatusType.UNASSIGN_STATUS_TYPES.contains(workStatusType.getCode())) {
		  errors.add(new ConstraintViolation("assignment.unassign.notallowed"));
		}

		if (!StringUtils.isEmpty(dto.getCancellationReasonTypeCode()) &&
				!CancellationReasonType.UNASSIGN_REASON_MAP.containsKey(dto.getCancellationReasonTypeCode())) {
			errors.add(new ConstraintViolation("assignment.unassign.generic_error"));
		}

		if (CancellationReasonType.UNASSIGN_REASON_MAP.containsKey(dto.getCancellationReasonTypeCode()) &&
				StringUtils.isEmpty(dto.getNote())) {
			errors.add(new ConstraintViolation("assignment.unassign.note_required"));
		}
		return errors;
	}

	private void log(Long workId, List<ConstraintViolation> constraints) {
		for (ConstraintViolation c : constraints) {
			logger.debug(String.format("Constraint violation found for work %s: %s", workId, c.toString()));
		}
	}

	@Override
	public List<com.workmarket.thrift.core.ConstraintViolation> validateWorkUniqueId(WorkSaveRequest request, WorkStatusType statusType){
		List<com.workmarket.thrift.core.ConstraintViolation> errors = Lists.newLinkedList();

		workSaveRequestValidator.validateUniqueExternalId(request, errors, WorkStatusType.DRAFT.equals(statusType.getCode()));

		return errors;
	}

	@Override
	public ConstraintViolation validateNotInvoiced(Long workId) {
		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.addFilter(WorkChangeLogPagination.FILTER_KEYS.TYPE, WorkChangeLog.WORK_STATUS_CHANGE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		List<WorkChangeLog> changeLogs = workChangeLogService.findAllChangeLogsByWorkId(workId, pagination).getResults();
		for (WorkChangeLog entry : changeLogs) {
			if (WorkChangeLog.WORK_STATUS_CHANGE.equals(entry.getType())) {
				WorkStatusChangeChangeLog workChangeLog = workChangeLogService.findWorkChangeLog(entry.getId());
				if (WorkStatusType.PAYMENT_PENDING.equals(workChangeLog.getNewStatus().getCode())) {
					return new ConstraintViolation("assignment.unassign.notallowed");
				}
			}
		}
		return null;
	}
}
