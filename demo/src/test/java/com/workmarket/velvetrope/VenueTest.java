package com.workmarket.velvetrope;

import com.google.api.client.util.Sets;
import com.google.common.collect.ImmutableSet;
import com.workmarket.velvetrope.Venue.VenueType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class VenueTest {

	private final Venue venue;
	private final Set<Venue> providedVenues = Sets.newHashSet();
	private final boolean isSystemPlan;
	private final boolean isFeature;
	private final boolean isInternalBetaFeature;
	private final boolean isOpenSignUpBetaFeature;

	public VenueTest(Venue venue, Set<Venue> providedVenues, boolean isSystemPlan, boolean isFeature, boolean isInternalBetaFeature, boolean isOpenSignUpBetaFeature) {
		this.venue = venue;
		this.providedVenues.addAll(providedVenues);
		this.isSystemPlan = isSystemPlan;
		this.isFeature = isFeature;
		this.isInternalBetaFeature = isInternalBetaFeature;
		this.isOpenSignUpBetaFeature = isOpenSignUpBetaFeature;
	}

	@Parameters
	public static Collection<Object[]> cases() {
		return Arrays.asList(new Object[][]{
			{Venue.LOBBY, new HashSet<>(Arrays.asList(Venue.LOBBY)), false, false, false, false},
			{Venue.SHARED_GROUPS, new HashSet<>(Arrays.asList(Venue.SHARED_GROUPS)), false, true, false, false},
			{Venue.WEBHOOKS, new HashSet<>(Arrays.asList(Venue.WEBHOOKS)), false, true, false, false},
			{Venue.SALESFORCE_WEBHOOKS, new HashSet<>(Arrays.asList(Venue.SALESFORCE_WEBHOOKS)), false, true, false, false},
			{Venue.COMPANY, new HashSet<>(Arrays.asList(Venue.COMPANY)), false, false, false, false},
			{Venue.TRANSACTIONAL, new HashSet<>(Arrays.asList(Venue.COMPANY, Venue.TRANSACTIONAL)), true, false, false, false},
			{Venue.PROFESSIONAL, new HashSet<>(Arrays.asList(Venue.COMPANY, Venue.TRANSACTIONAL, Venue.PROFESSIONAL)), true, false, false, false},
			{Venue.ENTERPRISE, new HashSet<>(Arrays.asList(Venue.COMPANY, Venue.TRANSACTIONAL, Venue.PROFESSIONAL, Venue.ENTERPRISE)), true, false, false, false},
			{Venue.PRIVATE_NETWORK, new HashSet<>(Arrays.asList(Venue.PRIVATE_NETWORK)), false, true, false, false},
			{Venue.INTERNAL_NETWORK, new HashSet<>(Arrays.asList(Venue.INTERNAL_NETWORK)), false, true, false, false}
		});
	}

	@Test
	public void getCheckSum_IsTheCRC32ValueForTheStringOfVenueNamesConcatenated() {
		StringBuilder builder = new StringBuilder();
		for (Venue venue : Venue.values()) {
			for (Venue providedVenue : venue.getProvidedVenues()) {
				builder.append(providedVenue.name());
			}
		}

		byte[] bytes = builder.toString().getBytes();
		CRC32 crc = new CRC32();
		crc.update(bytes);

		assertThat(Venue.getCheckSum(), is(crc.getValue()));
	}

	@Test
	public void CHECKSUM_IsTheValueOfTheChecksumCalculation() {
		assertThat(Venue.CHECKSUM, is(Venue.getCheckSum()));
	}

	@Test
	public void displayName() {
		assertThat(venue.getDisplayName(), is(any(String.class)));
	}

	@Test
	public void description() {
		assertThat(venue.getDescription(), is(any(String.class)));
	}

	@Test
	public void id_IsOrdinalPlusOne() {
		assertThat(venue.id(), is(venue.ordinal() + 1));
	}

	@Test
	public void mask_IsBitmaskOROfVenuesProvidedVenueMasks() {
		int mask = 0;
		for (Venue provided : venue.getProvidedVenues()) {
			mask |= provided.mask();
		}
		assertThat(venue.mask(), is(mask));
	}

	@Test
	public void getProvidedVenues_HasItself() {
		assertThat(venue.getProvidedVenues(), hasItem(is(venue)));
	}

	@Test
	public void getProvidedVenues() {
		assertThat(venue.getProvidedVenues(), is(providedVenues));
	}

	@Test
	public void isOrIsNotASystemPlan() {
		assertThat(venue.isSystemPlan(), is(isSystemPlan));
	}

	@Test
	public void isOrIsNotAFeature() {
		assertThat(venue.isFeature(), is(isFeature));
	}

	@Test
	public void isOrIsNotAInternalBetaFeature() { assertThat(venue.isInternalBetaFeature(), is(isInternalBetaFeature)); }

	@Test
	public void isOrIsNotAOpenSignUpBetaFeature() { assertThat(venue.isOpenSignUpBetaFeature(), is(isOpenSignUpBetaFeature)); }

	@Test
	public void getVenueSetByType() {
		Set<Venue> expected = ImmutableSet.of(
			Venue.LOBBY,
			Venue.COMPANY
		);

		Set<Venue> result = Venue.getVenueSetByType(VenueType.SYSTEM);

		assertTrue(result.containsAll(expected) && expected.containsAll(result));
	}
}
