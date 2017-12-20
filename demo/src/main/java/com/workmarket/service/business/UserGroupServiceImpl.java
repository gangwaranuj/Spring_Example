package com.workmarket.service.business;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.employer.settings.models.SkillDTO;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipant;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.request.RequestDAO;
import com.workmarket.dao.skill.UserGroupSkillAssociationDAO;
import com.workmarket.dao.summary.group.UserGroupSummaryDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.groups.dao.GroupMembershipDAO;
import com.workmarket.domains.groups.dao.ManagedCompanyUserGroupDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.dao.UserGroupOrgUnitAssociationDAO;
import com.workmarket.domains.groups.dao.UserUserGroupAssociationDAO;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import com.workmarket.domains.groups.model.GroupMembershipPagination;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserGroupLastRoutedDTO;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.groups.model.UserGroupThroughputDTO;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.model.UserUserGroupAssociationPagination;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.changelog.user.UserAppliedToGroupChangeLog;
import com.workmarket.domains.model.changelog.user.UserGroupMembershipApprovedChangeLog;
import com.workmarket.domains.model.changelog.user.UserGroupMembershipDeclinedChangeLog;
import com.workmarket.domains.model.changelog.user.UserLeftGroupChangeLog;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.request.Request;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.summary.group.UserGroupSummary;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.service.business.asset.AssetBundlerQueue;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.group.AddUsersToGroupEvent;
import com.workmarket.service.business.event.group.GroupUpdateSearchIndexEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.requirementsets.EligibilityService;
import com.workmarket.service.business.status.DownloadProfilePhotosStatus;
import com.workmarket.service.business.wrapper.DownloadProfilePhotosResponse;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.index.UpdateUserGroupSearchIndex;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.service.talentpool.TalentPoolParticipationAdapter;
import com.workmarket.service.talentpool.TalentPoolService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.utility.CollectionUtilities.isEmpty;
import static com.workmarket.utility.CollectionUtilities.newListPropertyProjection;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class UserGroupServiceImpl implements UserGroupService {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private LaneService laneService;
	@Autowired private OrgStructureService orgStructureService;
	@Autowired private ProfileService profileService;
	@Autowired private RequestService requestService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private GroupSearchService groupSearchService;
	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private SummaryService summaryService;
	@Autowired private ServiceMessageHelper messageHelper;
	@Autowired private AssetBundlerQueue assetbundler;

	@Autowired private CompanyDAO companyDAO;
	@Autowired private IndustryDAO industryDAO;
	@Autowired private RequestDAO requestDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserChangeLogDAO userChangeLogDAO;
	@Autowired private UserService userService;
	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private UserUserGroupAssociationDAO userUserGroupAssociationDAO;
	@Autowired private ManagedCompanyUserGroupDAO managedCompanyUserGroupDAO;
	@Autowired private GroupMembershipDAO groupMembershipDAO;
	@Autowired private UserGroupSummaryDAO userGroupSummaryDAO;
	@Autowired private EligibilityService eligibilityService;
	@Autowired private UserRoleService userRoleService;
	@Autowired private ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired private UserGroupSkillAssociationDAO userGroupSkillAssociationDAO;
	@Autowired private UserGroupOrgUnitAssociationDAO userGroupOrgUnitAssociationDAO;
	@Autowired private TalentPoolService talentPoolService;
	@Autowired private TalentPoolParticipationAdapter talentPoolParticipationAdapter;
	@Autowired private FeatureEntitlementService featureEntitlementService;

	private static final Log logger = LogFactory.getLog(UserGroupServiceImpl.class);

	@Override
	@UpdateUserGroupSearchIndex(updateUsers = false, userGroupIdArgument = 1)
	public void updateGroupActiveFlag(long groupId, boolean active) {
		UserGroup group = findGroupById(groupId);
		group.setActiveFlag(active);
	}

	/*
	 DownloadProfilePhotosResponse gets all images for group downloads.
	 Members in the group are downloaded
	 Pending users are downloaded
	 Invited Users are downloaded if the Recipient is a group owner of any of the users groups
	  */
	@Override
	public DownloadProfilePhotosResponse downloadGroupProfileImages(long groupId, long userId) {
		List<UserPagination> allPaginations = Lists.newArrayList(new UserPagination(true), new UserPagination(true), new UserPagination(true));
		ListMultimap<User, String> userAssetMap = ArrayListMultimap.create();
		try {
			findAllUsersOfGroup(groupId, allPaginations.get(0));
			findAllPendingUsers(groupId, allPaginations.get(1));
			findAllInvitedUsers(groupId, allPaginations.get(2));
			int i = 0;
			for (UserPagination userPages : allPaginations) {
				List<User> results = userPages.getResults();
				for (User user : results) {

					boolean isGroupOwner = false;

					if (i == 2) {
						Set<User> allUsersInCompanyOfUsersGroups = companyService.findCompanyUsersOfCompanyUserGroups(user.getId());
						isGroupOwner = allUsersInCompanyOfUsersGroups.contains(userService.getUser(userId));
					}

					List<UserAssetAssociation> profileImages = profileService.findAllUserProfileAndAvatarImageAssociations(user.getId());
					for (UserAssetAssociation assetAssociation : profileImages) {
						if (assetAssociation == null) {
							continue;
						}

						boolean memberOrPending;
						boolean invited;
						boolean invitedButGroupOwner;

						try {
							String assetCode = assetAssociation.getAsset().getAvailability().getCode();
							memberOrPending = !assetCode.equals("private") && (i != 2);
							invited = (i == 2) && (assetCode.equals("all") || assetCode.equals("guest"));
							invitedButGroupOwner = isGroupOwner && (i == 2) && !assetCode.equals("private");
						} catch (Exception e) {
							memberOrPending = false;
							invited = false;
							invitedButGroupOwner = false;
							logger.error(String.format("Asset code error for group id %d and user id %d", groupId, user.getId()));
						}

						if (memberOrPending || invited || invitedButGroupOwner) {
							try {
								userAssetMap.put(user, assetAssociation.getAsset().getUUID());
							} catch (Exception e) {
								logger.error(String.format("UserGroupServiceImpl downloadGroupProfileImages error for group (%d), message is (%s)", groupId, e.getMessage()));
							}
						}
					}
				}
				i++;
			}
		} catch (Exception e) {
			return new DownloadProfilePhotosResponse(
					DownloadProfilePhotosStatus.FAILURE,
					messageHelper.getMessage("profile.photo.change.download.exception.fail"));
		}

		if (userAssetMap.isEmpty()) {
			return new DownloadProfilePhotosResponse(
					DownloadProfilePhotosStatus.FAILURE,
					messageHelper.getMessage("profile.photo.change.download.empty.fail"));
		}

		assetbundler.bundleAssetsForUser(userAssetMap, userId);
		return new DownloadProfilePhotosResponse(
				DownloadProfilePhotosStatus.SUCCESS,
				messageHelper.getMessage("profile.photo.change.download.success"));
	}

	@Override
	public List<User> findAllUsersOfGroupById(Long groupId) {
		Assert.notNull(groupId);
		return userUserGroupAssociationDAO.findAllUsersOfGroup(groupId);
	}

	@Override
	public UserGroup findGroupById(Long groupId) {
		UserGroup userGroup = userGroupDAO.findUserGroupById(groupId);
		return userGroup;
	}

	@Override
	public UserGroup getByUuid(final String uuid) {
		return userGroupDAO.findBy("uuid", uuid);
	}

	@Override
	public UserGroup findGroupByIdNoAssociations(Long groupId) {
		Assert.notNull(groupId);
		return userGroupDAO.findGroupByIdNoAssociations(groupId);
	}

	@Override
	public UserGroup findCompanyUserGroupByName(Long companyId, String name) {
		UserGroup userGroup = userGroupDAO.findUserGroupByName(companyId, name);

		if (userGroup != null) {
			Hibernate.initialize(userGroup.getCompany());
		}

		return userGroup;
	}

	@Override
	public Eligibility reValidateRequirementSets(long groupId, long userId) {
		UserGroup userGroup = findGroupById(groupId);
		Eligibility eligibility = eligibilityService.getEligibilityFor(userId, userGroup);
		// eviction when revalidating with expired requirements is optional
		if (!eligibility.getCriteria().isEmpty()) {
			for (Criterion criterion: eligibility.getCriteria()) {
				if (!criterion.isMet() && criterion.isExpired() && !criterion.isRemoveWhenExpired()) {
					criterion.setMet(true);
				}
			}
			eligibility = eligibilityService.makeEligibility(eligibility.getCriteria());
		}
		return eligibility;
	}

	@Override
	public Eligibility validateRequirementSets(long groupId, long userId) {
		UserGroup userGroup = findGroupById(groupId);
		return eligibilityService.getEligibilityFor(userId, userGroup);
	}

	@Override
	public boolean isUserMemberOfGroup(Long groupId, Long userId) {
		Assert.notNull(groupId);
		Assert.notNull(userId);
		UserUserGroupAssociation association = findAssociationByGroupIdAndUserId(groupId, userId);
		if (association == null) {
			return false;
		}
		boolean isDeleted = association.getDeleted();
		return association.isApproved() && !isDeleted && !association.isDeclined();
	}

	@Override
	@UpdateUserGroupSearchIndex(updateUsers = false, userGroupIdArgument = 1)
	public UserUserGroupAssociation applyToGroup(Long userGroupId, Long userId) {
		return applyToGroup(userGroupId, userId, false, false);
	}

	public UserUserGroupAssociation applyToGroup(Long userGroupId, Long userId, boolean suppressNotification, boolean overrideAndApproveAssociation) {
		Assert.notNull(userGroupId);
		Assert.notNull(userId);

		logger.debug(String.format("applyToGroup groupId %d userId %d", userGroupId, userId));

		UserGroup userGroup = findGroupById(userGroupId);
		User user = userService.getUser(userId);

		Assert.notNull(userGroup);

		// Discussed internally and could not find a good reason to prevent opted-out users from applying to groups.. comment this for now
		//Assert.isTrue(authorizeUserForGroup(userGroup, user), "User " + userId + " can't apply for Group " + userGroupId + " because he's not Lane 3 Active.");

		Company company = userGroup.getCompany();
		boolean isEligible = true;
		if (userGroup.getOpenMembership()) {
			Eligibility eligibility = validateRequirementSets(userGroupId, userId);
			isEligible = eligibility.isEligible();
		}

		/*
		 * WORK-1551 : Users who respond to campaign that is associated with a public group
		 * should be 'pending lane 2' for campaign owner unless they passed all group criteria,
		 * then they are in group and in lane 2 (group status depends on group review setting)
		 *
		 * In the other hand, if the user passes the validation and the group doesn't require manual approval
		 * add the user to Lane3 (WORK-1010), only if he's approved for the group (WORK-1758)
		 *
		 */
		if (isEligible) {
			LaneAssociation laneAssociation = laneService.findActiveAssociationByUserIdAndCompanyId(user.getId(), company.getId());
			if (laneAssociation == null) {
				if (!userGroup.getRequiresApproval() && authenticationService.isLane3Active(user)) {
					laneService.addUserToCompanyLane3(userId, company.getId());
				}
			} else if (laneAssociation.getApprovalStatus().isPending() && laneAssociation.getLaneType().equals(LaneType.LANE_2)) {
				RecruitingCampaign campaign = user.getRecruitingCampaign();

				if (recruitingCampaignGroupEqualsThisGroup(campaign, userGroup)) {
					laneAssociation.setApprovalStatus(ApprovalStatus.APPROVED);
				}
			}
		}

		UserUserGroupAssociation userUserGroupAssociation = findAssociationByGroupIdAndUserId(userGroupId, userId);
		if (userUserGroupAssociation == null) {
			userUserGroupAssociation = makeUserGroupAssociation(userId, userGroup);
		}

		boolean isMember = userUserGroupAssociation.isApproved();

		//Setting the Verification and Approval status
		if (overrideAndApproveAssociation) {
			userUserGroupAssociation.setVerificationStatus(VerificationStatus.VERIFIED);
			userUserGroupAssociation.setApprovalStatus(ApprovalStatus.APPROVED);

		} else if (isEligible) {
			userUserGroupAssociation.setVerificationStatus(VerificationStatus.VERIFIED);

			if (userUserGroupAssociation.isPending() && !userGroup.getRequiresApproval()) {
				userUserGroupAssociation.setApprovalStatus(ApprovalStatus.APPROVED);
			}
		} else {
			userUserGroupAssociation.setVerificationStatus(VerificationStatus.FAILED);
		}

		userUserGroupAssociation.setDeleted(false);

		Calendar dateInvited = null;

		List<UserGroupInvitation> invites = requestService.findUserGroupInvitationRequestsByInvitedUserAndUserGroup(userId, userGroupId);
		for (UserGroupInvitation i : invites) {
			if (dateInvited == null) {
				dateInvited = i.getCreatedOn();
			}
			requestService.acceptRequest(i);
		}

		userUserGroupAssociation.setInvitedFlag(!invites.isEmpty());
		userUserGroupAssociation.setDateInvited(dateInvited);
		userUserGroupAssociation.setDateApplied(DateUtilities.getCalendarNow());

		logger.debug("approvalStatus " + userUserGroupAssociation.getApprovalStatus());

		userUserGroupAssociationDAO.saveOrUpdate(userUserGroupAssociation);

		// Send notification
		if (!suppressNotification) {
			userNotificationService.onUserGroupApplication(userUserGroupAssociation);
		}

		if (!isMember) {
			userChangeLogDAO.saveOrUpdate(
				new UserAppliedToGroupChangeLog(userId,
					authenticationService.getCurrentUserId(),
					authenticationService.getMasqueradeUserId(),
					userGroup));
		}

		summaryService.saveUserGroupAssociationHistorySummary(userUserGroupAssociation);

		userIndexer.reindexById(userId);
		logger.debug("***** applyToGroup ***** END");
		return userUserGroupAssociation;
	}

	public boolean recruitingCampaignGroupEqualsThisGroup(RecruitingCampaign campaign, UserGroup userGroup) {
		return campaign != null && campaign.getCompanyUserGroup() != null && userGroup.getId().equals(campaign.getCompanyUserGroup().getId());
	}

	public UserUserGroupAssociation makeUserGroupAssociation(Long userId, UserGroup userGroup) {
		return new UserUserGroupAssociation(userService.getUser(userId), userGroup);
	}

	@Override
	public UserUserGroupAssociation buildUserUserGroupAssociation(final Long invitedUserId, final UserGroup userGroup, final List<UserGroupInvitation> invites) {
		Assert.notNull(invitedUserId);
		Assert.notNull(userGroup);
		User user = userService.findUserById(invitedUserId);
		Assert.notNull(user);

		UserUserGroupAssociation association = findAssociationByGroupIdAndUserId(userGroup.getId(), user.getId());
		if (association == null) {
			association = new UserUserGroupAssociation(user, userGroup);
		}

		if (authorizeUserForGroup(user, userGroup.getCompany().getId())) {
			association.setVerificationStatus(VerificationStatus.VERIFIED);
			if (association.isPending() && !userGroup.getRequiresApproval()) {
				association.setApprovalStatus(ApprovalStatus.APPROVED);
			}
		} else {
			association.setVerificationStatus(VerificationStatus.FAILED);
		}
		association.setDeleted(false);

		Calendar dateInvited = null;
		for (UserGroupInvitation i : invites) {
			if (dateInvited == null) {
				dateInvited = i.getCreatedOn();
			}
		}
		association.setInvitedFlag(!invites.isEmpty());
		association.setDateInvited(dateInvited);
		association.setDateApplied(DateUtilities.getCalendarNow());
		userUserGroupAssociationDAO.saveOrUpdate(association);
		return association;
	}

	private void createUserGroupAssociation(Long groupId, List<Long> userIds, Long invitedByUserId) {
		Assert.notNull(userIds);
		Assert.notNull(groupId);

		UserGroup group = findGroupById(groupId);
		Assert.notNull(group);

		for (Long userId : userIds) {
			logger.debug(String.format("[group] Add user (%s) to group (%s)", userId, groupId));

			UserUserGroupAssociation association = findAssociationByGroupIdAndUserId(groupId, userId);

			if (group.getOpenMembership()) {
				if (association == null || isTrue(association.getDeleted())) {
					logger.debug("[group] Membership is open. Sending invitation.");
					if (Constants.MY_COMPANY_FOLLOWERS.equals(group.getName())) {
						applyToGroup(groupId, userId, true, true);
					} else {
						requestService.inviteUserToGroup(invitedByUserId, userId, groupId);
					}
				}
			} else {
				logger.debug("[group] Membership is closed. Adding user.");

				// You can apply to group if the user is active and he's a shared worker (can accept work from outside companies) or a company worker
				User user = userService.getUser(userId);
				if (authenticationService.isActive(user) && (userRoleService.hasAclRole(user, AclRole.ACL_SHARED_WORKER)
					|| (userRoleService.hasAclRole(user, AclRole.ACL_WORKER) && user.getCompany().getId().equals(group.getCompany().getId())))) {
					applyToGroup(groupId, userId, true, true);
				} else {
					logger.debug("[group] User does not have permissions to apply to group because user is either inactive or is not a shared worker or is a worker for a different company.");
				}
			}
		}
	}

	@Override
	@UpdateUserGroupSearchIndex(updateUsers = false, userGroupIdArgument = 2)
	public void addUsersToGroup(List<Long> userIds, Long groupId, Long invitedByUserId) {
		createUserGroupAssociation(groupId, userIds, invitedByUserId);
	}

	@Override
	public Map<String, List<String>> applyOnBehalfOfUsers(List<String> userNumbers, Long groupId, Long invitedByUserId, boolean suppressNotification, boolean override) {
		Assert.notNull(userNumbers);
		Assert.notNull(groupId);

		UserGroup group = findGroupById(groupId);
		Assert.notNull(group);

		Map<String, List<String>> results = Maps.newHashMap();
		List<String> success = Lists.newArrayList();
		List<String> failure = Lists.newArrayList();

		for (String userNumber : userNumbers) {
			User user = userService.findUserByUserNumber(userNumber);

			if (user == null) {
				logger.debug(String.format("[group] Could not apply on behalf of user number '(%s)' to group (%s) - user not found", userNumber, groupId));
				failure.add(userNumber);
				continue;
			}

			logger.debug(String.format("[group] Apply on behalf of user '(%s)' to group (%s)", user.getId(), groupId));
			LaneAssociation lane2Relationship = laneService.findAssociationByUserIdAndCompanyId(user.getId(), group.getCompany().getId(), LaneType.LANE_2);
			
			if (lane2Relationship == null) {
				logger.debug(String.format("[group] Could not apply on behalf of user '(%s)' to group (%s) - user not onboarded by this company", user.getId(), groupId));
				failure.add(userNumber);
				continue;
			}

			UserUserGroupAssociation association = applyToGroup(groupId, user.getId(), suppressNotification, override);

			if (association == null || association.getApprovalStatus().isDeclined()) {
				failure.add(userNumber);
				continue;
			}

			success.add(userNumber);
		}

		results.put("success", success);
		results.put("failure", failure);

		return results;
	}

	@Override
	public void addUsersToGroupByUserNumberAsync(List<String> userNumbers, Long groupId, Long invitedByUserId) {
		List<Long> userIds = Lists.newArrayList();
		userIds.addAll(userService.findAllUserIdsByUserNumbers(userNumbers));
		eventRouter.sendEvent(new AddUsersToGroupEvent(groupId, userIds, invitedByUserId));
	}

	@Override
	public UserPagination findAllUsersOfGroup(Long userGroupId, UserPagination pagination) {
		return userUserGroupAssociationDAO.findAllUserOfGroup(userGroupId, pagination);
	}

	@Override
	public List<Long> findAllUserIdsOfGroup(Long userGroupId) {
		return userUserGroupAssociationDAO.findAllUserIdsOfGroup(userGroupId);
	}

	@Override
	public List<Long> findAllUserGroupIds() {
		return userGroupDAO.findAllUserGroupIds();
	}

	private UserPagination findAllPendingUsers(Long groupId, UserPagination pagination) {
		return userGroupDAO.findAllUsersByVerificationStatusAndApprovalStatus(groupId, VerificationStatus.VERIFIED, ApprovalStatus.PENDING, pagination);
	}

	@Override
	public UserPagination findAllInvitedUsers(Long groupId, UserPagination pagination) {
		List<UserGroupInvitation> invitations = requestService.findUserGroupInvitationRequestsByUserGroup(groupId);

		List<User> users = new ArrayList<>();
		for (Request i : invitations)
			users.add(i.getInvitedUser());

		pagination.setResults(users);
		pagination.setRowCount(users.size());
		return pagination;
	}

	@Override
	public void declineUser(Long userGroupId, Long userId) {
		Assert.notNull(userGroupId);
		Assert.notNull(userId);

		List<UserUserGroupAssociation> declinedUsers = declineUserGroupAssociations(userGroupId, Lists.newArrayList(userId));
		if (CollectionUtils.isNotEmpty(declinedUsers)) {
			eventRouter.sendEvent(
				eventFactory.buildUserGroupAssociationUpdateEvent(
					CollectionUtilities.first(declinedUsers)
				)
			);
		}
	}

	@Override
	@UpdateUserGroupSearchIndex(updateUsers = false, userGroupIdArgument = 1)
	public void declineUsers(Long userGroupId, Long[] userIds) {
		Assert.notNull(userGroupId);
		List<Long> users = Arrays.asList(userIds);

		declineUserGroupAssociations(userGroupId, users);

		if (isNotEmpty(userIds)) {
			eventRouter.sendEvent(
				eventFactory.buildUserGroupAssociationUpdateEvent(userGroupId, users)
			);
		}
	}

	private List<UserUserGroupAssociation> declineUserGroupAssociations(Long userGroupId, List<Long> userIds) {
		Assert.notNull(userGroupId);
		Assert.notNull(userIds);

		List<UserUserGroupAssociation> userGroupAssociations =
			userUserGroupAssociationDAO.findUserUserGroupAssociationByUserGroupIdAndUserId(userGroupId, userIds);

		final List<UserUserGroupAssociation> notificationsToBeSent = Lists.newArrayListWithExpectedSize(userGroupAssociations.size());

		if(CollectionUtils.isNotEmpty(userGroupAssociations)) {
			UserGroup userGroup = CollectionUtils.isNotEmpty(userGroupAssociations) ? CollectionUtilities.first(userGroupAssociations).getUserGroup() : null;
			Assert.notNull(userGroup);

			for (UserUserGroupAssociation userGroupAssociation : userGroupAssociations) {
				if (!userGroupAssociation.isDeclined()) {

					userGroupAssociation.setApprovalStatus(ApprovalStatus.DECLINED);

					// Send user notification
					if (BooleanUtils.isNotFalse(userGroup.getOpenMembership())) {
						notificationsToBeSent.add(userGroupAssociation);
					}

					userChangeLogDAO.saveOrUpdate(new UserGroupMembershipDeclinedChangeLog(userGroupAssociation.getUser().getId(), authenticationService.getCurrentUserId(),
						authenticationService.getMasqueradeUserId(), userGroup));

					summaryService.saveUserGroupAssociationHistorySummary(userGroupAssociation);
				}
			}

			for (UserUserGroupAssociation association : notificationsToBeSent) {
				userNotificationService.onUserDeclinedForGroup(association);
			}

		}

		return userGroupAssociations;
	}

	@Override
	public void approveUser(Long userGroupId, Long userId) {
		Assert.notNull(userGroupId);
		Assert.notNull(userId);

		List<UserUserGroupAssociation> associations = approveUserAssociations(userGroupId, Lists.newArrayList(userId));
		if (CollectionUtils.isNotEmpty(associations)) {
			eventRouter.sendEvent(
				eventFactory.buildUserGroupAssociationUpdateEvent(
					CollectionUtilities.first(associations)));
		}
	}

	@Override
	public void approveUsers(Long userGroupId, Long[] userIds) {
		Assert.notNull(userGroupId);
		Assert.notNull(userIds);

		List<Long> users = Arrays.asList(userIds);
		List<UserUserGroupAssociation> associations = approveUserAssociations(userGroupId, users);

		if (CollectionUtils.isNotEmpty(associations)) {
			eventRouter.sendEvent(
				eventFactory.buildUserGroupAssociationUpdateEvent(userGroupId, users)
				);
		}
	}

	@Override
	public List<UserUserGroupAssociation> approveUserAssociations(long userGroupId, List<Long> userIds) {
		List<UserUserGroupAssociation> groupAssociations =
			userUserGroupAssociationDAO.findUserUserGroupAssociationByUserGroupIdAndUserId(userGroupId, userIds);

		Assert.notNull(groupAssociations);

		UserGroup userGroup = CollectionUtils.isNotEmpty(groupAssociations) ? CollectionUtilities.first(groupAssociations).getUserGroup() : null;
		Assert.notNull(userGroup);

		final List<UserUserGroupAssociation> notifications = Lists.newArrayListWithExpectedSize(groupAssociations.size());
		final List<UserGroupInvitation> invitesToAccept = Lists.newArrayList();
		final List<Long> usersToAddAsLane3 = Lists.newArrayListWithExpectedSize(groupAssociations.size());
		final List<UserGroupMembershipApprovedChangeLog> userChangeLogs = Lists.newArrayListWithExpectedSize(groupAssociations.size());

		boolean sendNotifications = false;
		// Users should not know they are in a private group
		if (!userGroup.isAutoGenerated() && userGroup.getOpenMembership()) {
			sendNotifications = true;
		}

		for (UserUserGroupAssociation groupAssociation : groupAssociations) {
			boolean isVerifiedAndApproved = groupAssociation.getVerificationStatus().isVerified() && groupAssociation.getApprovalStatus().isApproved();

			if (!isVerifiedAndApproved) {
				User user = groupAssociation.getUser();

				// A user can be approved despite having failed verification. Odd but true.
				// Mark the record as overrideMember = true
				if (groupAssociation.getVerificationStatus().isFailed()) {
					ExtendedUserDetails userDetails = (ExtendedUserDetails) extendedUserDetailsService.loadUserByEmail(userGroup.getOwner().getEmail(), null);
					Eligibility eligibility = eligibilityService.getEligibilityFor(groupAssociation.getUser().getId(), userGroup);
					groupAssociation.setOverrideMember(!eligibility.isEligible());
				}

				groupAssociation.setApprovalStatus(ApprovalStatus.APPROVED);
				groupAssociation.setVerificationStatus(VerificationStatus.VERIFIED);

				if (sendNotifications) {
					notifications.add(groupAssociation);
				}

				// Adding user to Lane3 (WORK-1010)
				// Only if he's approved for the group (WORK-1758)
				if (authenticationService.isLane3Active(user)) {
					usersToAddAsLane3.add(user.getId());
				}

				invitesToAccept.addAll(requestService.findUserGroupInvitationRequestsByInvitedUserAndUserGroup(user.getId(), userGroupId));

				userChangeLogs.add(new UserGroupMembershipApprovedChangeLog(user.getId(), authenticationService.getCurrentUserId(),
					authenticationService.getMasqueradeUserId(), userGroup));

				logger.debug("approvalStatus " + groupAssociation.getApprovalStatus());
			}
		}

		//Add as lane 3
		laneService.addUsersToCompanyLane3(usersToAddAsLane3, userGroup.getCompany().getId());

		//Accept invites
		for (UserGroupInvitation i : invitesToAccept) {
			requestService.acceptRequest(i);
		}

		//UserChangeLog
		userChangeLogDAO.saveAll(userChangeLogs);

		//Send notifications
		for (UserUserGroupAssociation notification : notifications) {
			userNotificationService.onUserApprovedToGroup(notification);
		}

		return groupAssociations;
	}

	@Override
	public void revalidateAllAssociations(Long groupId) {
		eventRouter.sendEvent(eventFactory.buildRevalidateGroupAssociationsEvent(groupId));
	}

	@Override
	public Integer countAllActiveGroupMembers(Long groupId) {
		return userGroupDAO.countAllActiveGroupMembers(groupId);
	}

	@Override
	public List<Long> getAllActiveGroupMemberIds(Long groupId) {
		return userGroupDAO.getAllActiveGroupMemberIds(groupId);
	}

	@Override
	public Integer countAllActiveGroupMembersByCompany(Long companyId) {
		return userGroupDAO.countAllActiveGroupMembersByCompanyId(companyId);
	}

	@Override
	public List<Long> getDueForValidationUserGroupIds() {
		return userGroupDAO.findDueForValidationUserGroupIds();
	}

	@Override
	public Integer countAllGroupsByCompany(Long companyId) {
		return userGroupDAO.countCompanyUserGroups(companyId);
	}

	@Override
	public UserGroupPagination findAllGroupsByCompanyId(Long companyId, UserGroupPagination userGroupPagination) {
		return userGroupDAO.findAllUserGroupsByCompanyId(companyId, userGroupPagination);
	}

	@Override
	public List<Long> getUserGroupIdsWithAgreement(Long agreementId) {
		return userGroupDAO.getUserGroupIdsWithAgreement(agreementId);
	}

	@Override
	public List<UserGroupLastRoutedDTO> findAllWithNewLastRoutedSinceLastUpdate() {
		return userGroupDAO.findAllWithNewLastRoutedSinceLastUpdate();
	}

	@Override
	public UserGroup saveOrUpdateCompanyUserGroup(UserGroupDTO userGroupDTO) {
		Assert.notNull(userGroupDTO);

		UserGroup userGroup;
		if (userGroupDTO.getUserGroupId() == null) {

			Assert.isNull(findCompanyUserGroupByName(userGroupDTO.getCompanyId(), userGroupDTO.getName()), "A company user group with this name already exists ");

			userGroup = BeanUtilities.newBean(UserGroup.class, userGroupDTO);

			if (userGroupDTO.getCompanyId() != null)
				userGroup.setCompany(companyDAO.get(userGroupDTO.getCompanyId()));

			if (userGroupDTO.getOwnerId() != null) {
				userGroup.setOwner(userDAO.get(userGroupDTO.getOwnerId()));
			}

		} else {
			String[] ignore = {"terms"};
			userGroup = userGroupDAO.get(userGroupDTO.getUserGroupId());

			Assert.isTrue(userGroup.getOpenMembership().equals(userGroupDTO.getOpenMembership()), "Cannot change the membership type of a group.");

			BeanUtilities.copyProperties(userGroup, userGroupDTO, ignore);
		}

		if (userGroupDTO.getIndustryId() != null) {
			userGroup.setIndustry(industryDAO.get(userGroupDTO.getIndustryId()));
		}

		if (userGroupDTO.getOwnerId() != null) {
			userGroup.setOwner(userDAO.findUserById(userGroupDTO.getOwnerId()));
		}

		userGroupDAO.saveOrUpdate(userGroup);

		final long currentUserId = authenticationService.getCurrentUserId();
		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(currentUserId, "org_structures");
		if (hasOrgFeatureToggle) {
			userGroupOrgUnitAssociationDAO.setUserGroupOrgUnitAssociation(userGroup.getUuid(), userGroupDTO.getOrgUnitUuids());
		}

		//this is after the saveOrUpdate since its a separate transaction and will fail if its a new group
		if (userGroupDTO.getSkillIds() != null) {
			userGroupSkillAssociationDAO.setUserGroupSkillAssociation(userGroup, userGroupDTO.getSkillIds());
		}

		eventRouter.sendEvent(new GroupUpdateSearchIndexEvent(Lists.newArrayList(userGroup.getId())));

		return userGroup;
	}

	@Override
	public void removeAssociation(long userGroupId, long userId) {
		UserUserGroupAssociation association =
			userUserGroupAssociationDAO.findUserUserGroupAssociationByUserGroupIdAndUserId(userGroupId, userId);

		if (association == null) {
			return;
		}

		deleteAssociation(association, true);
	}

	@Override
	public void removeAssociations(Long userGroupId, Long[] userIds, Long companyId) {
		Assert.notNull(userIds);
		if (userIds.length == 0) {
			return;
		}
		Assert.notNull(userGroupId);
		Assert.notNull(companyId);
		UserGroup group = findGroupById(userGroupId);
		Assert.notNull(group);
		Assert.isTrue(companyId.equals(group.getCompany().getId()));

		for (Long userId : userIds) {
			UserUserGroupAssociation association = userUserGroupAssociationDAO.findUserUserGroupAssociationByUserGroupIdAndUserId(userGroupId, userId);
			deleteAssociation(association, false);
		}

		eventRouter.sendEvent(new GroupUpdateSearchIndexEvent().setGroupId(userGroupId));
		eventRouter.sendEvent(
			new UserSearchIndexEvent(Arrays.asList(userIds))
		);
	}

	@Override
	public void removeAllAssociationsAndInvitationsByUserAndCompanyId(Long userId, Long companyId) {
		Assert.notNull(companyId);
		Assert.notNull(userId);

		User user = userService.getUser(userId);
		Assert.notNull(user);

		// Get all associations (member, member override, pending, pending override)
		List<UserUserGroupAssociation> associations = userUserGroupAssociationDAO.findAllActiveAssociationsByUserIdAndCompanyId(userId, companyId);

		// Get group ids so we can reindex the groups
		List<Long> groupIds = Lists.newArrayListWithExpectedSize(associations.size());

		// Remove all associations and log changes
		for (UserUserGroupAssociation association : associations) {
			UserGroup group = association.getUserGroup();
			Assert.notNull(group);
			groupIds.add(group.getId());

			association.setDeleted(true);
			association.setApprovalStatus(ApprovalStatus.PENDING);
			association.setVerificationStatus(VerificationStatus.PENDING);

			userChangeLogDAO.saveOrUpdate(new UserLeftGroupChangeLog(userId, authenticationService.getCurrentUserId(),
					authenticationService.getMasqueradeUserId(), group));

			summaryService.saveUserGroupAssociationHistorySummary(association);
		}

		// Remove all invites
		requestDAO.deleteUsersInvitationsToAllCompanyGroups(userId, companyId);

		// Reindex user and groups
		eventRouter.sendEvent(new UserSearchIndexEvent(userId));
		eventRouter.sendEvent(new GroupUpdateSearchIndexEvent(Lists.newArrayList(groupIds)));
	}

	@Override
	public void removeAllAssociationsByUserAndCompanyId(Long userId, Long companyId) {
		Assert.notNull(companyId);
		Assert.notNull(userId);

		// Find all the groups where the user is a member of and remove it
		List<UserUserGroupAssociation> groups = userGroupDAO.findAllGroupAssociationByCompanyIdAndUser(companyId, userId);
		Set<Long> groupIds = Sets.newHashSetWithExpectedSize(groups.size());

		for (UserUserGroupAssociation association : groups) {
			deleteAssociation(association, false);
			groupIds.add(association.getUserGroup().getId());
		}

		eventRouter.sendEvent(new UserSearchIndexEvent(userId));
		eventRouter.sendEvent(new GroupUpdateSearchIndexEvent(groupIds));
	}

	@Override
	public Integer removeAllAssociationsBetweenCompanies(long companyId1, long companyId2) {
		List<Long> ids = userUserGroupAssociationDAO.findAllAssociationIdsBetweenCompanies(companyId1, companyId2);
		return userUserGroupAssociationDAO.removeAssociations(ids);
	}

	@Override
	public UserUserGroupAssociation findAssociationByGroupIdAndUserId(long userGroupId, long userId) {
		return userUserGroupAssociationDAO.findUserUserGroupAssociationByUserGroupIdAndUserId(userGroupId, userId);
	}

	@Override
	public UserUserGroupAssociationPagination findAllPendingAssociationByGroupId(Long userGroupId, UserUserGroupAssociationPagination pagination) {
		return userUserGroupAssociationDAO.findAllUserUserAssociationsByUserGroupIdAndVerificationStatusAndApprovalStatus(
				userGroupId, VerificationStatus.VERIFIED, ApprovalStatus.PENDING, pagination
		);
	}

	@Override
	public UserUserGroupAssociationPagination findAllPendingAndFailedAssociationByGroupId(Long userGroupId, UserUserGroupAssociationPagination pagination) {
		return userUserGroupAssociationDAO.findAllUserUserAssociationsByUserGroupIdAndVerificationStatusAndApprovalStatus(
			userGroupId, VerificationStatus.FAILED, ApprovalStatus.PENDING, pagination
		);
	}

	/**
	 * For auto generated groups. Example: My Paid Workers Returns the auto-generated group if it has been already created for the company, otherwise it creates the group.
	 *
	 * @param companyId , groupName, actor, legacyGroupName
	 */
	@Override
	public UserGroup findOrCreateCompanyGroup(Long companyId, String groupName, User actor, String legacyGroupName) {
		Assert.notNull(companyId);
		Assert.hasText(groupName);

		UserGroup companyGroup = findCompanyUserGroupByName(companyId, groupName);

		if (companyGroup == null) {
			companyGroup = findCompanyUserGroupByName(companyId, legacyGroupName);

			if (companyGroup == null) {
				companyGroup = new UserGroup();
			}
			companyGroup.setCompany(companyDAO.get(companyId));
			companyGroup.setName(groupName);
			companyGroup.setDescription(Constants.AUTO_GENERATED_GROUP_DESCRIPTION + groupName);
			companyGroup.setOpenMembership(Constants.MY_COMPANY_FOLLOWERS.equals(groupName));
			companyGroup.setRequiresApproval(false);
			companyGroup.setAutoGenerated(true);
			companyGroup.setOwner(actor);

			userGroupDAO.saveOrUpdate(companyGroup);
		}

		return companyGroup;
	}

	@Override
	public boolean isCompanyUserGroup(Long companyId, Long groupId) {
		Assert.notNull(companyId);
		Assert.notNull(groupId);

		UserGroup companyGroup = findGroupById(groupId);
		return companyGroup != null && (companyGroup.getCompany().getId().equals(companyId));
	}

	@Override
	public List<UserGroup> findCompanyOwnedGroupsHavingUserAsMember(Long companyId, Long userId) {
		Assert.notNull(companyId);
		Assert.notNull(userId);
		List<UserUserGroupAssociation> associations = userUserGroupAssociationDAO.findCompanyOwnedGroupAssociationsHavingUserAsMember(companyId, userId);
		//noinspection unchecked
		return newListPropertyProjection(associations, "userGroup");
	}

	@Override
	public boolean authorizeUserForGroup(long userGroupId, long userId) {
		User user = userService.getUser(userId);
		UserGroup userGroup = findGroupById(userGroupId);

		return authorizeUserForGroup(user, userGroup.getCompany().getId());
	}

	private boolean authorizeUserForGroup(User user, long userGroupCompanyId) {
		long userCompanyId = user.getCompany().getId();
		long userId = user.getId();

		return userCompanyId == userGroupCompanyId ||
			laneService.isUserPartOfLane123(userId, userGroupCompanyId) ||
			authenticationService.isLane3Active(user);
	}

	@Override
	public List<UserUserGroupAssociation> findAllActiveAssociations(long groupId) {
		return userUserGroupAssociationDAO.findAllActiveAssociations(groupId);
	}

	public void deleteAssociation(UserUserGroupAssociation userGroupAssociation, boolean reindex) {
		if (userGroupAssociation == null) {
			return;
		}

		userGroupAssociation.setDeleted(true);
		userGroupAssociation.setApprovalStatus(ApprovalStatus.PENDING);
		userGroupAssociation.setVerificationStatus(VerificationStatus.PENDING);
		userGroupAssociation.setOverrideMember(false);

		userChangeLogDAO.saveOrUpdate(
			new UserLeftGroupChangeLog(
				userGroupAssociation.getUser().getId(),
				authenticationService.getCurrentUserId(),
				authenticationService.getMasqueradeUserId(),
				userGroupAssociation.getUserGroup()
			)
		);

		summaryService.saveUserGroupAssociationHistorySummary(userGroupAssociation);

		if (reindex) {
			eventRouter.sendEvent(new GroupUpdateSearchIndexEvent().setGroupId(userGroupAssociation.getUserGroup().getId()));
			eventRouter.sendEvent(new UserSearchIndexEvent(userGroupAssociation.getUser().getId()));
		}
	}

	@Override
	public void reinviteAllGroupMembers(long userGroupId, UserGroupInvitationType userGroupInvitationType) {
		List<UserUserGroupAssociation> associations = findAllActiveAssociations(userGroupId);
		List<Long> userIds = new ArrayList<>();

		for (UserUserGroupAssociation association : associations) {
			logger.debug("Deleting association for group id " + association.getUserGroup().getId() + " user " + association.getUser().getId());
			deleteAssociation(association, false);
			requestService.inviteUserToGroup(authenticationService.getCurrentUser().getId(),
				association.getUser().getId(), association.getUserGroup().getId(), userGroupInvitationType);
			userIds.add(association.getUser().getId());
		}

		groupSearchService.reindexGroup(userGroupId);
		userIndexer.reindexById(userIds);
	}

	@Override
	public List<RequestContext> getRequestContext(Long groupId) {
		List<RequestContext> contexts = Lists.newArrayList(RequestContext.PUBLIC);
		User currentUser = authenticationService.getCurrentUser();
		UserGroup group = userGroupDAO.findUserGroupById(groupId);
		User creator = userService.getUser(group.getCreatorId());

		if (group.getCreatorId().equals(currentUser.getId())) {
			contexts.add(RequestContext.OWNER);
			contexts.add(RequestContext.ADMIN);
		} else if (group.getCompany().getId().equals(currentUser.getCompany().getId()) ||
				creator.getCompany().getId().equals(currentUser.getCompany().getId())) {
			contexts.add(RequestContext.COMPANY_OWNED);
			if (currentUser.isAdminOrManager()) {
				contexts.add(RequestContext.ADMIN);
			}
		}
		return contexts;
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findMyCompanyGroups(Long userId, ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(pagination);
		User user = userService.getUser(userId);
		Assert.notNull(user);
		pagination.setIsCurrentUserAnAdmin(user.isAdminOrManager());
		return managedCompanyUserGroupDAO.findMyCompanyGroups(user.getCompany().getId(), userId, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findCompanyActiveGroups(Long userId, ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(pagination);
		User user = userService.getUser(userId);
		Assert.notNull(user);
		pagination.setIsCurrentUserAnAdmin(user.isAdminOrManager());
		pagination.addFilter(ManagedCompanyUserGroupRowPagination.FILTER_KEYS.ACTIVATED, "1");
		return managedCompanyUserGroupDAO.findMyCompanyGroups(user.getCompany().getId(), userId, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findSharedAndOwnedGroups(
			final Long userId,
			final Long companyId,
			final ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(pagination);
		User user = userService.getUser(userId);
		Assert.notNull(user);
		pagination.setIsCurrentUserAnAdmin(user.isAdminOrManager());

		final boolean orgEnabledForUser = featureEntitlementService.hasFeatureToggle(userId, "org_structures");
		Map<String, OrgUnitDTO> uuidToOrgUnitMap = Maps.newHashMap();
		if (orgEnabledForUser) {
			final String orgModeSetting = orgStructureService.getOrgModeSetting(userId);
			final List<OrgUnitDTO> orgUnitsToFilterBy = orgStructureService.getSubtreePaths(userId, companyId, orgModeSetting);
			uuidToOrgUnitMap = Maps.uniqueIndex(orgUnitsToFilterBy, new Function<OrgUnitDTO, String>() {
				public String apply(final OrgUnitDTO orgUnitDto) {
					return orgUnitDto.getUuid();
				}
			});
		}

		final ManagedCompanyUserGroupRowPagination userGroupRowPagination =
			managedCompanyUserGroupDAO.findSharedAndOwnedGroups(
					user.getCompany().getId(),
					userId,
					orgEnabledForUser,
					uuidToOrgUnitMap,
					pagination);

		final List<String> talentPoolUuids =
			extract(userGroupRowPagination.getResults(), on(ManagedCompanyUserGroupRow.class).getUuid());

		final Map<String, List<TalentPoolParticipant>> talentPoolsAndParticipants =
			talentPoolService.getTalentPoolAndParticipants(talentPoolUuids);

		decorateWithVendorCounts(talentPoolsAndParticipants, userGroupRowPagination.getResults());

		return userGroupRowPagination;
	}

	private void decorateWithVendorCounts(Map<String, List<TalentPoolParticipant>> talentPoolsAndParticipants, List<ManagedCompanyUserGroupRow> rows) {
		if (talentPoolsAndParticipants.isEmpty())
			return;

		Set<GroupMemberRequestType> memberSet =
			ImmutableSet.of(GroupMemberRequestType.MEMBER, GroupMemberRequestType.MEMBER_PASSED, GroupMemberRequestType.MEMBER_OVERRIDE);

		Set<GroupMemberRequestType> pendingSet =
			ImmutableSet.of(GroupMemberRequestType.PENDING, GroupMemberRequestType.PENDING_PASSED, GroupMemberRequestType.PENDING_FAILED);

		for (ManagedCompanyUserGroupRow row : rows) {

			List<TalentPoolParticipant> participants = talentPoolsAndParticipants.get(row.getUuid());

			if (participants == null)
				continue;

			for (TalentPoolParticipant participant : participants) {

				if (participant == null)
					continue;

				final Optional<GroupMemberRequestType> memberRequestType =
					talentPoolParticipationAdapter.getGroupMemberRequestType(participant.getTalentPoolParticipation());

				if (!memberRequestType.isPresent())
					continue;

				if (memberSet.contains(memberRequestType.get())) {
					row.setMemberCount(row.getMemberCount() + 1);
				}
				if (pendingSet.contains(memberRequestType.get())) {
					row.setPendingApplicantCount(row.getPendingApplicantCount() + 1);
				}
				if (memberRequestType.get() == GroupMemberRequestType.INVITED) {
					row.setInvitedApplicantCount(row.getInvitedApplicantCount() + 1);
				}
			}
		}
	}

	@Override
	public List<ManagedCompanyUserGroupRow> findSharedAndOwnedGroups(Long companyId) {
		Assert.notNull(companyId);
		return managedCompanyUserGroupDAO.findSharedAndOwnedGroups(companyId);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findMyGroupMemberships(Long userId, ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		User user = userService.getUser(userId);
		return findMyGroupMemberships(user, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findMyGroupMemberships(User user, ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(user);
		Assert.notNull(pagination);
		return managedCompanyUserGroupDAO.findMyGroupMemberships(user.getCompany().getId(), user.getId(), pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findMyGroupMembershipsAndApplications(Long userId, ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		User user = userService.getUser(userId);
		Assert.notNull(user);
		pagination.setIsCurrentUserAnAdmin(user.isAdminOrManager());
		return managedCompanyUserGroupDAO.findMyGroupMembershipsAndApplications(user.getCompany().getId(), userId, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findVendorGroupMembershipsAndApplications(
		Long userId,
		Set<Long> groupIds,
		ManagedCompanyUserGroupRowPagination pagination
	) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		Assert.notNull(groupIds);
		User user = userService.getUser(userId);
		Assert.notNull(user);
		pagination.setIsCurrentUserAnAdmin(user.isAdminOrManager());
		return managedCompanyUserGroupDAO.findVendorGroupMembershipsAndApplications(user.getCompany().getId(), userId, groupIds, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findCompanyGroupsActiveOpenMembership(
			long companyId,
			ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(pagination);

		final Long currentUserId = authenticationService.getCurrentUser().getId();
		final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(currentUserId, "org_structures");

		List<String> orgUnitUuids = Lists.newArrayList();
		if (hasOrgFeatureToggle) {
			orgUnitUuids = orgStructureService.getSubtreePathOrgUnitUuidsForCurrentOrgMode(currentUserId, companyId);
		}

		return managedCompanyUserGroupDAO.findCompanyGroupsActiveOpenMembershipByCompanyIdAndOrgUnits(companyId, pagination, orgUnitUuids, hasOrgFeatureToggle);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findCompanyGroupsOpenMembership(Long companyId, ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return managedCompanyUserGroupDAO.findCompanyGroupsOpenMembership(companyId, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findGroupsActiveOpenMembershipByGroupIds(Set<Long> groupIds, ManagedCompanyUserGroupRowPagination pagination) {
		Assert.notNull(groupIds);
		Assert.notNull(pagination);
		if (isEmpty(groupIds)) {
			return pagination;
		}

		return managedCompanyUserGroupDAO.findCompanyGroupsActiveOpenMembershipByGroupIds(groupIds, pagination);
	}

	@Override
	@UpdateUserGroupSearchIndex(updateUsers = true, userGroupIdArgument = 1)
	public void deleteGroup(Long userGroupId) {
		Assert.notNull(userGroupId);
		UserGroup group = findGroupById(userGroupId);
		Assert.notNull(group);
		if (group.getDeleted()) {
			return;
		}

		String newName = StringUtilities.getDeletedName(group.getName(), 120);
		group.setName(newName);
		group.setDeleted(true);
	}

	@Override
	public GroupMembershipPagination findGroupMembersByUserGroupId(Long groupId, String groupMemberRequestTypeString, GroupMembershipPagination groupMembershipPagination) {
		Assert.notNull(groupId);
		Assert.notNull(groupMembershipPagination);
		Assert.hasText(groupMemberRequestTypeString);

		GroupMemberRequestType groupMemberRequestType = GroupMemberRequestType.valueOf(groupMemberRequestTypeString.toUpperCase());
		UserGroup group = findGroupById(groupId);
		return groupMembershipDAO.findGroupMembersByUserGroupId(groupId, groupMemberRequestType, groupMembershipPagination, group.getCompany().getId());
	}

	@Override
	public Integer countGroupMembersByUserGroupId(Long groupId, String memberTypeString) {
		GroupMemberRequestType memberType = GroupMemberRequestType.valueOf(memberTypeString.toUpperCase());
		UserGroup group = findGroupById(groupId);
		Long companyId = group.getCompany().getId();
		return groupMembershipDAO.countGroupMembersByUserGroupId(groupId, companyId, memberType);
	}

	@Override
	public int countPendingMembershipsByCompany(Long companyId) {
		return groupMembershipDAO.countPendingMembershipsByCompany(companyId);
	}

	@Override
	public int reassignGroupOwnership(Long fromId, Long toId) {
		List<Long> groupIds = userGroupDAO.findGroupIdsByOwner(fromId);
		int groupsUpdated = userGroupDAO.updateGroupOwner(toId, groupIds);
		if (groupsUpdated > 0) {
			groupSearchService.reindexGroups(groupIds);
		}

		if (groupsUpdated != groupIds.size())
			logger.warn("Not all groups have been updated");

		logger.debug("User groups updated: " + groupsUpdated);
		return groupsUpdated;
	}

	@Override
	public List<Long> getEligibleUserIdsForInvitationToGroup(List<Long> userIds, Long groupId) {
		Assert.notEmpty(userIds);
		Assert.notNull(groupId);

		ArrayList<Long> result = Lists.newArrayList(userIds);

		final List<UserGroupInvitation> allInvitations = requestService.findUserGroupInvitationRequestsAllStatusByInvitedUsersAndUserGroup(userIds, groupId);
		final List<UserUserGroupAssociation> allAssociations = userUserGroupAssociationDAO.findAllAssociationsByGroupAndUserIds(userIds, groupId);

		// build lookup maps
		final Multimap<Long, UserUserGroupAssociation> associationMap = ArrayListMultimap.create();
		for (UserUserGroupAssociation association : allAssociations) {
			associationMap.put(association.getUser().getId(), association);
		}
		final Multimap<Long, UserGroupInvitation> invitationMap = ArrayListMultimap.create();
		for (UserGroupInvitation invitation : allInvitations) {
			invitationMap.put(invitation.getInvitedUser().getId(), invitation);
		}

		for (Long userId : userIds) {

			// 	1. has no association and no invites - ALLOW
			if (!associationMap.containsKey(userId) && !invitationMap.containsKey(userId)) {
				continue;
			}

			// 	2. has an invite and no association - REMOVE
			if (!associationMap.containsKey(userId) && invitationMap.containsKey(userId)) {
				result.remove(userId);
				continue;
			}

			// 	3. has a non-deleted sent invite - REMOVE
			if (invitationMap.containsKey(userId)) {
				boolean hasSentInvite = Iterables.any(invitationMap.get(userId), new Predicate<UserGroupInvitation>() {
					@Override public boolean apply(UserGroupInvitation invitation) {
						return invitation.isSent() && isNotTrue(invitation.getDeleted());
					}
				});
				if (hasSentInvite) {
					result.remove(userId);
					continue;
				}
			}

			// 4. has an active or pending association - REMOVE
			if (associationMap.containsKey(userId)) {
				boolean hasIneligibleAssociation = Iterables.any(associationMap.get(userId), new Predicate<UserUserGroupAssociation>() {
					@Override public boolean apply(UserUserGroupAssociation association) {
						return (association.isActive() || association.isPending()) && isNotTrue(association.getDeleted());
					}
				});

				if (hasIneligibleAssociation) {
					result.remove(userId);
					continue;
				}
			}

			// 5. have a accepted invite and a non-deleted association - REMOVE
			// (if they had a deleted one, this means they accepted an invite in the past and were since removed, which is OK)
			if (invitationMap.containsKey(userId) && associationMap.containsKey(userId)) {
				boolean hasAcceptedInvite = Iterables.any(invitationMap.get(userId), new Predicate<UserGroupInvitation>() {
					@Override public boolean apply(UserGroupInvitation invitation) {
						return invitation.isAccepted() && isNotTrue(invitation.getDeleted());
					}
				});

				if (hasAcceptedInvite) {
					boolean hasDeletedAssociation = Iterables.any(associationMap.get(userId), new Predicate<UserUserGroupAssociation>() {
						@Override public boolean apply(UserUserGroupAssociation association) {
							return isTrue(association.getDeleted());
						}
					});

					if (!hasDeletedAssociation) {
						result.remove(userId);
					}
				} else {
					// the invite is not accepted - REMOVE
					result.remove(userId);
				}
			}
		}

		return result;
	}

	@Override
	public List<UserGroupThroughputDTO> calculateThroughputSinceLastUpdate() {
		return userGroupDAO.calculateThroughputSinceLastUpdate();
	}

	@Override
	public UserUserGroupAssociation saveOrUpdateUserUserGroupAssociation(UserUserGroupAssociation userUserGroupAssociation) {
		if (userUserGroupAssociation != null) {
			userUserGroupAssociationDAO.saveOrUpdate(userUserGroupAssociation);
		}
		return userUserGroupAssociation;
	}

	@Override
	public UserGroup saveOrUpdateUserGroup(UserGroup userGroup) {
		if (userGroup != null) {
			userGroupDAO.saveOrUpdate(userGroup);
		}

		return userGroup;
	}

	@Override
	public UserGroupSummary findUserGroupSummaryByUserGroup(long userGroupId) {
		return userGroupSummaryDAO.findByUserGroup(userGroupId);
	}

	@Override
	public int countGroupsCreatedSince(long companyId, Calendar fromDate) {
		return userGroupDAO.countGroupsCreatedSince(companyId, fromDate);
	}

	@Override
	public Long findUserGroupIdByUuid(final String uuid) {
		return userGroupDAO.findUserGroupIdByUuid(uuid);
	}

	@Override
	public String findUserGroupUuidById(final Long userGroupId) {
		return userGroupDAO.findUserGroupUuidById(userGroupId);
	}

	@Override
	public int countUserGroupMemberships(long userId) {
		return userGroupDAO.countUserGroupMemberships(userId);
	}

	@Override
	public Map<String, Long> findUserGroupUuidIdPairsByUuids(final Collection<String> uuids) {
		return userGroupDAO.findUserGroupUuidIdPairsByUuids(uuids);
	}

	@Override
	public List<SkillDTO> findUserGroupSkills(Long userGroupId) {
		List<SkillDTO> skillDTOs = Lists.newArrayList();
		for (Skill skill : userGroupSkillAssociationDAO.findUserGroupSkills(userGroupId)) {
			skillDTOs.add(
				new SkillDTO.Builder()
					.setId(skill.getId())
					.setName(skill.getName())
					.build()
			);
		}

		return skillDTOs;
	}

	public enum SortOption {
		CREATED_ON("created_on", "Sort by Create Date", Pagination.SORT_DIRECTION.DESC),
		RELEVANCY("default", "Sort by Relevancy", Pagination.SORT_DIRECTION.ASC),
		NAME("name", "Sort by Name", Pagination.SORT_DIRECTION.ASC),
		MEMBER_COUNT("member_count", "Sort by Member Count", Pagination.SORT_DIRECTION.DESC);

		private String key;
		private String description;
		private Pagination.SORT_DIRECTION sortDirection;

		SortOption(final String key, final String description, final Pagination.SORT_DIRECTION sortDirection) {
			this.key = key;
			this.description = description;
			this.sortDirection = sortDirection;
		}

		public String getKey() {
			return key;
		}

		public String getDescription() {
			return description;
		}

		public Pagination.SORT_DIRECTION getSortDirection() {
			return sortDirection;
		}

		// for ${enumInstance.name} to work in JSPs
		public String getName() {
			return this.name();
		}
	}
}
