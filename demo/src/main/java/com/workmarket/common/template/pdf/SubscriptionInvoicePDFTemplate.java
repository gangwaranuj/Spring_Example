package com.workmarket.common.template.pdf;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.StringUtils;

/**
 * author: rocio
 */
public class SubscriptionInvoicePDFTemplate extends InvoicePDFTemplate {

	private SubscriptionInvoice invoice;
	private String clientRefId;

	public SubscriptionInvoicePDFTemplate(SubscriptionInvoice invoice) {
		super();
		this.invoice = invoice;
		if (invoice != null) {
			setOutputFileName("Invoice_" + invoice.getInvoiceNumber());
		}
	}

	public SubscriptionInvoice getInvoice() {
		return invoice;
	}

	public String getClientRefId() {
		return invoice.getClientRefId();
	}

	public String getPaymentPeriodFromDate() {
		if (invoice != null && invoice.getPaymentPeriod() != null) {
			return DateUtilities.format("MMM dd, yyyy", invoice.getPaymentPeriod().getPeriodDateRange().getFrom());
		}
		return StringUtils.EMPTY;
	}

	public String getPaymentPeriodThroughDate() {
		if (invoice != null && invoice.getPaymentPeriod() != null) {
			return DateUtilities.format("MMM dd, yyyy", DateUtilities.getCalendarWithLastDayOfThePreviousMonth(invoice.getPaymentPeriod().getPeriodDateRange().getThrough()));
		}
		return StringUtils.EMPTY;
	}

	public String getCancellationDate() {
		if (invoice != null && SubscriptionInvoiceType.CANCELLATION.equals(invoice.getSubscriptionInvoiceType().getCode())) {
			if (invoice.getPaymentPeriod() != null) {
				SubscriptionPaymentPeriod paymentPeriod = (SubscriptionPaymentPeriod)invoice.getPaymentPeriod();
				if (paymentPeriod != null && paymentPeriod.getSubscriptionConfiguration() != null && paymentPeriod.getSubscriptionConfiguration().getSubscriptionCancellation() != null) {
					return DateUtilities.format("MMM dd, yyyy", paymentPeriod.getSubscriptionConfiguration().getSubscriptionCancellation().getEffectiveDate());
				}
			}
		}
		return StringUtils.EMPTY;
	}

	public String getCompanyFormattedAddress() {
		return super.getCompanyFormattedAddress(invoice);
	}
}
