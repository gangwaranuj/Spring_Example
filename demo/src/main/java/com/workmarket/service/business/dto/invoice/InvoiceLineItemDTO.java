package com.workmarket.service.business.dto.invoice;

import com.workmarket.domains.model.invoice.item.InvoiceLineItemType;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Author: rocio
 */
public class InvoiceLineItemDTO {

	private String description;
	private BigDecimal amount = BigDecimal.ZERO;
	private Calendar transactionDate;
	private String comment;
	private InvoiceLineItemType invoiceLineItemType;

	public InvoiceLineItemDTO() {
	}

	public InvoiceLineItemDTO(InvoiceLineItemType invoiceLineItemType) {
		this.invoiceLineItemType = invoiceLineItemType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public InvoiceLineItemDTO setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public InvoiceLineItemDTO setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public InvoiceLineItemDTO setDescription(String description) {
		this.description = description;
		return this;
	}

	public Calendar getTransactionDate() {
		return transactionDate;
	}

	public InvoiceLineItemDTO setTransactionDate(Calendar transactionDate) {
		this.transactionDate = transactionDate;
		return this;
	}

	public InvoiceLineItemType getInvoiceLineItemType() {
		return invoiceLineItemType;
	}

	public InvoiceLineItemDTO setInvoiceLineItemType(InvoiceLineItemType invoiceLineItemType) {
		this.invoiceLineItemType = invoiceLineItemType;
		return this;
	}

}
