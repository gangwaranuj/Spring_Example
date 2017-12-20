package com.workmarket.domains.model.invoice;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Set;

/**
 * Statement is similar to {@link com.workmarket.domains.model.invoice.InvoiceSummary InvoiceSummary}, with some additions, it could easily extend from it,
 * except that we need to differentiate between the two of them while presenting the data to the user.
 */
@Entity(name = "statement")
@DiscriminatorValue(Statement.STATEMENT_TYPE)
@AuditChanges
public class Statement extends AbstractInvoice {

	private static final long serialVersionUID = 1017390094066321852L;
	private Set<Invoice> invoices = Sets.newLinkedHashSet();
	private Calendar periodStartDate;
	private Calendar periodEndDate;
	public static final String STATEMENT_TYPE = "statement";

	public Statement() {
		super();
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "invoice_summary_detail",
		joinColumns = @JoinColumn(name = "invoice_summary_id"),
		inverseJoinColumns = @JoinColumn(name = "invoice_id"))
	@Where(clause = "deleted = 0")
	@BatchSize(size = 10)
	public Set<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(Set<Invoice> invoices) {
		this.invoices = invoices;
	}

	@Column(name = "statement_period_start_date")
	public Calendar getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(Calendar periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	@Column(name = "statement_period_end_date")
	public Calendar getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(Calendar periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	@Override
	@Transient
	public String getType() {
		return STATEMENT_TYPE;
	}

	@Transient
	public String getStatementNumber() {
		return this.getInvoiceNumber();
	}
}
