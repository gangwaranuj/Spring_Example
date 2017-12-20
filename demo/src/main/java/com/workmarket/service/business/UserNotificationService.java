package com.workmarket.service.business;

import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.WorkScopedAttempt;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotification;
import com.workmarket.domains.model.notification.UserNotificationPagination;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.request.Request;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.dto.UnreadNotificationsDTO;
import com.workmarket.group.UserGroupExpiration;
import com.workmarket.notification.user.vo.UserNotificationSearchRequest;
import com.workmarket.notification.user.vo.UserNotificationSearchResponse;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.business.event.work.WorkAcceptedEvent;
import com.workmarket.service.business.screening.ScreeningAndUser;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.dto.UserNotificationDTO;
import com.workmarket.thrift.work.display.ReportResponse;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserNotificationService {

	void sendUserNotification(UserNotificationDTO dto);
	boolean sendUserNotification(
			final String uuid,
			final boolean isSticky,
			final String displayMessage,
			final Long toUserId,
			final Long fromUserId,
			final String notificationTypeCode);

	UserNotificationPagination findAllUserNotifications(Long userId, UserNotificationPagination pagination);

	List<UserNotification> findAllCacheableUserNotifications(Long userId);

	String findAllUserNotificationsForBullhornJson(Long userId);

	void archiveUserNotification(String userNotificationUuid, Long userId);

	void setViewedAtNotificationAsync(Long userId, String startUuid, String endUuid);

	void setViewedAtNotificationAsync(Long userId, UnreadNotificationsDTO unreadNotificationsDTO);

	void setViewedAtNotification(Long userId, UnreadNotificationsDTO unreadNotificationsDTO);

	NotificationType findNotificationTypeByCode(String code);

	UnreadNotificationsDTO getUnreadNotificationsInfoByUser(long userId);

	UnreadNotificationsDTO getUnreadNotificationsDTO(long userId);

	Set<Long> getCompaniesWithLowBalanceForAlert();

	UserNotificationSearchResponse search(UserNotificationSearchRequest request);

	/**
	 * Work Notifications
	 */

	void onWorkCreated(Work work);

	void onWorkCreated(Long workId);

	void onWorkInvitation(Long workId, List<Long> userResourceIds, boolean voiceDelivery);

	void onWorkInvitationForVendor(Long workId, Long companyId);

	void onWorkReinvited(Work work, List<WorkResource> resources);

	void sendWorkNotifyInvitations(Long workId, List<Long> usersToNotify);

	void onWorkDeclined(Work work, WorkResource resource);

	void onWorkAccepted(Long workId, Long workResourceUserId);

	void onWorkAcceptedEvent(WorkAcceptedEvent event);

	void sendWorkDetailsToResource(Long resourceUserId, Long workId, String assignmentHTML);

	void onWorkUnassigned(WorkResource workResource, String messageForWorker);

	void onWorkAppointmentSet(Long workId);

	void onWorkAttachmentAdded(Work work, Asset asset);

	void onWorkResourceConfirmed(Long workResourceId);

	void onWorkResourceCheckedIn(Work work);

	void onWorkResourceCheckedOut(Work work);

	void onWorkResourceNotCheckedIn(WorkResource resource);

	void onAssignmentSurveyCompleted(WorkScopedAttempt attempt);

	void onWorkUpdated(Long workId, Map<PropertyChangeType, List<PropertyChange>> propertyChanges);

	void onProfileModificationApproved(Long userId, List<String> description);

	void onWorkIncomplete(Work work, String message);

	void onWorkNegotiationRequested(AbstractWorkNegotiation negotiation);

	void onWorkNegotiationApproved(AbstractWorkNegotiation negotiation);

	void onWorkNegotiationDeclined(AbstractWorkNegotiation negotiation);

	void onWorkNegotiationExpirationExtended(WorkNegotiation negotiation);

	void onWorkCompleted(long workId, boolean isCompleteOnBehalf);

	void onDeliverableLate(WorkResource resource);

	void onDeliverableDueReminder(WorkResource resource);

	void onDeliverableRequirementComplete(Work work);

	void onWorkNoteAdded(WorkNote note);

	void onQuestionCreated(WorkQuestionAnswerPair workQuestionAnswerPair, Long workId);

	void onQuestionAnswered(WorkQuestionAnswerPair workQuestionAnswerPair, Long workId);

	void onWorkRemindResourceToComplete(Work work, User resource, Note note);

	void onRatingCreated(Rating rating);

	/**
	 * Work Bundle notifications
	 */

	void onWorkBundleNegotiationApproved(AbstractWorkNegotiation negotiation);

	void onBundleWorkAcceptFailed(Long buyerId, User worker, Work work, AcceptWorkResponse failure);

	/**
	 * All payment related notifications
	 */

	void sendInvoiceDueReminders();

	void onNewStatement(long statementId);

	void onFailedStatement(Map<Long, Exception> exceptionMap);

	void onOverridePaymentTerms(Company company, String note);

	<T extends AbstractInvoice> void onNewInvoice(T invoice);

	void onSubscriptionConfigurationEffective(SubscriptionConfiguration configuration);

	void onSubscriptionConfigurationCancelled(SubscriptionConfiguration configuration);

	void onSubscriptionPaymentTierThroughputReached(SubscriptionConfiguration subscriptionConfiguration, SubscriptionPaymentTier activeSubscriptionPaymentTier, BigDecimal throughput);

	void onCreditCardTransaction(CreditCardTransaction creditCardTransaction, User user);

	void onCreditTransaction(RegisterTransaction transaction);

	void onDebitTransaction(RegisterTransaction transaction);

	void onFundsWithdrawn(BankAccountTransaction transaction);

	void onFundsProcessed(Long transactionId);

	void onWorkCancelled(Work work, WorkResource workResource, CancelWorkDTO cancelWorkDTO, boolean isAssignmentPaid);

	void onWorkStopPayment(Long workId, String reason);

	void onWorkClosedAndPaid(Long workResourceId);

	void onWorkSubStatus(Long workId, Long workResourceId, WorkSubStatusTypeAssociation association);

	/**
	 * Reconciliation notifications
	 */

	void onSubscriptionThroughputDifference(Collection<String> differences);

	void onAccountRegisterReconciliationDifference(Collection<String> differences);

	void onPaymentConfigurationReconciliationDifference(Collection<String> differences);

	void onNextThroughputResetDateDifference(Collection<String> differences);

	/**
	 * Groups notifications
	 */

	void onUserApprovedToGroup(UserUserGroupAssociation userUserGroupAssociation);

	void onUserDeclinedForGroup(UserUserGroupAssociation userUserGroupAssociation);

	void onUserGroupApplication(UserUserGroupAssociation userUserGroupAssociation);

	void onUserGroupInvitation(UserGroup group, Request request, UserGroupInvitationType userGroupInvitationType);

	void onExpirationNotificationsForBuyer(UserGroupExpiration expiration);

	void onUserGroupInvitations(Long groupId, Long requester, List<Long> invitedUserIds);

	void onUserGroupToVendorsInvitation(Long groupId, Long requester, List<Long> invitedCompanyIds);

	void onDeactivateInactiveUserGroups(List<Long> groupIds);


	/**
	 * Screening notifications
	 */

	<T extends Screening> void onScreeningResponse(T screening);

	void onDrugTestRequest(ScreeningAndUser screening);

	/**
	 * Assessment notifications
	 */

	void onTimedAssessmentAttemptStarted(Attempt attempt);

	void onAssessmentCompleted(Attempt attempt);

	void onAssessmentGraded(Attempt attempt);

	/**
	 * Profile Notifications
	 */

	void onLaneAssociationCreated(LaneAssociation association);

	void onConfirmAccount(User user, boolean sendWelcomeEmail);

	/**
	 * Company Wide Notifications
	 */

	void onCompanyAccountLocked(Long companyId);

	void on24HourInvoiceDueWarnings(Map<Long, Calendar> companyIds, Map<Long, Long> invoiceIdsToWarnOwners);

	void onCompanyAccountLockedOverdueWarning(Set<Long> companyIds, Integer daysSinceOverdue);

	void onLowBalanceAlert(Long companyId, Calendar scheduleDate);


	/**
	 * Misc Notifications
	 */

	void onSearchCSVGenerated(Asset asset, FileDTO fileDTO, String recipient);

	void onForumCommentAdded(ForumPost post, ForumPost parent);

	void onLargeWorkReportGenerated(String reportKey, String reportName, Set<String> recipients, Asset asset) throws HostServiceException;

	void onWorkReportGenerated(String reportKey, String reportName, Set<String> recipients, Asset asset, String filename, ReportResponse response, Long reportId);

	void onTalentPoolRequirementExpiration(Criterion criterion, String verb);

	void onAssetBundleAvailable(User user, Asset asset) throws HostServiceException;

	void onNewCompany(User user);

}
