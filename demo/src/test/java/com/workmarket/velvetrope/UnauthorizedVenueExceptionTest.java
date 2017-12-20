package com.workmarket.velvetrope;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnauthorizedVenueExceptionTest {

	private static final String REDIRECT_PATH = "/whatevs";
	private static final String MESSAGE = "Fer Realsies";
	private VelvetRope velvetRope;
	private UnauthorizedVenueException exception;

	@Before
	public void setUp() throws Exception {
		velvetRope = mock(VelvetRope.class);
		when(velvetRope.venue()).thenReturn(Venue.LOBBY);
	}

	@Test
	public void getRedirectPath_ReturnsTheRedirectPathOfTheVelvetRope() throws Throwable {
		when(velvetRope.redirectPath()).thenReturn(REDIRECT_PATH);
		exception = new UnauthorizedVenueException(velvetRope);
		try {
			throw exception;
		} catch(UnauthorizedVenueException e) {
			assertThat(e.getRedirectPath(), is(REDIRECT_PATH));
		}
	}

	@Test
	public void getMessage_ReturnsTheMessageOfTheVelvetRope() throws Throwable {
		when(velvetRope.message()).thenReturn(MESSAGE);
		exception = new UnauthorizedVenueException(velvetRope);
		try {
			throw exception;
		} catch(UnauthorizedVenueException e) {
			assertThat(e.getMessage(), is(MESSAGE));
		}
	}

	@Test
	public void getMessage_WhenVelvetRopeHasNoMessage_ReturnsTheDefaultMessage() throws Throwable {
		exception = new UnauthorizedVenueException(velvetRope);
		try {
			throw exception;
		} catch(UnauthorizedVenueException e) {
			assertThat(e.getMessage(), is(String.format(UnauthorizedVenueException.DEFAULT_MESSAGE, velvetRope.venue())));
		}
	}
}
