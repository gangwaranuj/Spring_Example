package com.workmarket.domains.velvetrope.guest;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.velvetrope.Venue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserGuestTest {
	private static final Long USER_ID = 9999999L;
	private static final Long COMPANY_ID = 8888888L;
	@Mock User user;
	@Mock Company company;

	@Before
	public void setup() {
		when(user.getId()).thenReturn(USER_ID);
		when(company.getId()).thenReturn(COMPANY_ID);
		when(user.getCompany()).thenReturn(company);
	}

	@Test
	public void getId_WithAUser_IsTheIdOfTheUser() throws Exception {
		UserGuest guest = new UserGuest(user);
		assertThat(guest.getId(), is(USER_ID));
	}

	@Test
	public void getCompanyId_WithAUser_GetsTheUsersCompany() throws Exception {
		UserGuest guest = new UserGuest(user);
		guest.getCompanyId();
		verify(user).getCompany();
	}

	@Test
	public void getCompanyId_WithAUser_GetsTheCompanysId() throws Exception {
		UserGuest guest = new UserGuest(user);
		guest.getCompanyId();
		verify(company).getId();
	}

	@Test
	public void getCompanyId_WithAUser_IsTheCompanyIdOfTheUser() throws Exception {
		UserGuest guest = new UserGuest(user);
		assertThat(guest.getCompanyId(), is(COMPANY_ID));
	}

	@Test
	public void canEnter_WithNoToken_isFalse() throws Exception {
		UserGuest guest = new UserGuest(user);
		assertThat(guest.canEnter(Venue.LOBBY), is(false));
	}

	@Test
	public void canEnter_WithSetTokenOnlyContainingVenue_isTrue() throws Exception {
		// Token is a bitmap integer, like 0b1
		int token = Venue.LOBBY.id();
		UserGuest guest = new UserGuest(user);
		guest.setToken(token);
		assertThat(guest.canEnter(Venue.LOBBY), is(true));
	}

	@Test
	public void canEnter_WithTokenOnlyContainingVenue_isTrue() throws Exception {
		// Token is a bitmap integer, like 0b1
		int token = Venue.LOBBY.id();
		UserGuest guest = new UserGuest(user);
		guest.setToken(token);
		assertThat(guest.canEnter(Venue.LOBBY), is(true));
	}

	@Test
	public void canEnter_WithTokenNotContainingVenue_isFalse() throws Exception {
		// Token is a bitmap integer
		int token = 0b10;
		UserGuest guest = new UserGuest(user);
		guest.setToken(token);
		assertThat(guest.canEnter(Venue.LOBBY), is(false));
	}

	@Test
	public void canEnter_WithTokenAlsoContainingVenue_isTrue() throws Exception {
		// Token is a bitmap integer, 0b11
		int token = Venue.LOBBY.id() + 0b10;
		UserGuest guest = new UserGuest(user);
		guest.setToken(token);
		assertThat(guest.canEnter(Venue.LOBBY), is(true));
	}
}
