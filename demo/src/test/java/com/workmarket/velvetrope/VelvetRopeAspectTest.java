package com.workmarket.velvetrope;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VelvetRopeAspectTest {

	@Mock AuthenticatedGuestService authenticatedGuestService;
	@Mock UnauthenticatedGuestService unauthenticatedGuestService;
	@InjectMocks VelvetRopeAspect aspect = spy(new VelvetRopeAspect());

	Guest guest;
	MockTargetInterface methodTargetProxy;
	MockTargetInterface classTargetProxy;
	MockTargetInterface methodClassTargetProxy;
	Doorman lobbyRopeDoorman;
	ProceedingJoinPoint proceedingJoinPoint;
	VelvetRope velvetRope;

	@Before
	public void setUp() throws Exception {
		MockTargetInterface methodTarget = new MockMethodTargetController();
		AspectJProxyFactory methodTargetFactory = new AspectJProxyFactory(methodTarget);
		methodTargetFactory.addAspect(aspect);
		methodTargetProxy = methodTargetFactory.getProxy();

		MockTargetInterface classTarget = new MockClassTargetController();
		AspectJProxyFactory classTargetFactory = new AspectJProxyFactory(classTarget);
		classTargetFactory.addAspect(aspect);
		classTargetProxy = classTargetFactory.getProxy();

		MockTargetInterface methodClassTarget = new MockMethodClassTargetController();
		AspectJProxyFactory methodClassTargetFactory = new AspectJProxyFactory(methodClassTarget);
		methodClassTargetFactory.addAspect(aspect);
		methodClassTargetProxy = methodClassTargetFactory.getProxy();

		Doorman doormanTarget = new LobbyRopeDoorman();
		AspectJProxyFactory doormanTargetFactory = new AspectJProxyFactory(doormanTarget);
		doormanTargetFactory.addAspect(aspect);
		lobbyRopeDoorman = doormanTargetFactory.getProxy();

		proceedingJoinPoint = mock(ProceedingJoinPoint.class);

		guest = mock(Guest.class);
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);

		velvetRope = mock(VelvetRope.class);
		when(velvetRope.venue()).thenReturn(Venue.LOBBY);

		when(authenticatedGuestService.getGuest()).thenReturn(guest);

		when(unauthenticatedGuestService.getGuest(any(Guest.class))).thenReturn(guest);
	}

	@Test
	public void callingRopedMethod_AdvisesRopedMethod() throws Throwable {
		methodTargetProxy.doThatThing();
		verify(aspect).checkAnnotatedMethod(any(ProceedingJoinPoint.class), any(VelvetRope.class));
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void callingRopedMethodWithBypass_Throws() throws Throwable {
		methodTargetProxy.dontDoThatThing();
	}

	@Test
	public void callingNotRopedMethod_DoesNotAdviseMethod() throws Throwable {
		methodTargetProxy.doThatOtherThing();
		verify(aspect, never()).checkAnnotatedMethod(any(ProceedingJoinPoint.class), any(VelvetRope.class));
	}

	@Test
	public void callingMethodInRopedClass_AdvisesRopedMethod() throws Throwable {
		classTargetProxy.doThatThing();
		verify(aspect).checkAnnotatedClass(any(ProceedingJoinPoint.class), any(VelvetRope.class));
		verify(aspect, never()).checkAnnotatedMethod(any(ProceedingJoinPoint.class), any(VelvetRope.class));
	}

	@Test
	public void callingRopedMethodInRopedClass_AdvisesRopedMethodTwice() throws Throwable {
		methodClassTargetProxy.doThatThing();
		verify(aspect).checkAnnotatedClass(any(ProceedingJoinPoint.class), any(VelvetRope.class));
		verify(aspect).checkAnnotatedMethod(any(ProceedingJoinPoint.class), any(VelvetRope.class));
	}

	@Test
	public void checkAnnotatedMethod_WithProceedingJoinPointAndVelvetRope_GetsTheCurrentGuest() throws Throwable {
		aspect.checkAnnotatedMethod(proceedingJoinPoint, velvetRope);
		verify(authenticatedGuestService).getGuest();
	}

	@Test
	public void checkAnnotatedClass_WithProceedingJoinPointAndVelvetRope_GetsTheCurrentGuest() throws Throwable {
		aspect.checkAnnotatedClass(proceedingJoinPoint, velvetRope);
		verify(authenticatedGuestService).getGuest();
	}

	@Test
	public void checkAnnotatedMethod_WithProceedingJoinPointAndVelvetRope_ChecksIfTheGuestCanEnter() throws Throwable {
		aspect.checkAnnotatedMethod(proceedingJoinPoint, velvetRope);
		verify(guest).canEnter(Venue.LOBBY);
	}

	@Test
	public void checkAnnotatedClass_WithProceedingJoinPointAndVelvetRope_ChecksIfTheGuestCanEnter() throws Throwable {
		aspect.checkAnnotatedClass(proceedingJoinPoint, velvetRope);
		verify(guest).canEnter(Venue.LOBBY);
	}

	@Test
	public void checkAnnotatedMethod_WhenGuestIsAllowedPastRopeAndRopeIsNotBypassed_Proceeds() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(velvetRope.bypass()).thenReturn(false);
		aspect.checkAnnotatedMethod(proceedingJoinPoint, velvetRope);
		verify(proceedingJoinPoint).proceed();
	}

	@Test
	public void checkAnnotatedMethod_WhenGuestIsNotAllowedPastRopeAndRopeIsBypassed_Proceeds() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(false);
		when(velvetRope.bypass()).thenReturn(true);
		aspect.checkAnnotatedMethod(proceedingJoinPoint, velvetRope);
		verify(proceedingJoinPoint).proceed();
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void checkAnnotatedMethod_WhenGuestIsNotAllowedPastRopeAndRopeIsNotBypassed_Throws() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(false);
		when(velvetRope.bypass()).thenReturn(false);
		aspect.checkAnnotatedMethod(proceedingJoinPoint, velvetRope);
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void checkAnnotatedMethod_WhenGuestIsAllowedPastRopeAndRopeIsBypassed_Throws() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(velvetRope.bypass()).thenReturn(true);
		aspect.checkAnnotatedMethod(proceedingJoinPoint, velvetRope);
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void checkAnnotatedMethod_WhenGuestIsNotAllowedPastRopeAndRopeIsNotBypassed_DoesNotProceed() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(false);
		when(velvetRope.bypass()).thenReturn(false);
		aspect.checkAnnotatedMethod(proceedingJoinPoint, velvetRope);
		verify(proceedingJoinPoint, never()).proceed();
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void checkAnnotatedMethod_WhenGuestIsAllowedPastRopeAndRopeIsBypassed_DoesNotProceed() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(velvetRope.bypass()).thenReturn(true);
		aspect.checkAnnotatedMethod(proceedingJoinPoint, velvetRope);
		verify(proceedingJoinPoint, never()).proceed();
	}

	@Test
	public void checkAnnotatedClass_WhenGuestIsAllowedPastRopeAndRopeIsNotBypassed_Proceeds() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(velvetRope.bypass()).thenReturn(false);
		aspect.checkAnnotatedClass(proceedingJoinPoint, velvetRope);
		verify(proceedingJoinPoint).proceed();
	}

	@Test
	public void checkAnnotatedClass_WhenGuestIsNotAllowedPastRopeAndRopeIsBypassed_Proceeds() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(false);
		when(velvetRope.bypass()).thenReturn(true);
		aspect.checkAnnotatedClass(proceedingJoinPoint, velvetRope);
		verify(proceedingJoinPoint).proceed();
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void checkAnnotatedClass_WhenGuestIsNotAllowedPastRopeAndRopeIsNotBypassed_Throws() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(false);
		when(velvetRope.bypass()).thenReturn(false);
		aspect.checkAnnotatedClass(proceedingJoinPoint, velvetRope);
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void checkAnnotatedClass_WhenGuestIsAllowedPastRopeAndRopeIsBypassed_Throws() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(velvetRope.bypass()).thenReturn(true);
		aspect.checkAnnotatedClass(proceedingJoinPoint, velvetRope);
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void checkAnnotatedClass_WhenGuestIsNotAllowedPastRopeAndRopeIsNotBypassed_DoesNotProceed() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(false);
		when(velvetRope.bypass()).thenReturn(false);
		aspect.checkAnnotatedClass(proceedingJoinPoint, velvetRope);
		verify(proceedingJoinPoint, never()).proceed();
	}

	@Test(expected = UnauthorizedVenueException.class)
	public void checkAnnotatedClass_WhenGuestIsAllowedPastRopeAndRopeIsBypassed_DoesNotProceed() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(velvetRope.bypass()).thenReturn(true);
		aspect.checkAnnotatedClass(proceedingJoinPoint, velvetRope);
		verify(proceedingJoinPoint, never()).proceed();
	}

	@Test
	public void callingMethodInARopedClass_AdvisesGuestMethod() throws Throwable {
		Rope rope = new LobbyRope();
		lobbyRopeDoorman.welcome(guest, rope);

		verify(aspect).checkAnnotatedGuestMethod(any(ProceedingJoinPoint.class), any(VelvetRope.class), eq(guest));
	}

	@Test
	public void checkAnnotatedGuestMethod_WithProceedingJoinPointAndVelvetRopeAndGuest_GetsTheUnauthenticatedGuest() throws Throwable {
		aspect.checkAnnotatedGuestMethod(proceedingJoinPoint, velvetRope, guest);
		verify(unauthenticatedGuestService).getGuest(guest);
	}

	@Test
	public void checkAnnotatedGuestMethod_WithProceedingJoinPointAndVelvetRopeAndGuest_AdvisesAllowed() throws Throwable {
		aspect.checkAnnotatedGuestMethod(proceedingJoinPoint, velvetRope, guest);
		verify(guest).canEnter(Venue.LOBBY);
	}

	@Test
	public void checkAnnotatedGuestMethod_WhenGuestIsAllowedPastRopeAndRopeIsNotBypassed_Proceeds() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(velvetRope.bypass()).thenReturn(false);
		aspect.checkAnnotatedGuestMethod(proceedingJoinPoint, velvetRope, guest);
		verify(proceedingJoinPoint).proceed();
	}

	@Test
	public void checkAnnotatedGuestMethod_WhenGuestIsNotAllowedPastRopeAndRopeIsBypassed_Proceeds() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(false);
		when(velvetRope.bypass()).thenReturn(true);
		aspect.checkAnnotatedGuestMethod(proceedingJoinPoint, velvetRope, guest);
		verify(proceedingJoinPoint).proceed();
	}

	@Test
	public void checkAnnotatedGuestMethod_WhenGuestIsNotAllowedPastRopeAndRopeIsNotBypassed_DoesNotProceed() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(false);
		when(velvetRope.bypass()).thenReturn(false);
		aspect.checkAnnotatedGuestMethod(proceedingJoinPoint, velvetRope, guest);
		verify(proceedingJoinPoint, never()).proceed();
	}

	@Test
	public void checkAnnotatedGuestMethod_WhenGuestIsAllowedPastRopeAndRopeIsBypassed_DoesNotProceed() throws Throwable {
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(velvetRope.bypass()).thenReturn(true);
		aspect.checkAnnotatedGuestMethod(proceedingJoinPoint, velvetRope, guest);
		verify(proceedingJoinPoint, never()).proceed();
	}

	private interface MockTargetInterface {
		public void doThatThing();
		public void doThatOtherThing();
		public void dontDoThatThing();
	}

	@Controller
	private class MockMethodTargetController implements MockTargetInterface {
		@VelvetRope(venue = Venue.LOBBY)
		@RequestMapping
		@Override
		public void doThatThing() {}

		@Override
		public void doThatOtherThing() {}

		@Override
		@VelvetRope(venue = Venue.LOBBY, bypass = true)
		@RequestMapping
		public void dontDoThatThing() {}
	}

	@Controller
	@VelvetRope(venue = Venue.LOBBY)
	private class MockClassTargetController implements MockTargetInterface {
		@Override
		@RequestMapping
		public void doThatThing() {}

		@Override
		public void doThatOtherThing() {}

		@Override
		public void dontDoThatThing() {}
	}

	@Controller
	@VelvetRope(venue = Venue.LOBBY)
	private class MockMethodClassTargetController implements MockTargetInterface {
		@Override
		@VelvetRope(venue = Venue.LOBBY)
		@RequestMapping
		public void doThatThing() {}

		@Override
		public void doThatOtherThing() {}

		@Override
		public void dontDoThatThing() {}
	}

	private class LobbyRope implements Rope {
		@Override
		public void enter() {}
	}

	@VelvetRope(venue = Venue.LOBBY)
	private class LobbyRopeDoorman implements Doorman<LobbyRope> {
		@Override
		public void welcome(Guest guest, LobbyRope rope) {}
	}
}
