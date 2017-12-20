package com.workmarket.common.template.pdf;

import com.workmarket.domains.model.AddressUtilities;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import org.apache.commons.lang.StringUtils;

public class InvoicePDFTemplate extends PDFTemplate{

	public String getCompanyFormattedAddress(AbstractInvoice invoice) {
		if (invoice != null && invoice.getCompany() != null && invoice.getCompany().getAddress() != null) {
			return AddressUtilities.formatAddressLong(invoice.getCompany().getAddress());
		}
		return StringUtils.EMPTY;
	}

}
