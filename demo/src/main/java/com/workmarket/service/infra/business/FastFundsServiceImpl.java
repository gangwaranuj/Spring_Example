package com.workmarket.service.infra.business;

import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class FastFundsServiceImpl implements FastFundsService {
	private static final Log logger = LogFactory.getLog(FastFundsServiceImpl.class);

	@Autowired private BillingService billingService;
	@Autowired private WorkService workService;

	@Override
	public FastFundInvoiceResponse fastFundInvoice(long invoiceId) {
		Work work = workService.findWorkByInvoice(invoiceId);
		Assert.notNull(work, "Could not find work object associated with invoice id: " + invoiceId);

		return fastFundInvoice(invoiceId, work.getId());
	}

	@Override
	public FastFundInvoiceResponse fastFundInvoice(long invoiceId, long workId) {

		FastFundInvoiceResponse fastFundInvoiceResponse = billingService.fastFundInvoice(invoiceId, workId);

		if (fastFundInvoiceResponse.isFail()) {
			logger.debug("Failed to fast fund invoiceId: " + invoiceId);
			return fastFundInvoiceResponse;
		}

		return fastFundInvoiceResponse;
	}

	@Override
	public FastFundInvoiceResponse fastFundInvoiceForWork(final String workNumber) {
		final Work work = workService.findWorkByWorkNumber(workNumber);
		Assert.notNull(work, "Could not find work object associated with work number: " + workNumber);
		if (work.getInvoice() == null) {
			return FastFundInvoiceResponse.invoiceNotFound();
		}

		return fastFundInvoice(work.getInvoice().getId(), work.getId());
	}
}
