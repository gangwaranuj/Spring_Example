package com.workmarket.domains.authentication.filters;

import com.workmarket.utility.RandomUtilities;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * to catch and prevent session hijacking across the app
 */
public class SessionHijackingFilter extends GenericFilterBean {

	private static final Log logger = LogFactory.getLog(SessionHijackingFilter.class);
	protected static final String SESSION_ATTR = "workmarketSessionId";

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession(false);

		// additional check so requests with no security contexts are skipped
		if(SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
			chain.doFilter(req, res);
			return;
		}

		String sessionValue = (session == null) ? null : (String) session.getAttribute(SESSION_ATTR);

		// the following is a temporary fix for our Android app. In some instances we are getting multiple
		// cookies with the name workmarketSessionId, one being valid another not. So while this is not
		// ideal it is a quick and dirty fix that should get the app back working. We need to fix the cookie
		// issue on Android (probably has something to do with the CookieManager) so this is temporary
		final Set<String> cookieValues = getCookieValues(request);
		if (CollectionUtils.isNotEmpty(cookieValues) && null != sessionValue) {
			if (!cookieValues.contains(sessionValue)) {
				logger.info("session values not found in our cookies: " + sessionValue + ", " + StringUtils.join(cookieValues, ","));
				clearSessionAndUser(request, response, sessionValue, StringUtils.join(cookieValues, ","));
				// This will redirect to this page again, which presumably will get redirected to login, which then will take
				// us back.
				response.sendRedirect("/");
				return;
			}
			logger.info("session values match: " + sessionValue + ", " + StringUtils.join(cookieValues, ","));
		}
/*
		String cookieValue = getCookieValue(request);
		if (StringUtils.isNotBlank(cookieValue) && null != sessionValue) {
			if (!sessionValue.equals(cookieValue)) {
				logger.info("session values don't match: " + sessionValue + ", " + cookieValue);
				clearSessionAndUser(request, response, sessionValue, cookieValue);
				return;
			}
			logger.info("session values match: " + sessionValue + ", " + cookieValue);
		}
*/
		else if (request.getCookies() != null) {

			String cookieString = RandomUtilities.generateAlphaNumericString(16) + "-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());

			session = request.getSession();
			session.setAttribute(SESSION_ATTR, cookieString);
			setCookieValue(response, cookieString);

			logger.info("setting value for session and cookie: " + ((String) session.getAttribute(SESSION_ATTR)) + ", " + cookieString);

		}

		chain.doFilter(req, res);
	}

	public String getCookieValue(HttpServletRequest request) {
		String cookieValue = null;
		Cookie cookies[] = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (StringUtils.equals(cookie.getName(), SESSION_ATTR)) {
					cookieValue = cookie.getValue();
					logger.info("found cookie: " + cookieValue + " '" + request.getRequestURI() + "'");
					break;
				}
			}
		}
		return cookieValue;
	}

	/**
	 * Gets all of the cookies under our workmarketSessionId. While there should only be one
	 * on Android we are getting 2 in some instances so this is a work-around to fix the
	 * white screen of death on Android. We need to figure out why we are getting the multiple
	 * entries on Android but that is a bigger problem
	 *
	 * @param request
	 * @return
	 */
	public Set<String> getCookieValues(HttpServletRequest request) {
		Assert.notNull(request);

		final Set<String> cookieValues =  new HashSet<>();
		String cookieValue = null;
		Cookie cookies[] = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(SESSION_ATTR)) {
					cookieValue = cookie.getValue();
					logger.info("found cookie: " + cookieValue + " '" + request.getRequestURI() + "'");
					cookieValues.add(cookieValue);
				}
			}
		}
		return cookieValues;
	}

	public void setCookieValue(HttpServletResponse response, String cookieValue) {
		Cookie cookie = new Cookie(SESSION_ATTR, cookieValue);
		logger.info("setting cookie: " + cookieValue);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	protected void clearSessionAndUser(HttpServletRequest request, HttpServletResponse response, String sessionValue, String cookieValue) throws IOException {
		// nuke the session protection cookie too so the user can recover
		for (final Cookie c : request.getCookies()) {
			if (c.getName().equals(SESSION_ATTR)) {
				c.setMaxAge(0);
				response.addCookie(c);
			}
		}
		SecurityContextHolder.clearContext();
		// request.getSession().invalidate();
		logger.warn("Terminating session, workmarketSessionId did not match on: '" + request.getRequestURI() + "', [" + sessionValue + "," + cookieValue + "]");
	}

}