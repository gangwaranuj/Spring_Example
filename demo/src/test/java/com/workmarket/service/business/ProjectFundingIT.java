package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ProjectFundingIT extends BaseServiceIT {

	private static final String DEFAULT_CASH_AMOUNT = "2000.00";


	@Test
	public void transferFundsToProject_AmountLessThanGeneralCash_Success() throws Exception{
		User user = initUserForProjectTest();

		BigDecimal amount = BigDecimal.valueOf(1000.00);
		BigDecimal expectedAvailableCash = new BigDecimal(DEFAULT_CASH_AMOUNT);
		BigDecimal expectedGeneralCash = expectedAvailableCash.subtract(amount);
		BigDecimal expectedProjectCash = BigDecimal.ZERO.add(amount);

		Project project = projectService.findReservedFundsEnabledProjectsForCompany(user.getCompany().getId()).get(0);

		accountRegisterService.transferFundsToProject(project.getId(), user.getCompany().getId(), amount);

		AccountRegisterSummaryFields accountRegisterSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(user.getCompany().getId());
		BigDecimal actualAvailableCash = accountRegisterSummaryFields.getAvailableCash();
		BigDecimal actualGeneralCash = accountRegisterSummaryFields.getGeneralCash();
		BigDecimal actualProjectCash = accountRegisterSummaryFields.getProjectCash();

		Assert.assertEquals(expectedAvailableCash.compareTo(actualAvailableCash), 0);
		Assert.assertEquals(expectedGeneralCash.compareTo(actualGeneralCash), 0);
		Assert.assertEquals(expectedProjectCash.compareTo(actualProjectCash), 0);
	}

	@Test
	public void transferFundsToProject_AmountMoreThanGeneralCash_ThrowsException() throws Exception{
		User user = initUserForProjectTest();

		BigDecimal amount = BigDecimal.valueOf(3000.00);
		BigDecimal expectedAvailableCash = new BigDecimal(DEFAULT_CASH_AMOUNT);
		BigDecimal expectedGeneralCash = new BigDecimal(DEFAULT_CASH_AMOUNT);
		BigDecimal expectedProjectCash = BigDecimal.ZERO;

		Project project = projectService.findReservedFundsEnabledProjectsForCompany(user.getCompany().getId()).get(0);

		try {
			accountRegisterService.transferFundsToProject(project.getId(), user.getCompany().getId(), amount);
		} catch (InsufficientFundsException e) {
			AccountRegisterSummaryFields accountRegisterSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(user.getCompany().getId());
			BigDecimal actualAvailableCash = accountRegisterSummaryFields.getAvailableCash();
			BigDecimal actualGeneralCash = accountRegisterSummaryFields.getGeneralCash();
			BigDecimal actualProjectCash = accountRegisterSummaryFields.getProjectCash();
			Assert.assertEquals(expectedAvailableCash.compareTo(actualAvailableCash), 0);
			Assert.assertEquals(expectedGeneralCash.compareTo(actualGeneralCash), 0);
			Assert.assertEquals(expectedProjectCash.compareTo(actualProjectCash), 0);
		}

	}

	private User initUserForProjectTest() throws Exception {
		User user = newFirstEmployeeWithNOCashBalanceAndPaymentTerms();
		accountRegisterService.addFundsToRegisterFromWire(user.getCompany().getId(), DEFAULT_CASH_AMOUNT);
		ClientCompany clientCompany = newClientCompany(user.getId());
		Project projectWithReserveFundsEnabled = newProject(user.getId(), clientCompany.getId(), "Project Title");

		// Create a project with reserve funds enabled
		projectWithReserveFundsEnabled.setName("Project With Reserved Funds Enabled");
		projectWithReserveFundsEnabled.setReservedFundsEnabled(true);
		projectService.saveOrUpdate(projectWithReserveFundsEnabled);

		// Create a project without reserve funds enabled
		Project projectWithoutReservedFundsEnabled = newProject(user.getId(), clientCompany.getId(), "Project Title");
		projectWithoutReservedFundsEnabled.setName("Project Without Budget Enabled");
		projectService.saveOrUpdate(projectWithoutReservedFundsEnabled);

		return user;
	}





}
