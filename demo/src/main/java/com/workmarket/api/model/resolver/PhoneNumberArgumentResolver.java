package com.workmarket.api.model.resolver;

import com.workmarket.service.business.dto.PhoneNumberDTO;
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

public class PhoneNumberArgumentResolver implements HandlerMethodArgumentResolver {
	private static final Logger logger = LoggerFactory.getLogger(PhoneNumberArgumentResolver.class);

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
				(parameterType.isArray() && parameterType.isAssignableFrom(PhoneNumberDTO[].class));

	}

	/**
	 * Phone numbers are sent using the following format:
	 * phones[<N>]=212-333-1188
	 * phones[<N>][ext]=123
	 * phones[<N>][type]={work, home, other}
	 * @param request Http Servlet Request
	 * @return an array of PhoneNumberDTOs
	 */
	protected PhoneNumberDTO[] evaluateArgument(HttpServletRequest request) {
		Map<Integer,PhoneNumberDTO> map = new TreeMap<Integer,PhoneNumberDTO>();
		Pattern pattern = Pattern.compile("phones\\[([0-9]+)\\](?:\\[(ext|type)\\])?");

		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String paramName = (String)e.nextElement();
			Matcher matcher = pattern.matcher(paramName);

			if (matcher.matches()) {
				Integer index = Integer.valueOf(matcher.group(1));
				PhoneNumberDTO phoneNumber = map.get(index);

				if (phoneNumber == null) {
					phoneNumber = new PhoneNumberDTO();
					map.put(index, phoneNumber);
				}

				if ("ext".equals(matcher.group(2))) {
					phoneNumber.setExtension(request.getParameter(paramName));
				}
				else if ("type".equals(matcher.group(2))) {
					try {
						phoneNumber.setContactContextType(request.getParameter(paramName));
					} catch (Exception ex) {
						logger.warn("error converting requested param {} = {} to ContactContextType",
								new Object[] {paramName, request.getParameter(paramName)}, ex);
					}
				}
				else {
					phoneNumber.setPhone(request.getParameter(paramName));
				}
			}
		}

		List<PhoneNumberDTO> list = new LinkedList<PhoneNumberDTO>();

		for (PhoneNumberDTO phoneNumber : map.values()) {
			if (StringUtils.hasText(phoneNumber.getPhone())) {
				list.add(phoneNumber);
			}
		}

		return list.toArray(new PhoneNumberDTO[list.size()]);
	}
}
