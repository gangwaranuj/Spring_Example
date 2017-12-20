package com.workmarket.domains.velvetrope.guest;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.velvetrope.Venue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebGuestTest {
	private static final Long USER_ID = 9999999L;
	private static final Long COMPANY_ID = 8888888L;
	@Mock ExtendedUserDetails user;

	@Before
	public void setup() {
		when(user.getId()).thenReturn(USER_ID);
		when(user.getCompanyId()).thenReturn(COMPANY_ID);
	}

	@Test
	public void getId_WithAUser_IsTheIdOfTheUser() throws Exception {
		WebGuest guest = new WebGuest(user);
		assertThat(guest.getId(), is(USER_ID));
	}

	@Test
	public void getCompanyId_WithAUser_IsTheCompanyIdOfTheUser() throws Exception {
		WebGuest guest = new WebGuest(user);
		assertThat(guest.getCompanyId(), is(COMPANY_ID));
	}

	@Test
	public void canEnter_WithNoToken_isFalse() throws Exception {
		WebGuest guest = new WebGuest(user);
		assertThat(guest.canEnter(Venue.LOBBY), is(false));
	}

	@Test
	public void canEnter_WithSetTokenOnlyContainingVenue_isTrue() throws Exception {
		// Token is a bitmap integer, like 0b1
		int token = Venue.LOBBY.id();
		WebGuest guest = new WebGuest(user);
		guest.setToken(token);
		assertThat(guest.canEnter(Venue.LOBBY), is(true));
	}

	@Test
	public void canEnter_WithTokenOnlyContainingVenue_isTrue() throws Exception {
		// Token is a bitmap integer, like 0b1
		int token = Venue.LOBBY.id();
		WebGuest guest = new WebGuest(user, token);
		assertThat(guest.canEnter(Venue.LOBBY), is(true));
	}

	@Test
	public void canEnter_WithTokenNotContainingVenue_isFalse() throws Exception {
		// Token is a bitmap integer
		int token = 0b10;
		WebGuest guest = new WebGuest(user, token);
		assertThat(guest.canEnter(Venue.LOBBY), is(false));
	}

	@Test
	public void canEnter_WithTokenAlsoContainingVenue_isTrue() throws Exception {
		// Token is a bitmap integer, 0b11
		int token = Venue.LOBBY.id() + 0b10;
		WebGuest guest = new WebGuest(user, token);
		assertThat(guest.canEnter(Venue.LOBBY), is(true));
	}
}
