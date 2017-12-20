package com.workmarket.service.business;

import static com.workmarket.testutils.matchers.CommonMatchers.at10amGMTorUTC;
import static com.workmarket.testutils.matchers.CommonMatchers.weekAfter;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.account.payment.AccountingProcessTime;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentCycle;
import com.workmarket.domains.payments.dao.PaymentConfigurationDAO;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.payments.service.BillingServiceImpl;
import com.workmarket.service.business.dto.PaymentConfigurationDTO;
import com.workmarket.service.infra.business.AuthenticationService;

@RunWith(Parameterized.class)
public class BillingServiceWeeklyConfigurationTest {
	private int preferredDayOfTheWeek;

	@InjectMocks
	private BillingService billingService = new BillingServiceImpl();
	private PaymentConfiguration paymentConfiguration;

	private Company company;
	private ManageMyWorkMarket manageMyWorkMarket;

	@Mock private CompanyService companyService;
	@Mock private PaymentConfigurationDAO paymentConfigurationDAO;
	@Mock private AuthenticationService authenticationService;

	@Before
	public void setUp() {
		initMocks(this);

		company = mock(Company.class);
		manageMyWorkMarket = mock(ManageMyWorkMarket.class);
		paymentConfiguration  = new PaymentConfiguration();

		when(companyService.findCompanyById(anyLong())).thenReturn(company);
		when(company.getPaymentConfiguration()).thenReturn(paymentConfiguration);
		when(company.getManageMyWorkMarket()).thenReturn(manageMyWorkMarket);
	}

	public BillingServiceWeeklyConfigurationTest(int preferredDayOfTheWeek) {
		this.preferredDayOfTheWeek = preferredDayOfTheWeek;
	}

	@Parameters
	public static Collection<Object[]> getListOfWeekDays() {
		return Arrays.asList(new Object[][] {
			{ Calendar.SUNDAY },
			{ Calendar.MONDAY },
			{ Calendar.TUESDAY },
			{ Calendar.WEDNESDAY },
			{ Calendar.THURSDAY },
			{ Calendar.FRIDAY },
			{ Calendar.SATURDAY }
		});
	}

	/* Test here that the next statement day is correct day of the week. It should be offset by one as encoded in the above function.
	 * This is because our front end saves days with monday indexed at "1", whereas JAVA Calendar object indexes it at 2.
	 */
	@Test
	public void testWeeklyPaymentConfiguration_NextStatementDate() throws Exception {
		PaymentConfigurationDTO dto = new PaymentConfigurationDTO();

		dto.setAccountingProcessDays(AccountingProcessTime.FIVE_DAYS.getPaymentDays());
		dto.setAchPaymentMethodEnabled(true);
		dto.setPaymentCycleDays(PaymentCycle.WEEKLY.getPaymentDays());
		dto.setPreferredDayOfWeek(preferredDayOfTheWeek);
		dto.setBiweeklyPaymentOnSpecificDayOfMonth(false);
		dto.setPreferredDayOfMonthBiweeklyFirstPayment(BaseServiceIT.THREE);

		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);

		assertThat(config.getNextStatementDate(), oneDayAfter(preferredDayOfTheWeek));
		assertThat(config.getNextStatementDate(), weekAfter(config.getStartDatePaymentCycle()));
		assertThat(config.getStartDatePaymentCycle(), at10amGMTorUTC());
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
	}
	
	private Matcher<Calendar> oneDayAfter(final int day_of_week) {
		return new BaseMatcher<Calendar>() {
			@Override
			public boolean matches(final Object item) {
				final Calendar actual = (Calendar) item;

				return (actual.get(Calendar.DAY_OF_WEEK) ==  day_of_week % 7 + 1);

			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("the given date fall on previous day as the input preferred day of week");
			}
		};
	}
}
