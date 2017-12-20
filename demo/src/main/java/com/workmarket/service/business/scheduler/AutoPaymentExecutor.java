package com.workmarket.service.business.scheduler;

import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AutoPaymentExecutor implements ScheduledExecutor {

	private static final Log logger = LogFactory.getLog(AutoPaymentExecutor.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private BillingService billingService;

	public void execute() {
		runAutoPayAssignments();
		runAutoPayInvoices();
	}

	public void runAutoPayAssignments() {
		logger.info("****** runAutoPayAssignments at " + new Date());
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		List<Integer> workIds = billingService.getAutoPayAssignmentList();

		for (Integer workId : workIds) {
			try {
				logger.info("Auto paying work id: " + workId);
				billingService.payAssignment(workId.longValue());
			} catch (Exception e) {
				logger.error("Error paying workId: " + workId, e);
			}
		}

		logger.info("runAutoPayAssignments.... completed:" + workIds.size());
	}

	public void runAutoPayInvoices() {
		logger.info("****** runAutoPayInvoices at " + new Date());
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		List<Integer> invoiceIds = billingService.getAutoPayInvoiceList();

		for (Integer invoiceId : invoiceIds) {
			try {
				logger.info("autoPaying invoiceId:" + invoiceId);
				billingService.payInvoice(Constants.WORKMARKET_SYSTEM_USER_ID, invoiceId);
			} catch (Exception isfe) {
				logger.error("Error paying invoiceId: " + invoiceId, isfe);
			}
		}

		logger.info("runAutoPayInvoices.... completed:" + invoiceIds.size());
	}

}
