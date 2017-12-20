package com.workmarket.domains.payments.service;

import com.google.api.client.util.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AccountRegisterServicePrefundIT extends BaseServiceIT {

	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	@Autowired CompanyService companyService;
	
	private User contractor;
	private Company company;
	private AccountRegister accountRegister;
	
	@Before
	public void setUp() throws Exception {
		contractor = newContractorIndependentlane4Ready();
		company = contractor.getCompany();
		accountRegister = Lists.newArrayList(company.getAccountRegisters()).get(0);
	}

	@Test
	public void calcAvailableCash_NewResource_NoCash() throws Exception {
		BigDecimal availableCash = accountRegisterService.calcAvailableCash(contractor.getId());

		assertEquals(0, availableCash.intValue());
	}

	@Test
	public void calcAvailableCash_NewResourceWithCompletedWork_CashEqualToWorkPrice() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWorkWithAutoPay(employee.getId());
		
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		authenticationService.setCurrentUser(contractor);
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employee);
		workService.closeWork(work.getId());

		BigDecimal availableCash = accountRegisterService.calcAvailableCash(contractor.getId());

		assertEquals(
			work.getPricingStrategy().getFullPricingStrategy().getFlatPrice().setScale(2, RoundingMode.HALF_DOWN),
			availableCash.setScale(2, RoundingMode.HALF_DOWN)
		);
	}

	@Test
	public void calcPendingCommitments_NewResourceWithCompletedWorkOnTerms_CommitmentsEqualToInvoicedAmount() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		authenticationService.setCurrentUser(contractor);
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employee);
		workService.closeWork(work.getId());

		BigDecimal pendingCommitments = accountRegisterService.calcPendingCashByCompany(employee.getCompany().getId());

		assertEquals(DEFAULT_WORK_FLAT_PRICE, pendingCommitments.setScale(2, RoundingMode.HALF_DOWN).doubleValue(), 0);
	}

	@Test
	public void getPaymentSummation_companyExists_returnCorrectVal() throws Exception {
		assertEquals(accountRegister.getPaymentSummation().doubleValue(), accountRegisterService.getPaymentSummation(company.getId()).doubleValue(), 2);
	}

	@Test
	public void getPaymentSummation_companyDoesNotExist_returnZero() throws Exception {
		assertEquals(BigDecimal.ZERO, accountRegisterService.getPaymentSummation(0L));
	}

	@Test
	public void getCurrentWorkFeePercentage_companyExists_returnCorrectVal() throws Exception {
		assertEquals(
			accountRegister.getCurrentWorkFeePercentage().doubleValue(),
			accountRegisterService.getCurrentWorkFeePercentage(company.getId()).doubleValue(),
			2
		);
	}

	@Test
	public void getCurrentWorkFeePercentage_companyDoesNotExist_returnZero() throws Exception {
		assertEquals(BigDecimal.ZERO, accountRegisterService.getCurrentWorkFeePercentage(0L));
	}

	@Test
	public void getAccountsPayableBalance_companyExists_returnCorrectVal() throws Exception {
		assertEquals(
			accountRegister.getAccountRegisterSummaryFields().getAccountsPayableBalance().doubleValue(),
			accountRegisterService.getAccountsPayableBalance(company.getId()).doubleValue(),
			2
		);
	}

	@Test
	public void getAccountsPayableBalance_companyDoesNotExist_returnZero() throws Exception {
		assertEquals(BigDecimal.ZERO, accountRegisterService.getAccountsPayableBalance(0L));
	}

	@Test
	public void getAPLimit_companyExists_returnCorrectVal() throws Exception {
		assertEquals(accountRegister.getApLimit().doubleValue(), accountRegisterService.getAPLimit(company.getId()).doubleValue(), 2);
	}

	@Test
	public void getAPLimit_companyDoesNotExist_returnZero() throws Exception {
		assertEquals(BigDecimal.ZERO, accountRegisterService.getAPLimit(0L));
	}
}
