package com.workmarket.api.model.resolver;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.api.v2.worker.model.WorkersSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class WorkersSearchRequestArgumentResolver implements HandlerMethodArgumentResolver {
	private static final Logger logger = LoggerFactory.getLogger(WorkersSearchRequestArgumentResolver.class);

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
				(parameterType.isAssignableFrom(WorkersSearchRequest.class));

	}

	/**
	 *
	 * @param request
	 * @return
	 */
	protected WorkersSearchRequest evaluateArgument(HttpServletRequest request) {
		WorkersSearchRequest.Builder builder = new WorkersSearchRequest.Builder();

		String address = request.getParameter("address");
		if(address != null) {
			builder.address(address);
		}

		String[] countries = request.getParameterValues("countries");
		if(countries != null) {
			builder.countries(ImmutableSet.copyOf(countries));
		}

		String[] industries = request.getParameterValues("industries");
		if(industries != null) {
			List<String> industriesList = ImmutableList.copyOf(industries);
			builder.industries(ImmutableSet.copyOf(Lists.transform(industriesList, new Function<String, Long>() {
				@Nullable
				@Override
				public Long apply(@Nullable String input) {
					return Long.parseLong(input);
				}
			})));
		}

		String keyword = request.getParameter("keyword");
		if(keyword != null) {
			builder.keyword(keyword);
		}

		String order = request.getParameter("order");
		if(order != null) {
			builder.order(order);
		}

		String page = request.getParameter("page");
		if(page != null) {
			builder.page(Integer.valueOf(page));
		}

		String pageSize = request.getParameter("pageSize");
		if(pageSize != null) {
			builder.pageSize(Integer.valueOf(pageSize));
		}

		String radius = request.getParameter("radius");
		if(radius != null) {
			builder.radius(Integer.valueOf(radius));
		}

		String sortBy = request.getParameter("sortBy");
		if(sortBy != null) {
			builder.sortby(sortBy);
		}

		return builder.build();
	}
}
