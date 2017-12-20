package com.workmarket.service.infra.event.transactional;

import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.account.InvoiceNotificationService;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkInvoiceSendType;
import com.workmarket.service.business.wrapper.InvoiceResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by nick on 8/1/13 3:43 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class EventServiceInvoiceEventUnitTest {


	@Mock WorkService workService;
	@Mock InvoiceNotificationService invoiceNotificationService;
	@Mock BillingService billingService;
	@InjectMocks EventService service = new EventServiceImpl();

	private static final long WORK_ID = 2L;

	@Before
	public void setupEventService() {
		when(workService.findWork(WORK_ID))
				.thenReturn(new Work());
		when(billingService.findAccountStatementDetailByInvoiceId(any(Long.class), any(User.class)))
				.thenReturn(new AccountStatementDetailRow());
		when(invoiceNotificationService.sendInvoiceToUsers(any(Work.class), any(AccountStatementDetailRow.class)))
				.thenReturn(new InvoiceResponse(BaseStatus.SUCCESS));
		when(invoiceNotificationService.sendInvoicePdfToAutoInvoiceEnabledUsersForWork(any(Work.class), any(AccountStatementDetailRow.class)))
				.thenReturn(new InvoiceResponse(BaseStatus.SUCCESS));
		when(invoiceNotificationService.sendInvoicePdfToSubscribedUsersForWork(any(Work.class), any(AccountStatementDetailRow.class)))
				.thenReturn(new InvoiceResponse(BaseStatus.SUCCESS));
	}

	@Test
	public void processInvoiceEvent_SendToAllEvent_sendToUsersMethodCalled() {
		service.processEvent(new WorkInvoiceGenerateEvent(1L, WORK_ID, WorkInvoiceSendType.ALL));
		verify(invoiceNotificationService, times(1)).sendInvoiceToUsers(any(Work.class), any(AccountStatementDetailRow.class));
	}

	@Test
	public void processInvoiceEvent_SendToAutoPay_sendToAutoPayMethodCalled() {
		service.processEvent(new WorkInvoiceGenerateEvent(1L, WORK_ID, WorkInvoiceSendType.AUTOEMAIL));
		verify(invoiceNotificationService, times(1)).sendInvoicePdfToAutoInvoiceEnabledUsersForWork(any(Work.class), any(AccountStatementDetailRow.class));
	}

	@Test
	public void processInvoiceEvent_SendToSubscribed_sendToSubscribedMethodCalled() {
		service.processEvent(new WorkInvoiceGenerateEvent(1L, WORK_ID, WorkInvoiceSendType.SUBSCRIBED));
		verify(invoiceNotificationService, times(1)).sendInvoicePdfToSubscribedUsersForWork(any(Work.class), any(AccountStatementDetailRow.class));
	}
}
