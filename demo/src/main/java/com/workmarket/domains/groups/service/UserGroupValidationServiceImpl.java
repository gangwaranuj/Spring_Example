package com.workmarket.domains.groups.service;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.service.UserGroupAssociationValidationUpdateServiceImpl.AssociationUpdateType;
import com.workmarket.domains.groups.service.association.UserGroupAssociationService;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.group.UserExpiration;
import com.workmarket.group.UserGroupExpiration;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
public class UserGroupValidationServiceImpl implements UserGroupValidationService {

	@Autowired private UserGroupService userGroupService;
	@Autowired private GroupSearchService groupSearchService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private UserIndexer userIndexer;
	@Autowired private SummaryService summaryService;
	@Autowired private UserGroupAssociationService userGroupAssociationService;
	@Autowired private UserGroupAssociationValidationUpdateService userGroupAssociationValidationUpdateService;
	@Autowired private UserNotificationService userNotificationService;

	@Override
	@SuppressWarnings("unchecked")
	public void revalidateAllAssociations(Long groupId) {
		Assert.notNull(groupId);

		UserGroup group = userGroupService.findGroupById(groupId);
		Assert.notNull(group, "Unable to find User Group");
		Set<User> updatedUsers = revalidateAssociations(userGroupService.findAllActiveAssociations(groupId), UserGroupInvitationType.CRITERIA_MODIFICATION);

		// Update search index
		groupSearchService.reindexGroup(groupId);

		List<Long> ids = CollectionUtilities.newListPropertyProjection(updatedUsers, "id");
		userIndexer.reindexById(ids);

		group.setValidationRequired(false);
		group.setUserAssociationsValidatedOn(DateUtilities.getCalendarNow());
		// need because we're not in a transaction
		userGroupService.saveOrUpdateUserGroup(group);
	}

	@Override
	public void revalidateAllAssociationsByUser(Long userId, Map<String, Object> modificationType) {
		Set<Long> userGroupAssociationsToEvaluate = Sets.newHashSet();
		for (Map.Entry<String, Object> modification : modificationType.entrySet()) {
			userGroupAssociationsToEvaluate.addAll(getUserGroupAssociationIdsToEvaluate(userId, modification));
		}

		revalidateUserGroupAssociations(userGroupAssociationsToEvaluate, UserGroupInvitationType.PROFILE_MODIFICATION);
	}

	private Collection<Long> getUserGroupAssociationIdsToEvaluate(Long userId, Map.Entry<String, Object> modification) {
		switch (modification.getKey()) {
			case ProfileModificationType.DOCUMENT:
				Long userGroupAssociationId =
					userGroupAssociationService.findUserGroupAssociationByUserIdAndGroupId(userId, (Long) modification.getValue());
				return (userGroupAssociationId == null) ? Collections.<Long>emptySet() : ImmutableSet.of(userGroupAssociationId);
			case ProfileModificationType.CERTIFICATION:
				return userGroupAssociationService.findAllPendingAssociationsWithCertification(userId, (Long) modification.getValue());
			case ProfileModificationType.LICENSE:
				return userGroupAssociationService.findAllPendingAssociationsWithLicense(userId, (Long) modification.getValue());
			case ProfileModificationType.ASSESSMENT:
				return userGroupAssociationService.findAllPendingAssociationsWithAssessment(userId, (Long) modification.getValue());
			case ProfileModificationType.INDUSTRY:
				return userGroupAssociationService.findAllPendingAssociationsWithIndustry(userId);
			case ProfileModificationType.INSURANCE_ADDED:
				return userGroupAssociationService.findAllPendingAssociationsWithInsurance(userId, (Long) modification.getValue());
			case ProfileModificationType.LANE_ASSOCIATION:
				return userGroupAssociationService.findAllPendingAssociationsWithLaneRequirement(userId);
			case ProfileModificationType.BACKGROUND_CHECK:
				return userGroupAssociationService.findAllPendingAssociationsWithBackgroundCheck(userId);
			case ProfileModificationType.DRUG_TEST:
				return userGroupAssociationService.findAllPendingAssociationsWithDrugTest(userId);
			case ProfileModificationType.WORKING_HOURS:
				return userGroupAssociationService.findAllPendingAssociationsWithWorkingHours(userId);
			case ProfileModificationType.MAX_TRAVEL_DISTANCE:
				return userGroupAssociationService.findAllPendingAssociationsWithLocationRequirements(userId);
			case ProfileModificationType.ADDRESS:
				return userGroupAssociationService.findAllPendingAssociationsWithLocationRequirements(userId);
			case ProfileModificationType.RATING:
				return userGroupAssociationService.findAllPendingAssociationsWithRating(userId);
			default:
				return Collections.emptyList();
		}
	}

	@Override
	public void revalidateAllAssociationsByUserAsync(Long userId, Map<String, Object> modificationType) {
		eventRouter.sendEvent(eventFactory.buildRevalidateGroupAssociationsEvent(userId, modificationType));
	}

	private void revalidateUserGroupAssociations(Collection<Long> associations, UserGroupInvitationType invitationType) {
		for (Long association : associations) {
			revalidateGroupAssociation(invitationType, association);
		}
	}

	private void revalidateGroupAssociation(UserGroupInvitationType invitationType, Long associationId) {
		if (associationId == null) {
			return;
		}

		UserUserGroupAssociation groupAssociation =
			userGroupAssociationService.findUserUserGroupAssociationById(associationId);

		if (groupAssociation == null) {
			return;
		}

		revalidateAssociation(groupAssociation, invitationType);
	}

	private class RevalidationResult {
		boolean update;
		List<Criterion> expiredCriteria;

		RevalidationResult(boolean update, List<Criterion> expiredCriteria) {
			this.update = update;
			this.expiredCriteria = expiredCriteria;
		}

		public boolean isUpdate() {
			return update;
		}

		public List<Criterion> getExpiredCriteria() {
			return expiredCriteria;
		}
	}

	private Set<User> revalidateAssociations(List<UserUserGroupAssociation> associations, UserGroupInvitationType invitationType) {
		Set<User> updatedUsers = Sets.newLinkedHashSet();
		List<UserExpiration> userExpirations = Lists.newArrayList();
		for (UserUserGroupAssociation association : associations) {
			if (!association.isOverrideMember()) {
				RevalidationResult revalidationResult = revalidateAssociation(association, invitationType);
				if (revalidationResult.getExpiredCriteria() != null && !revalidationResult.getExpiredCriteria().isEmpty()) {
					UserExpiration userExpiration = new UserExpiration();
					userExpiration.setUser(association.getUser());
					userExpiration.setCriteria(revalidationResult.getExpiredCriteria());
					userExpirations.add(userExpiration);
				}
				if (revalidationResult.isUpdate()) {
					updatedUsers.add(association.getUser());
				}
			}
		}
		if (!userExpirations.isEmpty()) {
			UserGroupExpiration userGroupExpiration = new UserGroupExpiration();
			userGroupExpiration.setUserExpirations(userExpirations);
			userGroupExpiration.setUserGroup(associations.get(0).getUserGroup());
			userNotificationService.onExpirationNotificationsForBuyer(userGroupExpiration);
		}
		return updatedUsers;
	}

	private RevalidationResult revalidateAssociation(UserUserGroupAssociation association, UserGroupInvitationType invitationType) {
		UserGroup userGroup = association.getUserGroup();
		Set<AssociationUpdateType> associationUpdates;
		List<Criterion> expiredCriteria = Lists.newArrayList();

		Eligibility eligibility = userGroupService.reValidateRequirementSets(userGroup.getId(), association.getUser().getId());

		// send weekly expiration warnings one month before and one month after expiration
		Calendar today = Calendar.getInstance();
		if (today.get(Calendar.DAY_OF_WEEK) == 1 && eligibility.isEligible()) {
			for (Criterion criterion: eligibility.getCriteria()) {
				if (criterion.getExpirationDate() == null) {
					continue;
				}
				long daysBetween =
					TimeUnit.MILLISECONDS.toDays(
						Math.abs(today.getTimeInMillis() - criterion.getExpirationDate().getTimeInMillis()));

				if (daysBetween < 30) {
					userNotificationService.onTalentPoolRequirementExpiration(
						criterion,
						criterion.getExpirationDate().compareTo(today) > 0 ? "will expire" : "expired");
				} else if (criterion.getExpirationDate().compareTo(today) > 0) {
					continue;
				}

				if (criterion.isWarnWhenExpired()) {
					expiredCriteria.add(criterion);
				}
			}
		}

		associationUpdates = getAssociationUpdates(association, eligibility);
		userGroupAssociationValidationUpdateService.updateAssociation(association, associationUpdates, invitationType, 0);

		boolean update = CollectionUtils.isNotEmpty(associationUpdates);

		if (update) {
			// needed here because we are not in a transaction
			association = userGroupAssociationService.findUserUserGroupAssociationById(association.getId());
			summaryService.saveUserGroupAssociationHistorySummary(association);
			eventRouter.sendEvent(eventFactory.buildUserGroupAssociationUpdateEvent(association));
		}
		return new RevalidationResult(update, expiredCriteria);
	}

	private Set<AssociationUpdateType> getAssociationUpdates(UserUserGroupAssociation association, Eligibility eligibility) {
		if (eligibility.isEligible()) {
			return userGroupAssociationValidationUpdateService.getMetRequirementsAssociationUpdateTypes(association);
		} else {
			return userGroupAssociationValidationUpdateService.getFailedRequirementsAssociationUpdateTypes(association);
		}
	}
}
