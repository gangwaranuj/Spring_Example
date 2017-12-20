package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoicePagination;

/**
 * User: micah
 * Date: 3/10/15
 * Time: 12:56 PM
 */
public interface WorkMarketSummaryInvoiceDAO {
	WorkMarketSummaryInvoicePagination findAll(WorkMarketSummaryInvoicePagination pagination);
}
