package com.workmarket.service.business.scheduler;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
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
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.PaymentConfigurationDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.workmarket.testutils.matchers.CommonMatchers.isInAscendingOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountingProcessorMultipleStatementTest {
	@InjectMocks private AccountingProcessor accountingProcessor;
	@InjectMocks @Spy private BillingService billingService = new BillingServiceImpl();
	
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
	@Mock UserNotificationService userNotificationService;

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
		
		when(paymentConfigurationDAO.findAllCompanyAccountsByNextStatementDate(
				any(Calendar.class)))
		.thenReturn(Lists.newArrayList(company));
		
		doAnswer(new Answer<Statement>() {

			@Override
			public Statement answer(InvocationOnMock invocation) throws Throwable {
				Statement s = (Statement) invocation.getArguments()[0];
				s.setId(2L);
				
				return s;
			}
			
		}).when(statementDAO).saveOrUpdate(any(Statement.class));
	}
	
	@Test
	public void generateStatements_oneCompanyMultipe_success() {
		PaymentConfiguration config = billingService.saveStatementPaymentConfigurationForCompany(1, dto);
		
		// shift the statement generation back by 3 weeks
		config.getStartDatePaymentCycle().add(Calendar.DAY_OF_YEAR, -21);
		config.getNextStatementDate().add(Calendar.DAY_OF_YEAR, -21);
		config.getStartDatePaymentCycle().add(Calendar.HOUR_OF_DAY, -3);
		
		accountingProcessor.generateStatements();
		
		verify(authenticationService, times(1)).setCurrentUser(eq(Constants.WORKMARKET_SYSTEM_USER_ID));
		
		ArgumentCaptor<Statement> statementCaptor = ArgumentCaptor.forClass(Statement.class);
		verify(statementDAO, atLeastOnce()).saveOrUpdate(statementCaptor.capture());
		
		List<Statement> capturedStatements = statementCaptor.getAllValues();
		
		@SuppressWarnings("unchecked")
		List<Calendar> periodEndDateList = (List<Calendar>) CollectionUtils.collect(capturedStatements,
				new BeanToPropertyValueTransformer("periodEndDate"));

		assertThat(periodEndDateList, isInAscendingOrder());
		
		for (int i = 0; i < periodEndDateList.size() - 1; i++) {
			assertEquals(periodEndDateList.get(i), capturedStatements.get(i + 1).getPeriodStartDate());
		}
	}
}
