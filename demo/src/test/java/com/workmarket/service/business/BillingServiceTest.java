package com.workmarket.service.business;

import com.google.api.client.util.Sets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.FastFundsReceivableCommitment;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.domains.payments.dao.FastFundsReceivableCommitmentDAO;
import com.workmarket.domains.payments.dao.PaymentConfigurationDAO;
import com.workmarket.domains.payments.dao.PaymentPeriodDAO;
import com.workmarket.domains.payments.dao.InvoiceDAO;
import com.workmarket.domains.payments.dao.InvoiceDueLogDAO;
import com.workmarket.domains.payments.dao.InvoiceLineItemDAO;
import com.workmarket.domains.payments.dao.ServiceInvoiceDAO;
import com.workmarket.domains.payments.dao.StatementDAO;
import com.workmarket.domains.payments.dao.AccountStatementDetailDAO;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.payment.AccountingProcessTime;
import com.workmarket.domains.model.account.payment.PaymentCycle;
import com.workmarket.domains.model.account.payment.PaymentMethod;
import com.workmarket.domains.model.account.payment.PaymentTermsDays;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.ServiceInvoicePagination;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.payments.service.AccountRegisterServicePaymentTermsImpl;
import com.workmarket.domains.payments.service.BillingServiceImpl;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.payments.service.AccountRegisterServicePrefundImpl;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.account.InvoiceLineItemFactory;
import com.workmarket.service.business.account.SubscriptionCalculator;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.dto.invoice.InvoiceDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class BillingServiceTest {

	@Mock InvoiceDAO invoiceDAO;
	@Mock AuthenticationService authenticationService;
	@Mock EmailTemplateFactory emailTemplateFactory;
	@Mock NotificationDispatcher notificationDispatcher;
	@Mock WorkService workService;
	@Mock UserService userService;
	@Mock ServiceInvoiceDAO serviceInvoiceDAO;
	@Mock StatementDAO statementDAO;
	@Mock WorkStatusService workStatusService;
	@Mock AccountRegisterServicePrefundImpl accountRegisterService;
	@Mock CompanyService companyService;
	@Mock PaymentConfigurationDAO paymentConfigurationDAO;
	@Mock AccountStatementDetailDAO accountStatementDetailDAO;
	@Mock com.workmarket.common.template.pdf.PDFTemplateFactory PDFTemplateFactory;
	@Mock TemplateService templateService;
	@Mock InvoiceLineItemFactory invoiceLineItemFactory;
	@Mock InvoiceLineItemDAO invoiceLineItemDAO;
	@Mock LookupEntityDAO lookupEntityDAO;
	@Mock PaymentPeriodDAO paymentPeriodDAO;
	@Mock InvoiceDueLogDAO invoiceDueLogDAO;
	@Mock UserNotificationService userNotificationService;
	@Mock InvoicePaymentHelper invoicePaymentHelper;
	@Mock SubscriptionService subscriptionService;
	@Mock EventRouter eventRouter;
	@Mock PricingService pricingService;
	@Mock AccountRegisterServicePaymentTermsImpl accountRegisterServicePaymentTerms;
	@Mock FastFundsReceivableCommitmentDAO fastFundsReceivableCommitmentDAO;
	@Mock SubscriptionCalculator subscriptionCalculator;
	@Mock WorkResourceService workResourceService;

	@InjectMocks BillingServiceImpl billingService;

	private InvoiceSummary invoiceSummary, invoiceSummaryNotPaymentPending;
	private Invoice invoiceOne, invoiceTwo;
	private Calendar downloadOn;
	private User user1, user2;
	private Company company1, company2;
	private String validEmail, emptyEmail;
	private Work work;
	private WorkResource workResource;
	private WorkWorkResourceAccountRegister workWorkResourceAccountRegister;
	private AccountRegister workerAccountRegister, buyerAccountRegister;
	private WorkCostDTO workCostDTO;
	private WorkResourceTransaction workResourceTransaction;
	private InvoiceDTO invoiceDTO;

	private static Long
		INVOICE_ONE_ID = 3L,
		INVOICE_TWO_ID = 4L,
		WORK_RESOURCE_ID = 5L,
		WORK_ID = 1L,
		COMPANY_ONE_ID = 5L,
		COMPANY_TWO_ID = 2L,
		BUYER_USER_ID = 123L,
		WORKER_USER_ID = 456L;
	private static BigDecimal TOTAL_RESOURCE_COST = new BigDecimal(10);
	private static Calendar FAST_FUNDS_EFFECTIVE_DATE = Calendar.getInstance();

	@Before
	public void setup() {
		when(notificationDispatcher.dispatchEmail(any(EmailTemplate.class)))
				.thenReturn(new EmailNotifyResponse(EmailNotifyResponse.Status.OK));

		invoiceSummary = new InvoiceSummary();
		invoiceSummaryNotPaymentPending = new InvoiceSummary();
		invoiceOne = mock(Invoice.class);
		invoiceTwo = mock(Invoice.class);
		user1 = mock(User.class);
		user2 = mock(User.class);
		company1 = mock(Company.class);
		company2 = mock(Company.class);
		downloadOn = DateUtilities.getCalendarNow();
		validEmail = "delicious@mokito.com";
		emptyEmail = "";
		workerAccountRegister = mock(AccountRegister.class);
		buyerAccountRegister = mock(AccountRegister.class);
		workWorkResourceAccountRegister = mock(WorkWorkResourceAccountRegister.class);

		workResourceTransaction = mock(WorkResourceTransaction.class);
		when(workResourceTransaction.getEffectiveDate()).thenReturn(FAST_FUNDS_EFFECTIVE_DATE);

		workCostDTO = mock(WorkCostDTO.class);
		when(workCostDTO.getTotalResourceCost()).thenReturn(TOTAL_RESOURCE_COST);

		when(user1.getId()).thenReturn(BUYER_USER_ID);
		when(user2.getId()).thenReturn(WORKER_USER_ID);
		when(authenticationService.getCurrentUser()).thenReturn(user1);
		when(user1.getCompany()).thenReturn(company1);
		when(user1.isAdminOrManager()).thenReturn(true);
		when(user2.getCompany()).thenReturn(company2);
		when(company1.getId()).thenReturn(COMPANY_ONE_ID);
		when(company1.getCompanyNumber()).thenReturn("123");
		when(company2.getId()).thenReturn(COMPANY_TWO_ID);
		when(company2.getCompanyNumber()).thenReturn("456");
		when(workWorkResourceAccountRegister.getWorkResourceId()).thenReturn(WORK_RESOURCE_ID);
		when(pricingService.findDefaultRegisterForCompany(anyLong(), anyBoolean())).thenReturn(workerAccountRegister);
		when(pricingService.findDefaultRegisterForCompany(anyLong())).thenReturn(buyerAccountRegister);
		when(accountRegisterServicePaymentTerms.calculateCostOnCompleteWork(any(Work.class), any(WorkResource.class))).thenReturn(workCostDTO);
		when(accountRegisterServicePaymentTerms.createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class)))
			.thenReturn(workResourceTransaction);
		when(authenticationService.getCurrentUser()).thenReturn(user2);

		invoiceSummary.setId(1L);
		invoiceSummary.setSentTo("drunk@mockito.com");
		invoiceSummary.setSentOn(DateUtilities.getCalendarNow());
		invoiceSummary.setDownloadedOn(DateUtilities.getCalendarNow());
		invoiceSummary.setInvoiceStatusType(new InvoiceStatusType("pending"));
		invoiceSummary.setCompany(company1);
		invoiceSummary.setCreatorId(user1.getId());

		invoiceSummaryNotPaymentPending.setSentTo("drunk@mockito.com");
		invoiceSummaryNotPaymentPending.setSentOn(DateUtilities.getCalendarNow());
		invoiceSummaryNotPaymentPending.setDownloadedOn(DateUtilities.getCalendarNow());
		invoiceSummaryNotPaymentPending.setInvoiceStatusType(new InvoiceStatusType("paid"));

		when(invoiceOne.getId()).thenReturn(INVOICE_ONE_ID);
		when(invoiceTwo.getId()).thenReturn(INVOICE_TWO_ID);
		when(invoiceOne.getCompany()).thenReturn(company1);
		when(invoiceTwo.getCompany()).thenReturn(company2);
		when(invoiceOne.isPaymentPending()).thenReturn(true);
		when(invoiceOne.getFastFundedOn()).thenReturn(null);
		when(invoiceOne.getActiveWorkResourceId()).thenReturn(WORK_RESOURCE_ID);
		when(invoiceTwo.getActiveWorkResourceId()).thenReturn(WORK_RESOURCE_ID);

		when(invoiceDAO.get(INVOICE_ONE_ID)).thenReturn(invoiceOne);
		when(invoiceDAO.get(INVOICE_TWO_ID)).thenReturn(invoiceTwo);
		when(invoiceDAO.findInvoiceById(1L)).thenReturn(invoiceSummary);
		when(invoiceDAO.findInvoiceById(2L)).thenReturn(invoiceSummaryNotPaymentPending);
		when(invoiceDAO.findInvoiceById(INVOICE_ONE_ID)).thenReturn(invoiceOne);
		when(invoiceDAO.findInvoiceById(INVOICE_TWO_ID)).thenReturn(invoiceTwo);
		when(invoiceDAO.findAllNonFastFundedAndDueInvoiceIdsToUser(any(Calendar.class), anyLong()))
			.thenReturn(Lists.newArrayList(INVOICE_ONE_ID, INVOICE_TWO_ID));

		when(companyService.isFastFundsEnabled(company1)).thenReturn(true);
		when(companyService.isFastFundsEnabled(company2)).thenReturn(false);

		work = mock(Work.class);
		when(work.isPaymentPending()).thenReturn(true);
		WorkStatusType workStatusType = mock(WorkStatusType.class);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.PAYMENT_PENDING);
		when(work.getWorkStatusType()).thenReturn(workStatusType);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getCompany()).thenReturn(company1);

		workResource = mock(WorkResource.class);
		when(workResource.getUser()).thenReturn(user2);

		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(workService.findWorkResourceById(anyLong())).thenReturn(workResource);
		when(workService.findWorkAndWorkResourceForPayment(Lists.newArrayList(WORK_ID)))
			.thenReturn(Lists.newArrayList(workWorkResourceAccountRegister));
		when(workService.findWorkByInvoice(anyLong())).thenReturn(work);

		invoiceDTO = mock(InvoiceDTO.class);
		when(invoiceDTO.getCompanyId()).thenReturn(COMPANY_ONE_ID);

		Calendar dueDate = Calendar.getInstance();
		dueDate.add(Calendar.YEAR, 1);
		when(invoiceDTO.getDueDate()).thenReturn(dueDate);

		InvoiceLineItemDTO invoiceLineItemDTO = mock(InvoiceLineItemDTO.class);
		when(invoiceDTO.getLineItemDTOList()).thenReturn(Lists.newArrayList(invoiceLineItemDTO));
	}

	@Test
	public void validateAccessToFastFundInvoice_invoiceNotFound_returnFalse() {
		when(invoiceDAO.findInvoiceById(INVOICE_ONE_ID)).thenReturn(null);

		assertFalse(billingService.validateAccessToFastFundInvoice(INVOICE_ONE_ID));
	}

	@Test
	public void validateAccessToFastFundInvoice_currentUserIsNull_exceptionThrown() {
		when(authenticationService.getCurrentUser()).thenReturn(null);

		assertFalse(billingService.validateAccessToFastFundInvoice(INVOICE_ONE_ID));
	}

	@Test
	public void validateAccessToFastFundInvoice_currentUserCompanyIdIsNull_exceptionThrown() {
		when(company1.getId()).thenReturn(null);

		assertFalse(billingService.validateAccessToFastFundInvoice(INVOICE_ONE_ID));
	}

	@Test
	public void validateAccessToFastFundInvoice_invoiceCompanyIdIsNull_exceptionThrown() {
		when(invoiceOne.getCompany()).thenReturn(company2);
		when(company2.getId()).thenReturn(null);

		assertFalse(billingService.validateAccessToFastFundInvoice(INVOICE_ONE_ID));
	}

	@Test
	public void validateAccessToFastFundInvoice_buyerCompanyIsTryingToFastFundInvoice_returnFalse() {
		when(authenticationService.getCurrentUser()).thenReturn(user1);

		assertFalse(billingService.validateAccessToFastFundInvoice(INVOICE_ONE_ID));
	}

	@Test
	public void validateAccessToFastFundInvoice_workerCompanyIsTryingToFastFundInvoice_returnTrue() {
		when(authenticationService.getCurrentUser()).thenReturn(user2);

		assertTrue(billingService.validateAccessToFastFundInvoice(INVOICE_ONE_ID));
	}

	@Test
	public void hasAtLeastOneFastFundableInvoice_atLeastOneInvoiceIsFastFundable_returnTrue() {
		assertTrue(billingService.hasAtLeastOneFastFundableInvoice(WORKER_USER_ID));
	}

	@Test
	public void hasAtLeastOneFastFundableInvoice_noInvoicesAreFastFundable_returnFalse() {
		when(companyService.isFastFundsEnabled(any(Company.class))).thenReturn(false);

		assertFalse(billingService.hasAtLeastOneFastFundableInvoice(WORKER_USER_ID));
	}

	@Test
	public void hasAtLeastOneFastFundableInvoice_noInvoiceIdsFound_returnFalse() {
		when(invoiceDAO.findAllNonFastFundedAndDueInvoiceIdsToUser(any(Calendar.class), anyLong()))
			.thenReturn(Collections.EMPTY_LIST);

		assertFalse(billingService.hasAtLeastOneFastFundableInvoice(WORKER_USER_ID));
	}

	@Test(expected = IllegalArgumentException.class)
	public void fastFundInvoice_invoiceNotFound_exceptionThrown() {
		when(invoiceDAO.findInvoiceById(anyLong())).thenReturn(null);

		billingService.fastFundInvoice(1L, 1L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fastFundInvoice_invoiceNotInPaymentPendingStatus_exceptionThrown() {
		when(invoiceOne.isPaymentPending()).thenReturn(false);

		billingService.fastFundInvoice(INVOICE_ONE_ID, WORK_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fastFundInvoice_invoiceIsAlreadyFastFunded_exceptionThrown() {
		when(invoiceOne.getFastFundedOn()).thenReturn(Calendar.getInstance());

		billingService.fastFundInvoice(INVOICE_ONE_ID, WORK_ID);
	}

	@Test
	public void fastFundInvoice_withInvoiceIdAndWorkId_fastFundsReceivableCommitmentSaved() {
		billingService.fastFundInvoice(INVOICE_ONE_ID, WORK_ID);

		verify(fastFundsReceivableCommitmentDAO, times(1))
			.saveOrUpdate(any(FastFundsReceivableCommitment.class));
	}

	@Test
	public void fastFundInvoice_withInvoiceIdAndWorkId_fastFundsPaymentCreatedAndExecuted() {
		billingService.fastFundInvoice(INVOICE_ONE_ID, WORK_ID);

		verify(accountRegisterServicePaymentTerms, times(1))
			.createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(eq(workResource), eq(workerAccountRegister), any(BigDecimal.class));
	}

	@Test
	public void fastFundInvoice_withInvoiceIdAndWorkId_fastFundsFeeCreatedAndExecuted() {
		billingService.fastFundInvoice(INVOICE_ONE_ID, WORK_ID);

		verify(accountRegisterServicePaymentTerms, times(1))
			.createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(eq(workResource), eq(workerAccountRegister), any(BigDecimal.class));
	}

	@Test
	public void fastFundInvoice_withInvoiceIdAndWorkId_invoiceFastFundedOnDateSet() {
		billingService.fastFundInvoice(INVOICE_ONE_ID, WORK_ID);

		verify(invoiceOne, times(1)).setFastFundedOn(FAST_FUNDS_EFFECTIVE_DATE);
	}

	@Test
	public void fastFundInvoice_withInvoiceIdAndWorkId_successResponseReturned() {
		FastFundInvoiceResponse fastFundInvoiceResponse = billingService.fastFundInvoice(INVOICE_ONE_ID, WORK_ID);

		assertTrue(fastFundInvoiceResponse.isSuccess());
	}

	@Test
	public void fastFundInvoice_noWorkWorkResourceAccountRegisterFound_failureResponseReturned() {
		when(workService.findWorkAndWorkResourceForPayment(Lists.newArrayList(WORK_ID)))
			.thenReturn(Collections.EMPTY_LIST);

		FastFundInvoiceResponse fastFundInvoiceResponse = billingService.fastFundInvoice(INVOICE_ONE_ID, WORK_ID);

		assertTrue(fastFundInvoiceResponse.isFail());
	}

	@Test(expected = IllegalArgumentException.class)
	public void fastFundInvoice_invoiceId_workCannotBeFoundByInvoiceId_exceptionThrown() {
		when(workService.findWorkByInvoice(anyLong())).thenReturn(null);

		billingService.fastFundInvoice(INVOICE_ONE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fastFundInvoice_invoiceId_failureResponseReturned() {
		when(workService.findWorkByInvoice(anyLong())).thenReturn(null);

		billingService.fastFundInvoice(INVOICE_ONE_ID);
	}

	@Test
	public void unlockInvoice_withPaymentPendingInvoice_success() {
		billingService.unlockInvoice(1L);

		assertNull(invoiceSummary.getSentTo());
		assertNull(invoiceSummary.getSentOn());
		assertNull(invoiceSummary.getDownloadedOn());
	}

	@Test
	public void unlockInvoice_withPaidInvoice_fail() {
		billingService.unlockInvoice(2L);

		assertNotNull(invoiceSummaryNotPaymentPending.getSentTo());
		assertNotNull(invoiceSummaryNotPaymentPending.getSentOn());
		assertNotNull(invoiceSummaryNotPaymentPending.getDownloadedOn());
	}

	@Test
	public void updateInvoiceLastDownloadedDate_success(){
		billingService.updateInvoiceLastDownloadedDate(1L, downloadOn);

		assertEquals(downloadOn.getTimeInMillis(), invoiceSummary.getDownloadedOn().getTimeInMillis());
	}

	@Test
	public void emailInvoiceToUser_emptyEmail_fail(){
		billingService.emailInvoiceToUser(emptyEmail, 1L);

		verify(emailTemplateFactory, never()).buildInvoiceEmailTemplate(validEmail, invoiceSummary);
	}

	@Test
	public void emailInvoiceToUser_validEmailAndInvoice_templateBuilt(){
		billingService.emailInvoiceToUser(validEmail, 1L);

		verify(emailTemplateFactory, times(1)).buildInvoiceEmailTemplate(validEmail, invoiceSummary);
	}

	@Test
	public void emailInvoiceToUser_validEmailAndInvoice_notificationSent(){
		billingService.emailInvoiceToUser(validEmail, 1L);

		verify(notificationDispatcher, times(1)).dispatchEmail(any(EmailTemplate.class));
	}

	@Test
	public void emailInvoiceToUser_validEmailAndInvoice_sentTo(){
		billingService.emailInvoiceToUser(validEmail, 1L);

		assertEquals(invoiceSummary.getSentTo(), validEmail);
	}

	@Test
	public void voidWorkInvoice_setDueDateNull_voidInvoice() {

		Work work = mock(Work.class);
		when(work.getId()).thenReturn(101L);
		Invoice invoice = mock(Invoice.class);
		when(work.getInvoice()).thenReturn(invoice);
		when(work.isPaid()).thenReturn(false);
		when(invoice.isPaid()).thenReturn(false);
		when(invoice.isBundled()).thenReturn(false);
		when(work.hasStatement()).thenReturn(false);

		billingService.voidWorkInvoice(work);

		verify(invoice).setDueDate(null);
		verify(invoice).setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.VOID));
		verify(invoice).setCancelPaymentWorkId(work.getId());

	}

	@Test
	public void generateInvoiceSummary_haveVoidAndPaidInvoice_calculateStatementRemainingBalance() {
		// Invoice1,2,3,4 belong to a statement
		// Invoice 1 was void.
		// Invoice 2 was paid.
		Invoice invoice1 = mock(Invoice.class);
		Invoice invoice2 = mock(Invoice.class);
		Invoice invoice3 = mock(Invoice.class);
		Invoice invoice4 = mock(Invoice.class);
		Statement statement = mock(Statement.class);
		Set<Invoice> invoices = Sets.newHashSet();
		invoices.add(invoice1);
		invoices.add(invoice2);
		invoices.add(invoice3);
		invoices.add(invoice4);
		when(statement.getInvoices()).thenReturn(invoices);
		when(invoice1.isVoid()).thenReturn(true);
		when(invoice2.isPaid()).thenReturn(true);
		when(invoice1.getBalance()).thenReturn(BigDecimal.ONE);
		when(invoice2.getBalance()).thenReturn(BigDecimal.ONE);
		when(invoice3.getBalance()).thenReturn(BigDecimal.ONE);
		when(invoice4.getBalance()).thenReturn(BigDecimal.ONE);

		billingService.generateInvoiceSummary(statement);
		verify(statement).setRemainingBalance(BigDecimal.valueOf(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateInvoiceForWork_withNullWork() {
		billingService.generateInvoiceForWork(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateInvoiceForWork_withNullInvoice() {
		billingService.generateInvoiceForWork(mock(Work.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateInvoiceForWork_withWorkInInvalidStatus() {
		when(work.isPaymentPending()).thenReturn(false);
		when(work.isPaid()).thenReturn(false);
		when(work.isClosed()).thenReturn(false);
		billingService.generateInvoiceForWork(work);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateInvoiceForWork_withNoFulfillmentStrategy_fail() {
		billingService.generateInvoiceForWork(work);
	}

	@Test
	public void generateInvoiceForWork_success() {
		FulfillmentStrategy fulfillmentStrategy = mock(FulfillmentStrategy.class);
		when(work.getFulfillmentStrategy()).thenReturn(fulfillmentStrategy);
		Invoice invoice = billingService.generateInvoiceForWork(work);
		assertNotNull(invoice);
		assertTrue(invoice.isPaymentPending());
	}

	@Test
	public void generateInvoiceForWork_withPaidWork_returnsPaidInvoice() {
		FulfillmentStrategy fulfillmentStrategy = mock(FulfillmentStrategy.class);
		when(work.getFulfillmentStrategy()).thenReturn(fulfillmentStrategy);
		when(work.isPaid()).thenReturn(true);
		Invoice invoice = billingService.generateInvoiceForWork(work);
		assertNotNull(invoice);
		assertTrue(invoice.isPaid());
	}

	@Test
	public void findInvoiceByWorkId_withNonexistentWork_returnsNull() {
		assertNull(billingService.findInvoiceByWorkId(1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findAllInvoicesByCompany_withNullPagination_fail() {
		billingService.findAllInvoicesByCompany(1L, null);
	}

	@Test
	public void findAllInvoicesByCompany_success() {
		billingService.findAllInvoicesByCompany(1L, new InvoicePagination());
		verify(invoiceDAO, times(1)).findAllByCompanyId(anyLong(), any(InvoicePagination.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findAllServiceInvoices_withNullPagination_fail() {
		billingService.findAllServiceInvoices(null);
	}

	@Test
	public void findAllServiceInvoices_success() {
		billingService.findAllServiceInvoices(new ServiceInvoicePagination());
		verify(serviceInvoiceDAO, times(1)).findAll(any(ServiceInvoicePagination.class));
	}

	@Test
	public void getAllServiceInvoicesTotalsByStatus_success() {
		billingService.getAllServiceInvoicesTotalsByStatus();
		verify(serviceInvoiceDAO, times(1)).getAllServiceInvoicesTotal();
	}

	@Test(expected = IllegalArgumentException.class)
	public void getStatementDashboard_withNullAuthenticationUser_fails() {
		when(authenticationService.getCurrentUser()).thenReturn(null);
		assertNotNull(billingService.getStatementDashboard(new AccountStatementFilters(), new AccountStatementDetailPagination()));
	}

	@Test
	public void getStatementDashboard_success() {
		assertNull(billingService.getStatementDashboard(new AccountStatementFilters(), new AccountStatementDetailPagination()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getStatementDashboardWithBundledInvoices_withNullAuthenticationUser_fails() {
		when(authenticationService.getCurrentUser()).thenReturn(null);
		assertNotNull(billingService.getStatementDashboardWithBundledInvoices(new AccountStatementFilters(), new AccountStatementDetailPagination()));
	}

	@Test
	public void getStatementDashboardWithBundledInvoices_success() {
		assertNull(billingService.getStatementDashboardWithBundledInvoices(new AccountStatementFilters(), new AccountStatementDetailPagination()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getStatementDashboardForUser_withNullUser_fails() {
		assertNotNull(billingService.getStatementDashboardForUser(new AccountStatementFilters(), new AccountStatementDetailPagination(), null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getStatementDashboardForUser_success() {
		billingService.getStatementDashboardForUser(new AccountStatementFilters(), new AccountStatementDetailPagination(), new User());
	}

	@Test(expected = IllegalArgumentException.class)
	public void payInvoices_withNullInvoices_fail() {
		billingService.payInvoices(1L, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateStatementPaymentTermsDays_withNullArguments_fail() {
		billingService.calculateStatementPaymentTermsDays(null, null, null);
	}

	@Test
	public void calculateStatementPaymentTermsDays_success() {
		PaymentTermsDays paymentTermsDays = billingService.calculateStatementPaymentTermsDays(PaymentCycle.MONTHLY, PaymentMethod.PRE_FUND, AccountingProcessTime.ONE_DAY);
		assertNotNull(paymentTermsDays);
		assertEquals(paymentTermsDays.getPaymentDays(), 40);
	}

	@Test
	public void findStatementPaymentConfigurationByCompany_success() {
		billingService.findStatementPaymentConfigurationByCompany(1L);
		verify(companyService, times(1)).findCompanyById(anyLong());
	}

	@Test(expected = IllegalArgumentException.class)
	public void issueAdHocInvoice_withNullInput_throwException() {
		billingService.issueAdHocInvoice(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void issueAdHocInvoice_withSubscriptionInvoice_andNullPaymentPeriod_throwException() {
		when(invoiceDTO.isSubscriptionInvoice()).thenReturn(true);

		billingService.issueAdHocInvoice(invoiceDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void issueAdHocInvoice_withSubscriptionInvoice_andPlanSubscriptionType_andNoActiveSubscription_throwException() {
		when(invoiceDTO.isSubscriptionInvoice()).thenReturn(true);
		when(invoiceDTO.getSubscriptionInvoiceTypeCode()).thenReturn(SubscriptionInvoiceType.REGULAR);
		when(subscriptionCalculator.getCurrentSubscriptionDetails(COMPANY_ONE_ID)).thenReturn(Maps.<String, Object>newHashMap());

		billingService.issueAdHocInvoice(invoiceDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void issueAdHocInvoice_withSubscriptionInvoice_andPlanSubscriptionType_andActiveSubscription_saveInvoice() {
		when(invoiceDTO.isSubscriptionInvoice()).thenReturn(true);
		when(invoiceDTO.getSubscriptionInvoiceTypeCode()).thenReturn(SubscriptionInvoiceType.REGULAR);
		when(subscriptionCalculator.getCurrentSubscriptionDetails(COMPANY_ONE_ID)).thenReturn(ImmutableMap.<String, Object>of("sdf", "sadf"));

		billingService.issueAdHocInvoice(invoiceDTO);

		verify(serviceInvoiceDAO).saveOrUpdate(any(AbstractServiceInvoice.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void issueAdHocInvoice_withSubscriptionInvoice_andInvalidSubscriptionType_throwException() {
		when(invoiceDTO.isSubscriptionInvoice()).thenReturn(true);
		when(invoiceDTO.getSubscriptionInvoiceTypeCode()).thenReturn("flarp");

		billingService.issueAdHocInvoice(invoiceDTO);
	}

	@Test
	public void getNearestPaymentTermsDays_success() throws Exception {
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(5), PaymentTermsDays.SEVEN);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(1), PaymentTermsDays.SEVEN);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(7), PaymentTermsDays.SEVEN);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(14), PaymentTermsDays.FIFTEEN);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(8), PaymentTermsDays.FIFTEEN);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(19), PaymentTermsDays.TWENTY_ONE);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(21), PaymentTermsDays.TWENTY_ONE);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(28), PaymentTermsDays.THIRTY);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(30), PaymentTermsDays.THIRTY);
		assertEquals(PaymentTermsDays.getNearestPaymentTermsDays(35), PaymentTermsDays.FORTY);

		assertNull(PaymentCycle.getPaymentCycle(0));
		assertEquals(PaymentCycle.getPaymentCycle(1), PaymentCycle.DAILY);
	}

	@Test
	public void shouldReturnFastFundableInvoices() {
		final List<Invoice> invoices = getInvoices();

		assertEquals(1, invoices.size());
		assertEquals(invoiceOne, invoices.get(0));
	}

	@Test
	public void shouldNotReturnFastFundableInvoiceIfFeatureToggleOff() {
		when(companyService.isFastFundsEnabled(any(Company.class))).thenReturn(false);

		final List<Invoice> invoices = getInvoices();

		assertEquals(0, invoices.size());
	}

	@Test
	public void shouldReturnTotalFastFundableAmount() {
		final BigDecimal totalCost = getTotalCost();

		assertEquals(TOTAL_RESOURCE_COST, totalCost);
	}

	@Test
	public void shouldNotReturnTotalFastFundableAmountIfFeatureToggleOff() {
		when(companyService.isFastFundsEnabled(any(Company.class))).thenReturn(false);

		final BigDecimal totalCost = getTotalCost();

		assertEquals(BigDecimal.ZERO, totalCost);
	}

	private BigDecimal getTotalCost() {
		getInvoices();
		when(workService.findWork(WORK_ID, false)).thenReturn(work);
		when(workResourceService.findWorkResourceById(WORK_RESOURCE_ID)).thenReturn(workResource);
		when(workResourceService.findActiveWorkResource(WORK_ID)).thenReturn(workResource);
		when(accountRegisterServicePaymentTerms.calculateCostOnCompleteWork(work, workResource))
			.thenReturn(new WorkCostDTO(TOTAL_RESOURCE_COST, BigDecimal.ZERO, BigDecimal.ZERO));

		return billingService.calculateTotalFastFundableResourceCostForWorker(WORKER_USER_ID);
	}

	private List<Invoice> getInvoices() {
		when(invoiceDAO.findAllNonFastFundedAndDueInvoiceIdsToUser(any(Calendar.class), any(Long.class)))
			.thenReturn(ImmutableList.of(INVOICE_ONE_ID));
		when(invoiceDAO.get(INVOICE_ONE_ID)).thenReturn(invoiceOne);

		return billingService.findAllFastFundableInvoicesForWorker(WORKER_USER_ID);
	}
}
