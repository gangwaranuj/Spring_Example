package com.workmarket.service.business.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Message;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.search.request.TrackableSearchRequest;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.UserImportDTO;
import com.workmarket.service.business.event.assessment.InviteUsersToAssessmentEvent;
import com.workmarket.service.business.event.assessment.TimedAssessmentAttemptAutoCompleteScheduledEvent;
import com.workmarket.service.business.event.asset.AssetBundleExpirationEvent;
import com.workmarket.service.business.event.asset.AssetExpirationEvent;
import com.workmarket.service.business.event.asset.BuildDocumentationPackageEvent;
import com.workmarket.service.business.event.group.RevalidateGroupAssociationsEvent;
import com.workmarket.service.business.event.reports.DownloadCertificatesEvent;
import com.workmarket.service.business.event.reports.ExportEvidenceReportEvent;
import com.workmarket.service.business.event.search.SearchRequestEvent;
import com.workmarket.service.business.event.user.BadActorEvent;
import com.workmarket.service.business.event.user.UserBlockCompanyEvent;
import com.workmarket.service.business.event.user.UserReassignmentEvent;
import com.workmarket.service.business.event.work.ResourceConfirmationRequiredScheduledEvent;
import com.workmarket.service.business.event.work.RoutingStrategyCompleteEvent;
import com.workmarket.service.business.event.work.ExecuteRoutingStrategyGroupEvent;
import com.workmarket.service.business.event.work.RoutingStrategyScheduledEvent;
import com.workmarket.service.business.event.work.ValidateResourceCheckInScheduledEvent;
import com.workmarket.service.business.event.work.WorkAcceptedEvent;
import com.workmarket.service.business.event.work.WorkAutoCloseScheduledEvent;
import com.workmarket.service.business.event.work.WorkBundleApplySubmitEvent;
import com.workmarket.service.business.event.work.WorkBundleCancelSubmitEvent;
import com.workmarket.service.business.event.work.WorkBundleDeclinedEvent;
import com.workmarket.service.business.event.work.WorkClosedEvent;
import com.workmarket.service.business.event.work.WorkNegotiationExpiredScheduledEvent;
import com.workmarket.service.business.event.work.WorkResourceInvitation;
import com.workmarket.service.business.event.work.WorkResourceLateLabelScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexByCompanyEvent;
import com.workmarket.service.business.event.work.WorkUpdatedEvent;
import com.workmarket.configuration.Constants;
import com.workmarket.service.search.user.SearchCSVGenerateEvent;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.utility.NumberUtilities;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.workmarket.service.business.upload.users.model.BulkUserUploadRequest;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.partition;

@Service
@SuppressWarnings({"unchecked"})
public class EventFactoryImpl implements EventFactory {
	@Override
	public ValidateResourceCheckInScheduledEvent buildValidateResourceCheckInScheduledEvent(Work work, Calendar scheduledDate) {
		Assert.notNull(work);
		Assert.notNull(work.getId());
		ValidateResourceCheckInScheduledEvent event = new ValidateResourceCheckInScheduledEvent(work.getId(), scheduledDate);
		event.setUser(work.getBuyer());
		return event;
	}

	@Override
	public ResourceConfirmationRequiredScheduledEvent buildResourceConfirmationRequiredScheduledEvent(Work work, Calendar scheduledDate) {
		Assert.notNull(work);
		ResourceConfirmationRequiredScheduledEvent event = new ResourceConfirmationRequiredScheduledEvent(work.getId(), scheduledDate);
		event.setUser(work.getBuyer());
		return event;
	}

	@Override
	public UserGroupAssociationUpdateEvent buildUserGroupAssociationUpdateEvent(UserUserGroupAssociation association) {
		Assert.notNull(association);
		return buildUserGroupAssociationUpdateEvent(association.getUserGroup().getId(), ImmutableList.of(association.getUser().getId()));
	}

	@Override
	public UserGroupAssociationUpdateEvent buildUserGroupAssociationUpdateEvent(Long groupId, List<Long> userIds) {
		Assert.notNull(groupId);
		Assert.notNull(userIds);
		return new UserGroupAssociationUpdateEvent(groupId, userIds);
	}

	@Override
	public WorkNegotiationExpiredScheduledEvent buildWorkNegotiationExpiredScheduledEvent(WorkNegotiation workNegotiation, Calendar scheduleDate) {
		Assert.notNull(workNegotiation);
		WorkNegotiationExpiredScheduledEvent event = new WorkNegotiationExpiredScheduledEvent(workNegotiation.getId(), scheduleDate);
		event.setUser(workNegotiation.getRequestedBy());
		return event;
	}

	@Override
	public UserGroupMessageNotificationEvent buildUserGroupMessageNotificationEvent(Message message) {
		Assert.notNull(message);
		UserGroupMessageNotificationEvent event = new UserGroupMessageNotificationEvent(message);
		event.setUser(message.getSender());
		return event;
	}

	@Override
	public CompanyAvatarUpdatedEvent buildCompanyAvatarUpdatedEvent(Long companyId) {
		return new CompanyAvatarUpdatedEvent(companyId);
	}

	@Override
	public WorkAutoCloseScheduledEvent buildWorkAutoCloseScheduledEvent(Work work, Calendar scheduledDate) {
		Assert.notNull(work);
		WorkAutoCloseScheduledEvent event = new WorkAutoCloseScheduledEvent(work.getId(), scheduledDate);
		event.setUser(work.getBuyer());
		return event;
	}

	@Override
	public TimedAssessmentAttemptAutoCompleteScheduledEvent buildTimedAssessmentAttemptAutoCompleteScheduledEvent(Attempt attempt, Calendar scheduleDate) {
		Assert.notNull(attempt);
		TimedAssessmentAttemptAutoCompleteScheduledEvent event = new TimedAssessmentAttemptAutoCompleteScheduledEvent();
		event.setUser(attempt.getAssessmentUserAssociation().getAssessment().getUser());
		event.setScheduledDate(scheduleDate);
		event.setAttempt(attempt);
		return event;
	}

	@Override
	public AssetExpirationEvent buildAssetExpirationEvent(String assetUuid, Calendar scheduledDate) {
		AssetExpirationEvent event = new AssetExpirationEvent();
		event.setAssetUuid(assetUuid);
		event.setScheduledDate(scheduledDate);
		return event;
	}

	@Override
	public AssetBundleExpirationEvent buildAssetBundleExpirationEvent(String assetUuid, Calendar scheduledDate) {
		AssetBundleExpirationEvent event = new AssetBundleExpirationEvent();
		event.setAssetUuid(assetUuid);
		event.setScheduledDate(scheduledDate);
		return event;
	}

	@Override
	public WorkUpdatedEvent buildWorkUpdatedEvent(Long workId, Map<PropertyChangeType, List<PropertyChange>> propertyChanges) {
		WorkUpdatedEvent event = new WorkUpdatedEvent();
		event.setWorkId(workId);
		event.setPropertyChanges(propertyChanges);
		return event;
	}

	@Override
	public WorkBundleDeclinedEvent buildWorkBundleDeclinedEvent(Long userId, Long workId, Long onBehaldOfUserId) {
		WorkBundleDeclinedEvent event = new WorkBundleDeclinedEvent();
		event.setUserId(userId);
		event.setWorkId(workId);
		event.setOnBehalfOfUserId(onBehaldOfUserId);

		return event;
	}

	@Override
	public WorkBundleApplySubmitEvent buildWorkBundleApplySubmitEvent(Long workId) {
		WorkBundleApplySubmitEvent event = new WorkBundleApplySubmitEvent();
		event.setWorkId(workId);

		return event;
	}

	@Override
	public WorkBundleCancelSubmitEvent buildWorkBundleCancelSubmitEvent(Long workId) {
		WorkBundleCancelSubmitEvent event = new WorkBundleCancelSubmitEvent();
		event.setWorkId(workId);

		return event;
	}

	@Override
	public WorkUpdateSearchIndexByCompanyEvent buildWorkUpdateSearchIndexByCompanyEvent(Long companyId) {
		return new WorkUpdateSearchIndexByCompanyEvent(companyId);
	}

	@Override
	public WorkAcceptedEvent buildWorkAcceptedEvent(Long resourceUserId, Long workId, String assignmentPDFPath) {
		return new WorkAcceptedEvent(resourceUserId, workId, assignmentPDFPath);

	}

	@Override
	public UserReassignmentEvent buildUserReassignmentEvent(Long ownerId, Long newGroupOwnerId, Long newWorkOwnerId, Long newTestOwnerId) {
		UserReassignmentEvent event = new UserReassignmentEvent();
		event.setCurrentUserId(ownerId);
		event.setNextWorkOwnerId(newWorkOwnerId);
		event.setNextGroupOwnerId(newGroupOwnerId);
		event.setNextAssessmentOwnerId(newTestOwnerId);
		return event;
	}

	@Override
	public WorkResourceLateLabelScheduledEvent buildWorkResourceLateLabelScheduledEvent(Long workResourceId, Calendar scheduledDate) {
		return new WorkResourceLateLabelScheduledEvent(scheduledDate, workResourceId);
	}

	@Override
	public WorkSubStatusTypeUpdatedEvent buildWorkSubStatusTypeUpdatedEvent(long userId, long workSubStatusTypeId) {
		return new WorkSubStatusTypeUpdatedEvent(userId, workSubStatusTypeId);
	}

	@Override
	public <T extends AbstractTaxReportSet> TaxReportGenerationEvent buildTaxReportGenerationEvent(User requestor, T taxReportSet) {
		Assert.isAssignable(AbstractTaxReportSet.class, taxReportSet.getClass());
		return new TaxReportGenerationEvent(taxReportSet, requestor);
	}

	@Override
	public <T extends AbstractTaxReportSet> TaxReportPublishedEvent buildTaxReportPublishedEvent(T taxReportSet) {
		Assert.isAssignable(AbstractTaxReportSet.class, taxReportSet.getClass());
		return new TaxReportPublishedEvent(taxReportSet);
	}

	@Override
	public InviteUsersToAssessmentEvent buildInviteUsersToAssessmentEvent(Long userId, Set<String> inviteeUserNumbers, Long assessmentId) {
		return new InviteUsersToAssessmentEvent(userId, inviteeUserNumbers, assessmentId);
	}

	@Override
	public List<InviteToGroupEvent> buildInviteToGroupEvent(List<Long> inviteeUserIds, Long groupId, Long invitedByUserId) {
		Assert.notNull(inviteeUserIds);
		Assert.notNull(groupId);
		Assert.notNull(invitedByUserId);
		List<InviteToGroupEvent> eventList = Lists.newArrayList();
		List<List<Long>> subList = partition(inviteeUserIds, Constants.BULK_INVITE_REQUEST_SIZE);
		for (List<Long> userIds : subList) {
			//Sublists are not serializable
			eventList.add(new InviteToGroupEvent(Lists.newArrayList(userIds), groupId, invitedByUserId));
		}
		return eventList;
	}

	@Override
	public List<InviteToGroupFromCartEvent> buildInviteToGroupFromCartEvent(Set<String> inviteeUserNumbers, Long groupId, Long inviterId) {
		Assert.notNull(inviteeUserNumbers);
		Assert.notNull(groupId);
		Assert.notNull(inviterId);
		List<InviteToGroupFromCartEvent> eventList = Lists.newArrayList();
		List<List<String>> subList = partition(Lists.newArrayList(inviteeUserNumbers), Constants.BULK_OPERATIONS_BUFFER_SIZE);
		for (List<String> userNumbers : subList) {
			eventList.add(new InviteToGroupFromCartEvent(groupId, Sets.newHashSet(userNumbers), inviterId));
		}
		return eventList;
	}

	@Override public List<InviteToGroupFromRecommendationEvent> buildInviteToGroupFromRecommendationEvent(
		final List<Long> inviteeUserIds,
		final Long groupId,
		final Long invitedByUserId
	) {
		Assert.notNull(inviteeUserIds);
		Assert.notNull(groupId);
		Assert.notNull(invitedByUserId);
		List<InviteToGroupFromRecommendationEvent> eventList = Lists.newArrayList();
		List<List<Long>> subList = partition(inviteeUserIds, Constants.BULK_INVITE_REQUEST_SIZE);
		for (List<Long> userIds : subList) {
			//Sublists are not serializable
			eventList.add(new InviteToGroupFromRecommendationEvent(Lists.newArrayList(userIds), groupId, invitedByUserId));
		}
		return eventList;
	}

	@Override
	public AddToWorkerPoolEvent buildAddToWorkerPoolEvent(Long companyId, String userNumber, Set<String> cartUsers) {
		return new AddToWorkerPoolEvent(companyId, userNumber, cartUsers);
	}

	@Override
	public DownloadCertificatesEvent buildDownloadCertificatesEvent(String toEmail, Long groupId, String screeningType) {
		return new DownloadCertificatesEvent(toEmail, groupId, screeningType);
	}

	@Override
	public ExportEvidenceReportEvent buildExportEvidenceReportEvent(String toEmail, Long groupId, String screeningType) {
		return new ExportEvidenceReportEvent(toEmail, groupId, screeningType);
	}

	@Override
	public RevalidateGroupAssociationsEvent buildRevalidateGroupAssociationsEvent(Long groupId) {
		return new RevalidateGroupAssociationsEvent(null, groupId, null);
	}

	@Override
	public SearchCSVGenerateEvent buildSearchCSVGenerateEvent(PeopleSearchRequest peopleSearchRequest) {
		Assert.notNull(peopleSearchRequest);
		return new SearchCSVGenerateEvent(peopleSearchRequest);
	}

	@Override
	public WorkClosedEvent buildWorkClosedEvent(long workId, CloseWorkDTO closeWorkDTO) {
		Assert.notNull(closeWorkDTO);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, 30);
		WorkClosedEvent event = new WorkClosedEvent(closeWorkDTO, workId);
		event.setScheduledDate(now);
		return event;
	}

	@Override
	public UserBlockCompanyEvent buildUserBlockCompanyEvent(long companyId, long userId) {
		return new UserBlockCompanyEvent(companyId, userId);
	}

	@Override
	public BadActorEvent buildBadActorEvent(Long blockedUserId, List<Long> blockingCompanyIds) {
		return new BadActorEvent(blockedUserId, blockingCompanyIds);
	}

	@Override
	public RoutingStrategyScheduledEvent buildRoutingStrategyScheduledEvent(AbstractRoutingStrategy routingStrategy) {
		Assert.notNull(routingStrategy);
		Assert.notNull(routingStrategy.getId());
		Calendar schedule = Calendar.getInstance();
		schedule.add(Calendar.MINUTE, NumberUtilities.defaultValue(routingStrategy.getDelayMinutes()));
		schedule.add(Calendar.SECOND, 5);
		return new RoutingStrategyScheduledEvent(schedule, routingStrategy.getId());
	}

	@Override
	public ExecuteRoutingStrategyGroupEvent buildExecuteRoutingStrategyGroupEvent(long routingStrategyGroupId, int delayMinutes) {
		Calendar schedule = Calendar.getInstance();
		schedule.add(Calendar.MINUTE, NumberUtilities.defaultValue(delayMinutes));
		schedule.add(Calendar.SECOND, 5);
		ExecuteRoutingStrategyGroupEvent event = new ExecuteRoutingStrategyGroupEvent(schedule, routingStrategyGroupId);

		return event;
	}

	@Override
	public RoutingStrategyCompleteEvent buildRoutingStrategyCompleteEvent(AbstractRoutingStrategy routingStrategy) {
		Assert.notNull(routingStrategy);
		Assert.notNull(routingStrategy.getId());
		return new RoutingStrategyCompleteEvent(routingStrategy.getId());
	}


	@Override
	public WorkResourceInvitation buildWorkResourceInvitation(long workId, List<Long> workResourcesIds, boolean voiceDelivery) {
		Set<Long> workResourcesSet = new HashSet<>(workResourcesIds);
		return new WorkResourceInvitation(workResourcesSet, voiceDelivery, workId);
	}

	@Override
	public SendLowBalanceAlertEvent buildSendLowBalanceAlertEvent(Long userId, String email, BigDecimal spendLimit, Calendar scheduleDate) {
		Assert.notNull(userId);
		return new SendLowBalanceAlertEvent(userId, email, spendLimit, scheduleDate);
	}

	@Override
	public RevalidateGroupAssociationsEvent buildRevalidateGroupAssociationsEvent(Long userId, Map<String, Object> modificationType) {
		Assert.notNull(userId);
		return new RevalidateGroupAssociationsEvent(userId, null, modificationType);
	}

	@Override
	public BuildDocumentationPackageEvent buildGetDocumentationPackageEvent(Long downloaderId, Long groupId, List<Long> userIds) {
		return new BuildDocumentationPackageEvent(downloaderId, groupId, userIds);
	}

	@Override
	public BulkWorkUploadEvent buildBulkWorkUploadEvent(final List<WorkSaveRequest> saveRequests, final String uploadKey, final String uploadSizeKey) {
		Assert.notNull(saveRequests);
		return new BulkWorkUploadEvent(saveRequests, uploadKey, uploadSizeKey);
	}

	@Override
	public BulkWorkUploadStarterEvent buildBulkWorkUploadStarterEvent(WorkUploadRequest uploadRequest, Long userId) {
		Assert.notNull(uploadRequest);
		return new BulkWorkUploadStarterEvent(uploadRequest, userId);
	}

	@Override
	public SearchRequestEvent buildSearchRequestEvent(TrackableSearchRequest searchRequest, PeopleSearchResponse peopleSearchResponse, long companyId) {
		if (searchRequest != null && searchRequest.getRequest() != null && peopleSearchResponse != null) {
			SearchRequestEvent event = new SearchRequestEvent<>(searchRequest.getRequest(), peopleSearchResponse.getFacets(), peopleSearchResponse.getTotalResultsCount(), companyId);
			if (TrackableSearchRequest.ASSIGNMENT_REQUEST.equals(searchRequest.getRequestType())) {
				event.setWorkNumber(((AssignmentResourceSearchRequest) searchRequest).getWorkNumber());
			}
			return event;
		}
		return null;
	}

	@Override
	public WorkResourceCacheEvent buildWorkResourceCache(long workId) {
		return new WorkResourceCacheEvent(workId);
	}

	@Override
	public BulkUserUploadStarterEvent buildBulkUserUploadStartEvent(final BulkUserUploadRequest uploadRequest,
	                                                                final BulkUserUploadResponse response,
	                                                                final boolean orgEnabledForUser) {
		Assert.notNull(uploadRequest);
		return new BulkUserUploadStarterEvent(uploadRequest, response, orgEnabledForUser);
	}

	@Override
	public BulkUserUploadDispatchEvent buildBulkUserUploadDispatchEvent(final Long userId,
	                                                                    final String uuid,
	                                                                    final UserImportDTO userImportDTO,
	                                                                    final boolean orgEnabledForUser,
	                                                                    final List<String> orgUnitPaths) {
		Assert.notNull(userId);
		Assert.notNull(uuid);
		Assert.notNull(userImportDTO);
		if(orgEnabledForUser) {
			Assert.notEmpty(orgUnitPaths);
		}
		return new BulkUserUploadDispatchEvent(userId, uuid, userImportDTO, orgUnitPaths);
	}

	@Override
	public BulkUserUploadFinishedEvent buildBulkUserUploadFinishedEvent(final BulkUserUploadResponse response) {
		Assert.notNull(response);
		return new BulkUserUploadFinishedEvent(response);
	}
}
