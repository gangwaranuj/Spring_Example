package com.workmarket.common.template;

import com.workmarket.common.template.email.BatchEvidenceReportTemplate;
import com.workmarket.common.template.email.BlockCompanyNotificationTemplate;
import com.workmarket.common.template.email.EvidenceReportCSVTemplate;
import com.workmarket.common.template.email.TalentPoolRequirementExpirationEmailTemplate;
import com.workmarket.common.template.email.UserGroupExpirationEmailTemplate;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.WorkScopedAttempt;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkDue;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.group.UserGroupExpiration;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.event.user.UserBlockCompanyEvent;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.thrift.work.display.ReportResponse;
import com.workmarket.thrift.work.uploader.WorkUpload;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NotificationTemplateFactory {

	/**
	 * Alert sub status.
	 *
	 * @param toId
	 * @param subStatusAssociation
	 * @param work
	 * @param workResource
	 * @return
	 */
	WorkSubStatusAlertNotificationTemplate buildWorkSubStatusAlertNotificationTemplate(Long toId, WorkSubStatusTypeAssociation subStatusAssociation, Work work, WorkResource workResource);

	/**
	 * Regular substatus.
	 *
	 * @param toId
	 * @param subStatusAssociation
	 * @param work
	 * @param workResource
	 * @return
	 */
	WorkSubStatusNotificationTemplate buildWorkSubStatusNotificationTemplate(Long toId, WorkSubStatusTypeAssociation subStatusAssociation, Work work, WorkResource workResource);

	GenericEmailOnlyNotificationTemplate buildGenericEmailOnlyNotificationTemplate(Long fromId, Long toId, String emailSubject, String message, NotificationType notificationType);

	ApproveProfileModificationNotificationTemplate buildApproveProfileModificationNotificationTemplate(Long toId, List<String> description);

	WorkCreatedNotificationTemplate buildWorkCreatedNotificationTemplate(Long toId, Work work);

	WorkGenericNotificationTemplate buildWorkGenericNotificationTemplate(Long toId, Work work, String message);

	WorkGenericNotificationTemplate buildWorkGenericNotificationTemplate(Long toId, Long workId, String message);

	WorkUpdatedNotificationTemplate buildWorkUpdatedNotificationTemplate(Long toId, Work work);

	WorkInvitationNotificationTemplate buildWorkInvitationNotificationTemplate(Long toId, Work work, int mandatoryRequirementsCount);

	WorkBundleInvitationNotificationTemplate buildWorkBundleInvitationNotificationTemplate(Long toId, Work work, Map<String, Object> bundleData);

	WorkStoppedPaymentNotificationTemplate buildWorkStoppedPaymentNotificationTemplate(Long toId, Work work, String reason);

	WorkReinvitedNotificationTemplate buildWorkReinvitedNotificationTemplate(Long toId, Work work);

	WorkAcceptedNotificationTemplate buildWorkAcceptedNotificationTemplate(Long toId, Work work, User resource, WorkNegotiation negotiation);

	WorkBundleAcceptedNotificationTemplate buildWorkBundleAcceptedNotificationTemplate(Long toId, Work work, User resource);

	WorkAcceptedDetailsNotificationTemplate buildWorkAcceptedDetailsNotificationTemplate(Long toId, Work work, User resource);

	WorkBundleAcceptedDetailsNotificationTemplate buildWorkBundleAcceptedDetailsNotificationTemplate(Long toId, Work work, User resource, Map<String, Object> bundleData);

	WorkCompleteNotificationTemplate buildWorkCompleteNotificationTemplate(Long fromId, Long toId, Work work, PaymentSummaryDTO payment);

	WorkCompletedByBuyerNotificationTemplate buildWorkCompletedByBuyerNotificationTemplate(Long fromId, Long toId, Work work, Boolean fastFundsEnabled);

	WorkCancelledNotificationTemplate buildWorkCancelledPaidNotificationTemplate(Long toId, Work work, WorkResource resource, String message);

	WorkUnassignedNotificationTemplate buildWorkUnassignedNotificationTemplate(Long toId, Work work, WorkResource resource, String message);

	WorkCancelledWithoutPayNotificationTemplate buildWorkCancelledWithoutPayNotificationTemplate(Long toId, Work work, WorkResource resource, String message);

	WorkIncompleteNotificationTemplate buildWorkIncompleteNotificationTemplate(Work work, Long toId, String message);

	WorkDeclinedNotificationTemplate buildWorkDeclinedNotificationTemplate(Long toId, Work work, User resource);

	WorkCompletedFundsAddedNotificationTemplate buildWorkCompletedFundsAddedNotificationTemplate(Long toId, Work work, WorkResource resource, boolean hasValidTaxEntity);

	WorkQuestionNotificationTemplate buildWorkQuestionNotificationTemplate(Long toId, Work work, WorkQuestionAnswerPair question);

	WorkQuestionAnsweredNotificationTemplate buildWorkQuestionAnsweredNotificationTemplate(Long toId, Work work, WorkQuestionAnswerPair question);

	WorkResourceConfirmationNotificationTemplate buildWorkResourceConfirmationNotificationTemplate(Long toId, Work work, DateRange appointment);

	WorkResourceCheckInNotificationTemplate buildWorkResourceCheckInNotificationTemplate(Long toId, Work work);

	WorkResourceConfirmedNotificationTemplate buildWorkResourceConfirmedNotificationTemplate(Long toId, Work work, User resource);

	WorkResourceCheckedInNotificationTemplate buildWorkResourceCheckedInNotificationTemplate(Long toId, Work work, User resource);

	WorkResourceCheckedOutNotificationTemplate buildWorkResourceCheckedOutNotificationTemplate(Long toId, Work work, User resource);

	AbstractWorkNotificationTemplate buildWorkNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation);

	WorkNegotiationApprovedNotificationTemplate buildWorkNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation);

	WorkBundleNegotiationApprovedNotificationTemplate buildWorkBundleNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation, Map<String, Object> bundleData);

	WorkBundleNegotiationApprovedNotificationTemplate buildWorkBundleNegotiationApprovedNotificationTemplate(Long toId, Long onBehalfOfUserId, Work work, WorkNegotiation negotiation, Map<String, Object> bundleData);

	NotificationTemplate buildAbstractWorkNegotiationDeclinedNotificationTemplate(Long toId, Work work, AbstractWorkNegotiation negotiation);

	WorkNegotiationDeclinedNotificationTemplate buildWorkNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation);

	WorkNegotiationExpirationExtendedNotificationTemplate buildWorkNegotiationExpirationExtendedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation);

	WorkRescheduleNegotiationRequestedNotificationTemplate buildWorkRescheduleNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation);

	WorkRescheduleNegotiationApprovedNotificationTemplate buildWorkRescheduleNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation);

	WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate buildWorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation);

	WorkRescheduleNegotiationDeclinedNotificationTemplate buildWorkRescheduleNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation);

	WorkBudgetNegotiationRequestedNotificationTemplate buildWorkBudgetNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation);

	WorkBudgetNegotiationAddedNotificationTemplate buildWorkBudgetNegotiationAddedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation);

	WorkBudgetNegotiationApprovedNotificationTemplate buildWorkBudgetNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation);

	WorkBudgetNegotiationDeclinedNotificationTemplate buildWorkBudgetNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation);

	WorkExpenseNegotiationRequestedNotificationTemplate buildWorkExpenseNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation);

	WorkExpenseNegotiationAddedNotificationTemplate buildWorkExpenseNegotiationAddedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation);

	WorkExpenseNegotiationApprovedNotificationTemplate buildWorkExpenseNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation);

	WorkExpenseNegotiationDeclinedNotificationTemplate buildWorkExpenseNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation);

	WorkBonusNegotiationRequestedNotificationTemplate buildWorkBonusNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation);

	WorkBonusNegotiationAddedNotificationTemplate buildWorkBonusNegotiationAddedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation);

	WorkBonusNegotiationApprovedNotificationTemplate buildWorkBonusNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation);

	WorkBonusNegotiationDeclinedNotificationTemplate buildWorkBonusNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation);

	WorkReportGeneratedEmailTemplate buildWorkReportGeneratedTemplate(Long reportId, String reportName, Set<String> toEmails, FileDTO asset, ReportResponse response);

	WorkReportGeneratedLargeEmailTemplate buildLargeWorkReportGeneratedTemplate(String reportKey, String reportName, Set<String> toEmails, String downloadUri);

	Lane23AssociationCreatedNotificationTemplate buildLane23AssociationCreatedNotificationTemplate(Long toId, Company company, boolean includeWelcomeEmail);

	LowBalanceEmailTemplate buildLowBalanceEmailTemplate(Long toId, String toEmail, BigDecimal spendLimit);

	UserGroupApprovalNotificationTemplate buildUserGroupApprovalNotificationTemplate(Long fromId, Long toId, UserGroup group);

	UserGroupDeclineNotificationTemplate buildUserGroupDeclineNotificationTemplate(Long fromId, Long toId, UserGroup group);

	UserGroupApplicationNotificationTemplate buildUserGroupApplicationNotificationTemplate(Long toId, UserGroup group, User resource, boolean overrideRequested);

	UserGroupPrivateApplicationNotificationTemplate buildUserGroupPrivateApplicationNotificationTemplate(Long toId, UserGroup group, User resource);

	UserGroupInvitationNotificationTemplate buildUserGroupInvitationNotificationTemplate(Long fromId, Long toId, UserGroup group, boolean vendorInvitation);

	UserGroupMessageNotificationTemplate buildUserGroupMessage(Long fromId, Long toId, String message, String title, UserGroup group);

	UserGroupRequirementsModificationNotificationTemplate buildUserGroupRequirementsModificationNotificationTemplate(Long fromId, Long toId, UserGroup group);

	UserGroupInvitationForUserProfileModificationNotificationTemplate buildUserGroupInvitationForUserProfileModificationNotificationTemplate(Long fromId, Long toId, UserGroup group);

	UserGroupInvitationForUserProfileModificationOwnerNotificationTemplate buildUserGroupInvitationForUserProfileModificationOwnerNotificationTemplate(Long toId, UserGroup group, User resource);

	NotificationTemplate buildAssessmentInvitationEmailTemplate(Long fromId, AbstractAssessment assessment, Long toUserId);

	MultipleAssessmentInvitationsNotificationTemplate buildMultipleAssessmentInvitationsNotificationTemplate(Long fromId, List<AbstractAssessment> assessments, Long toUserId);

	AssessmentCompletedNotificationTemplate buildAssessmentCompletedNotificationTemplate(Long fromId, Long toId, Attempt attempt, AbstractAssessment assessment);

	AssessmentGradedNotificationTemplate buildAssessmentGradedNotificationTemplate(Long fromId, Long toId, Attempt attempt);

	AssessmentGradePendingNotificationTemplate buildAssessmentGradePendingNotificationTemplate(Long fromId, Long toId, Attempt attempt);

	WorkSurveyCompletedNotificationTemplate buildWorkSurveyCompletedNotificationTemplate(Long fromId, Long toId, WorkScopedAttempt attempt, Work work);

	WorkRatingCreatedNotificationTemplate buildWorkRatingCreatedNotificationTemplate(Long toId, Rating rating);

	DrugTestPassedNotificationTemplate buildDrugTestPassedNotificationTemplate(Long toUserId, Screening screening);

	DrugTestFailedNotificationTemplate buildDrugTestFailedNotificationTemplate(Long toUserId, Screening screening);

	BackgroundCheckPassedNotificationTemplate buildBackgroundCheckPassedNotificationTemplate(Long toUserId, Screening screening);

	BackgroundCheckFailedNotificationTemplate buildBackgroundCheckFailedNotificationTemplate(Long toUserId, Screening screening);

	NotificationTemplate buildFundsDepositNotificationTemplate(Long toUserId, RegisterTransaction transaction, Integer invoicesDueCount, BigDecimal invoicesDueTotal, Calendar invoicesDueDate);

	<T extends AbstractBankAccount> NotificationTemplate  buildFundsWithdrawnNotificationTemplate(Long toUserId,RegisterTransaction transaction,T bankAccount);

	FundsProcessedNotificationTemplate  buildFundsProcessedNotificationTemplate(Long toUserId, RegisterTransaction transaction);

	FundsDepositReturnNotificationTemplate buildFundsDepositReturnNotificationTemplate(long toUserId, RegisterTransaction transaction);

	WorkResourceCancelledNotificationTemplate buildWorkResourceCancelledNotificationTemplate(WorkResource resource, Long toId);

	WorkAttachmentAddedNotificationTemplate buildWorkAttachmentAddedNotificationTemplate(Long toId, Work work, Asset asset);

	WorkNoteAddedNotificationTemplate buildWorkNoteAddedNotificationTemplate(Long toId, Work work, Note note, String type);

	WorkDeliverableRejectedNotificationTemplate buildWorkDeliverableRejectedNotificationTemplate(Long toId, Work work, String assetName, String rejectionReason);

	WorkDeliverableLateNotificationTemplate buildWorkDeliverableLateNotificationTemplate(Long toId, Work work);

	WorkDeliverableDueReminderNotificationTemplate buildWorkDeliverableDueReminderNotificationTemplate(Long toId, Work work);

	WorkDeliverableFulfilledNotificationTemplate buildWorkDeliverableFulfilledNotificationTemplate(Long toId, Work work);

	InvoiceDueNotificationTemplate buildInvoiceDueNotificationTemplate(Long toId, List<Invoice> invoices, Map<Long, WorkDue> invoiceAssignments);

	InvoiceDueNotificationTemplate buildOwnerInvoiceDueNotificationTemplate(Long toId, List<Invoice> invoices, Map<Long, WorkDue> invoiceAssignments);

/*{ LockedCompanyAccount */

	LockedCompanyAccountNotificationTemplate buildLockedCompanyAccountNotificationTemplate(Long toId, BigDecimal pastDuePayables);

	LockedCompanyAccount24HrsWarningNotificationTemplate buildLockedCompanyAccount24HrsWarningNotificationTemplate(Long toId, BigDecimal upcomingDuePayables);

	LockedCompanyAccount24HrsWarningNotificationTemplate buildInvoiceDue24HoursNotificationTemplate(Long toId, BigDecimal invoiceAmountDue);

	LockedCompanyAccountOverdueWarningNotificationTemplate buildLockedCompanyAccountOverdueWarningEmailTemplate(Long toId, Integer daysSinceOverdue, Integer daysTillSuspension, BigDecimal pastDuePayables);

/*} LockedCompanyAccount */

	NotificationTemplate buildWelcomeNotificationTemplate(User user);

	WorkAppointmentNotificationTemplate buildWorkAppointmentNotificationTemplate(Long toId, Work work, DateRange appointment);

	WorkAppointmentNotificationTemplate buildWorkAppointmentNotificationTemplate(Long toId, Long onBehalfOfId, Work work, DateRange appointment);

	WorkRemindResourceToCompleteNotificationTemplate buildWorkRemindResourceToComplete(Long toId, Long fromId, Work work, Note note);

	NotificationTemplate buildOverridePaymentTermsNotificationTemplate(Long toId, Company company, String note);

	NotificationTemplate buildAssetBundleAvailableNotificationTemplate(Long toId, String downloadUri, Calendar expiration);

	<T extends AbstractInvoice> NotificationTemplate buildNewInvoiceNotificationTemplate(long toId, T invoice);

	List<BlockCompanyNotificationTemplate> buildBlockCompanyNotificationTemplates(UserBlockCompanyEvent event);

	SurveyCompletedNotificationTemplate buildSurveyCompletedNotificationTemplate(Long fromId, Long toId, Attempt attempt);

	WorkSubStatusFailedConfirmationNotificationTemplate buildWorkSubStatusFailedConfirmationNotificationTemplate(WorkResource resource, Long toId, Work work, DateRange appointment);

	WorkNotAvailableNotificationTemplate buildWorkNotAvailableNotificationTemplate(Long toId, Work work);

	WorkNotAvailableNotificationTemplate buildWorkNotAvailableNotificationTemplate(Long toId, Long onBehalfOfUserId, Work work);

	SubscriptionEffectiveNotificationTemplate buildSubscriptionEffectiveNotificationTemplate(long toId, SubscriptionConfiguration configuration, String timeZoneId);

	SubscriptionCancelledNotificationTemplate buildSubscriptionCancelledNotificationTemplate(long toId, SubscriptionConfiguration configuration, String timeZoneId);

	TaxVerificationNotificationTemplate buildTaxEntityNotificationTemplate(AbstractTaxEntity entity);

	TaxReportGeneratedNotificationTemplate buildTaxReportGeneratedNotificationTemplate(long toId);

	<T extends AbstractTaxReport> List<NotificationTemplate> buildTaxReportAvailableNotificationTemplates(T taxReport);

	NotificationTemplate buildCreditCardReceiptNotificationTemplate(Long toUserId, CreditCardTransaction creditCardTransaction);

	BatchEvidenceReportTemplate buildBatchEvidenceReportTemplate(String toEmail, PDFTemplate pdfTemplate);

	EvidenceReportCSVTemplate buildEvidenceReportCSVTemplate(String toEmail,FileDTO fileDTO);

	SearchCSVGeneratedTemplate buildSearchCSVGeneratedTemplate(String toEmail, FileDTO fileDTO);

	SearchCSVGeneratedLargeEmailTemplate buildSearchCSVGeneratedLargeEmailTemplate(String downloadUri, String recipient);

	UserGroupExpirationEmailTemplate buildExpirationNotificationTemplate(UserGroupExpiration userGroupExpiration);

	TalentPoolRequirementExpirationEmailTemplate buildTalentPoolRequirementExpirationNotificationTemplate(Criterion criterion, UserGroup userGroup, String expirationDate, String verb);

	NotificationTemplate buildWorkResourceInvitation(Work work, long userId, boolean isVoiceDelivery, boolean isBundle, int requirementCount);

	NotificationTemplate buildWorkResourceInvitation(long workId, long userId, boolean isVoiceDelivery);

	NotificationTemplate buildWorkResourceInvitation(Work work, long userId, long onBehalfOfUserId, boolean isVoiceDelivery, boolean isBundle, int requirementCount);

	DocumentationPackageNotificationTemplate buildDocumentationPackageNotificationTemplate(Long downloaderId, String uri);

	UserGroupRequirementsExpirationNotificationTemplate buildUserGroupRequirementsExpirationNotificationTemplate(Long fromId, Long toId, UserGroup group);

	NotificationTemplate buildForumCommentAddedNotificationTemplate(Long toId, ForumPost post, ForumPost parent);

	NotificationTemplate buildForumCommentAddedPostForCreatorNotificationTemplate(Long toId, ForumPost post, ForumPost parent);

	BulkUploadFinishedNotificationTemplate buildBulkUploadFinishedNotificationTemplate(Long toId, List<String> workNumbers, List<Long> failedRows);

	BulkUploadFailedNotificationTemplate buildBulkUploadFailedNotificationTemplate(Long toId, List<WorkUpload> errorUploads);

	BundleWorkAcceptFailedNotificationTemplate buildBundleWorkAcceptFailedNotificationTemplate(Long toId, User Worker, Work work, AcceptWorkResponse failure);

	SubscriptionPaymentTierThroughputReachedNotificationTemplate buildSubscriptionPaymentTierThroughputReached(Long id, SubscriptionConfiguration configuration, SubscriptionPaymentTier activeSubscriptionPaymentTier, BigDecimal throughput, String timeZoneId);

	BulkUserUploadFinishedNotificationTemplate buildBulkUserUploadFinishedNotificationTemplate(Long toId, BulkUserUploadResponse response);

	BulkUserUploadFailedNotificationTemplate buildBulkUserUploadFailedNotificationTemplate(Long toId, BulkUserUploadResponse response, boolean showError);

	BulkWorkRepriceResultNotificationTemplate buildBulkWorkRepricResultNotificationTemplate(Long toId, int succeeded, int failed);
}
