package com.workmarket.service.business.accountregister;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkBundleTransaction;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisterTransactionExecutorTest {

	@Mock protected RegisterTransactionDAO registerTransactionDAO;
	@Mock protected AccountRegisterDAO accountRegisterDAO;
	@Mock protected AccountPricingService accountPricingService;
	@Mock private FeatureEvaluator featureEvaluator;
	@Mock private MetricRegistry registry;
	@InjectMocks RegisterTransactionExecutor registerTransactionExecutor = new BuyerAuthorizationImmediateWorkPayment();

	private Work work;
	private RegisterTransaction registerTransaction;
	private AccountRegisterSummaryFields summaryFields;
	private AccountRegister accountRegister;
	private Company company;

	@Before
	public void setUp() throws Exception {
		registerTransactionExecutor.init();
		final Meter mockMeter = mock(Meter.class);
		when(registry.meter((String) anyObject())).thenReturn(mockMeter);
		accountRegister = mock(AccountRegister.class);
		summaryFields = mock(AccountRegisterSummaryFields.class);
		work = mock(Work.class);
		company = mock(Company.class);
		registerTransaction = mock(RegisterTransaction.class);
		when(work.isWorkBundle()).thenReturn(false);
		when(accountRegister.getAccountRegisterSummaryFields()).thenReturn(summaryFields);
		when(summaryFields.getAvailableCash()).thenReturn(BigDecimal.TEN);
		when(summaryFields.getDepositedCash()).thenReturn(BigDecimal.TEN);
		when(summaryFields.getWithdrawableCash()).thenReturn(BigDecimal.ZERO);
		when(summaryFields.getPendingCommitments()).thenReturn(BigDecimal.ZERO);
		when(registerTransaction.getRegisterTransactionType()).thenReturn(registerTransactionExecutor.getRegisterTransactionType());
		when(registerTransaction.getAccountRegister()).thenReturn(accountRegister);
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.ONE);
		when(work.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(1L);
		when(featureEvaluator.hasFeature(anyLong(), anyObject())).thenReturn(true);
	}

	@Test
	public void createBeanName_withEmptyString_returnsEmpty() {
		Assert.assertEquals(registerTransactionExecutor.createBeanName(false, StringUtils.EMPTY), StringUtils.EMPTY);
	}

	@Test
	public void createBeanName_withEmptyStringAndPending_returnsEmpty() {
		Assert.assertEquals(registerTransactionExecutor.createBeanName(true, StringUtils.EMPTY), StringUtils.EMPTY);
	}

	@Test
	public void createBeanName_success() {
		Assert.assertEquals(registerTransactionExecutor.createBeanName(false, "SOME"), "SOME");
	}

	@Test
	public void createBeanName_withPendingTrue_success() {
		Assert.assertEquals(registerTransactionExecutor.createBeanName(true, "SOME"), "pendingSOME");
	}

	@Test
	public void createBeanName_withWorkBundle_success() {
		when(work.isWorkBundle()).thenReturn(true);
		Assert.assertEquals(registerTransactionExecutor.createBeanName(false, "SOME", work), "SOME");
	}

	@Test
	public void createBeanName_withPendingTrueAndWorkBundle_success() {
		when(work.isWorkBundle()).thenReturn(true);
		Assert.assertEquals(registerTransactionExecutor.createBeanName(true, "SOME", work), "pendingBundleSOME");
	}

	@Test
	public void buildWorkResourceTransaction_withWorkBundle_success() {
		when(work.isWorkBundle()).thenReturn(true);
		WorkBundleTransaction resourceTransaction = registerTransactionExecutor.buildWorkResourceTransaction(work, BigDecimal.TEN);
		assertNotNull(resourceTransaction);
		assertTrue(NumberUtilities.isPositive(resourceTransaction.getRemainingAuthorizedAmount()));
	}

	@Test(expected = ClassCastException.class)
	public void buildWorkResourceTransaction_withWork_throwsCastException() {
		WorkBundleTransaction resourceTransaction = registerTransactionExecutor.buildWorkResourceTransaction(work, BigDecimal.TEN);
		assertNotNull(resourceTransaction);
	}

	@Test
	public void buildWorkResourceTransaction_withWork_success() {
		WorkResourceTransaction resourceTransaction = registerTransactionExecutor.buildWorkResourceTransaction(work, BigDecimal.TEN);
		assertNotNull(resourceTransaction);
	}

	@Test
	public void onPostExecution_withDefaultImplementation_doesNothing() {
		assertEquals(registerTransactionExecutor.onPostExecution(registerTransaction), registerTransaction);
	}

	@Test
	public void updateAssignmentThroughputSummaries_withDefaultImplementation_returnFalse() {
		assertFalse(registerTransactionExecutor.updateAssignmentThroughputSummaries(new WorkResourceTransaction()));
	}

	@Test
	public void getRegisterTransactionType_neverReturnsNull() {
		assertTrue(isNotBlank(registerTransactionExecutor.getRegisterTransactionType().getCode()));
	}

	@Test
	public void populateTransaction_success() {
		RegisterTransaction registerTransaction = new RegisterTransaction();
		registerTransactionExecutor.populateTransaction(new AccountRegister(), registerTransaction, BigDecimal.TEN, new RegisterTransactionType(RegisterTransactionType.ACH_VERIFY), true);
		assertNotNull(registerTransaction.getAccountRegister());
		assertNotNull(registerTransaction.getAmount());
		assertNotNull(registerTransaction.getRegisterTransactionType());
		assertTrue(registerTransaction.getPendingFlag());
	}

	@Test(expected = IllegalArgumentException.class)
	public void populateTransaction_withNullTx_fails() {
		registerTransactionExecutor.populateTransaction(new AccountRegister(), null, BigDecimal.TEN, new RegisterTransactionType(RegisterTransactionType.ACH_VERIFY), true);
	}

	@Test
	public void reverseSummaries_withNullTx_wontSaveTransaction() {
		registerTransactionExecutor.reverseSummaries(null);
		verify(registerTransactionDAO, never()).saveOrUpdate(any(RegisterTransaction.class));
	}


	@Test
	public void reverseSummaries_success() {
		registerTransactionExecutor.reverseSummaries(registerTransaction);
		verify(registerTransaction, times(1)).setAccountRegisterSummaryFields(any(AccountRegisterSummaryFields.class));
		verify(registerTransactionDAO, times(1)).saveOrUpdate(eq(registerTransaction));
	}

	@Test
	public void updateDepositedAndWithdrawableCash_withEnoughDepositedCash_success() {
		when(summaryFields.getDepositedCash()).thenReturn(BigDecimal.TEN);
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.valueOf(-1));
		registerTransactionExecutor.updateDepositedAndWithdrawableCash(summaryFields, registerTransaction);

		verify(summaryFields, times(1)).setDepositedCash(eq(BigDecimal.valueOf(9)));
	}

	@Test
	public void updateDepositedAndWithdrawableCash_withoutDepositedCashAndWithWithdrawable_success() {
		when(summaryFields.getDepositedCash()).thenReturn(BigDecimal.ZERO);
		when(summaryFields.getWithdrawableCash()).thenReturn(BigDecimal.TEN);

		when(registerTransaction.getAmount()).thenReturn(BigDecimal.valueOf(-1));
		registerTransactionExecutor.updateDepositedAndWithdrawableCash(summaryFields, registerTransaction);

		verify(summaryFields, times(1)).setWithdrawableCash(eq(BigDecimal.valueOf(9)));
	}

	@Test(expected = InsufficientFundsException.class)
	public void updateDepositedAndWithdrawableCash_withoutDepositedCashAndWithoutWithdrawable_fails() {
		when(summaryFields.getDepositedCash()).thenReturn(BigDecimal.ZERO);
		when(summaryFields.getWithdrawableCash()).thenReturn(BigDecimal.ZERO);

		when(registerTransaction.getAmount()).thenReturn(BigDecimal.valueOf(-1));
		registerTransactionExecutor.updateDepositedAndWithdrawableCash(summaryFields, registerTransaction);
	}

	@Test
	public void updateActualCashAndAvailableCash_withPositiveValue_success() {
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.ONE);
		when(summaryFields.getActualCash()).thenReturn(BigDecimal.ZERO);
		when(summaryFields.getAvailableCash()).thenReturn(BigDecimal.ZERO);

		registerTransactionExecutor.updateActualCashAndAvailableCash(summaryFields, registerTransaction);
		verify(summaryFields, times(1)).setActualCash(eq(BigDecimal.ONE));
		verify(summaryFields, times(1)).setAvailableCash(eq(BigDecimal.ONE));
	}

	@Test
	public void updateActualCashAndAvailableCash_withNegativeValue_success() {
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.valueOf(-10));
		when(summaryFields.getActualCash()).thenReturn(BigDecimal.ZERO);
		when(summaryFields.getAvailableCash()).thenReturn(BigDecimal.ZERO);

		registerTransactionExecutor.updateActualCashAndAvailableCash(summaryFields, registerTransaction);
		verify(summaryFields, times(1)).setActualCash(eq(BigDecimal.valueOf(-10)));
		verify(summaryFields, times(1)).setAvailableCash(eq(BigDecimal.valueOf(-10)));
	}

	@Test
	public void addGeneralCash_updatesGeneralCash() {
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.ONE);
		when(summaryFields.getGeneralCash()).thenReturn(BigDecimal.ZERO);

		registerTransactionExecutor.addGeneralCash(summaryFields, registerTransaction);
		verify(summaryFields, times(1)).setGeneralCash(eq(BigDecimal.ONE));
	}

	@Test
	public void subtractGeneralCash_updatesGeneralCash() {
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.ONE);
		when(summaryFields.getGeneralCash()).thenReturn(BigDecimal.TEN);

		registerTransactionExecutor.subtractGeneralCash(summaryFields, registerTransaction);
		verify(summaryFields, times(1)).setGeneralCash(eq(BigDecimal.valueOf(9)));
	}

	@Test
	public void addProjectCash_updatesProjectCash() {
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.ONE);
		when(summaryFields.getProjectCash()).thenReturn(BigDecimal.ZERO);

		registerTransactionExecutor.addProjectCash(summaryFields, registerTransaction);
		verify(summaryFields, times(1)).setProjectCash(eq(BigDecimal.ONE));
	}

	@Test
	public void subtractProjectCash_updatesProjectCash() {
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.ONE);
		when(summaryFields.getProjectCash()).thenReturn(BigDecimal.TEN);

		registerTransactionExecutor.subtractProjectCash(summaryFields, registerTransaction);
		verify(summaryFields, times(1)).setProjectCash(eq(BigDecimal.valueOf(9)));
	}

	@Test
	public void subtractProjectCash_withNegativeAmount_updatesProjectCash() {
		when(registerTransaction.getAmount()).thenReturn(BigDecimal.valueOf(-1));
		when(summaryFields.getProjectCash()).thenReturn(BigDecimal.TEN);

		registerTransactionExecutor.subtractProjectCash(summaryFields, registerTransaction);
		verify(summaryFields, times(1)).setProjectCash(eq(BigDecimal.valueOf(9)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void execute_withNullAmount_fails() {
		registerTransactionExecutor.execute(work, null, accountRegister, null);
	}

	@Test
	public void execute_withNullWorkResourceAndDefaultFlags_success() {
		WorkResourceTransaction workResourceTransaction = registerTransactionExecutor.execute(work, null, accountRegister, BigDecimal.ONE);
		assertNotNull(workResourceTransaction);
		assertNotNull(workResourceTransaction.getRegisterTransactionType());
		assertNotNull(workResourceTransaction.getTransactionDate());
		assertNotNull(workResourceTransaction.getEffectiveDate());
		assertNotNull(workResourceTransaction.getAccountPricingServiceTypeEntity());
		assertNotNull(workResourceTransaction.getAccountRegisterSummaryFields());
		assertNull(workResourceTransaction.getWorkResource());
		assertNotNull(workResourceTransaction.getWork());

		assertTrue(workResourceTransaction.isPending());
		assertFalse(workResourceTransaction.isBatchPayment());
		assertFalse(workResourceTransaction.isBundlePayment());
		assertEquals(workResourceTransaction.getAmount(), BigDecimal.ONE);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getDepositedCash(), BigDecimal.TEN);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getAvailableCash(), BigDecimal.TEN);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getWithdrawableCash(), BigDecimal.ZERO);
	}

	@Test
	public void execute_withNullWorkResourceAndBundlePayment_success() {
		WorkResourceTransaction workResourceTransaction = registerTransactionExecutor.execute(work, null, accountRegister, BigDecimal.ONE, true, true, true);
		assertNotNull(workResourceTransaction);
		verify(summaryFields, atLeast(1)).setAvailableCash(any(BigDecimal.class));
		assertNotNull(workResourceTransaction.getRegisterTransactionType());
		assertNotNull(workResourceTransaction.getTransactionDate());
		assertNotNull(workResourceTransaction.getEffectiveDate());
		assertNotNull(workResourceTransaction.getAccountPricingServiceTypeEntity());
		assertNotNull(workResourceTransaction.getAccountRegisterSummaryFields());
		assertNull(workResourceTransaction.getWorkResource());
		assertNotNull(workResourceTransaction.getWork());

		assertTrue(workResourceTransaction.isPending());
		assertTrue(workResourceTransaction.isBatchPayment());
		assertTrue(workResourceTransaction.isBundlePayment());
		assertEquals(workResourceTransaction.getAmount(), BigDecimal.ONE);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getDepositedCash(), BigDecimal.TEN);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getAvailableCash(), BigDecimal.TEN);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getWithdrawableCash(), BigDecimal.ZERO);
	}

	@Test
	public void execute_withNullWorkResourceAndBundlePayment_wontUpdateSummaries() {
		WorkResourceTransaction workResourceTransaction = registerTransactionExecutor.execute(work, null, accountRegister, BigDecimal.ONE, false, true, true);
		assertNotNull(workResourceTransaction);

		verify(summaryFields, never()).setAvailableCash(any(BigDecimal.class));
		assertNotNull(workResourceTransaction.getRegisterTransactionType());
		assertNotNull(workResourceTransaction.getTransactionDate());
		assertNotNull(workResourceTransaction.getEffectiveDate());
		assertNotNull(workResourceTransaction.getAccountPricingServiceTypeEntity());
		assertNotNull(workResourceTransaction.getAccountRegisterSummaryFields());
		assertNull(workResourceTransaction.getWorkResource());
		assertNotNull(workResourceTransaction.getWork());

		assertTrue(workResourceTransaction.isPending());
		assertTrue(workResourceTransaction.isBatchPayment());
		assertTrue(workResourceTransaction.isBundlePayment());
		assertEquals(workResourceTransaction.getAmount(), BigDecimal.ONE);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getDepositedCash(), BigDecimal.TEN);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getAvailableCash(), BigDecimal.TEN);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getWithdrawableCash(), BigDecimal.ZERO);
	}

	@Test
	public void execute_withWorkBundleNullWorkResourceAndDefaultFlags_success() {
		when(work.isWorkBundle()).thenReturn(true);
		WorkBundleTransaction workResourceTransaction = (WorkBundleTransaction)registerTransactionExecutor.execute(work, null, accountRegister, BigDecimal.ONE);
		assertNotNull(workResourceTransaction);
		assertNotNull(workResourceTransaction.getRegisterTransactionType());
		assertNotNull(workResourceTransaction.getTransactionDate());
		assertNotNull(workResourceTransaction.getEffectiveDate());
		assertNotNull(workResourceTransaction.getAccountPricingServiceTypeEntity());
		assertNotNull(workResourceTransaction.getAccountRegisterSummaryFields());
		assertNull(workResourceTransaction.getWorkResource());
		assertNotNull(workResourceTransaction.getWork());
		assertNotNull(workResourceTransaction.getRemainingAuthorizedAmount());

		assertTrue(workResourceTransaction.isPending());
		assertFalse(workResourceTransaction.isBatchPayment());
		assertFalse(workResourceTransaction.isBundlePayment());
		assertEquals(workResourceTransaction.getAmount(), BigDecimal.ONE);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getDepositedCash(), BigDecimal.TEN);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getAvailableCash(), BigDecimal.TEN);
		assertEquals(workResourceTransaction.getAccountRegisterSummaryFields().getWithdrawableCash(), BigDecimal.ZERO);
	}

	@Test
	public void execute_saveRegisterTransaction() {
		RegisterTransaction transaction = registerTransactionExecutor.execute(accountRegister, BigDecimal.ONE, registerTransaction);
		assertNotNull(transaction);
		verify(registerTransaction, times(1)).setAccountRegisterSummaryFields(any(AccountRegisterSummaryFields.class));
		verify(registerTransactionDAO, times(1)).saveOrUpdate(any(RegisterTransaction.class));
	}
}

