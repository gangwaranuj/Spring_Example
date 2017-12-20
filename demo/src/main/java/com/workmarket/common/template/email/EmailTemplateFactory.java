package com.workmarket.common.template.email;

import com.workmarket.common.template.RecruitingCampaignInvitationEmailTemplate;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.request.PasswordResetRequest;

public interface EmailTemplateFactory {

	RegistrationRemindUserEmailTemplate buildRegistrationRemindUserEmailTemplate(Long toId);

	RegistrationConfirmEmailTemplate buildRegistrationConfirmUserEmailTemplate(Long toId, String toEmail);

	RegistrationConfirmEmailTemplate buildRegistrationConfirmUserEmailTemplate(Long toId);

	RegistrationConfirmWithPasswordResetEmailTemplate buildRegistrationConfirmWithPasswordResetEmailTemplate(Long fromId, Long toId, PasswordResetRequest request);

	RegistrationRemindConfirmationWithPasswordResetEmailTemplate buildRegistrationRemindConfirmationWithPasswordResetEmailTemplate(Long fromId, Long toId, PasswordResetRequest request);

	RegistrationInviteUserEmailTemplate buildRegistrationInviteUserEmailTemplate(Long fromId, String email, Invitation invitation);

	RegistrationRemindInviteExistingUserEmailTemplate buildRegistrationRemindInviteExistingUserEmailTemplate(Long fromId, String email, Invitation invitation);

	RegistrationRemindInviteUserEmailTemplate buildRegistrationRemindInviteUserEmailTemplate(Long fromId, String email, Invitation invitation);

	RegistrationRemindPasswordEmailTemplate buildRegistrationRemindPasswordEmailTemplate(Long toId, PasswordResetRequest request);

	RatingFlaggedClientServicesEmailTemplate buildRatingFlaggedClientServicesEmailTemplate(Long fromId, Rating rating);

	LockedCompanyAccountClientServicesEmailTemplate buildLockedCompanyAccountClientServicesEmailTemplate(Company company);

	RegistrationInviteExistingUserEmailTemplate buildRegistrationExistingInviteUserEmailTemplate(Long fromId, String email, Invitation invitation);

	RecruitingCampaignInvitationEmailTemplate buildRecruitingCampaignInvitationEmailTemplate(Long fromId, String email, RecruitingCampaign campaign, User invitedBy, String inviteeFirstName);

	<T extends AbstractInvoice> EmailTemplate buildInvoiceEmailTemplate(String toEmail, T invoice);

	EmailTemplate buildInvoiceDetailEmailTemplate(String toEmail, Invoice invoice, AccountStatementDetailRow invoiceDetail);

	AssignmentAgingEmailTemplate buildAssignmentAgingNotificationTemplate(Long userId, Long companyId, Email toEmail);

	GlobalCashCardCreatedEmailTemplate buildGlobalCashCardCreatedTemplate(Long toId);

	GlobalCashCardActivatedEmailTemplate buildGlobalCashCardActivatedTemplate(Long toId);

	GCCAdminNotificationEmailTemplate buildGCCAdminNotification(String toEmail, String message);

	ProfileUpdateEmailTemplate buildProfileUpdateEmailTemplate(Long toId, String field);

}
