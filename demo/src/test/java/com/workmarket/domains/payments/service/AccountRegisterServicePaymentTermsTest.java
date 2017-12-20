package com.workmarket.domains.payments.service;

import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.pricing.AccountPricingServiceTypeEntity;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.domains.payments.dao.AccountRegisterSummaryFieldsDAO;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionActivityDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.dao.WeeklyRevenueReportDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.project.ProjectBudgetService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.InvoicePaymentHelper;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.accountregister.BuyerAuthorizationPaymentTermsWorkBundlePayment;
import com.workmarket.service.business.accountregister.BuyerAuthorizationPaymentTermsWorkPayment;
import com.workmarket.service.business.accountregister.BuyerWorkPayment;
import com.workmarket.service.business.accountregister.FeeCalculator;
import com.workmarket.service.business.accountregister.FinishedWorkFeeLane3;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.accountregister.RemoveFundsFromGeneralCash;
import com.workmarket.service.business.accountregister.ResourceAuthorizationPaymentTermsWorkPayment;
import com.workmarket.service.business.accountregister.ResourceWorkPayment;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionExecutableFactory;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionFactory;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.account.AccountRegisterNoActiveWorkResourceException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InsufficientSpendLimitException;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.service.infra.business.PaymentService;
import com.workmarket.service.option.OptionsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountRegisterServicePaymentTermsTest {

	@Mock LaneService laneService;
	@Mock PaymentService paymentService;
	@Mock UserNotificationService userNotificationService;
	@Mock WorkService workService;
	@Mock PricingService pricingService;
	@Mock TaxService taxService;
	@Mock WorkResourceService workResourceService;
	@Mock BankAccountDAO bankAccountDAO;
	@Mock UserDAO userDAO;
	@Mock RegisterTransactionDAO registerTransactionDAO;
	@Mock AccountRegisterDAO accountRegisterDAO;
	@Mock AccountRegisterSummaryFieldsDAO accountRegisterSummaryFieldsDAO;
	@Mock RegisterTransactionActivityDAO registerTransactionActivityDAO;
	@Mock WeeklyRevenueReportDAO weeklyRevenueReportDAO;
	@Mock RegisterTransactionFactory registerTransactionFactory;
	@Mock FeeCalculator feeCalculator;
	@Mock ProjectService projectService;
	@Mock CompanyService companyService;
	@Mock InvoicePaymentHelper invoicePaymentHelper;
	@Mock ProjectBudgetService projectBudgetService;
	@Mock RegisterTransactionExecutableFactory registerTransactionExecutableFactory;
	@Mock OptionsService<AbstractWork> workOptionsService;
	@Mock MboProfileDAO mboProfileDAO;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock WorkBundleService workBundleService;
	@Mock AccountPricingService accountPricingService;
	@Mock BillingService billingService;

	@InjectMocks AccountRegisterServicePaymentTermsImpl accountRegisterService = new AccountRegisterServicePaymentTermsImpl();

	private WorkResource workResource;
	private Work work;
	private WorkBundle workBundle;
	private WorkCostDTO workCostDTO;
	private User userResource, userBuyer;
	private Project project;
	private AccountRegisterSummaryFields accountRegisterSummaryFields;
	private Company company;
	private RegisterTransactionExecutor registerTransactionsAbstract;
	private ResourceAuthorizationPaymentTermsWorkPayment resourceAuthorizationPaymentTermsWorkPayment;
	private BuyerAuthorizationPaymentTermsWorkPayment buyerAuthorizationPaymentTermsWorkPayment;
	private RemoveFundsFromGeneralCash removeFundsFromGeneral;
	private FinishedWorkFeeLane3 finishedWorkFeeLane3;
	private BuyerWorkPayment buyerWorkPayment;
	private ResourceWorkPayment resourceWorkPayment;
	private BuyerAuthorizationPaymentTermsWorkBundlePayment buyerAuthorizationPaymentTermsWorkBundlePayment;
	private AccountRegister accountRegister;
	private Invoice invoice;

	private static final Calendar FAST_FUNDED_ON_DATE = Calendar.getInstance();
	private static final BigDecimal
			ACCOUNTS_PAYABLE_BALANCE = BigDecimal.ZERO,
			AP_LIMIT = BigDecimal.valueOf(1000);

	@Before
	public void setUp() throws Exception {
		accountRegisterSummaryFields = new AccountRegisterSummaryFields();
		accountRegisterSummaryFields.setActualCash(BigDecimal.valueOf(300));
		accountRegisterSummaryFields.setAvailableCash(BigDecimal.valueOf(300));
		accountRegisterSummaryFields.setAccountsPayableBalance(ACCOUNTS_PAYABLE_BALANCE);

		workResource = mock(WorkResource.class);
		workCostDTO = mock(WorkCostDTO.class);
		work = mock(Work.class);
		workBundle = mock(WorkBundle.class);
		userResource = mock(User.class);
		userBuyer = mock(User.class);
		project = mock(Project.class);
		accountRegister = mock(AccountRegister.class);
		invoice = mock(Invoice.class);
		company = mock(Company.class);

		registerTransactionsAbstract = mock(RegisterTransactionExecutor.class);
		resourceAuthorizationPaymentTermsWorkPayment = mock(ResourceAuthorizationPaymentTermsWorkPayment.class);
		buyerAuthorizationPaymentTermsWorkPayment = mock(BuyerAuthorizationPaymentTermsWorkPayment.class);
		removeFundsFromGeneral = mock(RemoveFundsFromGeneralCash.class);
		finishedWorkFeeLane3 = mock(FinishedWorkFeeLane3.class);
		buyerWorkPayment = mock(BuyerWorkPayment.class);
		resourceWorkPayment = mock(ResourceWorkPayment.class);
		buyerAuthorizationPaymentTermsWorkBundlePayment = mock(BuyerAuthorizationPaymentTermsWorkBundlePayment.class);

		// TODO: Enable for FF testing
		//when(invoice.getFastFundedOn()).thenReturn(FAST_FUNDED_ON_DATE);

		when(company.getId()).thenReturn(1089L);

		when(accountRegister.getApLimit()).thenReturn(AP_LIMIT);
		when(accountRegister.getCompany()).thenReturn(company);
		when(accountRegisterService.getAPLimit(company.getId())).thenReturn(AP_LIMIT);
		when(accountRegisterService.getAccountsPayableBalance(company.getId())).thenReturn(ACCOUNTS_PAYABLE_BALANCE);

		when(workResource.getWork()).thenReturn(work);
		when(workResource.getUser()).thenReturn(userResource);

		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(workResourceService.findWorkResourceById(anyLong())).thenReturn(workResource);

		when(userResource.getCompany()).thenReturn(mock(Company.class));
		when(userResource.getId()).thenReturn(3424L);
		when(userBuyer.getCompany()).thenReturn(company);
		when(userBuyer.getSpendLimit()).thenReturn(BigDecimal.valueOf(1000L));

		when(work.getId()).thenReturn(1L);
		when(work.getCompany()).thenReturn(company);
		when(work.getFulfillmentStrategy()).thenReturn(mock(FulfillmentStrategy.class));
		when(work.getBuyer()).thenReturn(userBuyer);

		when(workBundle.getId()).thenReturn(1L);
		when(workBundle.getCompany()).thenReturn(company);
		when(workBundle.getFulfillmentStrategy()).thenReturn(mock(FulfillmentStrategy.class));
		when(workBundle.getBuyer()).thenReturn(userBuyer);

		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);

		when(workCostDTO.getTotalBuyerCost()).thenReturn(BigDecimal.valueOf(110));

		when(project.getRemainingBudget()).thenReturn(BigDecimal.valueOf(50));
		when(project.getBudgetEnabledFlag()).thenReturn(true);

		when(billingService.findInvoiceByWorkId(anyLong())).thenReturn(invoice);

		when(pricingService.calculateMaximumResourceCost(any(Work.class))).thenReturn(BigDecimal.valueOf(100));
		when(pricingService.calculateTotalResourceCost(any(Work.class), any(WorkResource.class))).thenReturn(BigDecimal.valueOf(100));
		when(pricingService.calculateBuyerNetMoneyFee(any(Work.class), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(10));
		when(pricingService.findDefaultRegisterForCompany(anyLong())).thenReturn(accountRegister);
		when(pricingService.findDefaultRegisterForCompany(anyLong(), anyBoolean())).thenReturn(accountRegister);

		doNothing().when(projectBudgetService).decreaseRemainingBudget(any(Project.class), any(BigDecimal.class));

		when(registerTransactionsAbstract.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class))).thenReturn(mock(WorkResourceTransaction.class));
		when(resourceAuthorizationPaymentTermsWorkPayment.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class))).thenReturn(mock(WorkResourceTransaction.class));
		when(buyerAuthorizationPaymentTermsWorkPayment.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class), eq(true))).thenReturn(mock(WorkResourceTransaction.class));
		when(buyerAuthorizationPaymentTermsWorkPayment.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class))).thenReturn(mock(WorkResourceTransaction.class));
		when(removeFundsFromGeneral.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class))).thenReturn(mock(WorkResourceTransaction.class));
		when(buyerAuthorizationPaymentTermsWorkBundlePayment.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class), eq(true))).thenReturn(mock(WorkResourceTransaction.class));

		when(accountRegister.getAccountRegisterSummaryFields()).thenReturn(accountRegisterSummaryFields);

		when(userDAO.get(anyLong())).thenReturn(userBuyer);

		when(registerTransactionExecutableFactory.newInstance("pendingPytrmspyct")).thenReturn(resourceAuthorizationPaymentTermsWorkPayment);
		when(registerTransactionExecutableFactory.newInstance("pendingPytrmscmmt")).thenReturn(buyerAuthorizationPaymentTermsWorkPayment);
		when(registerTransactionExecutableFactory.newInstance("removegenr")).thenReturn(removeFundsFromGeneral);
		when(registerTransactionExecutableFactory.newInstance("lane3work")).thenReturn(finishedWorkFeeLane3);
		when(registerTransactionExecutableFactory.newInstance(LaneType.LANE_3)).thenReturn(finishedWorkFeeLane3);
		when(registerTransactionExecutableFactory.newInstance("payment")).thenReturn(buyerWorkPayment);
		when(registerTransactionExecutableFactory.newInstance("wrkpayment")).thenReturn(resourceWorkPayment);
		when(registerTransactionExecutableFactory.newInstance(RegisterTransactionType.BUYER_AUTHORIZATION_WORK_BUNDLE_PAYMENT_TERMS_VIRTUAL_CODE)).thenReturn(buyerAuthorizationPaymentTermsWorkBundlePayment);

		FulfillmentStrategy fulfillmentStrategy = new FulfillmentStrategy();
		fulfillmentStrategy.setAmountEarned(BigDecimal.ONE);
		when(work.getFulfillmentStrategy()).thenReturn(fulfillmentStrategy);

		when(workService.findWork(anyLong(), anyBoolean())).thenReturn(work);
		MboProfile mboProfile = mock(MboProfile.class);
		when(mboProfileDAO.findMboProfile(anyLong())).thenReturn(mboProfile);
		when(featureEvaluator.hasFeature(anyLong(), anyObject())).thenReturn(true);
		when(workBundleService.getBundleBudget(any(User.class), any(WorkBundle.class))).thenReturn(BigDecimal.TEN);

	}

	@Test(expected = IllegalArgumentException.class)
	public void acceptWork_nullResource_fails() throws Exception {
		accountRegisterService.acceptWork(null);
	}

	@Test
	public void acceptWork_withLane1_returnsNull() throws Exception {
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(LaneType.LANE_1);
		assertNull(accountRegisterService.acceptWork(workResource));
	}

	@Test
	public void acceptWork_withLane2_success() throws Exception {
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(LaneType.LANE_2);
		assertNotNull(accountRegisterService.acceptWork(workResource));
	}

	@Test
	public void commitToWork_withNoResource_success() throws Exception {
		when(workResourceService.findAllResourcesForWork(anyLong())).thenReturn(Collections.EMPTY_LIST);
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(null);
		when(registerTransactionDAO.findWorkResourcePendingCommitmentTransaction(anyLong())).thenReturn(null);
		assertNotNull(accountRegisterService.commitToWork(work));
	}

	@Test
	public void commitToWork_withLane2Type_success() throws Exception {
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(LaneType.LANE_2);
		when(workResourceService.findAllResourcesForWork(anyLong())).thenReturn(Arrays.asList(workResource));
		assertNotNull(accountRegisterService.commitToWork(work));
	}

	@Test(expected = IllegalArgumentException.class)
	public void commitToWork_withNullWork_fails() throws Exception {
		accountRegisterService.commitToWork(null);
	}

	@Test
	public void commitToWork_success() throws Exception {
		assertNotNull(accountRegisterService.commitToWork(work, workResource));
	}

	@Test(expected = InsufficientSpendLimitException.class)
	public void commitToWork_withNoSpendLimit_fails() throws Exception {
		when(userBuyer.getSpendLimit()).thenReturn(BigDecimal.valueOf(50L));
		assertNotNull(accountRegisterService.commitToWork(work, workResource));
	}

	@Test(expected = InsufficientFundsException.class)
	public void commitToWork_withInsufficientFunds_fails() throws Exception {
		when(accountRegisterService.getAccountsPayableBalance(company.getId())).thenReturn(BigDecimal.valueOf(1500));
		assertNotNull(accountRegisterService.commitToWork(work, workResource));
	}

	@Test(expected = InsufficientBudgetException.class)
	public void commitToWorkAResourceLane234_withInsufficientBudget_fails() throws Exception {
		when(work.hasProject()).thenReturn(true);
		when(work.getProject()).thenReturn(project);
		assertNotNull(accountRegisterService.commitToWork(work, workResource));
		when(project.getRemainingBudget()).thenReturn(BigDecimal.valueOf(50));
		when(project.isReservedFundsEnabled()).thenReturn(false);
		when(project.getBudgetEnabledFlag()).thenReturn(true);
		assertNotNull(accountRegisterService.commitToWork(work, workResource));
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateCostOnCompleteWork_withNullArgs_fails() throws Exception {
		 accountRegisterService.calculateCostOnCompleteWork(null, null);
	}

	@Test
	public void calculateCostOnCompleteWork_success() throws Exception {
		WorkCostDTO costDTO = accountRegisterService.calculateCostOnCompleteWork(work, workResource);
		assertEquals(costDTO.getTotalBuyerCost().doubleValue(), 110.00, 0);
	}

	@Test
	public void calcSufficientBuyerFundsByCompany_success() throws Exception {
		assertNotNull(accountRegisterService.calcSufficientBuyerFundsByCompany(1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void calcSufficientBuyerFundsByCompany_withNullArgs_fails() throws Exception {
		assertNotNull(accountRegisterService.calcSufficientBuyerFundsByCompany(null));
	}

	@Test
	public void createFulfillmentStrategyFromWorkCostDTO_success() throws Exception {
		WorkCostDTO dto = new WorkCostDTO();
		dto.setBuyerFee(BigDecimal.valueOf(10));
		dto.setTotalResourceCost(BigDecimal.valueOf(100));
		dto.setTotalBuyerCost(BigDecimal.valueOf(110));

		FulfillmentStrategy fulfillmentStrategy = accountRegisterService.createFulfillmentStrategyFromWorkCostDTO(dto);

		assertNotNull(fulfillmentStrategy);
		assertEquals(fulfillmentStrategy.getBuyerTotalCost().doubleValue(), 110.0, 0);
		assertEquals(fulfillmentStrategy.getWorkPrice().doubleValue(), 100.0, 0);
		assertEquals(fulfillmentStrategy.getAmountEarned().doubleValue(), 100.0, 0);
		assertEquals(fulfillmentStrategy.getBuyerFee().doubleValue(), 10.0, 0);
		assertEquals(fulfillmentStrategy.getWorkPricePriorComplete().doubleValue(), 100.0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void payForBackgroundCheckUsingBalance_withNullArgs_fails() throws Exception {
		accountRegisterService.payForBackgroundCheckUsingBalance(null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void payForBackgroundCheckUsingBalance_withNullUserId_fails() throws Exception {
		accountRegisterService.payForBackgroundCheckUsingBalance(null, new BackgroundCheck(), "USA");
	}

	@Test(expected = IllegalArgumentException.class)
	public void payForBackgroundCheckUsingBalance_withNullBackground_fails() throws Exception {
		accountRegisterService.payForBackgroundCheckUsingBalance(1L, null, "USA");
	}

	@Test(expected = IllegalArgumentException.class)
	public void payForBackgroundCheckUsingBalance_withNullCountryCode_fails() throws Exception {
		accountRegisterService.payForBackgroundCheckUsingBalance(1L, new BackgroundCheck(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void payForBackgroundCheckUsingBalance_withInvalidCountryCode_fails() throws Exception {
		accountRegisterService.payForBackgroundCheckUsingBalance(1L, new BackgroundCheck(), "SOME INVALID COUNTRY");
	}

	@Test(expected = IllegalArgumentException.class)
	public void validatePayWork_withNullWork_fails() {
		accountRegisterService.validatePayWork(null);
	}

	@Test
	public void validatePayWork_withWorkPendingFulfillment_returnsFalse() {
		when(workService.isWorkPendingFulfillment(anyLong())).thenReturn(true);
		assertFalse(accountRegisterService.validatePayWork(work));
	}

	@Test
	public void validatePayWork_withWorkNotInPendingPaymentStatus_returnsFalse() {
		when(work.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(work.isPaymentPending()).thenReturn(false);
		assertFalse(accountRegisterService.validatePayWork(work));
	}

	@Test
	public void validatePayWork_withWorkNotInvoiced_returnsFalse() {
		when(work.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(work.isInvoiced()).thenReturn(false);
		assertFalse(accountRegisterService.validatePayWork(work));
	}

	@Test
	public void validatePayWork_withBundledInvoice_returnsFalse() {
		when(work.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAYMENT_PENDING));
		Invoice invoice = mock(Invoice.class);
		when(work.getInvoice()).thenReturn(invoice);
		when(invoice.isBundled()).thenReturn(true);
		assertFalse(accountRegisterService.validatePayWork(work));
	}

	@Test(expected = IllegalArgumentException.class)
	public void repriceWork_withNullWork_fails() {
		accountRegisterService.repriceWork(null);
	}

	@Test
	public void repriceWork_withActiveWork_shouldFindTheActiveResource() {
		when(work.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(work.isActive()).thenReturn(true);
		accountRegisterService.repriceWork(work);
		verify(workService, times(1)).findActiveWorkResource(anyLong());
	}


	@Test
	public void repriceWork_withWorkBundleAndBudgetSet_shouldAuthorizeBundle() {
		when(workBundle.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.DRAFT));
		when(workBundle.isWorkBundle()).thenReturn(true);
		when(workBundle.getBundleBudget()).thenReturn(BigDecimal.TEN);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(null);
		accountRegisterService.repriceWork(workBundle);
		verify(registerTransactionDAO, times(1)).findWorkResourcePendingPaymentTermsCommitmentTransaction(anyLong());
		verify(registerTransactionExecutableFactory, times(1)).newInstance(RegisterTransactionType.BUYER_AUTHORIZATION_WORK_BUNDLE_PAYMENT_TERMS_VIRTUAL_CODE);
	}

	@Test(expected = AccountRegisterNoActiveWorkResourceException.class)
	public void repriceWork_withActiveWorkAndNullWorkResource_fails() {
		when(work.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(work.isActive()).thenReturn(true);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(null);
		accountRegisterService.repriceWork(work);
	}

	@Test
	public void repriceWork_withNonActiveWork_shouldNotAuthorizeWork() {
		when(work.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.SENT));
		when(work.isActive()).thenReturn(false);
		accountRegisterService.repriceWork(work);
		verify(workService, times(1)).findActiveWorkResource(anyLong());
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifySufficientBuyerFunds_withNullArguments_fails() {
		accountRegisterService.verifySufficientBuyerFunds(null, new Project(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWorkRegisterTransactionDueDate_withNulls_fails() {
		accountRegisterService.updateWorkRegisterTransactionDueDate(null, null);
	}

	@Test
	public void updateWorkRegisterTransactionDueDate_withEmptyTxs_returnsFalse() {
		List<WorkResourceTransaction> workResourceTransactions = Lists.newArrayList();
		when(registerTransactionDAO.findWorkResourceTransactionPaymentTermsCommitmentReceivePay(anyLong())).thenReturn(workResourceTransactions);
		assertFalse(accountRegisterService.updateWorkRegisterTransactionDueDate(workResource, new WorkCostDTO().setTotalBuyerCost(BigDecimal.TEN)));
	}

	@Test
	public void updateWorkRegisterTransactionDueDate_withTxs_returnsTrue() {
		WorkResourceTransaction workResourceTransaction = new WorkResourceTransaction();
		workResourceTransaction.setAmount(BigDecimal.valueOf(5));
		List<WorkResourceTransaction> workResourceTransactions = Lists.newArrayList(workResourceTransaction);
		when(registerTransactionDAO.findWorkResourceTransactionPaymentTermsCommitmentReceivePay(anyLong())).thenReturn(workResourceTransactions);
		assertTrue(accountRegisterService.updateWorkRegisterTransactionDueDate(workResource, new WorkCostDTO().setTotalBuyerCost(BigDecimal.TEN)));
		verify(registerTransactionExecutableFactory, times(2)).newInstance(anyString());
	}

	@Test
	public void updateWorkRegisterTransactionDueDate_withTxsAndSameAmountEarned_returnsTrue() {
		FulfillmentStrategy fulfillmentStrategy = new FulfillmentStrategy();
		fulfillmentStrategy.setAmountEarned(BigDecimal.valueOf(5));
		when(work.getFulfillmentStrategy()).thenReturn(fulfillmentStrategy);

		WorkResourceTransaction workResourceTransaction = new WorkResourceTransaction();
		workResourceTransaction.setAmount(BigDecimal.valueOf(5));
		List<WorkResourceTransaction> workResourceTransactions = Lists.newArrayList(workResourceTransaction);
		when(registerTransactionDAO.findWorkResourceTransactionPaymentTermsCommitmentReceivePay(anyLong())).thenReturn(workResourceTransactions);
		assertTrue(accountRegisterService.updateWorkRegisterTransactionDueDate(workResource, new WorkCostDTO().setTotalBuyerCost(BigDecimal.TEN)));
		verify(registerTransactionExecutableFactory, never()).newInstance(anyString());
	}

	@Test
	public void payPaymentTerms_withEmptyTxs_returnsEmptyTransactionList() {
		List<WorkWorkResourceAccountRegister> workWorkResourceAccountRegisters = Lists.newArrayList();
		assertTrue(accountRegisterService.payPaymentTerms(workWorkResourceAccountRegisters).isEmpty());
	}

	@Test
	public void payPaymentTerms_withLane1_success() {
		WorkWorkResourceAccountRegister workWorkResourceAccountRegister = new WorkWorkResourceAccountRegister().setWorkId(1L);
		List<WorkWorkResourceAccountRegister> workWorkResourceAccountRegisters = Lists.newArrayList(workWorkResourceAccountRegister);
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(LaneType.LANE_1);
		assertFalse(accountRegisterService.payPaymentTerms(workWorkResourceAccountRegisters).isEmpty());
		verify(workService, never()).findWork(anyLong(), anyBoolean());
	}

	@Test
	public void payPaymentTerms_withLane0_success() {
		WorkWorkResourceAccountRegister workWorkResourceAccountRegister = new WorkWorkResourceAccountRegister().setWorkId(1L);
		List<WorkWorkResourceAccountRegister> workWorkResourceAccountRegisters = Lists.newArrayList(workWorkResourceAccountRegister);
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(LaneType.LANE_0);
		assertFalse(accountRegisterService.payPaymentTerms(workWorkResourceAccountRegisters).isEmpty());
		verify(workService, never()).findWork(anyLong(), anyBoolean());
	}

	@Test
	public void payPaymentTerms_withLane3_payWorkSuccessfully() {
		WorkWorkResourceAccountRegister workWorkResourceAccountRegister = new WorkWorkResourceAccountRegister()
				.setWorkId(1L).setCompanyId(1L).setWorkResourceId(1L);
		List<WorkWorkResourceAccountRegister> workWorkResourceAccountRegisters = Lists.newArrayList(workWorkResourceAccountRegister);
		when(laneService.getLaneTypeForUserAndCompany(anyLong(), anyLong())).thenReturn(LaneType.LANE_3);
		when(work.isPaymentPending()).thenReturn(true);
		when(work.isInvoiced()).thenReturn(true);
		Invoice invoice = mock(Invoice.class);
		when(work.getInvoice()).thenReturn(invoice);
		when(accountRegisterDAO.get(anyLong())).thenReturn(accountRegister);
		when(accountRegister.getAvailableCash()).thenReturn(BigDecimal.valueOf(1000));

		assertFalse(accountRegisterService.payPaymentTerms(workWorkResourceAccountRegisters).isEmpty());
		verify(workService, times(1)).findWork(anyLong(), anyBoolean());
		verify(accountRegisterDAO, times(1)).get(anyLong());
		verify(workResourceService, times(1)).findWorkResourceById(anyLong());

		verify(accountPricingService, times(1)).findAccountServiceTypeConfiguration(any(Work.class));
		verify(work, times(1)).setAccountPricingServiceTypeEntity(any(AccountPricingServiceTypeEntity.class));
	}
}
