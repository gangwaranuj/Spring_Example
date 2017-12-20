package com.workmarket.service.business.event;

import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.Message;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.search.request.TrackableSearchRequest;
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
import com.workmarket.service.business.event.work.ExecuteRoutingStrategyGroupEvent;
import com.workmarket.service.business.event.work.ResourceConfirmationRequiredScheduledEvent;
import com.workmarket.service.business.event.work.RoutingStrategyCompleteEvent;
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
import com.workmarket.service.business.upload.users.model.BulkUserUploadRequest;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.search.user.SearchCSVGenerateEvent;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventFactory {

	ValidateResourceCheckInScheduledEvent buildValidateResourceCheckInScheduledEvent(Work work, Calendar scheduledDate);

	ResourceConfirmationRequiredScheduledEvent buildResourceConfirmationRequiredScheduledEvent(Work work, Calendar scheduledDate);

	UserGroupAssociationUpdateEvent buildUserGroupAssociationUpdateEvent(UserUserGroupAssociation association);

	UserGroupAssociationUpdateEvent buildUserGroupAssociationUpdateEvent(Long groupId, List<Long> userIds);

	WorkNegotiationExpiredScheduledEvent buildWorkNegotiationExpiredScheduledEvent(WorkNegotiation workNegotiation, Calendar scheduleDate);

	UserGroupMessageNotificationEvent buildUserGroupMessageNotificationEvent(Message message);

	CompanyAvatarUpdatedEvent buildCompanyAvatarUpdatedEvent(Long companyId);

	WorkAutoCloseScheduledEvent buildWorkAutoCloseScheduledEvent(Work work, Calendar scheduledDate);

	TimedAssessmentAttemptAutoCompleteScheduledEvent buildTimedAssessmentAttemptAutoCompleteScheduledEvent(Attempt attempt, Calendar scheduleDate);

	AssetBundleExpirationEvent buildAssetBundleExpirationEvent(String assetUuid, Calendar scheduledDate);

	AssetExpirationEvent buildAssetExpirationEvent(String assetUuid, Calendar scheduledDate);

	WorkUpdatedEvent buildWorkUpdatedEvent(Long workId, Map<PropertyChangeType, List<PropertyChange>> propertyChanges);

	WorkUpdateSearchIndexByCompanyEvent buildWorkUpdateSearchIndexByCompanyEvent(Long companyId);

	WorkAcceptedEvent buildWorkAcceptedEvent(Long resourceUserId, Long workId, String assignmentPDFPath);

	UserReassignmentEvent buildUserReassignmentEvent(Long ownerId, Long newGroupOwnerId, Long newWorkOwnerId, Long newTestOwnerId);

	WorkResourceLateLabelScheduledEvent buildWorkResourceLateLabelScheduledEvent(Long workResourceId, Calendar scheduledDate);

	WorkSubStatusTypeUpdatedEvent buildWorkSubStatusTypeUpdatedEvent(long userId, long workSubStatusTypeId);

	<T extends AbstractTaxReportSet> TaxReportGenerationEvent buildTaxReportGenerationEvent(User requestor, T taxReportSet);

	<T extends AbstractTaxReportSet> TaxReportPublishedEvent buildTaxReportPublishedEvent(T taxReportSet);

	InviteUsersToAssessmentEvent buildInviteUsersToAssessmentEvent(Long userId, Set<String> inviteeUserIds, Long assessmentId);

	List<InviteToGroupEvent> buildInviteToGroupEvent(List<Long> inviteeUserIds, Long groupId, Long invitedByUserId);

	List<InviteToGroupFromCartEvent> buildInviteToGroupFromCartEvent(Set<String> inviteeUserNumbers, Long groupId, Long inviterId);

	List<InviteToGroupFromRecommendationEvent> buildInviteToGroupFromRecommendationEvent(List<Long> inviteeUserIds, Long groupId, Long invitedByUserId);

	AddToWorkerPoolEvent buildAddToWorkerPoolEvent(Long companyId, String userNumber, Set<String> cartUsers);

	DownloadCertificatesEvent buildDownloadCertificatesEvent(String toEmail, Long groupId, String screeningType);

	ExportEvidenceReportEvent buildExportEvidenceReportEvent(String toEmail, Long groupId, String screeningType);

	RevalidateGroupAssociationsEvent buildRevalidateGroupAssociationsEvent(Long userId, Map<String, Object> modificationType);

	RevalidateGroupAssociationsEvent buildRevalidateGroupAssociationsEvent(Long groupId);

	SearchCSVGenerateEvent buildSearchCSVGenerateEvent(PeopleSearchRequest peopleSearchRequest);

	WorkClosedEvent buildWorkClosedEvent(long workId, CloseWorkDTO closeWorkDTO);

	UserBlockCompanyEvent buildUserBlockCompanyEvent(long companyId, long userId);

	SendLowBalanceAlertEvent buildSendLowBalanceAlertEvent(Long userId, String email, BigDecimal spendLimit, Calendar scheduleDate);

	BadActorEvent buildBadActorEvent(Long blockedUserId, List<Long> blockingCompanyIds);

	RoutingStrategyScheduledEvent buildRoutingStrategyScheduledEvent(AbstractRoutingStrategy routingStrategy);

	ExecuteRoutingStrategyGroupEvent buildExecuteRoutingStrategyGroupEvent(long routingStrategyGroupId, int delayMinutes);

	RoutingStrategyCompleteEvent buildRoutingStrategyCompleteEvent(AbstractRoutingStrategy routingStrategy);

	WorkResourceInvitation buildWorkResourceInvitation(long workId, List<Long> workResourcesIds, boolean voiceDelivery);

	BuildDocumentationPackageEvent buildGetDocumentationPackageEvent(Long downloaderId, Long groupId, List<Long> userIds);

	WorkBundleDeclinedEvent buildWorkBundleDeclinedEvent(Long userId, Long workId, Long onBehaldOfUserId);

	WorkBundleApplySubmitEvent buildWorkBundleApplySubmitEvent(Long workId);

	WorkBundleCancelSubmitEvent buildWorkBundleCancelSubmitEvent(Long workId);

	BulkWorkUploadEvent buildBulkWorkUploadEvent(List<WorkSaveRequest> saveRequests, String uploadKey, String uploadSizeKey);

	BulkWorkUploadStarterEvent buildBulkWorkUploadStarterEvent(WorkUploadRequest uploadRequest, Long userId);

	SearchRequestEvent buildSearchRequestEvent(TrackableSearchRequest searchRequest, PeopleSearchResponse peopleSearchResponse, long companyId);

	WorkResourceCacheEvent buildWorkResourceCache(long workId);

	BulkUserUploadStarterEvent buildBulkUserUploadStartEvent(final BulkUserUploadRequest request,
	                                                         final BulkUserUploadResponse response,
	                                                         final boolean orgEnabledForUser);

	BulkUserUploadDispatchEvent buildBulkUserUploadDispatchEvent(final Long userId,
	                                                             final String uuid,
	                                                             final UserImportDTO user,
	                                                             final boolean orgEnabledForUser,
	                                                             final List<String> orgUnitPaths);

	BulkUserUploadFinishedEvent buildBulkUserUploadFinishedEvent(BulkUserUploadResponse response);
}
