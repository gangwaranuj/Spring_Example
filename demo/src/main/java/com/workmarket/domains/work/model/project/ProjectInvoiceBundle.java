package com.workmarket.domains.work.model.project;

import com.workmarket.domains.model.invoice.AbstractInvoice;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 5/8/13
 * Time: 7:50 PM
 */
public class ProjectInvoiceBundle {
	private static final long serialVersionUID = 1L;
	private Project project;
	private BigDecimal sumOfInvoices;
	private List<AbstractInvoice> invoices;

	public ProjectInvoiceBundle(Project project, List<AbstractInvoice> invoices) {
		this.project = project;
		this.invoices = invoices;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<AbstractInvoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<AbstractInvoice> invoices) {
		this.invoices = invoices;
	}

	public BigDecimal getSumOfInvoices() {
		BigDecimal total = BigDecimal.ZERO;
		for(AbstractInvoice i : invoices) {
			total = total.add(i.getRemainingBalance());
		}
		return total;
	}

}
