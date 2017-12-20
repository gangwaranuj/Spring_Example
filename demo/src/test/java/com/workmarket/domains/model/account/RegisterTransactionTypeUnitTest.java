package com.workmarket.domains.model.account;

import com.workmarket.domains.model.postalcode.Country;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by nick on 5/13/13 1:20 PM
 */
public class RegisterTransactionTypeUnitTest {
	@Test
	public void newPaypalFeeInstanceByCountry_WithUSA_ReturnUSA() throws Exception {
		assertEquals(RegisterTransactionType.PAY_PAL_FEE_USA,
				RegisterTransactionType.newPaypalFeeInstanceByCountry(Country.USA_COUNTRY).get());
	}

	@Test
	public void newPaypalFeeInstanceByCountry_WithNull_ReturnAbsent() throws Exception {
		assertFalse(RegisterTransactionType.newPaypalFeeInstanceByCountry(null).isPresent());
	}

	@Test
	public void newWMPaypalFeeInstanceByCountry_WithUSA_ReturnUSA() throws Exception {
		assertEquals(RegisterTransactionType.WM_PAY_PAL_FEE_USA,
				RegisterTransactionType.newWMPaypalFeeInstanceByCountry(Country.USA_COUNTRY).get());
	}

	@Test
	public void newWMPaypalFeeInstanceByCountry_WithNull_ReturnAbsent() throws Exception {
		assertEquals(RegisterTransactionType.WM_PAY_PAL_FEE_USA,
				RegisterTransactionType.newWMPaypalFeeInstanceByCountry(Country.USA_COUNTRY).get());
	}

	@Test
	public void newWMPaypalFeeInstanceByCountry_WithGB_ReturnINTL() throws Exception {
		assertEquals(RegisterTransactionType.WM_PAY_PAL_FEE_INTL,
				RegisterTransactionType.newWMPaypalFeeInstanceByCountry(Country.valueOf("GB")).get());
	}

	@Test
	public void newPaypalFeeInstanceByCountry_WithGB_ReturnINTL() throws Exception {
		assertEquals(RegisterTransactionType.PAY_PAL_FEE_INTL,
				RegisterTransactionType.newPaypalFeeInstanceByCountry(Country.valueOf("GB")).get());
	}
}
