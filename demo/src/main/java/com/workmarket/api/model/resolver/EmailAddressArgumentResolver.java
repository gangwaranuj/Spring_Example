package com.workmarket.api.model.resolver;

import com.workmarket.service.business.dto.EmailAddressDTO;
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

public class EmailAddressArgumentResolver implements HandlerMethodArgumentResolver {
	private static final Logger logger = LoggerFactory.getLogger(EmailAddressArgumentResolver.class);

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		Class<?> parameterType = methodParameter.getParameterType();
		return methodParameter.hasParameterAnnotation(ApiArgumentResolver.class)
				&& (parameterType.isArray() && parameterType.isAssignableFrom(EmailAddressDTO[].class));
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		if (supportsParameter(methodParameter)) {
			return evaluateArgument(nativeWebRequest.getNativeRequest(HttpServletRequest.class));
		}
		return null;
	}

	/**
	 * Email addresses are sent using the following format:
	 * emails[<N>]=info@workmarket.com
	 * emails[<N>][type]={work, home, other}
	 * @param request Http Servlet Request
	 * @return an array of WebsiteDTOs
	 */
	protected EmailAddressDTO[] evaluateArgument(HttpServletRequest request) {
		Map<Integer,EmailAddressDTO> map = new TreeMap<Integer,EmailAddressDTO>();
		Pattern pattern = Pattern.compile("emails\\[([0-9]+)\\](?:\\[(type)\\])?");

		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String paramName = (String)e.nextElement();
			Matcher matcher = pattern.matcher(paramName);

			if (matcher.matches()) {
				Integer index = Integer.valueOf(matcher.group(1));
				EmailAddressDTO emailAddressDTO = map.get(index);

				if (emailAddressDTO == null) {
					emailAddressDTO = new EmailAddressDTO();
					map.put(index, emailAddressDTO);
				}

				if ("type".equals(matcher.group(2))) { // can also test for null
					try {
						emailAddressDTO.setContactContextType(request.getParameter(paramName));
					} catch (Exception ex) {
						logger.warn("error converting requested param {} = {} to ContactContextType",
								new Object[] {paramName, request.getParameter(paramName)}, ex);
					}
				}
				else {
					emailAddressDTO.setEmail(request.getParameter(paramName));
				}
			}
		}

		List<EmailAddressDTO> list = new LinkedList<EmailAddressDTO>();

		for (EmailAddressDTO emailAddress : map.values()) {
			if (StringUtils.hasText(emailAddress.getEmail())) {
				list.add(emailAddress);
			}
		}

		return list.toArray(new EmailAddressDTO[list.size()]);
	}
}
