package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity(name = "abstractInvoice")
@Table(name = "invoice")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("base")
@AuditChanges
public abstract class AbstractInvoice extends DeletableEntity {

	private static final long serialVersionUID = -6913422898964069085L;

	private String description;
	private Calendar dueDate;
	private Calendar voidOn;
	private Company company;
	private InvoiceStatusType invoiceStatusType = new InvoiceStatusType(InvoiceStatusType.PAYMENT_PENDING);
	private Calendar paymentDate;
	private User paidBy;
	private BigDecimal balance = BigDecimal.ZERO;
	private String sentTo;
	private Calendar sentOn;
	private Calendar downloadedOn;
	private String invoiceNumber;
	private PaymentFulfillmentStatusType paymentFulfillmentStatusType = new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.PENDING_FULFILLMENT);
	private BigDecimal remainingBalance = BigDecimal.ZERO;

	protected AbstractInvoice() {}

	@Column(name = "description", length = 140, nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "due_date")
	public Calendar getDueDate() {
		return dueDate;
	}

	public void setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
	}

	@Column(name = "void_on")
	public Calendar getVoidOn() {
		return voidOn;
	}

	public void setVoidOn(Calendar voidOn) {
		this.voidOn = voidOn;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="company_id", referencedColumnName="id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="invoice_status_type_code", referencedColumnName="code")
	public InvoiceStatusType getInvoiceStatusType() {
		return invoiceStatusType;
	}

	public AbstractInvoice setInvoiceStatusType(InvoiceStatusType invoiceStatusType) {
		this.invoiceStatusType = invoiceStatusType;
		return this;
	}

	@Column(name = "payment_date")
	public Calendar getPaymentDate() {
		return paymentDate;
	}

	public AbstractInvoice setPaymentDate(Calendar paymentDate) {
		this.paymentDate = paymentDate;
		return this;
	}

	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="paid_by", referencedColumnName="id")
	public User getPaidBy() {
		return paidBy;
	}

	public void setPaidBy(User paidBy) {
		this.paidBy = paidBy;
	}

	@Column(name = "balance")
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Column(name = "last_sent_to")
	public String getSentTo() {
		return sentTo;
	}

	public void setSentTo(String sentTo) {
		this.sentTo = sentTo;
	}

	@Column(name = "last_sent_on")
	public Calendar getSentOn() {
		return sentOn;
	}

	public void setSentOn(Calendar sentOn) {
		this.sentOn = sentOn;
	}

	@Column(name = "downloaded_on")
	public Calendar getDownloadedOn() {
		return downloadedOn;
	}

	public void setDownloadedOn(Calendar downloadedOn) {
		this.downloadedOn = downloadedOn;
	}

	@Column(name = "invoice_number", length=50)
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public AbstractInvoice setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
		return this;
	}

	@Column(name = "remaining_balance")
	public BigDecimal getRemainingBalance() {
		return remainingBalance;
	}

	public void setRemainingBalance(BigDecimal remainingBalance) {
		this.remainingBalance = remainingBalance;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name="payment_fulfillment_status_type_code", referencedColumnName="code")
	public PaymentFulfillmentStatusType getPaymentFulfillmentStatusType() {
		return paymentFulfillmentStatusType;
	}

	public void setPaymentFulfillmentStatusType(PaymentFulfillmentStatusType paymentFulfillmentStatusType) {
		this.paymentFulfillmentStatusType = paymentFulfillmentStatusType;
	}

	@Transient
	public boolean isPaymentPending() {
		return getInvoiceStatusType().getCode().equals(InvoiceStatusType.PAYMENT_PENDING);
	}

	@Transient
	public boolean isPaid() {
		return getInvoiceStatusType().getCode().equals(InvoiceStatusType.PAID);
	}

	@Transient
	public boolean isEditable() {
		return (getSentOn() == null && getSentTo() == null && getDownloadedOn() == null && !isPaid());
	}

	@Transient
	public boolean isFulFilled() {
		return PaymentFulfillmentStatusType.FULFILLED.equals(getPaymentFulfillmentStatusType().getCode());
	}

	@Transient
	public boolean isPendingFulfillment() {
		return PaymentFulfillmentStatusType.PENDING_FULFILLMENT.equals(getPaymentFulfillmentStatusType().getCode());
	}

	@Transient
	public boolean isVoid() {
		return InvoiceStatusType.VOID.equals(this.invoiceStatusType.getCode());
	}

	@Transient
	public String getType() {
		return "base";
	}

	/**
	 * Number of days past due date
	 * @return	If due date was reached, (now - dueDate) in days; otherwise 0
	 */
	@Transient
	public int getDaysPastDue() {
		if (this.isPaid()) {
			return 0;
		}
		return Math.max(0, DateUtilities.getDaysBetween(this.dueDate, DateUtilities.getCalendarNow(), false));
	}

	public void markAsPaid(User paidBy, Calendar paymentDate) {
		if (paidBy != null && paymentDate != null) {
			this.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAID));
			this.setPaidBy(paidBy);
			this.setPaymentDate(paymentDate);
			this.setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLED));
			this.setRemainingBalance(BigDecimal.ZERO);
		}
	}

	public void markAsPaidOffline(User paidBy, Calendar paymentDate) {
		if (paidBy != null && paymentDate != null) {
			this.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAID_OFFLINE));
			this.setPaidBy(paidBy);
			this.setPaymentDate(paymentDate);
			this.setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLED));
			this.setRemainingBalance(BigDecimal.ZERO);
		}
	}
}
