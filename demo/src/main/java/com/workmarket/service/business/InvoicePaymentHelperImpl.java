package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectInvoiceBundle;
import com.workmarket.domains.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Author: rocio
 */
@Service
public class InvoicePaymentHelperImpl implements InvoicePaymentHelper {

	@Autowired private CompanyService companyService;
	@Autowired private WorkService workService;

	@Override
	public List<ProjectInvoiceBundle> groupInvoicesByProject(List<? extends AbstractInvoice> invoices, Long companyId) {
		List<ProjectInvoiceBundle> projectInvoiceBundleList = Lists.newArrayListWithExpectedSize(invoices.size());
		Map<Project, List<AbstractInvoice>> projectInvoiceMap = Maps.newHashMap();

		if (companyService.doesCompanyHaveReservedFundsEnabledProject(companyId)) {

			for(AbstractInvoice invoice : invoices) {
				// check if invoice is a bundle
				if(invoice instanceof InvoiceSummary) {
					InvoiceSummary bundleInvoice = (InvoiceSummary)invoice;
					projectInvoiceBundleList.addAll(groupInvoicesByProject(Lists.newArrayList(bundleInvoice.getInvoices()), companyId));
				}

				if(invoice instanceof Statement) {
					Statement statementInvoices = (Statement)invoice;
					projectInvoiceBundleList.addAll(groupInvoicesByProject(Lists.newArrayList(statementInvoices.getInvoices()), companyId));
				}

				Work work = workService.findWorkByInvoice(invoice.getId());
				if (work != null) {
					Project project = work.getProject();
					if(project != null && project.isReservedFundsEnabled()) {
						if(!projectInvoiceMap.containsKey(project)) {
							List<AbstractInvoice> list = Lists.newArrayList();
							projectInvoiceMap.put(project, list);
						}
						projectInvoiceMap.get(project).add(invoice);
					}
				}

			}

			for (Map.Entry<Project, List<AbstractInvoice>> entry : projectInvoiceMap.entrySet()) {
				projectInvoiceBundleList.add(new ProjectInvoiceBundle(entry.getKey(), entry.getValue()));
			}
		}
		return projectInvoiceBundleList;
	}

	@Override
	public List<? extends AbstractInvoice> findInvoicesWithoutProjectBudget(List<? extends AbstractInvoice> invoices, Long companyId) {
		List<AbstractInvoice> generalInvoices = Lists.newArrayList();
		if (!companyService.doesCompanyHaveReservedFundsEnabledProject(companyId)) {
			generalInvoices.addAll(invoices);
			return generalInvoices;
		}

		for(AbstractInvoice i: invoices){
			if(i instanceof InvoiceSummary) {
				InvoiceSummary bundleInvoice = (InvoiceSummary)i;
				generalInvoices.addAll(findInvoicesWithoutProjectBudget(Lists.newArrayList(bundleInvoice.getInvoices()), companyId));
			}

			if(i instanceof Statement) {
				Statement statementInvoices = (Statement)i;
				generalInvoices.addAll(findInvoicesWithoutProjectBudget(Lists.newArrayList(statementInvoices.getInvoices()), companyId));
			}

			Work work = workService.findWorkByInvoice(i.getId());
			if (work != null) {
				Project project = work.getProject();
				if(project == null || !project.isReservedFundsEnabled()) {
					generalInvoices.add(i);
				}
			}

		}
		return generalInvoices;
	}

	@Override
	public BigDecimal calculateTotalToPayFromGeneralCash(List<? extends AbstractInvoice> invoices, Long companyId) {
		BigDecimal generalTotalDue = BigDecimal.ZERO;
		List<? extends AbstractInvoice> generalCashInvoices = findInvoicesWithoutProjectBudget(invoices, companyId);
		for(AbstractInvoice invoice: generalCashInvoices) {
			if(invoice.isPaymentPending()) {
				generalTotalDue = generalTotalDue.add(invoice.getRemainingBalance());
			}
		}
		return generalTotalDue;
	}
}