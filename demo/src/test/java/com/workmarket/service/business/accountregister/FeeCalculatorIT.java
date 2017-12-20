package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.RegisterTransactionCost;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionFactory;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionFactoryImpl;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class FeeCalculatorIT {

	private FeeCalculator calculator;
	private AccountRegister accountRegister;
	private AbstractBankAccount payPalAccount;

	@Before
	public void init() {
		RegisterTransactionFactory transactionFactory = new RegisterTransactionFactoryImpl();
		PricingService pricingService = mock(PricingService.class);

		when(pricingService.findCostForTransactionType(eq(RegisterTransactionType.PAY_PAL_FEE_USA), any(AccountRegister.class))).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				RegisterTransactionCost cost = new RegisterTransactionCost();
				cost.setFixedAmount(BigDecimal.ONE);
				return cost;
			}
		});

		when(pricingService.findCostForTransactionType(eq(RegisterTransactionType.WM_PAY_PAL_FEE_USA), any(AccountRegister.class))).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				RegisterTransactionCost cost = new RegisterTransactionCost();
				cost.setPercentageAmount(BigDecimal.valueOf(0.0200));
				cost.setPercentageBasedAmountLimit(BigDecimal.ONE);
				return cost;
			}
		});

		calculator = new FeeCalculatorImpl(transactionFactory, pricingService);

		accountRegister = new AccountRegister();
		payPalAccount = new PayPalAccount();
		payPalAccount.setCountry(Country.USA_COUNTRY);
	}

	@Test
	public void calcPayPalFeeBelowLimit() throws Exception {
		TransactionBreakdown breakdown = calculator.calculateWithdraw(accountRegister, payPalAccount, BigDecimal.valueOf(29.00));
		Assert.assertEquals(BigDecimal.valueOf(29.00), breakdown.getGross());
		Assert.assertEquals(BigDecimal.valueOf(28.00), breakdown.getNet());
		Assert.assertEquals(BigDecimal.ONE, breakdown.getFee());
		Assert.assertEquals(BigDecimal.valueOf(.58), breakdown.getSecretFee());
	}

	@Test
	public void calcPayPalFeeAboveLimit() throws Exception {
		TransactionBreakdown breakdown = calculator.calculateWithdraw(accountRegister, payPalAccount, BigDecimal.valueOf(80.00));
		Assert.assertEquals(BigDecimal.valueOf(80.00), breakdown.getGross());
		Assert.assertEquals(BigDecimal.valueOf(79.00), breakdown.getNet());
		Assert.assertEquals(BigDecimal.ONE, breakdown.getFee());
		Assert.assertEquals(BigDecimal.ONE, breakdown.getSecretFee());
	}
}
