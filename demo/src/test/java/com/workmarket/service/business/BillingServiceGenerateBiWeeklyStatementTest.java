package com.workmarket.service.business;

import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.account.payment.AccountingProcessTime;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentCycle;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.payments.dao.AccountStatementDetailDAO;
import com.workmarket.domains.payments.dao.PaymentConfigurationDAO;
import com.workmarket.domains.payments.dao.StatementDAO;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.payments.service.BillingServiceImpl;
import com.workmarket.service.business.dto.PaymentConfigurationDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.joda.time.DateMidnight;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static com.workmarket.testutils.matchers.CommonMatchers.at10amGMTorUTC;
import static com.workmarket.testutils.matchers.CommonMatchers.dayOfWeek;
import static com.workmarket.testutils.matchers.CommonMatchers.twoWeeksAfter;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BillingServiceGenerateBiWeeklyStatementTest {
	@InjectMocks
	private BillingService billingService = new BillingServiceImpl();

	private PaymentConfiguration paymentConfiguration;
	private AccountStatementDetailPagination pagination;
	private PaymentConfigurationDTO dto;

	private Company company;
	private ManageMyWorkMarket manageMyWorkMarket;

	@Mock private CompanyService companyService;
	@Mock private PaymentConfigurationDAO paymentConfigurationDAO;
	@Mock private AuthenticationService authenticationService;
	@Mock private StatementDAO statementDAO;
	@Mock private AccountStatementDetailDAO accountStatementDetailDAO;

	@Before
	public void setUp() {
		company = mock(Company.class);
		manageMyWorkMarket = mock(ManageMyWorkMarket.class);
		paymentConfiguration  = new PaymentConfiguration();
		pagination = mock(AccountStatementDetailPagination.class);

		when(companyService.findCompanyById(anyLong())).thenReturn(company);
		when(companyService.getNextStatementNumber(anyLong())).thenReturn("statement 1");
		when(company.getPaymentConfiguration()).thenReturn(paymentConfiguration);
		when(company.getManageMyWorkMarket()).thenReturn(manageMyWorkMarket);
		when(accountStatementDetailDAO.findInvoices(anyLong(), anyLong(),
				any(AccountStatementDetailPagination.class), any(AccountStatementFilters.class), anyBoolean()))
		.thenReturn(pagination);
		when(pagination.getResults()).thenReturn(new ArrayList());

		dto = new PaymentConfigurationDTO();

		dto.setAccountingProcessDays(AccountingProcessTime.FIVE_DAYS.getPaymentDays());
		dto.setAchPaymentMethodEnabled(true);
		dto.setPaymentCycleDays(PaymentCycle.BIWEEKLY.getPaymentDays());
		dto.setPreferredDayOfWeek(3);
		dto.setBiweeklyPaymentOnSpecificDayOfMonth(false);
		dto.setPreferredDayOfMonthBiweeklyFirstPayment(BaseServiceIT.THREE);
	}

	/**
	 * We process statements at 6:30AM UTC. Here we test that only statements with 'next_statement_date' AFTER
	 * processing date will be skipped
	 */
	@Test
	public void testSkipStatementCondition(){

		DateMidnight midnightToday = new DateMidnight();

		Calendar dateProcessed = DateUtilities.getCalendarFromDateTimeString("2015-12-08 6:30:00", "UTC");

		Calendar dateBeforeProcessedDate = DateUtilities.getCalendarFromDateTimeString("2015-12-07 10:00:00", "UTC");
		Calendar dateAfterProcessedDate = DateUtilities.getCalendarFromDateTimeString("2015-12-09 10:00:00", "UTC");
		Calendar dateSameAsProcessedDate = DateUtilities.getCalendarFromDateTimeString("2015-12-08 10:00:00", "UTC");

		assertFalse(DateUtilities.getDaysBetween(dateBeforeProcessedDate, dateProcessed, false) < 0);
		assertFalse(DateUtilities.getDaysBetween(dateSameAsProcessedDate, dateProcessed, false) < 0);
		assertTrue(DateUtilities.getDaysBetween(dateAfterProcessedDate, dateProcessed, false) < 0);
	}

	@Test
	public void generateStatement_firstTime_nothingToDo() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);

		Calendar todat10amGMT = DateUtilities.getMidnightTodayRelativeToTimezone(TimeZone.getTimeZone("GMT"));
		todat10amGMT.set(Calendar.HOUR_OF_DAY, 10);

		assertThat(config.getStartDatePaymentCycle(), at10amGMTorUTC());
		assertThat(config.getStartDatePaymentCycle(), dayOfWeek(Calendar.WEDNESDAY));

		Statement statement = billingService.generateStatement(1L);

		Assert.assertNull(statement);
	}

	@Test
	public void generateStatement_duplicate_nothingToDo() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);

		// make it look like we just generated a statement
		config.setLastStatementSentOn(Calendar.getInstance());

		Statement secondStatement = billingService.generateStatement(1L);

		Assert.assertNull(secondStatement);
	}

	@Test
	public void generateStatement_wrongStartTime_success() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);

		// shift the statement generation back by 5 weeks
		config.getStartDatePaymentCycle().add(Calendar.DAY_OF_YEAR, -35);
		config.getStartDatePaymentCycle().add(Calendar.HOUR_OF_DAY, -3);
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -35);

		Statement statement = billingService.generateStatement(1L);

		Assert.assertNotNull(statement);
		Assert.assertEquals(config.getStartDatePaymentCycle(), statement.getPeriodStartDate());

		// check that statement dates are at 10 am GMT time and that they are week apart
		assertThat(statement.getPeriodEndDate(), at10amGMTorUTC());
		assertThat(statement.getPeriodStartDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(statement.getPeriodEndDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(statement.getPeriodEndDate(), twoWeeksAfter(statement.getPeriodStartDate()));

		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), twoWeeksAfter(statement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
		assertThat(config.getNextStatementDate(), dayOfWeek(Calendar.WEDNESDAY));
	}

	@Test
	public void generateStatement_firstTime_5weeksLater_success() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);

		// shift the statement generation back by 3 weeks
		config.getStartDatePaymentCycle().add(Calendar.DAY_OF_YEAR, -35);
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -35);

		Statement statement = billingService.generateStatement(1L);

		Assert.assertNotNull(statement);
		Assert.assertEquals(config.getStartDatePaymentCycle(), statement.getPeriodStartDate());

		// check that statement dates are at 10 am GMT time and that they are week apart
		assertThat(statement.getPeriodStartDate(), at10amGMTorUTC());
		assertThat(statement.getPeriodEndDate(), at10amGMTorUTC());
		assertThat(statement.getPeriodStartDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(statement.getPeriodEndDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(statement.getPeriodEndDate(), twoWeeksAfter(statement.getPeriodStartDate()));

		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), twoWeeksAfter(statement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
		assertThat(config.getNextStatementDate(), dayOfWeek(Calendar.WEDNESDAY));
	}

	@Test
	public void generateStatement_secondTime_success() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);

		// shift the statement generation back by 7 weeks
		config.getStartDatePaymentCycle().add(Calendar.DAY_OF_YEAR, -49);
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -49);

		Statement firstStatement = billingService.generateStatement(1L);
		Statement secondStatement = billingService.generateStatement(1L);

		Assert.assertNotNull(secondStatement);
		Assert.assertNotEquals(config.getStartDatePaymentCycle(), secondStatement.getPeriodStartDate());
		Assert.assertEquals(firstStatement.getPeriodEndDate(), secondStatement.getPeriodStartDate());

		// check that statement dates are at 10 am GMT time and that they are week apart
		assertThat(secondStatement.getPeriodStartDate(), at10amGMTorUTC());
		assertThat(secondStatement.getPeriodEndDate(), at10amGMTorUTC());
		assertThat(secondStatement.getPeriodStartDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(secondStatement.getPeriodEndDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(secondStatement.getPeriodEndDate(), twoWeeksAfter(secondStatement.getPeriodStartDate()));

		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), twoWeeksAfter(secondStatement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
		assertThat(config.getNextStatementDate(), dayOfWeek(Calendar.WEDNESDAY));
	}
}
