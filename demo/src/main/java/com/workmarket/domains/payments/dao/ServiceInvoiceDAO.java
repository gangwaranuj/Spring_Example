package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.invoice.ServiceInvoicePagination;
import com.workmarket.dto.AggregatesDTO;

public interface ServiceInvoiceDAO extends AbstractInvoiceDAO<AbstractServiceInvoice> {

	ServiceInvoicePagination findAll(ServiceInvoicePagination pagination);

	AggregatesDTO getAllServiceInvoicesTotal();

}
