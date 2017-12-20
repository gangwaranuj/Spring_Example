package com.workmarket.service.business.account;

import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.domains.payments.dao.InvoiceDAO;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.wrapper.InvoiceResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by nick on 4/16/13 5:45 PM
 */
@Service
public class InvoiceNotificationServiceImpl implements InvoiceNotificationService {

	private static final Log logger = LogFactory.getLog(InvoiceNotificationServiceImpl.class);
	@Autowired private AuthenticationService authenticationService;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private InvoiceDAO invoiceDAO;
	@Autowired private EmailTemplateFactory emailTemplateFactory;

	@Override
	public InvoiceResponse sendInvoicePdfToAutoInvoiceEnabledUsersForWork(Work work, AccountStatementDetailRow invoiceDetail) {
		checkNotNull(work);
		checkNotNull(invoiceDetail);

		String toEmail = work.getCompany().getInvoiceSentToEmail();

		if (isBlank(toEmail)) {
			return InvoiceResponse.fail(invoiceDetail.getInvoiceNumber());
		}

		logger.debug("[sendInvoicePdfToAutoInvoiceEnabledUsersForWork] : workId: " + work.getId() + " invoiceId: " + invoiceDetail.getInvoiceId() + " email: " + toEmail);
		try {
			emailInvoiceToUser(toEmail, invoiceDetail, work);
		} catch (Exception ex) {
			logger.error("[sendInvoicePdfToAutoInvoiceEnabledUsersForWork] Failed to send invoice id " + invoiceDetail.getInvoiceNumber()  + "  to the email: " + toEmail, ex);
		}

		return InvoiceResponse.success(invoiceDetail.getInvoiceNumber());
	}

	@Override
	public InvoiceResponse sendInvoicePdfToSubscribedUsersForWork(Work work, AccountStatementDetailRow invoiceDetail) {
		checkNotNull(work);
		checkNotNull(invoiceDetail);

		logger.debug("[sendInvoicePdfToSubscribedUsersForWork] : workId: " + work.getId() + " invoiceId: " + invoiceDetail.getInvoiceId());

		for (User user : authenticationService.findAllUsersSubscribedToNewAssignmentInvoices(work)) {
			try {
				emailInvoiceToUser(user.getEmail(), invoiceDetail, work);
			} catch (Exception ex) {
				logger.error("[sendInvoicePdfToSubscribedUsersForWork] Failed to send invoice id " + invoiceDetail.getInvoiceNumber() + "  to the email: " + user.getEmail(), ex);
			}
		}
		return InvoiceResponse.success(invoiceDetail.getInvoiceNumber());
	}

	@Override
	public InvoiceResponse sendInvoiceToUsers(Work work, AccountStatementDetailRow invoiceDetail) {

		sendInvoicePdfToAutoInvoiceEnabledUsersForWork(work, invoiceDetail);
		sendInvoicePdfToSubscribedUsersForWork(work, invoiceDetail);

		return InvoiceResponse.success(invoiceDetail.getInvoiceNumber());
	}

	private void emailInvoiceToUser(String toEmail, AccountStatementDetailRow invoiceDetail, Work work) {
		if (isBlank(toEmail)) return;
		if (!EmailValidator.getInstance().isValid(toEmail)) return;

		EmailTemplate template = emailTemplateFactory.buildInvoiceDetailEmailTemplate(toEmail, work.getInvoice(), invoiceDetail);

		notificationDispatcher.dispatchEmail(template);

		if (work.getInvoice() instanceof InvoiceSummary)
			updateInvoiceLastSentOnAndSentTo((InvoiceSummary) work.getInvoice(), toEmail, DateUtilities.getCalendarNow());
	}

	// TODO: this is a dupe of what's in BillingServiceImpl, delete that other one after refactoring
	private void updateInvoiceLastSentOnAndSentTo(InvoiceSummary invoiceSummary, String email, Calendar date) {
		checkNotNull(date);
		checkNotNull(invoiceSummary);

		invoiceSummary.setSentTo(email);
		invoiceSummary.setSentOn(date);

		//Update all bundled invoices
		if (invoiceSummary.getInvoices() != null) {
			for (Invoice invoiceBundled : invoiceSummary.getInvoices()) {
				invoiceBundled.setSentTo(email);
				invoiceBundled.setSentOn(date);
			}
		}
		invoiceDAO.saveOrUpdate(invoiceSummary);
	}

}
