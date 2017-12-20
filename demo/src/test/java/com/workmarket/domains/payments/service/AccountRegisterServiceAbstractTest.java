package com.workmarket.domains.payments.service;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionExecutableFactory;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.option.WorkOptionsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: iloveopt
 * Date: 4/14/14
 */

@RunWith(MockitoJUnitRunner.class)
public class AccountRegisterServiceAbstractTest {
	@Mock RegisterTransactionDAO registerTransactionDAO;
	@Mock RegisterTransactionExecutableFactory registerTransactionExecutableFactory;
	@Mock RegisterTransactionExecutor registerTransactionExecutor;
	@Mock PricingService pricingService;
	@Mock WorkService workService;
	@Mock MboProfileDAO mboProfileDAO;
	@Mock WorkOptionsService workOptionsService;
	@Mock AccountPricingService accountPricingService;
	@InjectMocks AccountRegisterServiceAbstract accountRegisterServiceAbstract = new AccountRegisterServicePrefundImpl();

	@Mock WorkResource workResource;
	@Mock Work work;
	@Mock AccountRegister workerAccountRegister;
	@Mock AccountRegister buyerAccountRegister;
	@Mock BigDecimal amount;
	@Mock User worker;
	@Mock Company buyerCompany;
	@Mock Company workerCompany;
	@Mock AccountRegister accountRegister;

	@Before
	public void setup() {
		workResource = mock(WorkResource.class);
		work = mock(Work.class);
		workerAccountRegister = mock(AccountRegister.class);
		buyerAccountRegister = mock(AccountRegister.class);
		registerTransactionExecutor = mock(RegisterTransactionExecutor.class);

		when(workResource.getWork()).thenReturn(work);
		when(workResource.getUser()).thenReturn(worker);
		when(work.getCompany()).thenReturn(buyerCompany);

		when(worker.getCompany()).thenReturn(workerCompany);

		when(registerTransactionExecutableFactory.newInstance(anyString())).thenReturn(registerTransactionExecutor);

		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAndExecuteFastFundsFeeWorkResourceTransactionTransaction_nullWorkResource_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(null, workerAccountRegister, amount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAndExecuteFastFundsFeeWorkResourceTransactionTransaction_nullWorkerAccountRegisterWorkResource_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(workResource, null, amount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAndExecuteFastFundsFeeWorkResourceTransactionTransaction_nullAmount_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(workResource, workerAccountRegister, null);
	}

	@Test
	public void createAndExecuteFastFundsFeeWorkResourceTransactionTransaction_fastFundsFeeExecutorInstanceUsed() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(workResource, workerAccountRegister, amount);

		verify(registerTransactionExecutableFactory).newInstance(RegisterTransactionType.FAST_FUNDS_FEE);
	}

	@Test
	public void createAndExecuteFastFundsFeeWorkResourceTransactionTransaction_amountNegated() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(workResource, workerAccountRegister, amount);

		verify(amount).negate();
	}

	@Test(expected = IllegalArgumentException.class)
		 public void createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction_nullWorkResource_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(null, workerAccountRegister, amount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction_nullWorkerAccountRegisterWorkResource_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(workResource, null, amount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction_nullAmount_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(workResource, workerAccountRegister, null);
	}

	@Test
	public void createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction_fastFundsFeeExecutorInstanceUsed() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(workResource, workerAccountRegister, amount);

		verify(registerTransactionExecutableFactory).newInstance(RegisterTransactionType.FAST_FUNDS_PAYMENT);
	}

	@Test
	public void createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction_amountNotNegated() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(workResource, workerAccountRegister, amount);

		verify(amount, never()).negate();
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAndExecuteFastFundsDebitWorkResourceTransactionTransaction_nullWorkResource_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(null, workerAccountRegister, amount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAndExecuteFastFundsDebitWorkResourceTransactionTransaction_nullWorkerAccountRegisterWorkResource_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(workResource, null, amount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAndExecuteFastFundsDebitWorkResourceTransactionTransaction_nullAmount_exceptionThrown() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(workResource, workerAccountRegister, null);
	}

	@Test
	public void createAndExecuteFastFundsDebitWorkResourceTransactionTransaction_fastFundsFeeExecutorInstanceUsed() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(workResource, workerAccountRegister, amount);

		verify(registerTransactionExecutableFactory).newInstance(RegisterTransactionType.FAST_FUNDS_DEBIT);
	}

	@Test
	public void createAndExecuteFastFundsDebitWorkResourceTransactionTransaction_amountNegated() {
		accountRegisterServiceAbstract.createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(workResource, workerAccountRegister, amount);

		verify(amount).negate();
	}

	@Test
	public void findFundingTransactionsByDate_notNullDateFilter_CallDAOMethod () {
		DateFilter dateFilter = mock(DateFilter.class);
		when(dateFilter.getFromDate()).thenReturn(Calendar.getInstance());
		when(dateFilter.getToDate()).thenReturn(Calendar.getInstance());
		accountRegisterServiceAbstract.findFundingTransactionsByDate(dateFilter);
		verify(registerTransactionDAO).findFundingTransactionsByDate(dateFilter);
	}

	@Test
	public void findFundingTransactionsByDate_nullDateFilter_returnEmptyList () {
		DateFilter dateFilter = null;
		assertTrue(accountRegisterServiceAbstract.findFundingTransactionsByDate(dateFilter).size() == 0);
		verify(registerTransactionDAO, never()).findFundingTransactionsByDate(dateFilter);

	}

	@Test
	public void findFundingTransactionsByDate_nullFromDate_returnEmptyList () {
		DateFilter dateFilter = new DateFilter();
		dateFilter.setFromDate(null);
		assertTrue(accountRegisterServiceAbstract.findFundingTransactionsByDate(dateFilter).size() == 0);
		verify(registerTransactionDAO, never()).findFundingTransactionsByDate(dateFilter);

	}

	@Test
	public void findFundingTransactionsByDate_nullToDate_returnEmptyList () {
		DateFilter dateFilter = new DateFilter();
		dateFilter.setToDate(null);
		assertTrue(accountRegisterServiceAbstract.findFundingTransactionsByDate(dateFilter).size() == 0);
		verify(registerTransactionDAO, never()).findFundingTransactionsByDate(dateFilter);

	}

	@Test
	public void completeOfflinePayment() {

		when(pricingService.calculateTotalResourceCost(any(Work.class), eq(workResource))).thenReturn(BigDecimal.TEN);
		when(pricingService.calculateBuyerNetMoneyFee(any(AbstractWork.class), any(BigDecimal.class))).thenReturn(BigDecimal.TEN);

		accountRegisterServiceAbstract.completeOfflinePayment(workResource);

		verify(registerTransactionExecutableFactory).newInstance(RegisterTransactionType.BUYER_OFFLINE_WORK_PAYMENT);
		verify(registerTransactionExecutableFactory).newInstance(RegisterTransactionType.RESOURCE_OFFLINE_WORK_PAYMENT);
	}
}
