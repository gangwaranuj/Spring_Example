package com.workmarket.domains.velvetrope.service;

import com.google.common.collect.Sets;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import com.workmarket.velvetrope.TokenService;
import com.workmarket.velvetrope.Venue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class TokenServiceImplIT extends BaseServiceIT {
	private static final int TOKEN = 12345;

	@Autowired TokenService service;
	@Autowired AdmissionService admissionService;
	@Autowired RedisAdapter redis;

	@Before
	public void setup() {
		service.deleteTokenFor(COMPANY_ID);
		admissionService.destroyAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.LOBBY);
	}

	@After
	public void teardown() {
		service.deleteTokenFor(COMPANY_ID);
		admissionService.destroyAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.LOBBY);
	}

	@Test
	public void tokenFor_WhenNotCached_DefaultsToVenueLOBBYId() throws Exception {
		int token = service.tokenFor(COMPANY_ID);
		assertThat(token, is(Venue.LOBBY.mask()));
	}

	@Test
	public void tokenFor_WhenCached_IsWhatYouCachedItAs() throws Exception {
		service.cacheToken(COMPANY_ID, TOKEN);
		int token = service.tokenFor(COMPANY_ID);
		assertThat(token, is(TOKEN));
	}

	@Test
	public void tokenFor_WhenSaved_IsWhatYouSavedItAs() throws Exception {
		admissionService.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.LOBBY);
		int token = service.tokenFor(COMPANY_ID);
		assertThat(token, is(Venue.LOBBY.mask()));
	}

	@Test
	public void tokenFor_WithMultipleNestedAdmissions_IsWhatYouSavedItAsWithoutDuplicatedVenueMasks() throws Exception {
		// COMPANY is nested within ENTERPRISE
		admissionService.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.COMPANY);
		admissionService.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.ENTERPRISE);

		// Get the token
		int token = service.tokenFor(COMPANY_ID);

		// Clean up now because we can
		admissionService.destroyAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.COMPANY);
		admissionService.destroyAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.ENTERPRISE);

		// Calculate the expected token, based on a deduped set of all provided venues
		Set<Venue> venues = Sets.newHashSet();
		venues.addAll(Venue.COMPANY.getProvidedVenues());
		venues.addAll(Venue.ENTERPRISE.getProvidedVenues());
		int calculatedToken = 0;
		for (Venue venue : venues) {
			calculatedToken |= venue.mask();
		}

		// the real token is the same as the deduped one above.
		assertThat(token, is(calculatedToken));
	}

	@Test
	public void tokenFor_WithExistingInvalidToken_IsWhatYouSavedItAs() throws Exception {
		// Cache a broken token in redis
		redis.set(String.format(TokenServiceImpl.VELVET_ROPE_TOKEN_KEY, COMPANY_ID), TOKEN);

		admissionService.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.LOBBY);
		int token = service.tokenFor(COMPANY_ID);
		assertThat(token, is(Venue.LOBBY.mask()));
	}
}
