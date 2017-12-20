
package com.workmarket.service.business.subscriptions;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.scheduler.subscription.SubscriptionMonthlyInvoiceExecutor;
import com.workmarket.service.business.scheduler.subscription.SubscriptionTransactionExecutor;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class IncrementalSubscriptionServiceIT extends SubscriptionServiceBaseIT {

	private User employee;
	private User contractor;
	private SubscriptionConfigurationDTO subscriptionDTO;

	@Autowired private SubscriptionService subscriptionService;
	@Autowired private BillingService billingService;
	@Autowired private PricingService pricingService;
	@Autowired private WorkService workService;
	@Autowired private SubscriptionTransactionExecutor subscriptionTransactionExecutor;
	@Autowired private SubscriptionMonthlyInvoiceExecutor subscriptionMonthlyInvoiceExecutor;

	@Override
	@Before
	public void before() throws Exception {
		User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.setCurrentUser(currentUser);

		employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		List<SubscriptionPaymentTierDTO> paymentTiers = Lists.newArrayList();
		SubscriptionPaymentTierDTO subscriptionPaymentTierDTO = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTO.setMinimum(BigDecimal.ZERO);
		subscriptionPaymentTierDTO.setMaximum(BigDecimal.valueOf(50.00));
		subscriptionPaymentTierDTO.setPaymentAmount(BigDecimal.valueOf(1000.00));
		subscriptionPaymentTierDTO.setVendorOfRecordAmount(BigDecimal.valueOf(20.00));
		paymentTiers.add(subscriptionPaymentTierDTO);

		SubscriptionPaymentTierDTO subscriptionPaymentTierDTOSecond = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTOSecond.setMinimum(BigDecimal.valueOf(50.00));
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
	@Ignore
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

		Assert.assertFalse(subscriptionService.findAllSubmittedSubscriptionThroughputIncrementTxs().isEmpty());

		//TODO: allow to change the transaction date, since this will fail to find a current payment period
		//subscriptionTransactionExecutor.execute();
	}

	@Test
	public void testGenerateIncrementalTransactionsWithFutureInvoices() throws Exception {
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

		Calendar updateTime = DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), TimeZone.getDefault());
		subscriptionService.updateSubscriptionConfigurationChanges(activeConfiguration.getId(), updateTime);
		subscriptionService.updateApprovedSubscriptionsPricingType(updateTime);

		//Generate future invoices
		SubscriptionInvoice invoice = subscriptionService.issueFutureSubscriptionInvoice(paymentPeriod.getId());
		Assert.assertNotNull(invoice);
		assertEquals(invoice.getPaymentPeriod().getId(), paymentPeriod.getId());
		assertEquals(invoice.getBalance().intValue(), 1020);
		assertTrue(invoice.isPaymentPending());
		assertFalse(invoice.isFulFilled());
		assertTrue(invoice.getInvoiceLineItems().size() == 2);

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

		List<SubscriptionThroughputIncrementTransaction> txs = subscriptionService.findAllSubmittedSubscriptionThroughputIncrementTxs();
		Assert.assertFalse(txs.isEmpty());
		for (SubscriptionThroughputIncrementTransaction transaction: txs) {
			assertNotNull(transaction.getSubscriptionPaymentTier());
			assertNotNull(transaction.getTriggeredByRegisterTransaction());
		}
	}

	@Test
	@Ignore
	public void testExecuteGeneratedIncrementalTransactions() throws Exception {
		/**
		 * Magic required ---- Before running this we need to update the transaction date on the register_transaction to something after
		 * DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), TimeZone.getDefault());
		 */

		/**
		 * Select * from work_resource_transaction t
		 inner join register_transaction r on t.id = r.id
		 inner join subscription_throughput_increment_transaction on subscription_throughput_increment_transaction.triggered_by_register_transaction_id = r.id
		 */

		subscriptionTransactionExecutor.execute();

		Map<Long, List<Long>> transactions = accountRegisterService.findAllSubscriptionTransactionPendingInvoice();
		assertFalse(transactions.isEmpty());

		for (Map.Entry<Long, List<Long>> entry : transactions.entrySet()) {
			SubscriptionInvoice invoice = subscriptionService.issueIncrementalSubscriptionInvoice(entry.getValue());
			assertNotNull(invoice);
			assertNotNull(invoice.getPaymentPeriod());
			assertTrue(invoice.getSubscriptionInvoiceType().getCode().equals(SubscriptionInvoiceType.INCREMENTAL) || invoice.getSubscriptionInvoiceType().getCode().equals(SubscriptionInvoiceType.INCREMENTAL_FUTURE));
			assertTrue(invoice.getBalance().intValue() > 0);
			assertFalse(invoice.getInvoiceLineItems().isEmpty());

			User user = userService.findAllActiveAdminUsers(invoice.getCompany().getId(), new UserPagination()).getResults().get(0);
			billingService.payInvoice(user.getId(), invoice.getId());

			invoice = billingService.findInvoiceById(invoice.getId());
			assertNotNull(invoice);
			assertTrue(invoice.isPaid());
			assertTrue(invoice.getRemainingBalance().intValue() == 0);
		}
		//subscriptionMonthlyInvoiceExecutor.execute();

	}
}

