package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.*;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

@RunWith(BlockJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class TransferFundsToProjectImplIT {

	private AccountRegisterSummaryFields accountRegisterSummaryFields;
	private RegisterTransaction registerTransaction = new RegisterTransaction();
	private TransferFundsToProjectCash transferFundsToProject = new TransferFundsToProjectCash();
	private AccountRegister accountRegister = new AccountRegister();

	@Before
	public void init() {
		accountRegisterSummaryFields = new AccountRegisterSummaryFields();
		accountRegisterSummaryFields.setGeneralCash(BigDecimal.valueOf(1000.00));
		accountRegisterSummaryFields.setProjectCash(BigDecimal.valueOf(0.00));
		accountRegister.setAccountRegisterSummaryFields(accountRegisterSummaryFields);
		registerTransaction.setAccountRegister(accountRegister);
		registerTransaction.setRegisterTransactionType( new RegisterTransactionType(RegisterTransactionType.TRANSFER_FUNDS_TO_PROJECT));

	}

	@Test
	public void tranferFundsBelowCurrentValue() throws Exception {
		BigDecimal transferAmount = BigDecimal.valueOf(400.00);
		BigDecimal expectedGeneralCash = BigDecimal.valueOf(600.00);
		BigDecimal expectedProjectCash = BigDecimal.valueOf(400.00);
		BigDecimal expectedSum = BigDecimal.valueOf(1000.00);
		registerTransaction.setAmount(transferAmount);
		transferFundsToProject.updateSummaries(registerTransaction);

		BigDecimal actualGeneralCash = accountRegisterSummaryFields.getGeneralCash();
		BigDecimal actualProjectCash = accountRegisterSummaryFields.getProjectCash();
		BigDecimal actualSum = accountRegisterSummaryFields.getProjectCash().add(accountRegisterSummaryFields.getGeneralCash());

		Assert.assertEquals(expectedGeneralCash, actualGeneralCash);
		Assert.assertEquals(expectedProjectCash, actualProjectCash);
		Assert.assertEquals(expectedSum, actualSum);
	}

	@Test
	public void transferFundsAboveCurrentValue() throws Exception {
		BigDecimal transferAmount = BigDecimal.valueOf(1500.00);
		BigDecimal expectedGeneralCash = BigDecimal.valueOf(-500.00);
		BigDecimal expectedProjectCash = BigDecimal.valueOf(1500.00);
		BigDecimal expectedSum = BigDecimal.valueOf(1000.00);
		registerTransaction.setAmount(transferAmount);
		try{
			transferFundsToProject.updateSummaries(registerTransaction);
		} catch (Exception e){
			return;
		}

		BigDecimal actualGeneralCash = accountRegisterSummaryFields.getGeneralCash();
		BigDecimal actualProjectCash = accountRegisterSummaryFields.getProjectCash();
		BigDecimal actualSum = accountRegisterSummaryFields.getProjectCash().add(accountRegisterSummaryFields.getGeneralCash());

		Assert.assertEquals(expectedGeneralCash, actualGeneralCash);
		Assert.assertEquals(expectedProjectCash, actualProjectCash);
		Assert.assertEquals(expectedSum, actualSum);
	}
}
