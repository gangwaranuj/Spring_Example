package com.workmarket.api.model.resolver;

import com.workmarket.search.request.user.PeopleSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendToGroupsArgumentResolver implements HandlerMethodArgumentResolver {
	private static final Logger logger = LoggerFactory.getLogger(SendToGroupsArgumentResolver.class);

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		if (supportsParameter(methodParameter)) {
			HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
			return evaluateArgument(request);
		}

		return null;
	}

	public boolean supportsParameter(MethodParameter methodParameter) {
		return (methodParameter.hasParameterAnnotation(ApiArgumentResolver.class)
				&& (PeopleSearchRequest.class == methodParameter.getParameterType()));
	}

	/**
	 * PeopleSearchRequest is sent in the following format: send_to_groups[<N>]
	 * @param request Http Servlet Request
	 * @return PeopleSearchRequest
	 */
	protected PeopleSearchRequest evaluateArgument(HttpServletRequest request) {
		Map<Integer,Long> map = new TreeMap<Integer,Long>();
		Pattern pattern = Pattern.compile("send_to_groups\\[([0-9]+)\\]");

		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String paramName = (String)e.nextElement();
			Matcher matcher = pattern.matcher(paramName);

			if (matcher.matches()) {
				Integer index = Integer.valueOf(matcher.group(1));

				try {
					map.put(index, Long.valueOf(request.getParameter(paramName)));
				}
				catch (Exception ex) {
					logger.warn("error converting requested param {} = {} to long",
							new Object[] {paramName, request.getParameter(paramName)}, ex);
				}
			}
		}

		if (map.isEmpty()) {
			return null;
		}
		else {
			PeopleSearchRequest routingFilter = new PeopleSearchRequest();
			routingFilter.setGroupFilter(new HashSet<Long>(map.values()));
			return routingFilter;
		}
	}
}