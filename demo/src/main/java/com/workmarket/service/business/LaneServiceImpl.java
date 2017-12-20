package com.workmarket.service.business;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.lane.LaneAssociationDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserLaneRelationshipPagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.changelog.user.UserLaneAddedChangeLog;
import com.workmarket.domains.model.changelog.user.UserLaneRemovedChangeLog;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.dto.CompanyResourcePagination;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.index.UpdateUserSearchIndex;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.thrift.search.cart.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LaneServiceImpl implements LaneService {

	private static final Log logger = LogFactory.getLog(JobServiceImpl.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private WorkService workService;
	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;
	@Autowired private AuthorizationService authorizationService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private LaneAssociationDAO laneAssociationDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserChangeLogService userChangeLogService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private RecruitingService recruitingService;

	@Value("${com.workmarket.network.add}")
	private String STATSD_NETWORK;

	@Override
	public void addUserToCompanyLane1(Long workerUserId, Long companyId) {
		Assert.notNull(companyId);
		addUserToLane(workerUserId, companyId, LaneType.LANE_1);
	}

	@Override
	public void addUserToCompanyLane2(Long workerUserId, Long companyId) {
		Assert.notNull(companyId);
		addUserToLane(workerUserId, companyId, LaneType.LANE_2);
	}

	@Override
	public void addUsersToCompanyLane2(List<Long> workerUserIds, Long companyId) {
		for (Long l : workerUserIds) {
			addUserToCompanyLane2(l, companyId);
		}
	}

	@Override
	public void addUserToCompanyLane3(Long workerUserId, Long companyId) {
		Assert.notNull(companyId);
		addUserToLane(workerUserId, companyId, LaneType.LANE_3);
	}

	@Override
	public void addUsersToCompanyLane3(List<Long> workerUserIds, Long companyId) {
		for (Long userId : workerUserIds) {
			addUserToCompanyLane3(userId, companyId);
		}
	}

	@Override
	@UpdateUserSearchIndex(userIdArgumentPosition = 1)
	public void removeUserFromCompanyLane(Long workerUserId, Long companyId) {
		Assert.notNull(workerUserId);
		Assert.notNull(companyId);

		/*
		 * TODO : implement the logic to remove a user from company for now we
		 * just need to make sure we remove also any association with the
		 * company's clients.
		 */
		LaneAssociation lane = findActiveAssociationByUserIdAndCompanyId(workerUserId, companyId);

		if (lane != null) {
			Assert.state(Boolean.FALSE.equals(lane.getDeleted()));
			// Assert.state(!ApprovalStatus.PENDING_REMOVAL.equals(lane.getApprovalStatus()));

			boolean hasDependencies = hasAnyDependencies(workerUserId, companyId);

			// TODO : Remove user from lane logic goes here (TBD)

			// Remove any association in the company's clients
			// getServiceFactory().getCrmService().removeResourceFromAllClientCompaniesByCompany(companyId,
			// workerUserId);
			if (!hasDependencies) {
				if (logger.isInfoEnabled())
					logger.debug("Removing user id " + lane.getUser().getId() + " from lane " + lane.getLaneType() + " of company id "
						+ lane.getCompany().getId());

				if (ApprovalStatus.PENDING_REMOVAL.equals(lane.getApprovalStatus()))
					lane.setApprovalStatus(ApprovalStatus.REMOVED);

				lane.setDeleted(true);
				userChangeLogService.createChangeLog(new UserLaneRemovedChangeLog(lane.getUser().getId(), authenticationService.getCurrentUserId(),
					authenticationService.getMasqueradeUserId(), lane.getCompany(), lane.getLaneType()));
			} else {
				if (logger.isInfoEnabled()) {
					logger.debug("Setting pending removal user id " + lane.getUser().getId() + " from lane " + lane.getLaneType()
						+ " of company id " + lane.getCompany().getId());
				}
				lane.setApprovalStatus(ApprovalStatus.PENDING_REMOVAL);
			}

			Map<String, Object> params = new HashMap<>();
			params.put(ProfileModificationType.LANE_ASSOCIATION, lane.getLaneType());
			userGroupValidationService.revalidateAllAssociationsByUserAsync(workerUserId, params);
		}
	}

	private boolean hasAnyDependencies(Long workerUserId, Long companyId) {
		Assert.notNull(workerUserId);
		Assert.notNull(companyId);

		return workService.doesWorkerHaveWorkWithCompany(companyId, workerUserId,
				WorkStatusType.CLOSED_WORK_STATUS_TYPES);
	}

	@Override
	public Set<LaneAssociation> findAllAssociationsWhereUserIdIn(Long companyId, Set<Long> userIds) {
		return Sets.newLinkedHashSet(laneAssociationDAO.findAllAssociationsWhereUserIdIn(companyId, userIds));
	}

	@Override
	public List<Long> findAllCompaniesWhereUserIsResource(Long userId, LaneType laneType) {
		return laneAssociationDAO.findAllCompaniesWhereUserIsResource(userId, laneType);
	}

	@Override
	@UpdateUserSearchIndex(userIdArgumentPosition = 1)
	public void updateUserCompanyLaneAssociation(Long workerUserId, Long companyId, LaneType newLaneType) {
		Assert.notNull(workerUserId);
		Assert.notNull(companyId);
		Assert.notNull(newLaneType);

		LaneAssociation association = findActiveAssociationByUserIdAndCompanyId(workerUserId, companyId);
		if (association != null) {
			final LaneType oldLaneType = association.getLaneType();
			if (oldLaneType.compareTo(newLaneType) != 0) {
				final Long actor = authenticationService.getCurrentUserId();
				final Long masqueradeUserId = authenticationService.getMasqueradeUserId();
				final Company company = association.getCompany();
				association.setLaneType(newLaneType);
				association.setDeleted(false);
				userChangeLogService.createChangeLog(new UserLaneRemovedChangeLog(workerUserId, actor,
						masqueradeUserId, company, oldLaneType));
				userChangeLogService.createChangeLog(new UserLaneAddedChangeLog(workerUserId, actor,
						masqueradeUserId, company, newLaneType));
			}
		}
	}

	@Override
	public void updateLanesForUserOnGroupApply(User user, UserGroup userGroup) {
		LaneAssociation laneAssociation = findActiveAssociationByUserIdAndCompanyId(user.getId(), userGroup.getCompany().getId());
		if (laneAssociation == null) {
			if (!userGroup.getRequiresApproval() && authenticationService.isLane3Active(user)) {
				addUserToCompanyLane3(user.getId(), userGroup.getCompany().getId());
			}
		} else if (laneAssociation.getApprovalStatus().isPending() && laneAssociation.getLaneType().isLane2()) {
			RecruitingCampaign campaign = user.getRecruitingCampaign();
			if (campaign != null) {
				// Need this because we might be outside of a hibernate session
				campaign = recruitingService.findRecruitingCampaign(user.getRecruitingCampaign().getId());
				if (campaign != null && campaign.getCompanyUserGroup() != null && userGroup.getId().equals(campaign.getCompanyUserGroup().getId())) {
					laneAssociation.setApprovalStatus(ApprovalStatus.APPROVED);
				}
			}
		}
	}

	@Override
	public LaneAssociation findActiveAssociationByUserIdAndCompanyId(Long workerUserId, Long companyId) {
		return laneAssociationDAO.findActiveAssociationByUserIdAndCompanyId(workerUserId, companyId);
	}

	@Override
	public LaneAssociation findAssociationByUserIdAndCompanyId(Long workerUserId, Long companyId, LaneType laneType) {
		return laneAssociationDAO.findAssociationByUserIdAndCompanyId(workerUserId, companyId, laneType);
	}

	@Override
	@UpdateUserSearchIndex(userIdArgumentPosition = 1)
	public LaneAssociation updateLaneAssociationApprovalStatus(Long workerUserId, Long companyId, ApprovalStatus status) {
		LaneAssociation association = findActiveAssociationByUserIdAndCompanyId(workerUserId, companyId);
		if (association != null) {
			association.setApprovalStatus(status);
		}
		return association;
	}

	@Override
	public boolean isUserPartOfLane123(Long workerUserId, Long companyId) {
		Assert.notNull(workerUserId);
		Assert.notNull(companyId);

		User user = userService.getUser(workerUserId);

		Assert.notNull(user);
		Assert.notNull(user.getCompany());

		LaneAssociation association = findActiveAssociationByUserIdAndCompanyId(workerUserId, companyId);

		return association != null
			&& (association.getLaneType().isLane1() || association.getLaneType().isLane2() || association.getLaneType().isLane3());

	}

	@Override
	public LaneType getLaneTypeForUserAndCompany(Long userId, Long companyId) {
		LaneContext context = getLaneContextForUserAndCompany(userId, companyId);
		return context != null ? context.getLaneType() : LaneType.LANE_4;
	}

	@Override
	public LaneContext getLaneContextForUserAndCompany(Long userId, Long companyId) {
		return getLaneContextForUserAndCompany(userId, companyId, true);
	}

	@Override
	public LaneContext getLaneContextForUserAndCompany(Long userId, Long companyId, boolean requireActiveAccount) {
		User user = userService.getUser(userId);
		LaneAssociation lane = findActiveAssociationByUserIdAndCompanyId(userId, companyId);
		return getLaneContext(companyId, user, lane, requireActiveAccount);
	}

	@VisibleForTesting
	LaneContext getLaneContext(final Long companyId, final User user, final LaneAssociation lane, boolean requireActiveAccount) {
		if (user.getCompany().getId().equals(companyId)) {
			if (lane != null && !lane.getDeleted()) {
				if (lane.getLaneType().isLane1() && (!requireActiveAccount || authenticationService.isLane1Active(user))) {
					return new LaneContext(lane.getLaneType(), lane.getApprovalStatus());
				}
			}
			return new LaneContext(LaneType.LANE_0, ApprovalStatus.APPROVED);
		}

		if (lane != null && !lane.getDeleted()) {
			if (lane.getLaneType().isLane2() && (requireActiveAccount && !authenticationService.isLane2Active(user))) {
				return null;
			} else if (lane.getLaneType().isLane3() && (!authenticationService.isLane3Active(user))) {
				return null;
			} else {
				return new LaneContext(lane.getLaneType(), lane.getApprovalStatus());
			}
		}

		if (authenticationService.isLane4Active(user)) {
			return new LaneContext(LaneType.LANE_4, ApprovalStatus.APPROVED);
		}
		return new LaneContext(LaneType.LANE_4, ApprovalStatus.PENDING);
	}

	@Override
	public UserLaneRelationshipPagination findAllLaneRelationshipsByUserId(Long userId, UserLaneRelationshipPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		Assert.isTrue(!authorizationService.getRequestContext(userId).equals(RequestContext.PUBLIC), "User is not authorized.");

		return laneAssociationDAO.findAllUserLaneRelationships(userId, pagination);
	}

	@Override
	@UpdateUserSearchIndex(userIdArgumentPosition = 1)
	public void addUserToLane(Long workerUserId, Long companyId, LaneType laneType) {
		Assert.notNull(workerUserId, "UserId can't be null");
		Assert.notNull(companyId, "CompanyId can't be null");
		Assert.notNull(laneType, "LaneType can't be null");

		User contractor = userService.getUser(workerUserId);
		Assert.notNull(contractor, "Unable to find user");

		/*
		 * TODO: Refactor to throw the Exception and catch it when required. For
		 * now we just need to validate and return to the regular flow and avoid
		 * the check in every method.
		 */

		// User can't be a contractor of his own company
		if (!laneType.isLane1() && (contractor.getCompany().getId().equals(companyId))) {
			return;
		}

		// User can't be added twice
		if (isUserPartOfLane123(workerUserId, companyId)) {
			return;
		}

		Company company = companyService.findCompanyById(companyId);
		LaneAssociation association = laneAssociationDAO.addUserToLane(contractor, company, laneType);

		userChangeLogService.createChangeLog(new UserLaneAddedChangeLog(workerUserId, authenticationService.getCurrentUserId(),
			authenticationService.getMasqueradeUserId(), company, laneType));

		userNotificationService.onLaneAssociationCreated(association);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.LANE_ASSOCIATION, laneType);
		userGroupValidationService.revalidateAllAssociationsByUserAsync(workerUserId, params);
	}

	@Override
	public CompanyResourcePagination findAllEmployeesByCompany(Long companyId, CompanyResourcePagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return userDAO.findAllEmployeesByCompanyId(companyId, pagination);

	}

	@Override
	public CompanyResourcePagination findAllContractorsByCompany(Long companyId, CompanyResourcePagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return userDAO.findAllContractorsByCompanyId(companyId, pagination);
	}

	@Override
	public List<LaneAssociation> findAllAssociationsWithApprovalStatus(ApprovalStatus status) {
		return laneAssociationDAO.findAllAssociationsWithApprovalStatus(status);
	}

	@Override
	public void addUserToWorkerPool(long companyId, String userNumber, String resourceUserNumber) {
		logger.info("Adding " + resourceUserNumber + " to company " + companyId);
		authenticationService.setCurrentUser(userService.findUserId(userNumber));
		User worker = userService.findUserByUserNumber(resourceUserNumber);
		if (worker == null) {
			return;
		}

		try {
			LaneContext context = getLaneContextForUserAndCompany(worker.getId(), companyId);
			if (context == null || context.getLaneType() == null) {
				return;
			}
			if (context.getLaneType().isLane4()) {
				addUserToCompanyLane3(worker.getId(), companyId);
			} else if (context.getLaneType().isLane2() && context.getApprovalStatus().isPending()) {
				updateLaneAssociationApprovalStatus(worker.getId(), companyId, ApprovalStatus.APPROVED);
			}
		} catch (Exception e) {
			logger.warn("Couldn't add " + worker.getId() + " to company " + companyId + " lane3.", e);
		}
	}

	@Override
	public void addUsersToWorkerPool(String userNumber, Set<String> resourceUserNumbers) {
		logger.debug("pushing " + userNumber + "'s cart to worker pool");
		try {
			User solrUser = userDAO.findUserByUserNumber(userNumber, false);
			if (solrUser == null) {
				throw new UserNotFoundException("User was not found.", userNumber);
			}
			eventRouter.sendEvent(
				eventFactory.buildAddToWorkerPoolEvent(solrUser.getCompany().getId(), userNumber, resourceUserNumbers));
		} catch (Exception e) {
			logger.error("fail adding user to worker pool:", e);
		}
	}

	@Override
	public boolean isLane3Active(Long userId) {
		return authenticationService.isLane3Active(userService.getUserWithRoles(userId));
	}
}
