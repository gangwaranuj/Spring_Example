package com.workmarket.web.validators;

import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class FastFundsValidator {

	private static final Log logger = LogFactory.getLog(FastFundsValidator.class);

	@Autowired private MessageBundleHelper messageBundleHelper;
	@Autowired private BillingService billingService;
	@Autowired private WorkService workService;
	@Autowired private CompanyService companyService;

	public boolean isWorkFastFundable(final String workNumber) {
		final MessageBundle messageBundle = createMessageBundle();

		validate(workNumber, messageBundle);

		return !messageBundle.hasErrors();
	}

	public boolean isInvoiceFastFundable(long invoiceId) {
		MessageBundle messageBundle = createMessageBundle();

		validate(invoiceId, messageBundle);

		return !messageBundle.hasErrors();
	}

	// TODO - API Errors - update error codes
	public void validate(String workNumber, MessageBundle messageBundle) {
		Long workId = workService.findWorkId(workNumber);
		if (workId == null) {
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.generic_error");
			return;
		}

		Invoice invoice = billingService.findInvoiceByWorkId(workId);
		if (invoice == null) {
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.generic_error");
			return;
		}

		validate(invoice.getId(), messageBundle);
	}

	public void validate(long invoiceId, MessageBundle messageBundle) {
		Invoice invoice = billingService.findInvoiceById(invoiceId);
		if (invoice == null) {
			logger.error("FastFundsValidator: invoiceId<" + invoiceId + "> not found.");
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.generic_error");
			return;
		}

		if (!invoice.isPaymentPending() || invoice.getFastFundedOn() != null) {
			if (!invoice.isPaymentPending()) {
				logger.error("FastFundsValidator: Invoice<" + invoiceId + "> payment is not pending.");
			} else {
				logger.error("FastFundsValidator: Invoice<" + invoiceId + "> already fast funded.");
			}
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.already_paid");
			return;
		}

		if (invoice.getDeleted()) {
			logger.error("FastFundsValidator: Invoice<" + invoiceId + "> is deleted.");
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.generic_error");
			return;
		}

		if (!companyService.isFastFundsEnabled(invoice.getCompany())) {
			logger.error("FastFundsValidator: Invoice<" + invoiceId + "> Company<" + invoice.getCompany().getId() + "> does not have feature.");
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.generic_error");
			return;
		}

		Calendar invoiceDueDate = invoice.getDueDate();

		if (invoiceDueDate == null) {
			logger.error("FastFundsValidator: Invoice<" + invoiceId + "> invoiceDueDate is null.");
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.generic_error");
			return;
		}

		Calendar now = createCalendarForNow();
		invoiceDueDate = (Calendar) invoiceDueDate.clone();

		// Invoice due in less than FF Availabilty threshold (24 hours)
		invoiceDueDate.add(Calendar.HOUR, -billingService.getFastFundsAvailabilityThresholdHours());
		if (now.compareTo(invoiceDueDate) == 1) {
			logger.error("FastFundsValidator: Invoice<" + invoiceId + "> Fast funds not available.");
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.no_longer_available");
		}

		if (!billingService.validateAccessToFastFundInvoice(invoiceId)) {
			logger.error("FastFundsValidator: Invoice<" + invoiceId + "> cannot access.");
			messageBundleHelper.addError(messageBundle, "payments.invoices.fast_funds.validation.generic_error");
			return;
		}
	}

	// Only here for unit testing purposes
	public Calendar createCalendarForNow() {
		return Calendar.getInstance();
	}

	// Only here for unit testing purposes
	public MessageBundle createMessageBundle() {
		return new MessageBundle();
	}

}
