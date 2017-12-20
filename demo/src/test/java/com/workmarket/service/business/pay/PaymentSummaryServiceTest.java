package com.workmarket.service.business.pay;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.domains.work.service.WorkMilestonesService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentSummaryServiceTest {

	@Mock private TaxService taxService;
	@Mock private PricingService pricingService;
	@Mock private WorkService workService;
	@Mock private WorkNegotiationService workNegotiationService;
	@Mock private WorkMilestonesService workMilestonesService;
	@InjectMocks PaymentSummaryServiceImpl paymentSummaryService;

	private Work work;
	private Company company;
	private AccountRegister accountRegister;
	private PricingStrategy pricingStrategy;
	private FullPricingStrategy fullPricingStrategy;
	private WorkResource workResource;
	private WorkMilestones workMilestones;
	private FulfillmentStrategy fulfillmentStrategy;
	private WorkNegotiation negotiation;

	@Before
	public void setup() {
		work = mock(Work.class);
		company = mock(Company.class);
		accountRegister = mock(AccountRegister.class);
		pricingStrategy = mock(PricingStrategy.class);
		workResource = mock(WorkResource.class);
		workMilestones = mock(WorkMilestones.class);
		fullPricingStrategy = mock(FullPricingStrategy.class);
		fulfillmentStrategy = mock(FulfillmentStrategy.class);
		negotiation = mock(WorkNegotiation.class);
		when(negotiation.getWork()).thenReturn(work);
		when(negotiation.getPricingStrategy()).thenReturn(pricingStrategy);
		when(work.getId()).thenReturn(1L);
		when(workService.findWork(anyLong(), anyBoolean())).thenReturn(work);
		when(pricingService.findDefaultRegisterForCompany(anyLong())).thenReturn(accountRegister);
		when(pricingService.calculateMaximumResourceCost(any(PricingStrategy.class))).thenReturn(BigDecimal.TEN);
		when(pricingService.calculateBuyerNetMoneyFee(eq(work), any(BigDecimal.class))).thenReturn(BigDecimal.ONE);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(null);
		when(work.getCompany()).thenReturn(company);
		when(work.getPricingStrategy()).thenReturn(pricingStrategy);
		when(workMilestones.getPaidOn()).thenReturn(Calendar.getInstance());
		when(workMilestonesService.findWorkMilestonesByWorkId(anyLong())).thenReturn(workMilestones);
		when(pricingStrategy.getFullPricingStrategy()).thenReturn(fullPricingStrategy);
		when(fullPricingStrategy.getSalesTaxCollectedFlag()).thenReturn(true);
		when(company.getId()).thenReturn(1L);
		when(work.getDueOn()).thenReturn(Calendar.getInstance());
		when(fulfillmentStrategy.getWorkPrice()).thenReturn(BigDecimal.valueOf(50));
		when(fulfillmentStrategy.getBuyerFee()).thenReturn(BigDecimal.valueOf(3));
		when(work.getClosedOn()).thenReturn(Calendar.getInstance());
		when(workNegotiationService.findById(anyLong())).thenReturn(negotiation);
	}

	@Test
	public void generatePaymentSummaryForWork_withNoWorkResource() throws Exception {
	 	PaymentSummaryDTO paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(1L);
		assertNotNull(paymentSummaryDTO);
		Assert.assertEquals(BigDecimal.valueOf(11), paymentSummaryDTO.getTotalCost());
	}

	@Test
	public void generatePaymentSummaryForWork_withActiveWorkResource() throws Exception {
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		PaymentSummaryDTO paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(1L);
		assertNotNull(paymentSummaryDTO);
		Assert.assertEquals(BigDecimal.valueOf(11), paymentSummaryDTO.getTotalCost());
	}

	@Test
	public void generatePaymentSummaryForWork_withPaidWork() throws Exception {
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(work.isPaid()).thenReturn(true);
		PaymentSummaryDTO paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(1L);
		assertNotNull(paymentSummaryDTO);
		assertNotNull(paymentSummaryDTO.getPaidOn());
		Assert.assertEquals(BigDecimal.valueOf(11), paymentSummaryDTO.getTotalCost());
	}

	@Test
	public void generatePaymentSummaryForWork_withPaidWorkAndFullfillmentStrategy() throws Exception {
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(work.getFulfillmentStrategy()).thenReturn(fulfillmentStrategy);
		when(work.isPaid()).thenReturn(true);
		PaymentSummaryDTO paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(1L);
		assertNotNull(paymentSummaryDTO);
		assertNotNull(paymentSummaryDTO.getPaidOn());
		Assert.assertEquals(BigDecimal.valueOf(53), paymentSummaryDTO.getTotalCost());
	}

	@Test
	public void generatePaymentSummaryForWork_withPaymentPendingWork() throws Exception {
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(work.isPaymentPending()).thenReturn(true);
		PaymentSummaryDTO paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(1L);
		assertNotNull(paymentSummaryDTO);
		assertNotNull(paymentSummaryDTO.getPaymentDueOn());
		assertNull(paymentSummaryDTO.getPaidOn());
		Assert.assertEquals(BigDecimal.valueOf(11), paymentSummaryDTO.getTotalCost());
	}

	@Test
	public void generatePaymentSummaryForWork_withCompleteWork() throws Exception {
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(work.isComplete()).thenReturn(true);
		when(pricingService.calculateTotalResourceCost(eq(work), eq(workResource))).thenReturn(BigDecimal.valueOf(20));
		PaymentSummaryDTO paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForWork(1L);
		assertNotNull(paymentSummaryDTO);
		assertNull(paymentSummaryDTO.getPaymentDueOn());
		assertNull(paymentSummaryDTO.getPaidOn());
		Assert.assertEquals(BigDecimal.valueOf(21), paymentSummaryDTO.getTotalCost());
		Assert.assertEquals(BigDecimal.valueOf(20), paymentSummaryDTO.getActualSpendLimit());
	}

	@Test
	public void generatePaymentSummaryForNegotiation() throws Exception {
		PaymentSummaryDTO paymentSummaryDTO = paymentSummaryService.generatePaymentSummaryForNegotiation(1L);
		assertNotNull(paymentSummaryDTO);
		Assert.assertEquals(BigDecimal.valueOf(11), paymentSummaryDTO.getTotalCost());
	}

}
