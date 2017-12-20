package com.workmarket.domains.model.invoice.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Author: rocio
 */
public enum InvoiceLineItemType {

	DEPOSIT_RETURN_FEE(InvoiceLineItem.DEPOSIT_RETURN_FEE, "Deposit Return Fees"),
	WITHDRAWAL_RETURN_FEE(InvoiceLineItem.WITHDRAWAL_RETURN_FEE, "Withdrawal Return Fees"),
	LATE_PAYMENT_FEE(InvoiceLineItem.LATE_PAYMENT_FEE, "Late Payment Fees"),
	MISC_FEE(InvoiceLineItem.MISC_FEE, "Miscellaneous Fees"),
	SUBSCRIPTION_SOFTWARE_FEE(InvoiceLineItem.SUBSCRIPTION_SOFTWARE_FEE_INVOICE_LINE_ITEM, "Subscription - NVOR SW Fees "),
	SUBSCRIPTION_SOFTWARE_FEE_VOR(InvoiceLineItem.SUBSCRIPTION_SOFTWARE_FEE_INVOICE_LINE_ITEM, "Subscription - VOR SW Fees "),
	SUBSCRIPTION_ADD_ON(InvoiceLineItem.SUBSCRIPTION_ADD_ON_INVOICE_LINE_ITEM, "Professional Service Fees "),
	SUBSCRIPTION_VOR(InvoiceLineItem.SUBSCRIPTION_VOR_INVOICE_LINE_ITEM, "Subscription - VOR Fees "),
	SUBSCRIPTION_SETUP_FEE(InvoiceLineItem.SUBSCRIPTION_SETUP_FEE_INVOICE_LINE_ITEM, "Subscription - Setup Fees "),
	SUBSCRIPTION_DISCOUNT(InvoiceLineItem.SUBSCRIPTION_DISCOUNT_INVOICE_LINE_ITEM, "Subscription Discount ");

	private final String type;
	private final String description;
	private static final Map<String, InvoiceLineItemType> lookup = Maps.newHashMapWithExpectedSize(InvoiceLineItemType.values().length);
	public static final List<InvoiceLineItemType>
		ADHOC_SERVICE_INVOICE_LINE_ITEMS,
		ADHOC_SERVICE_INVOICE_LINE_ITEMS_SUBSCRIPTION,
		ADHOC_SERVICE_INVOICE_LINE_ITEMS_NON_SUBSCRIPTION;

	static {
		for (InvoiceLineItemType invoiceLineItemType : EnumSet.allOf(InvoiceLineItemType.class)) {
			lookup.put(invoiceLineItemType.getType(), invoiceLineItemType);
		}

		ADHOC_SERVICE_INVOICE_LINE_ITEMS = Lists.newArrayList(InvoiceLineItemType.values());
		ADHOC_SERVICE_INVOICE_LINE_ITEMS.remove(SUBSCRIPTION_DISCOUNT);

		ADHOC_SERVICE_INVOICE_LINE_ITEMS_SUBSCRIPTION = Lists.newArrayList(SUBSCRIPTION_SOFTWARE_FEE, SUBSCRIPTION_SOFTWARE_FEE_VOR, SUBSCRIPTION_VOR, SUBSCRIPTION_SETUP_FEE, SUBSCRIPTION_DISCOUNT);
		ADHOC_SERVICE_INVOICE_LINE_ITEMS_NON_SUBSCRIPTION = Lists.newArrayList(DEPOSIT_RETURN_FEE, WITHDRAWAL_RETURN_FEE, LATE_PAYMENT_FEE, MISC_FEE, SUBSCRIPTION_ADD_ON);
	}

	InvoiceLineItemType(String type, String description) {
		this.type = type;
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return this.name();
	}

	public String getDescription() {
		return description;
	}

	public static List<String> getNonSubscriptionInvoiceLineItemTypeCodes() {
		List<String> nonSubscriptionInvoiceLineItemTypeCodes = Lists.newArrayListWithCapacity(InvoiceLineItemType.ADHOC_SERVICE_INVOICE_LINE_ITEMS_NON_SUBSCRIPTION.size());
		for (InvoiceLineItemType invoiceLineItemType : InvoiceLineItemType.ADHOC_SERVICE_INVOICE_LINE_ITEMS_NON_SUBSCRIPTION) {
			nonSubscriptionInvoiceLineItemTypeCodes.add(invoiceLineItemType.getName());
		}
		return nonSubscriptionInvoiceLineItemTypeCodes;
	}

}
