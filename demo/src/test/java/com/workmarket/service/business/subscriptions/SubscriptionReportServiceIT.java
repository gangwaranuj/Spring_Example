package com.workmarket.service.business.subscriptions;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportPagination;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportRow;
import com.workmarket.service.business.account.SubscriptionReportService;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class SubscriptionReportServiceIT extends SubscriptionServiceBaseIT {

	private SubscriptionConfiguration subscriptionConfiguration;

	@Autowired private SubscriptionReportService subscriptionReportService;

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
		paymentTiers.add(subscriptionPaymentTierDTO);

		SubscriptionPaymentTierDTO subscriptionPaymentTierDTOSecond = new SubscriptionPaymentTierDTO();
		subscriptionPaymentTierDTOSecond.setMinimum(BigDecimal.valueOf(50.00));
		subscriptionPaymentTierDTOSecond.setPaymentAmount(BigDecimal.valueOf(1500.00));
		paymentTiers.add(subscriptionPaymentTierDTOSecond);

		subscriptionDTO = new SubscriptionConfigurationDTO();
		subscriptionDTO.setSubscriptionPaymentTierDTOs(paymentTiers);
		subscriptionDTO.setEffectiveDate(DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), TimeZone.getDefault()));
		subscriptionDTO.setNumberOfPeriods(5);
		subscriptionDTO.setSetUpFee(BigDecimal.TEN);
		subscriptionDTO.setSubscriptionPeriod(SubscriptionPeriod.QUARTERLY);
		subscriptionDTO.setSubscriptionTypeCode(SubscriptionType.BAND);

		subscriptionConfiguration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		subscriptionService.approveSubscriptionConfiguration(subscriptionConfiguration.getId());
	}

	@Test
	public void testGetStandardReport() throws Exception {
		SubscriptionReportPagination pagination = subscriptionReportService.getStandardReport(new SubscriptionReportPagination());
		Assert.assertNotNull(pagination);
		Assert.assertFalse(pagination.getResults().isEmpty());
		for(SubscriptionReportRow row: pagination.getResults()) {
			if (row.getSubscriptionConfigurationId() == (subscriptionConfiguration.getId().longValue())) {
				assertEquals(row.getTermsInMonths(), 15);
				assertEquals(row.getPaymentPeriod(), SubscriptionPeriod.QUARTERLY.toString());
				//assertEquals(row.getEffectiveDate(), DateUtilities.getCalendarWithFirstDayOfTheMonth(DateUtilities.getMidnightNextMonth(), TimeZone.getDefault()));
				assertEquals(row.getNextTierPaymentAmount().intValue(), 1500);
				assertEquals(row.getNumberOfRenewals(), 0);
				assertEquals(row.getCurrentAnnualThroughput().intValue(), 0);
				assertEquals(row.getCurrentTierLowerBoundThroughput().intValue(), 0);
				assertEquals(row.getCurrentTierPaymentAmount().intValue(), 1000);
				assertEquals(row.getCurrentTierUpperBoundThroughput().intValue(), 50);
				assertEquals(row.getMonthlyRecurringRevenue().intValue(), 333);
				assertEquals(row.getTermsUsed().intValue(), 0);
				assertEquals(row.getTierThroughputUsage().intValue(), 0);
				assertNull(row.getRenewalDate());
				assertEquals(row.getAnnualRecurringRevenue().intValue(), row.getMonthlyRecurringRevenue().multiply(BigDecimal.valueOf(12)).intValue());
			}
		}
	}
}
