package com.workmarket.domains.model.account;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class AccountingSummaryTest {

	private AccountingSummary summary;

	@Before
	public void setUp() {
		summary = new AccountingSummary();

		summary.setMoneyInAch(new BigDecimal(50));
		summary.setMoneyInChecks(new BigDecimal(70));
		summary.setMoneyInCreditCard(new BigDecimal(80));
		summary.setMoneyInWire(new BigDecimal(90));

		summary.setMoneyOutWithdrawals(new BigDecimal(-1));
		summary.setMoneyOutNonUSAWithdrawals(new BigDecimal(-2));
		summary.setMoneyOutPayPalWithdrawal(new BigDecimal(-3));
		summary.setMoneyOutGCCWithdrawal(new BigDecimal(-4));

		summary.setMoneyOutPayPalFees(new BigDecimal(-5));
		summary.setMoneyOutCreditCardFees(new BigDecimal(-6));

		summary.setMoneyOutBackgroundChecks(new BigDecimal(-7));
		summary.setMoneyOutDrugTests(new BigDecimal(-8));

		summary.setMoneyOutDepositReturnFee(new BigDecimal(-9));
		summary.setMoneyOutWithdrawalReturnFee(new BigDecimal(-10));
		summary.setMoneyOutLatePaymentFee(new BigDecimal(-11));
		summary.setMoneyOutMiscellaneousFee(new BigDecimal(-12));

		summary.setMoneyOutDebitTransactions(new BigDecimal(-20));

		summary.setMoneyOutCreditTransactions(new BigDecimal(25));
	}

	@Test
	public void calculateTotalMoneyOnSystem_verifyNonUsWithdrawlsWithoutAccoutingPriceService() {
		// verify the calculation of our total money on system
		BigDecimal amount = summary.calculateTotalMoneyOnSystem();
		assertEquals(new BigDecimal(217).setScale(2),amount);
	}

	@Test
	public void calculateTotalMoneyOnSystem_verifyNonUsWithdrawlsWithAccoutingPriceService() {
		// add some additional accounting data
		AccountingPricingServiceTypeSummary pricingServiceTypeSummary = new AccountingPricingServiceTypeSummary();
		pricingServiceTypeSummary.setMoneyOutTransactionalVorSoftwareFee(new BigDecimal(-5));
		pricingServiceTypeSummary.setMoneyOutTransactionalVorVorFee(new BigDecimal(-7));
		pricingServiceTypeSummary.setMoneyOutTransactionalNonVorSoftwareFee(new BigDecimal(-9));

		pricingServiceTypeSummary.setMoneyOutSubscriptionVorSoftwareFee(new BigDecimal(-12));
		pricingServiceTypeSummary.setMoneyOutSubscriptionVorVorFee(new BigDecimal(-18));
		pricingServiceTypeSummary.setMoneyOutSubscriptionNonVorSoftwareFee(new BigDecimal(-22));

		pricingServiceTypeSummary.setMoneyOutProfessionalServiceFee(new BigDecimal(-17));

		summary.setAccountingPricingServiceTypeSummary(pricingServiceTypeSummary);


		// verify the calculation of our total money on system
		BigDecimal amount = summary.calculateTotalMoneyOnSystem();
		assertEquals(new BigDecimal(127).setScale(2),amount);
	}
}
