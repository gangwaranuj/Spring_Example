package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.utility.DateUtilities;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity(name = "wmSummaryInvoice")
@Table(name = "invoice")
@Immutable
public class WorkMarketSummaryInvoice extends DeletableEntity {

	private static final long serialVersionUID = -6913422898964069085L;

	private String type;
	private String description;
	private Calendar dueDate;
	private Company company;
	private InvoiceStatusType invoiceStatusType = new InvoiceStatusType(InvoiceStatusType.PAYMENT_PENDING);
	private Calendar paymentDate;
	private BigDecimal balance = BigDecimal.ZERO;
	private String invoiceNumber;

	protected WorkMarketSummaryInvoice() {}

	@Column(name = "type")
	public String getType() { return type; }

	public void setType(String type) { this.type = type; }

	@Column(name = "description", length = 140, nullable = false)
	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	@Column(name = "due_date")
	public Calendar getDueDate() { return dueDate; }

	public void setDueDate(Calendar dueDate) { this.dueDate = dueDate; }

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="company_id", referencedColumnName="id", updatable = false)
	public Company getCompany() { return company; }
	
	public void setCompany(Company company) { this.company = company; }
	
	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="invoice_status_type_code", referencedColumnName="code")
	public InvoiceStatusType getInvoiceStatusType() { return invoiceStatusType; }
	
	public void setInvoiceStatusType(InvoiceStatusType invoiceStatusType) { this.invoiceStatusType = invoiceStatusType; }

	@Column(name = "payment_date")
	public Calendar getPaymentDate() { return paymentDate; }

	public void setPaymentDate(Calendar paymentDate) { this.paymentDate = paymentDate; }

	@Column(name = "balance")
	public BigDecimal getBalance() { return balance; }
	
	public void setBalance(BigDecimal balance) { this.balance = balance; }
	
	@Column(name = "invoice_number", length=50, updatable = false)
	public String getInvoiceNumber() { return invoiceNumber; }

	public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

	@Transient
	public boolean isPaymentPending() {
		return getInvoiceStatusType().getCode().equals(InvoiceStatusType.PAYMENT_PENDING);
	}
	
	@Transient
	public boolean isPaid() {
		return getInvoiceStatusType().getCode().equals(InvoiceStatusType.PAID);
	}

	@Transient
	public boolean isVoid() {
		return InvoiceStatusType.VOID.equals(this.invoiceStatusType.getCode());
	}
	
	/**
	 * Number of days past due date
	 * @return	If due date was reached, (now - dueDate) in days; otherwise 0
	 */
	@Transient
	public int getDaysPastDue() {
		if (this.isPaid()) { return 0; }
		return Math.max(0, DateUtilities.getDaysBetween(this.dueDate, DateUtilities.getCalendarNow(), false));
	}
}
