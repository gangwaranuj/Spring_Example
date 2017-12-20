package com.workmarket.service.business;

import static com.workmarket.testutils.matchers.CommonMatchers.at10amGMTorUTC;
import static com.workmarket.testutils.matchers.CommonMatchers.dayAfter;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

@RunWith(MockitoJUnitRunner.class)
public class BillingServiceGenerateDailyStatementTest {
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
		dto.setPaymentCycleDays(PaymentCycle.DAILY.getPaymentDays());
		dto.setPreferredDayOfWeek(Calendar.WEDNESDAY);
		dto.setBiweeklyPaymentOnSpecificDayOfMonth(false);
		dto.setPreferredDayOfMonthBiweeklyFirstPayment(BaseServiceIT.THREE);

	}
	
	@Test
	public void generateStatement_firstTime_nothingToDo() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);
		
		Calendar todat10amGMT = DateUtilities.getMidnightTodayRelativeToTimezone(TimeZone.getTimeZone("GMT"));
		todat10amGMT.set(Calendar.HOUR_OF_DAY, 10);
		
		assertThat(config.getStartDatePaymentCycle(), dayAfter(todat10amGMT));
		
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
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -21);
		config.getStartDatePaymentCycle().add(Calendar.HOUR_OF_DAY, -3);
		
		Statement statement = billingService.generateStatement(1L);
		
		Assert.assertNotNull(statement);
		Assert.assertEquals(config.getStartDatePaymentCycle(), statement.getPeriodStartDate());
		
		// check that statement dates are at 10 am GMT time and that they are week apart
		assertThat(statement.getPeriodEndDate(), at10amGMTorUTC());
		assertThat(statement.getPeriodEndDate(), dayAfter(statement.getPeriodStartDate()));

		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), dayAfter(statement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
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
		assertThat(statement.getPeriodEndDate(), dayAfter(statement.getPeriodStartDate()));
		
		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), dayAfter(statement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
	}
	
	@Test
	public void generateStatement_secondTime_success() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);
		
		// shift the statement generation back by 3 weeks
		config.getStartDatePaymentCycle().add(Calendar.DAY_OF_YEAR, -21);
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -21);
		
		Statement firstStatement = billingService.generateStatement(1L);
		Statement secondStatement = billingService.generateStatement(1L);
		
		Assert.assertNotNull(secondStatement);
		Assert.assertNotEquals(config.getStartDatePaymentCycle(), secondStatement.getPeriodStartDate());
		Assert.assertEquals(firstStatement.getPeriodEndDate(), secondStatement.getPeriodStartDate());
		
		// check that statement dates are at 10 am GMT time and that they are week apart
		assertThat(secondStatement.getPeriodStartDate(), at10amGMTorUTC());
		assertThat(secondStatement.getPeriodEndDate(), at10amGMTorUTC());
		assertThat(secondStatement.getPeriodEndDate(), dayAfter(secondStatement.getPeriodStartDate()));
		
		// check that the next statement will be generated week later.
		assertThat(config.getNextStatementDate(), dayAfter(secondStatement.getPeriodEndDate()));
		assertThat(config.getNextStatementDate(), at10amGMTorUTC());
	}
}
