package com.workmarket.domains.payments.service;

import com.google.common.collect.Lists;
import com.workmarket.dao.account.AccountingSummaryDAO;
import com.workmarket.dao.account.AccountingSummaryDetailDAO;
import com.workmarket.dao.account.FiscalYearDAO;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.domains.model.account.AccountingSummaryDetail;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.account.JournalEntrySummaryServiceImpl;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class JournalEntrySummaryServiceTest {

	@Mock AccountingSummaryDAO accountingSummaryDAO;
	@Mock AccountingSummaryDetailDAO accountingSummaryDetailDAO;
	@Mock	FiscalYearDAO fiscalYearDAO;
	@InjectMocks JournalEntrySummaryServiceImpl journalEntrySummaryService;

	private Calendar startDate;
	private Calendar startFiscalYear;
	private BigDecimal moneyInFastFunds = BigDecimal.valueOf(10);
	private BigDecimal moneyInChecks = BigDecimal.valueOf(100);
	private BigDecimal moneyInArch = BigDecimal.valueOf(50);
	private BigDecimal moneyInWire = BigDecimal.valueOf(80);
	private BigDecimal moneyInCreditCard = BigDecimal.valueOf(40);
	private BigDecimal moneyInSubscriptionCreditMemo = BigDecimal.valueOf(40);
	private BigDecimal amount = BigDecimal.valueOf(100);
	private String invoiceType = String.valueOf("adHoc");
	private String companyName = String.valueOf("CoName_1000");
	private String invoiceNumber = String.valueOf("WMI-10000");
	private Calendar invoiceDueDate = DateUtilities.getCalendarFromDateString("2013-09-10 16:45:13", Constants.WM_TIME_ZONE);
	private Calendar invoiceOn = DateUtilities.getCalendarFromDateString("2013-09-10 16:45:13", Constants.WM_TIME_ZONE);
	private Calendar payment_date = DateUtilities.getCalendarFromDateString("2013-09-10 16:45:13", Constants.WM_TIME_ZONE);
	private List<AccountingSummaryDetail> accountingSummaryDetails;

	@Before
	public void setUp() throws Exception {
		startDate = DateUtilities.getMidnightYesterday();
		startFiscalYear = DateUtilities.getMidnightYTD();
		when(accountingSummaryDAO.findStartFiscalYearByYear(anyInt())).thenReturn(startFiscalYear);
		when(accountingSummaryDAO.findDateOfLastSummary()).thenReturn(startDate);
		when(accountingSummaryDAO.calculateMoneyInFastFunds(any(Calendar.class), any(Calendar.class))).thenReturn(moneyInFastFunds);
		when(accountingSummaryDAO.calculateMoneyInChecks(any(Calendar.class), any(Calendar.class))).thenReturn(moneyInChecks);
		when(accountingSummaryDAO.calculateMoneyInAch(any(Calendar.class), any(Calendar.class))).thenReturn(moneyInArch);
		when(accountingSummaryDAO.calculateMoneyInWire(any(Calendar.class), any(Calendar.class))).thenReturn(moneyInWire);
		when(accountingSummaryDAO.calculateMoneyInCreditCard(any(Calendar.class), any(Calendar.class))).thenReturn(moneyInCreditCard);
		when(accountingSummaryDAO.calculateCreditMemoTotalByType(anyList(), any(Calendar.class), any(Calendar.class), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(moneyInSubscriptionCreditMemo);
		when(accountingSummaryDAO.calculateCreditMemoTotalByType(anyInt(), any(Calendar.class), any(Calendar.class), anyBoolean(), anyBoolean())).thenReturn(moneyInSubscriptionCreditMemo);
		accountingSummaryDetails = Lists.newArrayList();
		AccountingSummaryDetail accountingSummaryDetail = new AccountingSummaryDetail();
		accountingSummaryDetail.setAmount(amount);
		accountingSummaryDetail.setInvoiceType(invoiceType);
		accountingSummaryDetail.setInvoiceNumber(invoiceNumber);
		accountingSummaryDetail.setCompanyName(companyName);
		accountingSummaryDetail.setInvoiceOn(invoiceOn);
		accountingSummaryDetail.setInvoiceDueDate(invoiceDueDate);
		accountingSummaryDetail.setPaymentDate(payment_date);
		accountingSummaryDetails.add(accountingSummaryDetail);
		when(accountingSummaryDetailDAO.getMoneyOutSubscriptionSoftwareFeesDetail(any(Calendar.class), any(Calendar.class), anyBoolean())).thenReturn(accountingSummaryDetails);
		when(accountingSummaryDetailDAO.getMoneyOutSubscriptionVORFeesDetail(any(Calendar.class), any(Calendar.class))).thenReturn(accountingSummaryDetails);
	}

	@Test
	public void findSummary_success() throws Exception {
		journalEntrySummaryService.findSummary(1L);
		verify(accountingSummaryDAO, times(1)).get(1L);
	}

	@Test
	public void findAllSummaries() throws Exception {
		journalEntrySummaryService.findAllSummaries();
		verify(accountingSummaryDAO, times(1)).findAllAccountingSummaries();
	}

	@Test
	public void findOrCreateStartFiscalYearForDate() {
		assertNotNull(journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance()));
	}

	@Test
	public void calculateMoneyIn_givenNotNullArguments_callDAO() {
		journalEntrySummaryService.calculateMoneyIn(new AccountingSummary(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance());
		verify(accountingSummaryDAO, times(2)).calculateMoneyInFastFunds(any(Calendar.class), any(Calendar.class));
		verify(accountingSummaryDAO, times(2)).calculateMoneyInChecks(any(Calendar.class), any(Calendar.class));
		verify(accountingSummaryDAO, times(2)).calculateMoneyInAch(any(Calendar.class), any(Calendar.class));
		verify(accountingSummaryDAO, times(2)).calculateMoneyInWire(any(Calendar.class), any(Calendar.class));
		verify(accountingSummaryDAO, times(2)).calculateMoneyInCreditCard(any(Calendar.class), any(Calendar.class));
	}

	@Test
	public void calculateMoneyIn_givenNotNullArguments_success() {
		AccountingSummary accountingSummary = new AccountingSummary();
		journalEntrySummaryService.calculateMoneyIn(accountingSummary, Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance());
		assertEquals(accountingSummary.getMoneyInFastFunds(), moneyInFastFunds);
		assertEquals(accountingSummary.getMoneyInChecks(), moneyInChecks);
		assertEquals(accountingSummary.getMoneyInAch(), moneyInArch);
		assertEquals(accountingSummary.getMoneyInWire(), moneyInWire);
		assertEquals(accountingSummary.getMoneyInCreditCard(), moneyInCreditCard);
	}

	@Test
	public void findMoneyOutSubscriptionSWFeesDetail_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.findMoneyOutSubscriptionSWFeesDetail(anyLong());
		verify(accountingSummaryDetailDAO, never()).getMoneyOutSubscriptionSoftwareFeesDetail(any(Calendar.class), any(Calendar.class), anyBoolean());
		assertTrue(response.isEmpty());
	}

	@Test
	public void findMoneyOutSubscriptionSWFeesDetail_givenNonNullSummary_returnList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(new AccountingSummary());
		List<AccountingSummaryDetail> response = journalEntrySummaryService.findMoneyOutSubscriptionSWFeesDetail(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getMoneyOutSubscriptionSoftwareFeesDetail(any(Calendar.class), any(Calendar.class), anyBoolean());
		assertNotNull(response);
		assertFalse(response.isEmpty());
	}

	@Test
	public void findMoneyOutSubscriptionVORFeesDetail_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.findMoneyOutSubscriptionVORFeesDetail(anyLong());
		verify(accountingSummaryDetailDAO, never()).getMoneyOutSubscriptionVORFeesDetail(any(Calendar.class), any(Calendar.class));
		assertTrue(response.isEmpty());
	}

	@Test
	public void findMoneyOutSubscriptionVORFeesDetail_givenNonNullSummary_returnList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(new AccountingSummary());
		List<AccountingSummaryDetail> response = journalEntrySummaryService.findMoneyOutSubscriptionVORFeesDetail(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getMoneyOutSubscriptionVORFeesDetail(any(Calendar.class), any(Calendar.class));
		assertNotNull(response);
		assertFalse(response.isEmpty());
	}

	@Test
	public void findMoneyOutSubscriptionNVORSoftwareFeesDetail_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.findMoneyOutSubscriptionNVORSoftwareFeesDetail(anyLong());
		verify(accountingSummaryDetailDAO, never()).getMoneyOutSubscriptionSoftwareFeesDetail(any(Calendar.class), any(Calendar.class), anyBoolean());
		assertTrue(response.isEmpty());
	}

	@Test
	public void findMoneyOutSubscriptionNVORSoftwareFeesDetail_givenNonNullSummary_returnList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(new AccountingSummary());
		List<AccountingSummaryDetail> response = journalEntrySummaryService.findMoneyOutSubscriptionNVORSoftwareFeesDetail(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getMoneyOutSubscriptionSoftwareFeesDetail(any(Calendar.class), any(Calendar.class), anyBoolean());
		assertNotNull(response);
		assertFalse(response.isEmpty());
	}

	@Test
	public void findMoneyOutSubscriptionSWFeesDetail_success() {
		long id = anyLong();
		List<AccountingSummaryDetail> accountSummaryDetail = journalEntrySummaryService.findMoneyOutSubscriptionVORFeesDetail(id);
		for (AccountingSummaryDetail detail : accountSummaryDetail) {
			assertEquals(detail.getAmount(), amount);
			assertEquals(detail.getCompanyName(), companyName);
			assertEquals(detail.getInvoiceDueDate(), invoiceOn);
			assertEquals(detail.getInvoiceNumber(), invoiceNumber);
			assertEquals(detail.getPaymentDate(), payment_date);
			assertEquals(detail.getInvoiceDueDate(), invoiceDueDate);
			assertEquals(detail.getInvoiceType(), invoiceType);
		}
	}

	@Test
	public void findMoneyOutSubscriptionVORFeesDetail_success() {
		long id = anyLong();
		List<AccountingSummaryDetail> accountSummaryDetail = journalEntrySummaryService.findMoneyOutSubscriptionVORFeesDetail(id);
		for (AccountingSummaryDetail detail : accountSummaryDetail) {
			assertEquals(detail.getAmount(), amount);
			assertEquals(detail.getCompanyName(), companyName);
			assertEquals(detail.getInvoiceDueDate(), invoiceOn);
			assertEquals(detail.getInvoiceNumber(), invoiceNumber);
			assertEquals(detail.getPaymentDate(), payment_date);
			assertEquals(detail.getInvoiceDueDate(), invoiceDueDate);
			assertEquals(detail.getInvoiceType(), invoiceType);
		}
	}

	@Test
	public void findMoneyOutSubscriptionNVORSoftwareFeesDetail_success() {
		long id = anyLong();
		List<AccountingSummaryDetail> accountSummaryDetail = journalEntrySummaryService.findMoneyOutSubscriptionNVORSoftwareFeesDetail(id);
		for (AccountingSummaryDetail detail : accountSummaryDetail) {
			assertEquals(detail.getAmount(), amount);
			assertEquals(detail.getCompanyName(), companyName);
			assertEquals(detail.getInvoiceDueDate(), invoiceOn);
			assertEquals(detail.getInvoiceNumber(), invoiceNumber);
			assertEquals(detail.getPaymentDate(), payment_date);
			assertEquals(detail.getInvoiceDueDate(), invoiceDueDate);
			assertEquals(detail.getInvoiceType(), invoiceType);
		}
	}

	@Test
	public void calculateTotalMoneyOnSystem() {
		AccountingSummary accountingSummary = mock(AccountingSummary.class);
		when(accountingSummary.getRequestDate()).thenReturn(Calendar.getInstance());
		when(accountingSummary.getPreviousRequestDate()).thenReturn(DateUtilities.getMidnightYesterday());
		when(accountingSummary.calculateTotalMoneyOnSystem()).thenReturn(BigDecimal.TEN);

		journalEntrySummaryService.calculateTotalMoneyOnSystem(accountingSummary);
		verify(accountingSummaryDAO, atLeastOnce()).findBy(eq("requestDate"), any(Calendar.class));
		verify(accountingSummary, times(1)).setTotalMoneyOnSystem(any(BigDecimal.class));
		verify(accountingSummary, times(1)).setTotalMoneyOnSystemHistorical(any(BigDecimal.class));
	}

	@Test
	public void getAccItemRevSubVorSw_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubVorSw(anyLong());
		assertTrue(response.isEmpty());
	}

	@Test
	public void getAccItemRevSubVorSw_givenNonNullSummary_returnList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(new AccountingSummary());
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubVorSw(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getAccItemRevSubVorSw(any(Calendar.class), any(Calendar.class));
		assertNotNull(response);
	}

	@Test
	public void getAccItemRevSubVorSwYTD_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubVorSwYTD(anyLong());
		assertTrue(response.isEmpty());
	}

	@Test
	public void getAccItemRevSubVorSwYTD_givenNonNullSummary_returnList() {
		AccountingSummary accountingSummary = mock(AccountingSummary.class);
		when(accountingSummary.getRequestDate()).thenReturn(Calendar.getInstance());
		when(accountingSummaryDAO.get(anyLong())).thenReturn(accountingSummary);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubVorSwYTD(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getAccItemRevSubVorSw(any(Calendar.class), any(Calendar.class));
		assertNotNull(response);
	}

	@Test
	public void getAccItemRevSubVorVor_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubVorVor(anyLong());
		assertTrue(response.isEmpty());
	}

	@Test
	public void getAccItemRevSubVorVor_givenNonNullSummary_returnList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(new AccountingSummary());
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubVorVor(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getAccItemRevSubVorVor(any(Calendar.class), any(Calendar.class));
		assertNotNull(response);
	}

	@Test
	public void getAccItemRevSubVorVorYTD_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubVorVorYTD(anyLong());
		assertTrue(response.isEmpty());
	}

	@Test
	public void getAccItemRevSubVorVorYTD_givenNonNullSummary_returnList() {
		AccountingSummary accountingSummary = mock(AccountingSummary.class);
		when(accountingSummary.getRequestDate()).thenReturn(Calendar.getInstance());
		when(accountingSummaryDAO.get(anyLong())).thenReturn(accountingSummary);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubVorVorYTD(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getAccItemRevSubVorVor(any(Calendar.class), any(Calendar.class));
		assertNotNull(response);
	}

	@Test
	public void getAccItemRevSubNVor_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubNVor(anyLong());
		assertTrue(response.isEmpty());
	}

	@Test
	public void getAccItemRevSubNVor_givenNonNullSummary_returnList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(new AccountingSummary());
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubNVor(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getAccItemRevSubNVor(any(Calendar.class), any(Calendar.class));
		assertNotNull(response);
	}

	@Test
	public void getAccItemRevSubNVorYTD_givenNullSummary_returnEmptyList() {
		when(accountingSummaryDAO.get(anyLong())).thenReturn(null);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubNVorYTD(anyLong());
		assertTrue(response.isEmpty());
	}

	@Test
	public void getAccItemRevSubNVorYTD_givenNonNullSummary_returnList() {
		AccountingSummary accountingSummary = mock(AccountingSummary.class);
		when(accountingSummary.getRequestDate()).thenReturn(Calendar.getInstance());
		when(accountingSummaryDAO.get(anyLong())).thenReturn(accountingSummary);
		List<AccountingSummaryDetail> response = journalEntrySummaryService.getAccItemRevSubNVorYTD(anyLong());
		verify(accountingSummaryDetailDAO, times(1)).getAccItemRevSubNVor(any(Calendar.class), any(Calendar.class));
		assertNotNull(response);
	}


}
