package com.workmarket.web.controllers.error;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import com.workmarket.service.web.cachebusting.CacheBusterService;
import com.workmarket.utility.WebUtilities;
import com.workmarket.velvetrope.UnauthorizedVenueException;
import com.workmarket.web.exceptions.AuthenticatedException;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.exceptions.MobileHttpException400;
import com.workmarket.web.exceptions.MobileHttpException401;
import com.workmarket.web.exceptions.MobileHttpException404;
import com.workmarket.web.exceptions.WebException;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.tiles.TilesViewAdapter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ErrorControllerAdvice {

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private TilesViewAdapter tilesViewAdapter;
	@Autowired private CacheBusterService cacheBusterService;

	@Value("${error.recipient}")
	private String errorRecipient;
	@Value("${error.fromUserId}")
	private Long fromUserId;

	private String mediaPrefix = null;

	@PostConstruct
	private void init() {
		mediaPrefix = StringUtils.defaultString(cacheBusterService.getMediaPrefix());
	}

	private static final Log logger = LogFactory.getLog(ErrorControllerAdvice.class);

	private static final Map<Class<? extends Exception>, String> EXCEPTION_VIEW_MAP = new ImmutableMap.Builder<Class<? extends Exception>, String>()
		.put(RememberMeAuthenticationException.class, "login")
		.put(HttpException401.class, "web/pages/error/no_access")
		.put(HttpException403.class, "web/pages/error/no_access")
		.put(AccessDeniedException.class, "web/pages/error/no_access")
		.put(HttpRequestMethodNotSupportedException.class, "web/pages/error/404")
		.put(MobileHttpException400.class, "mobile/pages/error/400")
		.put(MobileHttpException401.class, "mobile/pages/error/401")
		.put(MobileHttpException404.class, "mobile/pages/error/404")
		.build();

	@ExceptionHandler(Exception.class)
	public ModelAndView exception(HttpServletRequest request, Exception exception) {

		Exception ex = exception instanceof AuthenticatedException ? ((AuthenticatedException) exception).getE() : exception;
		MessageBundle messages = messageHelper.newBundle();

		if (ex != null && ArrayUtils.isNotEmpty(ex.getStackTrace())) {
			StackTraceElement[] trace = ex.getStackTrace();
			StackTraceElement traceElement = trace[trace.length - 1];
			boolean isWebException = ex instanceof WebException;

			if (!isWebException) {
				LogFactory.getLog(traceElement.getClass()).error(traceElement, ex);
			} else {
				WebException e = (WebException) ex;

				if (e.shouldPrintStackTrace()) {
					LogFactory.getLog(traceElement.getClass()).error(traceElement, ex);
				} else {
					logger.error(StringUtils.defaultIfBlank(messageHelper.getMessage(e.getMessageKey()), e.getMessage()));
				}

				messageHelper.addError(messages, StringUtils.defaultIfBlank(e.getMessageKey(), e.getMessage()));

				if (e.hasRedirectUri()) {
					FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
					flashMap.put("bundle", messages);
					return new ModelAndView(e.getRedirectUri());
				}
			}
		}
		ModelAndView mav = new ModelAndView(getViewForException(ex))
			.addObject("successful", false)
			.addObject("errors", messages.getErrors())
			.addObject("exception", ex)
			.addObject("mediaPrefix", mediaPrefix);

		tilesViewAdapter.mapTilesView(mav, request); // interceptors are not called from the HandlerExceptionResolver flow
		return mav;
	}

	// log these as INFO - they are noisy
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public @ResponseBody String handleMethodNotSupportedException(
		HttpServletRequest request,
		HttpServletResponse response,
		Exception ex) throws IOException {

		logger.info(String.format("Method %s not supported for URL: %s", request.getMethod(), request.getRequestURI()));

		if (WebUtilities.isPageRequest(request)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		response.setHeader("Content-Type", "application/json");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return ex.getMessage();
	}

	@ExceptionHandler(UnauthorizedVenueException.class)
	public String handleUnauthorizedVenueException(UnauthorizedVenueException e, HttpServletRequest httpRequest) {
		MessageBundle messages = messageHelper.newBundle();
		messageHelper.addNotice(messages, e.getMessage());
		FlashMap flashMap = RequestContextUtils.getOutputFlashMap(httpRequest);
		flashMap.put("bundle", messages);

		return "redirect:" + e.getRedirectPath();
	}

	private String getViewForException(Exception ex) {
		return MoreObjects.firstNonNull(EXCEPTION_VIEW_MAP.get(ex.getClass()), "web/pages/error/500");
	}

}
