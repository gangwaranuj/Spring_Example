package com.workmarket.service.business.scheduler.subscription;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriodType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.subscriptions.SubscriptionServiceBaseIT;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SubscriptionMonthlyInvoiceExecutorIT extends SubscriptionServiceBaseIT {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd Z");

	@Autowired SubscriptionMonthlyInvoiceExecutor executor;
	@Autowired SubscriptionService subscriptionService;
	@Autowired BillingService billingService;

	@Test
	public void testGenerateRegularSubscriptionInvoices() throws Exception {

		subscriptionDTO.setNumberOfPeriods(12);
		subscriptionDTO.setSubscriptionPeriod(SubscriptionPeriod.MONTHLY);
		subscriptionDTO.setClientRefId("What's up?");
		subscriptionDTO.setSubscriptionTypeCode(SubscriptionType.BAND);

		SubscriptionConfiguration configuration = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(employee.getCompany().getId(), subscriptionDTO, true);
		companyService.saveOrUpdateSubscriptionInvoicesEmailToCompany(employee.getCompany().getId(), Lists.newArrayList(new EmailAddressDTO("workmarket@workmarket.com")));
		SubscriptionConfiguration subscriptionConfiguration = subscriptionService.approveSubscriptionConfiguration(configuration.getId());

		assertEquals(subscriptionDTO.getSubscriptionTypeCode(), subscriptionConfiguration.getActiveSubscriptionFeeConfiguration().getSubscriptionType().getCode());

		// We want to test that past invoices will be generated so we set the 'current date' to three months after the subscription effective date
		Calendar threeMonthsAfterEffectiveDate = DateUtilities.getCalendarInUTC(DateUtilities.getCalendarNow());
		threeMonthsAfterEffectiveDate.add(Calendar.MONTH, 3);
		Calendar invoiceDate = DateUtilities.getCalendarWithFirstDayOfTheMonth(threeMonthsAfterEffectiveDate, TimeZone.getTimeZone("UTC"));

		executor.generateRegularSubscriptionInvoices(invoiceDate);

		Date firstOfNextMonth = getFirstDateOfNextMonthFromDate(threeMonthsAfterEffectiveDate).getTime();
		List<SubscriptionPaymentPeriod> paymentPeriods = subscriptionService.findAllSubscriptionPaymentPeriods(configuration.getId());
		for (SubscriptionPaymentPeriod paymentPeriod : paymentPeriods) {
			assertEquals(SubscriptionPeriodType.AUTO, paymentPeriod.getSubscriptionPeriodType().getCode());

			if (paymentPeriod.getPeriodDateRange().getFrom().getTime().before(firstOfNextMonth)) {
				SubscriptionInvoice subscriptionInvoice = billingService.findInvoiceById(paymentPeriod.getSubscriptionInvoice().getId());
				int daysUntilDue = DateUtilities.getDaysBetween(DateUtilities.getCalendarNow(), subscriptionInvoice.getDueDate()) + 1;
				assertTrue(paymentPeriod.getSubscriptionInvoice() != null);

				// Assert that invoice date is set from today using payment terms to determine due date
				assertTrue(String.format("Invoice due in %d days on %s (%d day terms)", daysUntilDue,
						dateFormat.format(subscriptionInvoice.getDueDate().getTime()), configuration.getPaymentTermsDays()),
						daysUntilDue >= configuration.getPaymentTermsDays());

				SubscriptionFeeConfiguration feeConfiguration = paymentPeriod.getSubscriptionConfiguration().getActiveSubscriptionFeeConfiguration();

				assertEquals(configuration.getId(), paymentPeriod.getSubscriptionConfiguration().getId());
				assertEquals(feeConfiguration.getId(), paymentPeriod.getSubscriptionFeeConfigurationId());
				assertEquals(feeConfiguration.getSubscriptionPaymentTiers().get(0).getId(), paymentPeriod.getSubscriptionPaymentTierSWId());
			}
		}
	}

	private Calendar getFirstDateOfNextMonthFromDate(Calendar currentDate) {
		Calendar firstDayOfNextMonth = (Calendar) currentDate.clone();
		firstDayOfNextMonth.add(Calendar.MONTH, 1);
		return DateUtilities.getCalendarWithFirstDayOfTheMonth(firstDayOfNextMonth, TimeZone.getTimeZone("UTC"));
	}
}
