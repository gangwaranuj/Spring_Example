package com.workmarket.web.controllers.admin;

import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.web.cachebusting.CacheBusterServiceImpl;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import com.workmarket.web.forms.admin.CreditMemoForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.View;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class AdminAccountingCreditMemoControllerTest extends BaseControllerUnitTest {

	@Mock private View mockView;
	@Mock private AbstractInvoice adhocInvoice;
	@Mock private CreditMemoForm form;
	@Mock private BillingService billingService;
	@Mock private MessageBundleHelper messageBundleHelper;
	@Mock private CacheBusterServiceImpl cacheBusterService;
	@InjectMocks AdminAccountingController controller;

	private MockMvc mockMvc;
	Long invoiceId;

	protected static class MockAdminAccountingCreditMemoControllerRequest {
		public static MockHttpServletRequestBuilder creditMemo(Long invoiceId) {
			return MockMvcRequestBuilders.get("/admin/accounting/credit_memo").param("invoiceId", String.valueOf(invoiceId));
		}
		public static MockHttpServletRequestBuilder issueCreditMemo() {
			return MockMvcRequestBuilders.post("/admin/accounting/credit_memo");
		}
	}

	@Before
	public void setUp() throws Exception {
		invoiceId = 1L;
		adhocInvoice = mock(AbstractServiceInvoice.class);
		form = mock(CreditMemoForm.class);
		when(billingService.findInvoiceById(invoiceId)).thenReturn(adhocInvoice);

		mockMvc = standaloneSetup(controller)
			.setSingleView(mockView)
			.build();
	}

	@Test
	public void creditMemo_withValidInvoiceId_IsOK() throws Exception {

		mockMvc.perform(AdminAccountingCreditMemoControllerTest.MockAdminAccountingCreditMemoControllerRequest.creditMemo(invoiceId))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("invoiceRef"))
			.andExpect(model().attributeExists("reasons"))
			.andExpect(view().name("web/pages/admin/accounting/credit_memo"));

		verify(billingService).findInvoiceById(invoiceId);
		assertNotNull(adhocInvoice);
	}

	@Test
	public void creditMemo_withInValidInvoiceId_IsOK() throws Exception {

		when(billingService.findInvoiceById(invoiceId)).thenReturn(null);
		mockMvc.perform(AdminAccountingCreditMemoControllerTest.MockAdminAccountingCreditMemoControllerRequest.creditMemo(invoiceId))
			.andExpect(status().isOk())
			.andExpect(model().attributeDoesNotExist("invoiceRef"))
			.andExpect(model().attributeExists("reasons"))
			.andExpect(view().name("redirect:web/pages/admin/accounting/workmarket_invoices"));

		verify(billingService).findInvoiceById(invoiceId);
	}


	@Test
	public void creditMemo_withValidInvoiceId_IssueCreditMemo_sucess() throws Exception {
		mockMvc.perform(AdminAccountingCreditMemoControllerTest.MockAdminAccountingCreditMemoControllerRequest.issueCreditMemo()
			.param("refInvoiceId", String.valueOf(invoiceId))
			.param("reason", String.valueOf(2))
			.param("note", "write off")
		).andExpect(status().isOk())
			.andExpect(model().hasNoErrors())
		.andExpect(view().name("redirect:/admin/accounting/workmarket_invoices"));

		verify(billingService).findInvoiceById(invoiceId);
		assertNotNull(adhocInvoice);
	}

	@Test
	public void creditMemo_withValidInvoiceId_IssueCreditMemo_withValidationError() throws Exception {

		mockMvc.perform(AdminAccountingCreditMemoControllerTest.MockAdminAccountingCreditMemoControllerRequest.issueCreditMemo()
			.param("refInvoiceId", String.valueOf(invoiceId))
			.param("reason", "")
			.param("note", "")
		).andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(model().attributeHasFieldErrors("form", "note"))
			.andExpect(model().attributeHasFieldErrors("form", "reason"))
		.andExpect(view().name("web/pages/admin/accounting/credit_memo"));
	}
}
