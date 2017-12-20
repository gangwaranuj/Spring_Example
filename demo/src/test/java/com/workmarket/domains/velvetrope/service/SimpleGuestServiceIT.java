package com.workmarket.domains.velvetrope.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.TokenService;
import com.workmarket.velvetrope.UnauthenticatedGuestService;
import com.workmarket.velvetrope.Venue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SimpleGuestServiceIT extends BaseServiceIT {
	@Autowired ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired TokenService tokenService;

	@Qualifier("simpleGuestService")
	@Autowired UnauthenticatedGuestService service;

	User worker;
	ExtendedUserDetails details;
	Guest guest;

	@Before
	public void setUp() throws Exception {
		worker = newRegisteredWorker();
		details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(worker.getEmail());

		tokenService.deleteTokenFor(details.getCompanyId());
	}

	@After
	public void teardown() {
		tokenService.deleteTokenFor(details.getCompanyId());
	}

	@Test
	public void getGuest_WhenGuestHasNoToken_GetsGuestThatCanAccessTheDefaultVenue() throws Exception {
		guest = service.getGuest(new UserGuest(worker));
		assertThat(guest.canEnter(Venue.LOBBY), is(true));
	}

	@Test
	public void getGuest_WhenGuestHasATokenWithoutAVenue_GetsGuestThatCannotAccessTheVenue() throws Exception {
		tokenService.cacheToken(details.getCompanyId(), 0b10);

		guest = service.getGuest(new UserGuest(worker));
		assertThat(guest.canEnter(Venue.LOBBY), is(false));
	}
}
