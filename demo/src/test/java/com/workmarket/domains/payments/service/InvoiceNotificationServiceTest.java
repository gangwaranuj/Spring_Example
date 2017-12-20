package com.workmarket.domains.payments.service;

import com.google.common.collect.Sets;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.domains.payments.dao.InvoiceDAO;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.work.model.Work;
import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.account.InvoiceNotificationService;
import com.workmarket.service.business.account.InvoiceNotificationServiceImpl;
import com.workmarket.service.business.wrapper.InvoiceResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by nick on 8/1/13 2:18 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoiceNotificationServiceTest {

	@Mock AuthenticationService authenticationService;
	@Mock NotificationDispatcher notificationDispatcher;
	@Mock EmailTemplateFactory emailTemplateFactory;
	@Mock InvoiceDAO invoiceDAO;
	@InjectMocks InvoiceNotificationService service = new InvoiceNotificationServiceImpl();

	private Work work;
	private AccountStatementDetailRow invoiceDetail;
	private User user1;
	private User user2;

	@Before
	public void setupInvoiceTest() {
		doNothing().when(invoiceDAO).saveOrUpdate(any(Invoice.class));
		when(notificationDispatcher.dispatchEmail(any(EmailTemplate.class)))
				.thenReturn(new EmailNotifyResponse(EmailNotifyResponse.Status.OK));

		Company company = new Company();
		company.setInvoiceSentToEmail("jean.pierre@zombeau.com");

		Invoice invoice = new Invoice();
		invoice.setId(123L);
		invoice.setInvoiceNumber("12345-54321");

		work = new Work();
		work.setId(5L);
		work.setCompany(company);
		work.setInvoice(invoice);

		invoiceDetail = new AccountStatementDetailRow();
		invoiceDetail.setInvoiceNumber(invoice.getInvoiceNumber());

		user1 = new User();
		user1.setEmail("user1@test.com");
		user2 = new User();
		user2.setEmail("user2@test.com");
		when(authenticationService.findAllUsersSubscribedToNewAssignmentInvoices(any(Work.class))).thenReturn(Sets.<User>newHashSet(
				user1, user2
		));
	}

	@Test(expected = NullPointerException.class)
	public void sendInvoicePdfToAutoInvoiceEnabledUsersForWork_nullWork_exception() {
		service.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(null, invoiceDetail);
	}

	@Test(expected = NullPointerException.class)
	public void sendInvoicePdfToAutoInvoiceEnabledUsersForWork_nullInvoice_exception() {
		service.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(work, null);
	}

	@Test
	public void sendInvoicePdfToAutoInvoiceEnabledUsersForWork_emptyInvoiceToEmail_fail() {
		work.getCompany().setInvoiceSentToEmail("");
		InvoiceResponse response = service.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(work, invoiceDetail);
		assertFalse(response.isSuccessful());
	}

	@Test
	public void sendInvoicePdfToAutoInvoiceEnabledUsersForWork_validWorkAndInvoice_templateBuilt() {
		service.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(work, invoiceDetail);
		verify(emailTemplateFactory, times(1)).buildInvoiceDetailEmailTemplate(work.getCompany().getInvoiceSentToEmail(), work.getInvoice(), invoiceDetail);
	}

	@Test
	public void sendInvoicePdfToAutoInvoiceEnabledUsersForWork_validWorkAndInvoice_notificationSent() {
		service.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(work, invoiceDetail);
		verify(notificationDispatcher, times(1)).dispatchEmail(any(EmailTemplate.class));
	}

	@Test
	public void sendInvoicePdfToAutoInvoiceEnabledUsersForWork_validWorkAndInvoice_successful() {
		InvoiceResponse response = service.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(work, invoiceDetail);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void sendInvoicePdfToAutoInvoiceEnabledUsersForWork_summaryInvoice_downloadCountUpdated() {
		work.setInvoice(new InvoiceSummary());
		service.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(work, invoiceDetail);
		verify(invoiceDAO, times(1)).saveOrUpdate(any(InvoiceSummary.class));
	}

	@Test(expected = NullPointerException.class)
	public void sendInvoicePdfToSubscribedUsersForWork_nullWork_exception() {
		service.sendInvoicePdfToSubscribedUsersForWork(null, invoiceDetail);
	}

	@Test(expected = NullPointerException.class)
	public void sendInvoicePdfToSubscribedUsersForWork_nullInvoice_exception() {
		service.sendInvoicePdfToSubscribedUsersForWork(work, null);
	}

	@Test
	public void sendInvoicePdfToSubscribedUsersForWork_emptyInvoiceToEmail_notSent() {
		work.getCompany().setInvoiceSentToEmail("");
		service.sendInvoicePdfToSubscribedUsersForWork(work, invoiceDetail);
		verify(emailTemplateFactory, never()).buildInvoiceDetailEmailTemplate(work.getCompany().getInvoiceSentToEmail(), work.getInvoice(), invoiceDetail);
	}

	@Test
	public void sendInvoicePdfToSubscribedUsersForWork_validWorkAndInvoiceOneInvalidEmail_OneTemplateBuilt() {
		User invalidEmailUser = new User();
		invalidEmailUser.setEmail("barf");
		when(authenticationService.findAllUsersSubscribedToNewAssignmentInvoices(any(Work.class))).thenReturn(Sets.<User>newHashSet(
				user1, invalidEmailUser
		));
		service.sendInvoicePdfToSubscribedUsersForWork(work, invoiceDetail);
		verify(emailTemplateFactory, times(1)).buildInvoiceDetailEmailTemplate(any(String.class), eq(work.getInvoice()), eq(invoiceDetail));
	}

	@Test
	public void sendInvoicePdfToSubscribedUsersForWork_validWorkAndInvoice_templatesBuilt() {
		service.sendInvoicePdfToSubscribedUsersForWork(work, invoiceDetail);
		verify(emailTemplateFactory, times(2)).buildInvoiceDetailEmailTemplate(any(String.class), eq(work.getInvoice()), eq(invoiceDetail));
	}

	@Test
	public void sendInvoicePdfToSubscribedUsersForWork_validWorkAndInvoice_notificationSent() {
		service.sendInvoicePdfToSubscribedUsersForWork(work, invoiceDetail);
		verify(notificationDispatcher, times(2)).dispatchEmail(any(EmailTemplate.class));
	}

	@Test
	public void sendInvoicePdfToSubscribedUsersForWork_validWorkAndInvoice_successful() {
		InvoiceResponse response = service.sendInvoicePdfToSubscribedUsersForWork(work, invoiceDetail);
		assertTrue(response.isSuccessful());
	}

	@Test
	public void sendInvoicePdfToSubscribedUsersForWork_summaryInvoice_downloadCountUpdated() {
		work.setInvoice(new InvoiceSummary());
		service.sendInvoicePdfToSubscribedUsersForWork(work, invoiceDetail);
		verify(invoiceDAO, times(2)).saveOrUpdate(any(InvoiceSummary.class));
	}


}