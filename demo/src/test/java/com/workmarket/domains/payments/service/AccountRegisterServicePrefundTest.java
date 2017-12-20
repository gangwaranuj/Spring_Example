package com.workmarket.domains.payments.service;

import com.google.common.base.Optional;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.domains.payments.dao.AccountRegisterSummaryFieldsDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionActivityDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.dao.WeeklyRevenueReportDAO;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.CreditCardType;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.InvoicePaymentHelper;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.accountregister.AddFunds;
import com.workmarket.service.business.accountregister.BuyerAuthorizationImmediateWorkPayment;
import com.workmarket.service.business.accountregister.FeeCalculator;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.accountregister.RemoveFundsFromGeneralCash;
import com.workmarket.service.business.accountregister.ResourceAuthorizationImmediateWorkPayment;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionExecutableFactory;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionFactory;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.project.ProjectBudgetService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InsufficientSpendLimitException;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.PaymentService;
import com.workmarket.service.option.OptionsService;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.utility.NumberUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

/**
 * author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountRegisterServicePrefundTest {

	@Mock private LaneService laneService;
	@Mock private PaymentService paymentService;
	@Mock private UserNotificationService userNotificationService;
	@Mock private WorkService workService;
	@Mock private PricingService pricingService;
	@Mock private TaxService taxService;
	@Mock private WorkResourceService workResourceService;
	@Mock private BankAccountDAO bankAccountDAO;
	@Mock private UserDAO userDAO;
	@Mock private RegisterTransactionDAO registerTransactionDAO;
	@Mock private AccountRegisterDAO accountRegisterDAO;
	@Mock private AccountRegisterSummaryFieldsDAO accountRegisterSummaryFieldsDAO;
	@Mock private RegisterTransactionActivityDAO registerTransactionActivityDAO;
	@Mock private WeeklyRevenueReportDAO weeklyRevenueReportDAO;
	@Mock private RegisterTransactionFactory registerTransactionFactory;
	@Mock private FeeCalculator feeCalculator;
	@Mock private ProjectService projectService;
	@Mock private CompanyService companyService;
	@Mock private InvoicePaymentHelper invoicePaymentHelper;
	@Mock private ProjectBudgetService projectBudgetService;
	@Mock private RegisterTransactionExecutableFactory registerTransactionExecutableFactory;
	@Mock private OptionsService<AbstractWork> workOptionsService;
	@Mock private FeatureEvaluator featureEvaluator;
	@Mock private AuthenticationService authenticationService;

	@InjectMocks AccountRegisterServiceAbstract accountRegisterService = new AccountRegisterServicePrefundImpl();

	private WorkResource workResource;
	private Work work;
	private WorkBundle workBundle;
	private WorkCostDTO workCostDTO;
	private User userResource;
	private User userBuyer;
	private Project project;
	private AccountRegisterSummaryFields accountRegisterSummaryFields;
	private AccountRegisterSummaryFields lowBalanceAccountRegisterSummaryFields;
	private AccountRegister accountRegister;

	private RegisterTransactionExecutor registerTransactionsAbstract;
	private ResourceAuthorizationImmediateWorkPayment resourceAuthorizationImmediateWorkPayment;
	private BuyerAuthorizationImmediateWorkPayment buyerAuthorizationImmediateWorkPayment;
	private RemoveFundsFromGeneralCash removeFundsFromGeneral;
	private AddFunds addFunds;

	@Before
	public void setUp() throws Exception {
		accountRegisterSummaryFields = new AccountRegisterSummaryFields();
		accountRegisterSummaryFields.setActualCash(BigDecimal.valueOf(300));
		accountRegisterSummaryFields.setAvailableCash(BigDecimal.valueOf(300));

		lowBalanceAccountRegisterSummaryFields = new AccountRegisterSummaryFields();
		lowBalanceAccountRegisterSummaryFields.setActualCash(BigDecimal.valueOf(30));
		lowBalanceAccountRegisterSummaryFields.setAvailableCash(BigDecimal.valueOf(30));

		accountRegister = mock(AccountRegister.class);
		workResource = mock(WorkResource.class);
		workCostDTO = mock(WorkCostDTO.class);
		work = mock(Work.class);
		workBundle = mock(WorkBundle.class);
		userResource = mock(User.class);
		userBuyer = mock(User.class);
		project = mock(Project.class);
		accountRegister = mock(AccountRegister.class);

		registerTransactionsAbstract = mock(RegisterTransactionExecutor.class);
		resourceAuthorizationImmediateWorkPayment = mock(ResourceAuthorizationImmediateWorkPayment.class);
		buyerAuthorizationImmediateWorkPayment = mock(BuyerAuthorizationImmediateWorkPayment.class);
		removeFundsFromGeneral = mock(RemoveFundsFromGeneralCash.class);
		addFunds = mock(AddFunds.class);

		when(workResource.getWork()).thenReturn(work);
		when(workResource.getUser()).thenReturn(userResource);

		when(userResource.getCompany()).thenReturn(mock(Company.class));
		when(userBuyer.getCompany()).thenReturn(mock(Company.class));
		when(userBuyer.getSpendLimit()).thenReturn(BigDecimal.valueOf(1000L));

		when(workBundle.getId()).thenReturn(1000L);
		when(work.getId()).thenReturn(1L);
		when(work.getCompany()).thenReturn(mock(Company.class));
		when(work.getFulfillmentStrategy()).thenReturn(mock(FulfillmentStrategy.class));
		when(work.getBuyer()).thenReturn(userBuyer);
		when(workOptionsService.hasOption(any(Work.class), anyString(), anyString())).thenReturn(false);

		when(workCostDTO.getTotalBuyerCost()).thenReturn(BigDecimal.valueOf(110));

		when(project.getRemainingBudget()).thenReturn(BigDecimal.valueOf(50));
		when(project.getBudgetEnabledFlag()).thenReturn(true);

		when(accountRegister.getAvailableCash()).thenReturn(BigDecimal.TEN);

		when(pricingService.calculateMaximumResourceCost(any(Work.class))).thenReturn(BigDecimal.valueOf(100));
		when(pricingService.calculateTotalResourceCost(any(Work.class), any(WorkResource.class))).thenReturn(BigDecimal.valueOf(100));
		when(pricingService.calculateBuyerNetMoneyFee(any(Work.class), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(10));
		when(pricingService.findDefaultRegisterForCompany(anyLong(), anyBoolean())).thenReturn(accountRegister);
		when(pricingService.findDefaultRegisterForCompany(anyLong())).thenReturn(accountRegister);

		doNothing().when(projectBudgetService).decreaseRemainingBudget(any(Project.class), any(BigDecimal.class));

		when(registerTransactionsAbstract.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class), eq(true))).thenReturn(mock(WorkResourceTransaction.class));
		when(resourceAuthorizationImmediateWorkPayment.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class))).thenReturn(mock(WorkResourceTransaction.class));
		when(buyerAuthorizationImmediateWorkPayment.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class), eq(true))).thenReturn(mock(WorkResourceTransaction.class));
		when(buyerAuthorizationImmediateWorkPayment.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class))).thenReturn(mock(WorkResourceTransaction.class));
		when(removeFundsFromGeneral.execute(any(Work.class), any(WorkResource.class), any(AccountRegister.class), any(BigDecimal.class))).thenReturn(mock(WorkResourceTransaction.class));

		when(accountRegisterSummaryFieldsDAO.findAccountRegisterSummaryByCompanyId(anyLong())).thenReturn(Optional.fromNullable(accountRegisterSummaryFields));

		when(userDAO.get(anyLong())).thenReturn(userBuyer);

		when(registerTransactionExecutableFactory.newInstance("pendingPaycommit")).thenReturn(resourceAuthorizationImmediateWorkPayment);
		when(registerTransactionExecutableFactory.newInstance("pendingCommitment")).thenReturn(buyerAuthorizationImmediateWorkPayment);
		when(registerTransactionExecutableFactory.newInstance("removegenr")).thenReturn(removeFundsFromGeneral);
		when(registerTransactionExecutableFactory.newInstance("addfunds")).thenReturn(addFunds);
		when(registerTransactionExecutableFactory.newInstance("bkgrdchk")).thenReturn(mock(com.workmarket.service.business.accountregister.BackgroundCheck.class));

		when(featureEvaluator.hasFeature(anyLong(), anyObject())).thenReturn(true);
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
		when(accountRegisterSummaryFieldsDAO.findAccountRegisterSummaryByCompanyId(anyLong())).thenReturn(Optional.fromNullable(lowBalanceAccountRegisterSummaryFields));
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

	@Test(expected = UnsupportedOperationException.class)
	public void payPaymentTerms_fails() throws Exception {
		accountRegisterService.payPaymentTerms(Collections.EMPTY_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateCostOnCompleteWork_withNullArgs_fails() throws Exception {
		 accountRegisterService.calculateCostOnCompleteWork(null, null);
	}

	@Test
	public void calculateCostOnCompleteWork_success() throws Exception {
		WorkCostDTO costDTO = accountRegisterService.calculateCostOnCompleteWork(work, workResource);
		assertEquals(costDTO.getTotalBuyerCost().doubleValue(), 110.00, .001);
	}

	@Test
	public void calculateCostOnCompleteWork_withZeroFee_success() throws Exception {
		when(pricingService.calculateTotalResourceCost(any(Work.class), any(WorkResource.class))).thenReturn(BigDecimal.valueOf(464.63));
		when(pricingService.calculateBuyerNetMoneyFee(any(Work.class), any(BigDecimal.class))).thenReturn(BigDecimal.ZERO);
		WorkCostDTO costDTO = accountRegisterService.calculateCostOnCompleteWork(work, workResource);
		assertEquals(costDTO.getTotalBuyerCost().doubleValue(), 464.63, .001);
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
		assertEquals(fulfillmentStrategy.getBuyerTotalCost().doubleValue(), 110.0, .001);
		assertEquals(fulfillmentStrategy.getWorkPrice().doubleValue(), 100.0, .001);
		assertEquals(fulfillmentStrategy.getAmountEarned().doubleValue(), 100.0, .001);
		assertEquals(fulfillmentStrategy.getBuyerFee().doubleValue(), 10.0, .001);
		assertEquals(fulfillmentStrategy.getWorkPricePriorComplete().doubleValue(), 100.0, .001);
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

	@Test
	public void generateRandomAchVerifyValue() {
		BigDecimal amount = accountRegisterService.generateRandomAchVerifyValue();
		assertNotNull(amount);
		assertTrue(NumberUtilities.isPositive(amount));
		assertTrue(amount.compareTo(BigDecimal.ONE) < 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void payForBackgroundCheckUsingCreditCard_invalidCardData_fails() throws Exception {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount("5");
		paymentDTO.setCardType("Mastercard");
		when(pricingService.findBackgroundCheckPrice(anyLong(), anyString())).thenReturn(BigDecimal.TEN);
		assertFalse(accountRegisterService.payForBackgroundCheckUsingCreditCard(1L, new BackgroundCheck(), paymentDTO, Country.US));
	}

	@Test
	public void payForBackgroundCheckUsingCreditCard_withValidCardData_success() throws Exception {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount("5");
		paymentDTO.setCardNumber("4263846384");
		paymentDTO.setCardType(CreditCardType.AMERICAN_EXPRESS.getName());
		when(pricingService.findBackgroundCheckPrice(anyLong(), anyString())).thenReturn(BigDecimal.TEN);
		when(registerTransactionFactory.newBackgroundCheckRegisterTransactionType(anyString())).thenReturn(new RegisterTransactionType(RegisterTransactionType.BACKGROUND_CHECK));

		PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
		paymentResponseDTO.setApproved(true);
		when(paymentService.doCardPayment(any(PaymentDTO.class))).thenReturn(paymentResponseDTO);
		assertTrue(accountRegisterService.payForBackgroundCheckUsingCreditCard(1L, new BackgroundCheck(), paymentDTO, Country.US));
	}

	@Test
	public void payForBackgroundCheckUsingCreditCard_paymentFails() throws Exception {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount("5");
		paymentDTO.setCardNumber("4263846384");
		paymentDTO.setCardType(CreditCardType.AMERICAN_EXPRESS.getName());
		when(pricingService.findBackgroundCheckPrice(anyLong(), anyString())).thenReturn(BigDecimal.TEN);
		when(registerTransactionFactory.newBackgroundCheckRegisterTransactionType(anyString())).thenReturn(new RegisterTransactionType(RegisterTransactionType.BACKGROUND_CHECK));

		PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
		paymentResponseDTO.setApproved(false);
		when(paymentService.doCardPayment(any(PaymentDTO.class))).thenReturn(paymentResponseDTO);
		assertFalse(accountRegisterService.payForBackgroundCheckUsingCreditCard(1L, new BackgroundCheck(), paymentDTO, Country.US));
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateApLimit_withNullAmount_fails() {
		accountRegisterService.updateApLimit(1L, null);
	}

	@Test
	public void updateApLimit_withAmount_success() {
		when(accountRegister.getApLimit()).thenReturn(BigDecimal.ZERO);
		accountRegisterService.updateApLimit(1L, BigDecimal.TEN);
		verify(accountRegister, times(1)).setApLimit(eq(BigDecimal.TEN));
	}

	@Test
	public void updateApLimit_withAmountAndPreviousLimit_success() {
		when(accountRegister.getApLimit()).thenReturn(BigDecimal.TEN);
		accountRegisterService.updateApLimit(1L, BigDecimal.TEN);
		verify(accountRegister, never()).setApLimit(eq(BigDecimal.TEN));
	}
}

