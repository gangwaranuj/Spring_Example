package com.workmarket.domains.model.invoice;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Set;

/**
 * Invoice Summary is a group of invoices and it extends from {@link com.workmarket.domains.model.invoice.Invoice Invoice}
 * because conceptually it is one when displaying all the invoices on a particular account.
 * In Hibernate if we say "Select * from invoice" we'll get the two types.
 *
 * @author rocio
 *
 */
@Entity(name = "invoiceSummary")
@DiscriminatorValue(InvoiceSummary.INVOICE_SUMMARY_TYPE)
@AuditChanges
public class InvoiceSummary extends Invoice {

	private static final long serialVersionUID = -6892106713125658347L;

	private Set<Invoice> invoices = Sets.newLinkedHashSet();
	public static final String INVOICE_SUMMARY_TYPE = "bundle";

	public InvoiceSummary() {
		super();
	}

	@OneToMany
	@JoinTable(name = "invoice_summary_detail",
	           joinColumns = @JoinColumn(name = "invoice_summary_id"),
	           inverseJoinColumns = @JoinColumn(name = "invoice_id"))
	public Set<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(Set<Invoice> invoices) {
		this.invoices = invoices;
	}

	@Override
	@Transient
	public String getType() {
		return INVOICE_SUMMARY_TYPE;
	}

	@Transient
	@Override
	public boolean isEditable() {
		return (getSentOn() == null && getSentTo() == null && getDownloadedOn() == null && !isPaid());
	}
}
