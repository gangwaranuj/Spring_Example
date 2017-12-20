package com.workmarket.service.thrift.transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.dao.assessment.AttemptDAO;
import com.workmarket.dao.assessment.AttemptResponseDAO;
import com.workmarket.dao.asset.AttemptResponseAssetAssociationDAO;
import com.workmarket.dao.asset.LinkDAO;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.assessment.AttemptResponse;
import com.workmarket.domains.model.asset.AttemptResponseAssetAssociation;
import com.workmarket.domains.model.asset.Link;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.service.business.assessment.AssessmentResponseBuilder;
import com.workmarket.service.business.dto.*;
import com.workmarket.service.EntityToObjectFactory;
import com.workmarket.thrift.assessment.*;
import com.workmarket.thrift.assessment.validator.AssessmentSaveRequestValidator;
import com.workmarket.thrift.assessment.validator.AttemptResponseRequestValidator;
import com.workmarket.thrift.assessment.validator.ItemSaveRequestValidator;
import com.workmarket.thrift.core.*;
import com.workmarket.thrift.core.User;
import com.workmarket.utility.SerializationUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AssessmentServiceFacadeImpl implements AssessmentServiceFacade, InitializingBean {
	private static final Log logger = LogFactory.getLog(AssessmentServiceFacadeImpl.class);
	private static final String allowedTags = "p,ul,ol,li,ul,br,strong,em,a,i,b,u";
	private static final int EMBED_URL_MAX_LENGTH = 256;

	@Autowired private AbstractAssessmentDAO assessmentDAO;
	@Autowired private AttemptDAO attemptDAO;
	@Autowired private AttemptResponseDAO attemptResponseDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private LinkDAO linkDAO;
	@Autowired private AttemptResponseAssetAssociationDAO attemptResponseAssetAssociationDAO;

	@Autowired private com.workmarket.service.business.AssessmentService assessmentService;
	@Autowired private com.workmarket.service.business.AssetManagementService assetManagementService;
	@Autowired private com.workmarket.service.infra.business.AuthenticationService authenticationService;

	@Autowired private AssessmentResponseBuilder responseBuilder;
	@Autowired private AssessmentSaveRequestValidator saveRequestValidator;
	@Autowired private ItemSaveRequestValidator itemSaveRequestValidator;
	@Autowired private AttemptResponseRequestValidator attemptResponseRequestValidator;
	@Autowired private EntityToObjectFactory objectFactory;

	@Override
	public AssessmentResponse findAssessment(AssessmentRequest request) throws AssessmentRequestException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
			com.workmarket.domains.model.assessment.AbstractAssessment assessment = assessmentDAO.findAssessmentById(request.getAssessmentId());

			Assert.notNull(currentUser);
			Assert.notNull(assessment);

			authenticationService.setCurrentUser(currentUser);

			Long scopedWorkId = (request.isSetWorkId()) ? request.getWorkId() : null;

			return responseBuilder.buildAssessmentResponse(assessment, currentUser, request.getIncludes(), scopedWorkId);
		} catch (Exception e) {
			logger.error(e);
			throw new AssessmentRequestException();
		}
	}


	@Override
	public AssessmentResponse findAssessmentForGrading(AssessmentGradingRequest request) throws AssessmentRequestException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
			com.workmarket.domains.model.assessment.AbstractAssessment assessment =
				assessmentDAO.findAssessmentById(request.getAssessmentId());
			Assert.isTrue(!assessment.getAssessmentStatusType().getCode().equals(AssessmentStatusType.REMOVED));

			com.workmarket.domains.model.assessment.Attempt attempt = attemptDAO.get(request.getAttemptId());
			Set<AttemptResponse> responses = new LinkedHashSet<AttemptResponse>(attemptResponseDAO.findByAttempt(attempt.getId()));
			attempt.setResponses(responses);

			Assert.notNull(currentUser);
			Assert.notNull(assessment);
			Assert.notNull(attempt);
			Assert.state(attempt.getAssessmentUserAssociation().getAssessment().equals(assessment));

			authenticationService.setCurrentUser(currentUser);

			return responseBuilder.buildAssessmentResponseForGrading(assessment, currentUser, attempt);
		} catch (Exception e) {
			logger.error(e);
			throw new AssessmentRequestException();
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public AssessmentResponse saveOrUpdateAssessment(AssessmentSaveRequest request) throws ValidationException {

		com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
		authenticationService.setCurrentUser(currentUser);

		saveRequestValidator.validate(request);
		Assessment assessment = request.getAssessment();
		AssessmentDTO dto = buildAssessmentDTO(assessment);

		try {
			com.workmarket.domains.model.assessment.AbstractAssessment savedAssessment = assessmentService.saveOrUpdateAssessment(currentUser.getId(), dto);

			// Save skills

			if (assessment.isSetSkills()) {
				List<Long> skillIds = Lists.newArrayList();
				for (Skill s : assessment.getSkills()) {
					skillIds.add(s.getId());
				}
				assessmentService.setSkillsForAssessment(savedAssessment.getId(), skillIds.toArray(new Long[skillIds.size()]));
			}

			// Save notification recipients
			List<Long> userIds = Lists.newArrayList();
			if (assessment.isSetConfiguration() && assessment.getConfiguration().isSetNotificationRecipients()) {
				for (User u : assessment.getConfiguration().getNotificationRecipients()) {
					userIds.add(u.getId());
				}
			}

			assessmentService.setNotificationRecipientsForAssessment(savedAssessment.getId(), userIds.toArray(new Long[userIds.size()]));

			// Save notification preferences

			if (assessment.isSetConfiguration() && assessment.getConfiguration().isSetNotifications()) {
				List<NotificationPreferenceDTO> prefs = getNotificationPreferenceDTOs(assessment);
				assessmentService.setNotificationPreferencesForAssessment(savedAssessment.getId(), prefs.toArray(new NotificationPreferenceDTO[prefs.size()]));
			}

			return responseBuilder.buildAssessmentResponse(savedAssessment, userDAO.findUserById(request.getUserId()), null, null);
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}

	private List<NotificationPreferenceDTO> getNotificationPreferenceDTOs(final Assessment assessment) {
		List<NotificationPreferenceDTO> prefs = Lists.newArrayList();
		for (NotificationTypeConfiguration n : assessment.getConfiguration().getNotifications()) {
			NotificationPreferenceDTO pref = getNotificationPreferenceDTO(n);
			if (pref == null) continue;
			prefs.add(pref);
		}
		return prefs;
	}

	private NotificationPreferenceDTO getNotificationPreferenceDTO(final NotificationTypeConfiguration n) {
		if (!n.isSetType()) {
			return null;
		}

		NotificationPreferenceDTO pref = new NotificationPreferenceDTO();
		String type = getNotificationTypeCode(n);
		pref.setNotificationTypeCode(type);
		if (n.isSetDays()) {
			pref.setDays(n.getDays());
		}
		return pref;
	}

	private String getNotificationTypeCode(final NotificationTypeConfiguration n) {
		String type = null;
		if (n.getType().equals(NotificationType.NEW_ATTEMPT)) {
			type = com.workmarket.domains.model.notification.NotificationType.ASSESSMENT_ATTEMPT_COMPLETED;
		} else if (n.getType().equals(NotificationType.NEW_ATTEMPT_BY_INVITEE)) {
			type = com.workmarket.domains.model.notification.NotificationType.WORK_SURVEY_COMPLETED;
		} else if (n.getType().equals(NotificationType.ATTEMPT_UNGRADED)) {
			type = com.workmarket.domains.model.notification.NotificationType.ASSESSMENT_ATTEMPT_UNGRADED;
		} else if (n.getType().equals(NotificationType.ASSESSMENT_INACTIVE)) {
			type = com.workmarket.domains.model.notification.NotificationType.ASSESSMENT_INACTIVE;
		}
		return type;
	}

	private AssessmentDTO buildAssessmentDTO(final Assessment assessment) {
		AssessmentDTO dto = new AssessmentDTO();
		if (assessment.isSetId()) {
			dto.setId(assessment.getId());
		}
		dto.setName(assessment.getName());
		if (assessment.isSetDescription()) {
			dto.setDescription(StringUtilities.stripTags(
					assessment.getDescription(),
					allowedTags));
		}
		dto.setIndustryId(assessment.getIndustry().getId());
		if (assessment.getType().equals(AssessmentType.GRADED)) {
			dto.setType(AbstractAssessment.GRADED_ASSESSMENT_TYPE);
		} else if (assessment.getType().equals(AssessmentType.SURVEY)) {
			dto.setType(AbstractAssessment.SURVEY_ASSESSMENT_TYPE);
		}
		if (assessment.isSetStatus() && assessment.getStatus().isSetCode()) {
			dto.setAssessmentStatusTypeCode(assessment.getStatus().getCode());
		}
		if (assessment.isSetApproximateDurationMinutes()) {
			dto.setApproximateDurationMinutes(assessment.getApproximateDurationMinutes());
		}

		if (assessment.isSetConfiguration()) {
			AssessmentOptions config = assessment.getConfiguration();
			addAssessmentConfigurationToAssessmentDTO(dto, config);
		}
		return dto;
	}

	private void addAssessmentConfigurationToAssessmentDTO(final AssessmentDTO dto, final AssessmentOptions config) {
		dto.setFeatured(config.isFeatured());
		if (config.isSetPassingScore()) {
			dto.setPassingScore(config.getPassingScore());
		}
		if (config.getPassingScoreShared() != null) {
			dto.setPassingScoreShared(config.getPassingScoreShared());
		}
		dto.setRetakesAllowed(config.getRetakesAllowed());
		if (config.isSetDurationMinutes()) {
			dto.setDurationMinutes(config.getDurationMinutes());
		}
		if (config.getResultsSharedWithPassers() != null) {
			dto.setResultsSharedWithPassers(config.getResultsSharedWithPassers());
		}

		if (config.getResultsSharedWithFailers() != null) {
			dto.setResultsSharedWithFailers(config.getResultsSharedWithFailers());
		}

		if (config.getStatisticsShared() != null) {
			dto.setStatisticsShared(config.getStatisticsShared());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public AssessmentResponse copyAssessment(AssessmentCopyRequest request) throws ValidationException {
		try {
			// NOTE It's much more straightforward to copy the object graph for an assessment, configuration, items/choices, etc via the Thrift interface

			AssessmentRequest findRequest = new AssessmentRequest()
					.setUserId(request.getUserId())
					.setAssessmentId(request.getAssessmentId());
			findRequest.addToIncludes(AssessmentRequestInfo.ITEM_INFO);
			findRequest.addToIncludes(AssessmentRequestInfo.CORRECT_CHOICES_INFO);
			findRequest.addToIncludes(AssessmentRequestInfo.CONTEXT_INFO);
			AssessmentResponse response = findAssessment(findRequest);

			// Copy and save assessment
			Assessment copy = (Assessment)SerializationUtilities.clone(response.getAssessment());

			copy.setId(0L);
			copy.setName(String.format("Copy of %s", copy.getName()));

			response = saveOrUpdateAssessment(
					new AssessmentSaveRequest()
							.setUserId(request.getUserId())
							.setAssessment(copy)
			);

			// Copy each item and save

			if (copy.isSetItems()) {
				for (Item i : copy.getItems()) {
					i.setId(0L);
					if (i.isSetChoices()) {
						for (Choice c : i.getChoices())
							c.setId(0L);
					}

					addOrUpdateItem(
							new ItemSaveRequest()
									.setUserId(request.getUserId())
									.setAssessmentId(response.getAssessment().getId())
									.setItem(i)
					);
				}
			}

			return findAssessment(
					findRequest.setUserId(request.getUserId())
							.setAssessmentId(response.getAssessment().getId())
			);
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAssessmentStatus(AssessmentStatusUpdateRequest request) throws ValidationException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
			authenticationService.setCurrentUser(currentUser);

			assessmentService.updateAssessmentStatus(request.getAssessmentId(), request.getStatus().getCode());
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Item addOrUpdateItem(ItemSaveRequest request) throws ValidationException {
		com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
		authenticationService.setCurrentUser(currentUser);

		itemSaveRequestValidator.validate(request);

		try {
			AssessmentItemDTO dto = new AssessmentItemDTO();
			if (request.getItem().isSetId())
				dto.setItemId(request.getItem().getId());
			dto.setPrompt(request.getItem().getPrompt());
			if (request.getItem().isSetDescription())
				dto.setDescription(request.getItem().getDescription());
			if (request.getItem().isSetHint())
				dto.setHint(request.getItem().getHint());

			String type = getItemType(request);
			dto.setType(type);

			if (request.getItem().isSetHint())
				dto.setHint(request.getItem().getHint());
			if (request.getItem().isSetIncorrectFeedback())
				dto.setIncorrectFeedback(request.getItem().getIncorrectFeedback());

			dto.setOtherAllowed(request.getItem().isOtherAllowed());

			if (request.getItem().isSetMaxLength())
				dto.setMaxLength(request.getItem().getMaxLength());

			dto.setGraded(request.getItem().isGraded());

			com.workmarket.domains.model.assessment.AbstractItem newItem = assessmentService.addOrUpdateItemInAssessment(request.getAssessmentId(), dto);
			com.workmarket.domains.model.assessment.AbstractAssessment assessment = assessmentDAO.findAssessmentById(request.getAssessmentId());

			int index = assessment.getItems().indexOf(newItem);

			Item titem = (Item)SerializationUtilities.clone(request.getItem());
			titem.setId(newItem.getId());
			titem.setPosition(index);

			if (titem.isSetChoices()) {
				List<AssessmentChoiceDTO> cdtos = getAssessmentChoiceDTOs(titem);

				List<com.workmarket.domains.model.assessment.Choice> choices = assessmentService.saveOrUpdateChoicesInItem(titem.getId(), cdtos.toArray(new AssessmentChoiceDTO[cdtos.size()]));
				for (int i = 0; i < choices.size(); i++) {
					titem.getChoices().get(i)
							.setId(choices.get(i).getId())
							.setPosition(i);
				}
			}

			//as per design only one link allowed. In future change this block of code if requirements change.
			if(request.getItem().isSetEmbedLink()){
				String embedLink = request.getItem().getEmbedLink();
				if(embedLink.length() > EMBED_URL_MAX_LENGTH) {
					embedLink = embedLink.substring(0, EMBED_URL_MAX_LENGTH - 1);
				}

				Link link = new Link(null, embedLink);
				linkDAO.saveOrUpdate(link);

				Set<Link> links = Sets.newHashSet();
				links.add(link);

				newItem.setLinks(links);

				titem.setLinks(null);
				for (Link a : newItem.getLinks())
					titem.addToLinks(objectFactory.newLink(link));
			}

			List<AssetDTO> adtos = Lists.newArrayList();
			if (titem.isSetAssets()) {
				for (Asset a : titem.getAssets()) {
					AssetDTO adto = new AssetDTO();
					adto.setAssetId(a.getId());
					adtos.add(adto);
				}
			}
			assetManagementService.setAssetsForAssessmentItem(adtos.toArray(new AssetDTO[adtos.size()]), newItem.getId());

			if (titem.isSetUploads()) {
				for (Upload tupload : titem.getUploads()) {
					UploadDTO uploadDTO = new UploadDTO();
					uploadDTO.setUploadUuid(tupload.getUuid());
					uploadDTO.setDescription(tupload.getDescription());
					assetManagementService.addUploadToAssessmentItem(uploadDTO, newItem.getId());
				}
			}

			titem.setAssets(null);
			for (com.workmarket.domains.model.asset.Asset a : newItem.getAssets()) {
				titem.addToAssets(objectFactory.newAsset(a));
			}
			return titem;
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}

	private List<AssessmentChoiceDTO> getAssessmentChoiceDTOs(final Item titem) {
		List<AssessmentChoiceDTO> cdtos = Lists.newArrayList();
		for (Choice tchoice : titem.getChoices()) {
			AssessmentChoiceDTO cdto = getAssessmentChoiceDTO(tchoice);
			cdtos.add(cdto);
		}
		return cdtos;
	}

	private AssessmentChoiceDTO getAssessmentChoiceDTO(final Choice tchoice) {
		AssessmentChoiceDTO cdto = new AssessmentChoiceDTO();
		if (tchoice.isSetId()) {
			cdto.setChoiceId(tchoice.getId());
		}
		cdto.setIsCorrect(tchoice.isCorrect());
		cdto.setValue(tchoice.getValue());
		return cdto;
	}

	private String getItemType(final ItemSaveRequest request) {
		String type = null;
		if (request.getItem().getType().equals(ItemType.DIVIDER)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.DIVIDER;
		} else if (request.getItem().getType().equals(ItemType.MULTIPLE_CHOICE)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.MULTIPLE_CHOICE;
		} else if (request.getItem().getType().equals(ItemType.SINGLE_CHOICE_RADIO)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.SINGLE_CHOICE_RADIO;
		} else if (request.getItem().getType().equals(ItemType.SINGLE_CHOICE_LIST)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.SINGLE_CHOICE_LIST;
		} else if (request.getItem().getType().equals(ItemType.SINGLE_LINE_TEXT)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.SINGLE_LINE_TEXT;
		} else if (request.getItem().getType().equals(ItemType.MULTIPLE_LINE_TEXT)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.MULTIPLE_LINE_TEXT;
		} else if (request.getItem().getType().equals(ItemType.DATE)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.DATE;
		} else if (request.getItem().getType().equals(ItemType.PHONE)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.PHONE;
		} else if (request.getItem().getType().equals(ItemType.EMAIL)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.EMAIL;
		} else if (request.getItem().getType().equals(ItemType.NUMERIC)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.NUMERIC;
		} else if (request.getItem().getType().equals(ItemType.ASSET)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.ASSET;
		} else if (request.getItem().getType().equals(ItemType.LINK)) {
			type = com.workmarket.domains.model.assessment.AbstractItem.LINK;
		}
		return type;
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void removeItem(ItemRemoveRequest request) throws AssessmentRequestException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
			authenticationService.setCurrentUser(currentUser);

			assessmentService.removeItemFromAssessment(request.getAssessmentId(), request.getItem().getId());
		} catch (Exception e) {
			logger.error(e);
			throw new AssessmentRequestException();
		}
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void reorderItems(ItemReorderRequest request) throws AssessmentRequestException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
			authenticationService.setCurrentUser(currentUser);

			List<Long> ids = Lists.newArrayList();
			for (Item i : request.getItems())
				ids.add(i.getId());

			assessmentService.reorderItemsInAssessment(request.getAssessmentId(), ids.toArray(new Long[ids.size()]));
		} catch (Exception e) {
			logger.error(e);
			throw new AssessmentRequestException();
		}
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Attempt startAttempt(AttemptStartRequest request) throws AssessmentAttemptLimitExceededException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
			authenticationService.setCurrentUser(currentUser);

			com.workmarket.domains.model.assessment.Attempt attempt;
			if (request.isSetWorkId()) {
				attempt = assessmentService.saveAttemptForAssessmentScopedToWork(request.getUserId(), request.getAssessmentId(), request.getWorkId(), request.getBehalfOfId());
			} else {
				attempt = assessmentService.saveAttemptForAssessment(request.getUserId(), request.getAssessmentId());
			}

			return new Attempt()
					.setId(attempt.getId())
					.setCreatedOn(attempt.getCreatedOn().getTimeInMillis());
		} catch (com.workmarket.service.exception.assessment.AssessmentAttemptLimitExceededException e) {
			logger.error(e);
			throw new AssessmentAttemptLimitExceededException();
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Response> submitResponses(AttemptResponseRequest request) throws AssessmentAttemptTimedOutException, ValidationException {
		com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
		authenticationService.setCurrentUser(currentUser);

		attemptResponseRequestValidator.validate(request);

		try {
			List<AttemptResponseDTO> dtos = Lists.newArrayList();
			for (Response r : request.getResponses()) {
				AttemptResponseDTO dto = new AttemptResponseDTO();
				if (r.isSetChoice()) {
					dto.setChoiceId(r.getChoice().getId());
				}
				if (r.isSetValue())
					dto.setValue(r.getValue());

				if (r.isSetAssets()) {
					for (Asset a : r.getAssets()) {
						AssetDTO adto = new AssetDTO();
						if (a.isSetId())
							adto.setAssetId(a.getId());
						dto.addToAssets(adto);
					}
				}

				if (r.isSetUploads()) {
					for (Upload u : r.getUploads()) {
						UploadDTO udto = new UploadDTO();
						if (u.isSetId()) {
							udto.setUploadId(u.getId());
						}
						udto.setUploadUuid(u.getUuid());
						udto.setName(u.getName());
						udto.setDescription(u.getDescription());
						udto.setAssociationType(AssetType.NONE);
						dto.addToUploads(udto);
					}
				}
				dtos.add(dto);
			}
			List<com.workmarket.domains.model.assessment.AttemptResponse> responses;
			if (request.isSetWorkId()) {
				responses = assessmentService.submitResponsesForItemInAssessmentScopedToWork(request.getUserId(), request.getAssessmentId(), request.getItemId(), dtos.toArray(new AttemptResponseDTO[dtos.size()]), request.getWorkId());
			} else {
				responses = assessmentService.submitResponsesForItemInAssessment(request.getUserId(), request.getAssessmentId(), request.getItemId(), dtos.toArray(new AttemptResponseDTO[dtos.size()]));
			}

			for (int i = 0; i < responses.size(); i++) {
				com.workmarket.domains.model.assessment.AttemptResponse r = responses.get(i);
				Response tresponse = request.getResponses().get(i);

				tresponse
						.setId(r.getId())
						.setItem(
								new Item()
										.setId(r.getItem().getId())
										.setType(responseBuilder.getItemType(r.getItem().getType()))
						);

				if (tresponse.isSetAssets() || tresponse.isSetUploads()) {
					if (tresponse.isSetAssets())
						tresponse.getAssets().clear();
					if (tresponse.isSetUploads())
						tresponse.getUploads().clear();
					for (AttemptResponseAssetAssociation a : attemptResponseAssetAssociationDAO.findByAttemptResponse(r.getId()))
						request.getResponses().get(i).addToAssets(objectFactory.newAsset(a.getAsset()));
				}
			}

			return request.getResponses();
		} catch (com.workmarket.service.exception.assessment.AssessmentAttemptTimedOutException e) {
			logger.error(e);
			throw new AssessmentAttemptTimedOutException();
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Response> submitMultipleItemResponses(AttemptMultipleItemResponsesRequest request) throws AssessmentAttemptTimedOutException, ValidationException {
		List<Response> responses = Lists.newArrayList();
		List<ConstraintViolation> errors = Lists.newArrayList();

		for (ItemResponses r : request.getItemResponses()) {
			AttemptResponseRequest responseRequest = new AttemptResponseRequest()
					.setUserId(request.getUserId())
					.setAssessmentId(request.getAssessmentId())
					.setItemId(r.getItemId())
					.setResponses(r.getResponses());
			if (request.isSetWorkId()) {
				responseRequest.setWorkId(request.getWorkId());
			}

			try {
				responses.addAll(submitResponses(responseRequest));
			} catch (ValidationException e) {
				errors.addAll(e.getErrors());
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors);
		}

		return responses;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Attempt completeAttempt(AttemptCompleteRequest request) throws AssessmentAttemptTimedOutException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getUserId());
			authenticationService.setCurrentUser(currentUser);

			com.workmarket.domains.model.assessment.Attempt attempt;
			if (request.isSetWorkId()) {
				attempt = assessmentService.completeAttemptForAssessmentScopedToWork(request.getUserId(), request.getAssessmentId(), request.getWorkId());
			} else {
				attempt = assessmentService.completeAttemptForAssessment(request.getUserId(), request.getAssessmentId());
			}

			return new Attempt()
					.setId(attempt.getId())
					.setCreatedOn(attempt.getCreatedOn().getTimeInMillis())
					.setCompleteOn(attempt.getCompletedOn().getTimeInMillis())
					.setStatus(objectFactory.newStatus(attempt.getStatus()));

		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void gradeResponses(GradeResponsesRequest request) throws AssessmentRequestException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getCurrentUserId());
			authenticationService.setCurrentUser(currentUser);

			assessmentService.gradeResponsesForItemInAttempt(request.getAttemptId(), request.getItemId(), request.isPassed());
		} catch (Exception e) {
			logger.error(e);
			throw new AssessmentRequestException();
		}
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Attempt gradeAttempt(GradeAttemptRequest request) throws AssessmentRequestException, AssessmentAttemptItemsNotGradedException {
		try {
			com.workmarket.domains.model.User currentUser = userDAO.findUserById(request.getCurrentUserId());
			authenticationService.setCurrentUser(currentUser);

			com.workmarket.domains.model.assessment.Attempt attempt = assessmentService.gradeAttemptForAssessment(request.getUserId(), request.getAssessmentId());

			return new Attempt()
					.setId(attempt.getId())
					.setCreatedOn(attempt.getCreatedOn().getTimeInMillis())
					.setCompleteOn(attempt.getCompletedOn().getTimeInMillis())
					.setGradedOn(attempt.getGradedOn().getTimeInMillis())
					.setPassed(attempt.getPassedFlag())
					.setScore(attempt.getScore().doubleValue())
					.setStatus(objectFactory.newStatus(attempt.getStatus()));

		} catch (com.workmarket.service.exception.assessment.AssessmentAttemptItemsNotGradedException e) {
			logger.error(e);
			throw new AssessmentAttemptItemsNotGradedException();
		} catch (Exception e) {
			logger.error(e);
			throw new AssessmentRequestException();
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
