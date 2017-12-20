package com.workmarket.service.infra.business;

import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FastFundsServiceTest {

	@Mock BillingService billingService;
	@Mock WorkService workService;

	@InjectMocks FastFundsServiceImpl fastFundsService;

	@Mock Work work;
	@Mock Invoice invoice;

	private static final Long INVOICE_ID = 1L, WORK_ID = 1L;
	private static final String WORK_NUMBER = "some-work-number";
	FastFundInvoiceResponse fastFundInvoiceResponse;

	@Before
	public void setUp() {
		work = mock(Work.class);
		invoice = mock(Invoice.class);
		when(invoice.getId()).thenReturn(INVOICE_ID);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getInvoice()).thenReturn(invoice);

		fastFundInvoiceResponse = FastFundInvoiceResponse.success();

		when(workService.findWorkByInvoice(anyLong())).thenReturn(work);
		when(billingService.fastFundInvoice(anyLong(), anyLong())).thenReturn(fastFundInvoiceResponse);
		when(workService.findWorkByWorkNumber(anyString())).thenReturn(work);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fastFundInvoice_workAssociatedWithInvoiceNotFound_exceptionThrown() {
		when(workService.findWorkByInvoice(anyLong())).thenReturn(null);

		fastFundsService.fastFundInvoice(INVOICE_ID);
	}

	@Test
	public void fastFundInvoice_invoiceId_invoiceFastFunded() {
		assertTrue(fastFundsService.fastFundInvoice(INVOICE_ID).isSuccess());
	}

	@Test
	public void fastFundInvoice_invoiceIdAndWorkId_invoiceFastFunded() {
		assertTrue(fastFundsService.fastFundInvoice(INVOICE_ID, WORK_ID).isSuccess());
	}

	@Test
	public void fastFundInvoice_invoiceIdAndWorkId_invoiceFastFundFailed() {
		when(billingService.fastFundInvoice(anyLong(), anyLong())).thenReturn(FastFundInvoiceResponse.fail());

		assertTrue(fastFundsService.fastFundInvoice(INVOICE_ID, WORK_ID).isFail());
	}

	@Test
	public void fastFundInvoiceForWork_workNumber_invoiceFastFunded() {
		assertTrue(fastFundsService.fastFundInvoiceForWork(WORK_NUMBER).isSuccess());
	}

	@Test
	public void fastFundInvoiceForWork_workNumber_invoiceNotFound() {
		when(work.getInvoice()).thenReturn(null);

		final FastFundInvoiceResponse response = fastFundsService.fastFundInvoiceForWork(WORK_NUMBER);

		assertEquals(FastFundInvoiceResponse.invoiceNotFound().getStatus(), response.getStatus());
	}

	@Test
	public void fastFundInvoiceForWork_workNumber_invoiceFastFundFailed() {
		when(billingService.fastFundInvoice(anyLong(), anyLong())).thenReturn(FastFundInvoiceResponse.fail());

		final FastFundInvoiceResponse response = fastFundsService.fastFundInvoiceForWork(WORK_NUMBER);

		assertTrue(response.isFail());
	}
}
