package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.utility.RandomUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by nick on 11/6/13 3:33 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class AbstractTaxEntityTest {

	@Test
	public void getCountryFromCountryId_US_USA() {
		assertEquals(AbstractTaxEntity.COUNTRY_USA, AbstractTaxEntity.getCountryFromCountryId(Country.US));
	}

	@Test
	public void getCountryFromCountryId_USA_USA() { // love it or leave it
		assertEquals(AbstractTaxEntity.COUNTRY_USA, AbstractTaxEntity.getCountryFromCountryId(Country.USA));
	}

	@Test
	public void getCountryFromCountryId_USATerritory_USA() {
		assertEquals(AbstractTaxEntity.COUNTRY_USA, AbstractTaxEntity.getCountryFromCountryId("PR"));
	}

	@Test
	public void getCountryFromCountryId_Canada_Canada() {
		assertEquals(AbstractTaxEntity.COUNTRY_CANADA, AbstractTaxEntity.getCountryFromCountryId(Country.CANADA));
	}

	@Test
	public void getCountryFromCountryId_Null_Other() {
		assertEquals(AbstractTaxEntity.COUNTRY_OTHER, AbstractTaxEntity.getCountryFromCountryId(null));
	}

	@Test
	public void getCountryFromCountryId_AnythingElse_Other() {
		assertEquals(AbstractTaxEntity.COUNTRY_OTHER, AbstractTaxEntity.getCountryFromCountryId(RandomUtilities.generateAlphaString(3)));
	}
}
