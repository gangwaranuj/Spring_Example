package com.workmarket.domains.work.model.negotiation;

/**
 * Created by nick on 10/26/12 1:29 PM
 */
public class WorkPriceNegotiationFactoryImplTest {

/* TODO: move some of this to PricingStrategyUtilitiesTest once the code has been migrated there, then delete this file

	PricingService pricingService;
	WorkPriceNegotiationFactoryImpl factory;

	long flatId = PricingStrategyType.FLAT.ordinal() + 1L;
	long perHourId = PricingStrategyType.PER_HOUR.ordinal() + 1L;
	final PricingStrategy flatBudget = new PricingStrategy(PricingStrategyType.FLAT.getDescription(), flatId);
	final PricingStrategy reimbursement = new PricingStrategy(PricingStrategyType.PER_HOUR.getDescription(), perHourId);

	@Before
	public void init() {
		pricingService = mock(PricingService.class);

		flatBudget.getFullPricingStrategy().setFlatPrice(BigDecimal.valueOf(50D));

		reimbursement.getFullPricingStrategy().setPerHourPrice(BigDecimal.valueOf(5D));
		reimbursement.getFullPricingStrategy().setMaxNumberOfHours(BigDecimal.valueOf(3D));
		reimbursement.getFullPricingStrategy().setAdditionalExpenses(BigDecimal.valueOf(6D));

		when(pricingService.findPricingStrategyById(flatId)).thenAnswer(new Answer<Object>() {
			@Override public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				return flatBudget;
			}
		});
		when(pricingService.findPricingStrategyById(perHourId)).thenAnswer(new Answer<Object>() {
			@Override public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				return reimbursement;
			}
		});

		factory = new WorkPriceNegotiationFactoryImpl();
		factory.setPricingService(pricingService);
	}

	@Test
	public void testNewInstanceByType() throws Exception {
		assertTrue(factory.newInstance(AbstractWorkPriceNegotiation.REIMBURSEMENT) instanceof WorkExpenseNegotiation);
		assertTrue(factory.newInstance(AbstractWorkPriceNegotiation.BUDGET_INCREASE) instanceof WorkBudgetNegotiation);
		try {
			factory.newInstance("glenn");
			fail();
		} catch (InstantiationException e) {
		}
	}

	@Test
	public void testNewInstanceByDTO() throws Exception {
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setAdditionalExpenses(3D);
		dto.setPricingStrategyId(flatId);
		AbstractWorkPriceNegotiation negotiation = factory.newInstance(dto);
		assertTrue(negotiation instanceof WorkExpenseNegotiation);

		dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setMaxNumberOfHours(5D);
		dto.setPricingStrategyId(perHourId);
		dto.setBudgetIncrease(true);
		negotiation = factory.newInstance(dto);
		assertTrue(negotiation instanceof WorkBudgetNegotiation);

		dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(false);
		try {
			factory.newInstance(dto);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testNewInstanceByDTOAndStrategy() throws Exception {

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setAdditionalExpenses(3D);
		dto.setPricingStrategyId(flatId);
		AbstractWorkPriceNegotiation negotiation = factory.newInstance(dto, flatBudget);
		assertTrue(negotiation instanceof WorkExpenseNegotiation);
		assertEquals(BigDecimal.valueOf(3D), negotiation.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());

		dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setMaxNumberOfHours(9D);
		dto.setPricingStrategyId(perHourId);
		dto.setBudgetIncrease(true);
		negotiation = factory.newInstance(dto, reimbursement);

		assertTrue(negotiation instanceof WorkBudgetNegotiation);
		assertEquals(BigDecimal.valueOf(9D), negotiation.getPricingStrategy().getFullPricingStrategy().getMaxNumberOfHours());
		assertEquals(BigDecimal.valueOf(6D), negotiation.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());

		// add more expenses to existing
		dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setAdditionalExpenses(3D);
		dto.setPricingStrategyId(perHourId);
		negotiation = factory.newInstance(dto, reimbursement);

		assertTrue(negotiation instanceof WorkExpenseNegotiation);
		assertEquals(BigDecimal.valueOf(9D), negotiation.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());

		dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(false);
		try {
			factory.newInstance(dto, reimbursement);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}
	*/
}
