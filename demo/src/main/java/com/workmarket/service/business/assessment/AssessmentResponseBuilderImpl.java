package com.workmarket.service.business.assessment;

import com.google.common.collect.Sets;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.assessment.AbstractItemDAO;
import com.workmarket.dao.asset.AttemptResponseAssetAssociationDAO;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.assessment.AttemptResponse;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AttemptResponseAssetAssociation;
import com.workmarket.domains.model.asset.Link;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.notification.AssessmentNotificationPreference;
import com.workmarket.service.EntityToObjectFactory;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentOptions;
import com.workmarket.thrift.assessment.AssessmentRequestInfo;
import com.workmarket.thrift.assessment.AssessmentResponse;
import com.workmarket.thrift.assessment.AssessmentStatistics;
import com.workmarket.thrift.assessment.AssessmentType;
import com.workmarket.thrift.assessment.Attempt;
import com.workmarket.thrift.assessment.AttemptWorkScope;
import com.workmarket.thrift.assessment.AuthorizationContext;
import com.workmarket.thrift.assessment.Choice;
import com.workmarket.thrift.assessment.Item;
import com.workmarket.thrift.assessment.ItemType;
import com.workmarket.thrift.assessment.NotificationType;
import com.workmarket.thrift.assessment.NotificationTypeConfiguration;
import com.workmarket.thrift.assessment.RequestContext;
import com.workmarket.thrift.assessment.Response;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.Skill;
import com.workmarket.thrift.core.User;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AssessmentResponseBuilderImpl implements AssessmentResponseBuilder {

	private static final Log logger = LogFactory.getLog(AssessmentResponseBuilderImpl.class);

	@Autowired private UserRoleService userRoleService;
	@Autowired private com.workmarket.service.business.AssessmentService assessmentService;
	@Autowired private EntityToObjectFactory objectFactory;
	@Autowired private UserAssetAssociationDAO userAssetAssociationDAO;
	@Autowired private AttemptResponseAssetAssociationDAO attemptResponseAssetAssociationDAO;
	@Autowired private AbstractItemDAO abstractItemDAO;

	@Override
	public AssessmentResponse buildAssessmentResponse(com.workmarket.domains.model.assessment.AbstractAssessment assessment, com.workmarket.domains.model.User currentUser, Set<AssessmentRequestInfo> includes, Long scopedWorkId) throws Exception {
		AssessmentResponse response = new AssessmentResponse();

		buildAssessment(response, assessment);
		buildSkills(response, assessment);

		if (includes != null) {
			if (includes.contains(AssessmentRequestInfo.CONTEXT_INFO))
				buildContext(response, currentUser, assessment);

			// Either correct choices were explicitly requested or we default
			// to trusting the context to include or hide the correct answers.
			if (includes.contains(AssessmentRequestInfo.ITEM_INFO)) {
				if (includes.contains(AssessmentRequestInfo.CORRECT_CHOICES_INFO)) {
					buildItems(response, assessment, true);
				} else {
					buildItems(response, currentUser, assessment);
				}
			}

			if (includes.contains(AssessmentRequestInfo.LATEST_ATTEMPT_INFO))
				buildLatestAttempt(response, currentUser, assessment, scopedWorkId);

			if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN) || userRoleService.isInternalUser(currentUser)
					|| assessment.isInvitationOnly()) {
				buildConfiguration(response, assessment);

				if (includes.contains(AssessmentRequestInfo.STATISTICS_INFO))
					buildStatistics(response, assessment);
			} else {
				final AssessmentOptions options = new AssessmentOptions();
				options.setFeatured(assessment.getConfiguration().isFeatured());
				if (assessment.getConfiguration().getDurationMinutes() != null)
					options.setDurationMinutes(assessment.getConfiguration().getDurationMinutes());
				response.getAssessment().setConfiguration(options);
			}
		}

		return response;
	}

	@Override
	public AssessmentResponse buildAssessmentResponseForGrading(com.workmarket.domains.model.assessment.AbstractAssessment assessment, com.workmarket.domains.model.User currentUser, com.workmarket.domains.model.assessment.Attempt attempt) throws Exception {
		AssessmentResponse response = new AssessmentResponse();

		buildAssessment(response, assessment);
		buildContext(response, currentUser, assessment);
		buildConfiguration(response, assessment);
		buildItems(response, assessment, true);
		buildAttempt(response, attempt);

		return response;
	}

	private void buildContext(AssessmentResponse response, com.workmarket.domains.model.User currentUser, com.workmarket.domains.model.assessment.AbstractAssessment assessment) throws Exception {
		Set<RequestContext> requestContexts = Sets.newHashSet();
		Set<AuthorizationContext> authzContexts = Sets.newHashSet();

		for (com.workmarket.service.infra.security.RequestContext c : assessmentService.getRequestContext(assessment.getId())) {
			if (com.workmarket.service.infra.security.RequestContext.OWNER.equals(c)) {
				requestContexts.add(RequestContext.OWNER);
			} else if (com.workmarket.service.infra.security.RequestContext.COMPANY_OWNED.equals(c)) {
				requestContexts.add(RequestContext.COMPANY_OWNED);
			} else if (com.workmarket.service.infra.security.RequestContext.INVITED.equals(c)) {
				requestContexts.add(RequestContext.INVITED);
			} else if (com.workmarket.service.infra.security.RequestContext.WORKER_POOL.equals(c)) {
				requestContexts.add(RequestContext.WORKER_POOL);
			} else if (com.workmarket.service.infra.security.RequestContext.RESOURCE.equals(c)) {
				requestContexts.add(RequestContext.RESOURCE);
			}
		}

		if (requestContexts.isEmpty())
			requestContexts.add(RequestContext.UNRELATED);

		response.setRequestContexts(requestContexts);

		if (response.getRequestContexts().contains(RequestContext.OWNER) ||
			(response.getRequestContexts().contains(RequestContext.COMPANY_OWNED) && userRoleService.userHasPermission(currentUser, Permission.MANAGE_ASSESSMENTS))
		) {
			authzContexts.add(AuthorizationContext.ADMIN);
		}

		if (assessment.getAssessmentStatusType().isActive()) {
			authzContexts.add(AuthorizationContext.ATTEMPT);

			if (assessmentService.isAttemptAllowedForAssessmentByUser(assessment.getId(), currentUser.getId()))
				authzContexts.add(AuthorizationContext.REATTEMPT);
		}

		response.setAuthorizationContexts(authzContexts);
	}

	private void buildAssessment(AssessmentResponse response, com.workmarket.domains.model.assessment.AbstractAssessment assessment) throws Exception {
		Assessment tassessment = new Assessment()
			.setId(assessment.getId())
			.setName(assessment.getName())
			.setStatus(objectFactory.newStatus(assessment.getAssessmentStatusType()))
			.setIndustry(
				new Industry()
					.setId(assessment.getIndustry().getId())
					.setName(assessment.getIndustry().getName())
			)
			.setCompany(objectFactory.newCompany(assessment.getCompany()))
			.setCreatedBy(objectFactory.newUser(assessment.getUser()))
			.setCreatedOn(assessment.getCreatedOn().getTimeInMillis())
			.setModifiedOn(assessment.getModifiedOn().getTimeInMillis());

		if (assessment.getDescription() != null)
			tassessment.setDescription(assessment.getDescription());
		if (assessment.getApproximateDurationMinutes() != null)
			tassessment.setApproximateDurationMinutes(assessment.getApproximateDurationMinutes());

		tassessment.setHasAssetItems(assessment.hasAssetItems());

		response.setAssessment(tassessment);

		if (assessment instanceof com.workmarket.domains.model.assessment.GradedAssessment) {
			response.getAssessment().setType(AssessmentType.GRADED);
		} else if (assessment instanceof com.workmarket.domains.model.assessment.SurveyAssessment) {
			response.getAssessment().setType(AssessmentType.SURVEY);
		}
	}

	private void buildConfiguration(AssessmentResponse response, com.workmarket.domains.model.assessment.AbstractAssessment assessment) {
		AssessmentOptions toptions = new AssessmentOptions();
		toptions.setFeatured(assessment.getConfiguration().isFeatured());

		if (assessment.getConfiguration().getPassingScore() != null)
			toptions.setPassingScore(assessment.getConfiguration().getPassingScore());
		if (assessment.getConfiguration().getPassingScoreShared() != null)
			toptions.setPassingScoreShared(assessment.getConfiguration().getPassingScoreShared());
		if (assessment.getConfiguration().getRetakesAllowed() != null)
			toptions.setRetakesAllowed(assessment.getConfiguration().getRetakesAllowed());
		if (assessment.getConfiguration().getDurationMinutes() != null)
			toptions.setDurationMinutes(assessment.getConfiguration().getDurationMinutes());
		if (assessment.getConfiguration().getResultsSharedWithPassers() != null)
			toptions.setResultsSharedWithPassers(assessment.getConfiguration().getResultsSharedWithPassers());
		if (assessment.getConfiguration().getResultsSharedWithFailers() != null)
			toptions.setResultsSharedWithFailers(assessment.getConfiguration().getResultsSharedWithFailers());
		if (assessment.getConfiguration().getStatisticsShared() != null)
			toptions.setStatisticsShared(assessment.getConfiguration().getStatisticsShared());

		for (com.workmarket.domains.model.User u : assessment.getConfiguration().getNotificationRecipients()) {
			toptions.addToNotificationRecipients(new User().setId(u.getId()));
		}

		for (AssessmentNotificationPreference p : assessment.getConfiguration().getNotifications()) {
			NotificationTypeConfiguration config = new NotificationTypeConfiguration();
			if (p.getNotificationType().getCode().equals(com.workmarket.domains.model.notification.NotificationType.ASSESSMENT_ATTEMPT_COMPLETED))
				config.setType(NotificationType.NEW_ATTEMPT);
			else if (p.getNotificationType().getCode().equals(com.workmarket.domains.model.notification.NotificationType.WORK_SURVEY_COMPLETED))
				config.setType(NotificationType.NEW_ATTEMPT_BY_INVITEE);
			else if (p.getNotificationType().getCode().equals(com.workmarket.domains.model.notification.NotificationType.ASSESSMENT_ATTEMPT_UNGRADED))
				config.setType(NotificationType.ATTEMPT_UNGRADED);
			else if (p.getNotificationType().getCode().equals(com.workmarket.domains.model.notification.NotificationType.ASSESSMENT_INACTIVE))
				config.setType(NotificationType.ASSESSMENT_INACTIVE);

			if (p.getDays() != null)
				config.setDays(p.getDays());

			toptions.addToNotifications(config);
		}

		response.getAssessment().setConfiguration(toptions);
	}

	private void buildSkills(AssessmentResponse response, com.workmarket.domains.model.assessment.AbstractAssessment assessment) {
		for (com.workmarket.domains.model.skill.Skill s : assessment.getSkills()) {
			response.getAssessment().addToSkills(
				new Skill()
					.setId(s.getId())
					.setName(s.getName())
			);
		}
	}

	private void buildItems(AssessmentResponse response, com.workmarket.domains.model.User currentUser, com.workmarket.domains.model.assessment.AbstractAssessment assessment) {
		boolean isAdmin = (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN) || userRoleService.isInternalUser(currentUser));
		buildItems(response, assessment, isAdmin);
	}

	private void buildItems(AssessmentResponse response, com.workmarket.domains.model.assessment.AbstractAssessment assessment, boolean identifyCorrectChoices) {
		int itemPosition = 0;
		int numberOfGradedItems = 0;
		int numberOfManuallyGradedItems = 0;
		for (com.workmarket.domains.model.assessment.AbstractItem i : assessment.getItems()) {
			// Seems that removing items doesn't properly update the position index on the items array;
			// the resultant array may have gaps and will result in an NPE.
			// TODO Find a better strategy for maintaining position of items/choices
			if (i == null) continue;

			if (i.isGraded()) {
				numberOfGradedItems++;
				if (i.isManuallyGraded())
					numberOfManuallyGradedItems++;
			}

			Item titem = buildItemFromItem(i, identifyCorrectChoices);
			titem.setPosition(itemPosition++);

			response.getAssessment().addToItems(titem);
		}

		response.getAssessment().setNumberOfGradedItems(numberOfGradedItems);
		response.getAssessment().setNumberOfManuallyGradedItems(numberOfManuallyGradedItems);
	}

	private Item buildItemFromItem(AbstractItem i, boolean identifyCorrectChoices) {
		Item titem = new Item()
			.setId(i.getId())
			.setGraded(i.isGraded())
			.setManuallyGraded(i.isManuallyGraded());
		if (i.getPrompt() != null)
			titem.setPrompt(i.getPrompt());
		if (i.getDescription() != null)
			titem.setDescription(i.getDescription());
		if (i.getHint() != null)
			titem.setHint(i.getHint());
		if (i.getIncorrectFeedback() != null)
			titem.setIncorrectFeedback(i.getIncorrectFeedback());
		if (i.getMaxLength() != null)
			titem.setMaxLength(i.getMaxLength());

		titem.setType(getItemType(i.getType()));

		for (com.workmarket.domains.model.asset.Asset a : i.getAssets()) {
			titem.addToAssets(objectFactory.newAsset(a));
		}

		for(Link link : i.getLinks()) {
			titem.addToLinks(objectFactory.newLink(link));
		}

		if (i instanceof com.workmarket.domains.model.assessment.AbstractItemWithChoices) {
			titem.setOtherAllowed(((com.workmarket.domains.model.assessment.AbstractItemWithChoices)i).getOtherAllowed());

			int choicePosition = 0;
			for (com.workmarket.domains.model.assessment.Choice c : ((com.workmarket.domains.model.assessment.AbstractItemWithChoices)i).getChoices()) {
				// Seems that removing choices doesn't properly update the position index on the choices array;
				// the resultant array may have gaps and will result in an NPE.
				// TODO Find a better strategy for maintaining position of items/choices
				if (c == null) continue;

				Choice tchoice = new Choice()
					.setId(c.getId())
					.setPosition(choicePosition++)
					.setValue(c.getValue());

				if (identifyCorrectChoices) {
					tchoice.setCorrect(c.getIsCorrect());
				}

				titem.addToChoices(tchoice);
			}
		}

		return titem;
	}

	public ItemType getItemType(String type) {
		if (type.equals(AbstractItem.DATE))
			return ItemType.DATE;
		if (type.equals(AbstractItem.DIVIDER))
			return ItemType.DIVIDER;
		if (type.equals(AbstractItem.EMAIL))
			return ItemType.EMAIL;
		if (type.equals(AbstractItem.MULTIPLE_CHOICE))
			return ItemType.MULTIPLE_CHOICE;
		if (type.equals(AbstractItem.MULTIPLE_LINE_TEXT))
			return ItemType.MULTIPLE_LINE_TEXT;
		if (type.equals(AbstractItem.NUMERIC))
			return ItemType.NUMERIC;
		if (type.equals(AbstractItem.PHONE))
			return ItemType.PHONE;
		if (type.equals(AbstractItem.SINGLE_CHOICE_LIST))
			return ItemType.SINGLE_CHOICE_LIST;
		if (type.equals(AbstractItem.SINGLE_CHOICE_RADIO))
			return ItemType.SINGLE_CHOICE_RADIO;
		if (type.equals(AbstractItem.SINGLE_LINE_TEXT))
			return ItemType.SINGLE_LINE_TEXT;
		if (type.equals(AbstractItem.ASSET))
			return ItemType.ASSET;
		if (type.equals(AbstractItem.LINK))
			return ItemType.LINK;
		return null;
	}

	private void buildLatestAttempt(AssessmentResponse response, com.workmarket.domains.model.User currentUser, AbstractAssessment assessment, Long scopedWorkId) throws Exception {
		com.workmarket.domains.model.assessment.Attempt attempt;

		if (scopedWorkId != null) {
			attempt = assessmentService.findLatestAttemptForAssessmentByUserScopedToWork(assessment.getId(), currentUser.getId(), scopedWorkId);
		} else {
			attempt = assessmentService.findLatestAttemptForAssessmentByUser(assessment.getId(), currentUser.getId());
		}

		if (attempt == null) return;

		boolean isAdmin = (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN) || userRoleService.isInternalUser(currentUser));
		boolean isGradedAndShared = (
			attempt.getStatus().isGraded() && (
				(assessment.getConfiguration().getResultsSharedWithPassers() && attempt.getPassedFlag()) ||
				(assessment.getConfiguration().getResultsSharedWithFailers() && !attempt.getPassedFlag())
			)
		);

		boolean showOverallGrade = (isAdmin || assessment.getConfiguration().getStatisticsShared());
		boolean showResponseGrade = (isAdmin || isGradedAndShared);

		Attempt tattempt = buildAttemptFromAttempt(attempt, showOverallGrade, showResponseGrade);

		response.setLatestAttempt(tattempt);
	}

	private void buildAttempt(AssessmentResponse response, com.workmarket.domains.model.assessment.Attempt attempt) {
		Attempt tattempt = buildAttemptFromAttempt(attempt, true, true);

		com.workmarket.domains.model.User u = attempt.getAssessmentUserAssociation().getUser();
		User tuser = objectFactory.newUser(u);
		tuser.setCompany(
			new Company()
				.setId(u.getCompany().getId())
				.setName(u.getCompany().getEffectiveName())
		);

		UserAssetAssociation avatars = userAssetAssociationDAO.findUserAvatars(u.getId());
		if (avatars != null) {
			Asset avatarOriginal = avatars.getAsset();
			Asset avatarSmall = avatars.getTransformedSmallAsset();
			Asset avatarLarge = avatars.getTransformedLargeAsset();

			if (avatarOriginal != null) {
				tuser.setAvatarOriginal(objectFactory.newAsset(avatarOriginal));
			}
			if (avatarSmall != null) {
				tuser.setAvatarSmall(objectFactory.newAsset(avatarSmall));
			}
			if (avatarLarge != null) {
				tuser.setAvatarLarge(objectFactory.newAsset(avatarLarge));
			}
		}

		tattempt.setUser(tuser);

		response.setRequestedAttempt(tattempt);
	}

	private Attempt buildAttemptFromAttempt(com.workmarket.domains.model.assessment.Attempt attempt, boolean showOverallGrade, boolean showResponseGrade) {
		Attempt tattempt = new Attempt()
			.setId(attempt.getId())
			.setCreatedOn(attempt.getCreatedOn().getTimeInMillis())
			.setStatus(objectFactory.newStatus(attempt.getStatus()));

		tattempt.setTotalAttemptsCount(attempt.getAssessmentUserAssociation().getAttempts().size());

		if (attempt.getCompletedOn() != null) {
			tattempt.setCompleteOn(attempt.getCompletedOn().getTimeInMillis());
			tattempt.setDuration(tattempt.getCompleteOn() - tattempt.getCreatedOn());
		}
		if (attempt.getStatus().isGraded()) {
			tattempt.setPassed(attempt.getPassedFlag());
			if (attempt.getGradedOn() != null)
				tattempt.setGradedOn(attempt.getGradedOn().getTimeInMillis());
			if (showOverallGrade)
				tattempt.setScore(attempt.getScore().doubleValue());
		}

		int position = 0;
		for (com.workmarket.domains.model.assessment.AttemptResponse r : attempt.getResponses()) {
			tattempt.addToResponses(buildResponseFromAttemptResponse(r, showResponseGrade, position++));
		}

		if (attempt instanceof com.workmarket.domains.model.assessment.WorkScopedAttempt) {
			com.workmarket.domains.work.model.AbstractWork w = ((com.workmarket.domains.model.assessment.WorkScopedAttempt)attempt).getWork();
			tattempt.setScopedToWork(true);
			tattempt.setWork(
				new AttemptWorkScope()
					.setId(w.getId())
					.setWorkNumber(w.getWorkNumber())
					.setTitle(w.getTitle())
			);
		}

		return tattempt;
	}

	private Response buildResponseFromAttemptResponse(AttemptResponse r, boolean showGrade, int position) {

		// grading needs to display a snapshot of the item values when it was taken, not the current assessment
		AbstractItem itemSnapshot = abstractItemDAO.get(r.getItem().getId());

		Item item = buildItemFromItem(itemSnapshot, true)
				.setPosition(position);

		Response tresponse = new Response()
			.setId(r.getId())
			.setItem(item);

		if (r.getValue() != null) {
			tresponse.setValue(r.getValue());
		}
		if (r.getChoice() != null) {
			Choice tchoice = new Choice()
				.setId(r.getChoice().getId())
				.setValue(r.getChoice().getValue());
			if (showGrade) {
				tchoice.setCorrect(r.getChoice().getIsCorrect());
			}
			tresponse.setChoice(tchoice);
		}
		if (!r.getAssets().isEmpty()) {
			for (AttemptResponseAssetAssociation a : attemptResponseAssetAssociationDAO.findByAttemptResponse(r.getId()))
				tresponse.addToAssets(objectFactory.newAsset(a.getAsset()));
		}

		if (showGrade && r.getItem().isManuallyGraded() && r.isGraded()) {
			tresponse.setCorrect(r.isCorrect());
			tresponse.setGradedOn(r.getGradedOn().getTimeInMillis());
			tresponse.setGrader(objectFactory.newUser(r.getGrader()));
		}
		return tresponse;
	}

	private void buildStatistics(AssessmentResponse response, AbstractAssessment assessment) {
		com.workmarket.domains.model.assessment.AssessmentStatistics stats = assessmentService.getAssessmentStatistics(assessment.getId());

		response.getAssessment().setStatistics(
			new AssessmentStatistics()
				.setNumberOfInvited(stats.getNumberOfInvited())
				.setNumberOfPassed(stats.getNumberOfPassed())
				.setNumberOfFailed(stats.getNumberOfFailed())
				.setAverageScore(stats.getAverageScore())
		);
	}
}
