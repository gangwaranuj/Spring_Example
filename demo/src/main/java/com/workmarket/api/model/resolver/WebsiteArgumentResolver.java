package com.workmarket.api.model.resolver;

import com.workmarket.service.business.dto.WebsiteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteArgumentResolver implements HandlerMethodArgumentResolver {
	private static final Logger logger = LoggerFactory.getLogger(WebsiteArgumentResolver.class);

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		if (supportsParameter(methodParameter)) {
			HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
			return evaluateArgument(request);
		}

		return null;
	}

	public boolean supportsParameter(MethodParameter methodParameter) {
		Class<?> parameterType = methodParameter.getParameterType();
		return methodParameter.hasParameterAnnotation(ApiArgumentResolver.class) &&
				(parameterType.isArray() && parameterType.isAssignableFrom(WebsiteDTO[].class));

	}

	/**
	 * Websites are sent using the following format:
	 * websites[<N>]=www.workmarket.com
	 * websites[<N>][type]={work, home, other}
	 * @param request Http Servlet Request
	 * @return an array of WebsiteDTOs
	 */
	protected WebsiteDTO[] evaluateArgument(HttpServletRequest request) {
		Map<Integer,WebsiteDTO> map = new TreeMap<Integer,WebsiteDTO>();
		Pattern pattern = Pattern.compile("websites\\[([0-9]+)\\](?:\\[(type)\\])?");

		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String paramName = (String)e.nextElement();
			Matcher matcher = pattern.matcher(paramName);

			if (matcher.matches()) {
				Integer index = Integer.valueOf(matcher.group(1));
				WebsiteDTO website = map.get(index);

				if (website == null) {
					website = new WebsiteDTO();
					map.put(index, website);
				}

				if ("type".equals(matcher.group(2))) {
					try {
						website.setContactContextType(request.getParameter(paramName));
					} catch (Exception ex) {
						logger.warn("error converting requested param {} = {} to ContactContextType",
								new Object[] {paramName, request.getParameter(paramName)}, ex);
					}
				}
				else {
					website.setWebsite(request.getParameter(paramName));
				}
			}
		}

		List<WebsiteDTO> list = new LinkedList<WebsiteDTO>();

		for (WebsiteDTO website : map.values()) {
			if (StringUtils.hasText(website.getWebsite())) {
				list.add(website);
			}
		}

		return list.toArray(new WebsiteDTO[list.size()]);
	}
}
