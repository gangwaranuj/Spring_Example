package com.workmarket.common.template.email;

import com.google.common.collect.Lists;
import com.workmarket.common.template.RecruitingCampaignInvitationEmailTemplate;
import com.workmarket.common.template.pdf.PDFTemplateFactory;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class EmailTemplateFactoryImpl implements EmailTemplateFactory {

	@Autowired private PDFTemplateFactory PDFTemplateFactory;
	@Autowired private WorkReportService workReportService;
	@Autowired private UserService userService;
	@Autowired private BillingService billingService;

	public RegistrationRemindUserEmailTemplate buildRegistrationRemindUserEmailTemplate(Long toId) {
		return new RegistrationRemindUserEmailTemplate(toId);
	}

	@Override
	public RegistrationConfirmEmailTemplate buildRegistrationConfirmUserEmailTemplate(Long toId, String toEmail) {
		return new RegistrationConfirmEmailTemplate(toId, toEmail);
	}

	@Override
	public RegistrationConfirmEmailTemplate buildRegistrationConfirmUserEmailTemplate(Long toId) {
		return new RegistrationConfirmEmailTemplate(toId);
	}

	@Override
	public RegistrationConfirmWithPasswordResetEmailTemplate buildRegistrationConfirmWithPasswordResetEmailTemplate(Long fromId, Long toId, PasswordResetRequest request) {
		return new RegistrationConfirmWithPasswordResetEmailTemplate(fromId, toId, request);
	}

	@Override
	public RegistrationRemindConfirmationWithPasswordResetEmailTemplate buildRegistrationRemindConfirmationWithPasswordResetEmailTemplate(Long fromId, Long toId, PasswordResetRequest request) {
		return new RegistrationRemindConfirmationWithPasswordResetEmailTemplate(fromId, toId, request);
	}

	@Override
	public RegistrationInviteUserEmailTemplate buildRegistrationInviteUserEmailTemplate(Long fromId, String email, Invitation invitation) {
		return new RegistrationInviteUserEmailTemplate(fromId, email, invitation);
	}

	@Override
	public RegistrationInviteExistingUserEmailTemplate buildRegistrationExistingInviteUserEmailTemplate(Long fromId, String email, Invitation invitation) {
		return new RegistrationInviteExistingUserEmailTemplate(fromId, email, invitation);
	}

	@Override
	public RecruitingCampaignInvitationEmailTemplate buildRecruitingCampaignInvitationEmailTemplate(Long fromId, String email, RecruitingCampaign campaign, User invitedBy, String inviteeFirstName) {
		return new RecruitingCampaignInvitationEmailTemplate(fromId, email, campaign, invitedBy, inviteeFirstName);
	}

	@Override
	public RegistrationRemindInviteExistingUserEmailTemplate buildRegistrationRemindInviteExistingUserEmailTemplate(Long fromId, String email, Invitation invitation) {
		return new RegistrationRemindInviteExistingUserEmailTemplate(fromId, email, invitation);
	}

	@Override
	public RegistrationRemindInviteUserEmailTemplate buildRegistrationRemindInviteUserEmailTemplate(Long fromId, String email, Invitation invitation) {
		return new RegistrationRemindInviteUserEmailTemplate(fromId, email, invitation);
	}

	@Override
	public RegistrationRemindPasswordEmailTemplate buildRegistrationRemindPasswordEmailTemplate(Long toId, PasswordResetRequest request) {
		return new RegistrationRemindPasswordEmailTemplate(toId, request);
	}

	@Override
	public RatingFlaggedClientServicesEmailTemplate buildRatingFlaggedClientServicesEmailTemplate(Long fromId, Rating rating) {
		Map<String, Object> props = userService.getProjectionMapById(rating.getCreatorId(), "firstName", "lastName", "userNumber");

		return new RatingFlaggedClientServicesEmailTemplate(fromId, rating,
				StringUtilities.fullName((String) props.get("firstName"), (String) props.get("lastName")), (String) props.get("userNumber"));
	}

	@Override
	public LockedCompanyAccountClientServicesEmailTemplate buildLockedCompanyAccountClientServicesEmailTemplate(Company company) {
		return new LockedCompanyAccountClientServicesEmailTemplate(company);
	}

	@Override
	public <T extends AbstractInvoice> EmailTemplate buildInvoiceEmailTemplate(String toEmail, T invoice) {
		Assert.hasText(toEmail);
		EmailTemplate template;

		if (invoice instanceof SubscriptionInvoice) {
			template = new SubscriptionInvoiceEmailTemplate(toEmail, (SubscriptionInvoice)invoice);
			template.setPdfTemplate(PDFTemplateFactory.newServiceInvoicePDFTemplate((SubscriptionInvoice) invoice));
		} else if (invoice instanceof AdHocInvoice) {
			template = new AdHocInvoiceEmailTemplate(toEmail, (AdHocInvoice)invoice);
			template.setPdfTemplate(PDFTemplateFactory.newServiceInvoicePDFTemplate((AdHocInvoice) invoice));
		} else {
			template = new InvoiceEmailTemplate(toEmail,(Invoice)invoice, billingService.findAccountStatementDetailByInvoiceId(invoice.getId()));
		}
		return template;
	}

	@Override
	public EmailTemplate buildInvoiceDetailEmailTemplate(String toEmail, Invoice invoice, AccountStatementDetailRow invoiceDetail) {
		InvoiceEmailTemplate template = new InvoiceEmailTemplate(toEmail, invoice, invoiceDetail);
		template.setPdfTemplate(PDFTemplateFactory.newAssignmentInvoicePDFTemplate(invoiceDetail));
		return template;
	}

	@Override
	public AssignmentAgingEmailTemplate buildAssignmentAgingNotificationTemplate(Long userId, Long companyId, Email toEmail) {
		List<SolrWorkData> oldAssignments = Lists.newArrayList();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, Constants.ASSIGNMENT_AGE_ALERT_DAYS * -1);

		WorkSearchDataPagination pagination = new WorkSearchDataPagination();
		pagination.setSortColumn(WorkSearchDataPagination.SORTS.SENT_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);
		pagination.setReturnAllRows(true);
		pagination.setShowAllCompanyAssignments(true);
		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.SENT_DATE_TO, DateUtilities.getISO8601(calendar));

		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.SENT);
		oldAssignments.addAll(workReportService.generateWorkDashboardReportBuyer(companyId, userId, pagination).getResults());

		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.ACTIVE);
		oldAssignments.addAll(workReportService.generateWorkDashboardReportBuyer(companyId, userId, pagination).getResults());

		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.INPROGRESS);
		oldAssignments.addAll(workReportService.generateWorkDashboardReportBuyer(companyId, userId, pagination).getResults());

		if (isNotEmpty(oldAssignments)) {
			CustomFieldReportFilters customFieldReportFilters = new CustomFieldReportFilters();
			customFieldReportFilters.setVisibleToBuyer(true);
			customFieldReportFilters.setShowOnEmail(true);
			return new AssignmentAgingEmailTemplate(toEmail.getEmail(), workReportService.addCustomFields(userId, companyId, oldAssignments, customFieldReportFilters));
		}
		return null;
	}

	@Override
	public GlobalCashCardCreatedEmailTemplate buildGlobalCashCardCreatedTemplate(Long toId) {
		return new GlobalCashCardCreatedEmailTemplate(toId);
	}

	@Override
	public GlobalCashCardActivatedEmailTemplate buildGlobalCashCardActivatedTemplate(Long toId) {
		return new GlobalCashCardActivatedEmailTemplate(toId);
	}

	@Override
	public GCCAdminNotificationEmailTemplate buildGCCAdminNotification(String toEmail, String message) {
		Assert.hasText(toEmail);
		return new GCCAdminNotificationEmailTemplate(toEmail,message);
	}

	@Override
	public ProfileUpdateEmailTemplate buildProfileUpdateEmailTemplate(final Long toId, final String field) {
		return new ProfileUpdateEmailTemplate(toId, field);
	}
}
