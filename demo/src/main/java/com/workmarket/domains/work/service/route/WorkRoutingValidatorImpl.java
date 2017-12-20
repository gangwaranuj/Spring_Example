package com.workmarket.domains.work.service.route;

import com.google.common.collect.Sets;
import com.workmarket.dao.BlockedAssociationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.utility.NumberUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
public class WorkRoutingValidatorImpl implements WorkRoutingValidator {

	@Autowired private BlockedAssociationDAO blockedAssociationDAO;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired
	@Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterServicePrefundImpl;

	@Override
	public WorkAuthorizationResponse validateSearchResult(PeopleSearchResult user, List<Long> resourcesAlreadyOnWork, Work work) {
		boolean isInternalWork = work.getPricingStrategy() instanceof InternalPricingStrategy;
		boolean isCompanyLocked = work.getCompany().isLocked();
		//Validate that is not a work resource already
		if (resourcesAlreadyOnWork.contains(user.getUserId())) {
			return WorkAuthorizationResponse.ALREADY_ADDED;
		}
		//Validate laneType
		if (LaneType.LANE_0.equals(user.getLane())) {
			return WorkAuthorizationResponse.LANE0_NOT_ALLOWED;
		}
		if (LaneType.LANE_4.equals(user.getLane()) && !work.getManageMyWorkMarket().isInstantWorkerPoolEnabled()) {
			return WorkAuthorizationResponse.DISABLED_WORKER_POOL;
		}
		if (isInternalWork && !LaneType.LANE_1.equals(user.getLane())) {
			return WorkAuthorizationResponse.INTERNAL_PRICING;
		}
		if (isCompanyLocked && !LaneType.LANE_1.equals(user.getLane())) {
			return WorkAuthorizationResponse.COMPANY_LOCKED;
		}
		if (blockedAssociationDAO.isUserBlockedForCompany(user.getUserId(), user.getCompanyId(), work.getCompany().getId())) {
			return WorkAuthorizationResponse.BLOCKED_RESOURCE;
		}
		if (!workValidationService.validateAssignmentCountry(work, user)) {
			return WorkAuthorizationResponse.INVALID_COUNTRY;
		}

		//TODO: VALIDATE Industry should we just trust search ?
		return WorkAuthorizationResponse.SUCCEEDED;
	}

	@Override
	public WorkAuthorizationResponse validateUser(User user, Work work, LaneType laneType) {
		Company workCompany = work.getCompany();
		if (LaneType.LANE_0.equals(laneType)) {
			return WorkAuthorizationResponse.LANE0_NOT_ALLOWED;
		}
		if (work.getPricingStrategy() instanceof InternalPricingStrategy && !laneType.isLane1()) {
			return WorkAuthorizationResponse.INTERNAL_PRICING;
		}
		if (workCompany.isLocked() && !laneType.isLane1()) {
			return WorkAuthorizationResponse.COMPANY_LOCKED;
		}
		if (blockedAssociationDAO.isUserBlockedForCompany(user.getId(), user.getCompany().getId(), workCompany.getId())) {
			return WorkAuthorizationResponse.BLOCKED_RESOURCE;
		}
		if (!workValidationService.validateAssignmentCountry(work, user)) {
			return WorkAuthorizationResponse.INVALID_COUNTRY;
		}
		return WorkAuthorizationResponse.SUCCEEDED;
	}

	@Override
	public WorkAuthorizationResponse validateProjectBudget(Work work) {
		Assert.notNull(work);
		if (work.hasProject() && work.getProject().getBudgetEnabledFlag()) {
			WorkCostDTO workCostDTO = accountRegisterServicePrefundImpl.calculateCostOnSentWork(work);
			if (workCostDTO.getTotalBuyerCost() != null && NumberUtilities.isPositive(workCostDTO.getTotalBuyerCost())) {
				if (work.getProject().getRemainingBudget().compareTo(workCostDTO.getTotalBuyerCost()) >= 0) {
					return WorkAuthorizationResponse.SUCCEEDED;
				}
				return WorkAuthorizationResponse.INSUFFICIENT_BUDGET;
			}
		}
		return WorkAuthorizationResponse.SUCCEEDED;
	}

	@Override
	public Set<WorkAuthorizationResponse> validateWorkForRouting(Work work) {
		Assert.notNull(work);
		Set<WorkAuthorizationResponse> result = Sets.newHashSet();
		if (work.isInBundle() && !work.getParent().getWorkStatusType().isSent()) {
			result.add(WorkAuthorizationResponse.INVALID_BUNDLE_STATE);
		}

		if (validateProjectBudget(work).fail()) {
			result.add(WorkAuthorizationResponse.INSUFFICIENT_BUDGET);
		}

		if (isEmpty(result)) {
			WorkAuthorizationResponse moneyAuthorization = accountRegisterAuthorizationService.authorizeWork(work);
			if (moneyAuthorization.fail()) {
				result.add(moneyAuthorization);
				return result;
			}
		}

		if (isEmpty(result)) {
			result.add(WorkAuthorizationResponse.SUCCEEDED);
		}
		return result;
	}

}
