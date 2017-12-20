package com.workmarket.service.infra.event;

import com.workmarket.domains.work.service.actions.AddAttachmentsWorkEvent;
import com.workmarket.domains.work.service.actions.AddNotesWorkEvent;
import com.workmarket.domains.work.service.actions.ApproveForPaymentWorkEvent;
import com.workmarket.domains.work.service.actions.BulkCancelWorksEvent;
import com.workmarket.domains.work.service.actions.BulkEditClientProjectEvent;
import com.workmarket.domains.work.service.actions.BulkLabelRemovalEvent;
import com.workmarket.domains.work.service.actions.RemoveAttachmentsEvent;
import com.workmarket.domains.work.service.actions.RescheduleEvent;
import com.workmarket.domains.work.service.actions.WorkViewedEvent;
import com.workmarket.logging.NRTrace;
import com.workmarket.service.business.event.*;
import com.workmarket.service.business.event.assessment.InviteUsersToAssessmentEvent;
import com.workmarket.service.business.event.assessment.TimedAssessmentAttemptAutoCompleteScheduledEvent;
import com.workmarket.service.business.event.asset.AssetBundleExpirationEvent;
import com.workmarket.service.business.event.asset.AssetExpirationEvent;
import com.workmarket.service.business.event.asset.BuildDocumentationPackageEvent;
import com.workmarket.service.business.event.asset.DeleteDeliverableEvent;
import com.workmarket.service.business.event.calendar.CalendarSyncAddAssignmentsEvent;
import com.workmarket.service.business.event.calendar.CalendarSyncRemoveAssignmentsEvent;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.business.event.group.AddUsersToGroupEvent;
import com.workmarket.service.business.event.group.GroupUpdateSearchIndexEvent;
import com.workmarket.service.business.event.group.RevalidateGroupAssociationsEvent;
import com.workmarket.service.business.event.reporting.WorkReportGenerateEvent;
import com.workmarket.service.business.event.reports.DownloadCertificatesEvent;
import com.workmarket.service.business.event.reports.ExportEvidenceReportEvent;
import com.workmarket.service.business.event.user.BadActorEvent;
import com.workmarket.service.business.event.user.ProfileUpdateEvent;
import com.workmarket.service.business.event.user.UserAverageRatingEvent;
import com.workmarket.service.business.event.user.UserBlockCompanyEvent;
import com.workmarket.service.business.event.user.UserReassignmentEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.ExecuteRoutingStrategyGroupEvent;
import com.workmarket.service.business.event.work.ResourceConfirmationRequiredScheduledEvent;
import com.workmarket.service.business.event.work.RoutingStrategyCompleteEvent;
import com.workmarket.service.business.event.work.RoutingStrategyScheduledEvent;
import com.workmarket.service.business.event.work.ValidateResourceCheckInScheduledEvent;
import com.workmarket.service.business.event.work.WorkAcceptedEvent;
import com.workmarket.service.business.event.work.WorkAutoCloseScheduledEvent;
import com.workmarket.service.business.event.work.WorkBundleAcceptEvent;
import com.workmarket.service.business.event.work.WorkBundleApplySubmitEvent;
import com.workmarket.service.business.event.work.WorkBundleCancelSubmitEvent;
import com.workmarket.service.business.event.work.WorkBundleDeclineOfferEvent;
import com.workmarket.service.business.event.work.WorkBundleDeclinedEvent;
import com.workmarket.service.business.event.work.WorkBundleRoutingEvent;
import com.workmarket.service.business.event.work.WorkBundleVendorRoutingEvent;
import com.workmarket.service.business.event.work.WorkClosedEvent;
import com.workmarket.service.business.event.work.WorkCompletedEvent;
import com.workmarket.service.business.event.work.WorkCreatedEvent;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkNegotiationExpiredScheduledEvent;
import com.workmarket.service.business.event.work.WorkRepriceEvent;
import com.workmarket.service.business.event.work.WorkResendInvitationsEvent;
import com.workmarket.service.business.event.work.WorkResourceInvitation;
import com.workmarket.service.business.event.work.WorkResourceLateLabelScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexByCompanyEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkUpdatedEvent;
import com.workmarket.service.business.queue.WorkPaidDelayedEvent;
import com.workmarket.service.search.user.SearchCSVGenerateEvent;

import java.util.Collection;

/**
 * NON TRANSACTIONAL Service, which only purpose should be to route the event content to a service that will
 * execute the expected actions. To be refactored......
 */
public interface EventRouter {

	void sendEvent(Event event);

	void sendEvents(Collection<? extends Event> events);

	void onEvent(Object e);

	void onEvent(EntityUpdateEvent event);

	void onEvent(ValidateResourceCheckInScheduledEvent event);

	void onEvent(ResourceConfirmationRequiredScheduledEvent event);

	void onEvent(UserGroupMessageNotificationEvent event);

	void onEvent(AddAttachmentsWorkEvent event);

	void onEvent(UserGroupAssociationUpdateEvent event);

	void onEvent(CompanyAvatarUpdatedEvent event);

	void onEvent(WorkAutoCloseScheduledEvent event);

	void onEvent(TimedAssessmentAttemptAutoCompleteScheduledEvent event);

	void onEvent(WorkNegotiationExpiredScheduledEvent event);

	void onEvent(AssetBundleExpirationEvent event);

	void onEvent(AssetExpirationEvent event);

	void onEvent(DeleteDeliverableEvent event);

	void onEvent(WorkUpdatedEvent event);

	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkBundleCancelSubmitEvent")
	void onEvent(WorkBundleCancelSubmitEvent event);

	/** INDEXER EVENTS **/
	void onEvent(WorkUpdateSearchIndexEvent event);

	void onEvent(WorkUpdateSearchIndexByCompanyEvent event);

	void onEvent(UserSearchIndexEvent event);

	void onEvent(VendorSearchIndexEvent event);

	void onEvent(GroupUpdateSearchIndexEvent event);

	void onEvent(WorkAcceptedEvent event);

	void onEvent(WorkInvoiceGenerateEvent event);

	void onEvent(WorkReportGenerateEvent event);

	void onEvent(UserReassignmentEvent event);

	void onEvent(WorkResourceLateLabelScheduledEvent event);

	void onEvent(WorkSubStatusTypeUpdatedEvent event);

	void onEvent(TaxVerificationEvent event);

	void onEvent(TaxReportGenerationEvent event);

	void onEvent(TaxReportPublishedEvent event);

	void onEvent(InviteUsersToAssessmentEvent event);

	void onEvent(InviteToGroupEvent event);

	void onEvent(InviteToGroupFromRecommendationEvent event);

	void onEvent(InviteToGroupFromCartEvent event);

	void onEvent(AddToWorkerPoolEvent event);

	void onEvent(DownloadCertificatesEvent event);

	void onEvent(ExportEvidenceReportEvent event);

	void onEvent(WorkResendInvitationsEvent event);

	void onEvent(AddNotesWorkEvent event);

	void onEvent(RemoveAttachmentsEvent event);

	void onEvent(ApproveForPaymentWorkEvent event);

	void onEvent(RevalidateGroupAssociationsEvent event);

	void onEvent(SearchCSVGenerateEvent event);

	void onEvent(WorkViewedEvent event);

	void onEvent(WorkClosedEvent event);

	void onEvent(BadActorEvent event);

	void onEvent(RoutingStrategyScheduledEvent event);

	void onEvent(ExecuteRoutingStrategyGroupEvent event);

	void onEvent(RoutingStrategyCompleteEvent event);

	void onEvent(UserBlockCompanyEvent event);

	void onEvent(SendLowBalanceAlertEvent event);

	void onEvent(RefreshUserNotificationCacheEvent event);

	void onEvent(MarkUserNotificationsAsReadEvent event);

	void onEvent(WorkResourceInvitation event);

	void onEvent(BuyerSignUpSugarIntegrationEvent event);

	void onEvent(RescheduleEvent event);

	void onEvent(CalendarSyncAddAssignmentsEvent event);

	void onEvent(CalendarSyncRemoveAssignmentsEvent event);

	void onEvent(BulkLabelRemovalEvent event);

	void onEvent(BuildDocumentationPackageEvent event);

	void onEvent(BulkCancelWorksEvent event);

	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkCreatedEvent") void onEvent(WorkCreatedEvent event);

	void onEvent(WorkBundleDeclinedEvent event);

	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkBundleDeclinedEvent") void onEvent(WorkBundleDeclineOfferEvent event);

	void onEvent(WorkBundleApplySubmitEvent event);

	void onEvent(BulkEditClientProjectEvent event);

	void onEvent(ProfileUpdateEvent event);

	void onEvent(WorkCompletedEvent event);

	void onEvent(UnlockCompanyEvent event);

	void onEvent(CompanyDueInvoicesEvent event);

	void onEvent(FundsProcessingEvent event);

	void onEvent(WorkBundleRoutingEvent event);

	void onEvent(WorkBundleVendorRoutingEvent event);

	void onEvent(WorkBundleAcceptEvent event);

	void onEvent(BulkWorkUploadEvent event);

	void onEvent(BulkWorkUploadStarterEvent event);

	void onEvent(UpdateBankTransactionsStatusEvent event);

	void onEvent(WorkPaidDelayedEvent event);

	void onEvent(WorkResourceCacheEvent event);

	void onEvent(AddUsersToGroupEvent event);

	void onEvent(MigrateBankAccountsEvent event);

	void onEvent(MigrateTaxEntitiesEvent event);

	void onEvent(UserAverageRatingEvent event);

	void onEvent(InvoicesDownloadedEvent event);

	void onEvent(BulkUserUploadStarterEvent event);

	void onEvent(BulkUserUploadDispatchEvent event);

	void onEvent(BulkUserUploadFinishedEvent event);

	void onEvent(RestoreTaxEntityTaxNumbersFromVault event);

	void onEvent(RestoreBankAccountNumbersFromVault event);

	void onEvent(RestoreTaxReportTaxNumbersFromVault event);

	void onEvent(UserGroupValidationEvent event);

	void onEvent(UserGroupsValidationEvent event);

	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/BulkSaveCustomFields") void onEvent(BulkSaveCustomFieldsEvent event);

	@NRTrace(dispatcher = true, metricName = "Custom/onEvent/WorkReprice") void onEvent(WorkRepriceEvent event);
}
