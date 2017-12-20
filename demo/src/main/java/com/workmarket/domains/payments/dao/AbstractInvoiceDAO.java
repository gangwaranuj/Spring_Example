package com.workmarket.domains.payments.dao;

import java.util.Calendar;
import java.util.Set;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.invoice.AbstractInvoice;

/**
 * Author: rocio
 */
public interface AbstractInvoiceDAO<T extends AbstractInvoice> extends PaginatableDAOInterface<T> {

	T findInvoiceById(long invoiceId);

	Set<? extends AbstractInvoice> findAllInvoicesPastDue(Calendar dueDate);

	Set<? extends AbstractInvoice> findAllDueInvoicesByDueDate(Calendar dueDateFrom, Calendar dueDateThrough);

	AbstractInvoice findEarliestDueInvoice(long companyId);
}
