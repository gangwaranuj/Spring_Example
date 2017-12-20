
package com.workmarket.service.business.subscriptions;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.PricingService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.scheduler.subscription.SubscriptionTransactionExecutor;
import com.workmarket.configuration.Constants;
import com.workmarket.test.BrokenTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class ThroughputSubscriptionServiceIT extends SubscriptionServiceBaseIT {

	private User employee;
	private User contractor;
	private SubscriptionConfigurationDTO subscriptionDTO;

	@Autowired private SubscriptionService subscriptionService;
	@Autowired private BillingService billingService;
	@Autowired private PricingService pricingService;
	@Autowired private WorkService workService;
	@Autowired private SubscriptionTransactionExecutor subscriptionTransactionExecutor;

	@Override
	@Before
	public void before() throws Exception {
		employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		List<SubscriptionPaymentTierDTO> paymentTiers = Lists.newArrayList();
		SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
		subscriptionPaymentTierDTO.setMaximum(BigDecimal.valueOf(200.00));
		subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.valueOf(1000.00));
		subscriptionPaymentTierDTO.setVendorOfRecordAmount(BigDecimal.valueOf(20.00));
		paymentTiers.add(subscriptionPaymentTierDTO);

		SubscriptionPaymentTierDTO subscriptionPaymentTierDTOSecond = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTOSecond.setMinimum(BigDecimal.valueOf(200.00));
		subscriptionPaymentTierDTOSecond.setPaymentAmount(BigDecimal.valueOf(1500.00));
		subscriptionPaymentTierDTOSecond.setVendorOfRecordAmount(BigDecimal.valueOf(30.00));
		paymentTiers.add(subscriptionPaymentTierDTOSecond);

		AccountServiceTypeDTO canadaDTO = new AccountServiceTypeDTO();
		canadaDTO.setAccountServiceTypeCode(AccountServiceType.VENDOR_OF_RECORD);
		canadaDTO.setCountryCode(Country.CANADA);

		AccountServiceTypeDTO usaDTO = new AccountServiceTypeDTO();
		usaDTO.setAccountServiceTypeCode(AccountServiceType.VENDOR_OF_RECORD);
		usaDTO.setCountryCode(Country.USA);

		subscriptionDTO = new SubscriptionConfigurationDTO();
		subscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTiers);
		subscriptionDTO.setEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), Constants.EST_TIME_ZONE));
		subscriptionDTO.setNumberOfPeriods(5);
		subscriptionDTO.setSetUpFee(BigDecimal.TEN);
		subscriptionDTO.setSubscriptionPeriod(SubscriptionPeriod.QUARTERLY);
		subscriptionDTO.addToAccountServiceTypeDTO(canadaDTO);
		subscriptionDTO.addToAccountServiceTypeDTO(usaDTO);
		subscriptionDTO.setSubscriptionTypeCode(SubscriptionType.BAND);
	}

	@Test
	public void testGenerateIncrementalTransactions() throws Exception {
		Assert.assertNull(subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId()));

		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isPendingApproval());
		Assert.assertTrue(configuration.isPending());

		configuration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());
		Assert.assertNotNull(configuration);
		Assert.assertTrue(configuration.isApproved());
		Assert.assertTrue(configuration.isActive());

		SubscriptionConfiguration activeConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(employee.getCompany().getId());
		Assert.assertNotNull(activeConfiguration);
		Assert.assertTrue(activeConfiguration.isApproved());
		Assert.assertTrue(activeConfiguration.isActive());
		Assert.assertEquals(configuration.getId(), activeConfiguration.getId());

		SubscriptionPaymentPeriod paymentPeriod = subscriptionService.findNextNotInvoicedSubscriptionPaymentPeriod(configuration.getId());
		Assert.assertNotNull(paymentPeriod);
		Assert.assertEquals(subscriptionService.findNextPossibleSubscriptionUpdateDate(employee.getCompany().getId()), paymentPeriod.getPeriodDateRange().getFrom());

		subscriptionService.updateSubscriptionConfigurationChanges(activeConfiguration.getId(), activeConfiguration.getEffectiveDate());
		subscriptionService.updateApprovedSubscriptionsPricingType(activeConfiguration.getEffectiveDate());
		//Create an assignment
		Work work = newWork(employee.getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		BigDecimal resourceCost = pricingService.calculateMaximumResourceCost(work);
		BigDecimal fee = pricingService.calculateBuyerNetMoneyFee(work, resourceCost);
		Assert.assertNotNull(fee);
		Assert.assertTrue(fee.compareTo(BigDecimal.ZERO) == 0);

		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		authenticationService.setCurrentUser(contractor.getId());
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		authenticationService.setCurrentUser(employee.getId());
		workService.closeWork(work.getId());

	}
}
