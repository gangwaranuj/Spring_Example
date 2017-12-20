package com.workmarket.api.model.resolver;

import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.Profile;
import com.workmarket.thrift.core.User;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class LocationContactsArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		if (supportsParameter(methodParameter)) {
			ApiArgumentResolver annotation = methodParameter.getParameterAnnotation(ApiArgumentResolver.class);

			if (StringUtils.hasText(annotation.value())) {
				HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
				int index = Integer.parseInt(annotation.value());
				return evaluateArgument(index, request);
			}

		}

		return null;
	}

	public boolean supportsParameter(MethodParameter methodParameter) {
		return (methodParameter.hasParameterAnnotation(ApiArgumentResolver.class)
				&& (User.class == methodParameter.getParameterType()));
	}

	protected User evaluateArgument(int index, HttpServletRequest request) {
		User contact = null;

		String locationParam = String.format("location_contacts[%s]", index);
		String id = request.getParameter(String.format("%s[id]", locationParam));

		if (id != null) {
			contact = new User();
			contact.setId(Long.parseLong(id));
		}
		else {
			String firstName = request.getParameter(String.format("%s[first_name]", locationParam));

			if (firstName != null) {
				contact = new User();
				contact.setName( new Name(firstName, request.getParameter(String.format("%s[last_name]", locationParam))) );
				contact.setEmail( request.getParameter(String.format("%s[email]", locationParam)) );

				Phone phone = new Phone();
				phone.setExtension(request.getParameter(String.format("%s[phone_extension]", locationParam)));

				String phoneNumber = request.getParameter( String.format("%s[phone]", locationParam) );

				if (phoneNumber != null) {
					phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
					phone.setPhone(phoneNumber);
				}

				Profile profile = new Profile();
				profile.setPhoneNumbers(Arrays.asList(phone));
				contact.setProfile(profile);
			}
		}

		return contact;
	}
}
