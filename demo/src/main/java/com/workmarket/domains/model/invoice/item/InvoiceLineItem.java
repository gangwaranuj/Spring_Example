package com.workmarket.domains.model.invoice.item;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Author: rocio
 */
@Entity(name = "invoiceLineItem")
@Table(name = "invoice_line_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(InvoiceLineItem.BASE_INVOICE_LINE_ITEM)
@AuditChanges
public class InvoiceLineItem extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	public static final String BASE_INVOICE_LINE_ITEM = "base";
	public static final String DEPOSIT_RETURN_FEE = "depRet";
	public static final String WITHDRAWAL_RETURN_FEE = "withRet";
	public static final String LATE_PAYMENT_FEE = "latePay";
	public static final String MISC_FEE = "misc";
	public static final String SUBSCRIPTION_SOFTWARE_FEE_INVOICE_LINE_ITEM = "subFee";
	public static final String SUBSCRIPTION_VOR_SOFTWARE_FEE_INVOICE_LINE_ITEM = "subVorSwFee";
	public static final String SUBSCRIPTION_ADD_ON_INVOICE_LINE_ITEM = "subAddOn";
	public static final String SUBSCRIPTION_VOR_INVOICE_LINE_ITEM = "subVor";
	public static final String SUBSCRIPTION_SETUP_FEE_INVOICE_LINE_ITEM = "subSetup";
	public static final String SUBSCRIPTION_DISCOUNT_INVOICE_LINE_ITEM = "subDiscount";

	private AbstractServiceInvoice invoice;
	private String description;
	private BigDecimal amount = BigDecimal.ZERO;
	private Calendar transactionDate;
	private String comment;
	private RegisterTransaction registerTransaction;

	public InvoiceLineItem() {
	}

	public InvoiceLineItem(AbstractServiceInvoice invoice) {
		this.invoice = invoice;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "invoice_id", updatable = false)
	public AbstractServiceInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(AbstractServiceInvoice invoice) {
		this.invoice = invoice;
	}

	@Column(name = "amount", nullable = false)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "description", nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "transaction_date", nullable = false)
	public Calendar getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Calendar transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Column(name = "comment", nullable = true)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@ManyToOne
	@JoinColumn(name = "register_transaction_id")
	public RegisterTransaction getRegisterTransaction() {
		return registerTransaction;
	}

	public void setRegisterTransaction(RegisterTransaction registerTransaction) {
		this.registerTransaction = registerTransaction;
	}

	@Transient
	public String getType() {
		return BASE_INVOICE_LINE_ITEM;
	}

	@Transient
	public boolean isSetRegisterTransaction() {
		return registerTransaction != null;
	}

	@Transient
	public String getEscapedDescription() {
		return StringUtilities.stripHTML(this.description);
	}

}
