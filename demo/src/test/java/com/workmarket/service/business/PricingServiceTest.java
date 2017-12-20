package com.workmarket.service.business;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.dao.account.WorkFeeConfigurationDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.WorkFeeBand;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.exception.account.OverAPLimitException;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.domains.model.pricing.PricingStrategyUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PricingServiceTest {

	@Mock WorkService workService;
	@Mock AccountRegisterDAO accountRegisterDAO;
	@Mock WorkFeeConfigurationDAO workFeeConfigurationDAO;
	@Mock CompanyDAO companyDAO;
	@Mock AccountRegisterService accountRegisterService;
	@InjectMocks @Spy PricingServiceImpl pricingService;

	private Multimap<Long, WorkExpenseNegotiation> preCompleteExpenses = ArrayListMultimap.create();
	private Multimap<Long, WorkBonusNegotiation> preCompleteBonuses = ArrayListMultimap.create();

	private static long id = 0, companyID = 1L;
	private Work work;
	private WorkResource workResource;
	private WorkStatusType workStatusType;
	private PricingStrategy pricingStrategy;
	private FlatPricePricingStrategy flatPricePricingStrategy;
	private FullPricingStrategy fullPricingStrategy;
	private FulfillmentStrategy fulfillmentStrategy;
	private AccountRegister accountRegister;

	@Before
	public void init() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		WorkNegotiationService workNegotiationService = mock(WorkNegotiationService.class);

		pricingService.setWorkNegotiationService(workNegotiationService);

		final ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
		when(workNegotiationService.findPreCompletionExpenseIncreasesForWork(idCaptor.capture()))
				.thenAnswer(new Answer<List<WorkExpenseNegotiation>>() {
					@Override public List<WorkExpenseNegotiation> answer(InvocationOnMock invocation) throws Throwable {
						return Lists.newArrayList(preCompleteExpenses.get(idCaptor.getValue()));
					}
				});
		when(workNegotiationService.findPreCompletionBonusesForWork(idCaptor.capture()))
				.thenAnswer(new Answer<List<WorkBonusNegotiation>>() {
					@Override public List<WorkBonusNegotiation> answer(InvocationOnMock invocation) throws Throwable {
						return Lists.newArrayList(preCompleteBonuses.get(idCaptor.getValue()));
					}
				});
		id++;

		workResource = mock(WorkResource.class);
		work = mock(Work.class);
		workStatusType = mock(WorkStatusType.class);
		workResource = mock(WorkResource.class);
		pricingStrategy = mock(PricingStrategy.class);
		flatPricePricingStrategy = mock(FlatPricePricingStrategy.class);
		fullPricingStrategy = mock(FullPricingStrategy.class);
		fulfillmentStrategy = mock(FulfillmentStrategy.class);
		accountRegister = mock(AccountRegister.class);

		when(work.getWorkStatusType()).thenReturn(workStatusType);
		when(work.getId()).thenReturn(1L);
		when(work.getPricingStrategy()).thenReturn(pricingStrategy);
		when(work.getFulfillmentStrategy()).thenReturn(fulfillmentStrategy);

		when(accountRegisterDAO.findByCompanyId(anyLong(), anyBoolean())).thenReturn(accountRegister);

		when(workService.findActiveWorkResource(any(Long.class))).thenReturn(workResource);
	}

	@Test(expected = NullPointerException.class)
	public void adjustPricingByCompanyFeePercentage_NullException() throws Exception {
		pricingService.adjustPricingByCompanyFeePercentage(null, null);
	}

	private void adjustPricingByCompanyFeePercentageSetupFlat(BigDecimal percentage, BigDecimal flatPrice) {
		when(accountRegister.getCurrentWorkFeePercentage()).thenReturn(percentage);
		when(pricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
		when(pricingStrategy.getId()).thenReturn((long) PricingStrategyType.FLAT.ordinal() + 1);
		when(fullPricingStrategy.getFlatPrice()).thenReturn(flatPrice);

		doReturn(pricingStrategy).when(pricingService).copyPricingStrategy(pricingStrategy);
	}

	@Test
	public void adjustPricingByCompanyFeePercentage_Flat_PercentageExceedsMaxFee() throws Exception {
		BigDecimal percentage = new BigDecimal(10);
		BigDecimal overFlatPrice = new BigDecimal(Constants.MAX_WORK_FEE.intValue()*10);
		adjustPricingByCompanyFeePercentageSetupFlat(percentage, overFlatPrice);

		pricingService.adjustPricingByCompanyFeePercentage(pricingStrategy, 1L);

		ArgumentCaptor<BigDecimal> arg = ArgumentCaptor.forClass(BigDecimal.class);
		verify(fullPricingStrategy).setFlatPrice(arg.capture());
		assertEquals((int)(Constants.MAX_WORK_FEE.intValue() * 10 * .909), arg.getValue().intValue());
	}

	@Test
	public void adjustPricingByCompanyFeePercentage_Flat_PercentageLessThanMaxFee() throws Exception {
		BigDecimal percentage = new BigDecimal(10);
		BigDecimal flatPrice = Constants.MAX_WORK_FEE;
		adjustPricingByCompanyFeePercentageSetupFlat(percentage, flatPrice);

		pricingService.adjustPricingByCompanyFeePercentage(pricingStrategy, 1L);

		ArgumentCaptor<BigDecimal> arg = ArgumentCaptor.forClass(BigDecimal.class);
		verify(fullPricingStrategy).setFlatPrice(arg.capture());
		assertEquals((int)(Constants.MAX_WORK_FEE.intValue()*.909), arg.getValue().intValue());
	}

	@Test
	public void testCostForFlat() throws Exception {

		// flat
		WorkResource workResource = new WorkResource();

		Work flatWork = getFlatWork(BigDecimal.TEN);

		assertEquals(BigDecimal.TEN, pricingService.calculateTotalResourceCost(flatWork, workResource));
	}


	@Test
	public void testCostForFlatWithOverride() throws Exception {

		// flat
		WorkResource workResource = new WorkResource();

		Work flatWork = getFlatWork(BigDecimal.TEN);
		flatWork.getPricingStrategy().getFullPricingStrategy().setOverridePrice(BigDecimal.valueOf(15));

		assertEquals(BigDecimal.valueOf(15), pricingService.calculateTotalResourceCost(flatWork, workResource));
	}

	@Test
	public void testCostForFlatPreCompleteExpense() throws Exception {

		// flat
		Work flatWork = getFlatWork(BigDecimal.TEN);
		WorkResource workResource = new WorkResource();

		// put a re-im of 7 on the assignment
		addReimbursement(flatWork, workResource, BigDecimal.valueOf(7));

		assertEquals(BigDecimal.valueOf(17), pricingService.calculateTotalResourceCost(flatWork, workResource));
	}

	@Test
	public void testCostForFlatPostCompleteExpense() throws Exception {

		// flat
		WorkResource workResource = new WorkResource();

		Work flatWork = getFlatWork(BigDecimal.TEN);

		// put a re-im of 7 on the assignment
		addPostCompleteReimbursement(flatWork, BigDecimal.valueOf(7));

		assertEquals(BigDecimal.valueOf(17), pricingService.calculateTotalResourceCost(flatWork, workResource));
	}

	@Test
	public void testCostForPerUnitPreCompleteExpense() throws Exception {

		WorkResource workResource = new WorkResource();

		Work perUnitWork = getPerUnitWork(BigDecimal.valueOf(2), BigDecimal.valueOf(5));

		// put a re-im of 7 on the assignment
		addReimbursement(perUnitWork, workResource, BigDecimal.valueOf(7));

		workResource.setUnitsProcessed(perUnitWork.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfUnits());

		assertEquals(BigDecimal.valueOf(17).setScale(2), pricingService.calculateTotalResourceCost(perUnitWork, workResource));
	}

	@Test
	public void testCostForPerUnitPreCompleteExpense_threeDecimals_roundUp() throws Exception {
		WorkResource workResource = new WorkResource();
		Work perUnitWork = getPerUnitWork(BigDecimal.valueOf(2), BigDecimal.valueOf(5));
		workResource.setUnitsProcessed(perUnitWork.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfUnits());
		assertEquals(BigDecimal.valueOf(1.45), calculateTotalResourceCost(1, 1.445));
	}

	@Test
	public void testCostForPerUnitPreCompleteExpense_threeDecimals_roundDown() throws Exception {
		WorkResource workResource = new WorkResource();
		Work perUnitWork = getPerUnitWork(BigDecimal.valueOf(2), BigDecimal.valueOf(5));
		workResource.setUnitsProcessed(perUnitWork.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfUnits());
		assertEquals(BigDecimal.valueOf(1.44), calculateTotalResourceCost(1, 1.444));
	}

	@Test
	public void testCostForBlendedCounterOfferExpense() throws Exception {
		WorkResource workResource = new WorkResource();

		Work blendedWork = getBlendedPerHourWork(BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(3), BigDecimal.valueOf(4));

		// put expenses of 7 on the assignment
		addPreAcceptReimbursement(blendedWork, workResource, BigDecimal.valueOf(7));

		workResource.setHoursWorked(
				blendedWork.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours().add(
				blendedWork.getPricingStrategy().getFullPricingStrategy().getMaxBlendedNumberOfHours()));

		assertEquals(BigDecimal.valueOf(29), pricingService.calculateTotalResourceCost(blendedWork, workResource));
	}

	@Test
	public void testCostForBlendedExpensesWithHoursOverride() throws Exception {
		WorkResource workResource = new WorkResource();

		Work blendedWork = getBlendedPerHourWork(BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(3), BigDecimal.valueOf(4));

		// put expenses of 7 on the assignment
		addReimbursement(blendedWork, workResource, BigDecimal.valueOf(7));

		workResource.setHoursWorked(blendedWork.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours());

		assertEquals(BigDecimal.valueOf(17), pricingService.calculateTotalResourceCost(blendedWork, workResource));
	}


	@Test
	public void testFlatPreAndPostExpenses() throws Exception {
		WorkResource workResource = new WorkResource();

		Work flatWork = getFlatWork(BigDecimal.TEN);

		// put expenses of 7 on the assignment before, and 3 after (total 10)
		addReimbursement(flatWork, workResource, BigDecimal.valueOf(7));
		addPostCompleteReimbursement(flatWork, BigDecimal.valueOf(3));

		workResource.setHoursWorked(flatWork.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours());

		assertEquals(BigDecimal.valueOf(20), pricingService.calculateTotalResourceCost(flatWork, workResource));
	}

	@Test
	public void testFlatPreAndPostExpensesWithExpenseOverride() throws Exception {
		WorkResource workResource = new WorkResource();

		Work flatWork = getFlatWork(BigDecimal.TEN);

		// put expenses of 7 on the assignment before, override to 1, and 3 after (total 4)
		addReimbursement(flatWork, workResource, BigDecimal.valueOf(7));
		completeWork(flatWork);
		overrideExpenses(workResource, BigDecimal.ONE);
		addPostCompleteReimbursement(flatWork, BigDecimal.valueOf(3));

		workResource.setHoursWorked(flatWork.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours());

		assertEquals(BigDecimal.valueOf(14), pricingService.calculateTotalResourceCost(flatWork, workResource));
	}


	@Test
	public void testFlatPreAndPostExpensesWithPreAndPostBonuses() throws Exception {
		WorkResource workResource = new WorkResource();

		Work flatWork = getFlatWork(BigDecimal.TEN);

		// put expenses of 7 on the assignment before 3 after, bonus of 1 before and 2 after, total = 13
		addReimbursement(flatWork, workResource, BigDecimal.valueOf(7));
		addBonus(flatWork, workResource, BigDecimal.ONE);
		completeWork(flatWork);
		addPostCompleteReimbursement(flatWork, BigDecimal.valueOf(3));
		addPostCompleteBonus(flatWork, BigDecimal.valueOf(2));

		workResource.setHoursWorked(flatWork.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours());

		assertEquals(BigDecimal.valueOf(23), pricingService.calculateTotalResourceCost(flatWork, workResource));
	}


	@Test
	public void testFlatPreAndPostExpensesWithPreAndPostBonusesWithExpenseOverride() throws Exception {
		WorkResource workResource = new WorkResource();

		Work flatWork = getFlatWork(BigDecimal.TEN);

		// put expenses of 7 on the assignment before, override to 1, 3 after, bonus of 1 before and 2 after, total = 7
		addReimbursement(flatWork, workResource, BigDecimal.valueOf(7));
		addBonus(flatWork, workResource, BigDecimal.ONE);
		completeWork(flatWork);
		overrideExpenses(workResource, BigDecimal.ONE);
		addPostCompleteReimbursement(flatWork, BigDecimal.valueOf(3));
		addPostCompleteBonus(flatWork, BigDecimal.valueOf(2));

		workResource.setHoursWorked(flatWork.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours());

		assertEquals(BigDecimal.valueOf(17), pricingService.calculateTotalResourceCost(flatWork, workResource));
	}


	@Test
	public void testBlendedCounterOfferWithPreAndPostExpensesWithPreAndPostBonusesWithExpenseOverride() throws Exception {
		WorkResource workResource = new WorkResource();

		Work blendedWork = getBlendedPerHourWork(BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(3), BigDecimal.valueOf(4));

		// put expenses of 1 on the assignment before, 7 after, override to 6, 3 after, bonus of 1 before and 2 after, total = 13
		addPreAcceptReimbursement(blendedWork, workResource, BigDecimal.ONE);
		addReimbursement(blendedWork, workResource, BigDecimal.valueOf(7));
		addBonus(blendedWork, workResource, BigDecimal.ONE);
		completeWork(blendedWork);
		overrideExpenses(workResource, BigDecimal.valueOf(6));
		addPostCompleteReimbursement(blendedWork, BigDecimal.valueOf(3));
		addPostCompleteBonus(blendedWork, BigDecimal.valueOf(2));

		workResource.setHoursWorked(blendedWork.getPricingStrategy().getFullPricingStrategy().getInitialNumberOfHours()
				.add(blendedWork.getPricingStrategy().getFullPricingStrategy().getMaxBlendedNumberOfHours()));

		assertEquals(BigDecimal.valueOf(35), pricingService.calculateTotalResourceCost(blendedWork, workResource));
	}

	@Test
	public void testWorkPriceInSent() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.SENT);
		when(work.getPricingStrategy()).thenReturn(flatPricePricingStrategy);
		when(flatPricePricingStrategy.getFlatPrice()).thenReturn(BigDecimal.ONE);
		assertTrue(pricingService.calculateWorkPrice(work).compareTo(BigDecimal.ONE) == 0);
	}


	@Test
	public void testWorkPriceInActive() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);
		when(work.getPricingStrategy()).thenReturn(flatPricePricingStrategy);
		when(flatPricePricingStrategy.getFlatPrice()).thenReturn(BigDecimal.ONE);
		assertTrue(pricingService.calculateWorkPrice(work).compareTo(BigDecimal.ONE) == 0);
	}


	@Test
	public void testWorkPriceInPaid() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.PAID);
		when(fulfillmentStrategy.getWorkPrice()).thenReturn(BigDecimal.ONE);
		assertTrue(pricingService.calculateWorkPrice(work).compareTo(BigDecimal.ONE) == 0);
	}

	@Test
	public void testWorkPriceInCancelledWithPay() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.CANCELLED_WITH_PAY);
		when(workResource.getHoursWorked()).thenReturn(BigDecimal.ZERO);
		when(workResource.getUnitsProcessed()).thenReturn(BigDecimal.ZERO);
		when(pricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
		when(fullPricingStrategy.getOverridePrice()).thenReturn(BigDecimal.ONE);
		when(workResource.getAdditionalExpenses()).thenReturn(BigDecimal.ZERO);
		when(workResource.getBonus()).thenReturn(BigDecimal.ZERO);
		assertTrue(pricingService.calculateWorkPrice(work).compareTo(BigDecimal.ONE) == 0);
	}

	@Test
	public void testWorkPriceInCancelledPaymentPending() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.CANCELLED_PAYMENT_PENDING);
		when(workResource.getHoursWorked()).thenReturn(BigDecimal.ZERO);
		when(workResource.getUnitsProcessed()).thenReturn(BigDecimal.ZERO);
		when(pricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
		when(fullPricingStrategy.getOverridePrice()).thenReturn(BigDecimal.ONE);
		when(workResource.getAdditionalExpenses()).thenReturn(BigDecimal.ZERO);
		when(workResource.getBonus()).thenReturn(BigDecimal.ZERO);
		assertTrue(pricingService.calculateWorkPrice(work).compareTo(BigDecimal.ONE) == 0);
	}



	private void addPreAcceptReimbursement(Work work, WorkResource workResource, BigDecimal amount) {
		workResource.setAdditionalExpenses(NumberUtilities.defaultValue(workResource.getAdditionalExpenses()).add(amount));
		work.getPricingStrategy().getFullPricingStrategy().setAdditionalExpenses(
				NumberUtilities.defaultValue(work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses())
						.add(amount));
	}

	private void addReimbursement(Work work, WorkResource workResource, BigDecimal amount) {
		WorkExpenseNegotiation reim = new WorkExpenseNegotiation();
		PricingStrategy pricing = PricingStrategyUtilities.clonePricingStrategy(work.getPricingStrategy());
		pricing.getFullPricingStrategy().setAdditionalExpenses(amount);
		reim.setPricingStrategy(pricing);
		reim.setApprovalStatus(ApprovalStatus.APPROVED);
		reim.setDuringCompletion(false);
		reim.setCreatedOn(Calendar.getInstance());

		preCompleteExpenses.put(work.getId(), reim);

		workResource.setAdditionalExpenses(NumberUtilities.defaultValue(workResource.getAdditionalExpenses()).add(amount));
		work.getPricingStrategy().getFullPricingStrategy().setAdditionalExpenses(
				NumberUtilities.defaultValue(work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses())
						.add(amount));
	}

	private void addPostCompleteReimbursement(Work work, BigDecimal amount) {
		work.getPricingStrategy().getFullPricingStrategy().setAdditionalExpenses(
				NumberUtilities.defaultValue(work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses())
						.add(amount));
	}

	private void completeWork(Work work) {
		work.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.COMPLETE));
	}

	private void overrideExpenses(WorkResource workResource, BigDecimal amount) {
		workResource.setAdditionalExpenses(amount);
	}

	private void addBonus(Work work, WorkResource workResource, BigDecimal amount) {
		WorkBonusNegotiation bonus = new WorkBonusNegotiation();
		PricingStrategy pricing = PricingStrategyUtilities.clonePricingStrategy(work.getPricingStrategy());
		pricing.getFullPricingStrategy().setBonus(amount);
		bonus.setPricingStrategy(pricing);
		bonus.setApprovalStatus(ApprovalStatus.APPROVED);
		bonus.setDuringCompletion(false);
		bonus.setCreatedOn(Calendar.getInstance());

		preCompleteBonuses.put(work.getId(), bonus);

		workResource.setBonus(NumberUtilities.defaultValue(workResource.getBonus()).add(amount));
		work.getPricingStrategy().getFullPricingStrategy().setBonus(
				NumberUtilities.defaultValue(work.getPricingStrategy().getFullPricingStrategy().getBonus())
						.add(amount));
	}

	private void addPostCompleteBonus(Work work, BigDecimal amount) {
		work.getPricingStrategy().getFullPricingStrategy().setBonus(
				NumberUtilities.defaultValue(work.getPricingStrategy().getFullPricingStrategy().getBonus())
						.add(amount));
	}

	private Work getFlatWork(BigDecimal price) {
		FullPricingStrategy flatFps = new FullPricingStrategy();
		flatFps.setPricingStrategyType(PricingStrategyType.FLAT);
		flatFps.setFlatPrice(price);
		PricingStrategy flatPs = new PricingStrategy(flatFps, PricingStrategyType.FLAT);
		Work flatWork = new Work();
		flatWork.setId(id);
		flatWork.setPricingStrategy(flatPs);
		flatWork.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));
		return flatWork;
	}

	private Work getPerUnitWork(BigDecimal price, BigDecimal units) {
		FullPricingStrategy perUnitFps = new FullPricingStrategy();
		perUnitFps.setPricingStrategyType(PricingStrategyType.PER_UNIT);
		perUnitFps.setPerUnitPrice(price);
		perUnitFps.setMaxNumberOfUnits(units);

		PricingStrategy perUnitPs = new PricingStrategy(perUnitFps, PricingStrategyType.PER_UNIT);
		Work perUnitWork = new Work();
		perUnitWork.setId(id);
		perUnitWork.setPricingStrategy(perUnitPs);
		perUnitWork.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));
		return perUnitWork;
	}

	private Work getBlendedPerHourWork(
			BigDecimal initialPrice, BigDecimal initialHours, BigDecimal additionalPrice, BigDecimal maxHours) {

		FullPricingStrategy blendedPerHourFps = new FullPricingStrategy();
		blendedPerHourFps.setPricingStrategyType(PricingStrategyType.BLENDED_PER_HOUR);
		blendedPerHourFps.setInitialPerHourPrice(initialPrice);
		blendedPerHourFps.setInitialNumberOfHours(initialHours);
		blendedPerHourFps.setAdditionalPerHourPrice(additionalPrice);
		blendedPerHourFps.setMaxBlendedNumberOfHours(maxHours);
		PricingStrategy blendedPerHourPs = new PricingStrategy(blendedPerHourFps, PricingStrategyType.BLENDED_PER_HOUR);
		Work blendedPerHourWork = new Work();
		blendedPerHourWork.setId(id);
		blendedPerHourWork.setPricingStrategy(blendedPerHourPs);
		blendedPerHourWork.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));
		return blendedPerHourWork;
	}

	@Test
	public void calculateBuyerNetMoneyFee() {
		Work work = mock(Work.class);
		Company company = mock(Company.class);
		when(company.getId()).thenReturn(companyID);
		AccountRegister accountRegister = mock(AccountRegister.class);
		AccountPricingType accountPricingType = new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
		when(work.getCompany()).thenReturn(company);
		when(work.hasLegacyWorkFeeConfiguration()).thenReturn(false);
		when(work.hasSubscriptionPricing()).thenReturn(false);
		when(accountRegister.getCompany()).thenReturn(company);
		when(company.getAccountPricingType()).thenReturn(accountPricingType);
		when(accountRegisterService.getPaymentSummation(companyID)).thenReturn(BigDecimal.TEN);

		List<WorkFeeBand> workFeeBands = Lists.newArrayList();
		WorkFeeBand workFeeBand = new WorkFeeBand();
		workFeeBand.setMinimum(BigDecimal.ONE);
		workFeeBand.setMaximum(BigDecimal.valueOf(999999));
		workFeeBand.setPercentage(BigDecimal.TEN);

		workFeeBands.add(workFeeBand);
		WorkFeeConfiguration workFeeConfiguration = new WorkFeeConfiguration();
		workFeeConfiguration.setWorkFeeBands(workFeeBands);

		when(workFeeConfigurationDAO.findWithWorkFeeBands(anyLong())).thenReturn(workFeeConfiguration);

		assertEquals(pricingService.calculateBuyerNetMoneyFee(work, BigDecimal.valueOf(1334.45)), BigDecimal.valueOf(133.45));
		assertEquals(pricingService.calculateBuyerNetMoneyFee(work, BigDecimal.valueOf(45.45)), BigDecimal.valueOf(4.55));
	}

	@Test(expected = OverAPLimitException.class)
	public void updateApLimit_exceedMaxLimit_Fail() throws Exception {
		BigDecimal limitExceedMax = new BigDecimal(1000000000);
		pricingService.updateAPLimit(1L, String.valueOf(limitExceedMax));
	}

	@Test
	public void updateApLimit_withinMaxLimit_Success() throws Exception {

		Company company = mock(Company.class);
		AccountRegister accountRegister = mock(AccountRegister.class);
		when(companyDAO.findCompanyById(anyLong())).thenReturn(company);

		doReturn(accountRegister).when(pricingService).findDefaultRegisterForCompany(anyLong(), anyBoolean());

		BigDecimal limitWithinMax = new BigDecimal(1000);
		pricingService.updateAPLimit(1L, String.valueOf(limitWithinMax));
		verify(accountRegister).setApLimit(limitWithinMax);
	}

	private BigDecimal calculateTotalResourceCost(int units, double perUnitCost) throws Exception {
		WorkResource workResource = new WorkResource();
		Work perUnitWork = getPerUnitWork(BigDecimal.valueOf(perUnitCost), BigDecimal.valueOf(units));
		workResource.setUnitsProcessed(perUnitWork.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfUnits());
		return pricingService.calculateTotalResourceCost(perUnitWork, workResource);

	}

}
