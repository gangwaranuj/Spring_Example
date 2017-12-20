package com.workmarket.web.controllers.error;

import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.velvetrope.UnauthorizedVenueException;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.tiles.TilesViewAdapter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nick on 3/11/14 1:13 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ErrorControllerAdviceTest {

	@Mock private NotificationService notificationService;
	@Mock private MessageBundleHelper messageHelper;
	@Mock private TilesViewAdapter tilesViewAdapter;
	@InjectMocks private ErrorControllerAdvice errorControllerAdvice;
	private MessageBundle messages;

	@Before
	public void setUp() throws Exception {
		messages = new MessageBundle();
		when(messageHelper.newBundle()).thenReturn(messages);
	}

	@Test
	public void exception_WebException_HasError() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());

		errorControllerAdvice.exception(request, new HttpException401());

		verify(messageHelper, times(1)).addError(any(MessageBundle.class), anyString());
	}

	@Test
	public void exception_WebException_RedirectsToUrl() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
		final String redirect = "test";

		ModelAndView mav = errorControllerAdvice.exception(request, new HttpException401().setRedirectUri(redirect));

		Assert.assertEquals(redirect, mav.getViewName());
	}

	@Test
	public void handleVelvetRopeException_CreatesANewMessageBundle() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		FlashMap flashMap = new FlashMap();
		request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, flashMap);
		UnauthorizedVenueException exception = mock(UnauthorizedVenueException.class);

		errorControllerAdvice.handleUnauthorizedVenueException(exception, request);

		verify(messageHelper).newBundle();
	}

	@Test
	public void handleVelvetRopeException_AddsTheVelvetRopeExceptionMessage() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		FlashMap flashMap = new FlashMap();
		request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, flashMap);
		UnauthorizedVenueException exception = mock(UnauthorizedVenueException.class);

		errorControllerAdvice.handleUnauthorizedVenueException(exception, request);

		verify(messageHelper).addNotice(messages, exception.getMessage());
	}

	@Test
	public void handleVelvetRopeException_AddsMessagesToTheFlash() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		FlashMap flashMap = new FlashMap();
		request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, flashMap);
		UnauthorizedVenueException exception = mock(UnauthorizedVenueException.class);

		errorControllerAdvice.handleUnauthorizedVenueException(exception, request);

		assertThat((MessageBundle)flashMap.get("bundle"), is(messages));
	}

	@Test
	public void handleVelvetRopeException_RedirectsToTheExceptionsRedirect() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		FlashMap flashMap = new FlashMap();
		request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, flashMap);
		UnauthorizedVenueException exception = mock(UnauthorizedVenueException.class);
		when(exception.getRedirectPath()).thenReturn("/whatevs");

		String redirect = errorControllerAdvice.handleUnauthorizedVenueException(exception, request);

		assertThat(redirect, is("redirect:" + exception.getRedirectPath()));
	}
}
