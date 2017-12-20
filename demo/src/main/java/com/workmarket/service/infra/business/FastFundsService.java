package com.workmarket.service.infra.business;

import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;

public interface FastFundsService {
	FastFundInvoiceResponse fastFundInvoice(long invoiceId);

	FastFundInvoiceResponse fastFundInvoice(long invoiceId, long workId);

	FastFundInvoiceResponse fastFundInvoiceForWork(String workNumber);
}
