package com.workmarket.service.business.dto.invoice;

import com.google.common.collect.Lists;

import java.util.Calendar;
import java.util.List;

/**
 * Author: rocio
 */
public class InvoiceDTO {

	private Long invoiceId;
	private Long companyId;
	private String description;
	private Calendar dueDate;
	private List<InvoiceLineItemDTO> lineItemDTOList = Lists.newArrayList();
	private String subscriptionInvoiceTypeCode;
	private Calendar paymentPeriod;

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getDueDate() {
		return dueDate;
	}

	public void setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
	}

	public List<InvoiceLineItemDTO> getLineItemDTOList() {
		return lineItemDTOList;
	}

	public void setLineItemDTOList(List<InvoiceLineItemDTO> lineItemDTOList) {
		this.lineItemDTOList = lineItemDTOList;
	}

	public String getSubscriptionInvoiceTypeCode() {
		return subscriptionInvoiceTypeCode;
	}

	public void setSubscriptionInvoiceTypeCode(String subscriptionInvoiceTypeCode) {
		this.subscriptionInvoiceTypeCode = subscriptionInvoiceTypeCode;
	}

	public Calendar getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(Calendar paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}

	public boolean isSubscriptionInvoice() {
		return this.subscriptionInvoiceTypeCode != null;
	}
}
