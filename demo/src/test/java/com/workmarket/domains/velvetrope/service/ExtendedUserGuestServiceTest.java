package com.workmarket.domains.velvetrope.service;

import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.velvetrope.AuthenticatedGuestService;
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
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExtendedUserGuestServiceTest {
	private static final Long COMPANY_ID = 99999L;
	private static final Integer TOKEN = 12345;

	@Mock SecurityContextFacade securityContextFacade;
	@Mock TokenService tokenService;
	@InjectMocks AuthenticatedGuestService service = spy(new ExtendedUserGuestService());

	ExtendedUserDetails user;
	Guest guest;
	Guest deniedGuest;

	@Before
	public void setUp() throws Exception {
		user = mock(ExtendedUserDetails.class);
		when(user.getCompanyId()).thenReturn(COMPANY_ID);

		when(securityContextFacade.getCurrentUser()).thenReturn(user);
		when(tokenService.tokenFor(COMPANY_ID)).thenReturn(TOKEN);

		guest = mock(Guest.class);
		when(service.makeGuest(user, TOKEN)).thenReturn(guest);

		deniedGuest = mock(Guest.class);
		when(service.makeGuest(null, WebGuest.EMPTY_TOKEN)).thenReturn(deniedGuest);
	}

	@Test
	public void getGuest_GetsTheCurrentUser() throws Exception {
		service.getGuest();
		verify(securityContextFacade).getCurrentUser();
	}

	@Test
	public void getGuest_GetsTheCurrentUserCompanysToken() throws Exception {
		service.getGuest();
		verify(tokenService).tokenFor(COMPANY_ID);
	}

	@Test
	public void getGuest_WhenThereIsNoUserInTheSecurityContext_NeverGetsACompanysToken() throws Exception {
		when(securityContextFacade.getCurrentUser()).thenReturn(null);
		service.getGuest();
		verify(tokenService, never()).tokenFor(anyLong());
	}

	@Test
	public void getGuest_MakesAGuest() throws Exception {
		service.getGuest();
		verify(service).makeGuest(user, TOKEN);
	}

	@Test
	public void getGuest_WhenThereIsNoUserInTheSecurityContext_MakesAGuest() throws Exception {
		when(securityContextFacade.getCurrentUser()).thenReturn(null);
		service.getGuest();
		verify(service).makeGuest(null, WebGuest.EMPTY_TOKEN);
	}

	@Test
	public void getGuest_ReturnsAGuest() throws Exception {
		Guest actualGuest = service.getGuest();
		assertThat(actualGuest, is(guest));
	}

	@Test
	public void getGuest_WhenThereIsNoUserInTheSecurityContext_ReturnsAGuest() throws Exception {
		when(securityContextFacade.getCurrentUser()).thenReturn(null);
		Guest actualGuest = service.getGuest();
		assertThat(actualGuest, is(deniedGuest));
	}
}
