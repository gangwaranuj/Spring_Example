package com.workmarket.domains.model.postalcode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by nick on 9/4/13 5:18 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CountryTest {


	@Test
	public void valueOf_InvalidCountry_WithoutCountry() {
		assertEquals(Country.WITHOUTCOUNTRY, Country.valueOf("Soviet Canuckistan").getId());
	}

	@Test
	public void valueOf_US_USACountry() {
		assertEquals(Country.USA_COUNTRY, Country.valueOf("US"));
	}

	@Test
	public void valueOf_USA_USACountry() {
		assertEquals(Country.USA_COUNTRY, Country.valueOf("USA"));
	}

	@Test
	public void valueOf_UnitedStates_USACountry() {
		assertEquals(Country.USA_COUNTRY, Country.valueOf("United States"));
	}

	@Test
	public void valueOf_CA_CanadaCountry() {
		assertEquals(Country.CANADA_COUNTRY, Country.valueOf("CA"));
	}

	@Test
	public void valueOf_CAN_CanadaCountry() {
		assertEquals(Country.CANADA_COUNTRY, Country.valueOf("CAN"));
	}

	@Test
	public void valueOf_Canada_CanadaCountry() {
		assertEquals(Country.CANADA_COUNTRY, Country.valueOf("Canada"));
	}

	@Test
	public void valueOf_PL_PolandCountry() {
		assertEquals("Poland", Country.valueOf("PL").getName());
	}

	@Test
	public void valueOf_Poland_PolandCountry() {
		assertEquals("Poland", Country.valueOf("Poland").getName());
	}
}
