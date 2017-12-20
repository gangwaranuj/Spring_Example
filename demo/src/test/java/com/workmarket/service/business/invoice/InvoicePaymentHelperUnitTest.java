package com.workmarket.service.business.invoice;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.util.Assert;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.service.business.CompanyServiceImpl;
import com.workmarket.service.business.InvoicePaymentHelperImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: iloveopt
 * Date: 9/3/13
 */
@RunWith(MockitoJUnitRunner.class)

public class InvoicePaymentHelperUnitTest {

	@InjectMocks InvoicePaymentHelperImpl invoicePaymentHelper;
	@Mock CompanyServiceImpl companyService;

	private Invoice invoice;
	private Invoice voidInvoice;
	private List<Invoice> invoiceList = Lists.newArrayList();


	@Before
	public void setup() {
		invoice = mock(Invoice.class);
		when(invoice.getId()).thenReturn(1L);
		when(invoice.getRemainingBalance()).thenReturn(BigDecimal.TEN);
		when(invoice.isPaymentPending()).thenReturn(true);

		voidInvoice = mock(Invoice.class);
		when(voidInvoice.getId()).thenReturn(1L);
		when(voidInvoice.getRemainingBalance()).thenReturn(BigDecimal.TEN);
		when(voidInvoice.isPaymentPending()).thenReturn(false);

		when(companyService.doesCompanyHaveReservedFundsEnabledProject(anyLong())).thenReturn(false);
	}

	@Test
	public void calculateTotalToPayFromGeneralCash_paymentPendingInvoice() {
		invoiceList.add(invoice);
		BigDecimal totalToPay = invoicePaymentHelper.calculateTotalToPayFromGeneralCash(invoiceList, 1L);
		Assert.equals(totalToPay.compareTo(BigDecimal.TEN), 0);
	}

	@Test
	public void calculateTotalToPayFromGeneralCash_voidInvoice() {
		invoiceList.add(voidInvoice);
		BigDecimal totalToPay = invoicePaymentHelper.calculateTotalToPayFromGeneralCash(invoiceList, 1L);
		Assert.equals(totalToPay.compareTo(BigDecimal.ZERO), 0);
	}

	@Test
	public void calculateTotalToPayFromGeneralCash_multipleInvoices() {
		invoiceList.add(invoice);
		invoiceList.add(voidInvoice);
		BigDecimal totalToPay = invoicePaymentHelper.calculateTotalToPayFromGeneralCash(invoiceList, 1L);
		Assert.equals(totalToPay.compareTo(BigDecimal.TEN), 0);
	}



}
