package com.workmarket.service.business;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembership;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipList;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsResponse;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.BlockedAssociationDAO;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkVendorInvitation;
import com.workmarket.domains.model.WorkVendorInvitationToGroupAssociation;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.block.BlockedCompanyCompanyAssociation;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.dao.WorkVendorInvitationDAO;
import com.workmarket.domains.work.dao.WorkVendorInvitationToGroupAssociationDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.route.WorkRoutingValidator;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.dto.TalentPoolMembershipDTO;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkBundleVendorRoutingEvent;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.forms.work.WorkFormRouting;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import rx.functions.Action1;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Service
public class VendorServiceImpl implements VendorService {
	private static final Logger logger = LoggerFactory.getLogger(VendorServiceImpl.class);

	private static final int MAX_VENDORS_PER_ASSIGNMENT = 200;
	private static final boolean ASSIGN_TO_FIRST_TO_ACCEPT = true;
	private static final boolean NEED_TO_APPLY = false;

	@Autowired private AnalyticsService analyticsService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private BlockedAssociationDAO blockedAssociationDAO;
	@Autowired private CompanyService companyService;
	@Autowired private EventRouter eventRouter;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private UserService userService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private WorkResourceDetailCache workResourceDetailCache;
	@Autowired private WorkRoutingValidator workRoutingValidator;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private WorkService workService;
	@Autowired private WorkVendorInvitationDAO workVendorInvitationDAO;
	@Autowired private UserRoleService userRoleService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private WorkVendorInvitationToGroupAssociationDAO workVendorInvitationToGroupAssociationDAO;
	@Autowired private TalentPoolClient talentPoolClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private PricingService pricingService;

	@Override
	public void inviteVendorsToWork(WorkFormRouting workFormRouting, long workId) {
		if (!workFormRouting.hasVendorCompanyNumbers()) {
			return;
		}

		try {
			inviteVendorsToWork(workFormRouting.getAssignToFirstToAcceptVendorCompanyNumbers(), workId, ASSIGN_TO_FIRST_TO_ACCEPT, Collections.<Long>emptySet());
			inviteVendorsToWork(workFormRouting.getNeedToApplyVendorCompanyNumbers(), workId, NEED_TO_APPLY, Collections.<Long>emptySet());
		} catch (WorkNotFoundException e) {
			logger.error("could not find workId: {}", workId, e);
		}
	}

	@Override
	public WorkRoutingResponseSummary inviteVendorsToWork(
		final Set<String> vendorNumbersToInvite,
		final Long workId,
		final boolean assignToFirstToAccept,
		final Collection<Long> groupIdsToRoute)
			throws WorkNotFoundException
	{
		Assert.notNull(vendorNumbersToInvite);
		Assert.notNull(workId);

		WorkRoutingResponseSummary workRoutingResponseSummary = new WorkRoutingResponseSummary();
		Work work = workService.findWork(workId);

		final Set<WorkAuthorizationResponse> workAuthorizationResponses = workRoutingValidator.validateWorkForRouting(work);
		if (!workAuthorizationResponses.contains(WorkAuthorizationResponse.SUCCEEDED)) {
			WorkAuthorizationResponse authorizationResponse = CollectionUtilities.first(workAuthorizationResponses);
			workRoutingResponseSummary.addToWorkAuthorizationResponse(authorizationResponse, Collections.<String>emptySet());
			return workRoutingResponseSummary;
		}

		if (CollectionUtilities.isEmpty(vendorNumbersToInvite)) {
			workRoutingResponseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.SUCCEEDED, Collections.<String>emptySet());
			return workRoutingResponseSummary;
		}

		List<WorkVendorInvitation> existingInvitations = workVendorInvitationDAO.getVendorInvitationsByWork(workId);
		List<CompanyIdentityDTO> companyIdentityDTOList = companyService.findCompanyIdentitiesByCompanyNumbers(vendorNumbersToInvite);

		List<CompanyIdentityDTO> vendorsToInvite =
			getVendorsToInvite(existingInvitations, companyIdentityDTOList, workRoutingResponseSummary);

		Set<WorkVendorInvitation> workVendorInvitations =
			inviteVendors(workId, vendorsToInvite, existingInvitations, assignToFirstToAccept, workRoutingResponseSummary);
		if (CollectionUtils.isNotEmpty(groupIdsToRoute)) {
			addVendorInvitedByGroupAssociations(workId, groupIdsToRoute, workVendorInvitations, existingInvitations, companyIdentityDTOList);
		}

		return workRoutingResponseSummary;
	}

	@Override
	public List<Long> copyVendorsFromWorkToWork(Long fromWorkId, Long toWorkId) {
		List <Long> invitedVendorIds = Lists.newArrayList();
		List<Long> companyIdsFrom = getNotDeclinedVendorIdsByWork(fromWorkId);
		List<Long> assignToFirstToAcceptCompanyIds= getAssignToFirstToAcceptVendorIdsByWork(fromWorkId);
		Set<Long> companyIdsTo = Sets.newHashSet(getNotDeclinedVendorIdsByWork(toWorkId));
		Set<WorkVendorInvitation> workVendorInvitations = Sets.newHashSetWithExpectedSize(companyIdsFrom.size());
		for (Long companyId : companyIdsFrom) {
			if (!companyIdsTo.contains(companyId)) {
				workVendorInvitations.add(new WorkVendorInvitation(toWorkId, companyId, assignToFirstToAcceptCompanyIds.contains(companyId)));
				invitedVendorIds.add(companyId);
			}
		}

		if (workVendorInvitations.size() > 0) {
			workVendorInvitationDAO.saveAll(workVendorInvitations);
		}
		return invitedVendorIds;
	}

	@Override
	public boolean hasInvitedAtLeastOneVendor(Long workId) {
		Assert.notNull(workId);

		return workVendorInvitationDAO.hasInvitedAtLeastOneVendor(workId);
	}

	@Override
	public List<Long> getDeclinedVendorIdsByWork(Long workId) {
		Assert.notNull(workId);

		return workVendorInvitationDAO.getDeclinedVendorIdsByWork(workId);
	}

	@Override
	public List<String> getDeclinedVendorNumbersByWork(final Long workId) {
		Assert.notNull(workId);

		return workVendorInvitationDAO.getDeclinedVendorNumbersByWork(workId);
	}

	@Override
	public List<Long> getVendorIdsByWork(Long workId) {
		Assert.notNull(workId);

		return workVendorInvitationDAO.getVendorIdsByWork(workId);
	}

	@Override
	public List<Long> getAssignToFirstToAcceptVendorIdsByWork(Long workId) {
		Assert.notNull(workId);

		return workVendorInvitationDAO.getAssignToFirstToAcceptVendorIdsByWork(workId);
	}

	@Override
	public List<Long> getNotDeclinedVendorIdsByWork(Long workId) {
		Assert.notNull(workId);

		return workVendorInvitationDAO.getNotDeclinedVendorIdsByWork(workId);
	}

	@Override
	public List<String> getNotDeclinedVendorNumbersByWork(final Long workId) {
		Assert.notNull(workId);

		return workVendorInvitationDAO.getNotDeclinedVendorNumbersByWork(workId);
	}

	@Override
	public List<String> getVendorNumbersByWork(Long workId) {
		Assert.notNull(workId);

		return workVendorInvitationDAO.getVendorNumbersByWork(workId);
	}

	@Override
	public int getMaxVendorsPerAssignment() {
		return MAX_VENDORS_PER_ASSIGNMENT;
	}

	@Override
	public boolean isVendorInvitedToWork(Long companyId, Long workId) {
		Assert.notNull(companyId);
		Assert.notNull(workId);

		return CollectionUtilities.contains(getVendorIdsByWork(workId), companyId) || workResourceDAO.isAtLeastOneWorkerFromCompanyInvitedToWork(companyId, workId);
	}

	@Override
	public void sendVendorsInvitedNotifications(Long workId, Set<Long> vendorIds) {
		if (CollectionUtils.isEmpty(vendorIds)) return;
		List<Long> inviteeIds = Lists.newArrayList();
		for (Long vendorId : vendorIds) {
			List <Long> dispatcherIds = getDispatcherIdsForVendor(vendorId);
			if (!dispatcherIds.isEmpty()) {
				inviteeIds.addAll(dispatcherIds);
			}
		}
		if (!inviteeIds.isEmpty()) {
			userNotificationService.onWorkInvitation(workId, inviteeIds, false);
		}
	}

	@Override
	public void declineWork(Long workId, Long vendorId, Long userId) {
		List<Long> workerIds = workResourceDAO.getAllWorkersFromCompanyInvitedToWork(vendorId, workId);
		List<Long> workIds = Lists.newArrayList(workId);
		if (workBundleService.isAssignmentBundle(workId)) {
			workIds.addAll(workBundleService.getAllWorkIdsInBundle(workId));
		}

		for (Long declinedWorkId : workIds) {
			WorkVendorInvitation wva = workVendorInvitationDAO.findBy("workId", declinedWorkId, "companyId", vendorId);
			if (wva == null) {
				wva = new WorkVendorInvitation(declinedWorkId, vendorId);
				workVendorInvitationDAO.saveOrUpdate(wva);
			}
			if (wva != null) {
				wva.setIsDeclined(true);
				wva.setDeclinedOn(Calendar.getInstance());
				wva.setDeclinedById(userId);
			}
			for (Long workerId: workerIds) {
				workService.declineWork(workerId, declinedWorkId);
			}
		}
	}

	@Override
	public void blockVendor(Long userId, Long blockedCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(blockedCompanyId);
		User user = userService.findUserById(userId);

		blockCompanyFromCompany(user, blockedCompanyId);
	}

	@Override
	public void unblockVendor(Long userId, Long blockedCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(blockedCompanyId);
		User user = userService.findUserById(userId);
		List<User> blockedCompanyUsers = userService.findAllActiveEmployees(blockedCompanyId);

		if (userRoleService.hasAnyAclRole(user, AclRole.ACL_ADMIN, AclRole.ACL_MANAGER)) {
			unblockCompanyFromCompany(user.getCompany().getId(), blockedCompanyId);
			unblockAllBlockedCompanyUsers(user, blockedCompanyUsers);
		}
	}

	@Override
	public boolean isVendorBlockedByCompany(Long blockingCompanyId, Long blockedCompanyId) {
		Assert.notNull(blockingCompanyId);
		Assert.notNull(blockedCompanyId);

		return blockedAssociationDAO.isVendorBlockedByCompany(blockingCompanyId, blockedCompanyId);
	}

	@Override
	public void removeAutoAssign(long vendorId, long workId) {
		WorkVendorInvitation workVendorInvitation = workVendorInvitationDAO.findBy("workId", workId, "companyId", vendorId);
		workVendorInvitation.setAssignToFirstToAccept(false);
		if (workBundleService.isAssignmentBundle(workId)) {
			for (Long bundledWorkId : workBundleService.getAllWorkIdsInBundle(workId)) {
				workVendorInvitation = workVendorInvitationDAO.findBy("workId", bundledWorkId, "companyId", vendorId);
				workVendorInvitation.setAssignToFirstToAccept(false);
			}
		}
		workResourceDetailCache.evict(workId);
	}

	private void blockCompanyFromCompany(User user, Long blockedCompanyId) {
		Assert.notNull(user);
		Assert.notNull(blockedCompanyId);

		Long blockingCompanyId = user.getCompany().getId();
		Assert.isTrue(!blockingCompanyId.equals(blockedCompanyId), "blockclient.company.owned");

		Company blockedCompany = companyService.findById(blockedCompanyId);
		Company blockingCompany = companyService.findById(blockingCompanyId);
		List<User> blockedCompanyUsers = userService.findAllActiveEmployees(blockedCompanyId);

		BlockedCompanyCompanyAssociation block = blockedAssociationDAO.findByCompanyIdAndBlockedCompanyId(blockingCompanyId, blockedCompanyId);
		if (block == null) {
			block = new BlockedCompanyCompanyAssociation(user, blockedCompany);
		}
		block.setDeleted(false);
		block.setUser(user);
		block.setBlockingCompany(blockingCompany);
		blockedAssociationDAO.saveOrUpdate(block);

		eventRouter.sendEvent(new VendorSearchIndexEvent(blockedCompanyId));

		blockAllBlockedCompanyUsers(user, blockedCompanyUsers);
	}

	private void unblockCompanyFromCompany(Long companyId, Long blockedCompanyId) {
		Assert.notNull(companyId);
		Assert.notNull(blockedCompanyId);

		BlockedCompanyCompanyAssociation block = blockedAssociationDAO.findByCompanyIdAndBlockedCompanyId(companyId, blockedCompanyId);
		if (block != null) {
			block.setDeleted(true);
			eventRouter.sendEvent(new VendorSearchIndexEvent(blockedCompanyId));
		}
	}

	private void blockAllBlockedCompanyUsers(User currUser, List<User> blockedCompanyUsers) {
		Assert.notNull(blockedCompanyUsers);

		for (User user : blockedCompanyUsers) {
			userService.blockUser(currUser.getId(), user.getId());
		}
	}

	private void unblockAllBlockedCompanyUsers(User currUser, List<User> blockedCompanyUsers) {
		Assert.notNull(blockedCompanyUsers);

		for (User user : blockedCompanyUsers) {
			userService.unblockUser(currUser.getId(), user.getId());
		}
	}

	private List<Long> getDispatcherIdsForVendor(Long vendorId) {
		List <User> users = authenticationService.findAllUsersByACLRoleAndCompany(vendorId, AclRole.ACL_DISPATCHER);
		if (users.size() > 0) {
			return extract(users, on(User.class).getId());
		}
		return Lists.newArrayList();
	}

	/**
	 * Remove already invited vendors from invite request list, and returns a list of vendors to be invited.
	 *
	 * @param existingInvitations
	 * @param vendorIdentityList
	 * @param workRoutingResponseSummary
	 * @return
	 */
	private List<CompanyIdentityDTO> getVendorsToInvite(
		final List<WorkVendorInvitation> existingInvitations,
		final List<CompanyIdentityDTO> vendorIdentityList,
		final WorkRoutingResponseSummary workRoutingResponseSummary
	) {
		List<CompanyIdentityDTO> vendorsToInvite = Lists.newArrayList();
		Set<Long> alreadyInvitedVendorIds = Sets.newHashSet(extract(existingInvitations, on(WorkVendorInvitation.class).getCompanyId()));
		Set<String> alreadyInvitedVendorNumbers = Sets.newHashSet();
		for (CompanyIdentityDTO companyIdentityDTO : vendorIdentityList) {
			if (alreadyInvitedVendorIds.contains(companyIdentityDTO.getCompanyId())) {
				alreadyInvitedVendorNumbers.add(companyIdentityDTO.getCompanyNumber());
			} else {
				vendorsToInvite.add(companyIdentityDTO);
			}
		}

		if (CollectionUtils.isNotEmpty(alreadyInvitedVendorNumbers)) {
			workRoutingResponseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.ALREADY_INVITED_TO_WORK, alreadyInvitedVendorNumbers);
		}
		return vendorsToInvite;
	}

	/**
	 * Invites a list of vendors to the assignment:
	 * 1. caps invites under quota
	 * 2. build invitation object, and update DB
	 * 3. update routing summary
	 * 4. notify invited vendors
	 *
	 * @param workId
	 * @param vendorsToInvite
	 * @param existingInvitations
	 * @param assignToFirstToAccept
	 * @param workRoutingResponseSummary
	 * @return
	 */
	private Set<WorkVendorInvitation> inviteVendors(
		final Long workId,
		final List<CompanyIdentityDTO> vendorsToInvite,
		final List<WorkVendorInvitation> existingInvitations,
		final boolean assignToFirstToAccept,
		final WorkRoutingResponseSummary workRoutingResponseSummary
	) {
		// build a list of invitations and save them
		Set<String> successfullyInvited = Sets.newHashSet();
		Set<Long> successfullyInvitedIds = Sets.newHashSet();

		Set<WorkVendorInvitation> workVendorInvitations = Sets.newHashSet();
		int remainingNumberOfVendorsToInvite = Math.min(MAX_VENDORS_PER_ASSIGNMENT - existingInvitations.size(), vendorsToInvite.size());

		if (remainingNumberOfVendorsToInvite < vendorsToInvite.size()) {
			Set<String> maxVendorsExceeded = Sets.newHashSet();
			for (int i = remainingNumberOfVendorsToInvite; i < vendorsToInvite.size(); i++) {
				maxVendorsExceeded.add(vendorsToInvite.get(i).getCompanyNumber());
			}
			workRoutingResponseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.MAX_VENDORS_EXCEEDED, maxVendorsExceeded);
		}

		for (int ix = 0; ix < remainingNumberOfVendorsToInvite; ix++) {
			CompanyIdentityDTO pair = vendorsToInvite.get(ix);
			workVendorInvitations.add(new WorkVendorInvitation(workId, pair.getCompanyId(), assignToFirstToAccept));
			successfullyInvited.add(pair.getCompanyNumber());
			successfullyInvitedIds.add(pair.getCompanyId());
		}
		workVendorInvitationDAO.saveAll(workVendorInvitations);

		// notify
		if (CollectionUtils.isNotEmpty(successfullyInvited)) {
			if (workBundleService.isAssignmentBundle(workId)) {
				Long companyId = workService.findBuyerCompanyId(workId);
				AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
				WorkBundleVendorRoutingEvent event = new WorkBundleVendorRoutingEvent(workId);
				event.setMessageGroupId(String.format(Constants.ACCOUNT_REGISTER_MESSAGE_GROUP_ID, accountRegister.getId()));
				eventRouter.sendEvent(event);
			} else {
				workRoutingService.openWork(workId);
				sendVendorsInvitedNotifications(workId, successfullyInvitedIds);
			}
			workRoutingResponseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.SUCCEEDED, successfullyInvited);
		}

		return workVendorInvitations;
	}

	/**
	 * For invites that are from group routing, add invite to group associations.
	 *
	 * Business logic:
	 * 1. if it is a new invite, we inserts (new_invite_id, group_id) to association table
	 * 2. if a vendor is already invited by previous routing, we insert (existing_invite_id, group_id) to association table
	 *
	 * @param workId
	 * @param groupIdsToRoute
	 * @param workVendorInvitations
	 * @param existingInvitations
	 * @param vendorIdentityList
	 * @return
	 */
	@VisibleForTesting
	Set<WorkVendorInvitationToGroupAssociation> addVendorInvitedByGroupAssociations(
		final Long workId,
		final Collection<Long> groupIdsToRoute,
		final Set<WorkVendorInvitation> workVendorInvitations,
		final List<WorkVendorInvitation> existingInvitations,
		final List<CompanyIdentityDTO> vendorIdentityList
	) {
		Collection<UserGroup> userGroups = Sets.newHashSet();
		for (final Long groupId : groupIdsToRoute) {
			userGroups.add(userGroupService.findGroupById(groupId));
		}

		// create new workVendorInvitation to group associations
		Map<Long, Set<Long>> vendorGroupIdMap = getVendorUserGroupMemberships(vendorIdentityList, userGroups);
		Set<WorkVendorInvitationToGroupAssociation> associations = Sets.newHashSet();
		for (WorkVendorInvitation workVendorInvitation : workVendorInvitations) {
			if (vendorGroupIdMap.containsKey(workVendorInvitation.getCompanyId())) {
				Set<Long> groupIds = vendorGroupIdMap.get(workVendorInvitation.getCompanyId());
				for (UserGroup userGroup : userGroups) {
					if (groupIds.contains(userGroup.getId())) {
						associations.add(new WorkVendorInvitationToGroupAssociation(workVendorInvitation, userGroup));
					}
				}
			}
		}

		// for existing workVendorInvitations, add existing workVendorInvitation to new group associations
		Collection<WorkVendorInvitationToGroupAssociation> additionalAssociations =
			createGroupAssociationsForExistingInvitations(workId, existingInvitations, vendorGroupIdMap);
		associations.addAll(additionalAssociations);
		workVendorInvitationToGroupAssociationDAO.saveAll(associations);
		return associations;
	}

	/**
	 * For a list vendors and a list of talent pools, query talent-pool-service to build vendor to talent-pool associations.
	 *
	 * @param companyIdentityDTOList a list of vendors
	 * @param userGroups          a list of talent pools
	 * @return Map of group id to a set of vendors that belong to the group
	 */
	@VisibleForTesting
	Map<Long, Set<Long>> getVendorUserGroupMemberships(
		final List<CompanyIdentityDTO> companyIdentityDTOList,
		final Collection<UserGroup> userGroups
	) {
		if (companyIdentityDTOList.size() == 0 || userGroups.size() == 0) {
			return Collections.emptyMap();
		}

		final Map<String, Long> userGroupUuidIdPairs = Maps.newHashMap();
		for (UserGroup userGroup : userGroups) {
			userGroupUuidIdPairs.put(userGroup.getUuid(), userGroup.getId());
		}
		final Map<String, Long> vendorUuidIdPairs = Maps.newHashMap();
		for (CompanyIdentityDTO identity : companyIdentityDTOList) {
			vendorUuidIdPairs.put(identity.getUuid(), identity.getCompanyId());
		}

		final TalentPoolMembershipsRequest request =
			TalentPoolMembershipsRequest.newBuilder()
				.addAllParticipantUuid(vendorUuidIdPairs.keySet())
				.build();

		final ImmutableList.Builder<TalentPoolMembershipList> builder = ImmutableList.builder();

		talentPoolClient.getMemberships(request, webRequestContextProvider.getRequestContext()).subscribe(
			new Action1<TalentPoolMembershipsResponse>() {
				@Override
				public void call(final TalentPoolMembershipsResponse response) {
					builder.addAll(response.getTalentPoolMembershipListList());
				}
			},
			new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					logger.warn("Failed to get talent pool vendor memberships: " + throwable.getMessage());
				}
			});

		final List<TalentPoolMembershipList> membershipLists = builder.build();

		final Map<Long, Set<Long>> vendorGroupIdMap = Maps.newHashMap();
		for (TalentPoolMembershipList membershipList : membershipLists) {
			final String vendorUuid = membershipList.getParticipantUuid();
			final Long vendorId = vendorUuidIdPairs.get(vendorUuid);
			if (vendorId == null) {
				continue;
			}
			final Set<Long> groupIds = Sets.newHashSet();
			for (TalentPoolMembership membership : membershipList.getTalentPoolMembershipList()) {
				if (userGroupUuidIdPairs.containsKey(membership.getTalentPoolUuid()) &&
					(StringUtils.isNotEmpty(membership.getTalentPoolParticipation().getApprovedOn()))) { // do we have other participation condition?
					groupIds.add(userGroupUuidIdPairs.get(membership.getTalentPoolUuid()));
				}
			}
			if (groupIds.size() > 0) {
				vendorGroupIdMap.put(vendorId, groupIds);
			}
		}

		return vendorGroupIdMap;
	}

	/**
	 * Given existing invitations and vendor to group associations, creates vendor to group associations.
	 *
	 * @param workId
	 * @param existingInvitations
	 * @param vendorGroupMap
	 * @return
	 */
	@VisibleForTesting
	Collection<WorkVendorInvitationToGroupAssociation> createGroupAssociationsForExistingInvitations(
		final Long workId,
		final List<WorkVendorInvitation> existingInvitations,
		final Map<Long, Set<Long>> vendorGroupMap
	) {
		Map<Long, Set<Long>> vendorInvitedByGroupAssociations =
			workVendorInvitationDAO.getVendorInvitationGroupAssociationsByWorkId(workId);
		Map<Long, Set<Long>> invitedVendorsWithNewGroups = Maps.newHashMap();
		for (Long vendorId : vendorGroupMap.keySet()) {
			if (vendorInvitedByGroupAssociations.containsKey(vendorId)) {
				// remove existing associations
				Set<Long> diff = Sets.difference(vendorGroupMap.get(vendorId), vendorInvitedByGroupAssociations.get(vendorId));
				if (diff.size() > 0) {
					invitedVendorsWithNewGroups.put(vendorId, diff);
				}
			} else {
				invitedVendorsWithNewGroups.put(vendorId, vendorGroupMap.get(vendorId));
			}
		}
		if (invitedVendorsWithNewGroups.size() == 0) {
			return Collections.emptyList();
		}
		Collection<WorkVendorInvitationToGroupAssociation> newAssociations = Lists.newArrayList();
		for (Long vendorId : invitedVendorsWithNewGroups.keySet()) {
			for (WorkVendorInvitation invitation : existingInvitations) {
				if (vendorId.equals(invitation.getCompanyId())) {
					for (Long groupId : invitedVendorsWithNewGroups.get(vendorId)) {
						newAssociations.add(new WorkVendorInvitationToGroupAssociation(invitation.getId(), groupId));
					}
					break;
				}
			}

		}

		return newAssociations;
	}

	@Override
	public final TalentPoolMembershipDTO getAllVendorUserGroupMemberships(final Long userId) {
		Assert.notNull(userId);
		User user = userService.findUserById(userId);
		String companyNumber = user.getCompany().getCompanyNumber();
		String timeZoneId = user.getProfile().getTimeZone().getTimeZoneId();
		TalentPoolMembershipDTO dto = new TalentPoolMembershipDTO();

		List<CompanyIdentityDTO> companyIdentityDTOList =
			companyService.findCompanyIdentitiesByCompanyNumbers(Lists.newArrayList(companyNumber));

		if (companyIdentityDTOList.isEmpty()) {
			return dto;
		}

		final Map<String, Long> vendorUuidIdPairs = Maps.newHashMap();
		vendorUuidIdPairs.put(companyIdentityDTOList.get(0).getUuid(), companyIdentityDTOList.get(0).getCompanyId());

		final TalentPoolMembershipsRequest request =
			TalentPoolMembershipsRequest.newBuilder()
				.addAllParticipantUuid(vendorUuidIdPairs.keySet())
				.build();

		final ImmutableList.Builder<TalentPoolMembershipList> builder = ImmutableList.builder();

		talentPoolClient.getMemberships(request, webRequestContextProvider.getRequestContext()).subscribe(
			new Action1<TalentPoolMembershipsResponse>() {
				@Override
				public void call(final TalentPoolMembershipsResponse response) {
					builder.addAll(response.getTalentPoolMembershipListList());
				}
			},
			new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					logger.warn("Failed to get talent pool vendor memberships: " + throwable.getMessage());
				}
			});

		final List<TalentPoolMembershipList> membershipLists = builder.build();

		if (membershipLists.isEmpty()) {
			return dto;
		}

		TalentPoolMembershipList membershipList = membershipLists.get(0);

		List<String> talentPoolUuids = Lists.newArrayList();
		for (final TalentPoolMembership membership : membershipList.getTalentPoolMembershipList()) {
			talentPoolUuids.add(membership.getTalentPoolUuid());
		}
		Map<String, Long> groupIds = userGroupService.findUserGroupUuidIdPairsByUuids(talentPoolUuids);

		for (TalentPoolMembership membership : membershipList.getTalentPoolMembershipList()) {
			String approvedOn = membership.getTalentPoolParticipation().getApprovedOn();
			String invitedOn = membership.getTalentPoolParticipation().getInvitedOn();
			String appliedOn = membership.getTalentPoolParticipation().getAppliedOn();

			if (StringUtils.isNotEmpty(approvedOn)) {
				dto.getMemberships().put(
					groupIds.get(membership.getTalentPoolUuid()),
					getCalendarFromString(timeZoneId, groupIds, membership, approvedOn)
				);
			} else if (StringUtils.isNotEmpty(appliedOn)) {
				dto.getApplications().put(
					groupIds.get(membership.getTalentPoolUuid()),
					getCalendarFromString(timeZoneId, groupIds, membership, appliedOn)
				);

			} else if (StringUtils.isNotEmpty(invitedOn)) {
				dto.getInvitations().put(
					groupIds.get(membership.getTalentPoolUuid()),
					getCalendarFromString(timeZoneId, groupIds, membership, invitedOn)
				);
			}
		}
		return dto;
	}

	private Calendar getCalendarFromString(String timeZoneId, Map<String, Long> groupIds, TalentPoolMembership membership, String dateStr) {
		Calendar calendar;
		try {
			calendar = DateUtilities.getCalendarFromString(dateStr, "yyyy-MM-dd hh:mm:ss.S", timeZoneId);
		} catch (java.text.ParseException e) {
			logger.error("failed to parse date for talent pool id:{}", groupIds.get(membership.getTalentPoolUuid()), e);
			calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		}
		return calendar;
	}
}
