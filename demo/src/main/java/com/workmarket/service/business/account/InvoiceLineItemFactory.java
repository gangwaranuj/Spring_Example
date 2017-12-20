package com.workmarket.service.business.account;

import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;

import java.util.List;

/**
 * Author: rocio
 */
public interface InvoiceLineItemFactory {

	InvoiceLineItem newInvoiceLineItem(InvoiceLineItemDTO invoiceLineItemDTO);

	InvoiceLineItem newSubscriptionInvoiceLineItem(ServiceTransaction serviceTransaction);

	List<InvoiceLineItemDTO> newSubscriptionInvoiceLineItemDTOList(SubscriptionPaymentDTO subscriptionPaymentDTO);
}
