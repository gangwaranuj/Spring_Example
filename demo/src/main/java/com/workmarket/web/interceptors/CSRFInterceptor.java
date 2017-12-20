package com.workmarket.web.interceptors;

import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.service.web.CSRFTokenService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

import static com.workmarket.utility.InetAddressUtilities.getAddressFromRequest;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Created by nick on 7/12/13 11:47 AM
 */
public class CSRFInterceptor implements HandlerInterceptor {

	private static final Log logger = LogFactory.getLog(CSRFInterceptor.class);

	public CSRFInterceptor() {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@Autowired private CSRFTokenService csrfTokenService;
	@Autowired private FeatureEvaluator featureEvaluator;

	// TODO: after Spring 3.2 upgrade, replace this with a <exclude-mapping> in the interceptor definition
	private static final Pattern URLS_TO_EXCLUDE = Pattern.compile(
		"(/api/.*)" +
			"|(/v2/.*)" +
			"|(/v3/.*)" +
			"|(/worker/v2/.*)" +
			"|(/employer/v2/.*)" +
			"|(/mobile/register_device)");
	public static final Pattern USER_AGENT_STRING_BLACKLIST_PATTERN = Pattern.compile(".*whitehat.*",
		Pattern.CASE_INSENSITIVE);

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		boolean isEligibleMethod = CSRFTokenService.METHODS_TO_CHECK.contains(
			StringUtils.defaultIfBlank(request.getMethod(), "").toUpperCase());
		boolean isEligibleUrl = !URLS_TO_EXCLUDE.matcher(trim(request.getServletPath())).matches();

		if (isEligibleMethod && isEligibleUrl && !csrfTokenService.acceptsTokenIn(request)) {
			response.addHeader("X-WM-InvalidCSRFToken", Boolean.toString(true));
			response.sendError(HttpServletResponse.SC_FORBIDDEN);

			String userAgent = request.getHeader("user-agent");
			if (!USER_AGENT_STRING_BLACKLIST_PATTERN.matcher(userAgent).matches()) {
				logger.warn(String.format("Cross-Site Request Forgery (CSRF) attempt! [URL=%s, IP=%s, User-Agent=%s]",
					request.getServletPath(), getAddressFromRequest(request), userAgent));
			}
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
												 HttpServletResponse response,
												 Object handler,
												 ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
															HttpServletResponse response,
															Object handler,
															Exception ex) throws Exception {
	}
}
