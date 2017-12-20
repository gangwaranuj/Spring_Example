package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.account.InvoicePaymentTransaction;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ManagedResource(objectName="bean:name=paymentFulfillment", description="Payment fulfillment job")
public class PaymentFulfillmentExecutor implements ScheduledExecutor {

	private static final Log logger = LogFactory.getLog(PaymentFulfillmentExecutor.class);

	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterService;
	@Autowired private BillingService billingService;
	@Autowired private WorkStatusService workStatusService;
	@Autowired private AuthenticationService authenticationService;

	/**
	 * Process bulk payments of invoice bundles or statements and fulfills the individual work and work resource register transactions.
	 */
	@ManagedOperation(description = "Payment Fulfillment")
	public void execute() {
		logger.info("[paymentFulfillment] PaymentFulfillmentExecutor: start");
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		/**
		 * First we search for all the transactions pending fulfillment, these could be invoice bundles or statement payments
		 */
		List<InvoicePaymentTransaction> invoicesPendingFulfillment = accountRegisterService.findAllInvoicePaymentTransactionsPendingFulfillment();
		logger.info("[paymentFulfillment] found: " + invoicesPendingFulfillment.size() + " invoices pending fulfillment");
		for (InvoicePaymentTransaction paymentTransaction : invoicesPendingFulfillment) {
			long paymentTrxId = paymentTransaction.getId();
			long paymentTrxCreatorId = paymentTransaction.getCreatorId();

			logger.info("[paymentFulfillment] paymentTrxId " + paymentTrxId);
			try {
				AccountStatementDetailPagination pagination = new AccountStatementDetailPagination(true);
				pagination = billingService.findAllPendingPaymentInvoicesForInvoiceSummary(paymentTransaction.getInvoice(), pagination);
				if (pagination.getResults().isEmpty()) {
					logger.info("[paymentFulfillment] Found 0 pending invoices/assignments for invoice id: " + paymentTransaction.getInvoice().getId());
				} else {
					for (AccountStatementDetailRow detail : pagination.getResults()) {
						fulfillInvoice(detail, paymentTrxId, paymentTrxCreatorId);
					}
				}
			} catch (Exception e) {
				logger.error("Error fulfilling transaction id " + paymentTransaction.getId(), new Exception());
			}
		}
	}

	private void fulfillInvoice(AccountStatementDetailRow accountStatementDetailRow, long paymentTrxId, long paymentTrxCreatorId) {
		String invoiceType = accountStatementDetailRow.getInvoiceType();

		if (StringUtils.isNotBlank(invoiceType)) {
			// If a regular invoice, pay the individual assignment
			if (invoiceType.equals(Invoice.INVOICE_TYPE)) {
				Long workId = accountStatementDetailRow.getWorkId();
				logger.info("[paymentFulfillment] detail workId " + workId);
				if (workId != null) {
					try {
						logger.info("[paymentFulfillment] transitionToFulfilledAndPaidFromInvoiceBulkPayment ");
						List<ConstraintViolation> violations = workStatusService.transitionToFulfilledAndPaidFromInvoiceBulkPayment(workId, paymentTrxId, paymentTrxCreatorId);
						if (!violations.isEmpty()) {
							for (ConstraintViolation c : violations) {
								logger.info(String.format("Constraint violation found for work %s: %s", workId, c.toString()));
							}
						}
					} catch (Exception e) {
						logger.error("Error fulfilling work id " + workId);
					}
				}
			}
		}
	}
}
