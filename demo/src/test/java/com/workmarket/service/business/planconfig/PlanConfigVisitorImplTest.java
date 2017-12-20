package com.workmarket.service.business.planconfig;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.WorkFeeBand;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.domains.model.planconfig.PlanConfigVisitor;
import com.workmarket.domains.model.planconfig.TransactionFeePlanConfig;
import com.workmarket.service.business.CompanyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;


/**
 * User: micah
 * Date: 9/2/14
 * Time: 10:29 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class PlanConfigVisitorImplTest {
	private static final BigDecimal TRANSACTION_FEE_PERCENTAGE = new BigDecimal(6);
	private static final Long COMPANY_ID = 111111L;

	TransactionFeePlanConfig transactionFeePlanConfig;
	WorkFeeBand workFeeBand;
	AccountRegister accountRegister;
	Company company;

	@Mock CompanyService companyService;
	@InjectMocks PlanConfigVisitor planConfigVisitor = new PlanConfigVisitorImpl();

	@Before
	public void setUp() throws Exception {
		transactionFeePlanConfig = mock(TransactionFeePlanConfig.class);
		when(transactionFeePlanConfig.getPercentage()).thenReturn(TRANSACTION_FEE_PERCENTAGE);

		workFeeBand = mock(WorkFeeBand.class);
		List<WorkFeeBand> workFeeBands = Lists.newArrayList(workFeeBand);

		WorkFeeConfiguration workFeeConfiguration = mock(WorkFeeConfiguration.class);
		when(workFeeConfiguration.getWorkFeeBands()).thenReturn(workFeeBands);
		List<WorkFeeConfiguration> workFeeConfigurations = Lists.newArrayList(workFeeConfiguration);

		accountRegister = mock(AccountRegister.class);
		when(accountRegister.getWorkFeeConfigurations()).thenReturn(workFeeConfigurations);
		Set<AccountRegister> accountRegisters = Sets.newHashSet(accountRegister);

		company = mock(Company.class);
		when(company.getAccountRegisters()).thenReturn(accountRegisters);
		when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company);
	}

	@Test
	public void visit_VerifySetPercentage() throws Exception {
		planConfigVisitor.visit(transactionFeePlanConfig, COMPANY_ID);

		verify(workFeeBand).setPercentage(TRANSACTION_FEE_PERCENTAGE);
	}

	@Test
	public void visit_VerifySetCurrentWorkFeePercentage() throws Exception {
		planConfigVisitor.visit(transactionFeePlanConfig, COMPANY_ID);

		verify(accountRegister).setCurrentWorkFeePercentage(TRANSACTION_FEE_PERCENTAGE);
	}


	@Test
	public void visit_CompanyNullNoSetPercentage() throws Exception {
		when(companyService.findCompanyById(COMPANY_ID)).thenReturn(null);
		planConfigVisitor.visit(transactionFeePlanConfig, COMPANY_ID);

		verify(workFeeBand, never()).setPercentage(any(BigDecimal.class));
	}

	@Test
	public void visit_CompanyNullNoSetCurrentWorkFeePercentage() throws Exception {
		when(companyService.findCompanyById(COMPANY_ID)).thenReturn(null);
		planConfigVisitor.visit(transactionFeePlanConfig, COMPANY_ID);

		verify(accountRegister, never()).setCurrentWorkFeePercentage(any(BigDecimal.class));
	}
}
