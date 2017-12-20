package com.workmarket.domains.velvetrope.service;

import com.workmarket.velvetrope.Guest;
import com.workmarket.velvetrope.TokenService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleGuestServiceTest {
	private static final Long COMPANY_ID = 99999L;
	private static final Integer TOKEN = 12345;

	@Mock TokenService tokenService;
	@InjectMocks SimpleGuestService service = new SimpleGuestService();

	Guest guest;

	@Before
	public void setUp() throws Exception {
		when(tokenService.tokenFor(COMPANY_ID)).thenReturn(TOKEN);

		guest = mock(Guest.class);
		when(guest.getCompanyId()).thenReturn(COMPANY_ID);
	}

	@Test
	public void getGuest_WithGuest_GetsTheGuestCompanysToken() throws Exception {
		service.getGuest(guest);
		verify(tokenService).tokenFor(COMPANY_ID);
	}

	@Test
	public void getGuest_WithGuest_SetsTheGuestsToken() throws Exception {
		service.getGuest(guest);
		verify(guest).setToken(TOKEN);
	}

	@Test
	public void getGuest_WithGuest_ReturnsTheGuest() throws Exception {
		Guest actualGuest = service.getGuest(guest);
		assertThat(actualGuest, is(guest));
	}
}
