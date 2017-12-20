package com.workmarket.common.template;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import com.workmarket.common.template.email.BatchEvidenceReportTemplate;
import com.workmarket.common.template.email.BlockCompanyNotificationTemplate;
import com.workmarket.common.template.email.EvidenceReportCSVTemplate;
import com.workmarket.common.template.email.TalentPoolRequirementExpirationEmailTemplate;
import com.workmarket.common.template.email.UserGroupExpirationEmailTemplate;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.common.template.pdf.PDFTemplateFactory;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.assessment.WorkScopedAttempt;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.velvetrope.dao.AdmissionDAO;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.WorkDue;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.group.UserGroupExpiration;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.event.user.UserBlockCompanyEvent;
import com.workmarket.service.business.requirementsets.RequirementSetsService;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.sms.ExperimentPercentageEvaluator;
import com.workmarket.thrift.work.display.ReportResponse;
import com.workmarket.thrift.work.uploader.WorkUpload;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NotificationTemplateFactoryImpl implements NotificationTemplateFactory {

	@Autowired private WorkService workService;
	@Autowired private PDFTemplateFactory PDFTemplateFactory;
	@Qualifier("accountRegisterServicePrefundImpl")
	@Autowired private AccountRegisterService accountRegisterService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private RequirementSetsService requirementSetsService;
	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private ExperimentPercentageEvaluator experimentPercentageEvaluator;
	@Autowired private AdmissionDAO admissionDAO;
	@Autowired private UserRoleService userRoleService;

	@Override
	public WorkSubStatusAlertNotificationTemplate buildWorkSubStatusAlertNotificationTemplate(Long toId, WorkSubStatusTypeAssociation subStatusAssociation, Work work, WorkResource workResource) {
		return new WorkSubStatusAlertNotificationTemplate(toId, subStatusAssociation, work, workResource, workService.findActiveWorkerId(work.getId()));
	}

	@Override
	public WorkSubStatusNotificationTemplate buildWorkSubStatusNotificationTemplate(Long toId, WorkSubStatusTypeAssociation subStatusAssociation, Work work, WorkResource workResource) {
		return new WorkSubStatusNotificationTemplate(toId, subStatusAssociation, work, workResource, workService.findActiveWorkerId(work.getId()));
	}

	@Override
	public GenericEmailOnlyNotificationTemplate buildGenericEmailOnlyNotificationTemplate(Long fromId, Long toId, String emailSubject, String message, NotificationType notificationType) {
		return new GenericEmailOnlyNotificationTemplate(fromId, toId, emailSubject, message, notificationType);
	}

	@Override
	public ApproveProfileModificationNotificationTemplate buildApproveProfileModificationNotificationTemplate(Long toId, List<String> description) {
		return new ApproveProfileModificationNotificationTemplate(toId, description);
	}

	@Override
	public WorkGenericNotificationTemplate buildWorkGenericNotificationTemplate(Long toId, Work work, String message) {
		return new WorkGenericNotificationTemplate(toId, work, message);
	}

	@Override
	public WorkGenericNotificationTemplate buildWorkGenericNotificationTemplate(Long toId, Long workId, String message) {
		Work work = workService.findWork(workId);
		return new WorkGenericNotificationTemplate(toId, work, message);
	}

	@Override
	public WorkCreatedNotificationTemplate buildWorkCreatedNotificationTemplate(Long toId, Work work) {
		return new WorkCreatedNotificationTemplate(toId, work);
	}

	@Override
	public WorkUpdatedNotificationTemplate buildWorkUpdatedNotificationTemplate(Long toId, Work work) {
		return new WorkUpdatedNotificationTemplate(toId, work, workService.calculateDistanceToWork(toId, work));
	}

	@Override
	public WorkInvitationNotificationTemplate buildWorkInvitationNotificationTemplate(Long toId, Work work, int mandatoryRequirementsCount) {
		return new WorkInvitationNotificationTemplate(toId, work, workService.calculateDistanceToWork(toId, work), mandatoryRequirementsCount);
	}

	@Override
	public WorkBundleInvitationNotificationTemplate buildWorkBundleInvitationNotificationTemplate(Long toId, Work work, Map<String, Object> bundleData) {
		return new WorkBundleInvitationNotificationTemplate(toId, work, bundleData);
	}

	@Override
	public WorkStoppedPaymentNotificationTemplate buildWorkStoppedPaymentNotificationTemplate(Long toId, Work work, String reason) {
		return new WorkStoppedPaymentNotificationTemplate(toId, work, reason);
	}

	@Override
	public WorkReinvitedNotificationTemplate buildWorkReinvitedNotificationTemplate(Long toId, Work work) {
		return new WorkReinvitedNotificationTemplate(toId, work, workService.calculateDistanceToWork(toId, work));
	}

	@Override
	public WorkAcceptedNotificationTemplate buildWorkAcceptedNotificationTemplate(Long toId, Work work, User resource, WorkNegotiation negotiation) {
		return new WorkAcceptedNotificationTemplate(toId, work, resource, negotiation);
	}

	@Override
	public WorkBundleAcceptedNotificationTemplate buildWorkBundleAcceptedNotificationTemplate(Long toId, Work work, User resource) {
		return new WorkBundleAcceptedNotificationTemplate(toId, work, resource);
	}

	@Override
	public WorkAcceptedDetailsNotificationTemplate buildWorkAcceptedDetailsNotificationTemplate(Long toId, Work work, User resource) {
		return new WorkAcceptedDetailsNotificationTemplate(toId, work, resource);
	}

	@Override
	public WorkBundleAcceptedDetailsNotificationTemplate buildWorkBundleAcceptedDetailsNotificationTemplate(Long toId, Work work, User resource, Map<String, Object> bundleData) {
		return new WorkBundleAcceptedDetailsNotificationTemplate(toId, work, resource, bundleData);
	}

	@Override
	public WorkCompleteNotificationTemplate buildWorkCompleteNotificationTemplate(Long fromId, Long toId, Work work, PaymentSummaryDTO payment) {
		return new WorkCompleteNotificationTemplate(fromId, toId, work, payment);
	}

	@Override
	public WorkCompletedByBuyerNotificationTemplate buildWorkCompletedByBuyerNotificationTemplate(Long fromId, Long toId, Work work, Boolean fastFundsEnabled) {
		return new WorkCompletedByBuyerNotificationTemplate(fromId, toId, work, fastFundsEnabled);
	}

	@Override
	public WorkCancelledNotificationTemplate buildWorkCancelledPaidNotificationTemplate(Long toId, Work work, WorkResource resource, String message) {
		return new WorkCancelledNotificationTemplate(toId, work, resource, message);
	}

	@Override
	public WorkUnassignedNotificationTemplate buildWorkUnassignedNotificationTemplate(Long toId, Work work, WorkResource resource, String message) {
		return new WorkUnassignedNotificationTemplate(toId, work, resource, message);
	}

	@Override
	public WorkCancelledWithoutPayNotificationTemplate buildWorkCancelledWithoutPayNotificationTemplate(Long toId, Work work, WorkResource resource, String message) {
		return new WorkCancelledWithoutPayNotificationTemplate(toId, work, resource, message);
	}

	@Override
	public WorkIncompleteNotificationTemplate buildWorkIncompleteNotificationTemplate(Work work, Long toId, String message) {
		return new WorkIncompleteNotificationTemplate(toId, work, message);
	}

	@Override
	public WorkDeclinedNotificationTemplate buildWorkDeclinedNotificationTemplate(Long toId, Work work, User resource) {
		return new WorkDeclinedNotificationTemplate(toId, work, resource);
	}

	@Override
	public WorkCompletedFundsAddedNotificationTemplate buildWorkCompletedFundsAddedNotificationTemplate(Long toId, Work work, WorkResource resource, boolean hasValidTaxEntity) {
		return new WorkCompletedFundsAddedNotificationTemplate(toId, work, resource, hasValidTaxEntity);
	}

	@Override
	public WorkQuestionNotificationTemplate buildWorkQuestionNotificationTemplate(Long toId, Work work, WorkQuestionAnswerPair question) {
		Map<String, Object> props = userService.getProjectionMapById(question.getQuestionerId(), "firstName", "lastName");
		return new WorkQuestionNotificationTemplate(toId, work, question, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkQuestionAnsweredNotificationTemplate buildWorkQuestionAnsweredNotificationTemplate(Long toId, Work work, WorkQuestionAnswerPair question) {
		Map<String, Object> props = userService.getProjectionMapById(question.getAnswererId(), "firstName", "lastName");
		return new WorkQuestionAnsweredNotificationTemplate(toId, work, question, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkResourceConfirmationNotificationTemplate buildWorkResourceConfirmationNotificationTemplate(Long toId, Work work, DateRange appointment) {
		return new WorkResourceConfirmationNotificationTemplate(toId, appointment, work);
	}

	@Override
	public WorkResourceCheckInNotificationTemplate buildWorkResourceCheckInNotificationTemplate(Long toId, Work work) {
		return new WorkResourceCheckInNotificationTemplate(toId, work);
	}

	@Override
	public WorkResourceConfirmedNotificationTemplate buildWorkResourceConfirmedNotificationTemplate(Long toId, Work work, User resource) {
		return new WorkResourceConfirmedNotificationTemplate(toId, work, resource);
	}

	@Override
	public WorkResourceCheckedInNotificationTemplate buildWorkResourceCheckedInNotificationTemplate(Long toId, Work work, User resource) {
		return new WorkResourceCheckedInNotificationTemplate(toId, work, resource);
	}

	@Override
	public WorkResourceCheckedOutNotificationTemplate buildWorkResourceCheckedOutNotificationTemplate(Long toId, Work work, User resource) {
		return new WorkResourceCheckedOutNotificationTemplate(toId, work, resource);
	}

	@Override
	public AbstractWorkNotificationTemplate buildWorkNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation) {
		Long creatorId = negotiation.getNote() != null ? negotiation.getNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		String fullName = StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName"));

		return (workBundleService.isAssignmentBundle(work)) ?
				new WorkBundleNegotiationRequestedNotificationTemplate(toId, work, negotiation, fullName) :
				new WorkNegotiationRequestedNotificationTemplate(toId, work, negotiation, fullName);
	}

	@Override
	public WorkNegotiationApprovedNotificationTemplate buildWorkNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation) {
		return new WorkNegotiationApprovedNotificationTemplate(toId, work, negotiation);
	}

	@Override
	public UserGroupExpirationEmailTemplate buildExpirationNotificationTemplate(UserGroupExpiration userGroupExpiration) {
		return new UserGroupExpirationEmailTemplate(userGroupExpiration);
	}

	@Override
	public TalentPoolRequirementExpirationEmailTemplate buildTalentPoolRequirementExpirationNotificationTemplate(
		Criterion criterion,
		UserGroup userGroup,
		String expirationDate,
		String verb) {
		return new TalentPoolRequirementExpirationEmailTemplate(criterion, userGroup, expirationDate, verb);
	}

	@Override
	public NotificationTemplate buildWorkResourceInvitation(Work work, long userId, boolean isVoiceDelivery, boolean isBundle, int requirementCount) {
		Assert.notNull(work);

		AbstractWorkNotificationTemplate template;
		if (isBundle) {
			ServiceResponseBuilder serviceResponse = workBundleService.getBundleData(userService.findUserById(userId), (WorkBundle) work);
			template = buildWorkBundleInvitationNotificationTemplate(userId, work, serviceResponse.getData());
		} else {
			template = buildWorkInvitationNotificationTemplate(userId, work, requirementCount);
		}

		boolean emailDelivery, smsDelivery, userDelivery, followDelivery;
		emailDelivery = smsDelivery = userDelivery = followDelivery = !isVoiceDelivery;
		template.setEnabledDeliveryMethods(emailDelivery, smsDelivery, userDelivery, isVoiceDelivery, followDelivery);
		return template;
	}

	@Override
	public NotificationTemplate buildWorkResourceInvitation(Work work, long userId, long onBehalfOfUserId, boolean isVoiceDelivery, boolean isBundle, int requirementCount) {
		NotificationTemplate template = buildWorkResourceInvitation(work, userId, isVoiceDelivery, isBundle, requirementCount);
		template.setOnBehalfOfId(onBehalfOfUserId);
		return template;
	}

	@Override
	public NotificationTemplate buildWorkResourceInvitation(long workId, long userId, boolean isVoiceDelivery) {
		Work work = workService.findWork(workId, true);
		Assert.notNull(work);

		if (!work.isInBundle()) {
			AbstractWorkNotificationTemplate template;
			if (workBundleService.isAssignmentBundle(work)) {
				ServiceResponseBuilder serviceResponse = workBundleService.getBundleData(userId, work.getId());
				template = buildWorkBundleInvitationNotificationTemplate(userId, work, serviceResponse.getData());
			} else {
				template = buildWorkInvitationNotificationTemplate(
					userId, work, requirementSetsService.getMandatoryRequirementCountByWorkId(work.getId())
				);
			}

			boolean emailDelivery, smsDelivery, userDelivery, followDelivery;
			emailDelivery = smsDelivery = userDelivery = followDelivery = !isVoiceDelivery;
			template.setEnabledDeliveryMethods(emailDelivery, smsDelivery, userDelivery, isVoiceDelivery, followDelivery);
			return template;
		}
		return null;
	}

	@Override
	public WorkBundleNegotiationApprovedNotificationTemplate buildWorkBundleNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation, Map<String, Object> bundleData) {
		return new WorkBundleNegotiationApprovedNotificationTemplate(toId, work, negotiation, bundleData);
	}

	@Override
	public WorkBundleNegotiationApprovedNotificationTemplate buildWorkBundleNegotiationApprovedNotificationTemplate(Long toId, Long onBehalfOfUserId, Work work, WorkNegotiation negotiation, Map<String, Object> bundleData) {
		WorkBundleNegotiationApprovedNotificationTemplate template = new WorkBundleNegotiationApprovedNotificationTemplate(toId, work, negotiation, bundleData);
		template.setOnBehalfOfId(onBehalfOfUserId);
		return template;
	}

	@Override
	public NotificationTemplate buildAbstractWorkNegotiationDeclinedNotificationTemplate(Long toId, Work work, AbstractWorkNegotiation negotiation) {
		NotificationTemplate template = null;

		if (negotiation instanceof WorkNegotiation) {
			template = buildWorkNegotiationDeclinedNotificationTemplate(
				toId, work, (WorkNegotiation) negotiation
			);

		} else if (negotiation instanceof WorkBudgetNegotiation) {
			template = buildWorkBudgetNegotiationDeclinedNotificationTemplate(
				toId, work, (WorkBudgetNegotiation) negotiation
			);

		} else if (negotiation instanceof WorkExpenseNegotiation) {
			template = buildWorkExpenseNegotiationDeclinedNotificationTemplate(
				toId, work, (WorkExpenseNegotiation) negotiation
			);

		} else if (negotiation instanceof WorkBonusNegotiation) {
			template = buildWorkBonusNegotiationDeclinedNotificationTemplate(
				toId, work, (WorkBonusNegotiation) negotiation
			);

		} else if (negotiation instanceof WorkRescheduleNegotiation) {
			template = buildWorkRescheduleNegotiationDeclinedNotificationTemplate(
				toId, work, (WorkRescheduleNegotiation) negotiation
			);
		}
		return template;
	}

	@Override
	public WorkNegotiationDeclinedNotificationTemplate buildWorkNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation) {
		Long creatorId = negotiation.getDeclineNote() != null ? negotiation.getDeclineNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		return new WorkNegotiationDeclinedNotificationTemplate(toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkNegotiationExpirationExtendedNotificationTemplate buildWorkNegotiationExpirationExtendedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation) {
		return new WorkNegotiationExpirationExtendedNotificationTemplate(toId, work, negotiation);
	}

	@Override
	public WorkRescheduleNegotiationRequestedNotificationTemplate buildWorkRescheduleNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation) {
		Long creatorId = negotiation.getNote() != null ? negotiation.getNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		Long activeWorkerId = workService.findActiveWorkerId(work.getId());
		return new WorkRescheduleNegotiationRequestedNotificationTemplate(
			toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")), activeWorkerId
		);
	}

	@Override
	public WorkExpenseNegotiationRequestedNotificationTemplate buildWorkExpenseNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation) {
		Long creatorId = negotiation.getNote() != null ? negotiation.getNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		return new WorkExpenseNegotiationRequestedNotificationTemplate(toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkExpenseNegotiationAddedNotificationTemplate buildWorkExpenseNegotiationAddedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation) {
		return new WorkExpenseNegotiationAddedNotificationTemplate(toId, work, negotiation);
	}

	@Override
	public WorkExpenseNegotiationApprovedNotificationTemplate buildWorkExpenseNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation) {
		return new WorkExpenseNegotiationApprovedNotificationTemplate(toId, work, negotiation);
	}

	@Override
	public WorkExpenseNegotiationDeclinedNotificationTemplate buildWorkExpenseNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation) {
		Long creatorId = negotiation.getDeclineNote() != null ? negotiation.getDeclineNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		return new WorkExpenseNegotiationDeclinedNotificationTemplate(toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkBonusNegotiationRequestedNotificationTemplate buildWorkBonusNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation) {
		Long creatorId = negotiation.getNote() != null ? negotiation.getNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		return new WorkBonusNegotiationRequestedNotificationTemplate(toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkBonusNegotiationAddedNotificationTemplate buildWorkBonusNegotiationAddedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation) {
		return new WorkBonusNegotiationAddedNotificationTemplate(toId, work, negotiation);
	}

	@Override
	public WorkBonusNegotiationApprovedNotificationTemplate buildWorkBonusNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation) {
		return new WorkBonusNegotiationApprovedNotificationTemplate(toId, work, negotiation);
	}

	@Override
	public WorkBonusNegotiationDeclinedNotificationTemplate buildWorkBonusNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation) {
		Long creatorId = negotiation.getDeclineNote() != null ? negotiation.getDeclineNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		return new WorkBonusNegotiationDeclinedNotificationTemplate(toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkBudgetNegotiationRequestedNotificationTemplate buildWorkBudgetNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation) {
		Long creatorId = negotiation.getNote() != null ? negotiation.getNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		return new WorkBudgetNegotiationRequestedNotificationTemplate(toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkBudgetNegotiationAddedNotificationTemplate buildWorkBudgetNegotiationAddedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation) {
		return new WorkBudgetNegotiationAddedNotificationTemplate(toId, work, negotiation);
	}

	@Override
	public WorkBudgetNegotiationApprovedNotificationTemplate buildWorkBudgetNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation) {
		return new WorkBudgetNegotiationApprovedNotificationTemplate(toId, work, negotiation);
	}

	@Override
	public WorkBudgetNegotiationDeclinedNotificationTemplate buildWorkBudgetNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation) {
		Long creatorId = negotiation.getDeclineNote() != null ? negotiation.getDeclineNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		return new WorkBudgetNegotiationDeclinedNotificationTemplate(toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")));
	}

	@Override
	public WorkReportGeneratedEmailTemplate buildWorkReportGeneratedTemplate(Long reportId, String reportName, Set<String> recipients, FileDTO asset, ReportResponse response) {
		return new WorkReportGeneratedEmailTemplate(reportId, reportName, recipients, asset, response);
	}

	@Override
	public WorkReportGeneratedLargeEmailTemplate buildLargeWorkReportGeneratedTemplate(String reportKey, String reportName, Set<String> recipients, String downloadUri) {
		return new WorkReportGeneratedLargeEmailTemplate(reportKey, reportName, recipients, downloadUri);
	}

	@Override
	public WorkRescheduleNegotiationApprovedNotificationTemplate buildWorkRescheduleNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation) {
		return new WorkRescheduleNegotiationApprovedNotificationTemplate(toId, work, negotiation, workService.findActiveWorkerId(work.getId()));
	}

	@Override
	public WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate buildWorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation) {
		String fullName = userService.getFullName(negotiation.getRequestedBy().getId());
		return new WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(toId, work, negotiation, fullName);
	}

	@Override
	public WorkRescheduleNegotiationDeclinedNotificationTemplate buildWorkRescheduleNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation) {
		Long creatorId = negotiation.getDeclineNote() != null ? negotiation.getDeclineNote().getCreatorId() : negotiation.getCreatorId();
		Map<String, Object> props = userService.getProjectionMapById(creatorId, "firstName", "lastName");
		Long activeWorkerId = workService.findActiveWorkerId(work.getId());
		return new WorkRescheduleNegotiationDeclinedNotificationTemplate(
			toId, work, negotiation, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")), activeWorkerId
		);
	}

	@Override
	public Lane23AssociationCreatedNotificationTemplate buildLane23AssociationCreatedNotificationTemplate(Long toId, Company company, boolean includeWelcomeEmail) {
		return new Lane23AssociationCreatedNotificationTemplate(toId, company, includeWelcomeEmail);
	}

	@Override
	public LowBalanceEmailTemplate buildLowBalanceEmailTemplate(Long toId, String toEmail, BigDecimal spendLimit) {
		return new LowBalanceEmailTemplate(toId, toEmail, spendLimit);
	}

	@Override
	public UserGroupApprovalNotificationTemplate buildUserGroupApprovalNotificationTemplate(Long fromId, Long toId, UserGroup group) {
		Assert.notNull(group);
		return new UserGroupApprovalNotificationTemplate(fromId, toId, group, group.getCompany());
	}

	@Override
	public UserGroupDeclineNotificationTemplate buildUserGroupDeclineNotificationTemplate(Long fromId, Long toId, UserGroup group) {
		Assert.notNull(group);
		return new UserGroupDeclineNotificationTemplate(fromId, toId, group, group.getCompany());
	}

	@Override
	public UserGroupApplicationNotificationTemplate buildUserGroupApplicationNotificationTemplate(Long toId, UserGroup group, User resource, boolean overrideRequested) {
		Assert.notNull(group);
		return new UserGroupApplicationNotificationTemplate(toId, group, resource, overrideRequested);
	}

	@Override
	public UserGroupPrivateApplicationNotificationTemplate buildUserGroupPrivateApplicationNotificationTemplate(Long toId, UserGroup group, User resource) {
		Assert.notNull(group);
		return new UserGroupPrivateApplicationNotificationTemplate(toId, group, resource);
	}

	@Override
	public UserGroupInvitationNotificationTemplate buildUserGroupInvitationNotificationTemplate(Long fromId, Long toId, UserGroup group, boolean vendorInvitation) {
		Assert.notNull(group);
		return new UserGroupInvitationNotificationTemplate(fromId, toId, group, group.getCompany(), vendorInvitation);
	}

	@Override
	public UserGroupMessageNotificationTemplate buildUserGroupMessage(Long fromId, Long toId, String message, String title, UserGroup group) {
		Assert.notNull(group);
		return new UserGroupMessageNotificationTemplate(fromId, toId, message, title, group, group.getCompany());
	}

	@Override
	public UserGroupRequirementsModificationNotificationTemplate buildUserGroupRequirementsModificationNotificationTemplate(Long fromId, Long toId, UserGroup group) {
		Assert.notNull(group);
		return new UserGroupRequirementsModificationNotificationTemplate(fromId, toId, group, group.getCompany());
	}

	@Override
	public UserGroupRequirementsExpirationNotificationTemplate buildUserGroupRequirementsExpirationNotificationTemplate(Long fromId, Long toId, UserGroup group) {
		Assert.notNull(group);
		return new UserGroupRequirementsExpirationNotificationTemplate(fromId, toId, group, group.getCompany());
	}

	@Override
	public UserGroupInvitationForUserProfileModificationNotificationTemplate buildUserGroupInvitationForUserProfileModificationNotificationTemplate(Long fromId, Long toId, UserGroup group) {
		Assert.notNull(group);
		return new UserGroupInvitationForUserProfileModificationNotificationTemplate(fromId, toId, group, group.getCompany());
	}

	@Override
	public UserGroupInvitationForUserProfileModificationOwnerNotificationTemplate buildUserGroupInvitationForUserProfileModificationOwnerNotificationTemplate(Long toId, UserGroup group, User resource) {
		Assert.notNull(group);
		return new UserGroupInvitationForUserProfileModificationOwnerNotificationTemplate(toId, group, resource);
	}

	@Override
	public NotificationTemplate buildAssessmentInvitationEmailTemplate(Long fromId, AbstractAssessment assessment, Long toUserId) {
		return (AbstractAssessment.SURVEY_ASSESSMENT_TYPE.equals(assessment.getType())) ?
				new SurveyInvitationNotificationTemplate(fromId, assessment, toUserId) :
				new AssessmentInvitationNotificationTemplate(fromId, assessment, toUserId);
	}

	@Override
	public MultipleAssessmentInvitationsNotificationTemplate buildMultipleAssessmentInvitationsNotificationTemplate(Long fromId, List<AbstractAssessment> assessments, Long toUserId) {
		return new MultipleAssessmentInvitationsNotificationTemplate(fromId, assessments, toUserId);
	}

	@Override
	public AssessmentCompletedNotificationTemplate buildAssessmentCompletedNotificationTemplate(Long fromId, Long toId, Attempt attempt, AbstractAssessment assessment) {
		return new AssessmentCompletedNotificationTemplate(fromId, toId, attempt, assessment);
	}

	@Override
	public AssessmentGradedNotificationTemplate buildAssessmentGradedNotificationTemplate(Long fromId, Long toId, Attempt attempt) {
		return new AssessmentGradedNotificationTemplate(fromId, toId, attempt);
	}

	@Override
	public AssessmentGradePendingNotificationTemplate buildAssessmentGradePendingNotificationTemplate(Long fromId, Long toId, Attempt attempt) {
		return new AssessmentGradePendingNotificationTemplate(fromId, toId, attempt);
	}

	@Override
	public WorkRatingCreatedNotificationTemplate buildWorkRatingCreatedNotificationTemplate(Long toId, Rating rating) {
		return new WorkRatingCreatedNotificationTemplate(toId, rating);
	}

	@Override
	public DrugTestPassedNotificationTemplate buildDrugTestPassedNotificationTemplate(Long toUserId, Screening screening) {
		return new DrugTestPassedNotificationTemplate(toUserId, screening);
	}

	@Override
	public DrugTestFailedNotificationTemplate buildDrugTestFailedNotificationTemplate(Long toUserId, Screening screening) {
		return new DrugTestFailedNotificationTemplate(toUserId, screening);
	}

	@Override
	public BackgroundCheckPassedNotificationTemplate buildBackgroundCheckPassedNotificationTemplate(Long toUserId, Screening screening) {
		return new BackgroundCheckPassedNotificationTemplate(toUserId, screening);
	}

	@Override
	public BackgroundCheckFailedNotificationTemplate buildBackgroundCheckFailedNotificationTemplate(Long toUserId, Screening screening) {
		return new BackgroundCheckFailedNotificationTemplate(toUserId, screening);
	}

	@Override
	public NotificationTemplate buildFundsDepositNotificationTemplate(Long toUserId, RegisterTransaction transaction, Integer invoicesDueCount, BigDecimal invoicesDueTotal, Calendar invoicesDueDate) {
		if (transaction instanceof BankAccountTransaction) {
			BankAccountTransaction bankTransaction = (BankAccountTransaction) accountRegisterService.findRegisterTransaction(transaction.getId());
			if (BankAccountTransactionStatus.SUBMITTED.equals(bankTransaction.getBankAccountTransactionStatus().getCode())) {
				return new FundsPendingNotificationTemplate(toUserId, transaction, invoicesDueCount, invoicesDueTotal, invoicesDueDate);
			}
		}
		if (RegisterTransactionType.CREDIT_ACH_WITHDRAWABLE_RETURN.equals(transaction.getRegisterTransactionType().getCode())) {
			return new FundsWithdrawReturnNotificationTemplate(toUserId, transaction);
		}
		return new FundsDepositedNotificationTemplate(toUserId, transaction, invoicesDueCount, invoicesDueTotal, invoicesDueDate);
	}

	@Override
	public <T extends AbstractBankAccount> NotificationTemplate buildFundsWithdrawnNotificationTemplate(Long toUserId, RegisterTransaction transaction, T bankAccount) {
		if (bankAccount instanceof GlobalCashCardAccount) {
			return new FundsWithdrawnFromGCCNotificationTemplate(toUserId, transaction);
		} else if (bankAccount instanceof PayPalAccount) {
			return new FundsWithdrawnFromPaypalNotificationTemplate(toUserId, transaction);
		} else {
			return new FundsWithdrawnNotificationTemplate(toUserId, transaction);
		}
	}

	@Override
	public FundsProcessedNotificationTemplate buildFundsProcessedNotificationTemplate(Long toUserId, RegisterTransaction transaction) {
		return new FundsProcessedNotificationTemplate(toUserId, transaction);
	}

	@Override
	public FundsDepositReturnNotificationTemplate buildFundsDepositReturnNotificationTemplate(long toUserId, RegisterTransaction transaction) {
		return new FundsDepositReturnNotificationTemplate(toUserId, transaction);
	}

	@Override
	public WorkResourceCancelledNotificationTemplate buildWorkResourceCancelledNotificationTemplate(WorkResource resource, Long toId) {
		return (toId == null) ?
				new WorkResourceCancelledNotificationTemplate(resource) :
				new WorkResourceCancelledNotificationTemplate(resource, toId);
	}

	@Override
	public WorkAttachmentAddedNotificationTemplate buildWorkAttachmentAddedNotificationTemplate(Long toId, Work work, Asset asset) {
		return new WorkAttachmentAddedNotificationTemplate(toId, work, asset, workService.findActiveWorkerId(work.getId()));
	}

	@Override
	public WorkNoteAddedNotificationTemplate buildWorkNoteAddedNotificationTemplate(Long toId, Work work, Note note, String type) {
		Map<String, Object> props = userService.getProjectionMapById(note.getCreatorId(), "firstName", "lastName");
		Long activeWorkerId = workService.findActiveWorkerId(work.getId());
		return new WorkNoteAddedNotificationTemplate(
			toId,
			work,
			note,
			StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")),
			activeWorkerId,
			type);
	}

	@Override
	public WorkDeliverableRejectedNotificationTemplate buildWorkDeliverableRejectedNotificationTemplate(Long toId, Work work, String assetName, String rejectionReason) {
		return new WorkDeliverableRejectedNotificationTemplate(toId, work, assetName, rejectionReason);
	}

	@Override
	public WorkDeliverableLateNotificationTemplate buildWorkDeliverableLateNotificationTemplate(Long toId, Work work) {
		return new WorkDeliverableLateNotificationTemplate(toId, work);
	}

	@Override
	public WorkDeliverableDueReminderNotificationTemplate buildWorkDeliverableDueReminderNotificationTemplate(Long toId, Work work) {
		return new WorkDeliverableDueReminderNotificationTemplate(toId, work);
	}

	@Override
	public WorkDeliverableFulfilledNotificationTemplate buildWorkDeliverableFulfilledNotificationTemplate(Long toId, Work work) {
		return new WorkDeliverableFulfilledNotificationTemplate(toId, work);
	}

	@Override
	public InvoiceDueNotificationTemplate buildInvoiceDueNotificationTemplate(Long toId, List<Invoice> invoices, Map<Long, WorkDue> invoiceAssignments) {
		return new InvoiceDueNotificationTemplate(toId, invoices, invoiceAssignments, new NotificationType(NotificationType.INVOICE_DUE_3_DAYS), false);
	}

	@Override
	public InvoiceDueNotificationTemplate buildOwnerInvoiceDueNotificationTemplate(Long toId, List<Invoice> invoices, Map<Long, WorkDue> invoiceAssignments) {
		return new InvoiceDueNotificationTemplate(toId, invoices, invoiceAssignments, new NotificationType(NotificationType.MY_INVOICES_DUE_3_DAYS), true);
	}

/*{ LockedCompanyAccount */

	@Override
	public LockedCompanyAccountNotificationTemplate buildLockedCompanyAccountNotificationTemplate(Long toId, BigDecimal pastDuePayables) {
		return new LockedCompanyAccountNotificationTemplate(toId, pastDuePayables);
	}

	@Override
	public LockedCompanyAccount24HrsWarningNotificationTemplate buildLockedCompanyAccount24HrsWarningNotificationTemplate(Long toId, BigDecimal upcomingDuePayables) {
		return new LockedCompanyAccount24HrsWarningNotificationTemplate(toId, upcomingDuePayables, false);
	}

	@Override
	public LockedCompanyAccount24HrsWarningNotificationTemplate buildInvoiceDue24HoursNotificationTemplate(Long toId, BigDecimal invoiceAmountDue) {
		return new LockedCompanyAccount24HrsWarningNotificationTemplate(toId, invoiceAmountDue, true);
	}

	@Override
	public LockedCompanyAccountOverdueWarningNotificationTemplate buildLockedCompanyAccountOverdueWarningEmailTemplate(Long toId, Integer daysSinceOverdue, Integer daysTillSuspended, BigDecimal pastDuePayables) {
		return new LockedCompanyAccountOverdueWarningNotificationTemplate(toId, daysSinceOverdue, daysTillSuspended, pastDuePayables);
	}

/*} LockedCompanyAccount */

	@Override
	public NotificationTemplate buildWelcomeNotificationTemplate(User user) {

		Company company = companyService.findCompanyById(user.getCompany().getId());

		if (company.isResourceAccount()) {
			Invitation invitation = user.getInvitation();
			RecruitingCampaign campaign = user.getRecruitingCampaign();
			if ((invitation != null && invitation.getInvitationType() == InvitationType.EXCLUSIVE) ||
				(campaign != null && campaign.isPrivateCampaign())) {
				String companyName = invitation != null ? invitation.getCompany().getEffectiveName() : campaign.getCompany().getEffectiveName();
				Long companyId = invitation != null ? invitation.getCompany().getId() : campaign.getCompany().getId();
				Admission admission = admissionDAO.findBy("venue", Venue.OFFLINE_PAY, "keyName", "companyId", "value", companyId.toString(), "deleted", Boolean.FALSE);
				return new WelcomeExclusiveWorkerNotificationTemplate(user.getId(), companyName, admission != null);
			}
			return new WelcomeResourceNotificationTemplate(user.getId());
		}
		if (userRoleService.hasAnyAclRole(user, AclRole.ACL_EMPLOYEE_WORKER)) {
			String companyName = user.getCompany().getEffectiveName();
			return new WelcomeEmployeeWorkerNotificationTemplate(user.getId(), companyName);
		} else {
			return new WelcomeNotificationTemplate(user.getId());
		}
	}

	@Override
	public WorkAppointmentNotificationTemplate buildWorkAppointmentNotificationTemplate(Long toId, Work work, DateRange appointment) {
		return new WorkAppointmentNotificationTemplate(toId, work, appointment, workService.findActiveWorkerId(work.getId()));
	}

	@Override
	public WorkAppointmentNotificationTemplate buildWorkAppointmentNotificationTemplate(Long toId, Long onBehalfOfId, Work work, DateRange appointment) {
		WorkAppointmentNotificationTemplate template = new WorkAppointmentNotificationTemplate(toId, work, appointment, workService.findActiveWorkerId(work.getId()));
		template.setOnBehalfOfId(onBehalfOfId);
		return template;
	}

	@Override
	public WorkRemindResourceToCompleteNotificationTemplate buildWorkRemindResourceToComplete(Long toId, Long fromId, Work work, Note note) {
		Map<String, Object> props = userService.getProjectionMapById(note.getCreatorId(), "firstName", "lastName");
		return new WorkRemindResourceToCompleteNotificationTemplate(
			fromId, toId, work, note, StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName"))
		);
	}

	@Override
	public NotificationTemplate buildOverridePaymentTermsNotificationTemplate(Long toId, Company company, String note) {
		return new OverridePaymentTermsNotificationTemplate(toId, company, note);
	}

	@Override
	public NotificationTemplate buildAssetBundleAvailableNotificationTemplate(Long toId, String downloadUri, Calendar expiration) {
		return new AssetBundleAvailableNotificationTemplate(toId, downloadUri, expiration);
	}

	@Override
	public <T extends AbstractInvoice> NotificationTemplate buildNewInvoiceNotificationTemplate(long toId, T invoice) {
		if (invoice instanceof Statement) {
			return new StatementReminderNotificationTemplate(toId, (Statement) invoice);
		}
		if (invoice instanceof SubscriptionInvoice) {
			((SubscriptionPaymentPeriod) ((SubscriptionInvoice) invoice).getPaymentPeriod()).getSubscriptionConfiguration().getSubscriptionCancellation();
			NotificationTemplate template = new SubscriptionInvoiceNotificationTemplate(toId, (SubscriptionInvoice) invoice);
			template.setPdfTemplate(PDFTemplateFactory.newServiceInvoicePDFTemplate((SubscriptionInvoice) invoice));
			return template;
		}
		if (invoice instanceof AdHocInvoice) {
			NotificationTemplate template = new AdHocInvoiceNotificationTemplate(toId, (AdHocInvoice) invoice);
			template.setPdfTemplate(PDFTemplateFactory.newServiceInvoicePDFTemplate((AdHocInvoice) invoice));
			return template;
		}
		return null;
	}

	@Override
	public List<BlockCompanyNotificationTemplate> buildBlockCompanyNotificationTemplates(UserBlockCompanyEvent event) {
		List<BlockCompanyNotificationTemplate> templates = Lists.newArrayList();
		User blockingUser = userService.getUser(event.getUserId());
		Company company = companyService.findCompanyById(event.getCompanyId());
		if (blockingUser == null || company == null) {
			return templates;
		}
		for (User admin : authenticationService.findAllUsersByACLRoleAndCompany(blockingUser.getCompany().getId(), AclRole.ACL_ADMIN)) {
			templates.add(new BlockCompanyNotificationTemplate(admin.getId(), blockingUser, company));
		}
		return templates;
	}

	@Override
	public WorkSurveyCompletedNotificationTemplate buildWorkSurveyCompletedNotificationTemplate(Long fromId, Long toId, WorkScopedAttempt attempt, Work work) {
		return new WorkSurveyCompletedNotificationTemplate(fromId, toId, attempt, work);
	}

	@Override
	public SurveyCompletedNotificationTemplate buildSurveyCompletedNotificationTemplate(Long fromId, Long toId, Attempt attempt) {
		return new SurveyCompletedNotificationTemplate(fromId, toId, attempt);
	}

	@Override
	public WorkSubStatusFailedConfirmationNotificationTemplate buildWorkSubStatusFailedConfirmationNotificationTemplate(WorkResource resource, Long toId, Work work, DateRange appointment) {
		return resource == null ?
				new WorkSubStatusFailedConfirmationNotificationTemplate(work, toId, appointment) :
				new WorkSubStatusFailedConfirmationNotificationTemplate(work, resource, toId, appointment);
	}

	@Override
	public WorkNotAvailableNotificationTemplate buildWorkNotAvailableNotificationTemplate(Long toId, Work work) {
		return new WorkNotAvailableNotificationTemplate(toId, work);
	}

	@Override
	public WorkNotAvailableNotificationTemplate buildWorkNotAvailableNotificationTemplate(Long toId, Long onBehalfOfUserId, Work work) {
		WorkNotAvailableNotificationTemplate template = new WorkNotAvailableNotificationTemplate(toId, work);
		template.setOnBehalfOfId(onBehalfOfUserId);
		return template;
	}

	@Override
	public SubscriptionEffectiveNotificationTemplate buildSubscriptionEffectiveNotificationTemplate(long toId, SubscriptionConfiguration configuration, String timeZoneId) {
		SubscriptionEffectiveNotificationTemplate template = new SubscriptionEffectiveNotificationTemplate(toId, configuration);
		template.setTimeZoneId(timeZoneId);
		return template;
	}

	@Override
	public SubscriptionCancelledNotificationTemplate buildSubscriptionCancelledNotificationTemplate(long toId, SubscriptionConfiguration configuration, String timeZoneId) {
		SubscriptionCancelledNotificationTemplate template = new SubscriptionCancelledNotificationTemplate(toId, configuration);
		template.setTimeZoneId(timeZoneId);
		return template;
	}

	@Override
	public SubscriptionPaymentTierThroughputReachedNotificationTemplate buildSubscriptionPaymentTierThroughputReached(Long toId, SubscriptionConfiguration configuration, SubscriptionPaymentTier activeSubscriptionPaymentTier, BigDecimal throughput, String timeZoneId) {
		SubscriptionPaymentTierThroughputReachedNotificationTemplate template = new SubscriptionPaymentTierThroughputReachedNotificationTemplate(toId, configuration, activeSubscriptionPaymentTier, throughput);
		template.setTimeZoneId(timeZoneId);
		return template;
	}

	@Override
	public TaxVerificationNotificationTemplate buildTaxEntityNotificationTemplate(AbstractTaxEntity entity) {
		User toUser = MoreObjects.firstNonNull(entity.getSignedBy(), entity.getCompany().getCreatedBy());
		return new TaxVerificationNotificationTemplate(toUser, entity);
	}


	@Override
	public TaxReportGeneratedNotificationTemplate buildTaxReportGeneratedNotificationTemplate(long toId) {
		return new TaxReportGeneratedNotificationTemplate(toId);
	}

	@Override
	public <T extends AbstractTaxReport> List<NotificationTemplate> buildTaxReportAvailableNotificationTemplates(T taxReport) {
		List<NotificationTemplate> templates = Lists.newArrayList();

		if (taxReport instanceof TaxForm1099) {
			for (User u : authenticationService.findAllUsersByACLRoleAndCompany(taxReport.getCompanyId(), AclRole.ACL_ADMIN)) {
				templates.add(new TaxForm1099AvailableNotificationTemplate(u.getId(), (TaxForm1099) taxReport));
			}
		} else if (taxReport instanceof EarningReport) {
			for (User u : authenticationService.findAllUsersByACLRoleAndCompany(taxReport.getCompanyId(), AclRole.ACL_ADMIN)) {
				templates.add(new EarningReportAvailableNotificationTemplate(u.getId(), (EarningReport) taxReport));
			}
		}
		return templates;
	}

	@Override
	public NotificationTemplate buildCreditCardReceiptNotificationTemplate(Long toUserId, CreditCardTransaction creditCardTransaction) {
		return new CreditCardReceiptNotificationTemplate(toUserId, creditCardTransaction);
	}

	@Override
	public BatchEvidenceReportTemplate buildBatchEvidenceReportTemplate(String toEmail, PDFTemplate pdfTemplate) {
		return new BatchEvidenceReportTemplate(toEmail, pdfTemplate);
	}

	@Override
	public EvidenceReportCSVTemplate buildEvidenceReportCSVTemplate(String toEmail, FileDTO fileDTO) {
		EvidenceReportCSVTemplate template = new EvidenceReportCSVTemplate(toEmail);
		template.addAttachment(fileDTO);
		return template;
	}

	@Override
	public SearchCSVGeneratedTemplate buildSearchCSVGeneratedTemplate(String toEmail, FileDTO fileDTO) {
		return new SearchCSVGeneratedTemplate(toEmail, fileDTO);
	}

	@Override
	public SearchCSVGeneratedLargeEmailTemplate buildSearchCSVGeneratedLargeEmailTemplate(String downloadUri, String recipient) {
		return new SearchCSVGeneratedLargeEmailTemplate(downloadUri, recipient);
	}

	@Override
	public DocumentationPackageNotificationTemplate buildDocumentationPackageNotificationTemplate(Long downloaderId, String uri) {
		return new DocumentationPackageNotificationTemplate(downloaderId, uri);
	}

	@Override
	public NotificationTemplate buildForumCommentAddedNotificationTemplate(final Long toId, final ForumPost post, final ForumPost parent) {
		Assert.notNull(toId);
		Assert.notNull(post);
		Assert.notNull(parent);

		final Map<String, Object> props = userService.getProjectionMapById(post.getCreatorId(), "firstName", "lastName");
		Assert.notNull(props);
		final String fullName = StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName"));

		if (experimentPercentageEvaluator.shouldRunExperiment("notification-json-ForumCommentAddedNotificationTemplate")) {
			final Map<String, Object> postMap = post.toStringObjectMap();
			final Map<String, Object> parentMap = parent.toStringObjectMap();

			return new ForumCommentAddedNotificationTemplateWithJSONObjects(toId, postMap, parentMap, fullName);
		} else {
			return new ForumCommentAddedNotificationTemplateWithJavaObjects(toId, post, parent, fullName);
		}
	}

	@Override
	public NotificationTemplate buildForumCommentAddedPostForCreatorNotificationTemplate(final Long toId, final ForumPost post, final ForumPost parent) {
		Assert.notNull(toId);
		Assert.notNull(post);
		Assert.notNull(parent);

		Map<String, Object> props = userService.getProjectionMapById(post.getCreatorId(), "firstName", "lastName");
		Assert.notNull(props);
		final String fullName = StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName"));

		if (experimentPercentageEvaluator.shouldRunExperiment("notification-json-ForumCommentAddedPostForCreatorNotificationTemplate")) {
			final Map<String, Object> postMap = post.toStringObjectMap();
			final Map<String, Object> parentMap = parent.toStringObjectMap();
			return new ForumCommentAddedPostForCreatorNotificationTemplateWithJSONObjects(toId, postMap, parentMap, fullName);
		} else {
			return new ForumCommentAddedPostForCreatorNotificationTemplateWithJavaObjects(toId, post, parent, fullName);
		}
	}

	@Override
	public BulkUploadFinishedNotificationTemplate buildBulkUploadFinishedNotificationTemplate(Long toId, List<String> workNumbers, List<Long> failedRows) {
		return new BulkUploadFinishedNotificationTemplate(toId, workNumbers, failedRows);
	}

	@Override
	public BulkUploadFailedNotificationTemplate buildBulkUploadFailedNotificationTemplate(Long toId, List<WorkUpload> errorUploads) {
		return new BulkUploadFailedNotificationTemplate(toId, errorUploads);
	}

	@Override
	 public BundleWorkAcceptFailedNotificationTemplate buildBundleWorkAcceptFailedNotificationTemplate(Long toId, User worker, Work work, AcceptWorkResponse failure) {
		return new BundleWorkAcceptFailedNotificationTemplate(toId, worker, work, failure);
	}

	@Override
	public BulkUserUploadFinishedNotificationTemplate buildBulkUserUploadFinishedNotificationTemplate(Long toId, BulkUserUploadResponse response) {
		return new BulkUserUploadFinishedNotificationTemplate(toId, response);
	}

	@Override
	public BulkUserUploadFailedNotificationTemplate buildBulkUserUploadFailedNotificationTemplate(Long toId, BulkUserUploadResponse response, boolean showError) {
		return new BulkUserUploadFailedNotificationTemplate(toId, response, showError);
	}

	@Override
	public BulkWorkRepriceResultNotificationTemplate buildBulkWorkRepricResultNotificationTemplate(Long toId, int succeeded, int failed) {
		return new BulkWorkRepriceResultNotificationTemplate(toId, succeeded, failed);
	}

}
