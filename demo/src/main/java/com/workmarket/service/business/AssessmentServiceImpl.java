package com.workmarket.service.business;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.dao.UserDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.dao.assessment.AbstractItemDAO;
import com.workmarket.dao.assessment.AssessmentGroupAssociationDAO;
import com.workmarket.dao.assessment.AssessmentUserAssociationDAO;
import com.workmarket.dao.assessment.AttemptDAO;
import com.workmarket.dao.assessment.AttemptReportDAO;
import com.workmarket.dao.assessment.AttemptResponseDAO;
import com.workmarket.dao.assessment.ChoiceDAO;
import com.workmarket.dao.assessment.ManagedAssessmentDAO;
import com.workmarket.dao.assessment.SurveyAssessmentDAO;
import com.workmarket.dao.assessment.WorkAssessmentAssociationDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.notification.AssessmentNotificationPreferenceDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.dao.requirement.TestRequirementDAO;
import com.workmarket.dao.skill.SkillDAO;
import com.workmarket.data.report.assessment.AttemptReportPagination;
import com.workmarket.data.report.assessment.AttemptResponseAssetReportPagination;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.assessment.AbstractItemWithChoices;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.model.assessment.AssessmentStatistics;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.AssessmentUserAssociationPagination;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.AttemptResponse;
import com.workmarket.domains.model.assessment.AttemptStatusType;
import com.workmarket.domains.model.assessment.Choice;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;
import com.workmarket.domains.model.assessment.WorkScopedAttempt;
import com.workmarket.domains.model.assessment.item.MultipleChoiceItem;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.notification.AssessmentNotificationPreference;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.model.InternalPrivateNetworkAdmitted;
import com.workmarket.domains.velvetrope.rope.InternalPrivateNetworkAdmittedRope;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.dto.AggregatesDTO;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.service.business.assessment.RecommendedAssessmentCache;
import com.workmarket.service.business.dto.AssessmentChoiceDTO;
import com.workmarket.service.business.dto.AssessmentDTO;
import com.workmarket.service.business.dto.AssessmentItemDTO;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.AttemptResponseDTO;
import com.workmarket.service.business.dto.NotificationPreferenceDTO;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.assessment.AssessmentAttemptItemsNotGradedException;
import com.workmarket.service.exception.assessment.AssessmentAttemptLimitExceededException;
import com.workmarket.service.exception.assessment.AssessmentAttemptTimedOutException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.ProjectionUtilities;
import com.workmarket.velvetrope.Doorman;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AssessmentServiceImpl implements AssessmentService {

	private static final Log logger = LogFactory.getLog(AssessmentServiceImpl.class);
	@Autowired private AbstractAssessmentDAO assessmentDAO;
	@Autowired private AbstractItemDAO abstractItemDAO;
	@Autowired private AssetService assetService;
	@Autowired private ChoiceDAO choiceDAO;
	@Autowired private AttemptDAO attemptDAO;
	@Autowired private AttemptResponseDAO attemptResponseDAO;
	@Autowired private AttemptReportDAO attemptReportDAO;
	@Autowired private IndustryDAO industryDAO;
	@Autowired private SkillDAO skillDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private AssessmentUserAssociationDAO assessmentUserAssociationDAO;
	@Autowired private AssessmentNotificationPreferenceDAO assessmentNotificationPreferenceDAO;
	@Autowired private ManagedAssessmentDAO assessmentUserInvitationDAO;
	@Autowired private UserRoleService userRoleService;
	@Autowired private ProfileDAO profileDAO;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private AuthorizationService authorizationService;
	@Autowired private SkillService skillService;
	@Autowired private RequestService requestService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private WorkService workService;
	@Autowired private UserIndexer userSearchIndexerHelper;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private WorkAssessmentAssociationDAO workAssessmentAssociationDAO;
	@Autowired private RecommendedAssessmentCache recommendedAssessmentCache;
	@Autowired private BaseWorkDAO baseWorkDAO;
	@Autowired private IndustryService industryService;
	@Autowired private SurveyAssessmentDAO surveyAssessmentDAO;
	@Autowired private UserService userService;
	@Autowired private LaneService laneService;
	@Autowired protected AssessmentGroupAssociationDAO assessmentGroupAssociationDAO;
	@Autowired protected TestRequirementDAO testRequirementDAO;
	@Autowired @Qualifier("internalPrivateNetworkDoorman") private Doorman internalPrivateNetworkDoorman;

	@Override
	public AbstractAssessment findAssessment(Long assessmentId) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		if (assessment != null) {
			abstractItemDAO.initialize(assessment.getItems());
			skillDAO.initialize(assessment.getSkills());
		}
		return assessment;
}

	@Override
	public Integer countAssessmentsByCompanyCreatedSince(long companyId, Calendar dateFrom) {
		return assessmentDAO.countAssessmentsByCompanyCreatedSince(companyId, dateFrom);
	}

	@Override
	public Integer countAssessmentsByCompany(long companyId) {
		return assessmentDAO.countAssessmentsByCompany(companyId);
	}

	@Override
	public Map<Long, String> findActiveSurveysByCompany(Long companyId) {
		return assessmentDAO.findSurveysByCompany(companyId);
	}

	@Override
	public AssessmentPagination findAssessmentsByCompany(Long companyId, AssessmentPagination pagination) {
		return assessmentDAO.findByCompany(companyId, pagination);
	}

	@Override
	public AbstractAssessment saveOrUpdateAssessment(final Long userId, final AssessmentDTO assessmentDTO) {
		User user = userDAO.get(userId);

		AbstractAssessment assessment;
		if (assessmentDTO.getId() == null) {

			Assert.notNull(assessmentDTO.getType(), "Type must be set.");

			assessment = AbstractAssessment.newInstance(assessmentDTO.getType());
			assessment.setUser(user);
			assessment.setCompany(user.getCompany());
		} else {
			assessment = assessmentDAO.get(assessmentDTO.getId());
			if (assessment == null) {
				return null;
			}
		}

		assessment.setAssessmentStatusType(new AssessmentStatusType(assessmentDTO.getAssessmentStatusTypeCode()));

		Assert.notNull(assessmentDTO.getIndustryId(), "Industry must be set.");

		Industry industry = industryDAO.findIndustryById(assessmentDTO.getIndustryId());
		if (industry == null) {
			industry = industryService.getDefaultIndustryForProfile(user.getProfile().getId());
		}
		assessment.setIndustry(industry);

		BeanUtilities.copyProperties(assessment, assessmentDTO);
		BeanUtilities.copyProperties(assessment.getConfiguration(), assessmentDTO);

		assessment.getConfiguration().setFeatured(assessmentDTO.isFeatured());

		assessmentDAO.saveOrUpdate(assessment);

		return assessment;
	}

	@Override
	public AbstractAssessment saveOrUpdateAssessment(AbstractAssessment assessment) {
		assessmentDAO.saveOrUpdate(assessment);
		return assessment;
	}

	@Override
	public void updateAssessmentStatus(Long assessmentId, String statusTypeCode) {
		Assert.notNull(assessmentId, "Assessment ID can't be null");
		Assert.notNull(statusTypeCode, "Status code can't be null");

		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment);

		assessment.setAssessmentStatusType(new AssessmentStatusType(statusTypeCode));
	}

	private void addSkillToAssessment(Long assessmentId, Long skillId) {
		Assert.notNull(assessmentId);
		Assert.notNull(skillId);

		AbstractAssessment assessment = findAssessment(assessmentId);
		Assert.notNull(assessment, "Unable to find Assessment Id " + assessmentId);

		assessment.getSkills().add(skillService.findSkillById(skillId));
	}

	@Override
	public void setSkillsForAssessment(Long assessmentId, Long[] skillIds) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment);

		assessment.getSkills().clear();
		for (Long sid : skillIds) {
			addSkillToAssessment(assessmentId, sid);
		}
	}

	@Override
	public List<AssessmentNotificationPreference> setNotificationPreferencesForAssessment(Long assessmentId, NotificationPreferenceDTO[] dtos) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment);

		for (AssessmentNotificationPreference p : assessment.getConfiguration().getNotifications()) {
			p.setDeleted(true);
		}

		Set<AssessmentNotificationPreference> prefs = Sets.newHashSet();
		for (NotificationPreferenceDTO dto : dtos) {
			prefs.add(addOrUpdateNotificationPreferenceForAssessment(assessmentId, dto));
		}
		assessment.getConfiguration().setNotifications(prefs);

		return Lists.newArrayList(assessment.getConfiguration().getNotifications());
	}

	private AssessmentNotificationPreference addOrUpdateNotificationPreferenceForAssessment(Long assessmentId, NotificationPreferenceDTO dto) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment);
		Assert.notNull(dto);

		AssessmentNotificationPreference pref = assessmentNotificationPreferenceDAO.findByAssessmentAndNotificationType(assessmentId, dto.getNotificationTypeCode());
		if (pref == null) {
			pref = new AssessmentNotificationPreference(dto.getNotificationTypeCode());
			pref.setAssessment(assessment);
		}
		pref.setDeleted(false);
		if (dto.getDays() != null) {
			pref.setDays(dto.getDays());
		}

		assessmentNotificationPreferenceDAO.saveOrUpdate(pref);

		return pref;
	}

	@Override
	public void setNotificationRecipientsForAssessment(Long assessmentId, Long[] userIds) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment);

		assessment.getConfiguration().getNotificationRecipients().clear();
		for (Long uid : userIds) {
			assessment.getConfiguration().getNotificationRecipients().add(userDAO.get(uid));
		}
	}

	@Override
	public List<AbstractItem> saveOrUpdateItemsInAssessment(Long assessmentId, AssessmentItemDTO[] itemDTOs) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment, "Unable to find Assessment Id " + assessmentId);

		Map<Long, AbstractItem> lookupOld = new HashMap<>();
		for (AbstractItem q : assessment.getItems()) {
			lookupOld.put(q.getId(), q);
		}
		Map<Long, Boolean> lookupNew = new HashMap<>();
		for (AssessmentItemDTO t : itemDTOs) {
			if (t.getItemId() != null) {
				lookupNew.put(t.getItemId(), Boolean.TRUE);
			}
		}

		// Remove any old questions not included in the new update
		for (Long oldId : lookupOld.keySet()) {
			if (lookupNew.containsKey(oldId)) {
				continue;
			}
			lookupOld.get(oldId).setDeleted(true);
		}

		// Reorder questions, combining both new and old
		List<AbstractItem> items = new ArrayList<>();
		for (AssessmentItemDTO itemDTO : itemDTOs) {
			AbstractItem item = lookupOld.containsKey(itemDTO.getItemId()) ?
				lookupOld.get(itemDTO.getItemId()) :
				AbstractItem.newInstance(itemDTO.getType());

			Assert.notNull(item);

			BeanUtilities.copyProperties(item, itemDTO);
			items.add(item);
		}
		assessment.setItems(items);

		return assessment.getItems();
	}

	@Override
	public AbstractItem addOrUpdateItemInAssessment(Long assessmentId, AssessmentItemDTO itemDTO) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment, "Unable to find Assessment Id " + assessmentId);

		AbstractItem item;
		if (itemDTO.getItemId() != null) {
			item = abstractItemDAO.get(itemDTO.getItemId());

			// If changing the type, effectively delete the original and swap in the new item.

			if (itemDTO.getType() != null && !itemDTO.getType().equals(item.getType())) {
				int index = assessment.getItems().indexOf(item);
				item.setDeleted(true);
				item = AbstractItem.newInstance(itemDTO.getType());
				assessment.getItems().add(index, item);
			}
		} else {
			item = AbstractItem.newInstance(itemDTO.getType());
			assessment.getItems().add(item);
		}

		Assert.notNull(item);

		BeanUtilities.copyProperties(item, itemDTO);

		abstractItemDAO.saveOrUpdate(item);

		return item;
	}

	@Override
	public void removeItemFromAssessment(Long assessmentId, Long itemId) {
		Assert.notNull(assessmentId);
		Assert.notNull(itemId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		AbstractItem item = abstractItemDAO.get(itemId);
		Assert.notNull(assessment);
		Assert.notNull(item);

		item.setDeleted(true);

		// mark orphaned in-progress attempt responses deleted
		List<AttemptResponse> attemptResponses = attemptResponseDAO.findResponsesByItemAndStatus(itemId, AttemptStatusType.INPROGRESS);
		for (AttemptResponse attemptResponse : attemptResponses) {
			attemptResponse.setDeleted(true);
		}
	}

	@Override
	public List<AbstractItem> reorderItemsInAssessment(Long assessmentId, Long[] itemIds) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment);

		assessment.setItems(new ArrayList<AbstractItem>());
		for (Long id : itemIds) {
			assessment.getItems().add(abstractItemDAO.get(id));
		}
		return assessment.getItems();
	}

	@Override
	public List<Choice> saveOrUpdateChoicesInItem(Long itemId, AssessmentChoiceDTO[] choiceDTOs) {
		Assert.notNull(itemId);
		AbstractItem item = abstractItemDAO.get(itemId);
		Assert.notNull(item);
		Assert.isInstanceOf(AbstractItemWithChoices.class, item);

		AbstractItemWithChoices itemWithChoices = (AbstractItemWithChoices) item;

		Map<Long, Choice> lookupOld = new HashMap<>();
		for (Choice a : itemWithChoices.getChoices()) {
			lookupOld.put(a.getId(), a);
		}

		Map<Long, Boolean> lookupNew = new HashMap<>();
		for (AssessmentChoiceDTO a : choiceDTOs) {
			if (a.getChoiceId() != null) {
				lookupNew.put(a.getChoiceId(), Boolean.TRUE);
			}
		}

		// Remove any old choices not included in the new update

		for (Long oldId : lookupOld.keySet()) {
			if (lookupNew.containsKey(oldId)) {
				continue;
			}

			lookupOld.get(oldId).setDeleted(true);
		}

		// Reorder answers, combining both new and old

		List<Choice> choices = new ArrayList<>();
		for (AssessmentChoiceDTO choiceDTO : choiceDTOs) {
			Choice choice = (lookupOld.containsKey(choiceDTO.getChoiceId())) ?
				lookupOld.get(choiceDTO.getChoiceId()) :
				new Choice();
			BeanUtilities.copyProperties(choice, choiceDTO);
			choices.add(choice);
		}
		itemWithChoices.setChoices(choices);

		for (Choice choice : itemWithChoices.getChoices()) {
			choiceDAO.saveOrUpdate(choice);
		}

		return itemWithChoices.getChoices();
	}

	@Override
	public Choice addOrUpdateChoiceInItem(Long itemId, AssessmentChoiceDTO choiceDTO) {
		Assert.notNull(itemId);
		AbstractItem item = abstractItemDAO.get(itemId);
		Assert.notNull(item);
		Assert.isInstanceOf(AbstractItemWithChoices.class, item);

		AbstractItemWithChoices itemWithChoices = (AbstractItemWithChoices) item;

		Choice choice = new Choice();
		BeanUtilities.copyProperties(choice, choiceDTO);
		itemWithChoices.getChoices().add(choice);

		choiceDAO.saveOrUpdate(choice);

		return choice;
	}

	@Override
	public List<Choice> reorderChoicesInItem(Long itemId, Long[] choiceIds) {
		Assert.notNull(itemId);
		AbstractItem item = abstractItemDAO.get(itemId);
		Assert.notNull(item);
		Assert.isInstanceOf(AbstractItemWithChoices.class, item);

		AbstractItemWithChoices itemWithChoices = (AbstractItemWithChoices) item;

		itemWithChoices.setChoices(new ArrayList<Choice>());
		for (Long id : choiceIds) {
			itemWithChoices.getChoices().add(choiceDAO.get(id));
		}

		return itemWithChoices.getChoices();
	}


	private AssessmentUserAssociation findAssessmentUserAssociation(Long userId, Long assessmentId, Attempt attempt) {
		Assert.notNull(attempt);
		Assert.notNull(assessmentId);
		Assert.notNull(userId);

		return (attempt instanceof WorkScopedAttempt) ?
			assessmentUserAssociationDAO.findByUserAssessmentAndWork(userId, assessmentId, ((WorkScopedAttempt) attempt).getWork().getId()) :
			findAssessmentUserAssociationByUserAndAssessment(userId, assessmentId);
	}

	@Override
	public AssessmentUserAssociation findAssessmentUserAssociationByUserAndAssessment(Long userId, Long assessmentId) {
		Assert.notNull(assessmentId);
		Assert.notNull(userId);
		return assessmentUserAssociationDAO.findByUserAndAssessment(userId, assessmentId);
	}

	@Override
	public AssessmentUserAssociationPagination findAssessmentUserAssociationsByUser(Long userId, AssessmentUserAssociationPagination pagination) {
		Assert.notNull(userId);
		return assessmentUserAssociationDAO.findByUser(userId, pagination);
	}

	@Override
	public AssessmentUserAssociationPagination findAssessmentUserAssociationsByUsers(Set<Long> userIds, AssessmentUserAssociationPagination pagination) {
		Assert.notEmpty(userIds);
		return assessmentUserAssociationDAO.findByUsers(userIds, pagination);
	}

	@Override
	public List<String> getActiveAssessmentForGroup(Long companyId, Long assessmentId) {
		Assert.notNull(assessmentId);
		Assert.notNull(companyId);
		return assessmentDAO.getActiveAssessmentForGroup(companyId, assessmentId);
	}

	@Override
	public List<String> getActiveAssessmentForAssignment(Long companyId, Long assessmentId) {
		Assert.notNull(assessmentId);
		Assert.notNull(companyId);
		return assessmentDAO.getActiveAssessmentForAssignment(companyId, assessmentId);
	}

	@Override
	public List<String> getActiveAssessmentForReqSet(Long companyId, Long assessmentId) {
		Assert.notNull(assessmentId);
		Assert.notNull(companyId);
		return assessmentDAO.getActiveAssessmentForReqSet(companyId, assessmentId);
	}

	@Override
	public boolean hasUserPassedAssessment(Long userId, Long assessmentId) {
		AssessmentUserAssociation association = findAssessmentUserAssociationByUserAndAssessment(userId, assessmentId);
		return (association != null ? association.getPassedFlag() : false);
	}

	public List<Attempt> findAttemptsForAssessmentByUser(Long assessmentId, Long userId) {
		Assert.notNull(assessmentId);
		Assert.notNull(userId);

		AssessmentUserAssociation association = findAssessmentUserAssociationByUserAndAssessment(userId, assessmentId);

		return (association == null) ? null : association.getAttempts();
	}

	@Override
	public Attempt findLatestAttemptForAssessmentByUser(Long assessmentId, Long userId) {
		Assert.notNull(assessmentId);
		Assert.notNull(userId);

		return attemptDAO.findLatestForAssessmentByUser(assessmentId, userId);
	}

	@Override
	public Attempt findLatestAttemptForAssessmentByUserScopedToWork(Long assessmentId, Long userId, Long workId) {
		Assert.notNull(assessmentId);
		Assert.notNull(userId);
		Assert.notNull(workId);

		return attemptDAO.findLatestForAssessmentByUserAndWork(assessmentId, userId, workId);
	}

	@Override
	public boolean isAttemptAllowedForAssessmentByUser(Long assessmentId, Long userId) {
		AssessmentUserAssociation association = findAssessmentUserAssociationByUserAndAssessment(userId, assessmentId);
		return association == null || isAttemptAllowedForAssessmentByAssociation(association);
	}

	@Override
	public boolean isAttemptAllowedForAssessmentByUserScopedToWork(Long assessmentId, Long userId, Long workId) {
		return null == attemptDAO.findLatestForAssessmentByUserAndWork(assessmentId, userId, workId);
	}

	@Override
	public Attempt saveAttemptForAssessment(Long userId, Long assessmentId) throws AssessmentAttemptLimitExceededException {
		return saveAttemptForAssessment(userId, assessmentId, new Attempt());
	}

	@Override
	public Attempt saveAttemptForAssessmentScopedToWork(Long userId, Long assessmentId, Long workId, Long behalfOfId) throws AssessmentAttemptLimitExceededException {
		Assert.notNull(workId);
		return saveAttemptForAssessment(userId, assessmentId, new WorkScopedAttempt(workDAO.get(workId), userDAO.get(behalfOfId)));
	}

	@Override
	public Attempt completeAttemptForAssessment(Long attemptId) {
		Assert.notNull(attemptId);
		Attempt attempt = attemptDAO.findById(attemptId);
		Assert.notNull(attempt, "Must start an attempt before completing one.");

		if (attempt.isComplete()) {
			logger.debug(String.format("Attempt already marked as complete [%d]", attempt.getId()));
			return attempt;
		}

		AbstractAssessment assessment = attempt.getAssessmentUserAssociation().getAssessment();

		Assert.state(assessment.getAssessmentStatusType().isActive());

		attempt.setCompletedOn(Calendar.getInstance());

		if (assessment.getType().equals(AbstractAssessment.SURVEY_ASSESSMENT_TYPE)) {
			attempt.setStatus(new AttemptStatusType(AttemptStatusType.COMPLETE));
		} else if (requiresManualGrading(assessment.getId())) {
			attempt.setStatus(new AttemptStatusType(AttemptStatusType.GRADE_PENDING));
		} else {
			attempt = gradeAttemptForAssessment(attempt);
		}

		attempt.getAssessmentUserAssociation().snapshotAttempt(attempt);

		// revalidate the group associations
		Map<String, Object> params = Maps.newHashMap();
		params.put(ProfileModificationType.ASSESSMENT, attempt.getAssessment().getId());
		userGroupValidationService.revalidateAllAssociationsByUserAsync(attempt.getUser().getId(), params);

		userNotificationService.onAssessmentCompleted(attempt);
		userNotificationService.onAssessmentGraded(attempt);
		userSearchIndexerHelper.reindexById(attempt.getAssessmentUserAssociation().getUser().getId());

		if (attempt instanceof WorkScopedAttempt) {
			notifyWorkScopeAttemptComplete((WorkScopedAttempt) attempt);
		}

		return attempt;
	}

	private Attempt notifyWorkScopeAttemptComplete(WorkScopedAttempt attempt) {
		userNotificationService.onAssignmentSurveyCompleted(attempt);
		return attempt;
	}

	@Override
	public Attempt completeAttemptForAssessment(Long userId, Long assessmentId) {
		Attempt attempt = findLatestAttemptForAssessmentByUser(assessmentId, userId);
		Assert.notNull(attempt, "Must start an attempt before completing one.");
		return completeAttemptForAssessment(attempt.getId());
	}

	@Override
	public Attempt completeAttemptForAssessmentScopedToWork(Long userId, Long assessmentId, Long workId) {
		Attempt attempt = attemptDAO.findLatestForAssessmentByUserAndWork(assessmentId, userId, workId);
		Assert.notNull(attempt, "Must start an attempt before completing one.");
		return completeAttemptForAssessment(attempt.getId());
	}

	@Override
	public List<AttemptResponse> submitResponsesForItem(Long attemptId, Long itemId, AttemptResponseDTO[] dtos) throws AssessmentAttemptTimedOutException, HostServiceException, AssetTransformationException, IOException {
		Assert.notNull(attemptId);
		Assert.notNull(itemId);
		Assert.notNull(dtos);

		Attempt attempt = attemptDAO.get(attemptId);
		AbstractItem item = abstractItemDAO.get(itemId);

		Assert.notNull(attempt, "Must start an attempt before submitting responses.");
		Assert.notNull(item);
		Assert.state(attempt.getAssessmentUserAssociation().getAssessment().getAssessmentStatusType().isActive());

		// Is the attempt still available for response?
		if (attempt.getAssessmentUserAssociation().getAssessment().getConfiguration().isTimed() && attempt.isComplete()) {
			throw new AssessmentAttemptTimedOutException();
		}
		Assert.state(!attempt.isComplete());

		if (dtos.length > 1) {
			Assert.isInstanceOf(MultipleChoiceItem.class, item, "Item accepts only a single response.");
		}

		List<AttemptResponse> oldResponses = attemptResponseDAO.findForItemInAttempt(attemptId, itemId);
		for (AttemptResponse r : oldResponses) {
			r.setDeleted(true);
		}

		List<AttemptResponse> responses = Lists.newArrayList();
		for (AttemptResponseDTO dto : dtos) {
			AttemptResponse r = new AttemptResponse();
			r.setAttempt(attempt);
			r.setItem(item);
			r.setValue(dto.getValue());
			if (dto.getChoiceId() != null) {
				Assert.isInstanceOf(AbstractItemWithChoices.class, item, "Item does not have any choices.");
				r.setChoice(choiceDAO.get(dto.getChoiceId()));
			}
			attemptResponseDAO.saveOrUpdate(r);

			if (!dto.getAssets().isEmpty()) {
				for (AssetDTO a : dto.getAssets()) {
					assetManagementService.storeAssetForAttemptResponse(a, r.getId());
				}
			}

			if (!dto.getUploads().isEmpty()) {
				for (UploadDTO u : dto.getUploads()) {
					assetManagementService.addUploadToAttemptResponse(u, r.getId());
				}
			}

			attempt.getResponses().add(r);
			responses.add(r);
		}

		// Do all of the items have a response?
		// This is a bit naive of an implementation as it assumes that all of the items can have responses
		// (e.g. this is not true for "divider" type items)
		// In the case of a survey - this matters - we simply want to know if all the questions are answered.
		Set<Long> itemIdsWithResponse = Sets.newHashSet();
		for (AttemptResponse r : attempt.getResponses()) {
			itemIdsWithResponse.add(r.getId());
		}
		attempt.setAllQuestionsRespondedTo(attempt.getAssessment().getItems().size() == itemIdsWithResponse.size());

		return responses;
	}

	@Override
	public List<AttemptResponse> submitResponsesForItemInAssessment(Long userId, Long assessmentId, Long itemId, AttemptResponseDTO[] responses) throws AssessmentAttemptTimedOutException, HostServiceException, AssetTransformationException, IOException {
		Attempt attempt = findLatestAttemptForAssessmentByUser(assessmentId, userId);
		Assert.notNull(attempt, "Must start an attempt before submitting responses.");
		return submitResponsesForItem(attempt.getId(), itemId, responses);
	}

	@Override
	public List<AttemptResponse> submitResponsesForItemInAssessmentScopedToWork(Long userId, Long assessmentId, Long itemId, AttemptResponseDTO[] responses, Long workId) throws AssessmentAttemptTimedOutException, HostServiceException, AssetTransformationException, IOException {
		Attempt attempt = attemptDAO.findLatestForAssessmentByUserAndWork(assessmentId, userId, workId);
		Assert.notNull(attempt, "Must start an attempt before submitting responses.");
		return submitResponsesForItem(attempt.getId(), itemId, responses);
	}

	@Override
	public AssessmentStatistics getAssessmentStatistics(Long assessmentId) {
		Assert.notNull(assessmentId);
		return assessmentDAO.getAssessmentStatistics(assessmentId);
	}

	@Override
	public AttemptReportPagination generateReportForAssessment(Long assessmentId, AttemptReportPagination pagination) {
		Assert.notNull(assessmentId);
		return attemptReportDAO.generateReportForAssessment(assessmentId, pagination);
	}

	@Override
	public AttemptResponseAssetReportPagination findAssessmentAttemptResponseAssets(AttemptResponseAssetReportPagination pagination) {
		return assetService.getAssessmentAttemptResponseAssets(pagination);
	}

	private boolean requiresManualGrading(Long assessmentId) {
		Assert.notNull(assessmentId);
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment);

		// another instance of hibernate giving nulls back on filtered collection elements
		for (AbstractItem item : assessment.getItems()) {
			if (item != null && item.isManuallyGraded() && item.isGraded()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void gradeResponsesForItemInAttempt(Long attemptId, Long itemId, boolean correct) {
		for (AttemptResponse r : attemptResponseDAO.findForItemInAttempt(attemptId, itemId)) {
			r.setCorrect(correct);
			r.setGradedOn(Calendar.getInstance());
			r.setGrader(authenticationService.getCurrentUser());

			attemptResponseDAO.saveOrUpdate(r);
		}
	}

	@Override
	public Attempt gradeAttemptForAssessment(Long userId, Long assessmentId) {
		// TODO Authorization?
		// TODO Do we care about who committed the grade?
		Attempt attempt = gradeAttemptForAssessment(findLatestAttemptForAssessmentByUser(assessmentId, userId));
		attempt.getAssessmentUserAssociation().snapshotAttempt(attempt);

		// revalidate the group associations
		Map<String, Object> params = Maps.newHashMap();
		params.put(ProfileModificationType.ASSESSMENT, attempt.getAssessment().getId());
		userGroupValidationService.revalidateAllAssociationsByUserAsync(attempt.getUser().getId(), params);

		userNotificationService.onAssessmentGraded(attempt);
		return attempt;
	}

	@Override
	public List<RequestContext> getRequestContext(Long assessmentId) {
		User currentUser = authenticationService.getCurrentUser();
		AbstractAssessment assessment = assessmentDAO.get(assessmentId);

		List<RequestContext> contexts = authorizationService.getEntityRequestContexts(currentUser, assessment.getUser(), assessment.getCompany());

		if (requestService.userHasInvitationToAssessment(currentUser.getId(), assessmentId)) {
			contexts.add(RequestContext.INVITED);
		}
		if (workService.isUserActiveResourceForWorkWithAssessment(currentUser.getId(), assessmentId)) {
			contexts.add(RequestContext.RESOURCE);
		}
		if (contexts.contains(RequestContext.OWNER) ||
			(contexts.contains(RequestContext.COMPANY_OWNED) &&
				userRoleService.userHasPermission(currentUser, Permission.MANAGE_ASSESSMENTS))
			) {
			contexts.add(RequestContext.ADMIN);
		}
		return contexts;
	}

	@Override
	public AssessmentUserPagination findAllAssessmentUsers(Long assessmentId, AssessmentUserPagination pagination) {
		Assert.notNull(assessmentId);
		Assert.notNull(pagination);
		AbstractAssessment assessment = findAssessment(assessmentId);
		Assert.notNull(assessment);
		return assessmentUserAssociationDAO.findAllAssessmentUsers(assessment.getCompany().getId(), assessmentId, pagination);
	}

	@Override
	public AssessmentUserPagination findLatestAssessmentUserAttempts(Long assessmentId, AssessmentUserPagination pagination) {
		Assert.notNull(assessmentId);
		Assert.notNull(pagination);
		return assessmentUserAssociationDAO.findLatestAssessmentUserAttempts(assessmentId, pagination);
	}

	@Override
	public AggregatesDTO countAssessmentUsers(Long assessmentId, AssessmentUserPagination pagination) {
		Assert.notNull(assessmentId);
		Assert.notNull(pagination);
		AbstractAssessment assessment = findAssessment(assessmentId);
		Assert.notNull(assessment);
		return assessmentUserAssociationDAO.countAssessmentUsers(assessment.getCompany().getId(), assessmentId, pagination);
	}

	@Override
	public ManagedAssessmentPagination findAssessmentsForUser(Long userId, ManagedAssessmentPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		Profile profile = profileDAO.findByUser(userId);
		Assert.notNull(profile);
		User user = userService.findUserById(userId);
		List<Long> exclusiveCompanyIds = Collections.EMPTY_LIST;
		if (user.isUserExclusive()) {
			exclusiveCompanyIds = laneService.findAllCompaniesWhereUserIsResource(userId, LaneType.LANE_2);
		} else if (isUserPrivateEmployee(user)) {
			exclusiveCompanyIds = ImmutableList.of(user.getCompany().getId());
		}

		List<Long> blockingCompanyIds = userService.findBlockedOrBlockedByCompanyIdsByUserId(userId);
		return assessmentUserInvitationDAO.findAssessmentsForUser(
			profile.getUser().getCompany().getId(),
			profile.getUser().getId(),
			profile.getId(),
			exclusiveCompanyIds,
			blockingCompanyIds,
			pagination
		);
	}

	@Override
	public ManagedAssessmentPagination findRecommendedAssessmentsForUser(Long userId) {
		Optional<List<Long>> assessmentIds = recommendedAssessmentCache.get(userId);

		ManagedAssessmentPagination pagination;
		if (assessmentIds.isPresent()) {
			pagination = findAssessmentsForUser(
				userId, ManagedAssessmentPagination.getRecommendedAssessmentPagination(false).setIdFilter(assessmentIds.get())
			);
		} else {
			pagination = findAssessmentsForUser(userId, ManagedAssessmentPagination.getRecommendedAssessmentPagination(true));
			recommendedAssessmentCache.set(userId, pagination);
		}
		Collections.shuffle(pagination.getResults());
		return pagination;
	}

	@Override
	public int reassignAssessmentsOwnership(Long fromId, Long toId) {
		int assessmentsUpdated = assessmentDAO.updateAssessmentOwner(toId, assessmentDAO.findAssessmentIdsByUser(fromId));
		logger.debug("Assessments updated: " + assessmentsUpdated);
		return assessmentsUpdated;
	}

	@Override
	public List<AbstractAssessment> findAllTestsByCompanyId(Long companyId) {
		return assessmentDAO.findAllBy(
			"company.id", companyId,
			"assessmentStatusType.code", AssessmentStatusType.ACTIVE
		);
	}

	@Override
	public void addUsersToTest(Long userId, String userNumber, Long testId, Set<String> resourceUserNumbers) {
		logger.debug("pushing " + userNumber + "'s cart to test");
		try {
			eventRouter.sendEvent(
				eventFactory.buildInviteUsersToAssessmentEvent(userId, resourceUserNumbers, testId)
			);
		} catch (Exception e) {
			logger.debug("fail adding user to test:", e);
		}

	}

	@Override
	public List<WorkAssessmentAssociation> findAllWorkAssessmentAssociationByWork(long workId) {
		return workAssessmentAssociationDAO.findAllByWork(workId);
	}

	@Override
	public List<Attempt> findLatestAttemptByUserAndWork(long userId, long workId) {
		return attemptDAO.findLatestByUserAndWork(userId, workId);
	}

	private boolean isAttemptAllowedForAssessmentByAssociation(AssessmentUserAssociation association) {
		return isAttemptAllowedForAssessmentByAssociation(association, association.getAttempts().size());
	}

	private boolean isAttemptAllowedForAssessmentByAssociation(
		AssessmentUserAssociation association,
		Integer attempts) {

		return isAttemptAllowed(
			attempts,
			association.getAssessment().getConfiguration().getRetakesAllowed(),
			association.getPassedFlag(),
			association.getStatus());
	}

	private boolean isAttemptAllowed(
		Integer assessmentAttempts,
		Integer assessmentReattempts,
		Boolean assessmentPassedFlag,
		AttemptStatusType assessmentStatus) {

		if (MoreObjects.firstNonNull(assessmentAttempts, 0) == 0) {
			return true;
		}

		if (assessmentReattempts == null || assessmentPassedFlag == null) {
			return true;
		}

		return 0 != assessmentReattempts
			&& !assessmentPassedFlag
			&& !assessmentStatus.isInProgress()
			&& !assessmentStatus.isGradePending()
			&& assessmentAttempts < assessmentReattempts + 1; // include the initial attempt
	}

	private Attempt saveAttemptForAssessment(Long userId, Long assessmentId, Attempt attempt) throws AssessmentAttemptLimitExceededException {
		Assert.notNull(assessmentId);
		Assert.notNull(userId);

		AbstractAssessment assessment = assessmentDAO.get(assessmentId);
		Assert.notNull(assessment);
		Assert.state(assessment.getAssessmentStatusType().isActive());

		AssessmentUserAssociation association = findAssessmentUserAssociation(userId, assessmentId, attempt);
		if (association == null) {
			association = new AssessmentUserAssociation();
			association.setUser(userDAO.get(userId));
			association.setAssessment(assessment);
			association.setReattemptAllowedFlag(false);
			assessmentUserAssociationDAO.saveOrUpdate(association);
		}

		if (attempt instanceof WorkScopedAttempt) {
			Long assessmentUserId = userId;
			WorkScopedAttempt workScopedAttempt = (WorkScopedAttempt) attempt;
			if (workScopedAttempt.getBehalfOf() != null) {
				assessmentUserId = workScopedAttempt.getBehalfOf().getId();
			}
			if (!isAttemptAllowedForAssessmentByUserScopedToWork(assessmentId, assessmentUserId, workScopedAttempt.getWork().getId())) {
				throw new AssessmentAttemptLimitExceededException();
			}
		} else {
			if (!isAttemptAllowedForAssessmentByUser(assessmentId, userId) && !association.getReattemptAllowedFlag()) {
				throw new AssessmentAttemptLimitExceededException();
			}
		}

		if (association.getAttempts() == null) {
			association.setAttempts(new ArrayList<Attempt>());
		}

		attempt.setAssessmentUserAssociation(association);
		association.getAttempts().add(attempt);
		attemptDAO.saveOrUpdate(attempt);

		association.snapshotAttempt(attempt);

		if (association.getAssessment().getConfiguration().isTimed()) {
			userNotificationService.onTimedAssessmentAttemptStarted(attempt);
		}

		userSearchIndexerHelper.reindexById(userId);

		return attempt;
	}

	private BigDecimal calculateScoreForAttempt(Attempt attempt) throws AssessmentAttemptItemsNotGradedException {
		// Assume all submitted question/answers are correct.
		// Work backwards from there...
		// NOTE Correct == if all answers marked as 'correct' are provided. Nothing less. Nothing more.

		Map<AbstractItem, Set<AttemptResponse>> responsesByItem = Maps.newHashMap();
		for (AttemptResponse r : attempt.getResponses()) {
			if (!r.getItem().isGraded()) {
				continue;
			}

			if (!responsesByItem.containsKey(r.getItem())) {
				responsesByItem.put(r.getItem(), Sets.<AttemptResponse>newHashSet());
			}
			responsesByItem.get(r.getItem()).add(r);
		}

		Set<AbstractItem> gradedItems = attempt.getAssessmentUserAssociation().getAssessment().getGradedItems();

		int totalCount = gradedItems.size();

		if (totalCount < 1) {
			return BigDecimal.valueOf(100.0);
		}

		int correctCount = responsesByItem.size();
		for (AbstractItem item : responsesByItem.keySet()) {
			if (!item.isGraded()) {
				continue;
			}

			if (item.isManuallyGraded()) {
				// If manually graded, assume all or none is correct
				AttemptResponse r = responsesByItem.get(item).iterator().next();

				if (r.isCorrect() == null) {
					throw new AssessmentAttemptItemsNotGradedException();
				}

				if (!r.isCorrect()) {
					correctCount--;
				}

			} else {
				// If not manually graded, responses must have originated from choice selection.
				// Ensure that the response choice set matches the item choice set.
				// If all match we're good; otherwise FAIL.
				Set<Choice> responseChoices = Sets.newHashSet();
				for (AttemptResponse r : responsesByItem.get(item)) {
					responseChoices.add(r.getChoice());
				}

				if (!((AbstractItemWithChoices) item).getCorrectChoices().equals(responseChoices)) {
					correctCount--;
				}
			}
		}

		return BigDecimal.valueOf((double) correctCount / (double) totalCount * 100);
	}

	private Attempt gradeAttemptForAssessment(Attempt attempt) {
		Assert.notNull(attempt);
		Assert.state(!attempt.getStatus().isGraded(), "Attempt already graded");

		BigDecimal passingScore = new BigDecimal(attempt.getAssessment().getConfiguration().getPassingScore()).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal score = calculateScoreForAttempt(attempt);
		Boolean passed = (passingScore.compareTo(score) < 1);

		attempt.setStatus(new AttemptStatusType(AttemptStatusType.GRADED));
		attempt.setScore(score);
		attempt.setPassedFlag(passed);
		attempt.setGradedOn(Calendar.getInstance());

		if (!passed) {
			AssessmentUserAssociation association = attempt.getAssessmentUserAssociation();
			boolean attemptAllowed = isAttemptAllowed(
				association.getAttempts().size(),
				attempt.getAssessment().getConfiguration().getRetakesAllowed(),
				passed,
				attempt.getStatus()
			);
			association.setReattemptAllowedFlag(attemptAllowed);
		} else {
			attempt.getAssessmentUserAssociation().setReattemptAllowedFlag(false);
		}
		return attempt;
	}

	@Override
	public void setAssessmentsForWork(List<AssessmentDTO> assessments, Long workId) {
		if (CollectionUtils.isEmpty(assessments)) {
			return;
		}
		Assert.notNull(workId);

		for (WorkAssessmentAssociation a : findAllWorkAssessmentAssociationByWork(workId)) {
			a.setDeleted(true);
		}

		for (AssessmentDTO assessment : assessments) {
			addAssessmentToWork(assessment.getId(), assessment.isRequired(), workId);
		}
	}

	@Override
	public void addAssessmentToWork(Long assessmentId, Boolean required, Long workId) {
		WorkAssessmentAssociation a = workAssessmentAssociationDAO.findByWorkAndAssessment(workId, assessmentId);
		if (a == null) {
			a = makeWorkAssessmentAssociation();
			a.setWork(baseWorkDAO.get(workId));
			a.setAssessment(assessmentDAO.get(assessmentId));
		}

		a.setDeleted(false);
		a.setRequired(required);
		workAssessmentAssociationDAO.saveOrUpdate(a);
	}

	@Override
	public ImmutableList<Map> getProjectedSurveys(String[] fields) throws Exception {
		List<SurveyAssessment> surveys = surveyAssessmentDAO.findAllBy(
			"company", authenticationService.getCurrentUser().getCompany(),
			"assessmentStatusType", new AssessmentStatusType(AssessmentStatusType.ACTIVE)
		);

		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(fields, surveys));
	}

	@Override
	public List<SurveyAssessment> getSurveys() throws Exception {
		List<SurveyAssessment> surveys = surveyAssessmentDAO.findAllBy(
			"company", authenticationService.getCurrentUser().getCompany(),
			"assessmentStatusType", new AssessmentStatusType(AssessmentStatusType.ACTIVE)
		);

		return surveys;
	}

	@Override
	public boolean isUserAllowedToTakeAssessment(Long assessmentId, Long userId) {
		if (assessmentGroupAssociationDAO.isUserAllowedToTakeAssessment(assessmentId, userId)) {
			return true;
		}
		final Set<Long> workIds = Sets.newHashSet();
		workIds.addAll(testRequirementDAO.findSentWorkIdsWithTestRequirement(assessmentId, userId));
		workIds.addAll(testRequirementDAO.findSentWorkIdsWithTestRequirementFromGroup(assessmentId, userId));
		for (Long workId : workIds) {
			if (workService.isUserWorkResourceForWork(userId, workId) || workService.isWorkShownInFeed(workId)) {
				return true;
			}
		}
		return false;
	}

	public WorkAssessmentAssociation makeWorkAssessmentAssociation() {
		return new WorkAssessmentAssociation();
	}

	private boolean isUserPrivateEmployee(User user) {
		if (userRoleService.hasAnyAclRole(user, AclRole.ACL_EMPLOYEE_WORKER)) {
			return true;
		}

		if (user.getLane3ApprovalStatus().equals(ApprovalStatus.APPROVED) ||
			userRoleService.hasAnyAclRole(user, AclRole.ACL_ADMIN, AclRole.ACL_MANAGER)) {
			return false;
		}

		InternalPrivateNetworkAdmitted internalPrivateNetworkAdmitted = new InternalPrivateNetworkAdmitted();
		internalPrivateNetworkDoorman.welcome(
			new UserGuest(user),
			new InternalPrivateNetworkAdmittedRope(internalPrivateNetworkAdmitted)
		);
		return internalPrivateNetworkAdmitted.getAdmitted();
	}

	public Long getTimeUntilAttemptExpires(final Long assessmentId, final Long userId) {
		if (findAssessment(assessmentId).getConfiguration().getDurationMinutes() == null) {
			return 0L;
		}
		final Attempt attempt = findLatestAttemptForAssessmentByUser(assessmentId, userId);
		final Calendar now = DateUtilities.getCalendarNow();
		final Calendar latestAttemptStartTime = attempt != null ? attempt.getCreatedOn() : DateUtilities.cloneCalendar(now);
		final Calendar expiry = DateUtilities.addMinutes(latestAttemptStartTime, findAssessment(assessmentId).getConfiguration().getDurationMinutes());
		return DateUtilities.getDuration(now, expiry);
	}

	@Override
	public boolean isAttemptFinished(Long assessmentId, Long userId) {
		com.workmarket.domains.model.assessment.Attempt attempt = findLatestAttemptForAssessmentByUser(
			assessmentId, userId
		);

		Set<Long> itemIdsWithResponse = Sets.newHashSet();
		for (AttemptResponse r : attempt.getResponses()) {
			itemIdsWithResponse.add(r.getItem().getId());
		}

		int itemsExpectingResponsesCount = 0;
		for (AbstractItem item : attempt.getAssessment().getItems()) {
			if (!item.getType().equals(AbstractItem.DIVIDER)) {
				itemsExpectingResponsesCount++;
			}
		}

		return itemsExpectingResponsesCount == itemIdsWithResponse.size();
	}
}
