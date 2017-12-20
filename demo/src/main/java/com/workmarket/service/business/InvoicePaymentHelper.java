package com.workmarket.service.business;

import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.work.model.project.ProjectInvoiceBundle;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: rocio
 */
public interface InvoicePaymentHelper {

	List<ProjectInvoiceBundle> groupInvoicesByProject(List<? extends AbstractInvoice> invoices, Long companyId);

	List<? extends AbstractInvoice> findInvoicesWithoutProjectBudget(List<? extends AbstractInvoice> invoices, Long companyId);

	BigDecimal calculateTotalToPayFromGeneralCash(List<? extends AbstractInvoice> invoices, Long companyId);
}