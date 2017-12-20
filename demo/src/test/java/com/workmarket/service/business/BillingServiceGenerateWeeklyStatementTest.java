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
import static com.workmarket.testutils.matchers.CommonMatchers.dayAfter;
import static com.workmarket.testutils.matchers.CommonMatchers.dayOfWeek;
import static com.workmarket.testutils.matchers.CommonMatchers.weekAfter;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BillingServiceGenerateWeeklyStatementTest {
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
		dto.setPaymentCycleDays(PaymentCycle.WEEKLY.getPaymentDays());
		dto.setPreferredDayOfWeek(3);
		dto.setBiweeklyPaymentOnSpecificDayOfMonth(false);
		dto.setPreferredDayOfMonthBiweeklyFirstPayment(BaseServiceIT.THREE);
	}
	
	@Test
	public void generateStatement_firstTime_nothingToDo() {
		billingService.saveStatementPaymentConfigurationForCompany(1, dto);
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
		
		// shift the statement generation back by 3 weeks
		config.getStartDatePaymentCycle().add(Calendar.DAY_OF_YEAR, -21);
		config.getStartDatePaymentCycle().add(Calendar.HOUR_OF_DAY, -3);
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -21);
		
		Statement statement = billingService.generateStatement(1L);
		
		Assert.assertNotNull(statement);
		Assert.assertEquals(config.getStartDatePaymentCycle(), statement.getPeriodStartDate());
		
		// check that statement dates are at 10 am GMT time and that they are week apart
		assertThat(statement.getPeriodEndDate(), at10amGMTorUTC());
		assertThat(statement.getPeriodStartDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(statement.getPeriodEndDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(statement.getPeriodEndDate(), weekAfter(statement.getPeriodStartDate()));

		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), weekAfter(statement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
		assertThat(config.getNextStatementDate(), dayOfWeek(Calendar.WEDNESDAY));
	}
	
	@Test
	public void generateStatement_firstTime_3weeksLater_success() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);
		
		// shift the statement generation back by 3 weeks
		config.getStartDatePaymentCycle().add(Calendar.DAY_OF_YEAR, -21);
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -21);
		
		Statement statement = billingService.generateStatement(1L);
		
		Assert.assertNotNull(statement);
		Assert.assertEquals(config.getStartDatePaymentCycle(), statement.getPeriodStartDate());
		
		// check that statement dates are at 10 am GMT time and that they are week apart
		assertThat(statement.getPeriodStartDate(), at10amGMTorUTC());
		assertThat(statement.getPeriodEndDate(), at10amGMTorUTC());
		assertThat(statement.getPeriodStartDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(statement.getPeriodEndDate(), dayOfWeek(Calendar.WEDNESDAY));
		assertThat(statement.getPeriodEndDate(), weekAfter(statement.getPeriodStartDate()));
		
		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), weekAfter(statement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
		assertThat(config.getNextStatementDate(), dayOfWeek(Calendar.WEDNESDAY));
	}
	
	@Test
	public void generateStatement_secondTime_success() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);
		
		// shift the statement generation back by 5 weeks
		config.getStartDatePaymentCycle().add(Calendar.DAY_OF_YEAR, -35);
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -35);
		
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
		assertThat(secondStatement.getPeriodEndDate(), weekAfter(secondStatement.getPeriodStartDate()));
		
		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), weekAfter(secondStatement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
		assertThat(config.getNextStatementDate(), dayOfWeek(Calendar.WEDNESDAY));
	}


	/**
	 * Create a PaymentConfiguration with lastStatementSentOn and nextStatementDate aligned with preferredDayOfWeek, assuming Sunday=1, Monday=2, Tuesday=3,...
	 * We change the way we assign lastStatementDate so the next statement will be assigned lastStatementDate based on Sunday=0, Monday=1, Tuesday=2,..
	 * This will create a shift in the Statements so that they will start/end on the next weekday.
	 * As a result we will create a 'mini' statement covering the gap.
	 */
	@Test
	public void generateThreeStatements_afterWeekdayShift() {

		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);

		Calendar fifteenDaysAgo = Calendar.getInstance();
		fifteenDaysAgo.add(Calendar.DAY_OF_YEAR, -15);
		fifteenDaysAgo.setTimeZone(TimeZone.getTimeZone("GMT"));
		fifteenDaysAgo.set(Calendar.HOUR_OF_DAY, 10);

		Calendar eightDaysAgo = Calendar.getInstance();
		eightDaysAgo.add(Calendar.DAY_OF_YEAR, -8);
		eightDaysAgo.setTimeZone(TimeZone.getTimeZone("GMT"));
		eightDaysAgo.set(Calendar.HOUR_OF_DAY, 10);

		config.setLastStatementSentOn(fifteenDaysAgo);
		config.setNextStatementDate(eightDaysAgo);
		config.setPreferredDayOfWeek(eightDaysAgo.get(Calendar.DAY_OF_WEEK));

		// first statement will be created from 12/2 - 12/9
		Statement firstStatement = billingService.generateStatement(1L);

		// second statement will be created from 12/9 - 12/10
		Statement secondStatement = billingService.generateStatement(1L);

		Assert.assertNotNull(firstStatement);
		Assert.assertNotNull(secondStatement);

		// check that second statement only spans 2 days
		assertThat(secondStatement.getPeriodEndDate(), dayAfter(secondStatement.getPeriodStartDate()));
	}

	/**
	 * We won't create a Statement if the current date is after the Statement end date
	 */
	@Test
	public void generateStatements_withStatementCycleEndingTomorrow() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);

		Calendar sixDaysAgo = Calendar.getInstance();
		sixDaysAgo.add(Calendar.DAY_OF_YEAR, -6);
		sixDaysAgo.setTimeZone(TimeZone.getTimeZone("GMT"));
		sixDaysAgo.set(Calendar.HOUR_OF_DAY, 10);

		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
		tomorrow.setTimeZone(TimeZone.getTimeZone("GMT"));
		tomorrow.set(Calendar.HOUR_OF_DAY, 10);

		config.setLastStatementSentOn(sixDaysAgo);
		config.setNextStatementDate(tomorrow);
		config.setPreferredDayOfWeek(tomorrow.get(Calendar.DAY_OF_WEEK));

		Statement statement = billingService.generateStatement(1L);

		// Third statement should not get created because it ends tomorrow.
		Assert.assertNull(statement);
	}
}
